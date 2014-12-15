package at.jku.ssw.cmm.compiler;

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
	public static final int _stringCon = 5;
	public static final int _lpar = 6;
	public static final int _rpar = 7;
	public static final int _lbrac = 8;
	public static final int _rbrac = 9;
	public static final int _lsqu = 10;
	public static final int _rsqu = 11;
	public static final int _semicolon = 12;
	public static final int _assign = 13;
	public static final int _assignplus = 14;
	public static final int _assignminus = 15;
	public static final int _assigntimes = 16;
	public static final int _assigndiv = 17;
	public static final int _assignrem = 18;
	public static final int _assignleftshift = 19;
	public static final int _assignrightshift = 20;
	public static final int _assignbitand = 21;
	public static final int _assignbitxor = 22;
	public static final int _assignbitor = 23;
	public static final int _eql = 24;
	public static final int _neq = 25;
	public static final int _lss = 26;
	public static final int _leq = 27;
	public static final int _gtr = 28;
	public static final int _geq = 29;
	public static final int _bang = 30;
	public static final int _and = 31;
	public static final int _or = 32;
	public static final int _bitand = 33;
	public static final int _bitor = 34;
	public static final int _bitxor = 35;
	public static final int maxT = 66;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	public Tab       tab;                     // symbol table
    public boolean[] debug;
    public int breakLevel, continueLevel;
  
      Obj curProc;
    Strings strings = new Strings();
//--- LL(1) conflict resolvers

    /** 
     * Check if a VarDecl comes next in the input
     *
     * @return true if a var-declaration follow
     */
    boolean isVarDecl() {
        // is the next kind an identifier?
        if (la.kind != _ident) 
            return false;
        // get identifier and check if it represent a type
        Obj obj = tab.find(la.val);
        if (obj.kind == Obj.TYPE){
            // get next token
            Token x = scanner.Peek();
            
            // if the current token is a semicolon, it doesn't reach a wrong token, so it would be a declaration
            while (x.kind != _semicolon) {
                // this token cannot follow direct after a declaration
                if (x.kind == _EOF || x.kind == _lpar  || x.kind == _assignplus
                    || x.kind == _assignminus || x.kind == _assigntimes  || x.kind == _assigndiv  
                    || x.kind == _assignrem || x.kind == _assignleftshift || x.kind == _assignrightshift
                    || x.kind == _assignbitand || x.kind == _assignbitxor || x.kind == _assignbitor) 
                    return false;
                    
                // return true because it is a assignment
                else if(x.kind == _assign)
                    return true;
                    
                // get next token
                x = scanner.Peek();
            }
            // token is semicolon
            return true;
        }
        // identifier is not a type
        return false;
    }
    
    /**
     * check if next input is an Expression, and not a '(' Condition ')'
     *
     * @return true if the next input is an Expr
     */
    boolean isExpr() {
        // if the next token is a "!", it is sure a condition
        if (la.kind == _bang) 
            return false;
            
        // the next token is a "(", so it can be an expression or a condition
        else if (la.kind == _lpar) {
            
            // get next token
            Token x = scanner.Peek();
            
            // if one of the following tokens occour, it would be a condition
            while (x.kind != _rpar && x.kind != _EOF) {
                if (x.kind == _eql || x.kind == _neq || x.kind == _lss 
                    || x.kind == _leq || x.kind == _gtr || x.kind == _geq || x.kind == _and | x.kind == _or ) 
                    return false;
                // get next token
                x = scanner.Peek();
            }

            // if last readed character is a ")", it is a expression, otherwise we reached end of file
            return x.kind == _rpar;
        } 
        // anything else is an Expression
        else 
            return true;
    }
    
    /**
     * Check if the next input is a type cast
     *
     * @return true if the next input is a type cast 
     *
     * @info requires symbol table
     */
    boolean isCast() {
        // get next token
        Token x = scanner.Peek();
        
        // if it is not an identifier, it cannot be a cast
        if (x.kind != _ident) 
            return false;
            
        // get the identifier
        Obj obj = tab.find(x.val);
        
        // check if the identfier declare a type
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
		// open global scope
		tab.openScope(); 
		breakLevel = 0;
		Node e; 
		while (StartOf(1)) {
			if (la.kind == 36) {
				ConstDecl();
			} else if (la.kind == 41) {
				StructDecl();
			} else if (isVarDecl()) {
				e = VarDecl();
				if(e != null) 
				   SemErr("variable assigment is not allowed for global variables"); 
			} else {
				ProcDecl();
			}
			while (!(StartOf(2))) {SynErr(67); Get();}
		}
		if (debug[0]) 
		   tab.dumpScope(tab.curScope.locals, 0);
		if (debug[2])
		   strings.dump();
		                                        // check if all forward-declarations resolved
		tab.checkIfForwardsResolved(tab.curScope);
		                                        // check if main-function is declared correct
		Obj obj = tab.find("main");
		if(obj == Tab.noObj || obj.kind != Obj.PROC) 
		   SemErr("main is not declared as function");
		else if(obj.nPars != 0) 
		   SemErr("function parameters not allowed for main function");
		
	}

	void ConstDecl() {
		Struct type; 
		int line = la.line; 
		boolean library = false; 
		Expect(36);
		type = Type();
		if (la.kind == 37) {
			Get();
			library = true; 
		}
		Expect(1);
		Obj curCon = tab.insert(Obj.CON, t.val, type, line); 
		curCon.library = library; 
		Expect(13);
		if (la.kind == 38 || la.kind == 39) {
			if (la.kind == 38) {
				Get();
				curCon.val = 1; 
				if (type != Tab.boolType) 
				   SemErr("bool constant not allowed here"); 
			} else {
				Get();
				curCon.val = 0; 
				if (type != Tab.boolType) 
				   SemErr("bool constant not allowed here"); 
			}
		} else if (la.kind == 2) {
			Get();
			curCon.val = tab.intVal(t.val); 
			if (type != Tab.intType) 
			   SemErr("int constant not allowed here"); 
		} else if (la.kind == 3) {
			Get();
			curCon.fVal = tab.floatVal(t.val); 
			if (type != Tab.floatType) 
			   SemErr("float constant not allowed here"); 
		} else if (la.kind == 4) {
			Get();
			curCon.val = tab.charVal(t.val); 
			if (type != Tab.charType) 
			   SemErr("char constant not allowed here"); 
		} else if (la.kind == 5) {
			Get();
			curCon.val = strings.put(tab.stringVal(t.val));
			if (type != Tab.stringType) 
			   SemErr("string constant not allowed here"); 
		} else SynErr(68);
		Expect(12);
	}

	void StructDecl() {
		Node e; 
		int line = la.line; 
		Expect(41);
		Struct type = new Struct(Struct.STRUCT); 
		Expect(1);
		Obj struct = tab.insert(Obj.TYPE, t.val, type, line); 
		Expect(8);
		tab.openScope(); 
		while (la.kind == 1) {
			if(la.val.equals(struct.name)) {
			   SemErr("it is not allowed to declare the same struct in a struct");
			   break;
			} 
			e = VarDecl();
			if(e!=null) 
			   SemErr("variable assigment is not allowed in struct"); 
		}
		Expect(9);
		type.fields = tab.curScope.locals;
		// copy size
		type.size = tab.curScope.size;
		     
		// check if no variable is declared
		if(type.fields==null) 
		   SemErr("struct must contain at least one variable");
		    
		// close current scope
		tab.closeScope(); 
	}

	Node  VarDecl() {
		Node  e;
		Struct type;
		boolean library = false; 
		Node eNew, eHelp;
		e = null; 
		type = Type();
		if (la.kind == 37) {
			Get();
			library = true; 
		}
		e = VarDeclPart(type, library);
		eHelp = e; 
		while (la.kind == 40) {
			Get();
			eNew = VarDeclPart(type, library);
			if(eNew != null) {
			   if(eHelp == null)
			       eHelp = eNew;
			   else
			       eHelp.next = eNew;
			   eHelp = eNew; 
			} 
		}
		Expect(12);
		return e;
	}

	void ProcDecl() {
		Struct type = Tab.noType; 
		int line = la.line; 
		boolean library = false; 
		if (la.kind == 1) {
			type = Type();
		} else if (la.kind == 42) {
			Get();
		} else SynErr(69);
		if (la.kind == 37) {
			Get();
			library = true; 
		}
		Expect(1);
		curProc = tab.insert(Obj.PROC, t.val, type, line);
		                                        if(library)
		   curProc.library = library;
		                                        // check if it return the correct type
		if(type != Tab.noType && type != Tab.stringType && !type.isPrimitive()) 
		   SemErr("procedure must return a primitive type, a string or is void"); 
		Expect(6);
		tab.openScope(); 
		if (la.kind == 1) {
			curProc.nPars = FormPars();
		}
		Expect(7);
		if (la.kind == 8) {
			Get();
			if(curProc.isForward) {
			   if(curProc.library != library)
			       SemErr("forward declaration and declaration doesn't match with the library token");
			   // check if forward-declaration match with the current declaration
			   tab.checkForwardParams(curProc.locals,tab.curScope.locals);
			   
			   // check return type
			   if(curProc.type != type)
			       SemErr("return value of forware declaration does not match declaration");
			      
			   // remove forward-flag
			   curProc.isForward = false;
			}
			Node startNode = null, curNode = null, newNode; 
			while (StartOf(3)) {
				if (la.kind == 36) {
					ConstDecl();
				} else if (isVarDecl()) {
					newNode = VarDecl();
					if(newNode != null) { 
					   if(startNode == null) {
					       // if that is the first statment in the procedure, set start node
					       startNode = newNode;
					   } else {
					       //otherwise check if current node is correct
					       if(curNode == null) 
					           SemErr("invalide statement");
					                
					           // add statment to list if possible
					       else 
					           curNode.next = newNode;  
					   }
					          
					   // set new current node
					   curNode = newNode;
					        
					   // go forward in the list, if more than one statment occour while the declaration
					   while(curNode.next != null) {
					       curNode = curNode.next; 
					   } 
					} 
				} else {
					newNode = Statement();
					if(startNode == null) {
					   // if that is the first statment in the procedure, set start node
					   startNode = newNode;
					} else {
					   //otherwise check if current node is correct
					   if(curNode == null) 
					       SemErr("invalide statement");
					        
					   // add statment to list if possible
					   else 
					       curNode.next = newNode;  
					}
					    
					// set new current node
					curNode = newNode; 
				}
			}
			Expect(9);
			if(curProc.type != Tab.noType) {
			   // add Node.TRAP at end of procedure if possible
			   if(startNode == null) {
			       startNode = new Node(Node.TRAP,null,null,t.line);
			   } else {
			       if(curNode == null) 
			           SemErr("invalide statement");
			       else 
			           curNode.next = new Node(Node.TRAP,null,null,t.line);
			   }
			}
			    
			// add created syntax-tree to procedure
			curProc.ast = new Node(Node.STATSEQ,startNode,null,line);
			    
			// generate debug-output if the correct flags are set
			if (debug[1]) 
			   Node.dump(curProc.ast, 0); 
		} else if (la.kind == 12) {
			Get();
			if(curProc.isForward) 
			   SemErr("function is already forward declared");
			   
			// set forward-flag
			curProc.isForward = true; 
		} else SynErr(70);
		curProc.locals = tab.curScope.locals;
		                                        // copy variable size of lcurrent scope into procedure
		curProc.size = tab.curScope.size;
		  
		// close current scope
		tab.closeScope(); 
	}

	Struct  Type() {
		Struct  type;
		Expect(1);
		Obj obj = tab.find(t.val);
		if(obj.kind != Obj.TYPE)
		   SemErr(obj.name + " is not a type");
		 
		type = obj.type; 
		return type;
	}

	Node  VarDeclPart(Struct type, boolean library) {
		Node  e;
		Struct curType; 
		Node newNode; 
		Obj curObj;
		e = null;
		// get current line
		int line = la.line; 
		Expect(1);
		String varName = t.val;
		// init array-list, which store the size of the dimensions
		ArrayList<Integer> dimensions = new ArrayList<>(); 
		while (la.kind == 10) {
			Get();
			int arraySize = 1; 
			if (la.kind == 2) {
				Get();
				arraySize = tab.intVal(t.val); 
			} else if (la.kind == 1) {
				Get();
				Obj helpObj = tab.find(t.val); 
				if(helpObj.kind != Obj.CON)
				   SemErr(helpObj.name + " is not a constant");
				if(helpObj.type != Tab.intType)
				   SemErr(helpObj.name + " is not an int constant");
				arraySize = helpObj.val; 
			} else SynErr(71);
			dimensions.add(arraySize);
			// check if size of dimension is at least 1 
			if(arraySize <= 0)
			   SemErr("array-size must be 1 or higher"); 
			Expect(11);
		}
		curType = type;
		for(int i = dimensions.size()-1; i>=0;i--) {
		   curType = new Struct(Struct.ARR, dimensions.get(i), curType);
		} 
		// create new variable
		curObj = tab.insert(Obj.VAR, varName, curType, line); 
		curObj.library = library; 
		if (la.kind == 13) {
			Get();
			newNode = BinExpr();
			if(curType == null || (!curType.isPrimitive() && curType != Tab.stringType)) 
			   SemErr("type is not a primitive or string");
			                                      // check if Expression is not null
			else if(newNode == null) 
			   SemErr("right operator is not defined");
			
			// make implicit type conversation
			else 
			   newNode = tab.impliciteTypeCon(newNode, curType);
			e = new Node(Node.ASSIGN,new Node(curObj),newNode,line); 
		}
		return e;
	}

	Node  BinExpr() {
		Node  res;
		int kind;
		Node n = null; 
		res = Shift();
		while (la.kind == 33 || la.kind == 34 || la.kind == 35) {
			kind = Binop();
			n = Shift();
			if(!res.type.isPrimitive() || n == null || !n.type.isPrimitive())
			   SemErr("type is not a primitive");
			else {
			   res = tab.doImplicitCastByAritmetic(res, res.type, n.type);
			   n = tab.doImplicitCastByAritmetic(n, res.type, n.type);
			   res = new Node(kind, res, n , res.type);
			} 
		}
		return res;
	}

	int  FormPars() {
		int  n;
		FormPar();
		n = 1; 
		while (la.kind == 40) {
			Get();
			FormPar();
			n++; 
		}
		return n;
	}

	Node  Statement() {
		Node  st;
		Node e = null, con, curStat, newStat; 
		st = null; 
		int line = la.line; 
		switch (la.kind) {
		case 1: {
			st = Command();
			Expect(12);
			break;
		}
		case 43: {
			Get();
			Expect(6);
			Node ifYes, ifNo; 
			con = Condition();
			Expect(7);
			ifYes = Statement();
			st = new Node(Node.IF,con,ifYes,line); 
			if (la.kind == 44) {
				Get();
				ifNo = Statement();
				st = new Node(Node.IFELSE,st,ifNo,line); 
			}
			break;
		}
		case 45: {
			Get();
			Expect(6);
			con = Condition();
			Expect(7);
			breakLevel ++; 
			continueLevel ++; 
			st = Statement();
			st = new Node(Node.WHILE,con,st,line); 
			breakLevel --; 
			continueLevel --; 
			break;
		}
		case 46: {
			Get();
			breakLevel ++; 
			continueLevel ++; 
			st = Statement();
			breakLevel --; 
			continueLevel --; 
			Expect(45);
			Expect(6);
			con = Condition();
			Expect(7);
			Expect(12);
			st = new Node(Node.DOWHILE,con,st,line); 
			break;
		}
		case 47: {
			Get();
			Node command = null, relopCommand = null, relSt; 
			breakLevel ++; 
			continueLevel ++; 
			Expect(6);
			if (la.kind == 1) {
				command = Command();
			} else if (la.kind == 12) {
			} else SynErr(72);
			if(command == null)
			   command = new Node(Node.NOP,null,null,line); 
			Expect(12);
			con = Condition();
			Expect(12);
			if (la.kind == 1) {
				relopCommand = Command();
			} else if (la.kind == 7) {
			} else SynErr(73);
			if(relopCommand == null)
			   relopCommand = new Node(Node.NOP,null,null,line); 
			Expect(7);
			relSt = Statement();
			breakLevel --; 
			continueLevel --;
			relopCommand.next = relSt;
			command.next = relopCommand;
			st = new Node(Node.FOR,con,command,line); 
			break;
		}
		case 48: {
			Get();
			Expect(6);
			e = BinExpr();
			Expect(7);
			st = new Node(Node.SWITCH,e,null,line);
			curStat = null;
			breakLevel ++; 
			Node n, defaultNode = null;
			Node firstCaseStatement;
			Node lastStatement = new Node(Node.NOP,null,null,line); 
			Expect(8);
			while (la.kind == 49 || la.kind == 50) {
				n = null; 
				newStat = null; 
				if (la.kind == 49) {
					Get();
					if (la.kind == 2) {
						Get();
						n = new Node(tab.intVal(t.val)); 
					} else if (la.kind == 3) {
						Get();
						n = new Node(tab.floatVal(t.val)); 
					} else if (la.kind == 4) {
						Get();
						n = new Node(tab.charVal(t.val)); 
					} else SynErr(74);
					if(n.type != e.type)
					   SemErr("type of switch has to match type of case value");
					newStat = new Node(Node.CASE,n,null,line); 
				} else {
					Get();
					newStat = new Node(Node.CASE,null,null,line);
					if(defaultNode != null)
					   SemErr("you cannot declare the default statement twice");
					defaultNode = newStat; 
				}
				Expect(51);
				firstCaseStatement = null;
				lastStatement.next = firstCaseStatement; 
				while (StartOf(4)) {
					n = Statement();
					lastStatement.next = n;
					                                        // get reference to first case-statement in this node
					if(firstCaseStatement == null)
					   firstCaseStatement = n;
					                                        lastStatement = n; 
				}
				if(firstCaseStatement == null) {
				   firstCaseStatement = new Node(Node.NOP,null,null,line);
				   lastStatement.next = firstCaseStatement;
				   lastStatement = firstCaseStatement;
				}
				    newStat.right = firstCaseStatement;
				if(newStat != defaultNode) {
				   if(curStat == null)
				       st.right = newStat;
				   else
				       curStat.next = newStat;
				   curStat = newStat; 
				} 
			}
			Expect(9);
			if(defaultNode != null) {
			   if(curStat == null)
			       st.right = defaultNode;
			   else
			       curStat.next = defaultNode;
			}
			breakLevel --; 
			break;
		}
		case 8: {
			Get();
			curStat = null; con=null; 
			while (StartOf(4)) {
				newStat = Statement();
				if(curStat == null) {
				   curStat = newStat;
				   con = curStat;
				} else {
				   curStat.next = newStat;
				   curStat = curStat.next;
				} 
			}
			Expect(9);
			st = new Node(Node.STATSEQ,con,null,line); 
			break;
		}
		case 52: {
			Get();
			if (StartOf(5)) {
				e = BinExpr();
				if(curProc.type.kind == Struct.NONE)
				   SemErr("procedure has void as return type defined");
				// do implicite type convertation if required
				e = tab.impliciteTypeCon(e, curProc.type); 
			}
			Expect(12);
			if(e == null && curProc.type.kind != Struct.NONE)
			   SemErr("return require parameter from correct type");
			st = new Node(Node.RETURN,e,null,line); 
			break;
		}
		case 53: {
			Get();
			Expect(12);
			if(breakLevel <= 0)
			   SemErr("break is not allowed here");
			st = new Node(Node.BREAK,null,null,line); 
			break;
		}
		case 54: {
			Get();
			Expect(12);
			if(continueLevel <= 0)
			   SemErr("continue is not allowed here");
			st = new Node(Node.CONTINUE,null,null,line); 
			break;
		}
		case 12: {
			Get();
			st = null; 
			break;
		}
		default: SynErr(75); break;
		}
		return st;
	}

	void FormPar() {
		Struct type, mainType; 
		boolean isRef = false;
		boolean isArray = false; 
		int line = la.line; 
		String ident_val; 
		type = Type();
		mainType = type; 
		if (la.kind == 33) {
			Get();
			isRef = true; 
		}
		Expect(1);
		ident_val = t.val; 
		while (la.kind == 10) {
			Get();
			Expect(11);
			if(isRef && !isArray)
			   SemErr("array call and call by reference cannot mixed up");
			// change state variables
			isArray = true;
			isRef = true; 
			// generate array
			type = new Struct(Struct.ARR, -1, type); 
		}
		if(isRef)
		   type.size = 4;
		// add parameter to current scope
		Obj curPar = tab.insert(Obj.VAR, ident_val, type, line);
		// copy reference-flag
		curPar.isRef = isRef;
		                                       // check if parameter is primitive or string
		if(!isArray && !type.isPrimitive() && type != Tab.stringType) 
		   SemErr("var must be a primitive type or string");
		if(isArray && !mainType.isPrimitive() && mainType != Tab.stringType)
		   SemErr("array reference must be a primitive type or string"); 
	}

	Node  Command() {
		Node  st;
		Node design; 
		Node e = null; 
		int kind;
		st = null; 
		int line = la.line; 
		design = Designator();
		if (StartOf(6)) {
			kind = AssignOp();
			if(design.kind == Node.BOOLCON || design.kind == Node.INTCON
			   || design.kind == Node.FLOATCON || design.kind == Node.CHARCON
			   || design.kind == Node.STRINGCON) {
			   SemErr("assignment is not allowed for const values");
			} else if(design.kind == Node.IDENT && design.obj.kind == Obj.PROC) {
			   SemErr(design.obj.name + " is declared as function");
			} 
			e = BinExpr();
			if(design.kind != Node.IDENT && design.kind != Node.DOT && design.kind != Node.INDEX) 
			   SemErr("name must be an designator");
			if(design.type == null || (!design.type.isPrimitive() && design.type != Tab.stringType)) 
			   SemErr("type is not a primitive or string");
			else if(design.kind == Node.INDEX && design.left.type == Tab.stringType)
			   SemErr("string manipulation is not allowed"); 
			else if(e == null)
			   SemErr("right operator is not defined"); 
			else if(design.type == Tab.stringType && !(kind == Node.ASSIGN || kind == Node.ASSIGNPLUS)) 
			   SemErr("only == or += is allowed for string assignements");
			else if(design.type == Tab.boolType && !(kind == Node.ASSIGN || kind == Node.ASSIGNBITAND
			       || kind == Node.ASSIGNBITXOR || kind == Node.ASSIGNBITOR)) 
			   SemErr("only ==, &=, ^= or |= is allowed for boolean assignements"); 
			else
			   e = tab.impliciteTypeCon(e, design.type);
			                                     // add special node if assign has operator inside (like *=, -0,...)
			if(kind == Node.ASSIGNPLUS)
			   e = new Node(Node.PLUS, design, e, design.type);
			else if(kind == Node.ASSIGNMINUS)
			   e = new Node(Node.MINUS, design, e, design.type);
			else if(kind == Node.ASSIGNTIMES)
			   e = new Node(Node.TIMES, design, e, design.type);
			else if(kind == Node.ASSIGNDIV)
			   e = new Node(Node.DIV, design, e, design.type);
			else if(kind == Node.ASSIGNREM)
			   e = new Node(Node.REM, design, e, design.type);
			else if(kind == Node.ASSIGNSHIFTLEFT)
			   e = new Node(Node.SHIFTLEFT, design, e, design.type);
			else if(kind == Node.ASSIGNSHIFTRIGHT)
			   e = new Node(Node.SHIFTRIGHT, design, e, design.type);
			else if(kind == Node.ASSIGNBITAND)
			   e = new Node(Node.BITAND, design, e, design.type);
			else if(kind == Node.ASSIGNBITXOR)
			   e = new Node(Node.BITXOR, design, e, design.type);
			else if(kind == Node.ASSIGNBITOR)
			   e = new Node(Node.BITOR, design, e, design.type);                                   
			// add node
			st = new Node(Node.ASSIGN,design,e,line); 
		} else if (la.kind == 6) {
			e = ActPars();
			if(design.type != Tab.noType)
			   SemErr("only void is allowed");
			                                     // create CALL node
			st = new Node(Node.CALL,e,null,line);
			st.obj = design.obj;
			tab.checkFunctionParams(design.obj,st); 
		} else if (la.kind == 55) {
			Get();
			if(design.kind != Node.IDENT && design.kind != Node.DOT && design.kind != Node.INDEX) 
			   SemErr("name must be an designator");
			if(!design.type.isPrimitive() || design.type == Tab.boolType)
			   SemErr("only a primitive type except boolean is allowed for increment operator");
			e = tab.impliciteTypeCon(new Node(1), design.type);
			e = new Node(Node.PLUS, design, e, design.type);
			st = new Node(Node.ASSIGN,design,e,line); 
		} else if (la.kind == 56) {
			Get();
			if(design.kind != Node.IDENT && design.kind != Node.DOT && design.kind != Node.INDEX) 
			   SemErr("name must be an designator");
			if(!design.type.isPrimitive() || design.type == Tab.boolType)
			   SemErr("only a primitive type except boolean is allowed for decrement operator");
			e = tab.impliciteTypeCon(new Node(1), design.type);
			e = new Node(Node.MINUS, design, e, design.type);
			st = new Node(Node.ASSIGN,design,e,line); 
		} else SynErr(76);
		return st;
	}

	Node  Condition() {
		Node  con;
		Node newCon; 
		con = CondTerm();
		while (la.kind == 32) {
			Get();
			newCon = CondTerm();
			con = new Node(Node.OR, con, newCon, Tab.boolType); 
		}
		return con;
	}

	Node  Designator() {
		Node  n;
		Obj obj; 
		Node e; 
		Struct type; 
		Expect(1);
		String name = t.val;
		obj = tab.find(name);
		if(obj.kind == Obj.CON) {
		   // If Node is constant, insert value directly
		   switch(obj.type.kind) {
		       case Struct.INT:
		           n = new Node((int)obj.val);
		           break;
		       case Struct.FLOAT:
		           n = new Node((float)obj.fVal);
		           break;
		       case Struct.CHAR:
		           n = new Node((char)obj.val);
		           break;
		       case Struct.BOOL:
		           if(obj.val == 0)
		               n = new Node(false);
		           else
		               n = new Node(true);
		           break;
		       case Struct.STRING:
		           n = new Node(strings.get(obj.val));
		           n.val = obj.val;
		           break;
		       default:
		           n = new Node(obj);
		   }
		} else {
		   if(obj.kind == Obj.TYPE)
		       SemErr(name + " is not a constant, variable or function");
		   // if Node is a normal identifier, using that Node
		   n = new Node(obj);
		}
		// set type of identifier
		type = obj.type; 
		while (la.kind == 10 || la.kind == 60) {
			if (la.kind == 60) {
				Get();
				if(type.kind != Struct.STRUCT) 
				   SemErr(name + " is not a struct"); 
				Expect(1);
				obj = tab.findField(t.val,type);
				// update type
				type = obj.type;
				// add Node
				n = new Node(Node.DOT, n, new Node(obj.adr), type); 
			} else {
				Get();
				if(type == null)
				   SemErr("invalide array selector");
				else if(type.kind != Struct.ARR && type.kind != Struct.STRING) 
				   SemErr(name + " is not an array"); 
				e = BinExpr();
				if(e == null || e.type == null || e.type.kind != Struct.INT)
				   SemErr("index must be an int");
				if(type != null) {
				   // the index of an string is returned as a char
				   if(type.kind == Struct.STRING)
				       n = new Node(Node.INDEX, n, e, Tab.charType);
				   else {
				       n = new Node(Node.INDEX, n, e, type.elemType);
				       type = type.elemType;
				   } 
				} 
				Expect(11);
			}
		}
		return n;
	}

	int  AssignOp() {
		int  kind;
		kind=Node.ASSIGN; 
		switch (la.kind) {
		case 13: {
			Get();
			break;
		}
		case 14: {
			Get();
			kind=Node.ASSIGNPLUS; 
			break;
		}
		case 15: {
			Get();
			kind=Node.ASSIGNMINUS; 
			break;
		}
		case 16: {
			Get();
			kind=Node.ASSIGNTIMES; 
			break;
		}
		case 17: {
			Get();
			kind=Node.ASSIGNDIV; 
			break;
		}
		case 18: {
			Get();
			kind=Node.ASSIGNREM; 
			break;
		}
		case 19: {
			Get();
			kind=Node.ASSIGNSHIFTLEFT; 
			break;
		}
		case 20: {
			Get();
			kind=Node.ASSIGNSHIFTRIGHT; 
			break;
		}
		case 21: {
			Get();
			kind=Node.ASSIGNBITAND; 
			break;
		}
		case 22: {
			Get();
			kind=Node.ASSIGNBITXOR; 
			break;
		}
		case 23: {
			Get();
			kind=Node.ASSIGNBITOR; 
			break;
		}
		default: SynErr(77); break;
		}
		return kind;
	}

	Node  ActPars() {
		Node  outPar;
		Node par, curPar = null; 
		outPar = null; 
		Expect(6);
		if (StartOf(5)) {
			outPar = ActPar();
			curPar = outPar; 
			while (la.kind == 40) {
				Get();
				par = ActPar();
				if(curPar == null)
				   SemErr("empty function parameters are not allowed");
				else {
				   curPar.next = par;
				   curPar = par;
				} 
			}
		}
		Expect(7);
		return outPar;
	}

	Node  ActPar() {
		Node  e;
		e = null; 
		e = BinExpr();
		return e;
	}

	Node  CondTerm() {
		Node  con;
		con = CondFact();
		while (la.kind == 31) {
			Get();
			Node con2; 
			con2 = CondFact();
			con = new Node(Node.AND, con, con2, Tab.boolType); 
		}
		return con;
	}

	Node  CondFact() {
		Node  con;
		Node e = null; int kind; 
		con = null; 
		if (isExpr()) {
			con = BinExpr();
			if (StartOf(7)) {
				kind = Relop();
				e = BinExpr();
				if(con == null || e == null || con.type == null || e.type == null)
				   SemErr("please check condition");
				else {
				   if((!con.type.isPrimitive() && con.type != Tab.stringType) || (!e.type.isPrimitive() && e.type != Tab.stringType))
				       SemErr("type is not a primitive or string");
				   else if((con.type == Tab.stringType || e.type == Tab.stringType) && ((con.type == Tab.stringType || con.type == Tab.charType) != (e.type == Tab.stringType || e.type == Tab.charType)))
				       SemErr("you cannot mix primitive and string in condition");
				   
				   con = tab.doImplicitCastByAritmetic(con, con.type, e.type);
				   e = tab.doImplicitCastByAritmetic(e, con.type, e.type);
				}
				con = new Node(kind,con,e,Tab.boolType); 
			}
			if(e == null) {
			   if(con == null || !con.type.isPrimitive())
			       SemErr("type is not a primitive");
			       
			   con = tab.impliciteTypeCon(con, Tab.boolType);
			} 
		} else if (la.kind == 30) {
			Get();
			con = Condition();
			con = new Node(Node.NOT, con, null, Tab.boolType); 
		} else if (la.kind == 6) {
			Get();
			con = Condition();
			Expect(7);
		} else SynErr(78);
		return con;
	}

	int  Relop() {
		int  kind;
		kind = Node.EQL; 
		switch (la.kind) {
		case 24: {
			Get();
			kind = Node.EQL; 
			break;
		}
		case 25: {
			Get();
			kind = Node.NEQ; 
			break;
		}
		case 28: {
			Get();
			kind = Node.GTR; 
			break;
		}
		case 29: {
			Get();
			kind = Node.GEQ; 
			break;
		}
		case 26: {
			Get();
			kind = Node.LSS; 
			break;
		}
		case 27: {
			Get();
			kind = Node.LEQ; 
			break;
		}
		default: SynErr(79); break;
		}
		return kind;
	}

	Node  Shift() {
		Node  res;
		int kind;
		Node n = null; 
		res = Expr();
		while (la.kind == 61 || la.kind == 62) {
			kind = Shiftop();
			n = Expr();
			if(!res.type.isPrimitive() || n == null || !n.type.isPrimitive() || n.type == Tab.boolType)
			   SemErr("type is not a primitive except bool");
			else {
			   res = tab.doImplicitCastByAritmetic(res, res.type, n.type);
			   n = tab.doImplicitCastByAritmetic(n, res.type, n.type);
			   res = new Node(kind, res, n , res.type);
			} 
		}
		return res;
	}

	int  Binop() {
		int  kind;
		kind=Node.BITAND; 
		if (la.kind == 33) {
			Get();
		} else if (la.kind == 35) {
			Get();
			kind=Node.BITXOR; 
		} else if (la.kind == 34) {
			Get();
			kind=Node.BITOR; 
		} else SynErr(80);
		return kind;
	}

	Node  Expr() {
		Node  res;
		int kind;
		Node n = null; 
		res = Term();
		while (la.kind == 57 || la.kind == 58) {
			kind = Addop();
			n = Term();
			if((!res.type.isPrimitive() && res.type !=Tab.stringType) || n==null || (!n.type.isPrimitive() && n.type !=Tab.stringType) || n.type == Tab.boolType)
			   SemErr("type is not a primitive or string except bool");
			else if(res.type == Tab.stringType && n.type == Tab.stringType && kind != Node.PLUS)
			   SemErr("for string operations, only + is allowed");
			else if ((res.type == Tab.stringType || n.type == Tab.stringType) && ((res.type == Tab.stringType || res.type == Tab.charType) != (n.type == Tab.stringType || n.type == Tab.charType)))
			   SemErr("you cannot mix primitive and string in expression");
			else {
			   res = tab.doImplicitCastByAritmetic(res, res.type, n.type);
			   n = tab.doImplicitCastByAritmetic(n, res.type, n.type);
			   res = new Node(kind, res, n , res.type);
			} 
		}
		return res;
	}

	int  Shiftop() {
		int  kind;
		kind=Node.SHIFTLEFT; 
		if (la.kind == 61) {
			Get();
		} else if (la.kind == 62) {
			Get();
			kind=Node.SHIFTRIGHT; 
		} else SynErr(81);
		return kind;
	}

	Node  Term() {
		Node  res;
		int kind; 
		Node n = null; 
		res = Factor();
		while (la.kind == 63 || la.kind == 64 || la.kind == 65) {
			kind = Mulop();
			n = Factor();
			if(!res.type.isPrimitive() || n == null || !n.type.isPrimitive() || n.type == Tab.boolType)
			   SemErr("type is not a primitive except bool");
			else {
			   res = tab.doImplicitCastByAritmetic(res, res.type, n.type);
			   n = tab.doImplicitCastByAritmetic(n, res.type, n.type);
			   res = new Node(kind, res, n, n.type);
			} 
		}
		return res;
	}

	int  Addop() {
		int  kind;
		kind=Node.PLUS; 
		if (la.kind == 58) {
			Get();
		} else if (la.kind == 57) {
			Get();
			kind=Node.MINUS; 
		} else SynErr(82);
		return kind;
	}

	Node  Factor() {
		Node  n;
		Struct type; 
		Node design; 
		n = null; 
		int line = la.line; 
		if (la.kind == 1) {
			design = Designator();
			if (la.kind == 6) {
				n = ActPars();
				if(design.obj == null || design.obj.kind != Obj.PROC )
				   SemErr("name is not a procedure"); 
				else if(design.obj.type == Tab.noType)
				   SemErr("function call of a void procedure"); 
				else {
				   n = new Node(Node.CALL,n,null,line); 
				   n.type = design.obj.type; 
				   n.obj = design.obj;
				   tab.checkFunctionParams(design.obj,n); 
				} 
			}
			if (n == null) {
			   if(design.obj != null && design.obj.kind == Obj.PROC)
			       SemErr("invalide using of procedure"); 
			   n = design; 
			} 
		} else if (la.kind == 2) {
			Get();
			n = new Node(tab.intVal(t.val)); 
		} else if (la.kind == 3) {
			Get();
			n = new Node(tab.floatVal(t.val)); 
		} else if (la.kind == 4) {
			Get();
			n = new Node(tab.charVal(t.val)); 
		} else if (la.kind == 5) {
			Get();
			n = new Node(tab.stringVal(t.val)); 
			n.val = strings.put(tab.stringVal(t.val)); 
		} else if (la.kind == 38 || la.kind == 39) {
			if (la.kind == 38) {
				Get();
				n = new Node(true); 
			} else {
				Get();
				n = new Node(false); 
			}
		} else if (la.kind == 57) {
			Get();
			n = Factor();
			if(n == null || !n.type.isPrimitive() || n.type == Tab.boolType)
			   SemErr("type is not a primitive except bool");
			else
			   n = new Node(Node.MINUS,n,null,n.type); 
		} else if (la.kind == 58) {
			Get();
			n = Factor();
			if(n == null || !n.type.isPrimitive() || n.type == Tab.boolType)
			   SemErr("type is not a primitive except bool");
			else
			   n = new Node(Node.PLUS,n,null,n.type); 
		} else if (la.kind == 59) {
			Get();
			n = Factor();
			if(n == null || !n.type.isPrimitive() || n.type == Tab.boolType)
			   SemErr("type is not a primitive except bool");
			else
			   n = new Node(Node.BITNEQ,n,null,n.type); 
		} else if (isCast()) {
			Expect(6);
			type = Type();
			Expect(7);
			n = Factor();
			n = tab.expliciteTypeCon(n, type); 
		} else if (la.kind == 6) {
			Get();
			n = BinExpr();
			Expect(7);
		} else SynErr(83);
		return n;
	}

	int  Mulop() {
		int  kind;
		kind=Node.TIMES; 
		if (la.kind == 63) {
			Get();
		} else if (la.kind == 64) {
			Get();
			kind=Node.DIV; 
		} else if (la.kind == 65) {
			Get();
			kind=Node.REM; 
		} else SynErr(84);
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
		{T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x,x, x,x,x,T, x,T,T,T, T,x,x,x, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,x,x, x,x,x,x, T,x,x,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,T,T,T, T,x,x,x, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,T,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x}

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
			case 5: s = "stringCon expected"; break;
			case 6: s = "lpar expected"; break;
			case 7: s = "rpar expected"; break;
			case 8: s = "lbrac expected"; break;
			case 9: s = "rbrac expected"; break;
			case 10: s = "lsqu expected"; break;
			case 11: s = "rsqu expected"; break;
			case 12: s = "semicolon expected"; break;
			case 13: s = "assign expected"; break;
			case 14: s = "assignplus expected"; break;
			case 15: s = "assignminus expected"; break;
			case 16: s = "assigntimes expected"; break;
			case 17: s = "assigndiv expected"; break;
			case 18: s = "assignrem expected"; break;
			case 19: s = "assignleftshift expected"; break;
			case 20: s = "assignrightshift expected"; break;
			case 21: s = "assignbitand expected"; break;
			case 22: s = "assignbitxor expected"; break;
			case 23: s = "assignbitor expected"; break;
			case 24: s = "eql expected"; break;
			case 25: s = "neq expected"; break;
			case 26: s = "lss expected"; break;
			case 27: s = "leq expected"; break;
			case 28: s = "gtr expected"; break;
			case 29: s = "geq expected"; break;
			case 30: s = "bang expected"; break;
			case 31: s = "and expected"; break;
			case 32: s = "or expected"; break;
			case 33: s = "bitand expected"; break;
			case 34: s = "bitor expected"; break;
			case 35: s = "bitxor expected"; break;
			case 36: s = "\"const\" expected"; break;
			case 37: s = "\"library\" expected"; break;
			case 38: s = "\"true\" expected"; break;
			case 39: s = "\"false\" expected"; break;
			case 40: s = "\",\" expected"; break;
			case 41: s = "\"struct\" expected"; break;
			case 42: s = "\"void\" expected"; break;
			case 43: s = "\"if\" expected"; break;
			case 44: s = "\"else\" expected"; break;
			case 45: s = "\"while\" expected"; break;
			case 46: s = "\"do\" expected"; break;
			case 47: s = "\"for\" expected"; break;
			case 48: s = "\"switch\" expected"; break;
			case 49: s = "\"case\" expected"; break;
			case 50: s = "\"default\" expected"; break;
			case 51: s = "\":\" expected"; break;
			case 52: s = "\"return\" expected"; break;
			case 53: s = "\"break\" expected"; break;
			case 54: s = "\"continue\" expected"; break;
			case 55: s = "\"++\" expected"; break;
			case 56: s = "\"--\" expected"; break;
			case 57: s = "\"-\" expected"; break;
			case 58: s = "\"+\" expected"; break;
			case 59: s = "\"~\" expected"; break;
			case 60: s = "\".\" expected"; break;
			case 61: s = "\"<<\" expected"; break;
			case 62: s = "\">>\" expected"; break;
			case 63: s = "\"*\" expected"; break;
			case 64: s = "\"/\" expected"; break;
			case 65: s = "\"%\" expected"; break;
			case 66: s = "??? expected"; break;
			case 67: s = "this symbol not expected in CMM"; break;
			case 68: s = "invalid ConstDecl"; break;
			case 69: s = "invalid ProcDecl"; break;
			case 70: s = "invalid ProcDecl"; break;
			case 71: s = "invalid VarDeclPart"; break;
			case 72: s = "invalid Statement"; break;
			case 73: s = "invalid Statement"; break;
			case 74: s = "invalid Statement"; break;
			case 75: s = "invalid Statement"; break;
			case 76: s = "invalid Command"; break;
			case 77: s = "invalid AssignOp"; break;
			case 78: s = "invalid CondFact"; break;
			case 79: s = "invalid Relop"; break;
			case 80: s = "invalid Binop"; break;
			case 81: s = "invalid Shiftop"; break;
			case 82: s = "invalid Addop"; break;
			case 83: s = "invalid Factor"; break;
			case 84: s = "invalid Mulop"; break;
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
