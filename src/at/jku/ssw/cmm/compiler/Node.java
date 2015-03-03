/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
package at.jku.ssw.cmm.compiler;

import java.util.HashMap;
import java.util.Map;


/*--------------------------------------------------------------------------------
Node   Node of the abstract syntax tree (AST) of a C-- program
====   =======================================================
Every node has a left and a right child. Some nodes (such as statements or parameters)
can also be linked by a "next" pointer.
Nodes representing a statement have a line number, whereas nodes representing
a part of an expression have a type.
--------------------------------------------------------------------------------*/

public final class Node {

	public static Strings strings;
	
	public static final int  // node kinds
		//-----------  statements
		STATSEQ  	= 0, 	// statement sequence
		ASSIGN   	= 1, 	// assignment
		ASSIGNPLUS		= 2,	// +=
		ASSIGNMINUS 	= 3,	// -=
		ASSIGNTIMES 	= 4,	// *=
		ASSIGNDIV 		= 5,	// /=
		ASSIGNREM 		= 6,	// %=
		ASSIGNSHIFTLEFT = 7,	// <<=
		ASSIGNSHIFTRIGHT = 8,	// >>=
		ASSIGNBITAND 	= 9,	// &=
		ASSIGNBITXOR 	= 10,	// ^=
		ASSIGNBITOR 	= 11,	// |=
		CALL     	= 12,	// procedure or function call
		IF       	= 13,  	// if statement without else branch
		IFELSE   	= 14,  	// if statement with else branch
		WHILE    	= 15,  	// while statement
		DOWHILE		= 16,
		FOR    		= 17,  	// print statement
		RETURN   	= 18,  	// return statement
		TRAP     	= 19,  	// trap if a function reaches its end without a return
		BREAK		= 20,
		CONTINUE	= 21,
		SWITCH		= 22,
		CASE		= 23,	// default node: Node.left = null;
		//------------ leaf expressions
		IDENT    	= 24,   // identifier
		BOOLCON		= 25,	// bool constant
		INTCON   	= 26,   // int constant
		FLOATCON 	= 27,   // float constant
		CHARCON  	= 28,   // char constant
		STRINGCON  	= 29,   // char constant
		//------------ designators and ref parameters
		DOT      	= 30,   // field selection (x.y)
		INDEX    	= 31,   // array element (a[i])
		REF      	= 32,   // ref parameter
		//------------ expressions
		PLUS     	= 33,  	// +
		MINUS    	= 34,   // -
		TIMES    	= 35,   // *
		DIV      	= 36,   // /
		REM      	= 37,   // %
		BITNEQ		= 38,	// ~
		BITAND		= 39,	// &
		BITOR		= 40,	// |
		BITXOR		= 41,	// ^
		SHIFTLEFT 	= 42,	// <<
		SHIFTRIGHT 	= 43,	// >>
		I2F      	= 44,   // conversion from int to float
		F2I      	= 45,   // conversion from float to int
		I2C      	= 46,   // conversion from int to char
		C2I      	= 47,   // conversion from char to int
		A2S      	= 48,   // conversion from char-array to string
		C2S			= 49,	// conversion from char to string
		B2I			= 50,	// conversion from bool to int
		I2B			= 51,	// conversion from int to bool
		//------------ conditionals
		EQL      	= 52,  	// ==
		NEQ      	= 53,  	// !=
		LSS      	= 54,  	// <
		LEQ      	= 55,  	// <=
		GTR      	= 56,  	// >
		GEQ      	= 57,  	// >=
		NOT      	= 58,  	// !
		OR       	= 59,  	// ||
		AND      	= 60,  	// &&
		//------------ special nodes
		NOP			= 61,	// No operation
		WAIT		= 62;	// No operation

	public int kind;        // STATSEQ, ASSIGN, ...
	public Struct type;     // only used in expressions
	public int line;        // only used in statement nodes
	public int col;        	// only used in statement nodes
	public int colLength;

	public Node left;       // left son
	public Node right;      // right son
	public Node next;       // for linking statements, parameters, ...

	public Obj obj;         // object node of an IDENT
	public int val;         // value of an INTCON or CHARCON
	public float fVal;      // value of a FLOATCON

	// for expression nodes
	public Node(int kind, Node left, Node right, Struct type) {
		this.kind = kind;
		this.left = left;
		this.right = right;
		this.type = type;
	}

	// for statement nodes
	public Node(int kind, Node left, Node right, int line) {
		this(kind, left, right, null);
		this.line = line;
	}
	
	public Node(int kind, Node left, Node right, int line, int col, int colLength) {
		this(kind, left, right, line);
		this.col = col;
		this.colLength = colLength;
	}
	
	// for leaf nodes
	public Node(Obj obj) {
		this.kind = IDENT;
		this.type = obj.type;
		this.obj = obj;
	}
	
	public Node(Obj obj, int line, int col, int colLength) {
		this(obj);
		this.line = line;
		this.col = col;
		this.colLength = colLength;
	}

	public Node(boolean val) {
		this.kind = BOOLCON;
		this.type = Tab.boolType;
		if(val == true)
			this.val = 1;
		else
			this.val = 0;
	}
	
	public Node(int val) {
		this.kind = INTCON;
		this.type = Tab.intType;
		this.val = val;
	}

	public Node(int val, int line, int col, int colLength) {
		this(val);
		this.line = line;
		this.col = col;
		this.colLength = colLength;
	}
	
	public Node(float fValue) {
		this.kind = FLOATCON;
		this.type = Tab.floatType;
		this.fVal = fValue;
	}

	public Node(char ch) {
		this.kind = CHARCON;
		this.type = Tab.charType;
		this.val = ch;
	}
	
	public Node(String str) {
		this.kind = STRINGCON;
		this.type = Tab.stringType;
		//this.val = ch;	// TODO
	}

	//----------------------- for dumping ASTs -----------------------------------

	public Map<Integer,Integer> countNodes() {
		Map<Integer,Integer> nodeCounter = new HashMap<>();
		if(this.left != null) {
			Map<Integer,Integer> helpCounter = this.left.countNodes();
			
			for (int key : helpCounter.keySet()) {
				int value = 0;
				// get current number of keys inside map
				if(nodeCounter.containsKey(key))
					value = nodeCounter.get(key);
				// add new nodes
				value += helpCounter.get(key);
				// put new value into map
				nodeCounter.put(key, value);
			}
		}
		
		if(this.right != null) {
			Map<Integer,Integer> helpCounter = this.right.countNodes();
			
			for (int key : helpCounter.keySet()) {
				int value = 0;
				// get current number of keys inside map
				if(nodeCounter.containsKey(key))
					value = nodeCounter.get(key);
				// add new nodes
				value += helpCounter.get(key);
				// put new value into map
				nodeCounter.put(key, value);
			}
		}
		
		if(this.next != null) {
			Map<Integer,Integer> helpCounter = this.next.countNodes();
			
			for (int key : helpCounter.keySet()) {
				int value = 0;
				// get current number of keys inside map
				if(nodeCounter.containsKey(key))
					value = nodeCounter.get(key);
				// add new nodes
				value += helpCounter.get(key);
				// put new value into map
				nodeCounter.put(key, value);
			}
		}
		
		int valueOfThisNode = 0;
		// get current number of keys inside map
		if(nodeCounter.containsKey(this.kind))
			valueOfThisNode = nodeCounter.get(this.kind);
		valueOfThisNode ++;
		// put new value into map
		nodeCounter.put(this.kind, valueOfThisNode);
		
		return nodeCounter;
	}
	
	static String[] name = {
		"STATSEQ", "ASSIGN", "ASSIGNPLUS", "ASSIGNMINUS", "ASSIGNTIMES", "ASSIGNDIV","ASSIGNREM",
		"ASSIGNSHIFTLEFT", "ASSIGNSHIFTRIGHT", "ASSIGNBITAND", "ASSIGNBITXOR", "ASSIGNBITOR",
		"CALL", "IF", "IFELSE", "WHILE", "DOWHILE", "FOR", "RETURN", "TRAP", "BREAK", "CONTINUE",
		"SWITCH", "CASE", "IDENT", "BOOLCON", "INTCON", "FLOATCON", "CHARCON", "STRINGCON",
		"DOT", "INDEX", "REF",
		"PLUS", "MINUS", "TIMES", "DIV", "REM", "BITNEQ", "BITAND", "BITOR", "BITXOR", 
		"SHIFTLEFT", "SHIFTRIGHT", "I2F", "F2I", "I2C", "C2I", "A2S", "C2S", "B2I", "I2B",
		"EQL", "NEQ", "LSS", "LEQ", "GTR", "GEQ", "NOT", "OR", "AND",
		"NOP", "WAIT"
	};

	static String[] typ = {
		"None", "Int", "Float", "Char", "Bool", "Arr", "Struct", "String"
	};

	static void dump(Node x, int indent) {
		for (int i = 0; i < indent; i++) System.out.print("  ");
		if (x == null) System.out.println("-null-");
		else {
			System.out.print(name[x.kind]);
			if (x.kind == IDENT) System.out.print(" " + x.obj.name + " level=" + x.obj.level);
			else if (x.kind == BOOLCON) System.out.print(" " + x.val);
			else if (x.kind == INTCON) System.out.print(" " + x.val);
			else if (x.kind == FLOATCON) System.out.print(" " + x.fVal);
			else if (x.kind == CHARCON) System.out.print(" \'" + (char)x.val + "\'");
			else if (x.kind == STRINGCON) System.out.print(" \"" +  x.val + "\"");
			else if (x.kind == CALL && x.obj != null)
				System.out.print(" " + x.obj.name + " col=" + x.col + " colLenght=" +  x.colLength);
			if (x.type != null)
				System.out.print(" type=" + typ[x.type.kind]);
			if (x.kind >= STATSEQ && x.kind <= TRAP)
				System.out.print(" line=" + x.line);
			System.out.println();
			if (x.left != null || x.right != null) {
				dump(x.left, indent + 1);
				dump(x.right, indent + 1);
			}
			if (x.next != null) {
				for (int i = 0; i < indent; i++) System.out.print("  ");
				System.out.println("--- next ---");
				dump(x.next, indent);
			}
		}
	}

}
