package compiler;

import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.compiler.Struct;
import static org.junit.Assert.*;

import org.junit.Test;

public class ObjTest {

	@Test
	public void testObj() {
		Struct intStruct = new Struct(Struct.INT);
		
		Obj newObj = new Obj(Obj.CON, "testObj", intStruct, 1);
		assertEquals(newObj.kind, Obj.CON);
		assertEquals(newObj.name, "testObj");
		assertEquals(newObj.line, 1);
		assertEquals(newObj.type, intStruct);
	}
}
