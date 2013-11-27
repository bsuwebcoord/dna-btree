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
	boolean leaf;
	int numTreeObjects;
	int parentPointer;
	int[] childPointers;
    TreeObject[] treeO;
    
    
    //Constructor for BTreeNode
    public BTreeNode(int a, boolean b, int c, int d, int[] e, TreeObject[] f){
    	globalOffset = a;
    	leaf = b;
        numTreeObjects = c;
        parentPointer = d;
        childPointers = e;
        treeO = f;
    }

}
