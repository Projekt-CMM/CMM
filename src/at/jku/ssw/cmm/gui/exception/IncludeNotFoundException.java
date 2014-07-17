package at.jku.ssw.cmm.gui.exception;

public class IncludeNotFoundException extends Exception {

	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = -124;
	
	public IncludeNotFoundException( String fileName, int line ){
		this.fileName = fileName;
		this.line = line;
	}
	
	private final String fileName;
	private final int line;
	
	public String getFileName(){
		return this.fileName;
	}
	
	public int getLine(){
		return this.line;
	}

}
