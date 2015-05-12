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
 
package at.jku.ssw.cmm.interpreter;

/**
 * General Info:
 * TODO: JUnit
 */
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.compiler.Struct;
import at.jku.ssw.cmm.compiler.Strings;
import at.jku.ssw.cmm.compiler.Tab;
import at.jku.ssw.cmm.debugger.Debugger;
import at.jku.ssw.cmm.debugger.StdInOut;
import at.jku.ssw.cmm.interpreter.exceptions.AbortException;
import at.jku.ssw.cmm.interpreter.exceptions.BreakException;
import at.jku.ssw.cmm.interpreter.exceptions.ContinueException;
import at.jku.ssw.cmm.interpreter.exceptions.ReturnException;
import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;
import at.jku.ssw.cmm.interpreter.exceptions.StackOverflowException;
import at.jku.ssw.cmm.interpreter.exceptions.StackUnderflowException;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.interpreter.memory.MethodContainer;
import at.jku.ssw.cmm.compiler.Node;

public final class Interpreter {

	private final Debugger debugger;
	private final StdInOut inout;
	
	private int libraryFunctionLevel;
	public static int currentLine;
	// private boolean running;

	/**
	 * Function for starting the Debugger
	 */
	public Interpreter(Debugger debugger, StdInOut stdInout) {
		this.debugger = debugger;
		this.inout = stdInout;
	}

	/**
	 * Start Function
	 * @throws RunTimeException 
	 */
	public void run(Tab symbolTab) throws RunTimeException {
		// get main-function
		Obj main = symbolTab.find("main");
		
		// fill MemoryInformation with informations about global-vars and main-functino
		try {
			
			// add variable names of global variables into MemoryInformation Array
			for(Obj globSymb = symbolTab.curScope.locals; globSymb != null; globSymb = globSymb.next) {
				if(globSymb.kind == Obj.VAR || globSymb.kind == Obj.CON) {
					Memory.getMemoryInformation(Memory.getGlobalPointer() + globSymb.adr).varName = globSymb.name;
					
					// safe number of elements of an array
					if(globSymb.isRef == false && globSymb.type.kind == Struct.ARR) {
						List<Integer> arrayElements = new ArrayList<>();
						
						// detect number of elements per dimension
						for(Struct curElement = globSymb.type;curElement.kind == Struct.ARR; curElement = curElement.elemType)
							arrayElements.add(curElement.elements);
						
						Memory.getMemoryInformation(Memory.getGlobalPointer() + globSymb.adr).arrayElements = arrayElements;
					}
				}
			}
			
			// add variable names of main-function into MemoryInformation Array
			Memory.openStackFrame(main.ast.line, MethodContainer.getMethodId("main"), main.size);

			for(Obj form = main.locals;form != null;form = form.next) {
				Memory.getMemoryInformation(Memory.getFramePointer() + form.adr).varName = form.name;
				
				// safe number of elements of an array
				if(form.isRef == false && form.type.kind == Struct.ARR) {
					List<Integer> arrayElements = new ArrayList<>();
					
					// detect number of elements per dimension
					for(Struct curElement = form.type;curElement.kind == Struct.ARR; curElement = curElement.elemType)
						arrayElements.add(curElement.elements);
					
					Memory.getMemoryInformation(Memory.getFramePointer() + form.adr).arrayElements = arrayElements;
				}
			}
		} catch (StackOverflowException e1) {
			throw new IllegalStateException(e1);
		}
		
		// TODO try catch abort messages
		try {
			StatSeq(main.ast);
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

	/**
	 * StartSequenz, Working Module
	 * @throws RunTimeException 
	 */
	void StatSeq(Node p) throws ReturnException, AbortException, BreakException, ContinueException, RunTimeException { // AST
		selectCurrentLine(p);
		for (p = p.left; p != null; p = p.next)
			Statement(p);
	}

	/**
	 * Statements: assign, stateseq, if, ifelse, while, return, trap, call TODO
	 * @throws RunTimeException 
	 */
	void Statement(Node p) throws ReturnException, AbortException, BreakException, ContinueException, RunTimeException { // b = a;
		selectCurrentLine(p);
		//TODO add changed variable list
		if (p.kind != Node.NOP && libraryFunctionLevel == 0 && !debugger.step(p, Memory.readVariables, Memory.changedVariables))
			throw new AbortException();
		
		// clear ArrayLists
		if(libraryFunctionLevel == 0) {
			Memory.readVariables.clear();
			Memory.changedVariables.clear();
		}
		
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
			case Struct.STRUCT:
				Memory.copyMemoryRegion(Adr(p.right), Adr(p.left), p.left.type.size);
				break;
			default:
				throw new RunTimeException("Not supportet node kind", p, currentLine);

			}
			break;
		case Node.CALL:
			Call(p);
			break;
		case Node.STATSEQ:
			StatSeq(p);
			break;
		case Node.TRAP: // For a Function with return!
			throw new RunTimeException("Return Statement missing", p, currentLine); 

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
						// TODO infinite loop in the current state
						if(p.right != null)
							Statement(p.right);
						else {
							if(libraryFunctionLevel == 0 && !debugger.step(p, Memory.readVariables, Memory.changedVariables))
								throw new AbortException();
							// clear ArrayLists
							if(libraryFunctionLevel == 0) {
								Memory.readVariables.clear();
								Memory.changedVariables.clear();
							}
						}
					} catch(ContinueException e) {
					} finally {
						selectCurrentLine(p);
					}
				}
			} catch(BreakException e) {
			}
			break;
		case Node.DOWHILE:
			try {
				do
					try {
						// TODO infinite loop in the current state
						if(p.right != null)
							Statement(p.right);
						else {
							if(libraryFunctionLevel == 0 && !debugger.step(p, Memory.readVariables, Memory.changedVariables))
								throw new AbortException();
							// clear ArrayLists
							if(libraryFunctionLevel == 0) {
								Memory.readVariables.clear();
								Memory.changedVariables.clear();
							}
						}
					} catch(ContinueException e) {
					} finally {
						selectCurrentLine(p);
					}
				while (Condition(p.left));
			} catch(BreakException e) {	
			}
			break;
		case Node.FOR:
			Node relopNode;
			if(p.right.next != null)
				relopNode = p.right.next;
			else
				throw new RunTimeException("not defined relop statement", p, currentLine);
			
			Node statement = relopNode.next;
			
			try {
				Statement(p.right);
				while (Condition(p.left)) {
					try {
						// TODO infinite loop in the current state
						if(statement != null)
							Statement(statement);
						else {
							if(libraryFunctionLevel == 0 && !debugger.step(p, Memory.readVariables, Memory.changedVariables))
								throw new AbortException();
							// clear ArrayLists
							if(libraryFunctionLevel == 0) {
								Memory.readVariables.clear();
								Memory.changedVariables.clear();
							}
						}
					} catch(ContinueException e) {
					} finally {
						selectCurrentLine(p);
					}
					Statement(relopNode);
				}
			} catch(BreakException e) {
			}
			break;
		case Node.SWITCH:
			try {
				Node caseNode = p.right;
				while(caseNode != null) {
					// default statement
					if(caseNode.left == null) {
						Node curStateseq = caseNode.right;
						while(curStateseq != null) {
							Statement(curStateseq);
							curStateseq = curStateseq.next;
						}
						break;
					} else {
						Node conNode = new Node(Node.EQL, p.left, caseNode.left, caseNode.line);

						// case statement
						if(Condition(conNode)) {
							Node curStateseq = caseNode.right;
							while(curStateseq != null) {
								Statement(curStateseq);
								curStateseq = curStateseq.next;
							}
							break;
						}
					}

					caseNode = caseNode.next;
				}
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
				throw new RunTimeException("Not supportet return node kind", p, currentLine);
			}
		case Node.BREAK:
				throw new BreakException();
		case Node.CONTINUE:
				throw new ContinueException();
		case Node.NOP:
				break;
		case Node.WAIT:
				break;
		default:
			throw new RunTimeException("Not supportet statement node kind", p, currentLine);
		}
	}
	
	/**
	 * BoolExpr: boolcon, call, ref, i2b,
	 * ident, dot, index
	 * @throws RunTimeException 
	 */
	boolean BoolExpr(Node p) throws ReturnException, AbortException, RunTimeException {
		selectCurrentLine(p);
		
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
			return BoolExpr(p.left) && BoolExpr(p.right);
		case Node.BITOR:
			return BoolExpr(p.left) || BoolExpr(p.right);
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
			return Memory.loadBoolSave(IdentAdr(p.obj), p);
		case Node.DOT:								//more at @Adr
			return Memory.loadBoolSave(Adr(p),p);			
		case Node.INDEX:							//more at @Adr
			return Memory.loadBoolSave(Adr(p),p);
		default:
			throw new RunTimeException("Not supportet boolexpr node kind", p, currentLine);
		}
	}
	
	/**
	 * IntExpr: intcon, plus, minus, times, div, rem, call, ref, f2i, c2i, b2i
	 * ident, dot, index
	 * @throws RunTimeException 
	 */
	int IntExpr(Node p) throws ReturnException, AbortException, RunTimeException {
		selectCurrentLine(p);
		
		try{
			switch (p.kind) {
			case Node.INTCON:
				return p.val; // Returns a Constante
				
			/*
			 * For calculation
			 */
			case Node.PLUS:
				if(p.right == null)
					return IntExpr(p.left);
				else
					return SaveIntOperator.add(IntExpr(p.left),IntExpr(p.right));
			case Node.MINUS:
				if(p.right == null) {
					return SaveIntOperator.subtract(0, IntExpr(p.left));
				} else {
					return SaveIntOperator.subtract(IntExpr(p.left), IntExpr(p.right));
				}
			case Node.TIMES:
				return SaveIntOperator.multiply(IntExpr(p.left), IntExpr(p.right));
			case Node.DIV:
				if (IntExpr(p.right) != 0)
					return SaveIntOperator.divide(IntExpr(p.left), IntExpr(p.right));
				throw new RunTimeException("Divided by 0", p, currentLine);
			case Node.REM:
				if (IntExpr(p.right) != 0)
					return IntExpr(p.left) % IntExpr(p.right);
				throw new RunTimeException("Divided by 0", p, currentLine);
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
				return Memory.loadIntSave(IdentAdr(p.obj), p);
			case Node.DOT:								//more at @Adr
				return Memory.loadIntSave(Adr(p), p);			
			case Node.INDEX:							//more at @Adr
				return Memory.loadIntSave(Adr(p), p);
			default:
				throw new RunTimeException("Not supportet intexpr node kind", p, currentLine);
			}
		} catch(ArithmeticException e) {
			throw new RunTimeException(e.getMessage(), p, currentLine);
		}
	}

	/**
	 * FloatExpr: Intcon, plus, minus, times, div, rem, call, ref, f2i, c2i,
	 * ident, dot, index
	 * @throws RunTimeException 
	 */
	float FloatExpr(Node p) throws AbortException, ReturnException, RunTimeException {
		selectCurrentLine(p);
		
		try {
			switch (p.kind) {
			case Node.FLOATCON:						//returning the Constant value
				return p.fVal;
				
			/*
			 * For calculation
			 */
			case Node.PLUS:
			    if(p.right == null)
			        return FloatExpr(p.left);
			    else
				    return SaveFloatOperator.add(FloatExpr(p.left), FloatExpr(p.right));
			case Node.MINUS:
				if(p.right == null) {
					return SaveFloatOperator.subtract(0, FloatExpr(p.left));
				} else {
					return SaveFloatOperator.subtract(FloatExpr(p.left), FloatExpr(p.right));
				}
			case Node.TIMES:
				return SaveFloatOperator.multiply(FloatExpr(p.left), FloatExpr(p.right));
			case Node.DIV:
				if (FloatExpr(p.right) != 0)
					return SaveFloatOperator.divide(FloatExpr(p.left), FloatExpr(p.right));
				throw new RunTimeException("Divided by 0", p, currentLine);
			case Node.REM:
				if (FloatExpr(p.right) != 0)
					return FloatExpr(p.left) % FloatExpr(p.right);
				throw new RunTimeException("Divided by 0", p, currentLine);
	
				
			case Node.I2F:							//Casts an Integer into an Float
				return (float) IntExpr(p.left);		
			case Node.CALL:							//Opens a new C-- Function and returns the Return Value
				Call(p);
				return Memory.getFloatReturnValue();						
			case Node.IDENT:						//more at @Adr
				return Memory.loadFloatSave(IdentAdr(p.obj),p);	
			case Node.DOT:							//more at @Adr
				return Memory.loadFloatSave(Adr(p),p);
			case Node.INDEX:						//more at @Adr
				return Memory.loadFloatSave(Adr(p),p);
	
			default:
				throw new RunTimeException("Not supportet floatexpr node kind", p, currentLine);
	
			}
		} catch(ArithmeticException e) {
			throw new RunTimeException(e.getMessage(), p, currentLine);
		}
	}

	/**
	 * Char Expressions: Charcon, i2c, ident, dot, index
	 * @throws RunTimeException 
	 */
	char CharExpr(Node p) throws AbortException, ReturnException, RunTimeException {
		selectCurrentLine(p);
		
		switch (p.kind) {
		case Node.CHARCON:
			return (char) p.val;					//Returning an Char constant
		case Node.I2C:
			return (char) IntExpr(p.left);			//Casting an IntExpression to Char
		case Node.IDENT:
			return Memory.loadCharSave(IdentAdr(p.obj),p);			//more at @Adr
		case Node.DOT:
			return Memory.loadCharSave(Adr(p),p);			//more at @Adr
		case Node.INDEX:
			if (p.left.type.kind != Struct.STRING) {
				return Memory.loadCharSave(Adr(p),p);		//Normal way of getting Arrays -> more at @Adr
			} else {									//Getting a String and look at a special Position
				try {
				    String s = Strings.get(StringExpr(p.left));
				    if(IntExpr(p.right) < 0) {
				        throw new RunTimeException("negative index chosen", p, currentLine);
				    }
				    if(IntExpr(p.right) >= s.length()) {
				        throw new RunTimeException("Too high index chosen", p, currentLine);
				    }
				    return Strings.get(StringExpr(p.left)).charAt(IntExpr(p.right));
				} catch (BufferOverflowException e) {
					throw new RunTimeException("Too high index chosen", p, currentLine);
				}
			}
		case Node.CALL:
			Call(p);
			return Memory.getCharReturnValue();

		default:
			throw new RunTimeException("Not supportet charexpr node kind", p, currentLine);

		}
	}

	int StringExpr(Node p) throws AbortException, ReturnException, RunTimeException {
		selectCurrentLine(p);
		
		switch (p.kind) {
		case Node.PLUS:		//Reads the left and the right String and putting them together
			return Strings.put(Strings.get(StringExpr(p.left)) + Strings.get(StringExpr(p.right)));
		case Node.STRINGCON:
			return p.val;	//Returns the Address of the String
		case Node.CALL:
			Call(p);
			return Memory.getIntReturnValue();
		case Node.A2S:
			String s = "";
			char ref;
			int numberOfArrayElements = p.left.type.elements;
			
			int baseAdr = Adr(p.left);

			// check if array is a reference, and return array size
			if(numberOfArrayElements == -1) {
				Node pHelp = p;
				
				for(pHelp = p;pHelp != null && pHelp.kind != Node.IDENT; pHelp = pHelp.left);

				if(pHelp == null)
					throw new RunTimeException("There is no informations about the array size available", p, currentLine);

				List<Integer>  singleArrayElements = Memory.getMemoryInformation(IdentAdr(pHelp.obj)).arrayElements;
				
				// calculate number of array Elements which can be used
				numberOfArrayElements = singleArrayElements.get(singleArrayElements.size()-1);

				List<Object[]> arrayElements = new ArrayList<>();
				
				int arrayElementSize = p.left.type.elemType.size;
				
				// create Object which store how much elements the Array store and how big one element is
				for(int i = singleArrayElements.size()-1; i >= 0 ; i--) {
					Object[] arrayObj = {singleArrayElements.get(i), arrayElementSize};
					arrayElementSize *=singleArrayElements.get(i);
					arrayElements.add(arrayObj);
				}
				
				arrayElements.remove(0);
				
				baseAdr = Adr(p.left, arrayElements);
			}

			for (int a = 0; a <= numberOfArrayElements; a++) {
				if (a == numberOfArrayElements)
					throw new RunTimeException("last Array Element has to be '\\0'", p, currentLine);

				ref = Memory.loadCharSave(baseAdr + p.left.type.elemType.size * a, p); //Left side * CharSize + Main Address
				
				if (ref == '\0')
					break;
				
				s += ref;			//Putting the Array, together to an String
			}
			return Strings.put(s);	//Saving the new String and returns Int Adress
		case Node.C2S:
			return Strings.put(Character.toString(CharExpr(p.left)));
		case Node.IDENT:						//more at @Adr
			return Memory.loadStringAddressSave(Adr(p),p);
		case Node.DOT:							//more at @Adr
			return Memory.loadStringAddressSave(Adr(p),p);
		case Node.INDEX:						//more at @Adr
			return Memory.loadStringAddressSave(Adr(p),p);
		default:
			throw new RunTimeException("Not supportet node kind", p, currentLine);
		}
	}

	/**
	 * Conditions EQL, NEQ, LSS, LEQ, GTR, GEQ, NOT, OR, AND
	 * @throws RunTimeException 
	 */
	boolean Condition(Node p) throws AbortException, ReturnException, RunTimeException {
		selectCurrentLine(p);
		
		if(p.kind == Node.BOOLCON) {
			if(p.val == 0)
				return false;
			else
				return true;
		} else if(p.kind == Node.IDENT) {
			if(p.type.kind == Struct.BOOL) {
				return Memory.loadBoolSave(IdentAdr(p.obj), p);
			} else {
				throw new RunTimeException("type not supported as ident in condition", p, currentLine);
			}
		} else if(p.kind == Node.CALL && p.type.kind == Struct.BOOL) {
			Call(p);
			return Memory.getBoolReturnValue();
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
					case Node.CALL:								//Opens new Integer c-- Function
						Call(p);
						//getting return Value	
						if(Memory.getIntReturnValue() == 0)
							return false;
						else
							return true;	
					default:
						System.out.println("kind: " + p.kind);
						throw new RunTimeException("Not supportet struct node kind", p, currentLine);
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
						// TODO required?
						return !Condition(p.left); // NOT
					default:
						throw new RunTimeException("Not supportet float node kind", p, currentLine);
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
						// TODO required?
						return !Condition(p.left); // NOT
					default:
						throw new RunTimeException("Not supportet char node kind", p, currentLine);
				}
			
			case Struct.BOOL:
				switch (p.kind) {
					case Node.IDENT:
						return Memory.loadBoolSave(Adr(p), p);
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
					case Node.CALL:								//Opens new Bool c-- Function
						Call(p);
						return Memory.getBoolReturnValue();
					default:
						throw new RunTimeException("Not supportet char node kind", p, currentLine);
				}

			// case Struct.BOOL: break; // Boolean Variables
			// case Struct.STRING: break; // Compiler implentation

		default:
			throw new RunTimeException("Not supportet condition node kind", p, currentLine);

		}
	}

	/**
	 * Call Function working TODO
	 * @throws RunTimeException 
	 */
	void Call(Node p) throws AbortException, ReturnException, RunTimeException {
		selectCurrentLine(p);
		
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
				Memory.setIntReturnValue(Strings.get(StringExpr(p.left)).length());
				break; 
			default:
				throw new RunTimeException("Not supportet length node kind", p, currentLine);
			}
			break;
		case "time":	//Read, can read in Characters
			Date date= new Date();
			// get Timestamp: Startdate: 1 Jan. 1970
			// TODO overflow-time: Tue, 19 Jan 2038 03:14:07 GMT
			Memory.setIntReturnValue((int)(date.getTime()/1000));
			break;
		case "__is_def_bool__":
		case "__is_def_int__":
		case "__is_def_float__":
		case "__is_def_char__":
		case "__is_def_string__":
			Memory.setBoolReturnValue(Memory.getMemoryInformation(IdentAdr(p.left.obj)).isInitialized);
			break;
		case "__assert__":
			if(!BoolExpr(p.left)) {
				String s = Strings.get(StringExpr(p.left.next));
				throw new RunTimeException(s, p, currentLine);
			}
			break;
		case "printf":
			String s = Strings.get(StringExpr(p.left));
			Node curPrintfNode = p.left;
			for(int i=0;i<s.length();i++) {
				if(s.charAt(i) == '%' && s.length() > i +1 && !(i > 1 && s.charAt(i-1) == '\\')) {
					curPrintfNode = curPrintfNode.next;
					if(curPrintfNode == null) {
						throw new RunTimeException("printf doesn't contain the required amount of arguments", p, currentLine);
					} else {
						i++;
						String sHelp = "%" + s.charAt(i);
						switch(s.charAt(i)){
							case 'd':
								switch(curPrintfNode.type.kind) {
									case Struct.INT:
										sHelp = Integer.toString(IntExpr(curPrintfNode));
										break;
									case Struct.FLOAT:
										sHelp = Integer.toString((int)FloatExpr(curPrintfNode));
										break;
									case Struct.CHAR:
										sHelp = Integer.toString((int)CharExpr(curPrintfNode));
										break;
									case Struct.BOOL:
										if(BoolExpr(curPrintfNode))
											sHelp = "1";
										else
											sHelp = "0";
										break;
									default:
										throw new RunTimeException("invalid printf parameter", p, currentLine);
								} 
								break;
							case 'x':
								switch(curPrintfNode.type.kind) {
									case Struct.INT:
										sHelp = Integer.toHexString(IntExpr(curPrintfNode));
										break;
									case Struct.FLOAT:
										sHelp = Integer.toHexString((int)FloatExpr(curPrintfNode));
										break;
									case Struct.CHAR:
										sHelp = Integer.toHexString((int)CharExpr(curPrintfNode));
										break;
									case Struct.BOOL:
										if(BoolExpr(curPrintfNode))
											sHelp = "1";
										else
											sHelp = "0";
										break;
									default:
										throw new RunTimeException("invalid printf parameter", p, currentLine);
								} 
								break;
							case 'f':
								switch(curPrintfNode.type.kind) {
									case Struct.INT:
										sHelp = Float.toString((float)IntExpr(curPrintfNode));
										break;
									case Struct.FLOAT:
										sHelp = Float.toString((float)FloatExpr(curPrintfNode));
										break;
									case Struct.CHAR:
										sHelp = Float.toString((float)CharExpr(curPrintfNode));
										break;
									case Struct.BOOL:
										if(BoolExpr(curPrintfNode))
											sHelp = "1.0";
										else
											sHelp = "0.0";
										break;
									default:
										throw new RunTimeException("invalid printf parameter", p, currentLine);
								} 
								break;
							case 'c':
								switch(curPrintfNode.type.kind) {
									case Struct.INT:
										sHelp = "" + (char)IntExpr(curPrintfNode);
										break;
									case Struct.FLOAT:
										sHelp = "" + (char)FloatExpr(curPrintfNode);
										break;
									case Struct.CHAR:
										sHelp = "" + (char)CharExpr(curPrintfNode);
										break;
									default:
										throw new RunTimeException("invalid printf parameter", p, currentLine);
								} 
								break;
							default:
								// TODO %s - string
								break;
						}
						for(int y=0;y<sHelp.length();y++) {
							inout.out(sHelp.charAt(y));
						}
					}
				} else
					inout.out(s.charAt(i));
			}
			break;
		default:
			// safe old FramePointer
			int oldFramePointer = Memory.getFramePointer();
			
			// New Memory Frame for C-- Function
			try {
				Memory.openStackFrame(p.line,MethodContainer.getMethodId(p.obj.name), p.obj.size);
			} catch (StackOverflowException e) {
				throw new RunTimeException("StackOverFlow", p, currentLine);
			}

			// safe new FramePointer to rewrite it later
			int newFramePointer = Memory.getFramePointer();
			// use old FramePointer, because we have to run the Expressions with the old one
			Memory.setFramePointer(oldFramePointer);
			
			// writing variables into new Stack and
			// add variable names into MemoryInformation Array
			Node ref = p.left;
			for(Obj form = p.obj.locals;form != null;form = form.next) {
				Memory.getMemoryInformation(Memory.getFramePointer() + form.adr).varName = form.name;
				// safe number of elements of an array
				// TODO: reqired?
				if(form.isRef == false && form.type.kind == Struct.ARR) {
					List<Integer> arrayElements = new ArrayList<>();
					
					// detect number of elements per dimension
					for(Struct curElement = form.type;curElement.kind == Struct.ARR; curElement = curElement.elemType)
						arrayElements.add(curElement.elements);
					
					Memory.getMemoryInformation(Memory.getFramePointer() + form.adr).arrayElements = arrayElements;
				}
				
				// write function arguments in the created stack
				if(ref != null) {
					if (form.isRef) {
						Memory.storeInt(newFramePointer + form.adr, Adr(ref));
					} else {
						switch (form.type.kind) {
						case Struct.BOOL:
							Memory.storeBool(newFramePointer + form.adr, BoolExpr(ref));
					 		break; 
						case Struct.INT:
							Memory.storeInt(newFramePointer + form.adr, IntExpr(ref));
					 		break; 
						case Struct.CHAR:
							Memory.storeChar(newFramePointer + form.adr, CharExpr(ref));
							break;
						case Struct.FLOAT:
							Memory.storeFloat(newFramePointer + form.adr, FloatExpr(ref));
							break;
						case Struct.STRING:
							Memory.storeStringAdress(newFramePointer + form.adr, StringExpr(ref));
							break;
						case Struct.STRUCT:
							Memory.copyMemoryRegion(Adr(ref), newFramePointer + form.adr, form.type.size);
							break;
						default:
							throw new RunTimeException("Not supportet node kind", p, currentLine);
						}
					}
					ref = ref.next;
				}
			}
			
			// set new FramePointer
			Memory.setFramePointer(newFramePointer);

			try {
				if(p.obj.library)
					libraryFunctionLevel ++;
				
				StatSeq(p.obj.ast); 		// Starting the new C-- Function
			} catch (ReturnException e) { 	// closing the C-- Function
			} catch(BreakException e) {
				throw new RunTimeException("break is not allowed here", p, currentLine);
			} catch(ContinueException e) {
				throw new RunTimeException("continue is not allowed here", p, currentLine);
			} finally {
				// stop debugger after closing function
				if (libraryFunctionLevel == 0 && !debugger.step(p, Memory.readVariables, Memory.changedVariables))
					throw new AbortException();

				if(p.obj.library)
					libraryFunctionLevel --;
			}

			try {
				Memory.closeStackFrame(); // Closing the C-- Function Frame
			} catch (StackUnderflowException e) {
				throw new RunTimeException("Stack Underflow", p, currentLine);
			}
			break;
		}
	}

	/**
	 * Designators: Address reserving Identifier, Dot, Index, Structs
	 * @throws RunTimeException 
	 */
	int Adr(Node p) throws ReturnException, AbortException, RunTimeException {
		return Adr(p, null);
	}
	
	/**
	 * Designators: Address reserving Identifier, Dot, Index, Structs
	 * @throws RunTimeException 
	 */
	int Adr(Node p, List<Object[]> arrayElements) throws ReturnException, AbortException, RunTimeException { // TODO
		//selectCurrentLine(p);
		
		switch (p.kind) {
		case Node.IDENT:					// more at @IdentAdr
			return IdentAdr(p.obj);
		case Node.DOT:						//for structs very familiar with index
			return IdentAdr(p.left.obj) + p.right.val;
		case Node.INDEX:					//left value + Integer * sizeof(Integer)
			int index = IntExpr(p.right);
			if(index < 0) {
		        throw new RunTimeException("negative index choosen", p, currentLine);
			}
			if(p.left.type.elements == -1) {
				// get number of arrayElements, if first dimenstion of ref-Array is called
				if(arrayElements == null) {
					Node pHelp = null;
					
					for(pHelp = p;pHelp != null && pHelp.kind != Node.IDENT; pHelp = pHelp.left);

					if(pHelp == null)
						throw new RunTimeException("There is no informations about the array size available", p, currentLine);

					// read number of array Elements
					List<Integer> singleArrayElements = Memory.getMemoryInformation(IdentAdr(pHelp.obj)).arrayElements;					
					
					if(singleArrayElements == null)
						throw new RunTimeException("There is no informations about the array size available", p, currentLine);
					
					arrayElements = new ArrayList<>();
					
					int arrayElementSize = p.left.type.elemType.size;
					
					// create Object which store how much elements the Array store and how big one element is
					for(int i = singleArrayElements.size()-1; i >= 0 ; i--) {
						Object[] arrayObj = {singleArrayElements.get(i), arrayElementSize};
						arrayElementSize *=singleArrayElements.get(i);
						arrayElements.add(arrayObj);
					}
				}
				
				if(arrayElements == null || arrayElements.isEmpty()) {
					throw new RunTimeException("There is no informations about the array size available", p, currentLine);
				} else {
					int arrayElementNumber = (int) arrayElements.get(0)[0];
					int elementSize = (int) arrayElements.get(0)[1];

					// buffer overflow detection
					if(index >= arrayElementNumber)
						throw new RunTimeException("too high index choosen", p, currentLine);
					
					arrayElements.remove(0);

					return Adr(p.left, arrayElements) + elementSize * index;
				}
			}
			else if(index >= p.left.type.elements) {
				throw new RunTimeException("too high index choosen", p, currentLine);
			}

			return Adr(p.left) + p.left.type.elemType.size * index;
		case Node.REF://TODO
			return Adr(p.left);
		default:
			throw new RunTimeException("Not supportet node kind", p, currentLine);
		}
	}

	/**
	 * Identifier Address
	 * @throws RunTimeException 
	 */
	int IdentAdr(Obj obj) throws ReturnException, AbortException, RunTimeException {
		int adr;
		if (obj.level == 0)					// Is the variable global?
			adr = Memory.getGlobalPointer() + obj.adr;	//yes - GlobalPointer + Address
		else
			adr = Memory.getFramePointer() + obj.adr;	//no - FramePointer + Address
		if (obj.isRef)
			return Memory.loadIntSave(adr, null); // References saves the Address in an Integer Variable
		else
			return adr;					//Returns the normal Address Value
	}
	
	void selectCurrentLine(Node p) {
		if(p.line > 0)
			currentLine = p.line;
	}
}
