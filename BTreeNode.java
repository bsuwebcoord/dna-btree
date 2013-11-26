public class BTreeNode {

    TreeObject[] treeO;
    int numTreeObjects;
    int[] childPointers;
    int parentPointer;
    int globalOffset;
    boolean leaf;
    
    //Constructor for BTreeNode
    public BTreeNode(int i){
        numTreeObjects = i;
    }

}
