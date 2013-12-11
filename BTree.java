import java.io.*;

public class BTree {
        
        BTreeNode root = null;
        BTreeNode temp = null;
        BTreeNode left = null;
        BTreeNode right = null;
        BTreeNode fullChildNode = null;
        BTreeNode child = null;
        int numTreeNodes = 1;
        int t;
        int sequenceLength = 0;
        long byteOffsetRoot = 0;
        RandomAccessFile dis;

        File file = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        String fullBinaryString = "";
        
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
        		
        		BTreeNode y = new BTreeNode(-2, false, 0, 0, t);
        	
        		y.copy(fullChildNode);
        		
                BTreeNode z = new BTreeNode((int)dis.length(), y.leaf, t-1, y.parentPointer, t);

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

                x.treeO[i].key = y.treeO[t-1].key;
                x.treeO[i].frequency = y.treeO[t-1].frequency;
                
                x.numTreeObjects = x.numTreeObjects + 1;
                
                left.copy(y);
                right.copy(z);
                
                if(x.globalOffset > -1){
                    y.parentPointer = x.globalOffset;
                    z.parentPointer = x.globalOffset;
                }
                
                //write z at end of file
                diskWrite(-1, z);

                
                diskWrite(y.globalOffset, y);
                
                //write x in same position if it's not the root
                if(x.globalOffset != -2){
                	diskWrite(x.globalOffset, x);
                }
                //if x is the root, update the root
                else{
                	root.copy(x);
                }
                
        }
        
        public void bTreeInsert(long k) throws IOException{
        	
        		BTreeNode r = new BTreeNode(-2, false, 0, 0, t);
        	
                r.copy(root);
                
                if(root.numTreeObjects == (2*t)-1){
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
        	
        	BTreeNode x = new BTreeNode(-2, false, 0, 0, t);
        	
        	x.copy(n);
        	
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
                        
                        if(x.globalOffset == -2){
                        	root.copy(x);
                        }
                        else{
                        	diskWrite(x.globalOffset, x);
                        }
                        
                }
                else{

                        while(i >= 0 && k < x.treeO[i].key){
                                i--;
                        }
                        i++;
                        
                        	child.copy(diskRead(x.childPointers[i]));
                        	
                        	if(child.numTreeObjects == (2*t)-1){
                        		
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
                                
                        }
                        	
                        bTreeInsertNonfull(child , k);

                }
                
        }
        
        public void diskWrite(long offset, BTreeNode node) throws IOException{
                
                //write to specified offset or end of file, end of file can be reached with a negative offset argument
                if(offset < 0){
                        dis.seek(dis.length());
                }
                else{
                        dis.seek(offset);
                }
                
                //write the current position of the binary file, write first to get the correct starting position for the node
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
                
        }
        
        public BTreeNode diskRead(long offset) throws IOException{
                
            long globalOffset;
            boolean leaf;
            int numTreeObjects;
            int parentPointer;

            dis.seek(offset);

            
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
                		
                		//visit right subtree
                		if(i == n.numTreeObjects-1){
                			inOrderPrintToDump(diskRead(n.childPointers[i+1]));
                		}
                		
                	}

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
        	else if(st.equals("10")){
        		return 'G';
        	}
        	else if(st.equals("01")){
        		return 'C';
        	}
        	else{
        		return 'N';
        	}
        	
        }

}
