package at.jku.ssw.cmm.interpreter;

/*
 * General Info:
 * TODO: JUnit
 */

import java.nio.BufferOverflowException;

import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.compiler.Struct;
import at.jku.ssw.cmm.compiler.Strings;
import at.jku.ssw.cmm.debugger.Debugger;
import at.jku.ssw.cmm.debugger.StdInOut;
import at.jku.ssw.cmm.interpreter.exceptions.AbortException;
import at.jku.ssw.cmm.interpreter.exceptions.BreakException;
import at.jku.ssw.cmm.interpreter.exceptions.ContinueException;
import at.jku.ssw.cmm.interpreter.exceptions.ReturnException;
import at.jku.ssw.cmm.interpreter.exceptions.StackOverflowException;
import at.jku.ssw.cmm.interpreter.exceptions.StackUnderflowException;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.interpreter.memory.MethodContainer;
import at.jku.ssw.cmm.compiler.Node;

public final class Interpreter {

	private final Debugger debugger;
	private final Strings strings;
	private final StdInOut inout;

	// private boolean running;

	/*
	 * Function for starting the Debugger
	 */
	public Interpreter(Debugger debugger, StdInOut stdInout, Strings strings) {
		this.debugger = debugger;
		this.strings = strings;
		this.inout = stdInout;
	}

	/*
	 * Start Function
	 */
	public void run(Node node) {
		// TODO try catch abort messages
		try {
			StatSeq(node);
		} catch (ReturnException e) {
			return;
		} catch (AbortException e) {
			return;
		} catch(BreakException e) {
			return;
		} catch(ContinueException e) {
			return;
		}
	}

	/*
	 * StartSequenz, Working Module
	 */
	void StatSeq(Node p) throws ReturnException, AbortException, BreakException, ContinueException { // AST
		for (p = p.left; p != null; p = p.next)
			Statement(p);
	}

	/*
	 * Statements: assign, stateseq, if, ifelse, while, return, trap, call TODO
	 */
	void Statement(Node p) throws ReturnException, AbortException, BreakException, ContinueException { // b = a;
		if (!debugger.step(p))
			throw new AbortException();

		switch (p.kind) {
		case Node.ASSIGN:
			switch (p.right.type.kind) {
			case Struct.BOOL:
				Memory.storeBool(Adr(p.left), BoolExpr(p.right));
				break;
			case Struct.INT:
				Memory.storeInt(Adr(p.left), IntExpr(p.right));
				break;
			case Struct.CHAR:
				Memory.storeChar(Adr(p.left), CharExpr(p.right));
				break;
			case Struct.FLOAT:
				Memory.storeFloat(Adr(p.left), FloatExpr(p.right));
				break;
			case Struct.STRING:
				Memory.storeStringAdress(Adr(p.left), StringExpr(p.right));
				break;
			default:
				debugger.abort("Not supportet node kind", p);
				throw new IllegalStateException("Kind" + p.kind);

			}
			break;
		case Node.CALL:
			Call(p);
			break;
		case Node.STATSEQ:
			StatSeq(p);
			break;
		case Node.TRAP: // For a Function with return!
			debugger.abort("Return Statement missing", p);
			throw new IllegalStateException("Kind" + p.kind); 

		case Node.IF:
			if (Condition(p.left))
				Statement(p.right);
			break;
		case Node.IFELSE:
			if (Condition(p.left.left))
				Statement(p.left.right);
			else
				Statement(p.right);
			break;
		case Node.WHILE:
			try {
				while (Condition(p.left)) {
					try {
						Statement(p.right);
					} catch(ContinueException e) {
					}
				}
			} catch(BreakException e) {
			}
			break;
		case Node.DOWHILE:
			try {
				do
					try {
						Statement(p.right);
					} catch(ContinueException e) {
					}
				while (Condition(p.left));
			} catch(BreakException e) {	
			}
			break;
		case Node.RETURN:
			if(p.left == null)
				throw new ReturnException();

			switch (p.left.type.kind) {
			case Struct.BOOL:
				Memory.setBoolReturnValue(BoolExpr(p.left));
				throw new ReturnException();
			case Struct.INT:
				Memory.setIntReturnValue(IntExpr(p.left));
				throw new ReturnException();
			case Struct.CHAR:
				Memory.setCharReturnValue(CharExpr(p.left));
				throw new ReturnException();
			case Struct.FLOAT:
				Memory.setFloatReturnValue(FloatExpr(p.left));
				throw new ReturnException(); // Exception for jumping out
			case Struct.STRING:
				Memory.setIntReturnValue(StringExpr(p.left));
				throw new ReturnException();
			default:
				debugger.abort("Not supportet return node kind", p);
				throw new IllegalStateException("Kind" + p.kind);
			}
		case Node.BREAK:
				throw new BreakException();
		case Node.CONTINUE:
				throw new ContinueException();
		default:
			debugger.abort("Not supportet statement node kind", p);
			throw new IllegalStateException("Kind" + p.kind);
		}
	}
	/*
	 * BoolExpr: boolcon, call, ref, i2b,
	 * ident, dot, index
	 */
	boolean BoolExpr(Node p) throws ReturnException, AbortException { //TODO
		switch (p.kind) {
		case Node.BOOLCON:
			// Returns a Constante
			if(p.val == 0)
				return false;
			else
				return true;

		/*
		 * Bit Operators
		 */
		case Node.BITAND:
			return BoolExpr(p.left) & BoolExpr(p.right);
		case Node.BITOR:
			return BoolExpr(p.left) | BoolExpr(p.right);
		case Node.BITXOR:
			return BoolExpr(p.left) ^ BoolExpr(p.right);
			
		case Node.CALL:								//Opens new Integer c-- Function
			Call(p);
			return Memory.getBoolReturnValue();		//getting return Value						
			
		case Node.I2B:
			int retIntExpr = IntExpr(p.left); //casting Integer to Bool
			if(retIntExpr == 0)
				return false;
			else
				return true;
		case Node.IDENT:							//more at @Adr
			return Memory.loadBool(IdentAdr(p.obj));
		case Node.DOT:								//more at @Adr
			return Memory.loadBool(Adr(p));			
		case Node.INDEX:							//more at @Adr
			return Memory.loadBool(Adr(p));
		default:
			debugger.abort("Not supportet boolexpr node kind", p);
			throw new IllegalStateException("Kind" + p.kind);
		}
	}
	
	/*
	 * IntExpr: intcon, plus, minus, times, div, rem, call, ref, f2i, c2i, b2i
	 * ident, dot, index
	 */
	int IntExpr(Node p) throws ReturnException, AbortException { //TODO
		switch (p.kind) {
		case Node.INTCON:
			return p.val; // Returns a Constante
			
		/*
		 * For calculation
		 */
		case Node.PLUS:
			return IntExpr(p.left) + IntExpr(p.right);
		case Node.MINUS:
			if(p.right == null) {
				return 0 - IntExpr(p.left);
			} else {
				return IntExpr(p.left) - IntExpr(p.right);
			}
		case Node.TIMES:
			return IntExpr(p.left) * IntExpr(p.right);
		case Node.DIV:
			if (IntExpr(p.right) != 0)
				return IntExpr(p.left) / IntExpr(p.right);
			else
				debugger.abort("Divided by 0", p);
			throw new IllegalStateException("Kind" + p.kind);
		case Node.REM:
			return IntExpr(p.left) % IntExpr(p.right);

		/*
		 * Bit Operators
		 */
		case Node.BITAND:
			return IntExpr(p.left) & IntExpr(p.right);
		case Node.BITNEQ:
			return ~IntExpr(p.left);
		case Node.BITOR:
			return IntExpr(p.left) | IntExpr(p.right);
		case Node.BITXOR:
			return IntExpr(p.left) ^ IntExpr(p.right);
		case Node.SHIFTLEFT:
			return IntExpr(p.left) << IntExpr(p.right);
		case Node.SHIFTRIGHT:
			return IntExpr(p.left) >> IntExpr(p.right);
			
		case Node.CALL:								//Opens new Integer c-- Function
			Call(p);
			return Memory.getIntReturnValue();		//getting return Value						
			
		case Node.F2I:								//casting float to Integer
			return (int) FloatExpr(p.left);
		case Node.C2I:								//casting char to Integer
			return (int) CharExpr(p.left);
		case Node.B2I:								//casting char to Integer
			boolean retBoolExpr = BoolExpr(p.left);
			if(retBoolExpr)
				return 0x01;
			else
				return 0x00;
		case Node.IDENT:							//more at @Adr
			return Memory.loadInt(IdentAdr(p.obj));
		case Node.DOT:								//more at @Adr
			return Memory.loadInt(Adr(p));			
		case Node.INDEX:							//more at @Adr
			return Memory.loadInt(Adr(p));
		default:
			debugger.abort("Not supportet intexpr node kind", p);
			throw new IllegalStateException("Kind" + p.kind);
		}
	}

	/*
	 * FloatExpr: Intcon, plus, minus, times, div, rem, call, ref, f2i, c2i,
	 * ident, dot, index
	 */

	float FloatExpr(Node p) throws AbortException, ReturnException { //TODO
		switch (p.kind) {
		case Node.FLOATCON:						//returning the Constant value
			return p.fVal;
			
		/*
		 * For calculation
		 */
		case Node.PLUS:
			return FloatExpr(p.left) + FloatExpr(p.right);
		case Node.MINUS:
			if(p.right == null) {
				return 0 - FloatExpr(p.left);
			} else {
				return FloatExpr(p.left) - FloatExpr(p.right);
			}
		case Node.TIMES:
			return FloatExpr(p.left) * FloatExpr(p.right);
		case Node.DIV:
			return FloatExpr(p.left) / FloatExpr(p.right);
		case Node.REM:
			return FloatExpr(p.left) % FloatExpr(p.right);

			
		case Node.I2F:							//Casts an Integer into an Float
			return (float) IntExpr(p.left);		
		case Node.CALL:							//Opens a new C-- Function and returns the Return Value
			Call(p);
			return Memory.getFloatReturnValue();						
		case Node.IDENT:						//more at @Adr
			return Memory.loadFloat(IdentAdr(p.obj));	
		case Node.DOT:							//more at @Adr
			return Memory.loadFloat(Adr(p));
		case Node.INDEX:						//more at @Adr
			return Memory.loadFloat(Adr(p));

		default:
			debugger.abort("Not supportet floatexpr node kind", p);
			throw new IllegalStateException("Kind" + p.kind);

		}
	}

	/*
	 * Char Expressions: Charcon, i2c, ident, dot, index
	 */

	char CharExpr(Node p) throws AbortException, ReturnException { //TODO
		switch (p.kind) {
		case Node.CHARCON:
			return (char) p.val;					//Returning an Char constant
		case Node.I2C:
			if (IntExpr(p.left) >= 0)
				return (char) IntExpr(p.left);		//Casting an IntExpression to Char
		case Node.IDENT:
			return Memory.loadChar(IdentAdr(p.obj));			//more at @Adr
		case Node.DOT:
			return Memory.loadChar(Adr(p));			//more at @Adr
		case Node.INDEX:
			if (p.left.type.kind != Struct.STRING)
				return Memory.loadChar(Adr(p));		//Normal way of getting Arrays -> more at @Adr
			else									//Getting a String and look at a special Position
				try {
					return strings.get(StringExpr(p.left)).charAt(IntExpr(p.right));
				} catch (BufferOverflowException e) {
					debugger.abort("Too high index choosen", p);
					throw new IllegalStateException("Kind" + p.kind);
				}
		case Node.CALL:
			Call(p);
			return Memory.getCharReturnValue();

		default:
			debugger.abort("Not supportet charexpr node kind", p);
			throw new IllegalStateException("Kind" + p.kind);

		}
	}

	@SuppressWarnings("unused")
	int StringExpr(Node p) throws AbortException, ReturnException { //TODO
		switch (p.kind) {
		case Node.IDENT:
			return Memory.loadStringAddress(Adr(p));
			
		case Node.PLUS:		//Reads the left and the right String and putting them together
			return strings.put(strings.get(StringExpr(p.left))+ strings.get(StringExpr(p.right)));
		case Node.STRINGCON:
			return p.val;	//Returns the Address of the String
		case Node.CALL:
			Call(p);
			return Memory.getIntReturnValue();
		case Node.A2S:
			String s = "";
			char ref;

			for (int a = 0; a <= p.left.type.size; a++) { 
				ref = Memory.loadChar(Adr(p.left) + p.left.type.elemType.size* a); //Left side * CharSize + Main Address 
				if (ref != '0') {
					s += ref;			//Putting the Array, together to an String
				}
				return strings.put(s);	//Saving the new String and returns Int Adress
			}
		case Node.C2S:
			return strings.put(Character.toString(CharExpr(p.left)));
		default:
			debugger.abort("Not supportet node kind", p);
			throw new IllegalStateException("Kind" + p.kind);
		}break;
	}

	/*
	 * Boolean Expressions: Have to be implemented in Compiler
	 */

	/*
	 * Conditions EQL, NEQ, LSS, LEQ, GTR, GEQ, NOT, OR, AND
	 */
	boolean Condition(Node p) throws AbortException, ReturnException {
		if(p.kind == Node.BOOLCON) {
			if(p.val == 0)
				return false;
			else
				return true;
		} else if(p.kind == Node.IDENT) {
			if(p.type.kind == Struct.BOOL) {
				return Memory.loadBool(p.obj.adr);
			} else {
				debugger.abort("type not supported as ident in condition", p);
				throw new IllegalStateException("Kind" + p.kind);
			}
		}
		
		switch (p.left.type.kind) {
		case Struct.INT:
			switch (p.kind) {
			case Node.I2B:
				if(IntExpr(p.left) == 0)
					return false;
				else
					return true;
			case Node.EQL:
				return IntExpr(p.left) == IntExpr(p.right);
			case Node.NEQ:
				return IntExpr(p.left) != IntExpr(p.right);
			case Node.LSS:
				return IntExpr(p.left) < IntExpr(p.right); // Less <
			case Node.LEQ:
				return IntExpr(p.left) <= IntExpr(p.right); // Lesser Equal <=
			case Node.GTR:
				return IntExpr(p.left) > IntExpr(p.right); // greater >
			case Node.GEQ:
				return IntExpr(p.left) >= IntExpr(p.right); // greater Equal >=
			case Node.OR:
				return Condition(p.left) || Condition(p.right); // OR ||
			case Node.AND:
				return Condition(p.left) && Condition(p.right); // AND &&
			case Node.NOT:
				return !Condition(p.left); // NOT
			default:
				debugger.abort("Not supportet struct node kind", p);
				throw new IllegalStateException("Kind" + p.kind);
			}
		case Struct.FLOAT:
			switch (p.kind) {
			case Node.EQL:
				return FloatExpr(p.left) == FloatExpr(p.right);
			case Node.NEQ:
				return FloatExpr(p.left) != FloatExpr(p.right);
			case Node.LSS:
				return FloatExpr(p.left) < FloatExpr(p.right); // Less <
			case Node.LEQ:
				return FloatExpr(p.left) <= FloatExpr(p.right); // Lesser Equal
																// <=
			case Node.GTR:
				return FloatExpr(p.left) > FloatExpr(p.right); // greater >
			case Node.GEQ:
				return FloatExpr(p.left) >= FloatExpr(p.right); // greater Equal
																// >=
			case Node.OR:
				return Condition(p.left) || Condition(p.right); // OR ||
			case Node.AND:
				return Condition(p.left) && Condition(p.right); // AND &&
			case Node.NOT:
				return !Condition(p.left); // NOT
			default:
				debugger.abort("Not supportet float node kind", p);
				throw new IllegalStateException("Kind" + p.kind);
			}

		case Struct.CHAR:
			switch (p.kind) {
			case Node.EQL:
				return CharExpr(p.left) == CharExpr(p.right);
			case Node.NEQ:
				return CharExpr(p.left) != CharExpr(p.right);
			case Node.LSS:
				return CharExpr(p.left) < CharExpr(p.right); // Less <
			case Node.LEQ:
				return CharExpr(p.left) <= CharExpr(p.right); // Lesser Equal <=
			case Node.GTR:
				return CharExpr(p.left) > CharExpr(p.right); // greater >
			case Node.GEQ:
				return CharExpr(p.left) >= CharExpr(p.right); // greater Equal
																// >=
			case Node.OR:
				return Condition(p.left) || Condition(p.right); // OR ||
			case Node.AND:
				return Condition(p.left) && Condition(p.right); // AND &&
			case Node.NOT:
				return !Condition(p.left); // NOT
			default:
				debugger.abort("Not supportet char node kind", p);
				throw new IllegalStateException("Kind" + p.kind);
			}
			
		case Struct.BOOL:
			switch (p.kind) {
			case Node.EQL:
				return Condition(p.left) == Condition(p.right);
			case Node.NEQ:
				return Condition(p.left) != Condition(p.right);
				
			case Node.OR:
				return Condition(p.left) || Condition(p.right); // OR ||
			case Node.AND:
				return Condition(p.left) && Condition(p.right); // AND &&
			case Node.NOT:
				return !Condition(p.left); // NOT
			default:
				debugger.abort("Not supportet char node kind", p);
				throw new IllegalStateException("Kind" + p.kind);
			}

			// case Struct.BOOL: break; // Boolean Variables
			// case Struct.STRING: break; // Compiler implentation

		default:
			debugger.abort("Not supportet condition node kind", p);
			throw new IllegalStateException("Kind" + p.kind);

		}
	}

	/*
	 * Call Function working TODO
	 */

	void Call(Node p) throws AbortException, ReturnException { 

		switch (p.obj.name) {
		case "print":	//Our Print, can only print Characters
			inout.out(CharExpr(p.left));
			break;
		case "read":	//Read, can read in Characters
			Memory.setCharReturnValue(inout.in());
			break; 
		case "length": 	//
			switch (p.left.type.kind) {
			case Struct.STRING: // Size of elements in String
				Memory.setIntReturnValue(strings.get(StringExpr(p.left)).length());
				break; 
			default:
				debugger.abort("Not supportet length node kind", p);
				throw new IllegalStateException("Kind" + p.kind);
			}
			break;
		default:

			Node ref = p;
			Obj form = p.obj.locals;

			int a = 0;

			// Amount of Items
			for (ref = p.left; ref != null; ref = ref.next)
				a++;
			Object[] object = new Object[a];
			a = 0;

			// Buffering the Data in an object[]
			for (ref = p.left; ref != null; ref = ref.next, form = form.next) {
				if (form.isRef) {
					object[a] = Adr(ref);
				} else {
					switch (form.type.kind) {
					case Struct.BOOL:
						object[a] = BoolExpr(ref);
						break; 
					case Struct.INT:
						object[a] = IntExpr(ref);
						break; 
					case Struct.CHAR:
						object[a] = CharExpr(ref);
						break; 
					case Struct.FLOAT:
						object[a] = FloatExpr(ref);
						break;
					case Struct.STRING:
						object[a] = StringExpr(ref);
						break;
					}
				}
				a++;
			}
			
			// New Memory Frame for C-- Function
			try {
				Memory.openStackFrame(p.line,MethodContainer.getMethodId(p.obj.name), p.obj.size);
			} catch (StackOverflowException e) {
				debugger.abort("StackOverFlow", p);
				throw new IllegalStateException("Kind" + p.kind);
			}

			// Saving the Object into the new C-- Function Memory Frame.
			form = p.obj.locals;
			a = 0;
			for (ref = p.left; ref != null; ref = ref.next, form = form.next) {
				if (form.isRef) {
					Memory.storeInt(Memory.getFramePointer() + form.adr,(int) object[a]);
				} else {
					switch (form.type.kind) {
					case Struct.BOOL:
						if((int)object[a] == 0)
							Memory.storeBool(Memory.getFramePointer() + form.adr,false);
						else
							Memory.storeBool(Memory.getFramePointer() + form.adr,true);
				 		break; 
					case Struct.INT:
						Memory.storeInt(Memory.getFramePointer() + form.adr,(int) object[a]);
				 		break; 
					case Struct.CHAR:
						Memory.storeChar(Memory.getFramePointer() + form.adr,(char) object[a]);
						break;
					case Struct.FLOAT:
						Memory.storeFloat(Memory.getFramePointer() + form.adr,(float) object[a]);
						break;
					case Struct.STRING:
						Memory.storeStringAdress(Memory.getFramePointer()+ form.adr, (int) object[a]);
						break;
					default:
						debugger.abort("Not supportet node kind", p);
						throw new IllegalStateException("Kind" + p.kind);
					}
				}
				a++;
		}
			try {
				StatSeq(p.obj.ast); 		// Starting the new C-- Function
			} catch (ReturnException e) { 	// closing the C-- Function
			} catch(BreakException e) {
				debugger.abort("break is not allowed here", p);
			} catch(ContinueException e) {
				debugger.abort("continue is not allowed here", p);
			}

			try {
				Memory.closeStackFrame(); // Closing the C-- Function Frame
			} catch (StackUnderflowException e) {
				debugger.abort("Stack Underflow", p);
				throw new IllegalStateException();
			}
			break;
		}
	}

	/*
	 * Designators: Address reserving Identifier, Dot, Index, Structs
	 */

	int Adr(Node p) throws ReturnException, AbortException { // TODO
		
		switch (p.kind) {
		case Node.IDENT:					// more at @IdentAdr
			return IdentAdr(p.obj);
		case Node.DOT:						//for structs very familiar with index
			return Adr(p.left) + p.right.val;
		case Node.INDEX:					//right value + Integer * sizeof(Integer)
			return Adr(p.left) + p.left.type.elemType.size * IntExpr(p.right);
		case Node.REF://TODO
			return Adr(p.left);
		default:
			debugger.abort("Not supported Node Kind", p);
			throw new IllegalStateException("p.kind" + p.kind);
		}
	}

	/*
	 * Identifier Address
	 */
	int IdentAdr(Obj obj) throws ReturnException, AbortException {
		int adr;
		if (obj.level == 0)					// Is the variable global?
			adr = Memory.getGlobalPointer() + obj.adr;	//yes - GlobalPointer + Address
		else
			adr = Memory.getFramePointer() + obj.adr;	//no - FramePointer + Address
		if (obj.isRef)
			return Memory.loadInt(adr); // References saves the Address in an Integer Variable
		else
			return adr;					//Returns the normal Address Value
	}
}
