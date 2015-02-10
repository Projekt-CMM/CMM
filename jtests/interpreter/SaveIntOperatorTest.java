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

import org.junit.Test;

import at.jku.ssw.cmm.interpreter.SaveIntOperator;

public class SaveIntOperatorTest {

	@Test
	public void testAdd() {
		// test problematic values
		assertEquals(SaveIntOperator.add(0, 0), 0);
		assertEquals(SaveIntOperator.add(0, Integer.MAX_VALUE), Integer.MAX_VALUE);
		assertEquals(SaveIntOperator.add(Integer.MAX_VALUE, 0), Integer.MAX_VALUE);
		assertEquals(SaveIntOperator.add(0, Integer.MIN_VALUE), Integer.MIN_VALUE);
		assertEquals(SaveIntOperator.add(Integer.MIN_VALUE, 0), Integer.MIN_VALUE);
		
		// test normal operations
		assertEquals(SaveIntOperator.add(500, 20), 520);
		assertEquals(SaveIntOperator.add(123456, 9876), 123456+9876);
		assertEquals(SaveIntOperator.add(123456, -9876), 123456-9876);

		// test wrong values
		try {
			SaveIntOperator.add(Integer.MAX_VALUE, 1);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
		
		try {
			SaveIntOperator.add(1, Integer.MAX_VALUE);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
		
		try {
			SaveIntOperator.add(-1, Integer.MIN_VALUE);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
		
		try {
			SaveIntOperator.add(Integer.MIN_VALUE, -1);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
		
		try {
			SaveIntOperator.add(Integer.MAX_VALUE, Integer.MAX_VALUE);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
		
		try {
			SaveIntOperator.add(Integer.MIN_VALUE, Integer.MIN_VALUE);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
	}

	@Test
	public void testSubtract() {
		// test problematic values
		assertEquals(SaveIntOperator.subtract(0, 0), 0);
		assertEquals(SaveIntOperator.subtract(0, Integer.MAX_VALUE), -Integer.MAX_VALUE);
		assertEquals(SaveIntOperator.subtract(Integer.MAX_VALUE, 0), Integer.MAX_VALUE);
		assertEquals(SaveIntOperator.subtract(Integer.MIN_VALUE, 0), Integer.MIN_VALUE);
		assertEquals(SaveIntOperator.subtract(Integer.MAX_VALUE, Integer.MAX_VALUE), 0);
		assertEquals(SaveIntOperator.subtract(Integer.MIN_VALUE, Integer.MIN_VALUE), 0);
		
		// test normal operations
		assertEquals(SaveIntOperator.subtract(500, 20), 480);
		assertEquals(SaveIntOperator.subtract(123456, 9876), 123456-9876);
		assertEquals(SaveIntOperator.subtract(123456, -9876), 123456+9876);
		assertEquals(SaveIntOperator.subtract(-56, -9876), -56+9876);
		
		// test wrong values
		try {
			SaveIntOperator.subtract(Integer.MAX_VALUE, -1);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
		
		try {
			SaveIntOperator.subtract(Integer.MIN_VALUE, 1);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
		
		try {
			SaveIntOperator.subtract(Integer.MIN_VALUE, Integer.MAX_VALUE);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
	}

	@Test
	public void testMultiply() {
		// test problematic values
		assertEquals(SaveIntOperator.multiply(0, 0), 0 * 0);
		assertEquals(SaveIntOperator.multiply(0, Integer.MAX_VALUE), 0);
		assertEquals(SaveIntOperator.multiply(Integer.MAX_VALUE, 0), 0);
		assertEquals(SaveIntOperator.multiply(0, Integer.MAX_VALUE), 0);
		assertEquals(SaveIntOperator.multiply(Integer.MIN_VALUE, 0), 0);
		assertEquals(SaveIntOperator.multiply(0, Integer.MIN_VALUE), 0);
		assertEquals(SaveIntOperator.multiply(Integer.MAX_VALUE, 1), Integer.MAX_VALUE);
		assertEquals(SaveIntOperator.multiply(1, Integer.MAX_VALUE), Integer.MAX_VALUE);
		assertEquals(SaveIntOperator.multiply(Integer.MIN_VALUE, 1), Integer.MIN_VALUE);
		assertEquals(SaveIntOperator.multiply(1, Integer.MIN_VALUE), Integer.MIN_VALUE);
		assertEquals(SaveIntOperator.multiply(2, -1073741824), 2 * (-1073741824));
		assertEquals(SaveIntOperator.multiply(-134217728, 16), (-134217728) * 16);
		
		// test normal operations
		assertEquals(SaveIntOperator.multiply(500, 20), 500 * 20);
		assertEquals(SaveIntOperator.multiply(123, -20), 123 * (-20));
		assertEquals(SaveIntOperator.multiply(-512, 20), (-512) * 20);
		assertEquals(SaveIntOperator.multiply(-5, -20), (-5) * (-20));
		
		// test wrong values
		try {
			SaveIntOperator.multiply(Integer.MAX_VALUE, Integer.MAX_VALUE);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
		
		try {
			SaveIntOperator.multiply(Integer.MIN_VALUE, Integer.MIN_VALUE);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
	}

	@Test
	public void testDivide() {
		// test problematic values
		assertEquals(SaveIntOperator.divide(0, 1), 0);
		assertEquals(SaveIntOperator.divide(1, 1), 1);
		assertEquals(SaveIntOperator.divide(Integer.MAX_VALUE, 1), Integer.MAX_VALUE);
		assertEquals(SaveIntOperator.divide(Integer.MIN_VALUE, 1), Integer.MIN_VALUE);
		assertEquals(SaveIntOperator.divide(Integer.MAX_VALUE, Integer.MAX_VALUE), 1);
		assertEquals(SaveIntOperator.divide(Integer.MIN_VALUE, Integer.MIN_VALUE), 1);
		
		// test normal operations
		assertEquals(SaveIntOperator.divide(500, 5), 500 / 5);
		assertEquals(SaveIntOperator.divide(500, -4), 500 / (-4));
		
		// test wrong values
		try {
			SaveIntOperator.divide(1, 0);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
		
		try {
			SaveIntOperator.divide(Integer.MAX_VALUE, 0);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
		
		try {
			SaveIntOperator.divide(Integer.MIN_VALUE, 0);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
	}

	@Test
	public void testNegate() {
		// test problematic values
		assertEquals(SaveIntOperator.negate(0), 0);
		assertEquals(SaveIntOperator.negate(1), -1);
		assertEquals(SaveIntOperator.negate(-1), 1);
		assertEquals(SaveIntOperator.negate(Integer.MAX_VALUE), -Integer.MAX_VALUE);
		assertEquals(SaveIntOperator.negate(-Integer.MAX_VALUE), Integer.MAX_VALUE);
		
		// test normal operations
		assertEquals(SaveIntOperator.negate(123456), -123456);
		assertEquals(SaveIntOperator.negate(-565), 565);
		
		// test wrong values
		try {
			SaveIntOperator.negate(Integer.MIN_VALUE);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
	}

	@Test
	public void testAbs() {
		// test problematic values
		assertEquals(SaveIntOperator.abs(0), 0);
		assertEquals(SaveIntOperator.abs(1), 1);
		assertEquals(SaveIntOperator.abs(-1), 1);
		assertEquals(SaveIntOperator.abs(Integer.MAX_VALUE), Integer.MAX_VALUE);
		assertEquals(SaveIntOperator.abs(Integer.MIN_VALUE+1), (Integer.MIN_VALUE+1)*-1);
		
		// test normal operations
		assertEquals(SaveIntOperator.abs(12345), 12345);
		assertEquals(SaveIntOperator.abs(-56889), 56889);
		
		// test wrong values
		try {
			SaveIntOperator.abs(Integer.MIN_VALUE);
			fail("ArithmeticException not thrown");
		} catch(ArithmeticException e) {}
	}

}
