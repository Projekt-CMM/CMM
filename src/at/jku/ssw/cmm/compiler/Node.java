package at.jku.ssw.cmm.compiler;

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
		//FOR    		= 17,  	// print statement
		RETURN   	= 18,  	// return statement
		TRAP     	= 19,  	// trap if a function reaches its end without a return
		//BREAK		= 20,
		//CONTINUE	= 21,
		//SWITCH		= 22,
		//CASE		= 23,
		//------------ leaf expressions
		IDENT    	= 24,   // identifier
		//BOOLCON		= 25,	// bool constant
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
		INC			= 44,	// ++	// Todo x++
		DEC			= 45,	// --	// Todo x--
		I2F      	= 46,   // conversion from int to float
		F2I      	= 47,   // conversion from float to int
		I2C      	= 48,   // conversion from int to char
		C2I      	= 49,   // conversion from char to int
		A2S      	= 50,   // conversion from char-array to string
		C2S			= 51,	// conversion from char to string
		//------------ conditionals
		EQL      	= 52,  	// ==
		NEQ      	= 53,  	// !=
		LSS      	= 54,  	// <
		LEQ      	= 55,  	// <=
		GTR      	= 56,  	// >
		GEQ      	= 57,  	// >=
		NOT      	= 58,  	// !
		OR       	= 59,  	// ||
		AND      	= 60;  	// &&

	public int kind;        // STATSEQ, ASSIGN, ...
	public Struct type;     // only used in expressions
	public int line;        // only used in statement nodes

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

	// for leaf nodes
	public Node(Obj obj) {
		this.kind = IDENT;
		this.type = obj.type;
		this.obj = obj;
	}

	public Node(int val) {
		this.kind = INTCON;
		this.type = Tab.intType;
		this.val = val;
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
	
	// TODO string

	//----------------------- for dumping ASTs -----------------------------------

	static String[] name = {
		"STATSEQ", "ASSIGN", "ASSIGNPLUS", "ASSIGNMINUS", "ASSIGNTIMES", "ASSIGNDIV","ASSIGNREM",
		"ASSIGNSHIFTLEFT", "ASSIGNSHIFTRIGHT", "ASSIGNBITAND", "ASSIGNBITXOR", "ASSIGNBITOR",
		"CALL", "IF", "IFELSE", "WHILE", "DOWHILE", "FOR", "RETURN", "TRAP", "BREAK", "CONTINUE",
		"SWITCH", "CASE", "IDENT", "BOOLCON", "INTCON", "FLOATCON", "CHARCON", "STRINGCON",
		"DOT", "INDEX", "REF",
		"PLUS", "MINUS", "TIMES", "DIV", "REM", "BITNEQ", "BITAND", "BITOR", "BITXOR", 
		"SHIFTLEFT", "SHIFTRIGHT", "INC", "DEC", "I2F", "F2I", "I2C", "C2I", "A2S", "C2S",
		"EQL", "NEQ", "LSS", "LEQ", "GTR", "GEQ", "NOT", "OR", "AND"
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
			else if (x.kind == INTCON) System.out.print(" " + x.val);
			else if (x.kind == FLOATCON) System.out.print(" " + x.fVal);
			else if (x.kind == CHARCON) System.out.print(" \'" + (char)x.val + "\'");
			else if (x.kind == STRINGCON) System.out.print(" \"" +  x.val + "\"");
			else if (x.kind == CALL && x.obj != null) System.out.print(" " + x.obj.name);
			if (x.type != null) System.out.print(" type=" + typ[x.type.kind]);
			if (x.kind >= STATSEQ && x.kind <= TRAP) System.out.print(" line=" + x.line);
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
