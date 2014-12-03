package at.jku.ssw.cmm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.jku.ssw.cmm.compiler.Compiler;
import at.jku.ssw.cmm.compiler.Error;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.debugger.DebuggerMock;
import at.jku.ssw.cmm.interpreter.Interpreter;
import at.jku.ssw.cmm.interpreter.exceptions.StackOverflowException;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.preprocessor.Preprocessor;

/*--------------------------------------------------------------------------------
 CMM   Main program of the C-- compiler
 ===   ================================
 This class can be used to invoke the C-- compiler.

 Synopsis:
 java cmm.compiler.CMM <sourcefile> [ -debug {n} ]

 Debug switches:
 0: dump the symbol table
 1: dump the AST
 --------------------------------------------------------------------------------*/

public class CMM {

	public static void main(String[] args) throws Exception {
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
			
			List<Object[]> codeRegister = new ArrayList();
			List<Integer> breakpoints = new ArrayList();
			String preprocessorOutput = Preprocessor.expand(new String(code), "./clib", codeRegister, breakpoints);
			
			System.out.println("######################\n" + preprocessorOutput + "\n######################");
			
			compiler.compile(preprocessorOutput);

			Error e = compiler.getError();
			int errCount = 0;
			while (e != null) {
				Object[] errorElement = Preprocessor.returnFileAndNumber(e.line, codeRegister);
				System.out.println("error: " + errorElement[0] + ":" + errorElement[1] + ", col " + e.col + ": "+ e.msg);
				errCount++;
				e = e.next;
			}
			System.out.println(errCount + " errors detected");
			Memory.initialize();

			Interpreter interpreter = new Interpreter(new DebuggerMock(), new StdInoutMock() );
			Obj main = compiler.getSymbolTable().find("main");
			interpreter.run(main);
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

}