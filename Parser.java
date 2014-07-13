package cse340;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector; 

public class Parser { 
	 Vector<Token> token;
	 int current = 0;
	 Writer out;
	 String type;
	 int relop;
	 Semantic semantic = new Semantic();
	 int operator; 
	 int pc;
	 int newLabel = 1;
	 int stNewLabel = 1;
	 String assemblyCode = new String();
	 String currentLabel;
	 String printLabels = new String();
	 Stack numTemp = new Stack();
	 ArrayList labelTemp = new ArrayList();
	 
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
			 pc++; addString("OPR 1, 0"); pc++; addString("OPR 0, 0"); addLabel(); out.write(printLabels + "@" + assemblyCode); out.close(); 
			 System.exit(0); 
		 }
	 }
	 private void checkEndOfProgramOpen() throws IOException{
		 if (current != (token.size()-1)) {
			 current++; 
		 } else {
			 out.write("Line " + (token.elementAt(current).getLine()) + ": " + "expected delimiter {" + "\n\n");
			 pc++; addString("OPR 1, 0"); pc++; addString("OPR 0, 0"); addLabel(); out.write(printLabels + "@" + assemblyCode); out.close(); 
			 System.exit(0); 
		 }
	 }
	 private void checkEndOfProgramOther() throws IOException{
		 if (current != (token.size()-1)) {
			 current++; 
		 } else {
			 out.write("Line " + (token.elementAt(current).getLine()) + ": " + "expected delimiter :" + "\n\n");
			 pc++; addString("OPR 1, 0"); pc++; addString("OPR 0, 0"); addLabel(); out.write(printLabels + "@" + assemblyCode); out.close(); 
			 System.exit(0); 
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
						 pc++; addString("OPR 1, 0"); pc++; addString("OPR 0, 0"); addLabel(); out.write(printLabels + "@" + assemblyCode); out.close(); 
						 System.exit(0);
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
			 //SEMANTIC ERROR HANDLING
			 if (!semantic.st.containsKey(token.get(current).getWord())) {
				 semantic.insertSymbol(token.get(current).getWord(), type, "global");
				 out.write(token.get(current).getWord() + ", " + type + "\n");
			 } else {
				 //semantic error
				 out.write("Line " + token.elementAt(current).getLine() + ": " + "duplicate variable " + token.elementAt(current).getWord() + "\n\n");
			 }
			 //IDs inserted into symbol table 
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
			 //Semantic Error Check
			 if (!semantic.st.containsKey(token.get(current).getWord())) {
				//semantic error: Line <line>: variable <variable> not found
				 out.write("Line " + token.elementAt(current).getLine() + ": " + "variable " + token.elementAt(current).getWord() + " not found \n\n");
			 } else {
				 semantic.registry.push(semantic.st.get(token.get(current).getWord()).elementAt(0).toString());
			 }
			 String var = token.get(current).getWord();
			 
			 current++;
			 assign_stmt();
			 
			 //Semantic error check: pop 2, use cube, if(!ok) error
			 String temp2 = new String();
			 String temp1 = new String();
			 if (!semantic.registry.empty()) {
				 temp2 = semantic.registry.pop();
			 }			 
			 if (!semantic.registry.empty()) {
				 temp1 = semantic.registry.pop();
			 }
			 if (temp1.equals(temp2) && !temp1.equals("error")) {
				//Generating intermediate code output: STO
				 pc++; addString("STO " + var + ", 0");
				 return true;
			 } else {
			 //semantic error Line <line>: type mismatch
				 if(temp2.equals("") || temp1.equals("")) {
					 return false;
				 } else {
					 out.write("Line " + token.elementAt(current-1).getLine() + ": "+ "type mismatch \n\n");
					 return false; 
				 }
			 }
				 
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
				 pc++; addString("OPR 1, 0"); pc++; addString("OPR 0, 0"); addLabel(); out.write(printLabels + "@" + assemblyCode); out.close(); 
				 System.exit(0); 
			 }
			 if (current == (token.size()-1)) {
				 out.write("Line " + (token.elementAt(current).getLine()) + ": " + "expected delimiter ;" + "\n\n");
				 pc++; addString("OPR 1, 0"); pc++; addString("OPR 0, 0"); addLabel(); out.write(printLabels + "@" + assemblyCode); out.close(); 
				 System.exit(0); 
			 }
			 
			 if(token.get(current).getWord().equals("true") || token.get(current).getWord().equals("false")) {
					//Push into stack 
					 semantic.registry.push("boolean");
					//Generating intermediate code output: LOD
					 pc++; addString("LIT " + token.get(current).getWord() + ", 0");
					 current++;
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
		 int op;
		 primary();
		 op = op();
		 if (token.get(current).getLine() == token.get(current-1).getLine()) {
			 primary();
			 //Semantic push to stack
			 String temp2 = new String();
			 String temp1 = new String();
			 if (!semantic.registry.empty()) {
				 temp2 = semantic.registry.pop();
			 }			 
			 if (!semantic.registry.empty()) {
				 temp1 = semantic.registry.pop();
			 }
			 if((temp2.equals("") || temp1.equals("")) && !temp1.equals(temp2) && !temp1.equals("error")) {
				 out.write("Line " + token.elementAt(current-1).getLine() + ": "+ "type mismatch \n\n");
			 } else {
				 String result = semantic.cube[semantic.checkType(temp1)][op][semantic.checkType(temp2)];
				 semantic.registry.push(result); 
			 }
			//Generating intermediate code output: OPR
			 pc++; addString("OPR " + operator + ", 0");
		 	 return true;
	 	 } else {
	 		out.write("Line " + token.elementAt(current-1).getLine() + ": " + "expected identifier" + "\n\n");
	 		 return false;
	 	 }
	 }
	 
	 private boolean primary() throws IOException {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			 //SEMANTIC ERROR CHECK
			 if (!semantic.st.containsKey(token.get(current).getWord())) {
				//semantic error: Line <line>: variable <variable> not found
				 out.write("Line " + token.elementAt(current).getLine() + ": " + "variable " + token.elementAt(current).getWord() + " not found \n\n");
			 } else {
				 semantic.registry.push(semantic.st.get(token.get(current).getWord()).elementAt(0).toString());
				//Generating intermediate code output: LOD
				 pc++; addString("LOD " + token.get(current).getWord() + ", 0");
			 }
			 //
			 current++;
			 return true;
		 } else if (token.get(current).getToken().equals("INTEGER")) {			 
			 //Push into stack 
			 semantic.registry.push("integer");
			//Generating intermediate code output: LOD
			 pc++; addString("LIT " + token.get(current).getWord() + ", 0");
			 current++;
			 return true;
		 } else {
			 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected identifier or value" + "\n\n");
			 return false;
		 }
	 }
	 
	 private int op() throws IOException {
		 if (token.get(current).getWord().equals("+")) {
			 operator = 2;
			 current++;
			 return 0;
		 } else if (token.get(current).getWord().equals("-")) {
			 operator = 3;
			 current++;
			 return 0;
		 } else if (token.get(current).getWord().equals("*")) {
			 operator = 4;
			 current++;
			 return 0;
		 } else if (token.get(current).getWord().equals("/")) {
			 operator = 5;
			 current++;
			 return 0;
		 } else {
			 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected operator" + "\n\n");
			 return 2;
		 }
	 }
	 
	 private boolean print_stmt() throws IOException {
		 if (token.get(current).getToken().equals("IDENTIFIER")) {
			//SEMANTIC ERROR CHECK
			 if (!semantic.st.containsKey(token.get(current).getWord())) {
				//semantic error: Line <line>: variable <variable> not found
				 out.write("Line " + token.elementAt(current).getLine() + ": " + "variable " + token.elementAt(current).getWord() + " not found \n\n");
			 } else {
				 semantic.registry.push(semantic.st.get(token.get(current).getWord()).elementAt(0).toString());
				//Generating intermediate code output: LOD
				 pc++; addString("LOD " + token.get(current).getWord() + ", 0");
			 }
			 //
			 current++;
			 if (token.get(current).getWord().equals(";")) {
				//Generating intermediate code output: OPR
				 pc++; addString("OPR 21, 0");
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
		//Semantic check pop 1, check if bool otherwise Line <line>: boolean expression expected
		 String temp1 = semantic.registry.pop();
		 if (temp1.equals("boolean")) {} else {
			//semantic error Line <line>: type mismatch
			 out.write("Line " + token.elementAt(current-1).getLine() + ": "+ "boolean expression expected \n\n");
		 } 
		 //Generating intermediate code output: JMC
		 pc++; addString("JMC " + a_new_label() + ", false");
		 body();
		 //Generating intermediate code output: JMP
		 pc++; addString("JMP " + a_new_label() + ", 0");
		 pc++; numTemp.push(pc); labelTemp.add(st_a_new_label());
		 if (token.get(current).getWord().equals("{")) {
			 body();
		 }
		 return true;
	 }
	 
	 private String a_new_label() {
		 String t = "#e" + newLabel;
		 newLabel++;
		 return t;
	 }
	 
	 private String st_a_new_label() {
		 String t = "#e" + stNewLabel;
		 stNewLabel++;
		 return t;
	 }
	 
	 private boolean if_stmt() throws IOException {
		 if (condition() == false) {
			 untilOpen(); 
		 }
		 //Semantic check pop 1, check if bool otherwise Line <line>: boolean expression expected
		 String temp1 = semantic.registry.pop();
		 if (temp1.equals("boolean")) {} else {
			//semantic error Line <line>: type mismatch
			 out.write("Line " + token.elementAt(current-1).getLine() + ": "+ "boolean expression expected \n\n");
		 }
		//Generating intermediate code output: JMC
		 pc++; addString("JMC " + a_new_label() + ", false"); out.write(currentLabel + ", " + pc);
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
			 numTemp.push(pc); labelTemp.add(st_a_new_label());
			 if (relop() == false) {
				 return false;
			 } else {
				 if (primary() == false) {
					 return false; 
				 } else {
					//Semantic push to stack
					 String temp2 = new String();
					 String temp1 = new String();
					 if (!semantic.registry.empty()) {
						 temp2 = semantic.registry.pop();
					 }			 
					 if (!semantic.registry.empty()) {
						 temp1 = semantic.registry.pop();
					 }
					 if((temp2.equals("") || temp1.equals("")) && !temp1.equals(temp2) && !temp1.equals("error")) {
						 out.write("Line " + token.elementAt(current-1).getLine() + ": "+ "type mismatch \n\n");
					 } else {
						 String result = semantic.cube[semantic.checkType(temp1)][relop][semantic.checkType(temp2)];
						 semantic.registry.push(result); 
					 }
					//Generating intermediate code output: OPR
					 pc++; addString("OPR " + operator + ", 0");
					 return true;
				 }
			 }
		 }
	 }
	 
	 private boolean relop() throws IOException {
		 if (token.get(current).getWord().equals(">")) {
			 operator = 11;
			 relop = 5;
			 current++;
			 return true; 
		 } else if (token.get(current).getWord().equals("<")){
			 operator = 12;
			 relop = 5;
			 current++;
			 return true;
		 } else if (token.get(current).getWord().equals("!")){
			 current++;
			 if (token.get(current).getWord().equals("=")) {
				 operator = 16;
				 relop = 5;
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
			//SEMANTIC ERROR CHECK
			 if (!semantic.st.containsKey(token.get(current).getWord())) {
				//semantic error: Line <line>: variable <variable> not found
				 out.write("Line " + token.elementAt(current).getLine() + ": " + "variable " + token.elementAt(current).getWord() + " not found \n\n");
				 out.write("Line " + token.elementAt(current).getLine() + ": " + "incompatible types: boolean cannot be converted to integer \n\n");
			 } else {
				 semantic.registry.push(semantic.st.get(token.get(current).getWord()).elementAt(0).toString());
				//Generating intermediate code output: LOD
				 pc++; addString("LOD " + token.get(current).getWord() + ", 0");
			 }
			 String temp1 = new String();
			 if (!semantic.registry.empty()) {
				 temp1 = semantic.registry.pop();
				 if (temp1.equals("integer")){} else {
					 out.write("Line " + token.elementAt(current).getLine() + ": " + "incompatible types: boolean cannot be converted to integer \n\n");
				 }
			 } 
			 //
			 current++;
			 if (token.get(current).getWord().equals("{")) {
				 current++;
				 case_list();
				 if (token.get(current).getWord().equals("}")) {
					 checkEndOfProgram();
					 return true; 
				 } else {
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
					 pc++; addString("OPR 1, 0"); pc++; addString("OPR 0, 0");  addLabel(); out.write(printLabels + "@" + assemblyCode); out.close(); 
					 System.exit(0);  //end of initial body
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
					pc++; addString("OPR 1, 0"); pc++; addString("OPR 0, 0"); addLabel(); out.write(printLabels + "@" + assemblyCode); out.close(); 
					System.exit(0);  //end of initial body
				}
				return false;
		 }
	 }
	 
	 private boolean type() throws IOException {
		 if (token.get(current).getWord().equals("integer")) {
			 type = "integer";
			 current++;
			 return true;
		 } else if (token.get(current).getWord().equals("boolean")) {
			 type = "boolean";
			 current++;
			 return true;
		 } else {
			 //error: type error
			 out.write("Line " + token.elementAt(current).getLine() + ": " + "expected type" + "\n\n");
			 newLine();
			 return false;
		 }
	 }
	 
	 private void addString(String s){
		 assemblyCode = assemblyCode + "\n" + s;
	 }
	 
	 private void addLabel(){
		 //last element of numTemp where 
		 for(int i =0;labelTemp.size()>i; i++){
		 String s = (String) labelTemp.get(i);
		 s = s + ", " + numTemp.pop() +"\n";
		 printLabels = printLabels + s;
		 }
	 }
}
