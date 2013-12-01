import java.io.*;

public class BTree {
        
        BTreeNode root = null;
        int numTreeNodes = 0;
        int t;
        RandomAccessFile dis;
        int[] childrenInitializer = new int[2*t];
		TreeObject[] treeObjectInitializer = new TreeObject[(2*t)-1];
        
        
        //similar to B-Tree-Create code in book p. 492
        public BTree(){
        	
        		//initialize the child array and tree object array to a constant size in order to read correctly
        		for(int i = 0; i <= treeObjectInitializer.length; i++){
        			childrenInitializer[i] = 0;
        			treeObjectInitializer[i] = new TreeObject(0,0);
        		}
        		//initialize the last item in the child array that wasn't covered by the for loop above
        		childrenInitializer[childrenInitializer.length] = 0;
        	
                root = new BTreeNode(0, true, 0, 0, childrenInitializer, treeObjectInitializer);
                
                try{
                        
                        dis = new RandomAccessFile("gbkfile.bin", "r");
                        
                }
                catch(FileNotFoundException e){
                        
                		System.out.println();
                		System.out.println("RuntimeException: " + e.getMessage());
                		System.out.println();
                		System.exit(1);
                        
                }
                
        }
        
        //similar to B-Tree-Search code in book p. 492
        //returns the global offset of the node if the key is found, otherwise returns -1 if not found
        public long bTreeSearch(BTreeNode x, long k) throws IOException{
                int i = 0;
                
                while(i < x.numTreeObjects && k > x.treeO[i].key){
                        i++;
                }
                
                if(i <= x.numTreeObjects && k == x.treeO[i].key){
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
                BTreeNode z = new BTreeNode(0, y.leaf, t-2, y.parentPointer, childrenInitializer, treeObjectInitializer);
                for(int j = 0; j <= t-2; j++){
                        z.treeO[j].key = y.treeO[j+t].key;
                }
                if(!y.leaf){
                        for(int j = 0; j <= t-1; j++){
                                z.childPointers[j] = y.childPointers[j+t];
                        }
                }
                y.numTreeObjects = t - 1;
                for(int j = x.numTreeObjects + 1; j >= i+1; j--){
                        x.childPointers[j+1] = x.childPointers[j];
                }
                x.childPointers[i+1] = z.globalOffset;
                for(int j = x.numTreeObjects; j >= i; j--){
                        x.treeO[j+1].key = x.treeO[j].key;
                }
                x.treeO[i].key = y.treeO[t].key;
                x.numTreeObjects = x.numTreeObjects + 1;
                
                //write y in same position
                diskWrite(y.globalOffset, y);
                //write z at end of file
                diskWrite(-1, z);
                //write x in same position
                diskWrite(x.globalOffset, x);
        }
        
        public void bTreeInsert(long k) throws IOException{
                BTreeNode r = root;
                if(root.numTreeObjects == 2*t-1){
                        BTreeNode s = new BTreeNode(0, true, 0, 0, childrenInitializer, treeObjectInitializer);
                        root = s;
                        s.leaf = false;
                        s.numTreeObjects = 0;
                        s.childPointers[1] = r.globalOffset;
                        bTreeSplitChild(s, 1);
                        bTreeInsertNonfull(s,k);
                }
                else{
                        bTreeInsertNonfull(r,k);
                }
        }
        
        public void bTreeInsertNonfull(BTreeNode x, long k) throws IOException{
                int i = x.numTreeObjects;
                if(x.leaf){
                        while(i >= 0 && k < x.treeO[i].key){
                                x.treeO[i+1].key = x.treeO[i].key;
                                i--;
                        }
                        x.treeO[i+1].key = k;
                        x.numTreeObjects = x.numTreeObjects + 1;
                        diskWrite(x.globalOffset, x);
                }
                else{
                        while(i >= 0 && k < x.treeO[i].key){
                                i--;
                        }
                        i++;
                        BTreeNode child = diskRead(x.childPointers[i]);
                        if(child.numTreeObjects == 2*t-1){
                                bTreeSplitChild(x, i);
                                if(k > x.treeO[i].key){
                                        i++;
                                }
                        }
                        bTreeInsertNonfull(child , k);
                }
        }
        
        public void diskWrite(int offset, BTreeNode node) throws IOException{
                
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
                
                for(int i = 0; i <= node.childPointers.length; i++){
                        
                        dis.writeInt(node.childPointers[i]);
                        
                }
                
                for(int i = 0; i <= node.treeO.length; i++){
                    
                        dis.writeInt(node.treeO[i].frequency);
                        dis.writeLong(node.treeO[i].key);
                                    
                }
                
        }
        
        //using RandomAccessFile
        public BTreeNode diskRead(int offset) throws IOException{
                
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
                
                int globalOffset;
                boolean leaf;
                int numTreeObjects;
                int parentPointer;
                int[] childPointers = new int[2*t];
            TreeObject[] treeO = new TreeObject[(2*t)-1];
            
            dis.seek(offset);
            
            globalOffset = dis.readInt();
            leaf = dis.readBoolean();
            numTreeObjects = dis.readInt();
            parentPointer = dis.readInt();
            
            for(int i = 0; i <= childPointers.length; i++){
                    
                    childPointers[i] = dis.readInt();
                    
            }
            
            for(int i = 0; i <= treeO.length; i++){
                    
                    treeO[i] = new TreeObject(dis.readInt(), dis.readLong());
                    
            }
            
            return new BTreeNode(globalOffset, leaf, numTreeObjects, parentPointer, childPointers, treeO);
                
        }

}
