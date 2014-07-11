package cmm.compiler;

/*--------------------------------------------------------------------------------
Node   Node of the abstract syntax tree (AST) of a C-- program
====   =======================================================
Every node has a left and a right child. Some nodes (such as statements or parameters)
can also be linked by a "next" pointer.
Nodes representing a statement have a line number, whereas nodes representing
a part of an expression have a type.
--------------------------------------------------------------------------------*/

public final class Node {

	public static final int  // node kinds
		//-----------  statements
		STATSEQ  	= 0, 	// statement sequence
		ASSIGN   	= 1, 	// assignment
		ASSIGNPLUS		= 2,	// +=
		ASSIGNMINUS 	= 3,	// -=
		ASSIGNTIMES 	= 4,	// *=
		ASSIGNDIV 		= 5,	// /=
		ASSIGNREM 		= 6,	// %=
		ASSIGNLEFTSHIFT = 7,	// <<=
		ASSIGNRIGHTSHIFT = 8,	// >>=
		ASSIGNBITAND 	= 9,	// &=
		ASSIGNBITXOR 	= 10,	// ^=
		ASSIGNBITOR 	= 11,	// |=
		CALL     	= 12,	// procedure or function call
		IF       	= 13,  	// if statement without else branch
		IFELSE   	= 14,  	// if statement with else branch
		WHILE    	= 15,  	// while statement
		PRINT    	= 16,  	// print statement
		RETURN   	= 17,  	// return statement
		TRAP     	= 18,  	// trap if a function reaches its end without a return
		//------------ leaf expressions
		IDENT    	= 19,   // identifier
		INTCON   	= 20,   // int constant
		FLOATCON 	= 21,   // float constant
		CHARCON  	= 22,   // char constant
		//------------ designators and ref parameters
		DOT      	= 23,   // field selection (x.y)
		INDEX    	= 24,   // array element (a[i])
		REF      	= 25,   // ref parameter
		//------------ expressions
		PLUS     	= 26,  	// +
		MINUS    	= 27,   // -
		TIMES    	= 28,   // *
		DIV      	= 29,   // /
		REM      	= 30,   // %
		BITNEQ		= 31,	// ~
		BITAND		= 32,	// &
		BITOR		= 33,	// |
		BITXOR		= 34,	// ^
		LEFTSHIFT 	= 35,	// <<
		RIGHTSHIFT 	= 36,	// >>
		INC			= 37,	// ++	// Todo x++
		DEC			= 38,	// --	// Todo x--
		READ     	= 39,   // read operation
		I2F      	= 40,   // conversion from int to float
		F2I      	= 41,   // conversion from float to int
		I2C      	= 42,   // conversion from int to char
		C2I      	= 43,   // conversion from char to int
		//------------ conditionals
		EQL      	= 44,  	// ==
		NEQ      	= 45,  	// !=
		LSS      	= 46,  	// <
		LEQ      	= 47,  	// <=
		GTR      	= 48,  	// >
		GEQ      	= 49,  	// >=
		NOT      	= 50,  	// !
		OR       	= 51,  	// ||
		AND      	= 52;  	// &&

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

	//----------------------- for dumping ASTs -----------------------------------

	static String[] name = {
		"STATSEQ", "ASSIGN", "ASSIGNPLUS", "ASSIGNMINUS", "ASSIGNTIMES", "ASSIGNDIV","ASSIGNREM",
		"ASSIGNLEFTSHIFT", "ASSIGNRIGHTSHIFT", "ASSIGNBITAND", "ASSIGNBITXOR", "ASSIGNBITOR",
		"CALL", "IF", "IFELSE", "WHILE", "PRINT", "RETURN", "TRAP",
		"IDENT", "INTCON", "FLOATCON", "CHARCON",
		"DOT", "INDEX", "REF",
		"PLUS", "MINUS", "TIMES", "DIV", "REM", "BITNEQ", "BITAND", "BITOR", "BITXOR", 
		"LEFTSHIFT", "RIGHTSHIFT", "INC", "DEC", "READ", "I2F", "F2I", "I2C", "C2I",
		"EQL", "NEQ", "LSS", "LEQ", "GTR", "GEQ", "NOT", "OR", "AND"
	};

	static String[] typ = {
		"None", "Int", "Float", "Char", "Bool", "Arr", "Struct"
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
