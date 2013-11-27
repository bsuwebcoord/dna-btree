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
