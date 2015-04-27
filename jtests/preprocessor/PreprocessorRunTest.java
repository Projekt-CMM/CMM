package preprocessor;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import at.jku.ssw.cmm.compiler.Compiler;
import at.jku.ssw.cmm.compiler.CompilerException;
import at.jku.ssw.cmm.compiler.Error;
import at.jku.ssw.cmm.debugger.DebuggerMock;
import at.jku.ssw.cmm.debugger.StdInOut;
import at.jku.ssw.cmm.interpreter.Interpreter;
import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.preprocessor.Preprocessor;
import at.jku.ssw.cmm.preprocessor.exception.PreprocessorException;

public class PreprocessorRunTest implements StdInOut {
	String output = new String();
	String input = new String();

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
		
		List<Object[]> codeRegister = new ArrayList();
		List<Integer> breakpoints = new ArrayList();
		String preprocessorOutput = preprocessorOutput = Preprocessor.expand(new String(code), "./clib", codeRegister);
		
		Compiler compiler = new Compiler();

		compiler.compile(preprocessorOutput);

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

	@Test(timeout=1000)
	public void testComment() throws Exception {
		// simple #define which is commented
		runCode("//#define __DEF__\n"
				+ "void main() {\n"
				+ "#ifdef __DEF__\n"
				+ "  print('a');\n"
				+ "#else\n"
				+ "  print('b');\n"
				+ "#endif\n"
				+ "}");
		assertEquals(output, "b");
		
		//TODO: fix bug
		// simple #define which is commented
		//runCode("/*#define __DEF__ \n"
		//		+ "*/void main() {\n"
		//		+ "#ifdef __DEF__\n"
		//		+ "  print('c');\n"
		//		+ "#else\n"
		//		+ "  print('d');\n"
		//		+ "#endif\n"
		//		+ "}");
		//assertEquals(output, "d");
		
		// simple #define which is commented
		runCode("/* cda */#define __DEF__/* asdf */\n"
				+ "void main() {\n"
				+ "#ifdef __DEF__\n"
				+ "  print('e');\n"
				+ "#else\n"
				+ "  print('f');\n"
				+ "#endif\n"
				+ "}");
		assertEquals(output, "e");
	}
	
	@Test(timeout=1000)
	public void testIf() throws Exception {
		// simple #ifdef
		runCode("#define __DEF__\n"
				+ "void main() {\n"
				+ "#ifdef __DEF__\n"
				+ "  print('a');\n"
				+ "#else\n"
				+ "  print('b');\n"
				+ "#endif\n"
				+ "}");
		assertEquals(output, "a");
		
		// simple #ifdef
		runCode("void main() {\n"
				+ "#ifdef __DEF__\n"
				+ "  print('c');\n"
				+ "#else\n"
				+ "  print('d');\n"
				+ "#endif\n"
				+ "}");
		assertEquals(output, "d");

		// simple #ifndef
		runCode("#define __DEF__\n"
				+ "void main() {\n"
				+ "#ifndef __DEF__\n"
				+ "  print('e');\n"
				+ "#else\n"
				+ "  print('f');\n"
				+ "#endif\n"
				+ "}");
		assertEquals(output, "f");
		
		// simple #ifndef
		runCode("void main() {\n"
				+ "#ifndef __DEF__\n"
				+ "  print('g');\n"
				+ "#else\n"
				+ "  print('h');\n"
				+ "#endif\n"
				+ "}");
		assertEquals(output, "g");
		
		// random #else
		try {
			runCode("void main() {\n"
					+ "#else\n"
					+ "}");
			fail("PreprocessorException not thrown");
		} catch(PreprocessorException e) {}
		
		// random #endif
		try {
			runCode("void main() {\n"
					+ "#endif\n"
					+ "}");
			fail("PreprocessorException not thrown");
		} catch(PreprocessorException e) {}
		
		// no end tag
		try {
			runCode("void main() {\n"
					+ "#ifdef asdf\n"
					+ "}");
			fail("PreprocessorException not thrown");
		} catch(PreprocessorException e) {}
		
	}

}
