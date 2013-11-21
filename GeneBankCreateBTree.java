import java.io.*;
import java.util.*;

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
       
       gbkFileName = args[2];
       seqLength = Integer.parseInt(args[3]);
       
       Parser parse = new Parser (seqLength, gbkFileName);
       
          
       
       
    }

}
