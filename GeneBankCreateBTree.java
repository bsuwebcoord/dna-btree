import java.io.File;
import java.util.Scanner;


public class GeneBankCreateBTree {

	public static void main(String[] args) {
	    
	       boolean withCache = false;
	       int degree = 0;
	       int seqLength = 0;
	       String gbkFileName = "";
	       Cache dnaCache;
	       
	    
	       try{
	       
	           if(args[0].equals("0")){
	           }
	           else if(args[0].equals("1")){
	               withCache = true;
	           }
	           else{
	               throw new RuntimeException("Error: Invalid first argument. Must be of the form <0/1(no/with Cache)>");
	           }
	           
	       }
	       catch(RuntimeException e){
	       
	           System.out.println();
	           System.out.println("RuntimeException: " + e.getMessage());
	           System.out.println();
	           System.exit(1);
	           
	       }
	       
	       try{
	            Scanner scan = new Scanner(new File(gbkFileName));
	            
	            while(scan.hasNext()){
	    	        
		            String a = scan.next();
		            
		            if(a.equals("ORIGIN")){
		            
		                
		            
		            }
		        
		        }
		        scan.close();
	        }
	        catch(Exception e){
	            System.out.println();
	            System.out.println("Could not find file. Please make sure the file is in the proper directory and that you entered the filename correctly.");
	            System.out.println();
	            System.exit(1);
	        }
	        
	        
	           
	       
	       
	    }

}
