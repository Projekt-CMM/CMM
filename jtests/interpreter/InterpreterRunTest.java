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
 *  Copyright (c) 2015 Thomas Pointhuber
 */

package interpreter;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

import at.jku.ssw.cmm.compiler.Compiler;
import at.jku.ssw.cmm.compiler.Error;
import at.jku.ssw.cmm.debugger.DebuggerMock;
import at.jku.ssw.cmm.debugger.StdInOut;
import at.jku.ssw.cmm.interpreter.Interpreter;
import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;
import at.jku.ssw.cmm.interpreter.memory.Memory;

public class InterpreterRunTest implements StdInOut {
	String output = new String();
	String input = new String();
	
	public void runFile(String file) throws Exception {
		// Init variables
		FileInputStream in = null;
		
		try {
			in = new FileInputStream(file);

			byte[] code = new byte[in.available()];

			in.read(code);

			runCode(in.toString());
			
		} catch (IOException e) {
			fail("could not open file " + file);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
	}
	
	public void runCode(String code) throws Exception {
		runCode(code, "");
	}
	
	public void runCode(String code, String _input) throws Exception {
		// Init variables
		output = new String();
		if(_input != null)
			input = _input;
		else
			input = "";
		
		Compiler compiler = new Compiler();

		compiler.compile(code);

		Error e = compiler.getError();

		while (e != null) {
			fail("line " + e.line + ", col " + e.col + ": "
				+ e.msg);
			e = e.next;
		}
			
		Memory.initialize();

		Interpreter interpreter = new Interpreter(new DebuggerMock(), this);

		interpreter.run(compiler.getSymbolTable());
	}

	@Override
	public char in() throws RunTimeException {
		if(input.isEmpty())
			throw new RuntimeException("No Input Data present!");
		
		char returnCharacter = input.charAt(0);
		
		input = input.substring(1);
		
		return returnCharacter;
	}

	@Override
	public void out(char arg0) {
		output = output + arg0;
	}

	@Test
	public void testSimpleMain() throws Exception {
		runCode("void main() {}");
		assertEquals(output, "");
	}
	
	@Test
	public void testCallPrint() throws Exception {
		runCode("void main() { print(' '); }");
		assertEquals(output, " ");
		
		runCode("void main() { print('c'); }");
		assertEquals(output, "c");
		
		runCode("void main() { print('\\n'); }");
		assertEquals(output, "\n");
	}
	
	@Test
	public void testCallPrintf() throws Exception {
		runCode("void main() { printf(\"\"); }");
		assertEquals(output, "");
		
		runCode("void main() { printf(\"Hello World\"); }");
		assertEquals(output, "Hello World");
		
		runCode("void main() { printf(\"int: %d\", 12345); }");
		assertEquals(output, "int: 12345");
		
		runCode("void main() { printf(\"a: %d\\tb: %d\", 1, 2); }");
		assertEquals(output, "a: 1\tb: 2");
	}
	
	@Test
	public void testCallRead() throws Exception {
		runCode("void main() { print(read()); print(read()); }", "pa");
		assertEquals(output, "pa");
	}
	
	@Test
	public void testCallLength() throws Exception {
		runCode("void main() { print( (char)( length(\"123456\") + 48) ); }");
		assertEquals(output, "6");
		
		runCode("void main() { print( (char)( length(\"\") + 48) ); }");
		assertEquals(output, "0");
	}
}
