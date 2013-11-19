import java.io.*;

public class BTree {
	
	BTreeNode root = null;
	int numTreeNodes = 0;
	int t;
	
	//similar to B-Tree-Create code in book p. 492
	public BTree(){
		root = new BTreeNode(5);
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
			//I'm not sure what Ci is in the book
			//needs to be a BTreeNode though
			diskRead(x.Ci);
			return bTreeSearch(x.Ci, k);
		}
	}
	
	bTreeSplitChild(BTreeNode x, int i){
		BTreeNode z = new BTreeNode(5);
		y = x.Ci;
		z.leaf = y.leaf;
		z.numTreeObjects = t - 1;
		for(int j = 1; j <= t-1; j++){
			z.treeO[j].key = y.treeO[j+t].key;
		}
		if(!y.leaf){
			for(int j = 1; j <= t; j++){
				z.Cj = y.C(j+t)
			}
		}
		y.numTreeObjects = t - 1;
		for(int j = x.numTreeObjects + 1; j >= i+1; j--){
			x.C(j+1) = x.Cj
		}
		x.C(i+1) = z;
		for(int j = x.numTreeObjects; j >= i; j--){
			x.treeO[j+1].key = x.treeO[j].key;
		}
		x.treeO[i].key = y.treeO[t].key;
		x.numTreeObjects = x.numTreeObjects + 1;
		diskWrite(y);
		diskWrite(z);
		diskWrite(x);
	}
	
	bTreeInsert(long k){
		BTreeNode r = root;
		if(root.numTreeObjects == 2t-1){
			BTreeNode s = new BTreeNode(5);
			root = s;
			s.leaf = false;
			s.numTreeObjects = 0;
			s.C1 = r;
			bTreeSplitChild(s, 1);
			bTreeInsertNonfull(s,k);
		}
		else{
			bTreeInsertNonfull(r,k);
		}
	}
	
	bTreeInsertNonfull(BTreeNode x, long k){
		int i = x.numTreeObjects;
		if(x.leaf){
			while(i >= 1 && k < x.treeO[i].key){
				x.treeO[i+1].key = x.treeO[i].key;
				i--;
			}
			x.treeO[i+1].key = k;
			x.numTreeObjects = x.numTreeObjects + 1;
			diskWrite(x);
		}
		else{
			while(i >= 1 && k < x.treeO[i].key){
				i--;
			}
			i++;
			diskRead(x.Ci)
			if(x.Ci.numTreeObjects == 2t-1){
				bTreeSplitChild(x, i);
				if(k > x.treeO[i].key){
					i++;
				}
			}
			bTreeInsertNonfull(x.Ci, k);
		}
	}
	
	diskWrite(BTreeNode n){
		
	}
	
	diskRead(BTreeNode n){
		DataInputStream dis = new DataInputStream(new FileInputStream("gbkfile.bin"));
		for(int i = 0; i < numTreeNodes; i++){
			//set the data according to the order which we're storing
			//Example:
			//long l1 = dis.readLong();
			//double d1 = disreadDouble();
			//long l2 = disreadLong();
		}
		dis.close();
	}

}
