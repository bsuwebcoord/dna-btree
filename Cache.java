/* Authors: David Allen, Guy Aydelotte, Dustin Calkins
 * Date: 12/10/13
 * Class: CS 342 Data Structures
 * Description: This program implements a BTree that can be created or searched using the GeneBankCreateBTree
 * 				or the GeneBankSearch class files respectively. 
 */

import java.util.*;
public class Cache {
    
    public int cacheSize;
    public int hits;
    public int references;
    List<BTreeNode> list = new LinkedList<BTreeNode>();
        
    //constructor
    public Cache(int i){
        cacheSize = i;
    }
        
    //returns object if object s is found in the cache, adds object to cache regardless of hit or miss
    public BTreeNode getObject(BTreeNode s, int j){
        
        references++;
        Boolean foundInList = false;
        BTreeNode nextNode = null;
        
        Iterator<BTreeNode> x = list.listIterator(0);
        
        while(x.hasNext() && !foundInList){
        	
        	nextNode = x.next();
        	
            if(s.equals(nextNode)){
                x.remove();
                hits++;
                foundInList = true;
                s = nextNode;
            }
        }
        
        s.treeO[j].frequency++;
        
        return addObject(s);
        
    }
    
    //search the cache with a global offset
    public int searchWithPosition(long pos, long k){
    	
    	Boolean foundInList = false;
        BTreeNode nextNode = null;
        int freq = -1;
        
        Iterator<BTreeNode> x = list.listIterator(0);
        
        while(x.hasNext() && !foundInList){
        	
        	nextNode = x.next();
        	
            if(pos == nextNode.globalOffset){
                x.remove();
                hits++;
                foundInList = true;
                
                int i = 0;
	            while(k != nextNode.treeO[i].key){
	            	i++;
	            }
	            
	            freq = nextNode.treeO[i].frequency;
                addObject(nextNode);
            }
        }
        
        return freq;
    }

    //adds object to the first position of cache, returns deleted object if an object was bumped out of the cache
    public BTreeNode addObject(BTreeNode s){
    	
    	BTreeNode deletedObject = null;
        
        if(list.size() == cacheSize){
            deletedObject = list.remove(cacheSize-1);
        }
        list.add(0, s);
        
        return deletedObject;
        
    }
    
    //removes object from index i of cache and returns it
    public BTreeNode removeObject(int i){
        
        return list.remove(i);
        
    }
    
    //clears the cache
    public void clearCache(){
        
        list = null;
        
    }
    
    //displays the cache size
    public String toString(){
        
        return String.format("Cache Size: %s", cacheSize);
        
    }

}
