package compiler;

import at.jku.ssw.cmm.compiler.Strings;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class StringsTest {

	@Test
	public void testPut() {
		Strings.clear();

		// test adding of simple strings
		assertEquals(Strings.put("test1"), 0);
		assertEquals(Strings.put("test2"), 6);
		assertEquals(Strings.put("test3"), 12);
		
		// already existing strings are reused
		assertEquals(Strings.put("test1"), 0);
		assertEquals(Strings.put("test2"), 6);
		assertEquals(Strings.put("test3"), 12);
		
		// add 5000 strings to check automatic array size change
		for(int i = 0;i < 5000; i++) {
			Strings.put("string_" + i);
		}
		
		// strings are not allowed to be deleted from array
		assertEquals(Strings.get(12), "test3");
	}

	@Test
	public void testGet() {
		Strings.clear();

		// put new string into array
		int stringPos = Strings.put("this is a test string");
		
		// check if string can be fetched using position
		assertEquals(Strings.get(stringPos), "this is a test string");
		assertEquals(Strings.get(stringPos+5), "is a test string");
	}

	@Test
	public void testCharAt() {
		Strings.clear();
		
		// put new string into array
		int stringPos = Strings.put("this is a test string");
		
		// fetch single characters
		assertEquals(Strings.charAt(stringPos), 't');
		assertEquals(Strings.charAt(stringPos+1), 'h');
		assertEquals(Strings.charAt(stringPos+2), 'i');
		assertEquals(Strings.charAt(stringPos+3), 's');
		assertEquals(Strings.charAt(stringPos+21), '\0');
		assertEquals(Strings.charAt(stringPos+22), ' ');
	}

	@Test
	public void testCheckAdr() {
		Strings.clear();
		
		// put new string into array
		int stringPos = Strings.put("this is a test string");
		
		// fetch single characters
		assertEquals(Strings.checkAdr(stringPos), true);
		assertEquals(Strings.checkAdr(stringPos+1), true);
		assertEquals(Strings.checkAdr(stringPos+21), true);
		assertEquals(Strings.checkAdr(stringPos+22), false);
	}
	
	@Test
	public void testClear() {
		Strings.clear();
		
		// test adding of simple strings
		assertEquals(Strings.put("test1"), 0);
		assertEquals(Strings.put("test2"), 6);
		
		assertEquals(Strings.put("test2"), 6);
		
		Strings.clear();
		
		assertEquals(Strings.put("test2"), 0);
		assertEquals(Strings.put("new test"), 6);
	}

}
