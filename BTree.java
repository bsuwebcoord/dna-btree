
public class BTree {
	
	BTreeNode root = null;
	
	//similar to B-Tree-Create code in book p. 492
	public BTree(){
		root = new BTreeNode(5);
		root.leaf = true;
		root.numTreeObjects = 0;
	}
	
	//similar to B-Tree-Search code in book p. 492
	bTreeSearch(BTreeNode x, long k){
		int i = 1;
		
		while(i < x.numTreeObjects && k > x.treeO[i].key){
			i++;
		}
		
		if(i <= x.numTreeObjects && k == x.key){
			return (x,i);
		}
		else if(x.leaf){
			return null;
		}
		else{
			diskRead(x, Ci){
				return bTreeSearch(x.Ci, k);
			}
		}
	}
	
	bTreeSplitChild(BTreeNode x, int i){
		BTreeNode z = new BTreeNode(5);
		y 
	}
	
	

}
