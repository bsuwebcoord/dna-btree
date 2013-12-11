import java.io.*;

public class BTree {
        
        static BTreeNode root = null;
        static BTreeNode temp = null;
        static BTreeNode left = null;
        static BTreeNode right = null;
        static BTreeNode fullChildNode = null;
        static BTreeNode child = null;
        int numTreeNodes = 1;
        int t;
        int sequenceLength = 0;
        int numSplits = 0;
        long byteOffsetRoot = 0;
        RandomAccessFile dis;

        File file = null;
        File dnaFile = null;
        FileWriter fw, yw = null;
        BufferedWriter bw, zw = null;
        String fullBinaryString = "";
        String fullDNASequence;
        
        public BTree(int degree) throws IOException{
                
                        t = degree;
                        
                        root = new BTreeNode(-2, false, 0, 0, t);
                        temp = new BTreeNode(-2, false, 0, 0, t);
                        left = new BTreeNode(-2, false, 0, 0, t);
                        right = new BTreeNode(-2, false, 0, 0, t);
                        fullChildNode = new BTreeNode(-2, false, 0, 0, t);
                        child = new BTreeNode(-2, false, 0, 0, t);
                        
                      //open the dump file
                        try {
                     	    
                   			file = new File("dump.txt");
                   			dnaFile = new File("fullDna.txt");
                    
                   			// if file doesnt exists, then create it
                   			if (!file.exists()) {
                   				file.createNewFile();
                   			}
                   			
                   			if (!dnaFile.exists()) {
                   				dnaFile.createNewFile();
                   			}
                    
                   			fw = new FileWriter(file.getAbsoluteFile());
                   			bw = new BufferedWriter(fw);
                   			
                   			yw = new FileWriter(dnaFile.getAbsoluteFile());
                   			zw = new BufferedWriter(yw);
                    
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
                	           	

                            temp.copy(diskRead(x.childPointers[i]));
                            return bTreeSearch(temp, k);




                		
                }
        }
        
        public void bTreeSplitChild(BTreeNode n, int i) throws IOException{
        	
        		
        	
        		BTreeNode x = new BTreeNode(-2, false, 0, 0, t);
        	
        		x.copy(n);
        	
                //BTreeNode y = diskRead(x.childPointers[i]);
        		
        		BTreeNode y = new BTreeNode(-2, false, 0, 0, t);
        	
        		y.copy(fullChildNode);
        		
                BTreeNode z = new BTreeNode((int)dis.length(), y.leaf, t-1, y.parentPointer, t);
                
                if(numSplits == 0){
        			System.err.println("Root before split");
        			root.printNode();
        			System.err.println("fullChildNode before first split");
        			fullChildNode.printNode();
        			System.err.println("y node before first split");
        			y.printNode();
        			System.err.println("z node before first split");
        			z.printNode();
        		}
                //System.out.println("Print first z:");
                //z.printNode();
                //System.out.println("Print first y:");
                //y.printNode();
                numTreeNodes++;
                for(int j = 0; j <= t-2; j++){
                        z.treeO[j].key = y.treeO[j+t].key;
                        z.treeO[j].frequency = y.treeO[j+t].frequency;
                }
                if(!y.leaf){
                        for(int j = 0; j <= t-1; j++){
                                z.childPointers[j] = y.childPointers[j+t];
                        }
                }
                y.numTreeObjects = t - 1;
                for(int j = x.numTreeObjects; j >= i + 1; j--){
                        x.childPointers[j+1] = x.childPointers[j];
                }
                x.childPointers[i+1] = z.globalOffset;
                for(int j = x.numTreeObjects-1; j >= i; j--){
                        x.treeO[j+1].key = x.treeO[j].key;
                        x.treeO[j+1].frequency = x.treeO[j].frequency;
                }
                //changed y.treeO[t] to y.treeO[t-1]
                x.treeO[i].key = y.treeO[t-1].key;
                x.treeO[i].frequency = y.treeO[t-1].frequency;
                
                x.numTreeObjects = x.numTreeObjects + 1;
                //System.out.printf("\nThe number of tree objects is: %d\n", x.numTreeObjects);
                //write y in same position
                
                left.copy(y);
                right.copy(z);
                
                y.parentPointer = x.globalOffset;
                z.parentPointer = x.globalOffset;
                
                //System.out.println("Node z:");
                //z.printNode();
                //write z at end of file
                diskWrite(-1, z);
                
                
                //System.out.println("Node y:");
                //y.printNode();
                
                diskWrite(y.globalOffset, y);
                
                //System.out.println("Node x:");
                //x.printNode();
                //write x in same position if it's not the root
                if(x.globalOffset != -2){
                	diskWrite(x.globalOffset, x);
                }
                //if x is the root, update the root
                else{
                	root.copy(x);
                }
                
                if(numSplits == 0){
        			System.out.println("Root after split");
        			root.printNode();
        			System.out.println("fullChildNode after first split");
        			fullChildNode.printNode();
        			System.out.println("y node after first split");
        			y.printNode();
        			System.out.println("z node after first split");
        			z.printNode();
        		}
                
                numSplits++;
        }
        
        public void bTreeInsert(long k) throws IOException{
        	
        		BTreeNode r = new BTreeNode(-2, false, 0, 0, t);
        	
                r.copy(root);
                
                //System.out.printf("\nThe number of tree objects of root is: %d\n", root.numTreeObjects);
                if(root.numTreeObjects == (2*t)-1){
                		//the -2 globalOffset will write to the end of file since it's negative, and is a unique identifier for the root
                        root = new BTreeNode(-2, false, 0, 0, t);
                        numTreeNodes++;
                        root.childPointers[0] = (int)dis.length();
                        fullChildNode.copy(r);
                        fullChildNode.globalOffset = (int)dis.length();
                        diskWrite(-1, r);
                        bTreeSplitChild(root, 0);
                        bTreeInsertNonfull(root,k);
                }
                else{
                        bTreeInsertNonfull(root,k);
                }
        }
        
        public void bTreeInsertNonfull(BTreeNode n, long k) throws IOException{
                        //added the subtraction of i by 1 to avoid indexOutOfBounds exception
                        //System.out.printf("\nThe number of tree objects of x is: %d\n", x.numTreeObjects);
        	
        	//x.printNode();
        	//System.out.println("Printing key");
        	
        	BTreeNode x = new BTreeNode(-2, false, 0, 0, t);
        	
        	x.copy(n);
        	
        	System.out.println(k);
        	
        	
                int i = x.numTreeObjects - 1;
                if(x.leaf){
                        while(i >= 0 && k < x.treeO[i].key){
                                x.treeO[i+1].key = x.treeO[i].key;
                                x.treeO[i+1].frequency = x.treeO[i].frequency;
                                i--;
                        }
                        x.treeO[i+1].key = k;
                        x.treeO[i+1].frequency = 1;
                        x.numTreeObjects = x.numTreeObjects + 1;
                        //System.out.printf("\nThe number of tree objects in the bTreeInsertNonfull is: %d\n", x.numTreeObjects);
                        
                        if(x.globalOffset == -2){
                        	root.copy(x);
                        }
                        else{
                        	diskWrite(x.globalOffset, x);
                        }
                        
                }
                else{
                                //System.out.printf("\ni's value is: %d\n", i);
                        while(i >= 0 && k < x.treeO[i].key){
                                i--;
                        }
                        i++;
                        System.out.printf("\ni's value is: %d\n", i);
                        //System.out.println(x.childPointers[i]);
                        //System.out.printf("\nThe size of the bin file is: %d\n", dis.length());
                        
                        try{
                        	child.copy(diskRead(x.childPointers[i]));
                        	
                        	System.out.println("Child read");
                        	
                        	if(child.numTreeObjects == (2*t)-1){
                        		
                        		System.out.println("Standard error");
                        		System.err.println("Child full");
                        		System.out.println("Standard error");
                        		
                        		fullChildNode.copy(child);
                        		
                                bTreeSplitChild(x, i);
                                
                                //if x is the root, set x to the root
                                if(x.globalOffset == -2){
                                	x.copy(root);
                                }
                                //if x isn't the root, read in the node
                                else{
                                	x.copy(diskRead(x.globalOffset));
                                }
                                
                                
                                
                                if(k > x.treeO[i].key){
                                        i++;
                                        child.copy(right);
                                }
                                else{
                                	child.copy(left);
                                }
                                
                              //read in the updated split child
                              //child = diskRead(x.childPointers[i]);
                        }
                        bTreeInsertNonfull(child , k);
                        }
                        catch(Exception e){
                        	x.printNode();
                        }
                        
                        
                        
                        
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
                        dis.seek(dis.length());
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
                //if(offset < 0){
                //	dis.writeBoolean(false);
                //}
                
                
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
                int[] newChildPointers = new int[2*t];
                TreeObject[] newTreeO = new TreeObject[(2*t)-1];
            
            try{
            	dis.seek(offset);
            }
            catch(Exception e){
            	System.out.println("The offset is:");
            	System.out.println(offset);
            }
            
            
            
            
            globalOffset = dis.readLong();
            leaf = dis.readBoolean();
            numTreeObjects = dis.readInt();
            parentPointer = dis.readInt();
            
            BTreeNode readNode = new BTreeNode((int)globalOffset, leaf, numTreeObjects, parentPointer, t);
            
            for(int i = 0; i < readNode.childPointers.length; i++){
                    
                    readNode.childPointers[i] = dis.readInt();
                    
            }
            
            for(int i = 0; i < readNode.treeO.length; i++){
                    
                    readNode.treeO[i] = new TreeObject(dis.readInt(), dis.readLong());
                    
            }
            
            return readNode;
                
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
            			
            			if(binaryToSequence(fullBinaryString).equals("AG")){
            				System.out.println("Reached leaf");
            				n.printNode();
            				System.out.println("Root");
            				root.printNode();
            			}
            			
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
                		
                		if(binaryToSequence(fullBinaryString).equals("AG")){
                			System.out.println("Not leaf");
            				n.printNode();
            				System.out.println("Root");
            				root.printNode();
            			}
                		
                		//visit right subtree
                		if(i == n.numTreeObjects-1){
                			inOrderPrintToDump(diskRead(n.childPointers[i+1]));
                		}
                		
                	}

            	}
        
        	} 
        	catch (IOException e) {
        		System.out.println("Problem");
     		   e.printStackTrace();
     		   bw.close();
     	   	}
        	
        	
        	
    		
    	}
        
        public void writeDNASequence() throws IOException{
        	zw.write(fullDNASequence);
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
        	else if(st.equals("10")){
        		return 'G';
        	}
        	else if(st.equals("01")){
        		return 'C';
        	}
        	else{
        		//System.out.println("'"+st+"'");
        		return 'N';
        	}
        	
        }

}
