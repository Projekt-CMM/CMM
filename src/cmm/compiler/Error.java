package cmm.compiler;

/*--------------------------------------------------------------------------------
Error   Error info
=====   ==========
Holds information about a syntax error or a semantic error in a C-- program.
--------------------------------------------------------------------------------*/

public class Error {
	int    line;
	int    col;
	String msg;
	Error  next;
	Error(int line, int col, String msg) {
		this.line = line; this.col = col; this.msg = msg;
	}
}
