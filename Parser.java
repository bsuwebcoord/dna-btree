import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
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
        
        dnaSequence();
        
        
    }
    
    /** Parse gbk file to full dna sequence string
     * 
     * @param i
     * @param filename
     * @return
     */
    public ArrayList<String> dnaSequence(){
    	
    	Scanner file;
		try {
			file = new Scanner(new BufferedReader(new FileReader(fileName)));
		
			String line;
			boolean in_origin = false;
			ArrayList<String> dna = new ArrayList<String>();
			
			while(file.hasNextLine()){
				
				
				
					line = file.nextLine();
					//Remove all spaces and numbers
					line = line.replaceAll("\\s", "").replaceAll("[0-9]","");
					
					
					if(line.matches("^//")){
						in_origin = false;
					}
					
					
					if(line.matches("^ORIGIN")){
						in_origin = true;
						String origin = line;
						line = file.nextLine();//to not include ORIGIN
						line = line.replaceAll("\\s", "").replaceAll("[0-9]","");
					}
					
					//Add to ArrayList
					if(in_origin)
					{
						dna.add(line);
						//System.out.println(line);
					}
										
			}
			file.close();
			
			//Concatenate into one large string
			String dna_string = "";
			for(String d : dna){
				dna_string += d;
			}
			//System.out.println(dna_string);
			
			int chEndPointer = seqLength;
			
			for (int ch = 0; ch < dna_string.length(); ch++) 
	        {
				if(chEndPointer <= dna_string.length()){
					String sequence = dna_string.substring(ch,chEndPointer);
					if(sequence.contains("n")){
						sequence = null;
						System.out.println("N appeared in the sequence");
					}else{
												
						
						TreeObject t = new TreeObject(1, this.seq2Long(sequence));
						
						/*TESTING TO VIEW BINARY REPRESENTATION
						 *****************************
						System.out.println(sequence);
						long dnaLong = this.seq2Long(sequence);
						System.out.println("Long value: "+dnaLong);
						String binString = Long.toBinaryString(dnaLong);
						System.out.println("toBinaryString: "+binString);
						
						int length = (seqLength*2) - binString.length();
						System.out.println("Short by: "+length);
						
						//USE THIS SNIPPET FOR READING LONG BACK TO BINARY (PADS MISSING "0"s IN FRONT)
						String newBinString = "";//reset
						if(length > 0){
							char[] padArray = new char[length];
							Arrays.fill(padArray, '0');
							String padString = new String(padArray);
							newBinString = padString + binString;
						}
						
						System.out.println("toBinaryString: "+newBinString);
						System.out.println("string:         "+seq2BinStr(sequence));
						System.out.println("");
						*/
						
						
					}
					chEndPointer++;
				}
				
	        }
			
			return dna;
			
		} catch (FileNotFoundException e) {
			System.out.println();
            System.out.println("Could not find file. Please make sure the file is in the proper directory and that you entered the filename correctly.");
            System.out.println();
            System.exit(1);
		}
		return null;
    }
    
    public String seq2BinStr(String  subsequence){
    	
	    	String dnaBinStr = "";
	    	for (int ch = 0; ch < subsequence.length(); ch++) 
	        {
	    		dnaBinStr += this.dnaBase(subsequence.charAt(ch));
	        }
	    	
	    	return dnaBinStr;
	    	
	    
    	
    }
    
    public Long seq2Long(String  subsequence){
    	try{
	    	String dnaBinStr = "";
	    	for (int ch = 0; ch < subsequence.length(); ch++) 
	        {
	    		dnaBinStr += this.dnaBase(subsequence.charAt(ch));
	        }
	    	
	    	return Long.parseLong(dnaBinStr,2);
	    	
	    } catch (NumberFormatException nfe) {
	        System.out.println("NumberFormatException: " + nfe.getMessage());
	    }
    	return (long) 1;
    	
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
