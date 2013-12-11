import java.io.*;
import java.util.*;

public class GeneBankCreateBTree {

    public static void main(String[] args) throws IOException {
    
       boolean withCache = false;
       //this is the default degree (optimal based on our implementation)
       int degree = 127;
       int seqLength = 0;
       int debugLevel = 0;
       String gbkFileName = "";
       Cache dnaCache = null;
       BTree tree =  null;
       BTreeNode deletedNode = null;
       
       //Error handling and setting values from user input:
       
       if(args.length < 4 || args.length > 6){
           System.err.println();
           System.err.println("You provided too few or too many arguments.");
           System.err.println("Arguments have the following form: <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
           System.err.println();
           System.exit(1);
       }
    
       try{
       
           if(args[0].equals("0")){
           }
           else if(args[0].equals("1")){
               withCache = true;
               if(args.length < 5){
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
       
       try{
           
           if(args[1].equals("0")){
                   //default to optimal degree, do nothing since it's already set at optimal
           }
           else if(Integer.parseInt(args[1]) > 0){
                   degree = Integer.parseInt(args[1]);
           }
           else{
               throw new RuntimeException("Error: Invalid second argument. Must be of the form <degree>, where the degree is greater than 0.");
           }
           
       }
       catch(RuntimeException e){
       
           System.err.println();
           System.err.println("RuntimeException: " + e.getMessage());
           System.err.println();
           System.exit(1);
           
       }
       
       //this error will be handled later on if the filename is invalid
       gbkFileName = args[2];
       
       try{
           
           if(Integer.parseInt(args[3]) > 0 && Integer.parseInt(args[3]) <= 31){
                   seqLength = Integer.parseInt(args[3]);
           }
           else{
               throw new RuntimeException("Error: Invalid fourth argument. Must be of the form <sequence length>, where the sequence length is greater than 0 and less than or equal to 31.");
           }
           
       }
       catch(RuntimeException e){
       
           System.err.println();
           System.err.println("RuntimeException: " + e.getMessage());
           System.err.println();
           System.exit(1);
           
       }
       
       //if a cache was specified, check that the fifth argument is in the correct form
       if(args[0].equals("1")){
               
               try{
               if(Integer.parseInt(args[4]) > 0){
                       dnaCache = new Cache(Integer.parseInt(args[4]));
               }
               else{
                   throw new RuntimeException("Error: Invalid fifth argument. Must be of the form <cache size>, where the cache size is greater than 0.");
               }
               
           }
           catch(RuntimeException e){
           
               System.err.println();
               System.err.println("RuntimeException: " + e.getMessage());
               System.err.println();
               System.exit(1);
               
           }
               
               if(args.length == 6){
                       try{
                       
                       if(args[5].equals("0")){
                               //do nothing since debug level is already set to 0 by default
                       }
                       else if(args[5].equals("1")){
                               debugLevel = 1;
                       }
                       else{
                           throw new RuntimeException("Error: Invalid sixth argument. Must be of the form <debug level>, where the debug level is 0  or 1.");
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
       else if(args.length == 5){
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
               
                   System.err.println();
                   System.err.println("RuntimeException: " + e.getMessage());
                   System.err.println();
                   System.exit(1);
                   
               }
       }
       
       tree = new BTree(degree);
        
       //the -2 is important to identify the root node
       tree.root = new BTreeNode(-2, true, 0, -3, tree.t);
       
       try{
    	   
    	   		
              
    	   tree.dis = new RandomAccessFile(gbkFileName +  ".btree.data." + seqLength + "." + degree, "rw");
               
               
               
       }
       catch(FileNotFoundException e){
               
                       System.err.println();
                       System.err.println("RuntimeException: " + e.getMessage());
                       System.err.println();
                       System.exit(1);
               
       }
       
       //placeholders for BTree metadata at beginning of file (number of tree nodes, degree, and the byte offset of the root)
       tree.dis.writeInt(tree.numTreeNodes);
       tree.dis.writeInt(tree.t);
       tree.dis.writeLong(tree.byteOffsetRoot);
       
       //write a buffer byte
       //tree.dis.writeBoolean(false);
       
       Parser parse = new Parser (seqLength, gbkFileName);
       
       //tree.fullDNASequence = parse.entireDNASequence;
       
       tree.sequenceLength = seqLength;
       
       long binarySequence = parse.nextBinSequence();
       long foundKeyNodeGlobalPosition = 0;
       
       //insert or update all subsequences from the gbk file until the end of the file is reached
       
       int sequenceNumber = 0;
       /*
       if(withCache){
    	   
    	   BTreeNode deletedCacheNode = null;
    	   
    	   while(binarySequence != -1){
        	   
    		   System.out.println("Sequence number: " + sequenceNumber);
   	   	   	   sequenceNumber++;
              
   	   	   	   //this will add the 
   	   	   	   deletedCacheNode = dnaCache.getObject(binarySequence);
   	   	   	   
   	   	   	   if(deletedCacheNode != null){
   	   	   		   for(int i = 0; i < deletedCacheNode.numTreeObjects; i++){
   	   	   			   foundKeyNodeGlobalPosition = tree.bTreeSearch(tree.root, deletedCacheNode.treeO[i].key);
   	   	   			   
   	   	   		   }
   	   	   	   }
              
   	   	   	   binarySequence = parse.nextBinSequence();
    	   }
       }
       */
       //else{
       		boolean fourInserted = false;
       		int countOfAllT = 0;
       
    	   while(binarySequence != -1){
    		   
    		   if(binarySequence == 16383){
    			   countOfAllT++;
    		   }
    		   
    		   //System.out.printf("The number of tree nodes is: %d\n", tree.numTreeNodes);
        	   
   	   		//System.out.println("Sequence number: " + sequenceNumber);
   	   		sequenceNumber++;
              
              foundKeyNodeGlobalPosition = tree.bTreeSearch(tree.root, binarySequence);
              
              //if the key wasn't found, insert it into the BTree
              if(foundKeyNodeGlobalPosition == -1){
            	  
            	  if(fourInserted && binarySequence == 4){
            		  System.out.println("Four was inserted but wasn't found");
            	  }
            	  
            	  //when using cache, first remove all nodes from cache and update the tree with those nodes
            	  if(withCache){
            		  
	            		//remove each item from the cache and write it to the disk
	   	       		   for(int k = dnaCache.list.size()-1; k > -1; k--){
	   	       			   System.out.println(k);
	   	       			   deletedNode = dnaCache.removeObject(k);
	   	       			   tree.diskWrite(deletedNode.globalOffset, deletedNode);
	   	       		   }
            	  }
	            	
                   tree.bTreeInsert(binarySequence);
              }
              //key was found in the root
              else if(foundKeyNodeGlobalPosition == -2){
           	   int i = 0;
                  
	               while(binarySequence != tree.root.treeO[i].key){
	                       i++;
	               }
	               
	               tree.root.treeO[i].frequency++;
              }
              //if the key was found, update the node with an increased frequency for that key and write the updated node to disk
              else{
           	   
           	   	   BTreeNode updatedNode = tree.diskRead(foundKeyNodeGlobalPosition);
           	   
           	   	   int i = 0;
              
	               while(binarySequence != updatedNode.treeO[i].key){
	                       i++;
	               }
	               
	               //this is where the cache comes in, instead of writing to disk with the updated node, write to cache, then write to disk when node is bumped out, or program finished, write all cache nodes
	               //each cache write should search for the node, which each have a unique globalOffset value, remove that node from the cache, and always move the written node to the front of the cache
	               //if an item is deleted from the cache, it should do a diskWrite for that node, similar to the line below
	               if(withCache){            	   
	            	   deletedNode = dnaCache.getObject(updatedNode, i);
	            	   
	            	   //if a node was bumped out of the cache, write it to the disk
	            	   if(deletedNode != null){
	            		   tree.diskWrite(deletedNode.globalOffset, deletedNode);
	            	   }
	               }
	               else{
	            	   updatedNode.treeO[i].frequency++;
	            	   tree.diskWrite(updatedNode.globalOffset, updatedNode);
	               }
              
              }
              
              binarySequence = parse.nextBinSequence();
              
    	   }
    	   
    	   
    	 //remove each item from the cache and write it to the disk
    	   if(withCache){
    		   for(int k = dnaCache.list.size()-1; k > -1; k--){
    			   deletedNode = dnaCache.removeObject(k);
    			   tree.diskWrite(deletedNode.globalOffset, deletedNode);
    		   }
    	   }

       
       tree.byteOffsetRoot = tree.dis.length();
       
       //write the root node to disk after building the BTree
       tree.diskWrite(-1, tree.root);
       
       //write the metadata to disk
       tree.dis.seek(0);
       tree.dis.writeInt(tree.numTreeNodes);
       tree.dis.writeInt(tree.t);
       tree.dis.writeLong(tree.byteOffsetRoot);
       
       //write to dump file inorder traversal starting at root
       if(debugLevel == 1){
    	   tree.bw.write("frequency:  sequence");
    	   tree.bw.newLine();
    	   tree.inOrderPrintToDump(tree.diskRead(tree.byteOffsetRoot));
       }
       
       tree.bw.close();
       tree.zw.close();
       tree.dis.close();
       
    }

}
