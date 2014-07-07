package cmm.compiler;

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
		}
		if (debug[0]) tab.dumpScope(tab.curScope.locals, 0); 
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
		} else if (la.kind == 4) {
			Get();
			curCon.val = tab.charVal(t.val); 
		} else SynErr(41);
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
		if (la.kind == 1) {
			type = Type();
		} else if (la.kind == 21) {
			Get();
		} else SynErr(42);
		Expect(1);
		curProc = tab.insert(Obj.PROC, t.val, type); 
		Expect(5);
		tab.openScope(); 
		if (la.kind == 1 || la.kind == 23) {
			curProc.nPars = FormPars();
		}
		Expect(6);
		if (la.kind == 19) {
			Get();
			while (StartOf(2)) {
				if (la.kind == 16) {
					ConstDecl();
				} else if (isVarDecl()) {
					VarDecl();
				} else {
					Statement();
				}
			}
			curProc.locals = tab.curScope.locals;
			curProc.size = tab.curScope.size;
			tab.closeScope(); 
			Expect(20);
			if (debug[1]) Node.dump(curProc.ast, 0); 
		} else if (la.kind == 7) {
			Get();
			Expect(22);
			Expect(7);
		} else SynErr(43);
	}

	Struct  Type() {
		Struct  type;
		Expect(1);
		Obj obj = tab.find(t.val);
		 type = obj.type; 
		while (la.kind == 24) {
			Get();
			Expect(2);
			Expect(25);
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

	void Statement() {
		switch (la.kind) {
		case 1: {
			Designator();
			if (la.kind == 8) {
				Get();
				Expr();
			} else if (la.kind == 5) {
				ActPars();
			} else SynErr(44);
			Expect(7);
			break;
		}
		case 26: {
			Get();
			Expect(5);
			Condition();
			Expect(6);
			Statement();
			if (la.kind == 27) {
				Get();
				Statement();
			}
			break;
		}
		case 28: {
			Get();
			Expect(5);
			Condition();
			Expect(6);
			Statement();
			break;
		}
		case 29: {
			Get();
			Expect(5);
			Expr();
			Expect(6);
			Expect(7);
			break;
		}
		case 19: {
			Get();
			while (StartOf(3)) {
				Statement();
			}
			Expect(20);
			break;
		}
		case 30: {
			Get();
			Expr();
			Expect(7);
			break;
		}
		case 7: {
			Get();
			break;
		}
		default: SynErr(45); break;
		}
	}

	void FormPar() {
		Struct type; 
		bool isRef = false; 
		if (la.kind == 23) {
			Get();
			isRef = true; 
		}
		type = Type();
		Expect(1);
		Obj curRef = tab.insert(Obj.VAR, t.val, type); 
		curRef.isRef = isRef; 
	}

	void Designator() {
		Expect(1);
		String name = t.val;
		                      Obj objStruct = tab.find(name); 
		while (la.kind == 24 || la.kind == 35) {
			if (la.kind == 35) {
				Get();
				Expect(1);
				Obj objField = tab.findField(t.val,objStruct.type); 
			} else {
				Get();
				Expr();
				Expect(25);
			}
		}
	}

	void Expr() {
		Term();
		while (la.kind == 34 || la.kind == 36) {
			Addop();
			Term();
		}
	}

	void ActPars() {
		Expect(5);
		if (StartOf(4)) {
			ActPar();
			while (la.kind == 17) {
				Get();
				ActPar();
			}
		}
		Expect(6);
	}

	void Condition() {
		CondTerm();
		while (la.kind == 31) {
			Get();
			CondTerm();
		}
	}

	void ActPar() {
		if (StartOf(5)) {
			Expr();
		} else if (la.kind == 23) {
			Get();
			Expr();
		} else SynErr(46);
	}

	void CondTerm() {
		CondFact();
		while (la.kind == 32) {
			Get();
			CondFact();
		}
	}

	void CondFact() {
		if (isExpr()) {
			Expr();
			Relop();
			Expr();
		} else if (la.kind == 15) {
			Get();
			Expect(5);
			Condition();
			Expect(6);
		} else if (la.kind == 5) {
			Get();
			Condition();
			Expect(6);
		} else SynErr(47);
	}

	void Relop() {
		switch (la.kind) {
		case 9: {
			Get();
			break;
		}
		case 10: {
			Get();
			break;
		}
		case 13: {
			Get();
			break;
		}
		case 14: {
			Get();
			break;
		}
		case 11: {
			Get();
			break;
		}
		case 12: {
			Get();
			break;
		}
		default: SynErr(48); break;
		}
	}

	void Term() {
		Factor();
		while (la.kind == 37 || la.kind == 38 || la.kind == 39) {
			Mulop();
			Factor();
		}
	}

	void Addop() {
		if (la.kind == 36) {
			Get();
		} else if (la.kind == 34) {
			Get();
		} else SynErr(49);
	}

	void Factor() {
		Struct type; 
		if (la.kind == 1) {
			Designator();
			if (la.kind == 5) {
				ActPars();
			}
		} else if (la.kind == 2) {
			Get();
		} else if (la.kind == 3) {
			Get();
		} else if (la.kind == 4) {
			Get();
		} else if (la.kind == 33) {
			Get();
			Expect(5);
			Expect(6);
		} else if (la.kind == 34) {
			Get();
			Factor();
		} else if (isCast()) {
			Expect(5);
			type = Type();
			Expect(6);
			Factor();
		} else if (la.kind == 5) {
			Get();
			Expr();
			Expect(6);
		} else SynErr(50);
	}

	void Mulop() {
		if (la.kind == 37) {
			Get();
		} else if (la.kind == 38) {
			Get();
		} else if (la.kind == 39) {
			Get();
		} else SynErr(51);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		CMM();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
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
			case 41: s = "invalid ConstDecl"; break;
			case 42: s = "invalid ProcDecl"; break;
			case 43: s = "invalid ProcDecl"; break;
			case 44: s = "invalid Statement"; break;
			case 45: s = "invalid Statement"; break;
			case 46: s = "invalid ActPar"; break;
			case 47: s = "invalid CondFact"; break;
			case 48: s = "invalid Relop"; break;
			case 49: s = "invalid Addop"; break;
			case 50: s = "invalid Factor"; break;
			case 51: s = "invalid Mulop"; break;
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
