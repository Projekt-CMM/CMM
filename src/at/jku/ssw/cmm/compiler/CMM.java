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