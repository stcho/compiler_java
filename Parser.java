package cse340;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector; 

public class Parser { 
	 Vector<Token> token;
	 int current = 0;
	 Writer out;
	 
	 public Parser (Vector<Token> tokens, Writer out_args) throws IOException { 
		 this.token = tokens;
		 this.out = out_args;
		 // predictive-recursive-descendant parser start
		 program();
	 } 
	 
	 private void newLine() throws IOException{
		 int lineWithError = token.get(current).getLine();
		 while (lineWithError == token.get(current).getLine()) {
			 checkEndOfProgram();
		 }
	 }
	 private void untilClosed(){
		 int lineWithError = token.get(current).getLine();
		 while (lineWithError == token.get(current).getLine() && !token.get(current).getWord().equals("}")) {
			 if (current != (token.size()-1)) {
				 current++; 
			 } else {
				 return;
			 }
		 }
	 }
	 private void untilOpen(){
		 int lineWithError = token.get(current).getLine();
		 while (lineWithError == token.get(current).getLine() && !token.get(current).getWord().equals("{")) {
			 if (current != (token.size()-1)) {
				 current++; 
			 } else {
				 return;
			 }
		 }
	 }
	 private void checkEndOfProgram() throws IOException{
		 if (current != (token.size()-1)) {
			 current++; 
		 } else {
			 out.write("Line " + (token.elementAt(current).getLine()+1) + ": " + "expected delimiter }" + "\n\n");
			 out.close(); System.exit(0); 
		 }
	 }
	 private void checkEndOfProgramOpen() throws IOException{
		 if (current != (token.size()-1)) {
			 current++; 
		 } else {
			 out.write("Line " + (token.elementAt(current).getLine()) + ": " + "expected delimiter {" + "\n\n");
			 out.close(); System.exit(0); 
		 }
	 }
	 private void checkEndOfProgramOther() throws IOException{
		 if (current != (token.size()-1)) {
			 current++; 
		 } else {
			 out.write("Line " + (token.elementAt(current).getLine()) + ": " + "expected delimiter :" + "\n\n");
			 out.close(); System.exit(0); 
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
				 checkEndOfProgram(); 
				 var_section();
			 } else {
				 if (token.get(current).getWord().equals("{")) {
					 return true;
				 }
				 //otherwise return false: error no semicolon
				 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter ;" + "\n\n");
				 newLine();
			 }
		 }
		 return true; //var_section recursion is done. Move on to body
	 }
	 
	 private boolean body() throws IOException {
		 if (token.get(current).getWord().equals("{")) {			 
			 checkEndOfProgram(); 
			 if (token.get(current).getWord().equals("}")) {
				 checkEndOfProgram(); 
				 return true;
			 } else {
				 stmt_list();
				 if (token.get(current).getWord().equals("}")) {
					//No Error if correct end of body
					 if (current == (token.size()-1)) {
						 out.close(); System.exit(0);
					 }
					 checkEndOfProgram(); 
					 return true;
				 } else {
					 checkEndOfProgram(); 
					 return false;
				 }
			 } 
		 } else {
			 out.write("Line " + token.elementAt(current-1).getLine() + ": " + "expected delimiter {" + "\n\n");
			 untilOpen();
			 return false; //error: no opening bracket
		 }
	 }
	 
	 private boolean id_list() throws IOException {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 checkEndOfProgram(); 
			 if (token.get(current).getWord().equals(",")) {
				 checkEndOfProgram(); 
				 id_list();
				 return true;
			 } else if (token.get(current).getWord().equals(";")){
				 checkEndOfProgram();
				 var_section();
				 return true;
			 }
			 else {
//				 if (current.)
				 out.write("Line " + token.elementAt(current-1).getLine() + ": " + "expected delimiter ;" + "\n\n");
				 untilOpen();
				 return true; // we have an id but no ',' after it
			 }
		 } else {
			 out.write("Line " + token.elementAt(current-1).getLine() + ": " + "expected identifier" + "\n\n");
			 untilOpen();
			 return false; //error: no IDENTIFIER
		 }
	 }
	 
	 private boolean stmt_list() throws IOException {
		 while (!token.get(current).getWord().equals("}") && current != (token.size()-1)) {
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
			 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected identifier" + "\n\n");
			 newLine();
			 return false;
		 }
	 }
	 
	 private boolean assign_stmt() throws IOException {
		 if (token.get(current).getWord().equals("=")) {
			 if (current != (token.size()-1)) {
				 current++; 
			 } else {
				 out.write("Line " + (token.elementAt(current).getLine()) + ": " + "expected identifier" + "\n\n");
				 out.close(); System.exit(0); 
			 }
			 if (current == (token.size()-1)) {
				 out.write("Line " + (token.elementAt(current).getLine()) + ": " + "expected delimiter ;" + "\n\n");
				 out.close(); System.exit(0); 
			 }
			 if (((token.get(current).getToken().equals("IDENTIFIER") || token.get(current).getToken().equals("INTEGER"))) && (token.get(current+1).getWord().equals("+") || token.get(current+1).getWord().equals("-") || token.get(current+1).getWord().equals("*") || token.get(current+1).getWord().equals("/"))) {
				 if (expr() == false) {
					 return false;
				 } else {
					 if (token.get(current).getWord().equals(";")) {
						 checkEndOfProgram();
						 return true;
					 } else {
						 if (token.elementAt(current-1).getLine() == token.elementAt(current-2).getLine()) {
							 out.write("Line " + token.elementAt(current-1).getLine() + ": " + "expected delimiter ;" + "\n\n");
						 } else {
							 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter ;" + "\n\n");
						 }
						 newLine();
						 return false;
					 } 
				 }
			 } else {
				 if (token.get(current+1).getWord().equals(";")) {
					 primary();
					 checkEndOfProgram(); 
					 return true;
				 } else {
					 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter ;" + "\n\n");
					 newLine();
					 return false;
				 }
			 }
		 } else {
			 out.write("Line " + token.elementAt(current-1).getLine() + ": " + "expected operator =" + "\n\n");
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
	 		out.write("Line " + token.elementAt(current-1).getLine() + ": " + "expected identifier" + "\n\n");
	 		 return false;
	 	 }
	 }
	 
	 private boolean primary() throws IOException {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 current++;
			 return true;
		 } else if (token.get(current).getToken().equals("INTEGER")) {			 
			 current++;
			 return true;
		 } else {
			 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected identifier or value" + "\n\n");
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
			 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected operator" + "\n\n");
			 return false;
		 }
	 }
	 
	 private boolean print_stmt() throws IOException {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 current++;
			 if (token.get(current).getWord().equals(";")) {
				 checkEndOfProgram();
				 return true; //successfully iterated through print_stmt
			 } else {
				 if (token.elementAt(current-1).getLine() == token.elementAt(current-2).getLine()) {
					 out.write("Line " + token.elementAt(current-1).getLine() + ": " + "expected delimiter ;" + "\n\n");
				 } else {
					 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter ;" + "\n\n");
				 }
				 untilClosed();
				 return false; //error: no semicolon
			 }
		 } else {
			 if (token.get(current).getWord().equals("}")) {
				 out.write("Line " + token.elementAt(current-1).getLine() + ": " + "expected identifier" + "\n\n");
				 return false;
			 } else {
				 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected identifier" + "\n\n");
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
				 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected operator =" + "\n\n");
				 untilOpen();
				 return false; 
			 }
		 } else {
			 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected operator" + "\n\n");
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
					 checkEndOfProgram();
					 return true; 
				 } else {
//					 out.write("WORD: " + token.get(current).getWord());
					 case_list();
					 return true;
				 }
			 } else {
				 out.write("Line " + token.elementAt(current-1).getLine() + ": " + "expected delimiter {" + "\n\n");
				 untilOpen();
				 return false;
			 }
		 } else {
			 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected identifier" + "\n\n");
			 untilOpen();
			 return false; //error: no ID
		 }
	 }
	 
	 private boolean case_list() throws IOException {
		 while (!token.get(current).getWord().equals("}")) {
			 if (token.get(current).getWord().equals("CASE")) {
				 if (current != (token.size()-1)) {
					 current++;
				 } else {
					 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected value" + "\n\n");
					 out.close(); System.exit(0);  //end of initial body
				 }
				 CASE();
				 if (token.get(current).getWord().equals("{")) {
					 body();
					 return false;
				 } else {
					 if (token.get(current).getWord().equals("DEFAULT")) {
						 checkEndOfProgramOther();
						 default_case();
						 return true;
					 } else {
						 return false;
					 }
				 }
			 } else {
				 out.write("Line " + token.elementAt(current).getLine() + ": " + "CASE expected" + "\n\n");
				 return false; //error: no CASE 
			 }
		 }
		 return true; //successfully iterated through case_list?
	 }
	 
	 private boolean CASE() throws IOException {
		if (token.get(current).getToken().equals("INTEGER")) {
			checkEndOfProgramOther();
			if (token.get(current).getWord().equals(":")) {
				checkEndOfProgramOpen();
				body();
				return true;
			} else {
				out.write("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter :" + "\n\n");
				untilOpen();
				return false; //error: expecting ":"
			}
		} else {
			out.write("Line " + token.elementAt(current).getLine() + ": " + "expected type integer" + "\n\n");
			untilOpen();
			return false;
		}
	 }
	 
	 private boolean default_case() throws IOException {
	 	 if (token.get(current).getWord().equals(":")) {
				checkEndOfProgramOpen();
				body();
				return true;
		 } else {
				out.write("Line " + token.elementAt(current).getLine() + ": " + "expected delimiter :" + "\n\n");
				untilOpen();
				if (token.get(current).getWord().equals("{")) {
					body();
					return true;
				}
				if (current != (token.size()-1)) {
					return false; //error: expecting ":" 
				} else {
					 out.close(); System.exit(0);  //end of initial body
				}
				return false;
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
			 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected type" + "\n\n");
			 newLine();
			 return false;
		 }
	 }
}
