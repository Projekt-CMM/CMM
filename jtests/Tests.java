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
 
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import at.jku.ssw.cmm.compiler.Compiler;
import at.jku.ssw.cmm.compiler.Error;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.debugger.DebuggerMock;
import at.jku.ssw.cmm.interpreter.Interpreter;
import at.jku.ssw.cmm.interpreter.exceptions.StackOverflowException;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.debugger.StdInOut;

@SuppressWarnings("deprecation")
public class Tests implements StdInOut {

	List<Character> ref = new ArrayList<Character>(); 
	
//public char ref;

	public void run(String[] args) throws Exception {
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

			Interpreter interpreter = new Interpreter(new DebuggerMock(), this);

			interpreter.run(compiler.getSymbolTable());
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
		String[] a = { "interpretertests/file01.cmm"};//, "-debug", "1", "1" };
		System.out.println("\n" + a[0] + ":");
		run(a);
		Assert.assertEquals(1,(char) ref.get(0));
		Assert.assertEquals('c',(char) ref.get(1));
		Assert.assertEquals(1,(char) ref.get(2));
		
		ref.clear();
	}

	/*
	 * +  - *  / - Operatoren
	 */
	@Test
	public void test2() throws Exception {
		String[] a = { "interpretertests/file02.cmm" };
		System.out.println("\n" + a[0] + ":");
		run(a);

		Assert.assertEquals(5,(char) ref.get(0));
		Assert.assertEquals(10,(char) ref.get(1));
		Assert.assertEquals(5,(char) ref.get(2));
		Assert.assertEquals(50,(char) ref.get(3));
		Assert.assertEquals(2,(char) ref.get(4));
		ref.clear();
	}
	
	/*
	 * INT Array
	 */
	/*@Test
	public void test3() throws Exception{
		String[] a = { "interpretertests/file03.cmm" };
		System.out.println("\n" + a[0] + ":");
		run(a);

		Assert.assertEquals(10,(char) ref.get(0));
		Assert.assertEquals(20,(char) ref.get(1));
		Assert.assertEquals(30,(char) ref.get(2));
		Assert.assertEquals(40,(char) ref.get(3));
		Assert.assertEquals(50,(char) ref.get(4));
		ref.clear();
	}
	
	/*
	 * Char Array
	 */
	
	/*@Test
	public void test4() throws Exception{
		String[] a = { "interpretertests/file04.cmm" };
		System.out.println("\n" + a[0] + ":");
		run(a);

		Assert.assertEquals('a',(char) ref.get(0));
		Assert.assertEquals('b',(char) ref.get(1));
		Assert.assertEquals('c',(char) ref.get(2));
		Assert.assertEquals('d',(char) ref.get(3));
		Assert.assertEquals('e',(char) ref.get(4));
		ref.clear();
	}

	/*
	 * struct
	 */
	
	@Test
	public void test5() throws Exception{
		String[] a = { "interpretertests/file05.cmm" };
		System.out.println("\n" + a[0] + ":");
		run(a);

		Assert.assertEquals(10,(char) ref.get(0));
		Assert.assertEquals('a',(char) ref.get(1));
		ref.clear();
	}	

	/*
	 * If - Else
	 */
	@Test
	public void test6() throws Exception{
		String[] a = { "interpretertests/file06.cmm" };
		System.out.println("\n" + a[0] + ":");
		run(a);

		Assert.assertEquals(20,(char) ref.get(0));
		ref.clear();
	}	

	/*
	 * While
	 */
	@Test
	public void test7() throws Exception{
		String[] a = { "interpretertests/file07.cmm" };
		System.out.println("\n" + a[0] + ":");
		run(a);

		Assert.assertEquals(11,(char) ref.get(0));
		ref.clear();
	}	
	
	/*
	 * String
	 */
	@Test
	public void test8() throws Exception{
		String[] a = { "interpretertests/file08.cmm" };
		System.out.println("\n" + a[0] + ":");
		run(a);

		Assert.assertEquals('l',(char) ref.get(0));
		ref.clear();
	}
	
	/*
	 * methods:
	 * Minus Plus Inc. Return 
	 */
	
	@Test
	public void test9() throws Exception{
		String[] a = { "interpretertests/file09.cmm" };
		System.out.println("\n" + a[0] + ":");
		run(a);

		Assert.assertEquals(25,(char) ref.get(0));
		Assert.assertEquals(15,(char) ref.get(1));
		ref.clear();
	}
	
	/*
	 * methods
	 * multiply divide
	 */
	
	@Test
	public void test10() throws Exception{
		String[] a = { "interpretertests/file10.cmm"};
		System.out.println("\n" + a[0] + ":");
		run(a);

		Assert.assertEquals(0,(char) ref.get(0));
		Assert.assertEquals(0,(char) ref.get(1));
		ref.clear();
	}
/*
 * While, Arrays, Etc
 */
	
	/*@Test
	public void test11() throws Exception{
		String[] a = { "interpretertests/file11.cmm" };
		System.out.println("\n" + a[0] + ":");
		run(a);

		Assert.assertEquals(6,(char) ref.get(0));
		Assert.assertEquals('c',(char) ref.get(1));
		Assert.assertEquals(155,(char) ref.get(2)); 
		Assert.assertEquals(55,(char) ref.get(3));
		Assert.assertEquals('g',(char) ref.get(4));	
		Assert.assertEquals('c',(char) ref.get(5));	
		ref.clear();
	}
	
	/*
	 * If, Return + Recursion
	 */
	@Test
	public void test12() throws Exception{
		String[] a = { "interpretertests/file12.cmm"};
		System.out.println("\n" + a[0] + ":");
		run(a);
		
		Assert.assertEquals(13,(char) ref.get(0));
		ref.clear();
	}
	
	/*
	 * Globale Variablen
	 * multidimensional Arrays + while
	 */
	
	/*@Test
	public void test13() throws Exception{
		String[] a = { "interpretertests/file13.cmm"};
		System.out.println("\n" + a[0] + ":");
		run(a);
		
		Assert.assertEquals(276,(char) ref.get(0));
		ref.clear();
	}	
	
	/*
	 * Without Global Variable
	 * multidimensional Array + while
	 */
	/*@Test
	public void test14() throws Exception{
		String[] a = { "interpretertests/file14.cmm"};
		System.out.println("\n" + a[0] + ":");
		run(a);
		
		Assert.assertEquals(276,(char) ref.get(0));
		ref.clear();
	}
	
	/*
	 * 3 dimensional Array	
	 */
	/*@Test
	public void test15() throws Exception{
		String[] a = { "interpretertests/file15.cmm"};
		System.out.println("\n" + a[0] + ":");
		run(a);
		
		Assert.assertEquals(5,(char) ref.get(0));
		ref.clear();
	}
	/*
	 *  2 dimensional Array	
	 */
	/*@Test
	public void test16() throws Exception{
		String[] a = { "interpretertests/file16.cmm"};
		System.out.println("\n" + a[0] + ":");
		run(a);
		
		Assert.assertEquals(10,(char) ref.get(0));
		ref.clear();
	}

	/*
	 *  1 dimensional Array
	 */
	/*@Test
	public void test17() throws Exception{
		String[] a = { "interpretertests/file17.cmm"};
		System.out.println("\n" + a[0] + ":");
		run(a);
		
		Assert.assertEquals(10,(char) ref.get(0));
		ref.clear();
	}
	
	/*
	 * Method with Return Value
	 */
	
	@Test
	public void test18() throws Exception{
		String[] a = { "interpretertests/file18.cmm"};
		System.out.println("\n" + a[0] + ":");
		run(a);
		
		Assert.assertEquals(9,(char) ref.get(0));
		ref.clear();
	}
	
	/*
	 * Strings Ausgabe mit stdio.h
	 */
	
	@Test
	public void test19() throws Exception{
		String[] a = { "interpretertests/file19.cmm"};
		System.out.println("\n" + a[0] + ":");
		run(a);
		
		Assert.assertEquals('h',(char) ref.get(0));
		Assert.assertEquals('a',(char) ref.get(1));
		Assert.assertEquals('l',(char) ref.get(2));
		Assert.assertEquals('l',(char) ref.get(3));
		Assert.assertEquals('o',(char) ref.get(4));
		ref.clear();
	}
	
	/*
	 * Referenzen	
	 */
	@Test
	public void test20() throws Exception{
		String[] a = { "interpretertests/file20.cmm" , "-debug", "1", "1" };
		System.out.println("\n" + a[0] + ":");
		run(a);
		
		Assert.assertEquals(1,(char) ref.get(0));
		Assert.assertEquals('a',(char) ref.get(1));
		Assert.assertEquals(2,(char) ref.get(2));
		ref.clear();
	}

	/*
	 * Referenzen Char
	 */
	@Test
	public void test21() throws Exception{
		String[] a = { "interpretertests/file21.cmm" };
		System.out.println("\n" + a[0] + ":");
		run(a);
		
		Assert.assertEquals('a',(char) ref.get(0));
		ref.clear();
	}
	/*
	 * @see at.jku.ssw.cmm.debugger.StdInOut#in()
	 */
	
	@Override
	public char in() {
		return 0;
	}

	@Override
	public void out(char arg0) {
		ref.add(arg0);
	}

}
