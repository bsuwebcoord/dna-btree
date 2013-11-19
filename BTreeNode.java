
public class BTreeNode {

    TreeObject[] treeO;
    int numTreeObjects;
    int[] childPointers;
    int parentPointer;
    
    //Constructor for BTreeNode
    public BTreeNode(int i){
        numTreeObjects = i;
    }

}
