import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import at.jku.ssw.cmm.StdInoutMock;
import at.jku.ssw.cmm.compiler.Compiler;
import at.jku.ssw.cmm.compiler.Error;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.compiler.Strings;
import at.jku.ssw.cmm.debugger.DebuggerMock;
import at.jku.ssw.cmm.interpreter.Interpreter;
import at.jku.ssw.cmm.interpreter.excpetions.StackOverflowException;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.debugger.StdInOut;

@SuppressWarnings("deprecation")
public class Tests implements StdInOut {

	public char ref = 1;

	public static void run(String[] args) throws Exception {
		FileInputStream in = null;
		try {
			in = new FileInputStream(args[0]);
			byte[] code = new byte[in.available()];
			in.read(code);

			Compiler compiler = new Compiler();
			if (args.length > 2 && args[1].equals("-debug")) {
				for (int i = 2; i < args.length; i++) {
					int val = Integer.parseInt(args[i]);
					if (val >= 0 && val < compiler.debug.length)
						compiler.debug[val] = true;
				}
			}
			compiler.compile(new String(code));

			Error e = compiler.getError();
			int errCount = 0;
			while (e != null) {
				System.out.println("line " + e.line + ", col " + e.col + ": "
						+ e.msg);
				errCount++;
				e = e.next;
			}
			System.out.println(errCount + " errors detected");
			Memory.initialize();

			Strings strings = compiler.getStringStorage();
			Interpreter interpreter = new Interpreter(new DebuggerMock(),
					new StdInoutMock(), strings);
			Obj main = compiler.getSymbolTable().find("main");
			try {
				Memory.openStackFrame(main.ast.line, 0, main.size);
			} catch (StackOverflowException e1) {
				throw new IllegalStateException(e1);
			}
			interpreter.run(main.ast);
		} catch (IOException e) {
			System.out.println("could not open file " + args[0]);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
	}

	@Test
	public void test1() throws Exception {
		String[] a = { "interpretertests/file01.cmm" };
		run(a);

		Assert.assertEquals(10, ref);
	}

	@Test
	public void test2() throws Exception {
		String[] a = { "interpretertests/file02.cmm" };
		run(a);

		Assert.assertEquals(true, true);
	}

	@Test
	public void test3() throws Exception {
		String[] a = { "interpretertests/file03.cmm" };
		run(a);

		Assert.assertEquals(true, true);
	}

	@Test
	public void test4() throws Exception {
		String[] a = { "interpretertests/file04.cmm", "-debug", "1", "1" };
		run(a);

		Assert.assertEquals(true, true);
	}

	@Override
	public char in() {
		return 0;
	}

	@Override
	public void out(char arg0) {
		ref = arg0;
	}
}
