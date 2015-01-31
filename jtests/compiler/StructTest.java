package compiler;

import static org.junit.Assert.*;

import org.junit.Test;

import at.jku.ssw.cmm.compiler.Struct;

public class StructTest {

	@Test
	public void testStructInt() {
		// create some Struct from defined type, and check if size is correct
		Struct intStruct = new Struct(Struct.INT);
		assertEquals(intStruct.kind, Struct.INT);
		assertEquals(intStruct.size, 4);
		
		Struct floatStruct = new Struct(Struct.FLOAT);
		assertEquals(floatStruct.kind, Struct.FLOAT);
		assertEquals(floatStruct.size, 4);
		
		Struct charStruct = new Struct(Struct.CHAR);
		assertEquals(charStruct.kind, Struct.CHAR);
		assertEquals(charStruct.size, 2);
		
		Struct boolStruct = new Struct(Struct.BOOL);
		assertEquals(boolStruct.kind, Struct.BOOL);
		assertEquals(boolStruct.size, 1);
		
		Struct stringStruct = new Struct(Struct.STRING);
		assertEquals(stringStruct.kind, Struct.STRING);
		assertEquals(stringStruct.size, 4);
		
		// create Struct with unknow size
		Struct arrayStruct = new Struct(Struct.ARR);
		assertEquals(arrayStruct.kind, Struct.ARR);
		assertEquals(arrayStruct.size, 0);
		
		Struct structStruct = new Struct(Struct.STRUCT);
		assertEquals(structStruct.kind, Struct.STRUCT);
		assertEquals(structStruct.size, 0);
	}

	@Test
	public void testStructIntIntStruct() {
		Struct intArrayStruct = new Struct(Struct.ARR, 10, new Struct(Struct.INT));
		
		// test normal Array
		assertEquals(intArrayStruct.kind, Struct.ARR);
		assertEquals(intArrayStruct.elemType.equals(new Struct(Struct.INT)), true);
		assertEquals(intArrayStruct.elements, 10);
		assertEquals(intArrayStruct.size, 40);
		
		// test 2D-Array
		Struct intArray2DStruct = new Struct(Struct.ARR, 10, intArrayStruct);
		assertEquals(intArray2DStruct.kind, Struct.ARR);
		assertEquals(intArray2DStruct.elements, 10);
		assertEquals(intArray2DStruct.size, 400);
		
		// check for Null-Ptr-Exceptions
		Struct NullPtrExcptStruct = new Struct(Struct.ARR, 10, null);
		assertEquals(NullPtrExcptStruct.size, 0);
	}

	@Test
	public void testClone() {
		Struct baseStruct = new Struct(Struct.INT);
		Struct newStruct = null;
		
		try {
			newStruct = baseStruct.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			fail("CloneNotSupportedException");
			e.printStackTrace();
		}
		
		// check if the 2. structs are equal
		assertEquals(baseStruct.equals(newStruct), true);
		
		baseStruct.kind = Struct.FLOAT;
		
		// check if the 2. structs are equal
		assertEquals(baseStruct.equals(newStruct), false);
		
		assertEquals(baseStruct.kind, Struct.FLOAT);
		assertEquals(newStruct.kind, Struct.INT);
	}

	@Test
	public void testEqualsStruct() {
		// creating test-types
		Struct int1Struct = new Struct(Struct.INT);
		Struct int2Struct = new Struct(Struct.INT);
		
		Struct intArrayStruct = new Struct(Struct.ARR, 10, new Struct(Struct.INT));
		
		Struct floatStruct = new Struct(Struct.FLOAT);
		
		// test equal-function
		assertEquals(int1Struct.equals(int2Struct), true);
		assertEquals(floatStruct.equals(int1Struct), false);
		assertEquals(int2Struct.equals(floatStruct), false);
		
		assertEquals(int1Struct.equals(intArrayStruct), false);
		assertEquals(int1Struct.equals(intArrayStruct.elemType), true);
		
		// check for Null-Ptr-Exceptions
		assertEquals(int1Struct.equals(null), false);
	}

	@Test
	public void testIsPrimitive() {
		Struct intStruct = new Struct(Struct.INT);
		Struct floatStruct = new Struct(Struct.FLOAT);
		Struct charStruct = new Struct(Struct.CHAR);
		Struct boolStruct = new Struct(Struct.BOOL);
		Struct stringStruct = new Struct(Struct.STRING);
		Struct structStruct = new Struct(Struct.STRUCT);
		Struct arrayStruct = new Struct(Struct.ARR);
		
		assertEquals(intStruct.isPrimitive(), true);
		assertEquals(floatStruct.isPrimitive(), true);
		assertEquals(charStruct.isPrimitive(), true);
		assertEquals(boolStruct.isPrimitive(), true);
		assertEquals(stringStruct.isPrimitive(), false);
		assertEquals(structStruct.isPrimitive(), false);
		assertEquals(arrayStruct.isPrimitive(), false);
	}

}
