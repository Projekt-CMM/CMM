package at.jku.cmm.compiler;

/*--------------------------------------------------------------------------------
Error   Error info
=====   ==========
Holds information about a syntax error or a semantic error in a C-- program.
--------------------------------------------------------------------------------*/

public class Error {
	public int    line;
	public int    col;
	public String msg;
	public Error  next;
	public Error(int line, int col, String msg) {
		this.line = line; this.col = col; this.msg = msg;
	}
}
