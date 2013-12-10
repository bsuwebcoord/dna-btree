public class BTreeNode {
        
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
        int degree;
        boolean leaf;
        int numTreeObjects;
        int parentPointer;
        int[] childPointers;
        TreeObject[] treeO;
    
    
    //Constructor for BTreeNode
    public BTreeNode(int a, boolean b, int c, int d, int e){
        globalOffset = a;
        leaf = b;
        numTreeObjects = c;
        parentPointer = d;
        degree = e;
        
        childPointers = new int[2*degree];
        treeO = new TreeObject[2*degree];
         
        //initialize the child array and tree object array to a constant size in order to read correctly
        for(int i = 0; i < treeO.length; i++){
     	   childPointers[i] = 0;
     	   treeO[i] = new TreeObject(1,0);
        }
        //initialize the last item in the child array that wasn't covered by the for loop above
        childPointers[childPointers.length-1] = 0;
    }
    
    
    public TreeObject getTreeObject(long key){
    	int i=0;
    	while(i < this.numTreeObjects && key > this.treeO[i].key){
            i++;
    	}
    	
    	return this.treeO[i];
    }
    
    public  void printNode(){
    	
    	System.out.println(globalOffset);
    	
    	System.out.println("Printing node");
    	
    	for(int i = 0; i < childPointers.length; i++){
    		//System.out.println(childPointers.length);
    		System.out.println(childPointers[i]);
    	}
    }
    
    public Boolean equals(BTreeNode x){
    	
    	if(globalOffset == x.globalOffset){
    		return true;
    	}
    	else{
    		return false;
    	}
    	
    }

}
