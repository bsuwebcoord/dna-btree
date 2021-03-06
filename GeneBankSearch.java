/* Authors: David Allen, Guy Aydelotte, Dustin Calkins
 * Date: 12/10/13
 * Class: CS 342 Data Structures
 * Description: This program implements a BTree that can be created or searched using the GeneBankCreateBTree
 * 				or the GeneBankSearch class files respectively. 
 */

import java.io.*;

public class GeneBankSearch {

    public static void main(String[] args) throws IOException {
    	
    	String bTreeFileName = "";
    	String queryFileName = "";
    	int degree = 0;
    	int frequency = -1;
    	boolean withCache = false;
    	long byteOffsetRoot = 0;
    	Cache dnaCache = null;
    	BTree tree =  null;
    	RandomAccessFile dis = null;
    	
    	//Error handling and setting values from user input:
        if(args.length < 3 || args.length > 5){
            System.err.println();
            System.err.println("You provided too few or too many arguments.");
            System.err.println("Arguments have the following form: <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
            System.err.println();
            System.exit(1);
        }
     
        try{
        
            if(args[0].equals("0")){
            }
            else if(args[0].equals("1")){
                withCache = true;
                if(args.length < 4){
                    System.err.println();
                    System.err.println("You provided too few arguments.");
                    System.err.println("Since you specified using a cache, you must provide a fourth argument of the following form: <cache size>");
                    System.err.println();
                    System.exit(1);
                }
            }
            else{
                throw new RuntimeException("Error: Invalid first argument. Must be of the form <0/1(no/with Cache)>.");
            }
            
        }
        catch(RuntimeException e){
        
            System.err.println();
            System.err.println("RuntimeException: " + e.getMessage());
            System.err.println();
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
                        dnaCache = new Cache(Integer.parseInt(args[3]));
                }
                else{
                    throw new RuntimeException("Error: Invalid fourth argument. Must be of the form <cache size>, where the cache size is greater than 0.");
                }
            }
            catch(RuntimeException e){
            
                System.err.println();
                System.err.println("RuntimeException: " + e.getMessage());
                System.err.println();
                System.exit(1);
                
            }
                
            if(args.length == 5){
            	try{
                        
            		if(args[4].equals("0")){
            			//do nothing since debug level is already set to 0 by default
            		}
            		else{
            			throw new RuntimeException("Error: Invalid fifth argument. Must be of the form <debug level>, where the debug level is 0.");
            		}
                        
            	}
            	catch(RuntimeException e){
                    
            		System.err.println();
            		System.err.println("RuntimeException: " + e.getMessage());
            		System.err.println();
            		System.exit(1);
                        
            	}
            }
                
        }
        else if(args.length == 4){
                try{
                    
                    if(args[3].equals("0")){
                            //do nothing since debug level is already set to 0 by default
                    }
                    else{
                        throw new RuntimeException("Error: Invalid fourth argument. Must be of the form <debug level>, where the debug level is 0.");
                    }
                    
                }
                catch(RuntimeException e){
                
                    System.err.println();
                    System.err.println("RuntimeException: " + e.getMessage());
                    System.err.println();
                    System.exit(1);
                    
                }
        }
        
        try{
     	   dis = new RandomAccessFile(args[1], "rw"); 
        }
        catch(FileNotFoundException e){
                
                        System.err.println();
                        System.err.println("RuntimeException: " + e.getMessage());
                        System.err.println();
                        System.exit(1);
                
        }
        
        dis.seek(0);
        int numNodes = dis.readInt();
        degree = dis.readInt();
        byteOffsetRoot = dis.readLong();
        
        tree = new BTree(degree);
        
        tree.numTreeNodes = numNodes;
        
        Parser parse = new Parser(-1, "");
        
        long foundKeyNodeGlobalPosition = 0;
        long dnaLong = 0;
        
        tree.dis = dis;
        
        tree.root = tree.diskRead(byteOffsetRoot);
        
        BufferedReader br = new BufferedReader(new FileReader(queryFileName));
        String line;
        String[] arrayGBK = bTreeFileName.split("\\.");
        
        while ((line = br.readLine()) != null) {
        	
        	//Error handling for sequences that aren't the proper length for the BTree file
        	if(Integer.parseInt(arrayGBK[4]) != line.length()){
        		System.err.println();
                System.err.printf("One of the sequences was not length " + arrayGBK[4]);
                System.err.println();
                dis.close();
                br.close();
                System.exit(1);
        	}
        	
        	String sequenceString = parse.seq2Bin(line);
        	
        	dnaLong = Long.parseLong(sequenceString,2);
        	
        	//search the BTree for the sequence
        	foundKeyNodeGlobalPosition = tree.bTreeSearch(tree.root, dnaLong);
        	
        	//if not found print 0
        	if(foundKeyNodeGlobalPosition == -1){
        		System.out.printf("%s: 0\n", line);
        	}
        	//found in root
        	else if(foundKeyNodeGlobalPosition == -2){
        		int i = 0;
                
         	   	while(dnaLong != tree.root.treeO[i].key){
         	   		i++;
	            }
         	   	System.out.printf("%s: %d\n", line, tree.root.treeO[i].frequency);
        	}
        	//else found
        	else{
        		
            	if(withCache){
            		
            		//search through cache, if it's found the cache will be updated
            		frequency = dnaCache.searchWithPosition(foundKeyNodeGlobalPosition, dnaLong);
            			
            		//wasn't found in cache, add that node to the cache
            		if(frequency == -1){
            			
            			BTreeNode updatedNode = tree.diskRead(foundKeyNodeGlobalPosition);
            			
            			int i = 0;
                        
                 	   	while(dnaLong != updatedNode.treeO[i].key){
                 	   		i++;
        	            }
            			
            			System.out.printf("%s: %d\n", line, updatedNode.treeO[i].frequency);
            			
            			dnaCache.addObject(tree.diskRead(foundKeyNodeGlobalPosition));
            		}
            		else{
            			System.out.printf("%s: %d\n", line, frequency);
            		}
            		
            		
            	}
            	else{
        		
	        		BTreeNode updatedNode = tree.diskRead(foundKeyNodeGlobalPosition);
	
	         	   	int i = 0;
	            
	         	   	while(dnaLong != updatedNode.treeO[i].key){
	         	   		i++;
		            }
	         	   	
	         	   	System.out.printf("%s: %d\n", line, updatedNode.treeO[i].frequency);
         	   	
            	}
	           
        	}
        	
        }
        
        br.close();
        dis.close();
       
    }

}
