import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
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
        
        dnaSequence(a);
        
        
    }
    
    /** Parse gbk file to full dna sequence string
     * 
     * @param i
     * @param filename
     * @return
     */
    public ArrayList<String> dnaSequence(String filename){
    	
    	Scanner file;
		try {
			file = new Scanner(new BufferedReader(new FileReader(filename)));
		
			String line;
			boolean in_origin = false;
			ArrayList<String> dna = new ArrayList<String>();
			
			while(file.hasNextLine()){
				
				
				
					line = file.nextLine();
					
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
					
					
					
					if(in_origin)
					{
						dna.add(line);
					
						//System.out.println(line);
					}
					
					
					String dna_string = "";
					
					for(String d : dna){
						dna_string += d;
					}
					//System.out.println(dna_string);
					
					
					
					for (int ch = 0; ch < dna_string.length(); ch++) 
			        {
						if(seqLength <= dna_string.length()){
							String sequence = dna_string.substring(ch,seqLength);
							if(sequence.contains("n")){
								sequence = null;
								System.out.println("N appeared in the sequence");
							}else{
								//TreeObject t = new TreeObject(1, this.seq2Long(sequence));
								System.out.println(this.seq2Long(sequence));
							}
							seqLength++;
						}
						
			        }
					
			}
			file.close();
			return dna;
			
		} catch (FileNotFoundException e) {
			System.out.println();
            System.out.println("Could not find file. Please make sure the file is in the proper directory and that you entered the filename correctly.");
            System.out.println();
            System.exit(1);
		}
		return null;
    }
    
    public String seq2Long(String  subsequence){
    	
    	String dnaBinStr = null;
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
