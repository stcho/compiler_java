package cse340;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Vector; 

public class Parser { 
	 Vector<Token> token;
	 int current = 0;
	 Writer out;
	 
	 public Parser (Vector<Token> tokens, Writer out_args) throws IOException { 
		 this.token = tokens;
		 this.out = out_args;
		 //Here I need to check what line I am at and keep going if and only if it is a new line.
		 do {
			 program();
//			 System.out.println("FOOP WORD: " + token.get(current).getWord());
//			 System.out.println("FOOP LINE: " + token.get(current).getLine());
//			 System.out.println("FOOP LINE: " + token.get(current).getToken());
//			 int lineWithError = token.get(current).getLine();
//			 System.out.println("Hello my name is lineWithNumber and I am: " + lineWithError);
//			 System.out.println("Whoa I am a " + token.get(current).getLine());
//			 while (lineWithError == token.get(current).getLine()) {
//				 current++;
//			 }
//			 System.out.println("Hello my name is lineWithNumber and I am: " + lineWithError);
//			 System.out.println("Whoa I am a " + token.get(current).getLine());
		 } while (token.lastElement().getLine() != token.get(current).getLine());
		 
	 } 
	 // recursive-descendant parser
	 
	 private void newLine() {
		 int lineWithError = token.get(current).getLine();
		 while (lineWithError == token.get(current).getLine()) {
			 current++;
		 }
	 }
	 
	 private boolean program() throws FileNotFoundException, IOException{		
		 var_section();
		 body();
		 return true;
	 }
	 
	 private boolean var_section() throws IOException {
		 while(!token.get(current).getWord().equals("{")) {
			 type();
			 id_list();
			 if (token.get(current).getWord().equals(";")){
				 //if the program ends correctly with a semicolon call var_section again until "{" token for body 
				 current++;
				 var_section();
			 } else {
				 //otherwise return false: error no semicolon
				 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected delimiter ;" + "\n");
				 newLine();
//				 System.out.println(token.get(current).getWord());
//				 return false;
			 }
		 }
		 return true; //var_section recursion is done. Move on to body
	 }
	 
	 private boolean body() throws IOException {
		 if (token.get(current).getWord().equals("{")) {			 
			 current++;
			 stmt_list();
			 if (token.get(current).getWord().equals("}")) {
				 System.out.println("YOOP WORD: " + token.get(current).getWord());
				 System.out.println("YOOP LINE: " + token.get(current).getLine());
				 System.out.println("YOOP LINE: " + token.get(current).getToken());
				 newLine();
				 return true;
			 } else {
				 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected delimiter }" + "\n");
				 return false;
			 }
		 } else if (token.get(current).getWord().equals("}")) {
			 current++;
			 System.out.println("YoYoMa");
			 return false; 
		 } else {
			 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected delimiter {" + "\n");
			 return false; //error: no brackets
		 }
//		 if (token.get(current).getWord().equals("{")) {
//			 while (!token.get(current).getWord().equals("}")) {
//				 current++;
//				 stmt_list();
//			 }
//			 return true;
//		 } else {
//			 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected delimiter {" + "\n");
//			 return false; //error: no brackets
//		 }
	 }
	 private boolean id_list() throws IOException {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 current++;
			 if (token.get(current).getWord().equals(",")) {
				 current++;
				 id_list();
				 return true;
			 } else {
				 return true; // we have an id but no ',' after it
			 }
		 } else {
			 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected identifier 1" + "\n");
			 return false; //error: no IDENTIFIER
		 }
	 }
	 private boolean stmt_list() throws IOException {
		 do {
			 stmt();
		 } while (token.get(current).getToken().equals("IDENTIFIER") || token.get(current).getWord().equals("print") || token.get(current).getWord().equals("WHILE") || token.get(current).getWord().equals("IF") || token.get(current).getWord().equals("SWITCH")); 
		 return true;
	 }
	 private boolean stmt() throws IOException {
		 if (token.get(current).getWord().equals("print")) {
			 current++;
			 print_stmt();
			 return true;
		 } else if (token.get(current).getWord().equals("WHILE")) {
			 current++;
			 while_stmt();
			 return true;
		 } else if (token.get(current).getWord().equals("IF")) {
			 current++;
			 if_stmt();
			 return true;
		 } else if (token.get(current).getWord().equals("SWITCH")) {
			 current++;
			 switch_stmt();
			 return true;
		 } else if (token.get(current).getToken().equals("IDENTIFIER")) {
			 current++;			 
			 assign_stmt();
			 return true;
		 } else {
			 current++;
			 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected identifier 2" + "\n");
			 return false;
		 }
	 }
	 private boolean assign_stmt() throws IOException {
			 if (token.get(current).getWord().equals("=")) {
				 current++;
				 if (((token.get(current).getToken().equals("IDENTIFIER") || token.get(current).getToken().equals("INTEGER"))) && token.get(current+1).getToken().equals("OPERATOR")) {
					 if (token.get(current+3).getWord().equals(";")) {
						 expr();
						 current++;
						 System.out.println("WOOP WORD: " + token.get(current).getWord());
						 System.out.println("WOOP LINE: " + token.get(current).getLine());
						 System.out.println("WOOP LINE: " + token.get(current).getToken());
						 return true;
					 } else {
						 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected delimiter ;" + "\n");
						 newLine();
						 return false;
					 }
				 } else {
					 if (token.get(current+1).getWord().equals(";")) {
						 primary();
						 current++;
						 return true;
					 } else {
						 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected delimiter ;" + "\n");
						 newLine();
						 return false;
					 }
				 }
			 } else {
				 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected operator =" + "\n");
				 newLine();
				 return false; // error: no "="
			 }
	 } 
	 private boolean expr() throws IOException {
		 primary();
		 op();
		 primary();
		 return true;
	 }
	 private boolean primary() {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 current++;
			 return true;
		 } else if (token.get(current).getToken().equals("INTEGER")) {			 
			 current++;
			 return true;
		 } else {
			 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected identifier or value" + "\n");
			 return false;
		 }
	 }
	 private boolean op() throws IOException {
		 if (token.get(current).getWord().equals("+")) {
			 current++;
			 return true;
		 } else if (token.get(current).getWord().equals("-")) {
			 current++;
			 return true;
		 } else if (token.get(current).getWord().equals("*")) {
			 current++;
			 return true;
		 } else if (token.get(current).getWord().equals("/")) {
			 current++;
			 return true;
		 } else {
			 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected operator" + "\n");
			 newLine();
			 return false;
		 }
	 }
	 private boolean print_stmt() throws IOException {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 current++;
			 if (token.get(current).getWord().equals(";")) {
				 current++;
				 return true; //successfully iterated through print_stmt
			 } else {
				 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected delimiter ;" + "\n");
				 return false; //error: no semicolon
			 }
		 } else {
			 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected identifier 3" + "\n");
			 newLine();
			 return false; //error: no ID
		 }
	 }
	 private boolean while_stmt() throws IOException {
		 if (condition() == false) {
			 return false;
		 }
		 System.out.println("COOP WORD: " + token.get(current).getWord());
		 System.out.println("COOP LINE: " + token.get(current).getLine());
		 System.out.println("COOP LINE: " + token.get(current).getToken());
		 body();
		 System.out.println("NOOP WORD: " + token.get(current).getWord());
		 System.out.println("NOOP LINE: " + token.get(current).getLine());
		 System.out.println("NOOP LINE: " + token.get(current).getToken());
		 return true;
	 }
	 private boolean if_stmt() throws IOException {
		 if (condition() == false) {
			 return false;
		 }
		 body();
		 return true;
	 }
	 private boolean condition() throws IOException {
		 primary();
		 if (token.get(current).getWord().equals(">") || token.get(current).getWord().equals("<") || token.get(current).getWord().equals("!")) {
			 relop();
			 primary();
			 return true;
		 } else {
			 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected operator relop" + "\n");
			 newLine();
			 return false;
		 }
	 }
	 private boolean relop() throws IOException {
		 if (token.get(current).getWord().equals(">")) {
			 current++;
			 return true; 
		 } else if (token.get(current).getWord().equals("<")){
			 current++;
			 return true;
		 } else if (token.get(current).getWord().equals("!")){
			 current++;
			 if (token.get(current).getWord().equals("=")) {
				 current++;
				 return true;
			 } else {
				 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected operator =" + "\n");
				 newLine();
				 return false; 
			 }
		 } else {
			 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected operator" + "\n");
			 newLine();
			 return false;
		 }
	 }
	 private boolean switch_stmt() throws IOException {
		 if (token.get(current).getToken().equals("IDENTIFIER")){
			 current++;
			 if (token.get(current).getWord().equals("(")) {
				 current++;
				 case_list();
				 return true;
			 } else {
				 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected operator (" + "\n");
				 
				 return false; // error: no "("
			 }
		 } else {
			 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected identifier" + "\n");
			 
			 return false; //error: no ID
		 }
	 }
	 
	 private boolean case_list() throws IOException {
		 while (!token.get(current).getWord().equals(")")) {
			 if (token.get(current).getWord().equals("CASE")) {
				 current++;
				 CASE();
			 } else if (token.get(current).getWord().equals("DEFALUT")){
				 current++;
				 default_case();
			 } else {
				 return false; //error: not case or default
			 }
		 }
		 return true; //successfully iterated through case_list?
	 }
	 
	 private boolean CASE() throws IOException {
		if (token.get(current).getToken().equals("INTEGER")) {
			current++;
			if (token.get(current).getWord().equals(":")) {
				current++;
				body();
				return true;
			} else {
				System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected delimiter :" + "\n");
				
				return false; //error: expecting ":"
			}
		} else {
			System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected type integer" + "\n");
			
			return false; //error: expecting a number
		}
	 }
	 
	 private boolean default_case() throws IOException {
		 if (token.get(current).getWord().equals(":")) {
				current++;
				body();
				return true;
			} else {
				System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected delimiter :" + "\n");
				
				return false; //error: expecting ":"
			}
	 }
	 
	 private boolean type() throws IOException {
		 if (token.get(current).getWord().equals("integer")) {
			 current++;
			 return true;
		 } else if (token.get(current).getWord().equals("boolean")) {
			 current++;
			 return true;
		 } else {
			 //error: type error
			 System.out.println("Line " + token.elementAt(current).getLine() + ":\t" + "expected type" + "\n");
			 newLine();
			 return false;
		 }
	 }
}
