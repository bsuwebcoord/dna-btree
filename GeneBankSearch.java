import java.io.*;

public class GeneBankSearch {

    public static void main(String[] args) throws IOException {
    	
    	String bTreeFileName = "";
    	String queryFileName = "";
    	int degree = 0;
    	int debugLevel = 0;
    	boolean withCache = false;
    	long byteOffsetRoot = 0;
    	Cache dnaCache = null;
    	BTree tree =  null;
    	RandomAccessFile dis = null;
    	
    	//Error handling and setting values from user input:
        
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
                if(args.length < 4){
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
        
        //this error will be handled later on if the filename is invalid
        bTreeFileName = args[1];
        
        //this error will be handled later on if the filename is invalid
        queryFileName = args[2];
        
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
                
        }
        else if(args.length == 4){
                try{
                    
                    if(args[4].equals("0")){
                            //do nothing since debug level is already set to 0 by default
                    }
                    else if(args[4].equals("1")){
                            debugLevel = 1;
                    }
                    else{
                        throw new RuntimeException("Error: Invalid fourth argument. Must be of the form <debug level>, where the debug level is 0  or 1.");
                    }
                    
                }
                catch(RuntimeException e){
                
                    System.out.println();
                    System.out.println("RuntimeException: " + e.getMessage());
                    System.out.println();
                    System.exit(1);
                    
                }
        }
        
        try{
     	   dis = new RandomAccessFile(args[1], "rw"); 
        }
        catch(FileNotFoundException e){
                
                        System.out.println();
                        System.out.println("RuntimeException: " + e.getMessage());
                        System.out.println();
                        System.exit(1);
                
        }
        
        dis.seek(4);
        degree = dis.readInt();
        byteOffsetRoot = dis.readLong();
        
        tree = new BTree(degree);
        
        Parser parse = new Parser(-1, "");
        
        long foundKeyNodeGlobalPosition = 0;
        long dnaLong = 0;
        
        tree.dis = dis;
        
        tree.root = tree.diskRead(byteOffsetRoot);
        
        BufferedReader br = new BufferedReader(new FileReader(queryFileName));
        String line;
        while ((line = br.readLine()) != null) {
        	
        	dnaLong = Long.parseLong(parse.seq2Bin(line),2);
        	
        	//search the BTree for the sequence
        	foundKeyNodeGlobalPosition = tree.bTreeSearch(tree.root, dnaLong);
        	
        	//if not found print 0
        	if(foundKeyNodeGlobalPosition == -1){
        		System.out.println("0");
        	}
        	//else print the frequency
        	else{
        		BTreeNode updatedNode = tree.diskRead(foundKeyNodeGlobalPosition);

         	   	int i = 0;
            
         	   	while(dnaLong != updatedNode.treeO[i].key){
         	   		i++;
	            }
         	   	
         	   	System.out.println(updatedNode.treeO[i].frequency);
	           
        	}
        }
        br.close();
        
       
    }

}
