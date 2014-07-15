package at.jku.ssw.cmm.compiler;

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
import java.io.*;

class CMM {

public static void main(String[] args) {
	FileInputStream in = null;
	int state = 0;
	try {
		if(args.length < 1) {
			System.out.println("no file specified");
			System.exit(1);
		}
		in = new FileInputStream(args[0]);
		byte[] code = new byte[in.available()];
		in.read(code);

		Compiler compiler = new Compiler();
		if (args.length > 2 && args[1].equals("-debug")) {
			for (int i = 2; i < args.length; i++) {
				int val = Integer.parseInt(args[i]);
				if (val >= 0 && val < compiler.debug.length) compiler.debug[val] = true;
			}
		}
		compiler.compile(new String(code));
		int errCount = 0;
		Error e = compiler.getError();
		
		while (e != null) {
			System.out.println("line " + e.line + ", col " + e.col + ": " + e.msg);
			errCount++;
			state = 2;
			e = e.next;
		}
		System.out.println(errCount + " errors detected");
	} catch (IOException e) {
		System.out.println("could not open file " + args[0]);
		state = 2;
	} finally {
		try {
			if (in != null) in.close();
		} catch (IOException e) {}
	}
	System.out.println("exit "+ state);
	System.exit(state);
}

}