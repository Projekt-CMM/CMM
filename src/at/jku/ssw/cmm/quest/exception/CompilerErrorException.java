package at.jku.ssw.cmm.quest.exception;

public class CompilerErrorException extends Exception {

	private static final long serialVersionUID = -4173237209260462454L;
	
	public CompilerErrorException( String message, at.jku.ssw.cmm.compiler.Error e ){
		this.message = message;
		this.e = e;
	}
	
	private final String message;
	private final at.jku.ssw.cmm.compiler.Error e;
	
	public String getMessage(){
		return this.message;
	}
	
	public at.jku.ssw.cmm.compiler.Error getError(){
		return this.e;
	}

}
