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
		// recursive-descendant parser
		 program();
	 } 
	 
	 private void newLine() {
		 int lineWithError = token.get(current).getLine();
		 while (lineWithError == token.get(current).getLine()) {
			 current++;
		 }
	 }
	 private void untilClosed(){
		 int lineWithError = token.get(current).getLine();
		 while (lineWithError == token.get(current).getLine() && !token.get(current).getWord().equals("}")) {
			 current++;
		 }
	 }
	 private void untilOpen(){
		 int lineWithError = token.get(current).getLine();
		 while (lineWithError == token.get(current).getLine() && !token.get(current).getWord().equals("{")) {
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
				 if (token.get(current).getWord().equals("{")) {
					 return true;
				 }
				 //otherwise return false: error no semicolon
				 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter ;" + "\n");
				 newLine();
			 }
		 }
		 return true; //var_section recursion is done. Move on to body
	 }
	 
	 private boolean body() throws IOException {
		 if (token.get(current).getWord().equals("{")) {			 
			 current++;
			 if (token.get(current).getWord().equals("}")) {
				 current++;
				 return true;
			 } else {
				 stmt_list();
				 if (token.get(current).getWord().equals("}")) {
					 current++;
					 return true;
				 } else {
					 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter }" + "\n");
					 return false;
				 }
			 } 
		 } else {
			 System.out.println("Line " + token.elementAt(current-1).getLine() + ": " + "expected delimiter {" + "\n");
			 untilOpen();
			 return false; //error: no opening bracket
		 }
	 }
	 
	 private boolean id_list() throws IOException {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 current++;
			 if (token.get(current).getWord().equals(",")) {
				 current++;
				 id_list();
				 return true;
			 } else {
				 System.out.println("Line " + token.elementAt(current-1).getLine() + ": " + "expected delimiter ;" + "\n");
				 untilOpen();
				 return true; // we have an id but no ',' after it
			 }
		 } else {
			 System.out.println("Line " + token.elementAt(current-1).getLine() + ": " + "expected identifier" + "\n");
			 untilOpen();
			 return false; //error: no IDENTIFIER
		 }
	 }
	 
	 private boolean stmt_list() throws IOException {
		 while (!token.get(current).getWord().equals("}") && current != token.size()-1) {
			 stmt();
		 }
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
			 if (token.get(current).getWord().equals("{")){
				 current++;
				 case_list();
				 if (token.get(current).getWord().equals("}")) {
					 current++;
					 return true; 
				 } else {
					 case_list();
					 return true;
				 }
			 } else {
				 return true;
			 }
		 } else if (token.get(current).getToken().equals("IDENTIFIER")) {
			 current++;
			 assign_stmt();
			 return true;
		 } else {
			 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected identifier" + "\n");
			 newLine();
			 return false;
		 }
	 }
	 
	 private boolean assign_stmt() throws IOException {
		 if (token.get(current).getWord().equals("=")) {
			 current++;
			 if (((token.get(current).getToken().equals("IDENTIFIER") || token.get(current).getToken().equals("INTEGER"))) && (token.get(current+1).getWord().equals("+") || token.get(current+1).getWord().equals("-") || token.get(current+1).getWord().equals("*") || token.get(current+1).getWord().equals("/"))) {
				 if (expr() == false) {
					 return false;
				 } else {
					 if (token.get(current).getWord().equals(";")) {
						 current++;
						 return true;
					 } else {
						 if (token.elementAt(current-1).getLine() == token.elementAt(current-2).getLine()) {
							 System.out.println("Line " + token.elementAt(current-1).getLine() + ": " + "expected delimiter ;" + "\n");
						 } else {
							 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter ;" + "\n");
						 }
						 newLine();
						 return false;
					 } 
				 }
			 } else {
				 if (token.get(current+1).getWord().equals(";")) {
					 primary();
					 current++;
					 return true;
				 } else {
					 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter ;" + "\n");
					 newLine();
					 return false;
				 }
			 }
		 } else {
			 System.out.println("Line " + token.elementAt(current-1).getLine() + ": " + "expected operator =" + "\n");
			 newLine();
			 return false;
		 }
	 }
	 
	 private boolean expr() throws IOException {
		 primary();
		 op();
		 if (token.get(current).getLine() == token.get(current-1).getLine()) {
			 primary();
		 	 return true;
	 	 } else {
	 		System.out.println("Line " + token.elementAt(current-1).getLine() + ": " + "expected identifier" + "\n");
	 		 return false;
	 	 }
	 }
	 
	 private boolean primary() {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 current++;
			 return true;
		 } else if (token.get(current).getToken().equals("INTEGER")) {			 
			 current++;
			 return true;
		 } else {
			 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected identifier or value" + "\n");
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
			 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected operator" + "\n");
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
				 if (token.elementAt(current-1).getLine() == token.elementAt(current-2).getLine()) {
					 System.out.println("Line " + token.elementAt(current-1).getLine() + ": " + "expected delimiter ;" + "\n");
				 } else {
					 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter ;" + "\n");
				 }
				 untilClosed();
				 return false; //error: no semicolon
			 }
		 } else {
			 if (token.get(current).getWord().equals("}")) {
				 System.out.println("Line " + token.elementAt(current-1).getLine() + ": " + "expected identifier" + "\n");
				 return false;
			 } else {
				 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected identifier" + "\n");
				 untilClosed();
			 	return false; //error: no ID
			 }	
		 }
	 }
	 
	 private boolean while_stmt() throws IOException {
		 if (condition() == false) {
			 untilOpen(); 
		 }
		 body();
		 if (token.get(current).getWord().equals("{")) {
			 body();
		 }
		 return true;
	 }
	 
	 private boolean if_stmt() throws IOException {
		 if (condition() == false) {
			 untilOpen(); 
		 }
		 body();
		 if (token.get(current).getWord().equals("{")) {
			 body();
		 }
		 return true;
	 }
	 
	 private boolean condition() throws IOException {
		 if (primary() == false) {
			 return false;
		 } else {
			 if (relop() == false) {
				 return false;
			 } else {
				 if (primary() == false) {
					 return false; 
				 } else {
					 return true;
				 }
			 }
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
				 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected operator =" + "\n");
				 untilOpen();
				 return false; 
			 }
		 } else {
			 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected operator" + "\n");
			 untilOpen();
			 return false;
		 }
	 }
	 
	 private boolean switch_stmt() throws IOException {
		 if (token.get(current).getToken().equals("IDENTIFIER")){
			 current++;
			 if (token.get(current).getWord().equals("{")) {
				 current++;
				 case_list();
				 if (token.get(current).getWord().equals("}")) {
					 current++;
					 return true; 
				 } else {
					 case_list();
					 return true;
				 }
			 } else {
				 System.out.println("Line " + token.elementAt(current-1).getLine() + ": " + "expected delimiter {" + "\n");
				 untilOpen();
				 return false;
			 }
		 } else {
			 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected identifier" + "\n");
			 untilOpen();
			 return false; //error: no ID
		 }
	 }
	 
	 private boolean case_list() throws IOException {
		 while (!token.get(current).getWord().equals("}")) {
			 if (token.get(current).getWord().equals("CASE")) {
				 current++;
				 CASE();
				 if (token.get(current).getWord().equals("{")) {
					 body();
					 return false;
				 } else {
					 if (token.get(current).getWord().equals("DEFAULT")) {
						 current++;
						 default_case();
						 return true;
					 } else {
						 return false;
					 }
				 }
			 } else {
				 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "CASE expected" + "\n");
				 return false; //error: no CASE 
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
				System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter :" + "\n");
				untilOpen();
				return false; //error: expecting ":"
			}
		} else {
			System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected type integer" + "\n");
			untilOpen();
			return false;
		}
	 }
	 
	 private boolean default_case() throws IOException {
	 	 if (token.get(current).getWord().equals(":")) {
				current++;
				body();
				return true;
		 } else {
				System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter :" + "\n");
				untilOpen();
				if (token.get(current).getWord().equals("{")) {
					body();
					return true;
				}
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
			 System.out.println("Line " + token.elementAt(current).getLine() + ": " + "expected type" + "\n");
			 newLine();
			 return false;
		 }
	 }
}
