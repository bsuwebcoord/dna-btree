/* Authors: David Allen, Guy Aydelotte, Dustin Calkins
 * Date: 12/10/13
 * Class: CS 342 Data Structures
 * Description: This program implements a BTree that can be created or searched using the GeneBankCreateBTree
 * 				or the GeneBankSearch class files respectively. 
 */

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
    
    public void copy(BTreeNode c){
    	this.globalOffset = c.globalOffset;
    	this.degree = c.degree;
    	this.leaf = c.leaf;
    	this.numTreeObjects = c.numTreeObjects;
    	this.parentPointer = c.parentPointer;
    	
    	for(int i = 0; i < this.childPointers.length; i++){
    		this.childPointers[i] = c.childPointers[i];
    	}
    	
    	for(int i = 0; i < this.treeO.length; i++){
    		this.treeO[i].frequency = c.treeO[i].frequency;
    		this.treeO[i].key = c.treeO[i].key;
    	}
    }
    
    
    public TreeObject getTreeObject(long key){
    	int i=0;
    	while(i < this.numTreeObjects && key > this.treeO[i].key){
            i++;
    	}
    	
    	return this.treeO[i];
    }
    
    public  void printNode(){
    	
    	System.err.printf("\nThe global offset is: %d\n", globalOffset);
    	System.err.printf("The leaf value is: " + leaf + "\n");
    	System.err.printf("The number of tree objects is: %d\n", numTreeObjects);
    	System.err.printf("The parent pointer is: %d\n", parentPointer);
    	System.err.printf("The degree is: %d\n", degree);
    	
    	System.err.printf("Node pointers: ");
    	
    	for(int i = 0; i < childPointers.length; i++){
    		System.err.printf("%d: %d, ", i, childPointers[i]);
    	}
    	
    	System.err.printf("\n");
    	System.err.printf("Tree objects: ");
    	
    	for(int i = 0; i < numTreeObjects; i++){
    		System.err.printf("Object #%d - Key: %d - Freq: %d, ", i, treeO[i].key, treeO[i].frequency);
    	}
    	
    	System.err.printf("\n");
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
