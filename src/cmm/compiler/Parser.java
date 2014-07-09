package cmm.compiler;

import java.util.ArrayList;

/*-------------------------------------------------------------------------
CMM Compiler description for C--
=== ============================
-------------------------------------------------------------------------*/



public class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _intCon = 2;
	public static final int _floatCon = 3;
	public static final int _charCon = 4;
	public static final int _lpar = 5;
	public static final int _rpar = 6;
	public static final int _semicolon = 7;
	public static final int _assign = 8;
	public static final int _eql = 9;
	public static final int _neq = 10;
	public static final int _lss = 11;
	public static final int _leq = 12;
	public static final int _gtr = 13;
	public static final int _geq = 14;
	public static final int _bang = 15;
	public static final int maxT = 40;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	public  Tab       tab;                     // symbol table
  public  boolean[] debug;
  
  Obj curProc;

//--- LL(1) conflict resolvers

	// Returns true if a VarDecl comes next in the input
	boolean isVarDecl() { 
		if (la.kind == _ident || la.val.equals("int") || la.val.equals("float") || la.val.equals("char")) {
			Token x = scanner.Peek();
			while (x.kind != _semicolon) {
				if (x.kind == _EOF || x.kind == _lpar || x.kind == _assign) return false;
				x = scanner.Peek();
			}
			return true;
		}
		return false;
	}
	
	// Returns true if the next input is an Expr and not a '(' Condition ')'
	boolean isExpr() { 
		if (la.kind == _bang) return false;
		else if (la.kind == _lpar) {
			Token x = scanner.Peek();
			while (x.kind != _rpar && x.kind != _EOF) {
				if (x.kind == _eql || x.kind == _neq || x.kind == _lss || x.kind == _leq || x.kind == _gtr || x.kind == _geq) return false;
				x = scanner.Peek();
			}
			return x.kind == _rpar;
		} else return true;
	}
	
	// Returns true if the next input is a type cast (requires symbol table)
	boolean isCast() {
		Token x = scanner.Peek();
		if (x.kind != _ident) return false;
		Obj obj = tab.find(x.val);
		return obj.kind == Obj.TYPE;
	}

/*-------------------------------------------------------------------------*/



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void CMM() {
		tab = new Tab(this); 
		     tab.openScope(); 
		while (StartOf(1)) {
			if (la.kind == 16) {
				ConstDecl();
			} else if (la.kind == 18) {
				StructDecl();
			} else if (isVarDecl()) {
				VarDecl();
			} else {
				ProcDecl();
			}
			while (!(StartOf(2))) {SynErr(41); Get();}
		}
		if (debug[0]) tab.dumpScope(tab.curScope.locals, 0);
		tab.checkIfForwardsResolved(tab.curScope); 
	}

	void ConstDecl() {
		Struct type; 
		Expect(16);
		type = Type();
		Expect(1);
		Obj curCon = tab.insert(Obj.CON, t.val, type); 
		Expect(8);
		if (la.kind == 2) {
			Get();
			curCon.val = tab.intVal(t.val); 
			                   if (type != Tab.intType) SemErr("int constant not allowed here"); 
		} else if (la.kind == 3) {
			Get();
			curCon.fVal = tab.floatVal(t.val); 
			if (type != Tab.floatType) SemErr("float constant not allowed here"); 
		} else if (la.kind == 4) {
			Get();
			curCon.val = tab.charVal(t.val); 
			if (type != Tab.charType) SemErr("char constant not allowed here"); 
		} else SynErr(42);
		Expect(7);
	}

	void StructDecl() {
		Expect(18);
		Struct type = new Struct(Struct.STRUCT); 
		Expect(1);
		tab.insert(Obj.TYPE, t.val, type); 
		Expect(19);
		tab.openScope(); 
		while (la.kind == 1) {
			VarDecl();
		}
		Expect(20);
		type.fields = tab.curScope.locals;
		type.size = tab.curScope.size;
		if(type.fields==null) SemErr("struct must contain at least one variable");
		tab.closeScope(); 
	}

	void VarDecl() {
		Struct type; 
		type = Type();
		Expect(1);
		tab.insert(Obj.VAR, t.val, type); 
		while (la.kind == 17) {
			Get();
			Expect(1);
			tab.insert(Obj.VAR, t.val, type); 
		}
		Expect(7);
	}

	void ProcDecl() {
		Struct type = Tab.noType; 
		int line = la.line; 
		if (la.kind == 1) {
			type = Type();
		} else if (la.kind == 21) {
			Get();
		} else SynErr(43);
		Expect(1);
		curProc = tab.insert(Obj.PROC, t.val, type); 
		if(type != Tab.noType && !type.isPrimitive()) 
		SemErr("procedure must return a primitive type or void"); 
		Expect(5);
		tab.openScope(); 
		if (la.kind == 1 || la.kind == 23) {
			curProc.nPars = FormPars();
		}
		Expect(6);
		if (la.kind == 19) {
			Get();
			if(curProc.isForward) {
			tab.checkForwardParams(curProc.locals,tab.curScope.locals);
			if(curProc.type != type)
			SemErr("return value of forware declaration does not match declaration");
			curProc.isForward = false;
			}
			Node startNode = null, curNode = null, newNode; 
			while (StartOf(3)) {
				if (la.kind == 16) {
					ConstDecl();
				} else if (isVarDecl()) {
					VarDecl();
				} else {
					newNode = Statement();
					if(startNode == null) {
					startNode = newNode;
					} else {
					curNode.next = newNode;  
					} 
					curNode = newNode; 
				}
			}
			Expect(20);
			if(curProc.type != Tab.noType) {
			if(startNode == null) {
			startNode = new Node(Node.TRAP,null,null,t.line);
			} else {
			curNode.next = new Node(Node.TRAP,null,null,t.line);
			}
			}
			curProc.ast = new Node(Node.STATSEQ,startNode,null,line); 
			if (debug[1]) Node.dump(curProc.ast, 0); 
		} else if (la.kind == 7) {
			Get();
			Expect(22);
			Expect(7);
			if(curProc.isForward) SemErr("function is already forward declared");
			curProc.isForward = true; 
		} else SynErr(44);
		curProc.locals = tab.curScope.locals;
		curProc.size = tab.curScope.size;
		tab.closeScope(); 
	}

	Struct  Type() {
		Struct  type;
		Expect(1);
		Obj obj = tab.find(t.val);
		if(obj.kind != Obj.TYPE) SemErr(obj.name + " is not a type");
		 type = obj.type; 
		 ArrayList<Integer> dimensions = new ArrayList(); 
		while (la.kind == 24) {
			Get();
			Expect(2);
			int arraySize = tab.intVal(t.val);
			dimensions.add(arraySize); 
			if(arraySize <= 0) SemErr("array-size must be 1 or higher"); 
			Expect(25);
		}
		for(int i = dimensions.size()-1; i>=0;i--) {
		   type = new Struct(Struct.ARR, dimensions.get(i), type);
		} 
		return type;
	}

	int  FormPars() {
		int  n;
		FormPar();
		n = 1; 
		while (la.kind == 17) {
			Get();
			FormPar();
			n++; 
		}
		return n;
	}

	Node  Statement() {
		Node  st;
		Node design; 
		Node e, con; 
		st = null; 
		int line = la.line; 
		switch (la.kind) {
		case 1: {
			design = Designator();
			if (la.kind == 8) {
				Get();
				e = Expr();
				st = new Node(Node.ASSIGN,new Node(design.obj),e,line); 
			} else if (la.kind == 5) {
				e = ActPars();
				if(design.type != Tab.noType) SemErr("only void is allowed"); 
				st = new Node(Node.CALL,e,null,line); 
			} else SynErr(45);
			Expect(7);
			break;
		}
		case 26: {
			Get();
			Expect(5);
			Node ifYes, ifNo; 
			con = Condition();
			Expect(6);
			ifYes = Statement();
			st = new Node(Node.IF,con,ifYes,line); 
			if (la.kind == 27) {
				Get();
				ifNo = Statement();
				st = new Node(Node.IFELSE,st,ifNo,line); 
			}
			break;
		}
		case 28: {
			Get();
			Expect(5);
			con = Condition();
			Expect(6);
			st = Statement();
			st = new Node(Node.WHILE,con,st,line); 
			break;
		}
		case 29: {
			Get();
			Expect(5);
			e = Expr();
			st = new Node(Node.PRINT,e,null,line); 
			Expect(6);
			Expect(7);
			break;
		}
		case 19: {
			Get();
			while (StartOf(4)) {
				st = Statement();
				st = new Node(Node.STATSEQ,st,null,line); 
			}
			Expect(20);
			break;
		}
		case 30: {
			Get();
			e = Expr();
			Expect(7);
			if(curProc.type.kind == Struct.NONE) SemErr("procedure has void as return type"); 
			st = new Node(Node.RETURN,e,null,line); 
			break;
		}
		case 7: {
			Get();
			st = null; 
			break;
		}
		default: SynErr(46); break;
		}
		return st;
	}

	void FormPar() {
		Struct type; 
		boolean isRef = false; 
		if (la.kind == 23) {
			Get();
			isRef = true; 
		}
		type = Type();
		Expect(1);
		Obj curRef = tab.insert(Obj.VAR, t.val, type); 
		curRef.isRef = isRef;
		if(!type.isPrimitive()) 
		SemErr("var must be a primitive type"); 
	}

	Node  Designator() {
		Node  n;
		Obj obj; Node e; 
		Expect(1);
		String name = t.val;
		                      obj = tab.find(name); 
		                      n = new Node(obj); 
		while (la.kind == 24 || la.kind == 35) {
			if (la.kind == 35) {
				Get();
				if(obj.type.kind != Struct.STRUCT) SemErr(name + " is not a struct"); 
				Expect(1);
				obj = tab.findField(t.val,obj.type); 
				n = new Node(Node.DOT, n, new Node(obj.adr), obj.type); 
			} else {
				Get();
				if(obj.type.kind != Struct.ARR) SemErr(name + " is not an array"); 
				e = Expr();
				n = new Node(Node.INDEX, n, e, obj.type); 
				Expect(25);
			}
		}
		return n;
	}

	Node  Expr() {
		Node  res;
		int kind;
		Node n; 
		res = Term();
		while (la.kind == 34 || la.kind == 36) {
			kind = Addop();
			n = Term();
			if(!res.type.isPrimitive() || !n.type.isPrimitive())
				SemErr("type is not a primitive");
			res = new Node(kind, res, n , res.type); 
		}
		return res;
	}

	Node  ActPars() {
		Node  outPar;
		Node par, curPar = null; 
		outPar = null; 
		Expect(5);
		if (StartOf(5)) {
			outPar = ActPar();
			while (la.kind == 17) {
				Get();
				par = ActPar();
				curPar.next = par;
				curPar = par; 
			}
		}
		Expect(6);
		return outPar;
	}

	Node  Condition() {
		Node  con;
		Node newCon; 
		con = CondTerm();
		while (la.kind == 31) {
			Get();
			newCon = CondTerm();
			con = new Node(Node.OR, con, newCon, Tab.boolType); 
		}
		return con;
	}

	Node  ActPar() {
		Node  e;
		e = null; 
		if (StartOf(6)) {
			e = Expr();
		} else if (la.kind == 23) {
			Get();
			e = Expr();
			e = new Node(Node.REF, e, null, e.obj.type); 
		} else SynErr(47);
		return e;
	}

	Node  CondTerm() {
		Node  con;
		con = CondFact();
		while (la.kind == 32) {
			Get();
			Node con2; 
			con2 = CondFact();
			con = new Node(Node.AND, con, con2, Tab.boolType); 
		}
		return con;
	}

	Node  CondFact() {
		Node  con;
		Node e; int kind; 
		con = null; 
		if (isExpr()) {
			con = Expr();
			kind = Relop();
			e = Expr();
			con = new Node(kind,con,e,Tab.boolType); 
		} else if (la.kind == 15) {
			Get();
			Expect(5);
			con = Condition();
			con = new Node(Node.NOT, con, null, Tab.boolType); 
			Expect(6);
		} else if (la.kind == 5) {
			Get();
			con = Condition();
			Expect(6);
		} else SynErr(48);
		return con;
	}

	int  Relop() {
		int  kind;
		kind = Node.EQL; 
		switch (la.kind) {
		case 9: {
			Get();
			kind = Node.EQL; 
			break;
		}
		case 10: {
			Get();
			kind = Node.NEQ; 
			break;
		}
		case 13: {
			Get();
			kind = Node.GTR; 
			break;
		}
		case 14: {
			Get();
			kind = Node.GEQ; 
			break;
		}
		case 11: {
			Get();
			kind = Node.LSS; 
			break;
		}
		case 12: {
			Get();
			kind = Node.LEQ; 
			break;
		}
		default: SynErr(49); break;
		}
		return kind;
	}

	Node  Term() {
		Node  res;
		int kind; 
		Node n; 
		res = Factor();
		while (la.kind == 37 || la.kind == 38 || la.kind == 39) {
			kind = Mulop();
			n = Factor();
			if(!res.type.isPrimitive() || !n.type.isPrimitive())
			SemErr("type is not a primitive");
			res = new Node(kind, res, n, n.type); 
		}
		return res;
	}

	int  Addop() {
		int  kind;
		kind=Node.PLUS; 
		if (la.kind == 36) {
			Get();
		} else if (la.kind == 34) {
			Get();
			kind=Node.MINUS; 
		} else SynErr(50);
		return kind;
	}

	Node  Factor() {
		Node  n;
		Struct type; 
		Node design; 
		n = null; 
		if (la.kind == 1) {
			design = Designator();
			if (la.kind == 5) {
				n = ActPars();
				if(design.obj.kind != Obj.PROC ) SemErr("name is not a procedure"); 
				if(design.obj.type == Tab.noType) SemErr("function call of a void procedure"); 
			}
			n = design; 
		} else if (la.kind == 2) {
			Get();
			n = new Node(tab.intVal(t.val)); 
		} else if (la.kind == 3) {
			Get();
			n = new Node(tab.floatVal(t.val)); 
		} else if (la.kind == 4) {
			Get();
			n = new Node(tab.charVal(t.val)); 
		} else if (la.kind == 33) {
			Get();
			Expect(5);
			Expect(6);
			n = new Node(Node.READ,null,null, tab.charType); 
		} else if (la.kind == 34) {
			Get();
			n = Factor();
			n = new Node(Node.MINUS,n,null,n.type); 
		} else if (isCast()) {
			Expect(5);
			type = Type();
			Expect(6);
			n = Factor();
			if(type.kind == n.obj.type.kind) n = n;
			else if(type == Tab.intType && n.obj.type == Tab.floatType) 
			n = new Node(Node.F2I, n, null, Tab.intType);
			else if(type == Tab.floatType && n.obj.type == Tab.intType) 
			n = new Node(Node.I2F, n, null, Tab.floatType);
			else if(type == Tab.intType && n.obj.type == Tab.charType) 
			n = new Node(Node.C2I, n, null, Tab.intType);
			else if(type == Tab.charType && n.obj.type == Tab.intType) 
			n = new Node(Node.I2C, n, null, Tab.charType);
			else if(type == Tab.charType && n.obj.type == Tab.floatType) {
			n = new Node(Node.F2I, n, null, Tab.intType);
			n = new Node(Node.I2C, n, null, Tab.charType);
			} else if(type == Tab.floatType && n.obj.type == Tab.charType) {
			n = new Node(Node.C2I, n, null, Tab.intType);
			n = new Node(Node.I2F, n, null, Tab.floatType);
			}
			else SemErr("no known cast");
			
		} else if (la.kind == 5) {
			Get();
			System.out.println("c"); 
			n = Expr();
			Expect(6);
		} else SynErr(51);
		return n;
	}

	int  Mulop() {
		int  kind;
		kind=Node.TIMES; 
		if (la.kind == 37) {
			Get();
		} else if (la.kind == 38) {
			Get();
			kind=Node.DIV; 
		} else if (la.kind == 39) {
			Get();
			kind=Node.REM; 
		} else SynErr(52);
		return kind;
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		CMM();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,T, x,x,x,x, x,x,x,x, T,x,x,T, x,x,x,x, x,x,T,x, T,T,T,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,T,x, T,T,T,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,T,T,x, x,x,x,x, x,x},
		{x,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,x, x,x,x,x, x,x}

	};
} // end Parser


class Errors {
	public int count = 0;     // number of errors detected
	Error head, tail;         // list of errors
	
	protected void storeError(int line, int col, String msg) {
		Error e = new Error(line, col, msg);
		if (head == null) head = e; else tail.next = e;
		tail = e;
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "ident expected"; break;
			case 2: s = "intCon expected"; break;
			case 3: s = "floatCon expected"; break;
			case 4: s = "charCon expected"; break;
			case 5: s = "lpar expected"; break;
			case 6: s = "rpar expected"; break;
			case 7: s = "semicolon expected"; break;
			case 8: s = "assign expected"; break;
			case 9: s = "eql expected"; break;
			case 10: s = "neq expected"; break;
			case 11: s = "lss expected"; break;
			case 12: s = "leq expected"; break;
			case 13: s = "gtr expected"; break;
			case 14: s = "geq expected"; break;
			case 15: s = "bang expected"; break;
			case 16: s = "\"const\" expected"; break;
			case 17: s = "\",\" expected"; break;
			case 18: s = "\"struct\" expected"; break;
			case 19: s = "\"{\" expected"; break;
			case 20: s = "\"}\" expected"; break;
			case 21: s = "\"void\" expected"; break;
			case 22: s = "\"forward\" expected"; break;
			case 23: s = "\"ref\" expected"; break;
			case 24: s = "\"[\" expected"; break;
			case 25: s = "\"]\" expected"; break;
			case 26: s = "\"if\" expected"; break;
			case 27: s = "\"else\" expected"; break;
			case 28: s = "\"while\" expected"; break;
			case 29: s = "\"print\" expected"; break;
			case 30: s = "\"return\" expected"; break;
			case 31: s = "\"||\" expected"; break;
			case 32: s = "\"&&\" expected"; break;
			case 33: s = "\"read\" expected"; break;
			case 34: s = "\"-\" expected"; break;
			case 35: s = "\".\" expected"; break;
			case 36: s = "\"+\" expected"; break;
			case 37: s = "\"*\" expected"; break;
			case 38: s = "\"/\" expected"; break;
			case 39: s = "\"%\" expected"; break;
			case 40: s = "??? expected"; break;
			case 41: s = "this symbol not expected in CMM"; break;
			case 42: s = "invalid ConstDecl"; break;
			case 43: s = "invalid ProcDecl"; break;
			case 44: s = "invalid ProcDecl"; break;
			case 45: s = "invalid Statement"; break;
			case 46: s = "invalid Statement"; break;
			case 47: s = "invalid ActPar"; break;
			case 48: s = "invalid CondFact"; break;
			case 49: s = "invalid Relop"; break;
			case 50: s = "invalid Addop"; break;
			case 51: s = "invalid Factor"; break;
			case 52: s = "invalid Mulop"; break;
			default: s = "error " + n; break;
		}
		storeError(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		storeError(line, col, s);
		count++;
	}
} // Errors

class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
