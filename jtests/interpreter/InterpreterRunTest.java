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
import at.jku.ssw.cmm.compiler.CompilerException;
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
			throw new CompilerException(e.msg, e.line);
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
		// basic main-function
		runCode("void main() {}");
		assertEquals(output, "");
		
		// no main-function declared
		try {
			runCode("void test() {}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// function parameter not allowed
		try {
			runCode("void main(int i) {}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
	}
	
	@Test
	public void testCallPrint() throws Exception {
		// print space
		runCode("void main() {"
				+ "  print(' ');"
				+ "}");
		assertEquals(output, " ");
		
		// print single char
		runCode("void main() {"
				+ "  print('c');"
				+ "}");
		assertEquals(output, "c");
		
		// print new line
		runCode("void main() {"
				+ "  print('\\n');"
				+ "}");
		assertEquals(output, "\n");
		
		// no function-parameter
		try {
			runCode("void main() {"
					+ "  print();"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
	}
	
	@Test
	public void testCallPrintf() throws Exception {
		// printf empty string
		runCode("void main() {"
				+ "  printf(\"\");"
				+ "}");
		assertEquals(output, "");
		
		// printf string
		runCode("void main() {"
				+ "  printf(\"Hello World\");"
				+ "}");
		assertEquals(output, "Hello World");
		
		// printf single integer
		runCode("void main() {"
				+ "  printf(\"int: %d\", 12345);"
				+ "}");
		assertEquals(output, "int: 12345");
		
		// printf multible variables
		runCode("void main() {"
				+ "  printf(\"a: %d\\tb: %d\", 1, 2);"
				+ "}");
		assertEquals(output, "a: 1\tb: 2");
		
		// no function-parameter
		try {
			runCode("void main() {"
					+ "  printf();"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
	}
	
	@Test
	public void testCallRead() throws Exception {
		// read characters from input-string
		runCode("void main() {"
				+ "  print(read());"
				+ "  print(read());"
				+ "}", "pa");
		assertEquals(output, "pa");
	}
	
	@Test
	public void testCallLength() throws Exception {
		// length of simple string
		runCode("void main() {"
				+ "  print( (char)( length(\"123456\") + 48) );"
				+ "}");
		assertEquals(output, "6");
		
		// length of empty string
		runCode("void main() {"
				+ "  print( (char)( length(\"\") + 48) );"
				+ "}");
		assertEquals(output, "0");
		
		// no function-parameter
		try {
			runCode("void main() {"
					+ "  int i = length();"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
	}
	
	@Test
	public void testInitVar() throws Exception {
		// bool-init with assignment
		runCode("void main() {"
				+ "  bool b1=true, b2=false;"
				+ "  printf(\"%d %d\",b1, b2);"
				+ "}");
		assertEquals(output, "1 0");
		
		// int-init with assignment
		runCode("void main() {"
				+ "  int i=42, j=12;"
				+ "  printf(\"%d %d\",i, j);"
				+ "}");
		assertEquals(output, "42 12");
		
		// float-init with assignment
		runCode("void main() {"
				+ "  float f1=123.456, f2=98.321;"
				+ "  printf(\"%f %f\",f1, f2);"
				+ "}");
		assertEquals(output, "123.456 98.321");
		
		// char-init with assignment
		runCode("void main() {"
				+ "  char c1='c', c2='q';"
				+ "  printf(\"%c %c\",c1, c2);"
				+ "}");
		assertEquals(output, "c q");
		
		// string-init with assignment
		runCode("void main() {"
				+ "  string s1=\"Hello\", s2=\"World\";"
				+ "  printf(s1 + \" \" + s2);"
				+ "}");
		assertEquals(output, "Hello World");
		
		// incorrect type-declaration
		try {
			runCode("void main() {"
					+ "  noType i;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
	}
	
	@Test
	public void testInitStruct() throws Exception {
		// simple struct
		runCode("struct Point {int x, y;} "
				+ "void main() {"
				+ "  Point p;"
				+ "  p.x = 10;"
				+ "  p.y = 55;"
				+ "  printf(\"%d %d\", p.x, p.y);"
				+ "}");
		assertEquals(output, "10 55");
		
		// empty struct
		try {
			runCode("struct Point {"
					+ "}"
					+ "void main() {"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// same struct inside struct
		try {
			runCode("struct Point {"
					+ "  int x, y;"
					+ "  Point p;"
					+ "}"
					+ "void main() {"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
	}
	
	@Test
	public void testInitArray() throws Exception {
		// single-dimension array
		runCode("void main() {"
				+ "  int a[5];"
				+ "  a[0] = 2;"
				+ "  a[3] = 10;"
				+ "  printf(\"%d %d\", a[0], a[3]);"
				+ "}");
		assertEquals(output, "2 10");
		
		// multi-dimension array
		runCode("void main() {"
				+ "  int a[5][10];"
				+ "  a[4][5] = 54;"
				+ "  a[2][9] = 29;"
				+ "  printf(\"%d %d\", a[4][5], a[2][9]);"
				+ "}");
		assertEquals(output, "54 29");
		
		// access to not initialized variables
		try {
			runCode("void main() {"
					+ "  int a[5][10];"
					+ "  printf(\"%d %d\", a[4][5], a[2][9]);"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(RunTimeException e) {}

		// buffer-overflow
		try {
			runCode("void main() {"
					+ "  int a[5][10];"
					+ "  a[9][4] = 54;"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(RunTimeException e) {}
	}
	
	@Test
	public void testIntExpression() throws Exception {
		// addition
		runCode("void main() {"
				+ "  int i = 1 + 2;"
				+ "  int j = +2;"
				+ "  j += i;"
				+ "  printf(\"%d %d\", i, j);"
				+ "}");
		assertEquals(output, "3 5");

		// substraction
		runCode("void main() {"
				+ "  int i = 5 - 9;"
				+ "  int j = -10;"
				+ "  j -= i;"
				+ "  printf(\"%d %d\", i, j);"
				+ "}");
		assertEquals(output, "-4 -6");

		// multiplication
		runCode("void main() {"
				+ "  int i = 2 * 5;"
				+ "  int j = 12;"
				+ "  j *= i;"
				+ "  printf(\"%d %d\", i, j);"
				+ "}");
		assertEquals(output, "10 120");

		// division
		runCode("void main() {"
				+ "  int i = 10 / 2;"
				+ "  int j = 30;"
				+ "  j /= i;"
				+ "  printf(\"%d %d\", i, j);"
				+ "}");
		assertEquals(output, "5 6");

		// division with zero
		try {
			runCode("void main() {"
					+ "  int i = 10 / 0;"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(RunTimeException e) {}
		
		// modulo
		runCode("void main() {"
				+ "  int i = 15 % 10;"
				+ "  int j = 22;"
				+ "  j %= i;"
				+ "  printf(\"%d %d\", i, j);"
				+ "}");
		assertEquals(output, "5 2");

		// modulo with zero
		try {
			runCode("void main() {"
					+ "  int i = 10 % 0;"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(RunTimeException e) {}
		
		// bit and
		runCode("void main() {"
				+ "  int i = 0xFF & 0x09;"
				+ "  int j = 0x08;"
				+ "  j &= i;"
				+ "  printf(\"%d %d\", i, j);"
				+ "}");
		assertEquals(output, "9 8");

		// bit or
		runCode("void main() {"
				+ "  int i = 0x08 | 0x01;"
				+ "  int j = 0x12;"
				+ "  j |= i;"
				+ "  printf(\"%d %d\", i, j);"
				+ "}");
		assertEquals(output, "9 27");

		// bit xor
		runCode("void main() {"
				+ "  int i = 0x0F ^ 0x1D;"
				+ "  int j = 0x03;"
				+ "  j ^= i;"
				+ "  printf(\"%d %d\", i, j);"
				+ "}");
		assertEquals(output, "18 17");

		// bit neq
		runCode("void main() {"
				+ "  int i = (~0xFA) & 0x7F;"
				+ "  printf(\"%d\", i);"
				+ "}");
		assertEquals(output, "5");

		// shift left
		runCode("void main() {"
				+ "  int i = 0x01 << 2;"
				+ "  int j = 0x03;"
				+ "  j <<= i;"
				+ "  printf(\"%d %d\", i, j);"
				+ "}");
		assertEquals(output, "4 48");

		// shift right
		runCode("void main() {"
				+ "  int i = 0x10 >> 3;"
				+ "  int j = 0x44;"
				+ "  j >>= i;"
				+ "  printf(\"%d %d\", i, j);"
				+ "}");
		assertEquals(output, "2 17");
		
		// call int function
		runCode("int foo() { return 10; }"
				+ "void main() {"
				+ "  int i = foo();"
				+ "  printf(\"%d\", i);"
				+ "}");
		assertEquals(output, "10");
		
		// typeconversation: float to int
		runCode("void main() {"
				+ "  int i = (int)12.345;"
				+ "  printf(\"%d\", i);"
				+ "}");
		assertEquals(output, "12");
		
		// typeconversation: char to int
		runCode("void main() {"
				+ "  int i = (int)'a';"
				+ "  printf(\"%d\", i);"
				+ "}");
		assertEquals(output, "97");
		
		// typeconversation: char to int
		runCode("void main() {"
				+ "  int i = (int)false;"
				+ "  int j = (int)true;"
				+ "  printf(\"%d %d\", i, j);"
				+ "}");
		assertEquals(output, "0 1");
		
		// access int inside struct
		runCode("struct Point { int x; int y; }"
				+ "void main() {"
				+ "  Point p;"
				+ "  p.x = 5;"
				+ "  p.y = 23;"
				+ "  printf(\"%d %d\", p.x, p.y);"
				+ "}");
		assertEquals(output, "5 23");
		
		// access int inside array
		runCode("void main() {"
				+ "  int arr[5];"
				+ "  arr[0] = 120;"
				+ "  arr[3] = 345;"
				+ "  printf(\"%d %d\", arr[0], arr[3]);"
				+ "}");
		assertEquals(output, "120 345");
		
		// arithmetic exception
		try {
			runCode("void main() {"
					+ "  int i = 0xCFFF * 0xCFFF;"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(RunTimeException e) {}
	}
	
	@Test
	public void testFloatExpression() throws Exception {
		// addition
		runCode("void main() {"
				+ "  float f = 1.1 + 2.2;"
				+ "  printf(\"%f\", f);"
				+ "}");
		assertEquals(Float.parseFloat(output), 3.3, 0.001);
		
		runCode("void main() {"
				+ "  float f = +123.45;"
				+ "  printf(\"%f\", f);"
				+ "}");
		assertEquals(Float.parseFloat(output), 123.45, 0.001);

		// substraction
		runCode("void main() {"
				+ "  float f = 5.5 - 3.2;"
				+ "  printf(\"%f\", f);"
				+ "}");
		assertEquals(Float.parseFloat(output), 2.3, 0.001);

		runCode("void main() {"
				+ "  float f = -987.63;"
				+ "  printf(\"%f\", f);"
				+ "}");
		assertEquals(Float.parseFloat(output), -987.63, 0.001);

		// multiplication
		runCode("void main() {"
				+ "  float f = 12.3 * 15.5;"
				+ "  printf(\"%f\", f);"
				+ "}");
		assertEquals(Float.parseFloat(output), 190.65, 0.001);

		// division
		runCode("void main() {"
				+ "  float f = 25.13 / 6.3;"
				+ "  printf(\"%f\", f);"
				+ "}");
		assertEquals(Float.parseFloat(output), 3.98889, 0.001);

		// division with zero
		try {
			runCode("void main() {"
					+ "  float f = 123.345 / 0.;"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(RunTimeException e) {}
				
		// modulo
		runCode("void main() {"
				+ "  float f = 8.2 % 4.4;"
				+ "  printf(\"%f\", f);"
				+ "}");
		assertEquals(Float.parseFloat(output), 3.799999, 0.001);

		// modulo with zero
		try {
			runCode("void main() {"
					+ "  float f = 1.2 % 0.;"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(RunTimeException e) {}
				
		// bit and
		try {
			runCode("void main() {"
					+ "  float f = 1.2 & 3.4;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// bit or
		try {
			runCode("void main() {"
					+ "  float f = 2.3 | 4.5;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// bit xor
		try {
			runCode("void main() {"
					+ "  float f = 2.3 ^ 4.5;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// bit neq
		try {
			runCode("void main() {"
					+ "  float f = ~12.34;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// shift left
		try {
			runCode("void main() {"
					+ "  float f = 12.34 << 2;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// shift right
		try {
			runCode("void main() {"
					+ "  float f = 1.2 >> 3;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
				
		// call float function
		runCode("float foo() { return 52.16; }"
				+ "void main() {"
				+ "  float f = foo();"
				+ "  printf(\"%f\", f);"
				+ "}");
		assertEquals(Float.parseFloat(output), 52.16, 0.001);
				
		// typeconversation: int to float
		runCode("void main() {"
				+ "  float f = (float)12;"
				+ "  printf(\"%f\", f);"
				+ "}");
		assertEquals(Float.parseFloat(output), 12, 0.001);

		// access float inside struct
		runCode("struct Point { float x; float y; }"
				+ "void main() {"
				+ "  Point p;"
				+ "  p.x = 123.456;"
				+ "  printf(\"%f\", p.x);"
				+ "}");
		assertEquals(Float.parseFloat(output), 123.456, 0.001);
				
		// access float inside array
		runCode("void main() {"
				+ "  float arr[5];"
				+ "  arr[4] = 654.321;"
				+ "  printf(\"%f\", arr[4]);"
				+ "}");
		assertEquals(Float.parseFloat(output), 654.321, 0.001);

		// arithmetic exception
		/*try {
			runCode("void main() {"
					+ "  float f = 0xCFFF * 0xCFFF;"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(RunTimeException e) {}*/
	}
}
