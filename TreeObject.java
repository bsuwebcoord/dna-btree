
public class TreeObject {
	
	long key;
	int frequency;
	
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

}
