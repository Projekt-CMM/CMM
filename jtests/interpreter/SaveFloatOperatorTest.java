package interpreter;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import at.jku.ssw.cmm.interpreter.SaveFloatOperator;

public class SaveFloatOperatorTest {

	@Test
	public void testAdd() {
		// test problematic values
		assertEquals(SaveFloatOperator.add((float)0.0,(float)0.0), (float)0.0 + (float)0.0, 1e-4);
			
		// test normal operations in all variants
		assertEquals(SaveFloatOperator.add((float)1.5,(float)1.3), (float)1.5 + (float)1.3, 1e-4);
		assertEquals(SaveFloatOperator.add((float)0.0,(float)5.3), (float)0.0 + (float)5.3, 1e-4);
		assertEquals(SaveFloatOperator.add((float)2.0,(float)-5.3), (float)2.0 + (float)(-5.3), 1e-4);
		
		// test wrong values
		// TODO
	}

	@Test
	public void testSubtract() {
		// test problematic values
		assertEquals(SaveFloatOperator.subtract((float)0.0,(float)0.0), (float)0.0 - (float)0.0, 1e-4);
			
		// test normal operations in all variants
		assertEquals(SaveFloatOperator.subtract((float)1.5,(float)1.3), (float)1.5 - (float)1.3, 1e-4);
		assertEquals(SaveFloatOperator.subtract((float)0.0,(float)5.3), (float)0.0 - (float)5.3, 1e-4);
		assertEquals(SaveFloatOperator.subtract((float)2.0,(float)-5.3), (float)2.0 - (float)(-5.3), 1e-4);
		
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
