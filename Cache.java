
import java.util.*;
public class Cache<E> {
    
    public int cacheSize;
    public int hits;
    public int references;
    List<E> list = new LinkedList<E>();
        
    //constructor
    public Cache(int i){
        cacheSize = i;
    }
        
    //returns object if object s is found in the cache, adds object to cache regardless of hit or miss
    public E getObject(E s){
        
        references++;
        Boolean foundInList = false;
        
        Iterator<E> x = list.listIterator(0);
        
        while(x.hasNext() && !foundInList){
            if(s.equals(x.next())){
                x.remove();
                hits++;
                foundInList = true;     
            }
        }
        
        return addObject(s);
        
    }

    //adds object to the first position of cache
    public E addObject(E s){
    	
    	E deletedObject = null;
        
        if(list.size() == cacheSize){
            deletedObject = list.remove(cacheSize-1);
        }
        list.add(0, s);
        
        //System.out.println(list.size());
        
        return deletedObject;
        
    }
    
    //removes object from index i of cache and returns it
    public E removeObject(int i){
        
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
