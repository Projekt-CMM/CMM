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
import at.jku.ssw.cmm.interpreter.excpetions.AbortException;
import at.jku.ssw.cmm.interpreter.excpetions.ReturnException;
import at.jku.ssw.cmm.interpreter.excpetions.StackOverflowException;
import at.jku.ssw.cmm.interpreter.excpetions.StackUnderflowException;
import at.jku.ssw.cmm.interpreter.memory.Memory;
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

		try {
			StatSeq(node);
		} catch (ReturnException e) {
			return;
		} catch (AbortException e) {
			return;
		}
	}

	/*
	 * StartSequenz, Working Module
	 */
	void StatSeq(Node p) throws ReturnException, AbortException { // AST
		for (p = p.left; p != null; p = p.next)
			Statement(p);
	}

	/*
	 * Statements: assign, stateseq, if, ifelse, while, return, trap, call
	 */
	void Statement(Node p) throws ReturnException, AbortException { // b = a;
		if (!debugger.step(p))//TODO step
			throw new AbortException();

		switch (p.kind) {
		case Node.ASSIGN:
			switch (p.right.type.kind) {
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
			throw new IllegalStateException("Kind" + p.kind); // Exception

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
			while (Condition(p.left))
				Statement(p.right);
			break;
		// case Node.PRINT: System.out.print("I:"+ (int)CharExpr(p.left) +
		// " C:"+ CharExpr(p.left)+"\n");break;
		/*
		 * switch(p.left.type.kind){ case Struct.INT:
		 * System.out.println(IntExpr(p.left));break; //Type Convertierung case
		 * Struct.CHAR: System.out.println(CharExpr(p.left));break; case
		 * Struct.FLOAT: System.out.println(FloatExpr(p.left));break; }
		 */
		case Node.RETURN:
			switch (p.left.type.kind) {
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

		default:
			debugger.abort("Not supportet statement node kind", p);
			throw new IllegalStateException("Kind" + p.kind);
		}
	}

	/*
	 * IntExpr: intcon, plus, minus, times, div, rem, call, ref, f2i, c2i,
	 * ident, dot, index
	 */
	int IntExpr(Node p) throws ReturnException, AbortException {
		switch (p.kind) {
		case Node.INTCON:
			return p.val; // OK
		case Node.PLUS:
			return IntExpr(p.left) + IntExpr(p.right);
		case Node.MINUS:
			return IntExpr(p.left) - IntExpr(p.right);
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

		case Node.CALL:
			Call(p);
			return Memory.getIntReturnValue();
		case Node.REF:
			return Adr(p);
		case Node.F2I:
			return (int) FloatExpr(p.left);
		case Node.C2I:
			return (int) CharExpr(p.left);

		case Node.IDENT:
			return Memory.loadInt(IdentAdr(p.obj));

		case Node.DOT:
			return Memory.loadInt(Adr(p));
		case Node.INDEX:
			return Memory.loadInt(Adr(p));
			// case Node.READ: return Memory.loadInt(Adr(p));
		default:
			debugger.abort("Not supportet intexpr node kind", p);
			throw new IllegalStateException("Kind" + p.kind);
		}
	}

	/*
	 * FloatExpr: Intcon, plus, minus, times, div, rem, call, ref, f2i, c2i,
	 * ident, dot, index
	 */

	float FloatExpr(Node p) throws AbortException, ReturnException {
		switch (p.kind) {
		case Node.FLOATCON:
			return p.fVal;
		case Node.PLUS:
			return FloatExpr(p.left) + FloatExpr(p.right);
		case Node.MINUS:
			return FloatExpr(p.left) - FloatExpr(p.right);
		case Node.TIMES:
			return FloatExpr(p.left) * FloatExpr(p.right);
		case Node.DIV:
			return FloatExpr(p.left) / FloatExpr(p.right);
		case Node.REM:
			return FloatExpr(p.left) % FloatExpr(p.right);

		case Node.I2F:
			return (float) IntExpr(p.left);

		case Node.CALL:
			Call(p);
			return Memory.getFloatReturnValue();
		case Node.REF:
			return Adr(p);
		case Node.IDENT:
			return Memory.loadFloat(Adr(p));
		case Node.DOT:
			return Memory.loadFloat(Adr(p));
		case Node.INDEX:
			return Memory.loadFloat(Adr(p));
			// case Node.READ: return Memory.loadFloat(Adr(p));

		default:
			debugger.abort("Not supportet floatexpr node kind", p);
			throw new IllegalStateException("Kind" + p.kind);

		}
	}

	/*
	 * Char Expressions: Charcon, i2c, ident, dot, index
	 */

	char CharExpr(Node p) throws AbortException, ReturnException {
		switch (p.kind) {
		case Node.CHARCON:
			return (char) p.val;
		case Node.I2C:
			if (Memory.loadInt(Adr(p.left)) >= 0)
				return (char) IntExpr(p.left);

			// case Node.REF: return Adr(p);

		case Node.IDENT:
			return Memory.loadChar(Adr(p));
		case Node.DOT:
			return Memory.loadChar(Adr(p));
		case Node.INDEX:
			if (p.left.type.kind != Struct.STRING)
				return Memory.loadChar(Adr(p));
			else
				try {
					return strings.get(StringExpr(p.left)).charAt(
							IntExpr(p.right));
				} catch (BufferOverflowException e) {
					debugger.abort("Too high index choosen", p);
					// throw new BufferOverflowException();
				}
			// case Node.READ: return Memory.loadChar(Adr(p));

		case Node.CALL:
			Call(p);
			return Memory.getCharReturnValue();

		default:
			debugger.abort("Not supportet charexpr node kind", p);
			throw new IllegalStateException("Kind" + p.kind);

		}
	}

	@SuppressWarnings("unused")
	int StringExpr(Node p) throws AbortException, ReturnException {
		switch (p.kind) {
		case Node.IDENT:
			return Memory.loadStringAdress(Adr(p));
		case Node.PLUS:
			return strings.put(strings.get(StringExpr(p.left))
					+ strings.get(StringExpr(p.right)));
		case Node.STRINGCON:
			return p.val;
		case Node.CALL:
			Call(p);
			return Memory.getIntReturnValue();
			// case Node.INDEX: throw new IllegalStateException("Kind" +
			// p.kind);
		case Node.A2S:
			String s = "";
			char ref;

			for (int a = 0; a <= p.left.type.size; a++) {
				ref = Memory.loadChar(Adr(p.left) + p.left.type.elemType.size
						* a);
				if (ref != '0') {
					s += ref;
				}
				return strings.put(s); // TODO a2s
			}

		default:
			debugger.abort("Not supportet node kind", p);
			throw new IllegalStateException("Kind" + p.kind);
		}
	}

	/*
	 * Boolean Expressions: Have to be implementet in Compiler
	 */

	/*
	 * Conditions EQL, NEQ, LSS, LEQ, GTR, GEQ, NOT, OR, AND
	 */
	boolean Condition(Node p) throws AbortException, ReturnException {
		switch (p.left.type.kind) {
		case Struct.INT:
			switch (p.kind) {
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

			// case Struct.BOOL: break; // Boolean Variables
			// case Struct.STRING: break; // Compiler implentation

		default:
			debugger.abort("Not supportet condition node kind", p);
			throw new IllegalStateException("Kind" + p.kind);

		}
	}

	/*
	 * Call Function working
	 */

	void Call(Node p) throws AbortException, ReturnException {

		switch (p.obj.name) {
		case "print":
			inout.out(CharExpr(p.left));
			// System.out.print("I:"+ (int)CharExpr(p.left) + " C:"+
			// CharExpr(p.left)+"\n"); break;
		case "read":
			Memory.setCharReturnValue(inout.in());
			break; // Memory.setCharReturnValue(value); break;
		case "length":
			switch (p.left.type.kind) {
			case Struct.STRING:
				Memory.setIntReturnValue(strings.get(StringExpr(p.left))
						.length());
				break; // Size of elements
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

			// Buffer in Object[]
			for (ref = p.left; ref != null; ref = ref.next, form = form.next) {
				if (form.isRef) {
					object[a] = Adr(ref);
				} else {
					switch (form.type.kind) {
					case Struct.INT:
						object[a] = IntExpr(ref);
						break; // 4 Byte
					case Struct.CHAR:
						object[a] = CharExpr(ref);
						break; // 1 Byte
					case Struct.FLOAT:
						object[a] = FloatExpr(ref);
						break; // 4 Byte
					case Struct.STRING:
						object[a] = StringExpr(ref);
						break;
					}
				}
				a++;
			}

			// New Memory Frame
			try {
				Memory.openStackFrame(p.line, 0, p.obj.size);
			} catch (StackOverflowException e) {
				debugger.abort("StackOverFlow", p);
				throw new IllegalStateException("Kind" + p.kind);
			}

			// Saving the Object into the Memory
			form = p.obj.locals;
			a = 0;
			for (ref = p.left; ref != null; ref = ref.next, form = form.next) {

				switch (form.type.kind) {
				case Struct.INT:
					Memory.storeInt(Memory.getFramePointer() + form.adr,
							(int) object[a]);
					break; // 4 Byte
				case Struct.CHAR:
					Memory.storeChar(Memory.getFramePointer() + form.adr,
							(char) object[a]);
					break; // 1 Byte
				case Struct.FLOAT:
					Memory.storeFloat(Memory.getFramePointer() + form.adr,
							(float) object[a]);
					break; // 4 Byte
				case Struct.STRING:
					Memory.storeStringAdress(Memory.getFramePointer()
							+ form.adr, (int) object[a]);
					break;
				default:
					debugger.abort("Not supportet node kind", p);
					throw new IllegalStateException("Kind" + p.kind);
				}

				a++;
			}
			try {
				StatSeq(p.obj.ast); // starting the Function
			} catch (ReturnException e) { // Make Returns Close the Function
			}

			try {
				Memory.closeStackFrame(); // closing the Memory Frame*/
			} catch (StackUnderflowException e) {
				debugger.abort("Stack Underflow", p);
				throw new IllegalStateException();
			}
			break;
		}

		// if(!debugger.step(p))
	}

	/*
	 * Designators: Address reserving Ident, Dot, Index, Strukturen fertig
	 */

	int Adr(Node p) throws ReturnException, AbortException { // Designators
		switch (p.kind) {
		case Node.IDENT:
			return IdentAdr(p.obj);
		case Node.DOT:
			return Adr(p.left) + p.right.val;
		case Node.INDEX: // if(p.left.type.kind != Struct.STRING)
			return Adr(p.left) + p.left.type.elemType.size * IntExpr(p.right);
			// else
			// return Adr(p.left) + p.left.type.elemType.size * StringExpr(p);
		default:
			return Memory.getFramePointer();
		}
	}

	/*
	 * Identifer Adress
	 */
	int IdentAdr(Obj obj) throws ReturnException, AbortException {
		int adr;
		if (obj.level == 0)
			adr = Memory.getGlobalPointer() + obj.adr;
		else
			adr = Memory.getFramePointer() + obj.adr;
		if (obj.isRef)
			return Memory.loadInt(adr); // References saves the Adress in an Int
										// Variable
		else
			return adr;
	}
}
