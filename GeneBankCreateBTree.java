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
       Cache dnaCache;
       BTree tree =  null;
       
       //Error handling and setting values from user input:
       
       if(args.length < 4 || args.length > 6){
    	   System.out.println();
           System.out.println("You provided too few or too many arguments.");
           System.out.println("Arguments have the following form: <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
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
       
       try{
           
           if(args[1].equals("0")){
        	   //default to optimal degree, do nothing since it's already set at optimal
           }
           else if(Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) <= 127){
        	   degree = Integer.parseInt(args[1]);
           }
           else{
               throw new RuntimeException("Error: Invalid second argument. Must be of the form <degree>, where the degree is greater than 0 and less than or equal to 127.");
           }
           
       }
       catch(RuntimeException e){
       
           System.out.println();
           System.out.println("RuntimeException: " + e.getMessage());
           System.out.println();
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
       
           System.out.println();
           System.out.println("RuntimeException: " + e.getMessage());
           System.out.println();
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
           
               System.out.println();
               System.out.println("RuntimeException: " + e.getMessage());
               System.out.println();
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
    	       
    	           System.out.println();
    	           System.out.println("RuntimeException: " + e.getMessage());
    	           System.out.println();
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
	       
	           System.out.println();
	           System.out.println("RuntimeException: " + e.getMessage());
	           System.out.println();
	           System.exit(1);
	           
	       }
       }
       
       tree = new BTree(degree);
       
       tree.childrenInitializer = new int[2*tree.t];
       tree.treeObjectInitializer = new TreeObject[(2*tree.t)-1];
	
		//initialize the child array and tree object array to a constant size in order to read correctly
		for(int i = 0; i < tree.treeObjectInitializer.length; i++){
			tree.childrenInitializer[i] = 0;
			tree.treeObjectInitializer[i] = new TreeObject(0,0);
		}
		//initialize the last item in the child array that wasn't covered by the for loop above
		tree.childrenInitializer[tree.childrenInitializer.length-1] = 0;
	
		tree.root = new BTreeNode(0, true, 0, 0, tree.childrenInitializer, tree.treeObjectInitializer);
       
       try{
              
    	   tree.dis = new RandomAccessFile("gbkfile.bin", "rw");
               
       }
       catch(FileNotFoundException e){
               
       		System.out.println();
       		System.out.println("RuntimeException: " + e.getMessage());
       		System.out.println();
       		System.exit(1);
               
       }
       
       //placeholders for BTree metadata at beginning of file (number of tree nodes, degree, and the byte offset of the root)
       tree.dis.writeInt(tree.numTreeNodes);
       tree.dis.writeInt(tree.t);
       tree.dis.writeLong(tree.byteOffsetRoot);
       
       Parser parse = new Parser (seqLength, gbkFileName);
       
       long binarySequence = parse.nextBinSequence();
       long foundKeyNodeGlobalPosition = 0;
       
       //insert or update all subsequences from the gbk file until the end of the file is reached
       while(binarySequence != -1){
    	   
    	   foundKeyNodeGlobalPosition = tree.bTreeSearch(tree.root, binarySequence);
    	   
    	   //if the key wasn't found, insert it into the BTree
    	   if(foundKeyNodeGlobalPosition == -1){
    		   tree.bTreeInsert(binarySequence);
    	   }
    	   //if the key was found, update the node with an increased frequency for that key and write the updated node to disk
    	   else{
    		   BTreeNode updatedNode = tree.diskRead(foundKeyNodeGlobalPosition);
    		   
    		   int i = 0;
               
               while(binarySequence != updatedNode.treeO[i].key){
                       i++;
               }
               updatedNode.treeO[i].frequency++;
               
               tree.diskWrite(updatedNode.globalOffset, updatedNode);
               
    	   }
    	   
    	   binarySequence = parse.nextBinSequence();
       }
       
       
       
    }

}
