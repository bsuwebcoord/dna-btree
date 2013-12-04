import java.io.*;

public class BTree {
        
        BTreeNode root = null;
        int numTreeNodes = 0;
        int t;
        int sequenceLength = 0;
        long byteOffsetRoot = 0;
        RandomAccessFile dis;
        int[] childrenInitializer = null;
        TreeObject[] treeObjectInitializer = null;
        File file = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        String fullBinaryString = "";
        
        public BTree(int degree) throws IOException{
                
                        t = degree;
                        
                      //open the dump file
                        try {
                     	    
                   			file = new File("dump.txt");
                    
                   			// if file doesnt exists, then create it
                   			if (!file.exists()) {
                   				file.createNewFile();
                   			}
                    
                   			fw = new FileWriter(file.getAbsoluteFile());
                   			bw = new BufferedWriter(fw);
                    
                 	   } catch (IOException e) {
                 		   e.printStackTrace();
                 	   }
                
        }
        
        //similar to B-Tree-Search code in book p. 492
        //returns the global offset of the node if the key is found, otherwise returns -1 if not found
        public long bTreeSearch(BTreeNode x, long k) throws IOException{
                int i = 0;
                
                while(i < x.numTreeObjects && k > x.treeO[i].key){
                        i++;
                }
                
                if(i < x.numTreeObjects && k == x.treeO[i].key){
                        return x.globalOffset;
                }
                else if(x.leaf){
                        return -1;
                }
                else{
                        BTreeNode temp = diskRead(x.childPointers[i]);
                        return bTreeSearch(temp, k);
                }
        }
        
        public void bTreeSplitChild(BTreeNode x, int i) throws IOException{
                BTreeNode y = diskRead(x.childPointers[i]);
                BTreeNode z = new BTreeNode((int)dis.length()-1, y.leaf, t-1, y.parentPointer, childrenInitializer, treeObjectInitializer);
                for(int j = 0; j <= t-2; j++){
                        z.treeO[j].key = y.treeO[j+t].key;
                }
                if(!y.leaf){
                        for(int j = 0; j <= t-1; j++){
                                z.childPointers[j] = y.childPointers[j+t];
                        }
                }
                y.numTreeObjects = t - 1;
                for(int j = x.numTreeObjects; j >= i+1; j--){
                        x.childPointers[j+1] = x.childPointers[j];
                }
                x.childPointers[i+1] = z.globalOffset;
                for(int j = x.numTreeObjects-1; j >= i; j--){
                        x.treeO[j+1].key = x.treeO[j].key;
                }
                x.treeO[i].key = y.treeO[t].key;
                x.numTreeObjects = x.numTreeObjects + 1;
                //System.out.printf("\nThe number of tree objects is: %d\n", x.numTreeObjects);
                //write y in same position
                diskWrite(y.globalOffset, y);
                //write z at end of file
                diskWrite(-1, z);
                //write x in same position
                diskWrite(x.globalOffset, x);
                
        }
        
        public void bTreeInsert(long k) throws IOException{
                BTreeNode r = root;
                //System.out.printf("\nThe number of tree objects of root is: %d\n", root.numTreeObjects);
                if(root.numTreeObjects == 2*t-1){
                        BTreeNode s = new BTreeNode(-1, true, 0, 0, childrenInitializer, treeObjectInitializer);
                        root = s;
                        s.leaf = false;
                        s.numTreeObjects = 0;
                        s.childPointers[0] = r.globalOffset;
                        bTreeSplitChild(s, 0);
                        bTreeInsertNonfull(s,k);
                }
                else{
                        bTreeInsertNonfull(r,k);
                }
        }
        
        public void bTreeInsertNonfull(BTreeNode x, long k) throws IOException{
                        //added the subtraction of i by 1 to avoid indexOutOfBounds exception
                        //System.out.printf("\nThe number of tree objects of x is: %d\n", x.numTreeObjects);
                int i = x.numTreeObjects - 1;
                if(x.leaf){
                        while(i >= 0 && k < x.treeO[i].key){
                                x.treeO[i+1].key = x.treeO[i].key;
                                i--;
                        }
                        x.treeO[i+1].key = k;
                        x.numTreeObjects = x.numTreeObjects + 1;
                        //System.out.printf("\nThe number of tree objects in the bTreeInsertNonfull is: %d\n", x.numTreeObjects);
                        diskWrite(x.globalOffset, x);
                }
                else{
                                //System.out.printf("\ni's value is: %d\n", i);
                        while(i >= 0 && k < x.treeO[i].key){
                                i--;
                        }
                        i++;
                        //System.out.printf("\ni's value is: %d\n", i);
                        //System.out.printf("\nThe child pointer is: %d\n", x.childPointers[i]);
                        //System.out.printf("\nThe size of the bin file is: %d\n", dis.length());
                        BTreeNode child = diskRead(x.childPointers[i]);
                        if(child.numTreeObjects == 2*t-1){
                                bTreeSplitChild(x, i);
                                //read in the updated split child
                                child = diskRead(x.childPointers[i]);
                                if(k > x.treeO[i].key){
                                        i++;
                                }
                        }
                        bTreeInsertNonfull(child , k);
                }
        }
        
        public void diskWrite(long offset, BTreeNode node) throws IOException{
                
                // Each BTreeNode will be stored in the binary file with the following sequence:
                // "8 bytes globalOffset value"
                // "1 byte leaf true/false" 
                // "4 bytes number of tree objects" (this can reach the maximum int value of 2,147,483,647)
                // "4 bytes parent pointer" (this can reach the maximum int value of 2,147,483,647)
                // "2t * 4 bytes child pointers"
                // "2t-1 * 12 bytes tree object" (31 bits for the frequency, 64 bits for the key value)
                                
                // The optimal degree can be calculated with the following equation:
                // (2t-1)(12) + (2t+1)(4) + 13 <= 4096
                // t <= 127
                
                //write to specified offset or end of file, end of file can be reached with a negative offset argument
                        //? not sure if it should be dis.length()-1 , I would think it would just be dis.length(), but if I do dis.length() I get errors
                if(offset < 0){
                                //System.out.printf("\n-----------------------------------------------------------The dis.length() is: %d\n", dis.length());
                        dis.seek(dis.length()-1);
                }
                else{
                        dis.seek(offset);
                }
                
                //write the current position of the binary file, write first to get the correct starting position for the node
                //System.out.printf("\n1The dis.getFilePointer() is: %d\n", dis.getFilePointer());
                //System.out.printf("\n2The dis.getFilePointer() is: %d\n", dis.getFilePointer());
                dis.writeLong(dis.getFilePointer());
                dis.writeBoolean(node.leaf);
                dis.writeInt(node.numTreeObjects);
                dis.writeInt(node.parentPointer);
                
                for(int i = 0; i < node.childPointers.length; i++){
                        
                        dis.writeInt(node.childPointers[i]);
                        
                }
                
                for(int i = 0; i < node.treeO.length; i++){
                    
                        dis.writeInt(node.treeO[i].frequency);
                        dis.writeLong(node.treeO[i].key);
                                    
                }
                
                //write buffer byte if writing to the end of the bin file
                if(offset < 0){
                	dis.writeBoolean(false);
                }
                
                
        }
        
        //using RandomAccessFile
        public BTreeNode diskRead(long offset) throws IOException{
                
                // Each BTreeNode will be stored in the binary file with the following sequence:
                // "8 bytes globalOffset value"
                // "1 byte leaf true/false" 
                // "4 bytes number of tree objects" (this can reach the maximum int value of 2,147,483,647)
                // "4 bytes parent pointer" (this can reach the maximum int value of 2,147,483,647)
                // "2t * 4 bytes child pointers"
                // "2t-1 * 12 bytes tree object" (31 bits for the frequency, 64 bits for the key value)
                                                
                // The optimal degree can be calculated with the following equation:
                // (2t-1)(12) + (2t+1)(4) + 13 <= 4096
                // t <= 127
                
                //System.out.printf("\nThe input offset value is: %d\n", offset);
                
                long globalOffset;
                boolean leaf;
                int numTreeObjects;
                int parentPointer;
                int[] childPointers = new int[2*t];
            TreeObject[] treeO = new TreeObject[(2*t)-1];
            
            dis.seek(offset);
            
            globalOffset = dis.readLong();
            leaf = dis.readBoolean();
            numTreeObjects = dis.readInt();
            parentPointer = dis.readInt();
            
            for(int i = 0; i < childPointers.length; i++){
                    
                    childPointers[i] = dis.readInt();
                    
            }
            
            for(int i = 0; i < treeO.length; i++){
                    
                    treeO[i] = new TreeObject(dis.readInt(), dis.readLong());
                    
            }
            
            return new BTreeNode((int)globalOffset, leaf, numTreeObjects, parentPointer, childPointers, treeO);
                
        }
        
        public void inOrderPrintToDump(BTreeNode n) throws IOException{
        	
        	try {
         	    
        		if(n.leaf){
            		for(int i = 0; i < n.numTreeObjects; i++){
            			
            			//add the leading zeros to the binary string
            			fullBinaryString = "";
            			for(int j = 0; j < Long.numberOfLeadingZeros(n.treeO[i].key); j++){
            				fullBinaryString += "0";
            			}
            			
            			fullBinaryString += Long.toBinaryString(n.treeO[i].key);
            			
            			bw.write(n.treeO[i].frequency + ":  " + binaryToSequence(fullBinaryString));
                		bw.newLine();
                	}
            	}
            	else{
            		for(int i = 0; i < n.numTreeObjects; i++){
                		inOrderPrintToDump(diskRead(n.childPointers[i]));
                		
                		//add the leading zeros to the binary string
            			fullBinaryString = "";
            			for(int j = 0; j < Long.numberOfLeadingZeros(n.treeO[i].key); j++){
            				fullBinaryString += "0";
            			}
            			
            			fullBinaryString += Long.toBinaryString(n.treeO[i].key);
            			
            			bw.write(n.treeO[i].frequency + ":  " + binaryToSequence(fullBinaryString));
                		bw.newLine();
                		
                	}
                	//giving errors, but should be there
            		//visit right subtree
                	//inOrderPrintToDump(diskRead(n.childPointers[n.numTreeObjects]));
            	}
        
        	} 
        	catch (IOException e) {
     		   e.printStackTrace();
     		   bw.close();
     	   	}
        	
        	
        	
    		
    	}
        
        public String binaryToSequence(String binary){
        	String dnaSequenceString = "";
        	
        	for(int base = 64 - sequenceLength*2; base < binary.length()-1; base += 2){
        		dnaSequenceString += this.baseBinToChar(binary.substring(base, base+2));
        	}
        	
        	return dnaSequenceString;
        }
        
        public char baseBinToChar(String st){
        	
        	if(st.equals("00")){
        		return 'A';
        	}
        	else if(st.equals("11")){
        		return 'T';
        	}
        	else if(st.equals("01")){
        		return 'G';
        	}
        	else if(st.equals("10")){
        		return 'C';
        	}
        	else{
        		//System.out.println("'"+st+"'");
        		return 'N';
        	}
        	
        }

}
