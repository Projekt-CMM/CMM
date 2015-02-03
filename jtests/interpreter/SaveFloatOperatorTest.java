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

import org.junit.Ignore;
import org.junit.Test;

import at.jku.ssw.cmm.interpreter.SaveFloatOperator;

public class SaveFloatOperatorTest {

	@Test
	public void testAdd() {
		// test problematic values
		assertEquals(SaveFloatOperator.add((float)0.0, (float)0.0), (float)0.0 + (float)0.0, 1e-4);
		assertEquals(SaveFloatOperator.add(Float.MAX_VALUE, (float)0.0), Float.MAX_VALUE, Float.MAX_VALUE/1000);
		assertEquals(SaveFloatOperator.add((float)0.0, Float.MAX_VALUE), Float.MAX_VALUE, Float.MAX_VALUE/1000);
		assertEquals(SaveFloatOperator.add(Float.MIN_VALUE, (float)0.0), Float.MIN_VALUE, Float.MIN_VALUE/1000);
		assertEquals(SaveFloatOperator.add((float)0.0, Float.MIN_VALUE), Float.MIN_VALUE, Float.MIN_VALUE/1000);
			
		// test normal operations in all variants
		assertEquals(SaveFloatOperator.add((float)1.5, (float)1.3), (float)1.5 + (float)1.3, 1e-4);
		assertEquals(SaveFloatOperator.add((float)0.0, (float)5.3), (float)0.0 + (float)5.3, 1e-4);
		assertEquals(SaveFloatOperator.add((float)2.0, (float)-5.3), (float)2.0 + (float)(-5.3), 1e-4);
	}

	@Test
	public void testSubtract() {
		// test problematic values
		assertEquals(SaveFloatOperator.subtract((float)0.0, (float)0.0), (float)0.0 - (float)0.0, 1e-4);
		assertEquals(SaveFloatOperator.subtract(Float.MAX_VALUE, (float)0.0), Float.MAX_VALUE, Float.MAX_VALUE/1000);
		assertEquals(SaveFloatOperator.subtract(Float.MIN_VALUE, (float)0.0), Float.MIN_VALUE, Float.MIN_VALUE/1000);
		
		// test normal operations in all variants
		assertEquals(SaveFloatOperator.subtract((float)1.5, (float)1.3), (float)1.5 - (float)1.3, 1e-4);
		assertEquals(SaveFloatOperator.subtract((float)0.0, (float)5.3), (float)0.0 - (float)5.3, 1e-4);
		assertEquals(SaveFloatOperator.subtract((float)2.0, (float)-5.3), (float)2.0 - (float)(-5.3), 1e-4);
		
		// test wrong values
		// TODO
	}

	@Ignore
	@Test
	public void testMultiply() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testDivide() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testNegate() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testAbs() {
		fail("Not yet implemented");
	}

}
