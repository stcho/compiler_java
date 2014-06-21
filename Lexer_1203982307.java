package cse340;

import java.util.regex.Matcher;

public class Lexer_1203982307 {
 //Delete for
  private final static String[] keywords = {"if", "else", "while", "switch", "case", "return", "int", "float", "void", "char", "string", "boolean", "true", "false", "print"};
 
  public static String lexer(String string) {
    // 4. IMPLEMENT THE LEXICAL RULES HERE
	  String octRegEx = "0[0-7]+";
	  String intRegEx = "0|([1-9][\\d]*)";
	  String floatRegEx = "([0-9]+[.][0-9]*)|([0-9]*[.][0-9]+)|([0-9]+[.][0-9]*(e|E)[0-9]+)|([0-9]*[.][0-9]+(e|E)[0-9]+)";
	  String hexRegEx = "[0][x](\\d|[a-f]|[A-F])+";
	  String binRegEx = "[0][b]([0]|[1])+";
	  String stringRegEx = "[\\\"](.)*[\\\"]"; 
	  String charRegEx = "[\\\']((.)|([\\\\][\\\']))[\\\']";
	  String idRegEx = "(([A-Z]|[a-z])+([A-Z]|[a-z]|[0-9])*)|[_$]([A-Z]|[a-z]|[0-9])+";
	  
    // 5. RETURN THE TOKEN FOR THE string received as parameter; 
	  
	  if (string.length() == 1 && (isOperator(string.charAt(0))|isDelimiter(string.charAt(0)))) {
		  if (isOperator(string.charAt(0))) {
			  return "OPERATOR";
		  }
		  if (isDelimiter(string.charAt(0))) {
			  return "DELIMITER";
		  } 
	  } else if (string.matches(octRegEx)) {
		  return "OCTAL";
	  } else if (string.matches(intRegEx)) {
		  return "INTEGER";
	  } else if (string.matches(floatRegEx)) {
		  return "FLOAT";
	  } else if (string.matches(hexRegEx)) {
		  return "HEXADECIMAL";
	  } else if (string.matches(binRegEx)) {
		  return "BINARY";
	  } else if (string.matches(stringRegEx)) {
		  return "STRING";
	  } else if (string.matches(charRegEx)) {
		  return "CHARACTER";
	  } else if (string.matches(idRegEx)) {
		  // Each time that you detect and ID, search it in the array "keywords". If it exist then it is a keyword, else it is an ID
		  for(int i = 0; i<keywords.length; i++) {
			  if (string.equals(keywords[i])) {
				  return "KEYWORD";
			  }
		  }
		  return "IDENTIFIER";
	  } 
	  
    // 5. RETURN "ERROR" if the string is not a word
    return "ERROR";
  }

  private static boolean isDelimiter(char c) {
     char [] delimiters = {';', ' ', '}','{', '[',']','(',')',','};
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

}
