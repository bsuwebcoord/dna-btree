import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


//not sure if we need parser class or just parse in the main method of GeneBankCreateBTree
public class Parser {

    int seqLength = 0;
    String fileName = "";
    Scanner scan = null;

    //Constructor for Parser
    public Parser(int i, String a){
        seqLength = i;
        fileName = a;
        
        dna2Long(dnaSequence());
        
        
    }
    
    /** Parse gbk file to full dna sequence string
     * 
     * @param i
     * @param filename
     * @return
     */
    public String dnaSequence(){
    	
    	Scanner file;
    	String dna = "";
    	String line = "";
		boolean in_origin = false;
		
		
		try {
			
			file = new Scanner(new BufferedReader(new FileReader(fileName)));
			
			while(file.hasNextLine()){
			
					line = file.nextLine();
					//Remove all spaces and numbers
					line = line.replaceAll("\\s", "").replaceAll("[0-9]","");
					
					
					if(line.matches("^//")){
						in_origin = false;
					}
					
					
					if(line.matches("^ORIGIN")){
						in_origin = true;
						line = file.nextLine();//to not include ORIGIN
						line = line.replaceAll("\\s", "").replaceAll("[0-9]","");//line immediately after ORIGIN
					}
					
					//concatenate
					if(in_origin)
					{
						dna += line;
						//System.out.println(line);
					}
										
			}
			file.close();
			
			//System.out.println(dna);
			return dna;
			
		} catch (FileNotFoundException e) {
			System.out.println();
            System.out.println("Could not find file. Please make sure the file is in the proper directory and that you entered the filename correctly.");
            System.out.println();
            System.exit(1);
		}
		return dna;
		
    }
    
    
        
    /** takes fully parsed and concatenated dna string
     * 
     * @param dna_string 
     */
    public void dna2Long(String dna_string){
    	
    	
		int chEndPointer = seqLength;
		
		for (int ch = 0; ch < dna_string.length(); ch++) 
        {
			if(chEndPointer <= dna_string.length()){
				
				String sequence = dna_string.substring(ch,chEndPointer);
				
				//detect if "n" is in the sequence
				if(sequence.contains("n") || sequence.contains("N")){
					sequence = null;
					System.out.println("N appeared in the sequence");
				}else{
					
					long dnaLong = Long.parseLong(this.seq2Bin(sequence),2);
					TreeObject t = new TreeObject(1, dnaLong); 
					System.out.println(t);
					
					
					/*TESTING TO VIEW BINARY REPRESENTATION
					 *****************************
					System.out.println(sequence);
					long dnaBin = this.seq2Bin(sequence);
					
					System.out.println("Long value: "+dnaLong);
					
					//convert from long back to string
					String binString = Long.toBinaryString(dnaLong);
					System.out.println("toBinaryString: "+binString);
					
					//determin if length of string is less than sequence length (*2)
					int length = (seqLength*2) - binString.length();
					System.out.println("Short by: "+length);
					
					//USE THIS SNIPPET FOR READING LONG BACK TO BINARY (PADS MISSING "0"s IN FRONT)
					String newBinString = "";//reset
					if(length > 0){
						char[] padArray = new char[length];
						Arrays.fill(padArray, '0');
						String padString = new String(padArray);
						newBinString = padString + binString; //pad with "0"s if needed
					}
					
					System.out.println("toBinaryString: "+newBinString);
					System.out.println("string:         "+seq2BinStr(sequence));
					System.out.println("");
					*/
					
					
				}
				chEndPointer++;
			}
			
        }
    }
    
   
    /** Convert dna sequence to binary representation
     * 
     * @param subsequence
     * @return
     */
    public String seq2Bin(String  subsequence){
    	
	    	String dnaBinStr = "";
	    	for (int ch = 0; ch < subsequence.length(); ch++) 
	        {
	    		dnaBinStr += this.dnaBase(subsequence.charAt(ch));
	        }
	    	
	    	return dnaBinStr;
	    
    }
    
        
            
    public String dnaBase(char ch){
    	
    	switch(ch){
	    	case 'A':
	    	case 'a':
	    		return "00";
	    		
	    	case 'T':
	    	case 't':
	    		return "11";
	    		
	    	case 'G':
	    	case 'g':
	    		return "01";
	    		
	    	case 'C':
	    	case 'c':
	    		return "10";
    	}
    	return "";
    }
    
     

}
