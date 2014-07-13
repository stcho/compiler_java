package cse340;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import java.util.Iterator;
import java.util.Set;


public class Semantic {
	
	public Hashtable<String, Vector> st = new Hashtable <String, Vector>();
	Stack<String> registry = new Stack<String>();
	String scope;
	String[][][] cube = new String[3][9][3];
	
	int OP_ARITHMETIC = 0, OP_PLUS = 1, OP_MINUS = 2,  OP_MULT = 3, OP_DIV = 4;
	int OP_RELOP = 5, OP_GREATER = 6, OP_LESS = 7,  OP_NOTEQ = 8;
	int INTEGER = 0, BOOLEAN = 1, ERROR = 2;
	
	public Semantic() {
		//INTEGER Cube of Types for Arithmetic 
		cube[INTEGER][OP_ARITHMETIC][INTEGER] = "integer";
		cube[INTEGER][OP_ARITHMETIC][BOOLEAN] = "error";
		cube[INTEGER][OP_ARITHMETIC][ERROR] = "error";
		//BOOLEAN Cube of Types for Arithmetic
		cube[BOOLEAN][OP_ARITHMETIC][INTEGER] = "error";
		cube[BOOLEAN][OP_ARITHMETIC][BOOLEAN] = "error";
		cube[BOOLEAN][OP_ARITHMETIC][ERROR] = "error";
		//ERROR Cube of Types for Arithmetic
		cube[ERROR][OP_ARITHMETIC][INTEGER] = "error";
		cube[ERROR][OP_ARITHMETIC][BOOLEAN] = "error";
		cube[ERROR][OP_ARITHMETIC][ERROR] = "error";
		
		//INTEGER Cube of Types for Relop
		cube[INTEGER][OP_RELOP][INTEGER] = "boolean";
		cube[INTEGER][OP_RELOP][BOOLEAN] = "error";
		cube[INTEGER][OP_RELOP][ERROR] = "error";
		//BOOLEAN Cube of Types for Relop
		cube[BOOLEAN][OP_RELOP][INTEGER] = "error";
		cube[BOOLEAN][OP_RELOP][BOOLEAN] = "error";
		cube[BOOLEAN][OP_RELOP][ERROR] = "error";
		//ERROR Cube of Types for Relop
		cube[ERROR][OP_RELOP][INTEGER] = "error";
		cube[ERROR][OP_RELOP][BOOLEAN] = "error";
		cube[ERROR][OP_RELOP][ERROR] = "error";
	}
	
	
	public void insertSymbol(String id, String type, String scope) {
		Vector<String> temp = new Vector<String>();
		temp.addElement(type);
		temp.addElement(scope);
		st.put(id, temp);
	}
	
	public int checkType(String type) {
		if (type.equals("integer")) {
			 return 0;    
		}
		else if (type.equals("boolean")) {
			 return 1;    
		} else {
			 return 2;    
		}
	}
	
	public void storeID(String id) {
			
	}
	
	public boolean isBoolean() {
		return true;
	}
	
	public boolean isTypeMatching(String id) {
		return true;
	}
	
//	public String calculateType() {
//		
//	}
	
	public void stackPush(String type) {
		
	}
	
}
