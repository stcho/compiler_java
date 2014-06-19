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
	 
	 public Parser (Vector<Token> tokens, OutputStreamWriter out_args) throws IOException { 
		 this.token = tokens;
		 this.out = out_args;
		 program(); 
	 } 
	 // implement here your recursive-descendent parser
	 private boolean program() throws FileNotFoundException, IOException{
		 out.write("Line " + token.elementAt(current).getLine() + ":\t" + "expected");
		 out.close();
		 
		 var_section();
		 body();
		 return true;
	 }
	 private boolean var_section() {
		 while(!token.get(current).getWord().equals("{")) {
			 type();
			 id_list();
			 if (token.get(current).getWord().equals(";")){
				 //if the program ends correctly with a semicolon call var_section again until "{" for body 
				 current++;
				 var_section();
			 } else {
				 //otherwise return false: error no semicolon?				 
				 return false;
			 }
		 }
		 return true; //var_section recursion is done and we move on to body
	 }
	 private boolean body() {
		 if (token.get(current).getWord().equals("{")) {
			 current++;
			 stmt_list();
			 return true;
		 } else if (token.get(current).getWord().equals("}")) {
			 current++;
			 return false; 
		 } else {
			 return false; //error: no brackets
		 }
	 }
	 private boolean id_list() {
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
			 return false; //error: no IDENTIFIER
		 }
	 }
	 private boolean stmt_list() {
		 while(!token.get(current).getWord().equals("}")) {
			 stmt(); 
		 }
		 return true;
	 }
	 private boolean stmt() {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 current++;
			 assign_stmt();
			 return true;
		 } else if (token.get(current).getWord().equals("print")) {
			 current++;
			 print_stmt();
			 return true;
		 } else if (token.get(current).getWord().equals("while")) {
			 current++;
			 while_stmt();
			 return true;
		 } else if (token.get(current).getWord().equals("if")) {
			 current++;
			 if_stmt();
			 return true;
		 } else if (token.get(current).getWord().equals("switch")) {
			 current++;
			 switch_stmt();
			 return true;
		 } else {
			 return false;
		 }
	 }
	 private boolean assign_stmt() {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 if (token.get(current).getWord().equals("=")) {
				 current++;
				 if ((token.get(current).getToken().equals("IDENTIFIER") || token.get(current).getToken().equals("INTEGER")) && token.get(current+1).getToken().equals("OPERATOR")) {
					 current = current + 2;
					 expr();
				 } else {
					 primary();
				 }
			 } else {
				 return false; // error: no "="
			 }
		 } else {
			 return false; // error: no ID
		 }
		 return true;
	 }
	 private boolean expr() {
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
			 return false;
		 }
	 }
	 private boolean op() {
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
			 return false;
		 }
	 }
	 private boolean print_stmt() {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 current++;
			 if (token.get(current).getWord().equals(";")) {
				 current++;
				 return true; //successfully iteraed through print_stmt
			 } else {
				 return false; //error: no semicolon
			 }
		 } else {
			 return false; //error: no ID
		 }
	 }
	 private boolean while_stmt() {
		 condition();
		 body();
		 return true;
	 }
	 private boolean if_stmt() {
		 condition();
		 body();
		 return true;
	 }
	 private boolean condition() {
		 primary();
		 relop();
		 primary();
		 return true;
	 }
	 private boolean relop() {
		 if (token.get(current).getWord().equals(">")) {
			 return true; 
		 } else if (token.get(current).getWord().equals("<")){
			 return true;
		 } else if (token.get(current).getWord().equals("!=")){
			 return true;
		 } else {
			 return false;
		 }
	 }
	 private boolean switch_stmt() {
		 if (token.get(current).getToken().equals("IDENTIFIER")){
			 current++;
			 if (token.get(current).getWord().equals("(")) {
				 current++;
				 case_list();
				 return true;
			 } else {
				 return false; // error: no "("
			 }
		 } else {
			 return false; //error: no ID
		 }
	 }
	 
	 private boolean case_list() {
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
	 
	 private boolean CASE() {
		if (token.get(current).getToken().equals("INTEGER")) {
			current++;
			if (token.get(current).getWord().equals(":")) {
				current++;
				body();
				return true;
			} else {
				return false; //error: expecting ":"
			}
		} else {
			return false; //error: expecting a number
		}
	 }
	 
	 private boolean default_case() {
		 if (token.get(current).getWord().equals(":")) {
				current++;
				body();
				return true;
			} else {
				return false; //error: expecting ":"
			}
	 }
	 
	 private boolean type() {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 current++;
			 return true;
		 } else if (token.get(current).getToken().equals("INTEGER")) {
			 current++;
			 return true;
		 } else {
			 return false;
		 }
	 }
}
