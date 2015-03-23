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
		
		// assignment not for all variable declarations
		runCode("void main() {"
				+ "  int a, b=12, c;"
				+ "  printf(\"%d\",b);"
				+ "}");
		assertEquals(output, "12");
		
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
		
		// assignment while array declaration
		runCode("void main() {"
				+ "  int a[5] = {1,2,3,4,5};"
				+ "  printf(\"%d %d %d %d %d\",a[0], a[1], a[2], a[3], a[4]);"
				+ "}");
		assertEquals(output, "1 2 3 4 5");
		
		// assignment while array declaration (only part of array-elements)
		runCode("void main() {"
				+ "  int a[5] = {1,2};"
				+ "  printf(\"%d %d %d %d %d\",a[0], a[1], a[2], a[3], a[4]);"
				+ "}");
		assertEquals(output, "1 2 0 0 0");
		
		// assignment while array declaration (all elements with the same value)
		runCode("void main() {"
				+ "  int a[5] = {3};"
				+ "  printf(\"%d %d %d %d %d\",a[0], a[1], a[2], a[3], a[4]);"
				+ "}");
		assertEquals(output, "3 3 3 3 3");
		
		// assignment while array declaration (to high index)
		try {
			runCode("void main() {"
					+ "  int a[5] = {1,2,3,4,5,6};"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(CompilerException e) {}
		
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
	public void testStatement() throws Exception {
		// TODO: Assign
		
		// TODO: CALL
		
		// TODO: STATSEQ
		
		// no return specified
		try {
			runCode("int foo() {}"
					+ "void main() {"
					+ "  int i = foo();"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(RunTimeException e) {}
		
		// return int
		
		// return ...
		
		// if
		runCode("void main() {"
				+ "  if(true) {"
				+ "    print('1');"
				+ "  }"
				+ "  if(false)"
				+ "    print('2');"
				+ "}");
		assertEquals(output, "1");

		// if-else
		runCode("void main() {"
				+ "  if(true) {"
				+ "    print('1');"
				+ "  } else {"
				+ "    print('2');"
				+ "  }"
				+ "  if(false)"
				+ "    print('3');"
				+ "  else"
				+ "    print('4');"
				+ "}");
		assertEquals(output, "14");
		
		// while
		runCode("void main() {"
				+ "  int i = 0;"
				+ "  while(i < 10) {"
				+ "    i ++;"
				+ "    if(i == 5)"
				+ "      continue;"
				+ "    if(i == 8)"
				+ "      break;"
				+ "    printf(\"%d\", i);"
				+ "  }"
				+ "}");
		assertEquals(output, "123467");

		runCode("void main() {"
				+ "  int i = 0;"
				+ "  while(i < 5) {"
				+ "    printf(\"%d\", i);"
				+ "    i ++;"
				+ "  }"
				+ "}");
		assertEquals(output, "01234");
		
		runCode("void main() {"
				+ "  while(read() != 'c');"
				+ "  print(read());"
				+ "}", "abcde");
		assertEquals(output, "d");
		
		// do while
		runCode("void main() {"
				+ "  int i = 0;"
				+ "  do {"
				+ "    i ++;"
				+ "    if(i == 5)"
				+ "      continue;"
				+ "    if(i == 8)"
				+ "      break;"
				+ "    printf(\"%d\", i);"
				+ "  } while(i < 10);"
				+ "}");
		assertEquals(output, "123467");

		runCode("void main() {"
				+ "  int i = 0;"
				+ "  do {"
				+ "    printf(\"%d\", i);"
				+ "    i ++;"
				+ "  } while(false);"
				+ "}");
		assertEquals(output, "0");

		// for
		runCode("void main() {"
				+ "  int i;"
				+ "  for(i = 0; i < 10; i++) {"
				+ "    if(i == 2)"
				+ "      continue;"
				+ "    if(i == 5)"
				+ "      break;"
				+ "    printf(\"%d\", i);"
				+ "  }"
				+ "}");
		assertEquals(output, "0134");
		
		runCode("void main() {"
				+ "  int i = 0;"
				+ "  for(; i < 3; i++)"
				+ "    printf(\"%d\", i);"
				+ "}");
		assertEquals(output, "012");
		
		runCode("void main() {"
				+ "  int i = 0;"
				+ "  for(; i < 6;) {"
				+ "    printf(\"%d\", i);"
				+ "    i += 2;"
				+ "  }"
				+ "}");
		assertEquals(output, "024");
		
		runCode("void main() {"
				+ "  int i;"
				+ "  for(i = 0; i < 6; i++);"
				+ "  printf(\"%d\", i);"
				+ "}");
		assertEquals(output, "6");

		// switch
		runCode("void main() {"
				+ "  int i;"
				+ "  for(i = 0; i < 6; i++) {"
				+ "    switch(i) {"
				+ "      case 0:"
				+ "        print('a');"
				+ "        break;"
				+ "      case 2:"
				+ "        print('e');"
				+ "      default:"
				+ "        printf(\"%d\", i);"
				+ "        break;"
				+ "      case 3:"
				+ "        print('d');"
				+ "        break;"
				+ "    }"
				+ "  }"
				+ "}");
		assertEquals(output, "a1e2d45");
		
		runCode("void main() {"
				+ "  int i;"
				+ "  for(i = 0; i < 6; i++) {"
				+ "    switch(i) {"
				+ "      case 1:"
				+ "      case 3:"
				+ "        printf(\"%d\", i);"
				+ "        break;"
				+ "    }"
				+ "  }"
				+ "}");
		assertEquals(output, "13");
	}
	
	
	@Test
	public void testBoolExpression() throws Exception {
		// addition
		try {
			runCode("void main() {"
					+ "  bool b = true + false;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// substraction
		try {
			runCode("void main() {"
					+ "  bool b = true - false;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// multiplication
		try {
			runCode("void main() {"
					+ "  bool b = false * true;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// division
		try {
			runCode("void main() {"
					+ "  bool b = true / true;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// modulo
		try {
			runCode("void main() {"
					+ "  bool b = true % true;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// bit and
		runCode("void main() {"
				+ "  bool b1 = true & true;"
				+ "  bool b2 = false & true;"
				+ "  bool b3 = false & false;"
				+ "  printf(\"%d %d %d\", b1, b2, b3);"
				+ "}");
		assertEquals(output, "1 0 0");

		// bit or
		runCode("void main() {"
				+ "  bool b1 = true | true;"
				+ "  bool b2 = false | true;"
				+ "  bool b3 = false | false;"
				+ "  printf(\"%d %d %d\", b1, b2, b3);"
				+ "}");
		assertEquals(output, "1 1 0");

		// bit xor
		runCode("void main() {"
				+ "  bool b1 = true ^ true;"
				+ "  bool b2 = false ^ true;"
				+ "  bool b3 = false ^ false;"
				+ "  printf(\"%d %d %d\", b1, b2, b3);"
				+ "}");
		assertEquals(output, "0 1 0");

		// bit neq
		try {
			runCode("void main() {"
					+ "  bool b = ~true;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// shift left
		try {
			runCode("void main() {"
					+ "  bool b = true << 1;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// shift right
		try {
			runCode("void main() {"
					+ "  bool b = true >> 2;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// call bool function
		runCode("bool foo() { return true; }"
				+ "bool bar() { return false; }"
				+ "void main() {"
				+ "  bool b1 = foo();"
				+ "  bool b2 = bar();"
				+ "  printf(\"%d %d\", b1, b2);"
				+ "}");
		assertEquals(output, "1 0");
		
		// typeconversation: int to bool
		runCode("void main() {"
				+ "  bool b1 = (bool)1;"
				+ "  bool b2 = (bool)0;"
				+ "  bool b3 = (bool)123;"
				+ "  printf(\"%d %d %d\", b1, b2, b3);"
				+ "}");
		assertEquals(output, "1 0 1");
		
		// access bool inside struct
		runCode("struct Point { bool x; bool y; }"
				+ "void main() {"
				+ "  Point p;"
				+ "  p.x = true;"
				+ "  p.y = false;"
				+ "  printf(\"%d %d\", p.x, p.y);"
				+ "}");
		assertEquals(output, "1 0");
		
		// access bool inside array
		runCode("void main() {"
				+ "  bool arr[5];"
				+ "  arr[0] = true;"
				+ "  arr[3] = false;"
				+ "  arr[4] = true;"
				+ "  printf(\"%d %d %d\", arr[0], arr[3], arr[4]);"
				+ "}");
		assertEquals(output, "1 0 1");
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
		
		// typeconversation: bool to int
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

	@Test
	public void testCharExpression() throws Exception {
		// addition
		try {
			runCode("void main() {"
					+ "  char ch = 'a' + 'b';"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// substraction
		try {
			runCode("void main() {"
					+ "  char ch = 'c' - 'd';"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// multiplication
		try {
			runCode("void main() {"
					+ "  char ch = 'e' * 'f';"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// division
		try {
			runCode("void main() {"
					+ "  char ch = 'g' / 'h';"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// modulo
		try {
			runCode("void main() {"
					+ "  char ch = 'i' % 'j';"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// bit and
		try {
			runCode("void main() {"
					+ "  char ch = 'k' & 'l';"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// bit or
		try {
			runCode("void main() {"
					+ "  char ch = 'm' | 'n';"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// bit xor
		try {
			runCode("void main() {"
					+ "  char ch = 'o' ^ 'p';"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// bit neq
		try {
			runCode("void main() {"
					+ "  char ch = ~'q';"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// shift left
		try {
			runCode("void main() {"
					+ "  char ch = 'r' << 2;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// shift right
		try {
			runCode("void main() {"
					+ "  char ch = 's' >> 3;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// call int function
		runCode("char foo() { return 'A'; }"
				+ "void main() {"
				+ "  char ch = foo();"
				+ "  printf(\"%c\", ch);"
				+ "}");
		assertEquals(output, "A");
		
		// typeconversation: int to char
		runCode("void main() {"
				+ "  char i = (char)100;"
				+ "  printf(\"%c\", i);"
				+ "}");
		assertEquals(output, "d");
		
		// access char inside struct
		runCode("struct Point { char x; char y; }"
				+ "void main() {"
				+ "  Point p;"
				+ "  p.x = 'X';"
				+ "  p.y = 'y';"
				+ "  printf(\"%c %c\", p.x, p.y);"
				+ "}");
		assertEquals(output, "X y");
		
		// access char inside array
		runCode("void main() {"
				+ "  char arr[5];"
				+ "  arr[0] = 'h';"
				+ "  arr[3] = 'j';"
				+ "  printf(\"%c %c\", arr[0], arr[3]);"
				+ "}");
		assertEquals(output, "h j");
		
		// access char from string
		runCode("void main() {"
				+ "  string s = \"abcdefg\";"
				+ "  char ch1 = s[1];"
				+ "  char ch2 = s[5];"
				+ "  printf(\"%c %c\", ch1, ch2);"
				+ "}");
		assertEquals(output, "b f");
		
		// access char from string reference
		runCode("void foo(string &str) {"
				+ "  char ch1 = str[0];"
				+ "  char ch2 = str[4];"
				+ "  printf(\"%c %c\", ch1, ch2);"
				+ "}"
				+ "void main() {"
				+ "  string s = \"abcdefg\";"
				+ "  foo(s);"
				+ "}");
		assertEquals(output, "a e");
		
		// access char from string array reference
		runCode("void foo(string str[][]) {"
				+ "  char ch1 = str[1][2][0];"
				+ "  char ch2 = str[1][2][6];"
				+ "  printf(\"%c %c\", ch1, ch2);"
				+ "}"
				+ "void main() {"
				+ "  string s[2][4];"
				+ "  s[1][2] = \"abcdefg\";"
				+ "  foo(s);"
				+ "}");
		assertEquals(output, "a g");
		
		// access negativ index from string
		try {
			runCode("void main() {"
					+ "  string s = \"abcdefg\";"
					+ "  char ch = s[-1];"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(RunTimeException e) {}
		
		// access to high index from string
		try {
			runCode("void main() {"
					+ "  string s = \"abcdefg\";"
					+ "  char ch = s[10];"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(RunTimeException e) {}
	}

	@Test
	public void testStringExpression() throws Exception {
		// addition
		runCode("void main() {"
				+ "  string str = \"abc\" + \"def\";"
				+ "  printf(str);"
				+ "}");
		assertEquals(output, "abcdef");

		// substraction
		try {
			runCode("void main() {"
					+ "  string str = \"abc\" - \"def\";"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// multiplication
		try {
			runCode("void main() {"
					+ "  string str = \"abc\" * \"def\";"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// division
		try {
			runCode("void main() {"
					+ "  string str = \"abc\" / \"def\";"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// modulo
		try {
			runCode("void main() {"
					+ "  string str = \"abc\" % \"def\";"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// bit and
		try {
			runCode("void main() {"
					+ "  string str = \"abc\" & \"def\";"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// bit or
		try {
			runCode("void main() {"
					+ "  string str = \"abc\" | \"def\";"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// bit xor
		try {
			runCode("void main() {"
					+ "  string str = \"abc\" ^ \"def\";"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// bit neq
		try {
			runCode("void main() {"
					+ "  string str = ~\"abc\";"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// shift left
		try {
			runCode("void main() {"
					+ "  string str = \"abc\" << 2;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}

		// shift right
		try {
			runCode("void main() {"
					+ "  string str = \"abc\" >> 1;"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// call string function
		runCode("string foo() { return \"test\"; }"
				+ "void main() {"
				+ "  string str = foo();"
				+ "  printf(str);"
				+ "}");
		assertEquals(output, "test");
		
		// typeconversation: char to string
		runCode("void main() {"
				+ "  string str = 'c';"
				+ "  printf(str);"
				+ "}");
		assertEquals(output, "c");
		
		// typeconversation: array to string
		runCode("void main() {"
				+ "  char cha[3];"
				+ "  cha[0] = 'a';"
				+ "  cha[1] = 'b';"
				+ "  cha[2] = '\\0';"
				+ "  string str = (string)cha;"
				+ "  printf(str);"
				+ "}");
		assertEquals(output, "ab");
		
		// typeconversation: array to string without \0 at the end
		try {
			runCode("void main() {"
					+ "  char cha[3];"
					+ "  cha[0] = 'a';"
					+ "  cha[1] = 'b';"
					+ "  cha[2] = 'c';"
					+ "  string str = (string)cha;"
					+ "  printf(str);"
					+ "}");
			fail("RunTimeException not thrown");
		} catch(RunTimeException e) {}
		
		// typeconversation: multi-dim array as reference to string
		runCode("void foo(char cha[][]) {"
				+ "  string str = (string)cha[1];"
				+ "  printf(str);"
				+ "}"
				+ "void main() {"
				+ "  char cha[2][3];"
				+ "  cha[1][0] = 'c';"
				+ "  cha[1][1] = 'z';"
				+ "  cha[1][2] = '\\0';"
				+ "  foo(cha);"
				+ "}");
		assertEquals(output, "cz");

		// access string inside struct
		runCode("struct Point { string x; string y; }"
				+ "void main() {"
				+ "  Point p;"
				+ "  p.x = \"xX\";"
				+ "  p.y = \"yY\";"
				+ "  printf(p.x + \" \" + p.y);"
				+ "}");
		assertEquals(output, "xX yY");
		
		// access string inside array
		runCode("void main() {"
				+ "  string arr[5];"
				+ "  arr[0] = \"ar0\";"
				+ "  arr[3] = \"bar3\";"
				+ "  printf(arr[0] + \" \"+ arr[3]);"
				+ "}");
		assertEquals(output, "ar0 bar3");
	}
	
	@Test
	public void testIntCondition() throws Exception {		
		// condition with ident
		runCode("void main() {"
				+ "  int i1 = 12;"
				+ "  int i2 = 0;"
				+ "  if(i1)"
				+ "    print('d');"
				+ "  if(i2)"
				+ "    print('s');"
				+ "}");
		assertEquals(output, "d");

		// int condition
		runCode("void main() {"
				+ "  if(1)"
				+ "    print('d');"
				+ "  if(1234)"
				+ "    print('a');"
				+ "  if(0)"
				+ "    print('s');"
				+ "}");
		assertEquals(output, "da");
		
		// equal condition
		runCode("void main() {"
				+ "  if(15 == 15)"
				+ "    print('s');"
				+ "  if(12 == 13)"
				+ "    print('d');"
				+ "}");
		assertEquals(output, "s");
		
		// not equal condition
		runCode("void main() {"
				+ "  if(15 != 15)"
				+ "    print('t');"
				+ "  if(12 != 13)"
				+ "    print('z');"
				+ "}");
		assertEquals(output, "z");
		
		// lesser condition
		runCode("void main() {"
				+ "  if(15 < 14)"
				+ "    print('a');"
				+ "  if(15 < 15)"
				+ "    print('b');"
				+ "  if(15 < 16)"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "c");
		
		// lesser equal condition
		runCode("void main() {"
				+ "  if(15 <= 14)"
				+ "    print('a');"
				+ "  if(15 <= 15)"
				+ "    print('b');"
				+ "  if(15 <= 16)"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "bc");
		
		// greater condition
		runCode("void main() {"
				+ "  if(15 > 14)"
				+ "    print('a');"
				+ "  if(15 > 15)"
				+ "    print('b');"
				+ "  if(15 > 16)"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "a");
				
		// greater equal condition
		runCode("void main() {"
				+ "  if(15 >= 14)"
				+ "    print('a');"
				+ "  if(15 >= 15)"
				+ "    print('b');"
				+ "  if(15 >= 16)"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "ab");
		
		// call condition
		runCode("int foo() {"
				+ "  return 123;"
				+ "}"
				+ "int bar() {"
				+ "  return 0;"
				+ "}"
				+ "void main() {"
				+ "  if(foo())"
				+ "    print('f');"
				+ "  if(bar())"
				+ "    print('b');"
				+ "}");
		assertEquals(output, "f");
	}

	@Test
	public void testFloatCondition() throws Exception {				
		// equal condition
		runCode("void main() {"
				+ "  if(15.5 == 15.5)"
				+ "    print('s');"
				+ "  if(12.2 == 13.3)"
				+ "    print('d');"
				+ "}");
		assertEquals(output, "s");
		
		// not equal condition
		runCode("void main() {"
				+ "  if(15.5 != 15.5)"
				+ "    print('t');"
				+ "  if(12.2 != 13.3)"
				+ "    print('z');"
				+ "}");
		assertEquals(output, "z");
		
		// lesser condition
		runCode("void main() {"
				+ "  if(15.5 < 14.4)"
				+ "    print('a');"
				+ "  if(15.5 < 15.5)"
				+ "    print('b');"
				+ "  if(15.5 < 16.6)"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "c");
		
		// lesser equal condition
		runCode("void main() {"
				+ "  if(15.5 <= 14.4)"
				+ "    print('a');"
				+ "  if(15.5 <= 15.5)"
				+ "    print('b');"
				+ "  if(15.5 <= 16.6)"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "bc");
		
		// greater condition
		runCode("void main() {"
				+ "  if(15.5 > 14.4)"
				+ "    print('a');"
				+ "  if(15.5 > 15.5)"
				+ "    print('b');"
				+ "  if(15.5 > 16.6)"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "a");
				
		// greater equal condition
		runCode("void main() {"
				+ "  if(15.5 >= 14.4)"
				+ "    print('a');"
				+ "  if(15.5 >= 15.5)"
				+ "    print('b');"
				+ "  if(15.5 >= 16.6)"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "ab");
	}

	@Test
	public void testCharCondition() throws Exception {				
		// equal condition
		runCode("void main() {"
				+ "  if('c' == 'c')"
				+ "    print('s');"
				+ "  if('b' == 'c')"
				+ "    print('d');"
				+ "}");
		assertEquals(output, "s");
		
		// not equal condition
		runCode("void main() {"
				+ "  if('c' != 'c')"
				+ "    print('t');"
				+ "  if('a' != 'b')"
				+ "    print('z');"
				+ "}");
		assertEquals(output, "z");
		
		// lesser condition
		runCode("void main() {"
				+ "  if('c' < 'b')"
				+ "    print('a');"
				+ "  if('c' < 'c')"
				+ "    print('b');"
				+ "  if('c' < 'd')"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "c");
		
		// lesser equal condition
		runCode("void main() {"
				+ "  if('c' <= 'b')"
				+ "    print('a');"
				+ "  if('c' <= 'c')"
				+ "    print('b');"
				+ "  if('c' <= 'd')"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "bc");
		
		// greater condition
		runCode("void main() {"
				+ "  if('c' > 'b')"
				+ "    print('a');"
				+ "  if('c' > 'c')"
				+ "    print('b');"
				+ "  if('c' > 'd')"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "a");
				
		// greater equal condition
		runCode("void main() {"
				+ "  if('c' >= 'b')"
				+ "    print('a');"
				+ "  if('c' >= 'c')"
				+ "    print('b');"
				+ "  if('c' >= 'd')"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "ab");
	}

	@Test
	public void testBoolCondition() throws Exception {
		// condition with ident
		runCode("void main() {"
				+ "  bool b1 = true;"
				+ "  bool b2 = false;"
				+ "  if(b1)"
				+ "    print('d');"
				+ "  if(b2)"
				+ "    print('s');"
				+ "}");
		assertEquals(output, "d");

		// bool condition
		runCode("void main() {"
				+ "  if(true)"
				+ "    print('d');"
				+ "  if(false)"
				+ "    print('s');"
				+ "}");
		assertEquals(output, "d");
		
		// equal condition
		runCode("void main() {"
				+ "  if(true == true)"
				+ "    print('s');"
				+ "  if(true == false)"
				+ "    print('d');"
				+ "}");
		assertEquals(output, "s");
		
		// not equal condition
		runCode("void main() {"
				+ "  if(true != true)"
				+ "    print('t');"
				+ "  if(true != false)"
				+ "    print('z');"
				+ "}");
		assertEquals(output, "z");

		// lesser condition
		try {
			runCode("void main() {"
					+ "  if(true < false)"
					+ "    print('a');"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// lesser equal condition
		try {
			runCode("void main() {"
					+ "  if(true <= false)"
					+ "    print('a');"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// greater condition
		try {
			runCode("void main() {"
					+ "  if(true > false)"
					+ "    print('a');"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
				
		// greater equal condition
		try {
			runCode("void main() {"
					+ "  if(true >= false)"
					+ "    print('a');"
					+ "}");
			fail("CompilerException not thrown");
		} catch(CompilerException e) {}
		
		// or inside condition
		runCode("void main() {"
				+ "  if(true || true)"
				+ "    print('a');"
				+ "  if(true || false)"
				+ "    print('b');"
				+ "  if(false || false)"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "ab");
		
		// and inside condition
		runCode("void main() {"
				+ "  if(true && true)"
				+ "    print('a');"
				+ "  if(true && false)"
				+ "    print('b');"
				+ "  if(false && false)"
				+ "    print('c');"
				+ "}");
		assertEquals(output, "a");
		
		// or inside condition
		runCode("void main() {"
				+ "  if(!true)"
				+ "    print('a');"
				+ "  if(!false)"
				+ "    print('b');"
				+ "}");
		assertEquals(output, "b");
		
		// call condition
		runCode("bool foo() {"
				+ "  return true;"
				+ "}"
				+ "bool bar() {"
				+ "  return false;"
				+ "}"
				+ "void main() {"
				+ "  if(foo())"
				+ "    print('f');"
				+ "  if(bar())"
				+ "    print('b');"
				+ "}");
		assertEquals(output, "f");
	}

	@Test
	public void testCall() throws Exception {
		// bool parameter
		runCode("void foo(bool b) {"
				+ "  printf(\"%d\", b);"
				+ "}"
				+ "void main() {"
				+ "  foo(true);"
				+ "}");
		assertEquals(output, "1");
		
		// int parameter
		runCode("void foo(int i) {"
				+ "  printf(\"%d\", i);"
				+ "}"
				+ "void main() {"
				+ "  foo(1254);"
				+ "}");
		assertEquals(output, "1254");
		
		// char parameter
		runCode("void foo(char c) {"
				+ "  printf(\"%c\", c);"
				+ "}"
				+ "void main() {"
				+ "  foo('d');"
				+ "}");
		assertEquals(output, "d");
		
		// float parameter
		runCode("void foo(float f) {"
				+ "  printf(\"%f\", f);"
				+ "}"
				+ "void main() {"
				+ "  foo(15.66);"
				+ "}");
		assertEquals(Float.parseFloat(output), 15.66, 0.001);
		
		// string parameter
		runCode("void foo(string s) {"
				+ "  printf(s);"
				+ "}"
				+ "void main() {"
				+ "  foo(\"Hello\");"
				+ "}");
		assertEquals(output, "Hello");
		
		// struct reference
		runCode("struct Point{int x,y;}"
				+ "void foo(Point p) {"
				+ "  printf(\"%d %d\", p.x, p.y);"
				+ "}"
				+ "void main() {"
				+ "  Point p1;"
				+ "  p1.x = 23;"
				+ "  p1.y = -10;"
				+ "  foo(p1);"
				+ "}");
		assertEquals(output, "23 -10");
	}	
	
	@Test
	public void testCallReference() throws Exception {
		// bool reference
		runCode("void foo(bool &b) {"
				+ "  b = false;"
				+ "}"
				+ "void main() {"
				+ "  bool b1 = true;"
				+ "  foo(b1);"
				+ "  printf(\"%d\", b1);"
				+ "}");
		assertEquals(output, "0");
		
		// int reference
		runCode("void foo(int &i) {"
				+ "  i = 5366;"
				+ "}"
				+ "void main() {"
				+ "  int i1 = 1234;"
				+ "  foo(i1);"
				+ "  printf(\"%d\", i1);"
				+ "}");
		assertEquals(output, "5366");
		
		// char reference
		runCode("void foo(char &c) {"
				+ "  c = 'h';"
				+ "}"
				+ "void main() {"
				+ "  char c1 = 'a';"
				+ "  foo(c1);"
				+ "  printf(\"%c\", c1);"
				+ "}");
		assertEquals(output, "h");
		
		// float reference
		runCode("void foo(float &f) {"
				+ "  f = 1.234;"
				+ "}"
				+ "void main() {"
				+ "  float f1 = -22.5;"
				+ "  foo(f1);"
				+ "  printf(\"%f\", f1);"
				+ "}");
		assertEquals(Float.parseFloat(output), 1.234, 0.001);
		
		// string reference
		runCode("void foo(string &s) {"
				+ "  s = \"foo_ref\";"
				+ "}"
				+ "void main() {"
				+ "  string s1 = \"abc\";"
				+ "  foo(s1);"
				+ "  printf(s1);"
				+ "}");
		assertEquals(output, "foo_ref");
		
		// struct reference
		runCode("struct Point{int x,y;}"
				+ "void foo(Point &p) {"
				+ "  p.x = 5;"
				+ "  p.y = 11;"
				+ "}"
				+ "void main() {"
				+ "  Point p1;"
				+ "  foo(p1);"
				+ "  printf(\"%d %d\", p1.x, p1.y);"
				+ "}");
		assertEquals(output, "5 11");
		
		// array reference
		runCode("void foo(int i[][]) {"
				+ "  i[5][3] = 5366;"
				+ "  i[2][2] = 988;"
				+ "}"
				+ "void main() {"
				+ "  int i1[10][5];"
				+ "  foo(i1);"
				+ "  printf(\"%d %d\", i1[5][3], i1[2][2]);"
				+ "}");
		assertEquals(output, "5366 988");
	}
	
	@Test
	public void testConst() throws Exception {
		// bool const
		runCode("const bool c_b = true;"
				+ "void main() {"
				+ "  printf(\"%d\", c_b);"
				+ "}");
		assertEquals(output, "1");
		
		// int const
		runCode("const int c_i = 1568;"
				+ "void main() {"
				+ "  printf(\"%d\", c_i);"
				+ "}");
		assertEquals(output, "1568");
		
		// char const
		runCode("const char c_c = 'c';"
				+ "void main() {"
				+ "  printf(\"%c\", c_c);"
				+ "}");
		assertEquals(output, "c");
		
		// float const
		runCode("const float c_f = 15.336;"
				+ "void main() {"
				+ "  printf(\"%f\", c_f);"
				+ "}");
		assertEquals(Float.parseFloat(output), 15.336, 0.001);
		
		// string const
		runCode("const string c_s = \"asdf\";"
				+ "void main() {"
				+ "  printf(c_s);"
				+ "}");
		assertEquals(output, "asdf");
	}
}
