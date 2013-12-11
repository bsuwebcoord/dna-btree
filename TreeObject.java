/* Authors: David Allen, Guy Aydelotte, Dustin Calkins
 * Date: 12/10/13
 * Class: CS 342 Data Structures
 * Description: This program implements a BTree that can be created or searched using the GeneBankCreateBTree
 * 				or the GeneBankSearch class files respectively. 
 */

public class TreeObject {

	int frequency;
	long key;
	
	public TreeObject(int frequency, long key){
		this.frequency = frequency;
		this.key = key;
	}
	
	public long getKey(){
		return key;
	}
	
	public int getFreqency(){
		return frequency;
	}
	
	public void addFrequency(){
		frequency++;
	} 
	
	
	public String toString(){
		
		String string = key+" : "+frequency;
		
		return string;
	}

}
