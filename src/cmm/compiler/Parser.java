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
	public static final int _assignplus = 9;
	public static final int _assignminus = 10;
	public static final int _assigntimes = 11;
	public static final int _assigndiv = 12;
	public static final int _assignrem = 13;
	public static final int _assignleftshift = 14;
	public static final int _assignrightshift = 15;
	public static final int _assignbitand = 16;
	public static final int _assignbitxor = 17;
	public static final int _assignbitor = 18;
	public static final int _eql = 19;
	public static final int _neq = 20;
	public static final int _lss = 21;
	public static final int _leq = 22;
	public static final int _gtr = 23;
	public static final int _geq = 24;
	public static final int _bang = 25;
	public static final int maxT = 58;

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
				if (x.kind == _EOF || x.kind == _lpar || x.kind == _assign || x.kind == _assignplus
					|| x.kind == _assignminus || x.kind == _assigntimes  || x.kind == _assigndiv  
					|| x.kind == _assignrem || x.kind == _assignleftshift || x.kind == _assignrightshift
					|| x.kind == _assignbitand || x.kind == _assignbitxor || x.kind == _assignbitor) return false;
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
			if (la.kind == 26) {
				ConstDecl();
			} else if (la.kind == 28) {
				StructDecl();
			} else if (isVarDecl()) {
				VarDecl();
			} else {
				ProcDecl();
			}
			while (!(StartOf(2))) {SynErr(59); Get();}
		}
		if (debug[0]) tab.dumpScope(tab.curScope.locals, 0);
		tab.checkIfForwardsResolved(tab.curScope); 
		Obj obj = tab.find("main"); 
		if(obj == Tab.noObj || obj.kind != Obj.PROC) SemErr("main is not declared as function");
	}

	void ConstDecl() {
		Struct type; 
		Expect(26);
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
		} else SynErr(60);
		Expect(7);
	}

	void StructDecl() {
		Expect(28);
		Struct type = new Struct(Struct.STRUCT); 
		Expect(1);
		tab.insert(Obj.TYPE, t.val, type); 
		Expect(29);
		tab.openScope(); 
		while (la.kind == 1) {
			VarDecl();
		}
		Expect(30);
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
		while (la.kind == 27) {
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
		} else if (la.kind == 31) {
			Get();
		} else SynErr(61);
		Expect(1);
		curProc = tab.insert(Obj.PROC, t.val, type); 
		if(type != Tab.noType && !type.isPrimitive()) 
		SemErr("procedure must return a primitive type or void"); 
		Expect(5);
		tab.openScope(); 
		if (la.kind == 1 || la.kind == 33) {
			curProc.nPars = FormPars();
		}
		Expect(6);
		if (la.kind == 29) {
			Get();
			if(curProc.isForward) {
			tab.checkForwardParams(curProc.locals,tab.curScope.locals);
			if(curProc.type != type)
			SemErr("return value of forware declaration does not match declaration");
			curProc.isForward = false;
			}
			Node startNode = null, curNode = null, newNode; 
			while (StartOf(3)) {
				if (la.kind == 26) {
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
			Expect(30);
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
			Expect(32);
			Expect(7);
			if(curProc.isForward) SemErr("function is already forward declared");
			curProc.isForward = true; 
		} else SynErr(62);
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
		while (la.kind == 34) {
			Get();
			Expect(2);
			int arraySize = tab.intVal(t.val);
			dimensions.add(arraySize); 
			if(arraySize <= 0) SemErr("array-size must be 1 or higher"); 
			Expect(35);
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
		while (la.kind == 27) {
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
		int kind;
		st = null; 
		int line = la.line; 
		switch (la.kind) {
		case 1: {
			design = Designator();
			if (StartOf(4)) {
				kind = Assignment();
				e = BinExpr();
				if(design.kind != Node.IDENT && design.kind != Node.DOT && design.kind != Node.INDEX) 
				SemErr("name must be an identifier");
				if(design.type == null || !design.type.isPrimitive()) SemErr("type is not a primitive");
				else if(e == null) SemErr("right operator is not defined"); 
				else e = tab.impliciteTypeCon(e, design.type);
				st = new Node(kind,design,e,line); 
			} else if (la.kind == 5) {
				e = ActPars();
				if(design.type != Tab.noType) SemErr("only void is allowed"); 
				st = new Node(Node.CALL,e,null,line);
				st.obj = design.obj;
				tab.checkFunctionParams(design.obj,st);
			} else SynErr(63);
			Expect(7);
			break;
		}
		case 36: {
			Get();
			Expect(5);
			Node ifYes, ifNo; 
			con = Condition();
			Expect(6);
			ifYes = Statement();
			st = new Node(Node.IF,con,ifYes,line); 
			if (la.kind == 37) {
				Get();
				ifNo = Statement();
				st = new Node(Node.IFELSE,st,ifNo,line); 
			}
			break;
		}
		case 38: {
			Get();
			Expect(5);
			con = Condition();
			Expect(6);
			st = Statement();
			st = new Node(Node.WHILE,con,st,line); 
			break;
		}
		case 39: {
			Get();
			Expect(5);
			e = BinExpr();
			e = tab.impliciteTypeCon(e, Tab.charType);
			st = new Node(Node.PRINT,e,null,line); 
			Expect(6);
			Expect(7);
			break;
		}
		case 29: {
			Get();
			while (StartOf(5)) {
				st = Statement();
				st = new Node(Node.STATSEQ,st,null,line); 
			}
			Expect(30);
			break;
		}
		case 40: {
			Get();
			e = BinExpr();
			Expect(7);
			if(curProc.type.kind == Struct.NONE) SemErr("procedure has void as return type");
			e = tab.impliciteTypeCon(e, curProc.type);
			st = new Node(Node.RETURN,e,null,line); 
			break;
		}
		case 7: {
			Get();
			st = null; 
			break;
		}
		default: SynErr(64); break;
		}
		return st;
	}

	void FormPar() {
		Struct type; 
		boolean isRef = false; 
		if (la.kind == 33) {
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
		while (la.kind == 34 || la.kind == 47) {
			if (la.kind == 47) {
				Get();
				if(obj.type.kind != Struct.STRUCT) SemErr(name + " is not a struct"); 
				Expect(1);
				obj = tab.findField(t.val,obj.type); 
				n = new Node(Node.DOT, n, new Node(obj.adr), obj.type); 
			} else {
				Get();
				if(obj.type.kind != Struct.ARR) SemErr(name + " is not an array"); 
				e = BinExpr();
				if(e.type.kind != Struct.INT) SemErr("index must be an int");
				n = new Node(Node.INDEX, n, e, obj.type.elemType); 
				Expect(35);
			}
		}
		return n;
	}

	int  Assignment() {
		int  kind;
		kind=Node.ASSIGN; 
		switch (la.kind) {
		case 8: {
			Get();
			break;
		}
		case 9: {
			Get();
			kind=Node.ASSIGNPLUS; 
			break;
		}
		case 10: {
			Get();
			kind=Node.ASSIGNMINUS; 
			break;
		}
		case 11: {
			Get();
			kind=Node.ASSIGNTIMES; 
			break;
		}
		case 12: {
			Get();
			kind=Node.ASSIGNDIV; 
			break;
		}
		case 13: {
			Get();
			kind=Node.ASSIGNREM; 
			break;
		}
		case 14: {
			Get();
			kind=Node.ASSIGNLEFTSHIFT; 
			break;
		}
		case 15: {
			Get();
			kind=Node.ASSIGNRIGHTSHIFT; 
			break;
		}
		case 16: {
			Get();
			kind=Node.ASSIGNBITAND; 
			break;
		}
		case 17: {
			Get();
			kind=Node.ASSIGNBITXOR; 
			break;
		}
		case 18: {
			Get();
			kind=Node.ASSIGNBITOR; 
			break;
		}
		default: SynErr(65); break;
		}
		return kind;
	}

	Node  BinExpr() {
		Node  res;
		int kind;
		Node n; 
		res = Shift();
		while (la.kind == 50 || la.kind == 51 || la.kind == 52) {
			kind = Binop();
			n = Shift();
			if(!res.type.isPrimitive() || !n.type.isPrimitive())
			SemErr("type is not a primitive");
			res = tab.doImplicitCastByAritmetic(res, res.type, n.type);
			n = tab.doImplicitCastByAritmetic(n, res.type, n.type);
			res = new Node(kind, res, n , res.type); 
		}
		return res;
	}

	Node  ActPars() {
		Node  outPar;
		Node par, curPar = null; 
		outPar = null; 
		Expect(5);
		if (StartOf(6)) {
			outPar = ActPar();
			while (la.kind == 27) {
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
		while (la.kind == 41) {
			Get();
			newCon = CondTerm();
			con = new Node(Node.OR, con, newCon, Tab.boolType); 
		}
		return con;
	}

	Node  ActPar() {
		Node  e;
		e = null; 
		if (StartOf(7)) {
			e = BinExpr();
		} else if (la.kind == 33) {
			Get();
			e = BinExpr();
			if(tab.isCastOperator(e.kind)) SemErr("there is no type-conversation for ref-parameter(s) allowed"); 
			else e = new Node(Node.REF, e, null, e.obj.type); 
		} else SynErr(66);
		return e;
	}

	Node  CondTerm() {
		Node  con;
		con = CondFact();
		while (la.kind == 42) {
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
			con = BinExpr();
			kind = Relop();
			e = BinExpr();
			if(!con.type.isPrimitive() || !e.type.isPrimitive())
			SemErr("type is not a primitive");
			con = tab.doImplicitCastByAritmetic(con, con.type, e.type);
			e = tab.doImplicitCastByAritmetic(e, con.type, e.type);
			con = new Node(kind,con,e,Tab.boolType); 
		} else if (la.kind == 25) {
			Get();
			Expect(5);
			con = Condition();
			con = new Node(Node.NOT, con, null, Tab.boolType); 
			Expect(6);
		} else if (la.kind == 5) {
			Get();
			con = Condition();
			Expect(6);
		} else SynErr(67);
		return con;
	}

	int  Relop() {
		int  kind;
		kind = Node.EQL; 
		switch (la.kind) {
		case 19: {
			Get();
			kind = Node.EQL; 
			break;
		}
		case 20: {
			Get();
			kind = Node.NEQ; 
			break;
		}
		case 23: {
			Get();
			kind = Node.GTR; 
			break;
		}
		case 24: {
			Get();
			kind = Node.GEQ; 
			break;
		}
		case 21: {
			Get();
			kind = Node.LSS; 
			break;
		}
		case 22: {
			Get();
			kind = Node.LEQ; 
			break;
		}
		default: SynErr(68); break;
		}
		return kind;
	}

	Node  Shift() {
		Node  res;
		int kind;
		Node n; 
		res = Expr();
		while (la.kind == 53 || la.kind == 54) {
			kind = Shiftop();
			n = Expr();
			if(!res.type.isPrimitive() || !n.type.isPrimitive())
				SemErr("type is not a primitive");
			res = tab.doImplicitCastByAritmetic(res, res.type, n.type);
			n = tab.doImplicitCastByAritmetic(n, res.type, n.type);
			res = new Node(kind, res, n , res.type); 
		}
		return res;
	}

	int  Binop() {
		int  kind;
		kind=Node.BITAND; 
		if (la.kind == 50) {
			Get();
		} else if (la.kind == 51) {
			Get();
			kind=Node.BITXOR; 
		} else if (la.kind == 52) {
			Get();
			kind=Node.BITOR; 
		} else SynErr(69);
		return kind;
	}

	Node  Expr() {
		Node  res;
		int kind;
		Node n; 
		res = Term();
		while (la.kind == 44 || la.kind == 45) {
			kind = Addop();
			n = Term();
			if(!res.type.isPrimitive() || !n.type.isPrimitive())
				SemErr("type is not a primitive");
			res = tab.doImplicitCastByAritmetic(res, res.type, n.type);
			n = tab.doImplicitCastByAritmetic(n, res.type, n.type);
			res = new Node(kind, res, n , res.type); 
		}
		return res;
	}

	int  Shiftop() {
		int  kind;
		kind=Node.LEFTSHIFT; 
		if (la.kind == 53) {
			Get();
		} else if (la.kind == 54) {
			Get();
			kind=Node.RIGHTSHIFT; 
		} else SynErr(70);
		return kind;
	}

	Node  Term() {
		Node  res;
		int kind; 
		Node n; 
		res = Factor();
		while (la.kind == 55 || la.kind == 56 || la.kind == 57) {
			kind = Mulop();
			n = Factor();
			if(!res.type.isPrimitive() || !n.type.isPrimitive())
			SemErr("type is not a primitive");
			res = tab.doImplicitCastByAritmetic(res, res.type, n.type);
			n = tab.doImplicitCastByAritmetic(n, res.type, n.type);
			res = new Node(kind, res, n, n.type); 
		}
		return res;
	}

	int  Addop() {
		int  kind;
		kind=Node.PLUS; 
		if (la.kind == 45) {
			Get();
		} else if (la.kind == 44) {
			Get();
			kind=Node.MINUS; 
		} else SynErr(71);
		return kind;
	}

	Node  Factor() {
		Node  n;
		Struct type; 
		Node design; 
		int kind; 
		n = null; 
		int line = la.line; 
		if (la.kind == 1) {
			design = Designator();
			if (la.kind == 5) {
				n = ActPars();
				if(design.obj.kind != Obj.PROC ) SemErr("name is not a procedure"); 
				if(design.obj.type == Tab.noType) SemErr("function call of a void procedure"); 
				n = new Node(Node.CALL,n,null,line); 
				n.type = design.obj.type; 
				n.obj = design.obj;
				tab.checkFunctionParams(design.obj,n); 
			}
			if (n == null) n = design; 
		} else if (la.kind == 2) {
			Get();
			n = new Node(tab.intVal(t.val)); 
		} else if (la.kind == 3) {
			Get();
			n = new Node(tab.floatVal(t.val)); 
		} else if (la.kind == 4) {
			Get();
			n = new Node(tab.charVal(t.val)); 
		} else if (la.kind == 43) {
			Get();
			Expect(5);
			Expect(6);
			n = new Node(Node.READ,null,null, tab.charType); 
		} else if (la.kind == 44) {
			Get();
			n = Factor();
			if(!n.type.isPrimitive()) SemErr("type is not a primitive");
			n = new Node(Node.MINUS,n,null,n.type); 
		} else if (la.kind == 45) {
			Get();
			n = Factor();
			n = new Node(Node.PLUS,n,null,n.type); 
		} else if (la.kind == 46) {
			Get();
			n = Factor();
			n = new Node(Node.BITNEQ,n,null,n.type); 
		} else if (la.kind == 48 || la.kind == 49) {
			kind = IncDecop();
			n = Factor();
			n = new Node(kind,n,null,n.type); 
		} else if (isCast()) {
			Expect(5);
			type = Type();
			Expect(6);
			n = Factor();
			n = tab.expliciteTypeCon(n, type); 
		} else if (la.kind == 5) {
			Get();
			n = BinExpr();
			Expect(6);
		} else SynErr(72);
		return n;
	}

	int  Mulop() {
		int  kind;
		kind=Node.TIMES; 
		if (la.kind == 55) {
			Get();
		} else if (la.kind == 56) {
			Get();
			kind=Node.DIV; 
		} else if (la.kind == 57) {
			Get();
			kind=Node.REM; 
		} else SynErr(73);
		return kind;
	}

	int  IncDecop() {
		int  kind;
		kind=Node.INC; 
		if (la.kind == 48) {
			Get();
		} else if (la.kind == 49) {
			Get();
			kind=Node.DEC; 
		} else SynErr(74);
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
		{T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, T,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, T,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, T,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,T,x,x, x,x,x,x, T,x,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, T,x,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,T, T,T,T,x, T,T,x,x, x,x,x,x, x,x,x,x},
		{x,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,x, T,T,x,x, x,x,x,x, x,x,x,x}

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
			case 9: s = "assignplus expected"; break;
			case 10: s = "assignminus expected"; break;
			case 11: s = "assigntimes expected"; break;
			case 12: s = "assigndiv expected"; break;
			case 13: s = "assignrem expected"; break;
			case 14: s = "assignleftshift expected"; break;
			case 15: s = "assignrightshift expected"; break;
			case 16: s = "assignbitand expected"; break;
			case 17: s = "assignbitxor expected"; break;
			case 18: s = "assignbitor expected"; break;
			case 19: s = "eql expected"; break;
			case 20: s = "neq expected"; break;
			case 21: s = "lss expected"; break;
			case 22: s = "leq expected"; break;
			case 23: s = "gtr expected"; break;
			case 24: s = "geq expected"; break;
			case 25: s = "bang expected"; break;
			case 26: s = "\"const\" expected"; break;
			case 27: s = "\",\" expected"; break;
			case 28: s = "\"struct\" expected"; break;
			case 29: s = "\"{\" expected"; break;
			case 30: s = "\"}\" expected"; break;
			case 31: s = "\"void\" expected"; break;
			case 32: s = "\"forward\" expected"; break;
			case 33: s = "\"ref\" expected"; break;
			case 34: s = "\"[\" expected"; break;
			case 35: s = "\"]\" expected"; break;
			case 36: s = "\"if\" expected"; break;
			case 37: s = "\"else\" expected"; break;
			case 38: s = "\"while\" expected"; break;
			case 39: s = "\"print\" expected"; break;
			case 40: s = "\"return\" expected"; break;
			case 41: s = "\"||\" expected"; break;
			case 42: s = "\"&&\" expected"; break;
			case 43: s = "\"read\" expected"; break;
			case 44: s = "\"-\" expected"; break;
			case 45: s = "\"+\" expected"; break;
			case 46: s = "\"~\" expected"; break;
			case 47: s = "\".\" expected"; break;
			case 48: s = "\"++\" expected"; break;
			case 49: s = "\"--\" expected"; break;
			case 50: s = "\"&\" expected"; break;
			case 51: s = "\"^\" expected"; break;
			case 52: s = "\"|\" expected"; break;
			case 53: s = "\"<<\" expected"; break;
			case 54: s = "\">>\" expected"; break;
			case 55: s = "\"*\" expected"; break;
			case 56: s = "\"/\" expected"; break;
			case 57: s = "\"%\" expected"; break;
			case 58: s = "??? expected"; break;
			case 59: s = "this symbol not expected in CMM"; break;
			case 60: s = "invalid ConstDecl"; break;
			case 61: s = "invalid ProcDecl"; break;
			case 62: s = "invalid ProcDecl"; break;
			case 63: s = "invalid Statement"; break;
			case 64: s = "invalid Statement"; break;
			case 65: s = "invalid Assignment"; break;
			case 66: s = "invalid ActPar"; break;
			case 67: s = "invalid CondFact"; break;
			case 68: s = "invalid Relop"; break;
			case 69: s = "invalid Binop"; break;
			case 70: s = "invalid Shiftop"; break;
			case 71: s = "invalid Addop"; break;
			case 72: s = "invalid Factor"; break;
			case 73: s = "invalid Mulop"; break;
			case 74: s = "invalid IncDecop"; break;
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
