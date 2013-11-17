import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


public class GeneBankParser {
	
public static void main(String[] args) throws FileNotFoundException {
		
		
		Scanner file = new Scanner(new BufferedReader(new FileReader(args[0])));
		String line;
		boolean in_origin = false;
		
		while(file.hasNext()){
			line = file.next();
			if(line.matches("^//")){
				in_origin = false;
				
			}
			
			
			if(line.matches("^ORIGIN")){
				in_origin = true;
				String origin = line;
				line = file.next();
			}
			
			
			if(in_origin)
			{
			
				System.out.println(line);
			
			
			}
		}
		
		
		
		
		
		BufferedReader file2 = new BufferedReader(new FileReader(args[0]));
		String new_line;
		in_origin = false;
				
		try {
			while((new_line = file2.readLine()) != null){
				
				//System.out.println(new_line);
				
				if(new_line.matches("^//")){
					in_origin = false;
					System.out.println("**********END"+ in_origin);
				}
				
				
				if(new_line.matches("^ORIGIN")){
					in_origin = true;
					System.out.println("**********NEW ORIGIN");
				}
				
				
				if(in_origin)
				{
				
					System.out.println(new_line);
				
				
				}
				
				
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		

	}

}
