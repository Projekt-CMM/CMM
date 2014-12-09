package at.jku.ssw.cmm.compiler;

/*--------------------------------------------------------------------------------
Tab   Symbol table for C--
===   ====================
The symbol table is a stack of scopes
- universe: contains predeclared names
- global scope: contains the globally declared names
- local scope: contains the local names of a procedure

The symbol table has methods for
- opening and closing scopes
- inserting and retrieving named objects
- checking of forward declarations
- utilities for converting strings to constants
--------------------------------------------------------------------------------*/

public class Tab {
	public Scope curScope;	         // current scope
	public int   curLevel;	         // nesting level of current scope

	public static Struct intType;    // predefined types
	public static Struct floatType;
	public static Struct charType;
	public static Struct boolType;
	public static Struct stringType;
	public static Struct noType;
	public static Obj noObj;		     // predefined objects
	public static Obj printProc;
	public static Obj printfProc;
	public static Obj readProc;
	public static Obj lengthProc;
	public static Obj timeProc;
	public static Obj __is_def_bool__Proc;
	public static Obj __is_def_int__Proc;
	public static Obj __is_def_float__Proc;
	public static Obj __is_def_char__Proc;
	public static Obj __is_def_string__Proc;
	
	private Parser parser;           // for error messages

	//------------------ scope management ---------------------

	/**
	 * open a new Scope
	 */
	public void openScope() {
		Scope s = new Scope();
		s.outer = curScope;
		curScope = s;
		curLevel++;
	}

	/**
	 * close the current Scope
	 */
	public void closeScope() {
		curScope = curScope.outer;
		curLevel--;
	}

	//------------- Object insertion and retrieval --------------

	/**
	 *  Create a new object with the given kind, name and type 
	 *  and insert it into the current scope.
	 *  
	 *  @param kind kind of object
	 *  @param name name of object
	 *  @param type type of object
	 *  
	 *  @return return created object or forward declared procedure
	 */
	public Obj insert(int kind, String name, Struct type, int line) {
		//--- create
		Obj obj = new Obj(kind, name, type, line);
		if (kind == Obj.VAR) {
			obj.adr = curScope.size;
			curScope.size += type.size;
			obj.level = curLevel;
		}
		
		//--- check if function or type already exists in universe scope
		if(kind == Obj.PROC || kind == Obj.TYPE) {
			Scope checkScope = curScope;
			while(checkScope.outer != null) {
				checkScope = checkScope.outer;
				Obj p =  checkScope.locals;
				while (p != null) {
					if (p.name.equals(name)) {
						if(p.isForward) {
							return p;
						}
						parser.SemErr(name + " declared twice");
					}
					p = p.next;
				}
			}
		}
		//--- insert
		Obj p = curScope.locals, last = null;
		while (p != null) {
			if (p.name.equals(name)) {
				if(p.isForward) {
					return p;
				}
				parser.SemErr(name + " declared twice");
			}
			last = p;
			p = p.next;
		}
		if (last == null)
			curScope.locals = obj;
		else
			last.next = obj;
		return obj;
	}

	/** 
	 * Look up the object with the given name in all open scopes.
	 * Report an error if not found.
	 * 
	 * @param name name of object
	 * 
	 * @return return found object or tab.noObj if no object is found
	 */
	public Obj find(String name) {
		for(Scope scp = curScope; scp != null; scp = scp.outer) {
			for (Obj p = scp.locals; p != null ; p = p.next) {
				if (p.name.equals(name))
					return p;
			}
		}
		parser.SemErr(name + " not found");
		return noObj;
	}

	/**
	 * Retrieve a struct field with the given name from the fields of "type"
	 * 
	 * @param name name of field
	 * @param type type of struct
	 * 
	 * @return return field or tab.noObj if no field is found
	 */
	public Obj findField(String name, Struct type) {
		if(type.kind != Struct.STRUCT) {
			parser.SemErr(name + " is not in a struct");
			return noObj;
		}
		
		for (Obj f = type.fields; f != null ; f = f.next) {
			if (f.name.equals(name))
				return f;
		}
		parser.SemErr(name + " not found");
		return noObj;
	}

	/**
	 * Look up the object with the given name in the current scope.
	 *  
	 * @param name name of object
	 * 
	 * @return return noObj if not found.
	 */
	public Obj lookup(String name) {
		for (Obj p = curScope.locals; p != null ; p = p.next) {
			if (p.name.equals(name))
				return p;
		}
		parser.SemErr(name + " not found in current scope");
		return noObj;
	}
	
	/**
	 * Check if type is declared in current scope
	 *  
	 * @param checkType type of object
	 * 
	 * @return return true if found.
	 */
	public boolean lookupType(Struct checkType) {
		for (Obj p = curScope.locals; p != null ; p = p.next) {
			if (p.type == checkType)
				return true;
		}
		return false;
	}

	//----------------- handling of forward declaration  -----------------

	/**
	 * Check if parameters of forward declaration and actual declaration match
	 * 
	 * @param oldPar first old parameter
	 * @param newPar first new parameter
	 */
	public void checkForwardParams(Obj oldPar, Obj newPar) {
		while(oldPar != null && newPar != null) {
			if(oldPar.type.kind != newPar.type.kind) {
				parser.SemErr("parameter of function and forward declaration has not the same type");
			} else if(oldPar.type.kind == Struct.ARR) {
				Struct oldParElemType = oldPar.type.elemType;
				Struct newParElemType = newPar.type.elemType;

				while(oldParElemType.kind == Struct.ARR && newParElemType.kind == Struct.ARR) {
					oldParElemType = oldParElemType.elemType;
					newParElemType = newParElemType.elemType;
				}
				if(oldParElemType.kind == Struct.ARR || newParElemType.kind == Struct.ARR) {
					parser.SemErr("array parameter of function and forward declaration have not the same dimenstions");
				} else if(oldParElemType.kind != newParElemType.kind) {
					parser.SemErr("parameter of function and forward declaration has not the same type");
				}
				if(oldPar.type.elemType.kind == Struct.STRUCT) {
					parser.SemErr("struct array as parameter is not allowed");
				}
				
			}
			if(oldPar.isRef != newPar.isRef) {
				parser.SemErr("parameter of function or forward declaration is ref, in the other not");
			}
			oldPar = oldPar.next;
			newPar = newPar.next;
		}
		if(oldPar != null) {
			parser.SemErr("forward-declaration of function has more parameter as function itself");
		}
		if(newPar != null) {
			parser.SemErr("function has more parameters as forward-declaration declare");
		}
	}

	/**
	 * Check if all forward declarations were resolved at the end of the program
	 * 
	 * @param scope scope to check the declarations (normaly global scope)
	 */
	public void checkIfForwardsResolved(Scope scope) {
		for(Obj f = scope.locals; f != null; f = f.next) {
			if(f.isForward) {
				parser.SemErr(f.name + " has only a forward-declaration");
			}
		}
	}

	//---------------- conversion of strings to constants ----------------

	/**
	 * Convert a digit string into an int
	 * 
	 * @param s string which would be converted
	 * 
	 * @return return converted integer
	 */
	public int intVal(String s) {
		try {
			// convert decimal, hex and octal string into integer
			return Integer.decode(s);
		}
		catch ( java.lang.NumberFormatException e) {
			parser.SemErr(s + " is not an integer");
			return 0;
		}
	}

	/**
	 * Convert a string representation of a float constant into a float value
	 * 
	 * @param s string which would be converted
	 * 
	 * @return return converted float
	 */
	public float floatVal(String s) {
		try {
			return Float.parseFloat(s);
		}
		catch ( java.lang.NumberFormatException e) {
			parser.SemErr(s + " is not a float");
			return 0;
		}
	}
	
	/**
	 * Convert a string representation of a char constant into a char value
	 * 
	 * @param s string which would be converted
	 * 
	 * @return return converted char
	 */
	public char charVal(String s) {
		if(s.matches("^'.'$")) {
			return  s.charAt(1);
		} else {
			//--- s.charAt(1) == '\' so I parse the second character to get it usage
			switch(s.charAt(2))
			{
				case 'r':
					return '\r';
				case 'n':
					return '\n';
				case 't':
					return '\t';
				case '0':
					return '\0';
				case '\'':
					return '\'';
				case '\\':
					return '\\';
				default:
					parser.SemErr("unknow character");
					return 	'\0';
			}
		}
	}

	/**
	 * Convert a string representation of a string constant into a string value
	 * 
	 * @param s string which would be converted
	 * 
	 * @return return converted string
	 */
	public String stringVal(String s) {
		if(s.matches("^\".*\"$")) {
			s = s.substring(1, s.length()-1);
		} else 
			parser.SemErr(s + " string doesn't have \" at start end endpoint");
		String returnStr = new String();
		while(s.length() != 0) {
			if(s.charAt(0) != '\\') {
				returnStr += s.charAt(0);
				s = s.substring(1); // remove 1 letter
			} else if(s.length() >= 2) {
				switch(s.charAt(1))
				{
					case 'r':
						returnStr += '\r';
						break;
					case 'n':
						returnStr += '\n';
						break;
					case 't':
						returnStr += '\t';
						break;
					case '0':
						returnStr += '\0';
						break;
					case '\"':
						returnStr += '"';
						break;
					case '\\':
						returnStr += '\\';
						break;
				default:
						break;
				}
				s = s.substring(2);	// remove 2 letters
			} else {
				parser.SemErr(s + " no single \\ at the end of a string allowed");
			}
		}
		return returnStr;
	}
	
	//---------------- implicit and explicit type conversation and check -----------
	
	/**
	 * Return true if operator is a type-cast
	 * 
	 * @param kind id of operation
	 * 
	 * @return true if operation is a cast
	 */
	public boolean isCastOperator(int kind) {
		switch(kind) {
			case Node.I2F:
			case Node.F2I:
			case Node.I2C:
			case Node.C2I:
			case Node.A2S:
			case Node.C2S:
			case Node.B2I:
			case Node.I2B:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * check function parameter and do implicite type conversation if needed
	 * 
	 * @param declFunction declared function
	 * @param buildFunction your function with call-parameters
	 */
	public void checkFunctionParams(Obj declFunction, Node buildFunction) {
		if(declFunction == null) {
			parser.SemErr("no function declared");
			return;
		}
		Obj declObj = declFunction.locals;
		Node buildNode = buildFunction.left;
		Node oldNode = buildNode;
		for(int i = 0; (declFunction.nPars > 0 && i < declFunction.nPars) || (declFunction.nPars < 0 && i*-1 > declFunction.nPars);i++) {

			if(declObj == null) {
				parser.SemErr("parameter not declared");
				return;
			}
			
			if(buildNode == null) {
				parser.SemErr("declaration of function has more parameter as using of itself");
				break;
			}
			
			// edit function parameter to ref, if it is declared as ref
			if(declObj.isRef == true) {
				Node newNode = new Node(Node.REF, buildNode, null, declObj.type);
				newNode.next = buildNode.next;
				if(newNode != buildNode) {
					buildNode.next = null;
				}
				if(buildFunction.left == buildNode) {
					buildNode = newNode;
					buildFunction.left = newNode;
				} else {
					buildNode = newNode;
					oldNode.next = newNode;
				}
			}
			
			// implicit type conversation
			if(declObj.isRef == true && buildNode.kind == Node.REF) {
				if(buildNode.left.type.kind != Struct.ARR &&  buildNode.left.type != declObj.type)
					parser.SemErr("there is no type-conversation for ref-parameter(s) allowed");
				if(declObj.type.kind == Struct.ARR) {
					Struct declObjHelp = declObj.type;
					Struct buildObjHelp = buildNode.left.type;
					while(declObjHelp != null && declObjHelp.kind == Struct.ARR && 
						  buildObjHelp != null && buildObjHelp.kind == Struct.ARR) {
						declObjHelp = declObjHelp.elemType;
						buildObjHelp = buildObjHelp.elemType;
					}
					if(declObjHelp.kind == Struct.ARR || buildObjHelp.kind == Struct.ARR)
						parser.SemErr("the number of dimensions in the declaration isn't the same as in the call");
					else if(declObjHelp != buildObjHelp)
						parser.SemErr("there is no type-conversation for arrays allowed");
				}
				//buildNode.left = impliciteTypeCon(buildNode.left, declObj.type);
			} else {
				Node newNode = impliciteTypeCon(buildNode, declObj.type);
				newNode.next = buildNode.next;
				if(newNode != buildNode) {
					buildNode.next = null;
				}
				if(buildFunction.left == buildNode) {					
					buildNode = newNode;
					buildFunction.left = buildNode;
				} else {
					buildNode = newNode;
					oldNode.next = newNode;
				}
			}
			
			declObj = declObj.next;
			oldNode = buildNode;
			buildNode = buildNode.next;
		}
		if(buildNode != null && !(declFunction.nPars < 0)) {
			parser.SemErr("using of function has more parameters as the declaration");
		}
	}
	
	/**
	 * make implicit type conversation if possible
	 * 
	 * @param element current Node
	 * @param type type which is to convert
	 * 
	 * @return converted type or make SemError
	 */
	public Node impliciteTypeCon(Node element, Struct type) {
		if(element == null) {
			parser.SemErr("cast from null to " +  getNameOfType(type) + " not allowed");
			return element;
		} else if(type == element.type) 
			return element;
		else if(type == Tab.intType && element.type == Tab.floatType) 
			return new Node(Node.F2I, element, null, Tab.intType);
		else if(type == Tab.floatType && element.type == Tab.intType) 
			return new Node(Node.I2F, element, null, Tab.floatType);
		else if(type.kind == Struct.INT && element.type == Tab.charType)
			return new Node(Node.C2I, element, null, Tab.intType);
		else if(type.kind == Struct.INT && element.type == Tab.boolType)
			return new Node(Node.B2I, element, null, Tab.intType);
		else if(type.kind == Struct.BOOL && element.type == Tab.intType)
			return new Node(Node.I2B, element, null, Tab.boolType); 
		else if(type == Tab.floatType && element.type == Tab.charType) {
			element =new Node(Node.C2I, element, null, Tab.intType);
			return new Node(Node.I2F, element, null, Tab.floatType);
		} else if(type == Tab.stringType && element.type == Tab.charType) {
			return new Node(Node.C2S, element, null, Tab.stringType);
		} else parser.SemErr("no known cast from " + getNameOfType(element.type) + " to " + getNameOfType(type));
			return element;
	}
	
	/**
	 * make explicit type conversation if possible
	 * 
	 * @param element current Node
	 * @param type type which is to convert
	 * 
	 * @return converted type or make SemError
	 */
	public Node expliciteTypeCon(Node element, Struct type) {
		if(element == null) {
			parser.SemErr("cast from null to " +  getNameOfType(type) + " not allowed");
			return element;
		} else if(type == Tab.charType && element.type == Tab.floatType) {
			element = new Node(Node.F2I, element, null, Tab.intType);
			return new Node(Node.I2C, element, null, Tab.charType);
		} else if(type == Tab.charType && element.type == Tab.intType) 
			return new Node(Node.I2C, element, null, Tab.charType);
		else if(type == Tab.stringType && element.type.kind == Struct.ARR && element.type.elemType == Tab.charType) 
			return new Node(Node.A2S, element, null, Tab.stringType);
		else {
			return impliciteTypeCon(element, type);
		}
	}
	
	/**
	 * do implicite cast if necessary for aritmetic operation
	 * 
	 * @param element Element on which the conversation work
	 * @param type1 one datatype of the aritmetic operation
	 * @param type2 other datatype of the aritmetic operation
	 * 
	 * @return implicite cast element or original element
	 */
	public Node doImplicitCastByAritmetic(Node element, Struct type1, Struct type2) {
		if(type1 == null || type2==null)
			parser.SemErr("one aritmetic operator is null");
		else if(type1 != type2) {
			if(type1.size == type2.size) {
				if(type1 == Tab.floatType || type2 == Tab.floatType) {
					element = impliciteTypeCon(element, Tab.floatType);
				} else if(type1 == Tab.intType || type2 == Tab.intType) {
					element = impliciteTypeCon(element, Tab.intType);
				} else if(type1 == Tab.charType || type2 == Tab.charType) {
					// char must be converted to int
					element = impliciteTypeCon(element, Tab.intType);
				}
			}
			else if(type1.size > type2.size) {
				element = impliciteTypeCon(element, type1);
			} else {
				element = impliciteTypeCon(element, type2);
			}
		} else if(type1 == Tab.charType) {
			element = impliciteTypeCon(element, Tab.intType);
		}
		return element;
	}
	
	//---------------- methods for dumping the symbol table --------------

	/**
	 * Return the name of a type
	 * 
	 * @param type type of which we would get the name
	 * @return name of type
	 */
	public String getNameOfType(Struct type) {
		if(type == null) return "null";
		switch(type.kind) {
			case Struct.NONE:
				return "void";
			case Struct.INT:
				return "int";
			case Struct.FLOAT:
				return "float";
			case Struct.CHAR:
				return "char";
			case Struct.BOOL:
				return "bool";
			case Struct.ARR:
				return "arr";
			case Struct.STRUCT:
				return "struct";
			case Struct.STRING:
				return "string";
			default:
				return "unkow";
		}
	}
	
	/**
	 * Print a type
	 * 
	 * @param type
	 * @param indent
	 */
	public void dumpStruct(Struct type, int indent) {
		switch (type.kind) {
			case Struct.INT:
			  System.out.print("Int(" + type.size + ")"); break;
			case Struct.FLOAT:
			  System.out.print("Float(" + type.size + ")"); break;
			case Struct.CHAR:
			  System.out.print("Char(" + type.size + ")"); break;
			case Struct.BOOL:
				System.out.print("Bool(" + type.size + ")"); break;
			case Struct.STRING:
				System.out.print("String(" + type.size + ")"); break;
			case Struct.ARR:
			  System.out.print("Arr[" + type.elements + "(" + type.size + ")] of ");
			  dumpStruct(type.elemType, indent);
			  break;
			case Struct.STRUCT:
			  System.out.println("Struct(" + type.size + ") {");
			  for (Obj o = type.fields; o != null; o = o.next) dumpObj(o, indent + 1);
			  for (int i = 0; i < indent; i++) System.out.print("  ");
			  System.out.print("}");
			  break;
			default:
			  System.out.print("None"); break;
		}
	}

	/**
	 * Print an object
	 * 
	 * @param o
	 * @param indent
	 */
	public void dumpObj(Obj o, int indent) {
		for (int i = 0; i < indent; i++) System.out.print("  ");
		switch (o.kind) {
			case Obj.CON:
			  System.out.print("Con " + o.name);
			  if (o.type == Tab.floatType) 
				  System.out.print(" fVal=" + o.fVal + " library=" + o.library + " decLine=" + o.line);
			  else if (o.type == Tab.stringType) 
				  System.out.print(" val=" + o.val + " library=" + o.library + " string=\"" + parser.strings.get(o.val) + "\"" + " decLine=" + o.line);
			  else 
				  System.out.print(" val=" + o.val + " library=" + o.library + " decLine=" + o.line);
			  break;
			case Obj.VAR:
			  System.out.print("Var " + o.name + " adr=" + o.adr + " level=" + o.level + " library=" + o.library + " decLine=" + o.line);
			  if (o.isRef) System.out.print(" isRef");
			  break;
			case Obj.TYPE:
			  System.out.print("Type " + o.name);
			  break;
			case Obj.PROC:
			  System.out.println("Proc " + o.name + " size=" + o.size + " nPars=" + o.nPars + " library=" + o.library + " isForw=" + o.isForward + " {");
			  dumpScope(o.locals, indent + 1);
			  System.out.print("}");
			  break;
			default:
			  System.out.print("None " + o.name);
			  break;
		}
		System.out.print(" ");
		dumpStruct(o.type, indent);
		System.out.println();
	}

	/**
	 * Print all objects of a scope
	 *  
	 * @param head
	 * @param indent
	 */
	public void dumpScope(Obj head, int indent) {
		for (Obj o = head; o != null; o = o.next) dumpObj(o, indent);
	}

	//-------------- initialization of the symbol table ------------

	public Tab(Parser parser) {
		this.parser = parser;
		curScope = new Scope();
		curScope.outer = null;
		curLevel = -1;

		// create predeclared types
		intType   = new Struct(Struct.INT);
		floatType = new Struct(Struct.FLOAT);
		charType  = new Struct(Struct.CHAR);
		boolType  = new Struct(Struct.BOOL);
		stringType= new Struct(Struct.STRING);
		noType    = new Struct(Struct.NONE);
		noObj     = new Obj(Obj.VAR, "???", noType, -1);
		
		// insert predeclared types into universe
		insert(Obj.TYPE, "bool", boolType, -1);
		insert(Obj.TYPE, "int", intType, -1);
		insert(Obj.TYPE, "float", floatType, -1);
		insert(Obj.TYPE, "char", charType, -1);
		insert(Obj.TYPE, "string", stringType, -1);
		
		// insert predeclared constants into universe, TODO required and useful?
		insert(Obj.CON, "true", boolType, -1).val = 1;
		insert(Obj.CON, "false", boolType, -1).val = 0;
		
		// declare important-functions in universe
		printProc = insert(Obj.PROC, "print", noType, -1);
		printProc.locals = new Obj(Obj.VAR,"character",charType, -1);
		printProc.size = charType.size;
		printProc.nPars = 1;
		
		printfProc = insert(Obj.PROC, "printf", noType, -1);
		printfProc.locals = new Obj(Obj.VAR,"character",stringType, -1);
		printfProc.size = stringType.size;
		printfProc.nPars = -1;	// because procedure require 1 or more parameters
		
		readProc = insert(Obj.PROC, "read", charType, -1);
		
		lengthProc = insert(Obj.PROC, "length", intType, -1);
		lengthProc.locals = new Obj(Obj.VAR,"string",stringType, -1);
		lengthProc.size = stringType.size;
		lengthProc.nPars = 1;
		
		timeProc = insert(Obj.PROC, "time", intType, -1);
		
		__is_def_bool__Proc = insert(Obj.PROC, "__is_def_bool__", boolType, -1);
		__is_def_bool__Proc.locals = new Obj(Obj.VAR,"__bool__",boolType, -1);
		__is_def_bool__Proc.size = boolType.size;
		__is_def_bool__Proc.nPars = 1;
		
		__is_def_int__Proc = insert(Obj.PROC, "__is_def_int__", boolType, -1);
		__is_def_int__Proc.locals = new Obj(Obj.VAR,"__int__",intType, -1);
		__is_def_int__Proc.size = intType.size;
		__is_def_int__Proc.nPars = 1;
		
		__is_def_float__Proc = insert(Obj.PROC, "__is_def_float__", boolType, -1);
		__is_def_float__Proc.locals = new Obj(Obj.VAR,"__float__",floatType, -1);
		__is_def_float__Proc.size = floatType.size;
		__is_def_float__Proc.nPars = 1;
		
		__is_def_char__Proc = insert(Obj.PROC, "__is_def_char__", boolType, -1);
		__is_def_char__Proc.locals = new Obj(Obj.VAR,"__char__",charType, -1);
		__is_def_char__Proc.size = charType.size;
		__is_def_char__Proc.nPars = 1;
		
		__is_def_string__Proc = insert(Obj.PROC, "__is_def_string__", boolType, -1);
		__is_def_string__Proc.locals = new Obj(Obj.VAR,"__string__",stringType, -1);
		__is_def_string__Proc.size = stringType.size;
		__is_def_string__Proc.nPars = 1;
	}
}
