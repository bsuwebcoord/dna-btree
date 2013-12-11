/* Authors: David Allen, Guy Aydelotte, Dustin Calkins
 * Date: 12/10/13
 * Class: CS 342 Data Structures
 * Description: This program implements a BTree that can be created or searched using the GeneBankCreateBTree
 * 				or the GeneBankSearch class files respectively. 
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Parser {

    int seqLength = 0;
    String fileName = "";
    String entireDNASequence = "";
    Scanner scan = null;
    int subStart = 0;
    int subEnd = 0;

    //Constructor for Parser
    public Parser(int i, String a){
    	if(i < 0){
    		//do nothing, but you can use the methods in Parse
    	}
    	else{
    		seqLength = i;
            fileName = a;
            entireDNASequence = dnaSequence();
            entireDNASequence += 'N';
            dna2Long(dnaSequence());
            subEnd = i;
    	}
     
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
                                                //insert an N into the dna string so that sequences that span between two ORIGINs aren't counted
                                                if(dna.length() != 0){
                                                	dna += 'N';
                                                }
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
                                        }
                                                                                
                        }
                        file.close();
                        
                        return dna;
                        
                } catch (FileNotFoundException e) {
                        System.err.println();
            System.err.println("Could not find file. Please make sure the file is in the proper directory and that you entered the filename correctly.");
            System.err.println();
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
                                }else{
                                        
                                        long dnaLong = Long.parseLong(this.seq2Bin(sequence),2);
                                        TreeObject t = new TreeObject(1, dnaLong); 
                                             
                                }
                                chEndPointer++;
                        }
                        
        }
    }
    
    public long nextBinSequence(){
    	String subSequence = "";
    	if(subEnd >= entireDNASequence.length()){
    		return -1;
    	}
    	subSequence = entireDNASequence.substring(subStart, subEnd);
    	while(subSequence.contains("n") || subSequence.contains("N")){
    		subStart++;
    		subEnd++;
    		if(subEnd >= entireDNASequence.length()){
        		return -1;
        	}
    		subSequence = entireDNASequence.substring(subStart, subEnd);
    	}
    	subStart++;
    	subEnd++;
    	return Long.parseLong(seq2Bin(subSequence), 2);
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
                            return "10";
                            
                    case 'C':
                    case 'c':
                            return "01";
            }
            return "";
    }
    
     

}
