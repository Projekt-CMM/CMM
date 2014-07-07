package cmm.compiler;

/*--------------------------------------------------------------------------------
Compiler   C-- compiler
========   ============
This is the top class of the C-- compiler. It contains methods for compiling
C-- programs, retrieving the symbol table and the errors. The AST of every
procedure is stored in the procedure's object in the symbol table.

Debug switches:
0: dump the symbol table
1: dump the AST
--------------------------------------------------------------------------------*/
import java.io.ByteArrayInputStream;

public final class Compiler {

	private Parser parser;
	public  boolean[] debug = new boolean[10]; // debug switches

	// Compile the source code and build an AST as well as a symbol table
	public void compile(String source) {
		Scanner scanner = new Scanner(new ByteArrayInputStream(source.getBytes()));
		parser = new Parser(scanner);
		parser.debug = debug;
		parser.Parse();
	}

	// Retrieve the symbol table after compilation
	public Tab getSymbolTable() {
		return parser.tab;
	}

	// Retrieve the first error (or null)
	// More errors are linked via a "next" pointer
	public Error getError() {
		return parser.errors.head;
	}

}
