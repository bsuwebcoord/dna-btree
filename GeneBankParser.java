import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class GeneBankParser {
	
public static void main(String[] args) throws FileNotFoundException {
		
		
		Scanner file = new Scanner(new BufferedReader(new FileReader(args[0])));
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
				
		}
		
		String dna_string = "";
		
		for(String d : dna){
			dna_string += d;
		}
		System.out.println(dna_string);
		
		
		
		
		

	}


	

}
