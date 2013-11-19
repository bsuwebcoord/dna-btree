import java.io.File;
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
        
        try{
            scan = new Scanner(new File(fileName));
        }
        catch(Exception e){
            System.out.println();
            System.out.println("Could not find file. Please make sure the file is in the proper directory and that you entered the filename correctly.");
            System.out.println();
            System.exit(1);
        }
    }
    
    public long nextSequence(){
    	
    	while(scan.hasNext()){
    	    
            String a = scan.next();
            
            if(a.equals("ORIGIN")){
            
                
            
            }
        
        }
        scan.close();
    	
        return 5;
    }

}
