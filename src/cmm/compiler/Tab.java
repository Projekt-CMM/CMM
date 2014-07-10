package cmm.compiler;

import java.util.regex.*;

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
	public static Struct noType;
	public static Obj noObj;		     // predefined objects

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
	public Obj insert(int kind, String name, Struct type) {
		//--- erzeugen
		Obj obj = new Obj(kind, name, type);
		if (kind == Obj.VAR) {
			obj.adr = curScope.size;
			curScope.size += type.size;
			obj.level = curLevel;
		}
		//--- einfügen
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
			// TODO
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

	//----------------- handling of forward declaration  -----------------

	/**
	 * Check if parameters of forward declaration and actual declaration match
	 * 
	 * @param oldPar first old parameter
	 * @param newPar first new parameter
	 */
	public void checkForwardParams(Obj oldPar, Obj newPar) {
		while(oldPar != null && newPar != null) {
			if(oldPar.type != newPar.type) {
				parser.SemErr("parameter of function and forward declaration has not the same type");
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
				case '\'':
					return '\'';
				case '\\':
					return '\\';
			default:
					return 	'\0';
			}
		}
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
		Obj declObj = declFunction.locals;
		Node buildNode = buildFunction.left;
		
		for(int i = 0; i < declFunction.nPars;i++) {
		
			while(declObj != null && buildNode != null) {
				// check if ref is correct declared
				if(declObj.isRef == true && buildNode.kind != Node.REF) {
					parser.SemErr("parameter of function is declared as ref, but not in the call");
				}else if(declObj.isRef == false && buildNode.kind == Node.REF) {
					parser.SemErr("parameter of function is not declared as ref, but in the call");
				}
				
				// implicit type conversation
				if(declObj.isRef == true && buildNode.kind == Node.REF) {
					if(buildNode.left.type != declObj.type)
						parser.SemErr("there is no type-conversation for ref-parameter(s) allowed");
					//buildNode.left = impliciteTypeCon(buildNode.left, declObj.type);
				} else {
					if(buildFunction.left == buildNode) {					
						Node newNode = impliciteTypeCon(buildNode, declObj.type);
						newNode.next = buildNode.next;
						buildNode = newNode;
						buildFunction.left = buildNode;
					} else {
						Node newNode = impliciteTypeCon(buildNode, declObj.type);
						newNode.next = buildNode.next;
						buildNode = newNode;
					}
				}
				
				declObj = declObj.next;
				buildNode = buildNode.next;
			}
			if(declObj != null) {
				parser.SemErr("declaration of function has more parameter as using of itself");
			}
		}
		if(buildNode != null) {
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
		if(type == element.type) 
			return element;
		else if(type == Tab.intType && element.type == Tab.floatType) 
			return new Node(Node.F2I, element, null, Tab.intType);
		else if(type == Tab.floatType && element.type == Tab.intType) 
			return new Node(Node.I2F, element, null, Tab.floatType);
		else if(type.kind == Struct.INT && element.type == Tab.charType)
			return new Node(Node.C2I, element, null, Tab.intType); 
		else if(type == Tab.charType && element.type == Tab.intType) 
			return new Node(Node.I2C, element, null, Tab.charType);
		else if(type == Tab.floatType && element.type == Tab.charType) {
			element =new Node(Node.C2I, element, null, Tab.intType);
			return new Node(Node.I2F, element, null, Tab.floatType);
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
		if(type == Tab.charType && element.type == Tab.floatType) {
			element = new Node(Node.F2I, element, null, Tab.intType);
			return new Node(Node.I2C, element, null, Tab.charType);
		} else {
			return impliciteTypeCon(element, type);
		}
	}
	
	public Node doImplicitCastByAritmetic(Node element, Struct type1, Struct type2) {
		if(type1 != type2) {
			if(type1.size > type2.size) {
				element = impliciteTypeCon(element, type1);
			} else {
				element = impliciteTypeCon(element, type2);
			}
		} else if(type1 == Tab.charType) {
			element = impliciteTypeCon(element, Tab.intType);	// TODO warum?
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
			  if (o.type == Tab.floatType) System.out.print(" fVal=" + o.fVal);
			  else System.out.print(" val=" + o.val);
			  break;
			case Obj.VAR:
			  System.out.print("Var " + o.name + " adr=" + o.adr + " level=" + o.level);
			  if (o.isRef) System.out.print(" isRef");
			  break;
			case Obj.TYPE:
			  System.out.print("Type " + o.name);
			  break;
			case Obj.PROC:
			  System.out.println("Proc " + o.name + " size=" + o.size + " nPars=" + o.nPars + " isForw=" + o.isForward + " {");
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
		Obj o;
		this.parser = parser;
		curScope = new Scope();
		curScope.outer = null;
		curLevel = -1;

		// create predeclared types
		intType   = new Struct(Struct.INT);
		floatType = new Struct(Struct.FLOAT);
		charType  = new Struct(Struct.CHAR);
		boolType  = new Struct(Struct.BOOL);
		noType    = new Struct(Struct.NONE);
		noObj     = new Obj(Obj.VAR, "???", noType);

		// insert predeclared types into universe
		insert(Obj.TYPE, "int", intType);
		insert(Obj.TYPE, "float", floatType);
		insert(Obj.TYPE, "char", charType);
	}
}
