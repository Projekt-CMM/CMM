package preprocessor;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import at.jku.ssw.cmm.preprocessor.Preprocessor;

public class PreprocessorTest {

	@Ignore
	@Test
	public void testExpand() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testParseFile() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testAddString() {
		fail("Not yet implemented");
	}

	@Test
	public void testCountLines() {
		// test normal operations in all variants
		assertEquals(Preprocessor.countLines("first"),1);
		assertEquals(Preprocessor.countLines("first \n second"),2);
		assertEquals(Preprocessor.countLines("first \r second"),2);
		assertEquals(Preprocessor.countLines("first \r\n second"),2);
		assertEquals(Preprocessor.countLines("first \n second \n third"),3);
		
		// check for Null-Ptr-Exceptions
		assertEquals(Preprocessor.countLines(null),0);
	}

	@Test
	public void testReturnFileAndNumber() {
		// Build Object List
		List<Object[]> codeRegister = new ArrayList<>();
		Object[] firstCodeInsert = {1, 10, "alpha"};
		codeRegister.add(firstCodeInsert);
		Object[] secondCodeInsert = {11, 15, "beta"};
		codeRegister.add(secondCodeInsert);
		Object[] thirdCodeInsert = {16, 20, "alpha"};
		codeRegister.add(thirdCodeInsert);

		// test function
		Object[] result1 = Preprocessor.returnFileAndNumber(5, codeRegister);
		assertEquals(result1[0].toString(),"alpha");
		assertEquals((int)result1[1],5);

		Object[] result2 = Preprocessor.returnFileAndNumber(12, codeRegister);
		assertEquals(result2[0].toString(),"beta");
		assertEquals((int)result2[1],2);

		Object[] result3 = Preprocessor.returnFileAndNumber(17, codeRegister);
		assertEquals(result3[0].toString(),"alpha");
		assertEquals((int)result3[1],12);
	}

}
