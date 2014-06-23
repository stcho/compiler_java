package cse340;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Compiler {

  private static Vector<Token> tokens = new Vector<Token>() ;  
  
  private static String[] split(String line) {
    // 1. SPLIT THE LINE IN STRINGS (WORDS);
    	String temp = ""; 								//variable to store each line from input temporarily 
    	char[] lineCharArray = line.toCharArray(); 		//transforms line into array of chars  
    	List<String> array = new ArrayList<String>();	//ArrayList to store the split strings temporarily
    	
    	for (int i = 0; i<line.length(); i++){
    		if (!isDelimiter(lineCharArray[i]) && !isOperator(lineCharArray[i]) && !isQuotes(lineCharArray[i])){
    			temp += lineCharArray[i];
    		} else {
    			if (!temp.isEmpty()) {
    		        array.add(temp);
    		        temp = "";
    		    }
    			if (isDelimiter(lineCharArray[i])) {
    				if (lineCharArray[i] == ' ') {
    					continue;
    				}
    				array.add( String.valueOf(lineCharArray[i])); 
    			}
    			if (isOperator(lineCharArray[i])) {
    				array.add( String.valueOf(lineCharArray[i])); 
    			}
    			if (isQuotes(lineCharArray[i])) {    					
    				temp += lineCharArray[i]; 			//Adds the opening quote 
    				i++;
    				while(!isQuotes(lineCharArray[i]) && i<(line.length()-1)) {
    					if(lineCharArray[i] == '\\' && lineCharArray[i+1] == '\''){
    						temp += "\\\'";
    						i+=2;
    					}
    					if(lineCharArray[i] == '\\' && lineCharArray[i+1] == '\"'){
    						temp += "\\\"";
    						i+=2;
    					}
    					if(!isQuotes(lineCharArray[i]) && i<(line.length()-1)){
    						temp += lineCharArray[i];
    						i++;
    					}
    				}
    				temp += lineCharArray[i]; 			//Adds the closing quote 
    				array.add(temp);
    		        temp = "";
    			}
    		}
    	}
    	//Add the last temp
    	if (!temp.isEmpty()) {
	        array.add(temp);
	    }
    	
    // 2. INSERT EACH WORD IN THE ARRAY strings
    	String [] strings = new String[array.size()]; 
    	array.toArray( strings );
    	
    return strings;
  }

    private static boolean isQuotes(char q) {
    	char [] quotes = {'\"', '\''};
    	for (int x=0; x<quotes.length; x++) {
    		if (q == quotes[x]) return true;      
    	}
    	return false;
    }
    
  	private static boolean isDelimiter(char c) {
  		char [] delimiters = {':', ';', ' ', '}','{', '[',']','(',')',','};
  		for (int x=0; x<delimiters.length; x++) {
  			if (c == delimiters[x]) return true;      
	    }
	    return false;
	}
	  
	private static boolean isOperator(char o) {
		char [] operators = {'+', '-', '*','/', '%','<','>','=','!','&','|'};
		for (int x=0; x<operators.length; x++) {
			if (o == operators[x]) return true;      
		}
		return false;
	}

  public static void main(String[] args) throws FileNotFoundException, IOException {

    BufferedReader br = new BufferedReader(new FileReader(args[0]));	//Read in Input file
    Writer out = new OutputStreamWriter(new FileOutputStream(args[1])); //Write to Output file 
    int totalLexicalErrors = 0;
    int lineNumber = 1;
    int lineComparer = 1;
    try {            
      String line = br.readLine();
      while (line != null) { 
        String[] strings = split (line); //the split method
        for (String string : strings) {
          String token = Lexer_1203982307.lexer(string);
          tokens.add(new Token(string, token, lineNumber));
          if (token.equals("ERROR")) {
            totalLexicalErrors++;
          }
          // PRINT THE VECTOR<TOKENS> INTO THE OUTPUT FILE
          // out.write(token + ", " + string + ", " + lineNumber + "\n\n");
        }
        line = br.readLine();  
        lineNumber++;
      }        
    } finally {
      br.close();
      Parser p = new Parser(tokens, out);
      out.close();
    }
  }
  
}
