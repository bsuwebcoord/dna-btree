import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;


public class GeneBankSearch {

    public static void main(String[] args) throws IOException {
    	
    	boolean withCache = false;
        
        int debugLevel = 0;
        String btree_file = "";
        String query_file = "";
        Cache dnaCache;
        BTree tree =  null;
        
        //Error handling and setting values from user input:
        
      //this error will be handled later on if the filename is invalid
        btree_file = args[1];
        query_file = args[2];
        
        if(args.length < 3 || args.length > 5){
                System.out.println();
            System.out.println("You provided too few or too many arguments.");
            System.out.println("Arguments have the following form: <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
            System.out.println();
            System.exit(1);
        }
     
        try{
        
            if(args[0].equals("0")){
            }
            else if(args[0].equals("1")){
                withCache = true;
                if(args.length < 3){
                        System.out.println();
                    System.out.println("You provided too few arguments.");
                    System.out.println("Since you specified using a cache, you must provide a fourth argument of the following form: <cache size>");
                    System.out.println();
                    System.exit(1);
                }
            }
            else{
                throw new RuntimeException("Error: Invalid first argument. Must be of the form <0/1(no/with Cache)>.");
            }
            
        }
        catch(RuntimeException e){
        
            System.out.println();
            System.out.println("RuntimeException: " + e.getMessage());
            System.out.println();
            System.exit(1);
            
        }
        
      //if a cache was specified, check that the fifth argument is in the correct form
        if(args[0].equals("1")){
                
                try{
                if(Integer.parseInt(args[3]) > 0){
                        dnaCache = new Cache(Integer.parseInt(args[4]));
                }
                else{
                    throw new RuntimeException("Error: Invalid fourth argument. Must be of the form <cache size>, where the cache size is greater than 0.");
                }
                
            }
            catch(RuntimeException e){
            
                System.out.println();
                System.out.println("RuntimeException: " + e.getMessage());
                System.out.println();
                System.exit(1);
                
            }
                
                
          if(args.length == 5){
            try{
                
                if(args[4].equals("0")){
                        //do nothing since debug level is already set to 0 by default
                }
                else if(args[4].equals("1")){
                        debugLevel = 1;
                }
                else{
                    throw new RuntimeException("Error: Invalid fifth argument. Must be of the form <debug level>, where the debug level is 0  or 1.");
                }
                
            }
            catch(RuntimeException e){
            
                System.out.println();
                System.out.println("RuntimeException: " + e.getMessage());
                System.out.println();
                System.exit(1);
                
            }
          }
        }else if(args.length == 4){
            try{
                
                if(args[4].equals("0")){
                        //do nothing since debug level is already set to 0 by default
                }
                else if(args[4].equals("1")){
                        debugLevel = 1;
                }
                else{
                    throw new RuntimeException("Error: Invalid fifth argument. Must be of the form <debug level>, where the debug level is 0  or 1.");
                }
                
            }
            catch(RuntimeException e){
            
                System.out.println();
                System.out.println("RuntimeException: " + e.getMessage());
                System.out.println();
                System.exit(1);
                
            }
    }
        
        
        
        //get root node
        //scan the query file
        //search btree file for sequence (returns offset)
        //readFile starting at offset (returns BTreeNode)
        Scanner file;
        tree = new BTree(3); //get degree by counting length of first line of query file
        try{
        	file = new Scanner(new BufferedReader(new FileReader(query_file)));
            int seqLength = file.nextLine().length();
            System.out.println("Seq Length: "+seqLength);
            file.reset();
            
            tree.dis = new RandomAccessFile(btree_file, "rw");
            //get Root node
            BTreeNode root = tree.diskRead(tree.byteOffsetRoot);
            
            while(file.hasNextLine()){
            	//convert file.nextLine() to long
            	long sequence = 1;
            	tree.diskRead(tree.bTreeSearch(root, sequence));
            }
     	   
     	  
                
        }
        catch(FileNotFoundException e){
                
                        System.out.println();
                        System.out.println("RuntimeException: " + e.getMessage());
                        System.out.println();
                        System.exit(1);
                
        }
       
    }

}
