package at.jku.ssw.cmm.compiler;

public class CompilerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8441803230213480324L;

	public CompilerException( String msg, int line){
		this.msg = msg;
		this.line = line;
	}
	
	private final String msg;
	private final int line;
	
	public String getMessage(){
		return msg;
	}

	public int getLine(){
		return line;
	}
}
