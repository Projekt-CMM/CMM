package at.jku.ssw.cmm.gui.exception;

/**
 * This exception may be thrown by the preprocessor if an include file can't be found
 * 
 * @author fabian
 *
 */
public class IncludeNotFoundException extends Exception {

	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = -124;
	
	/**
	 * This exception may be thrown by the preprocessor if an include file can't be found
	 * 
	 * @param fileName Name of the library file
	 * @param line Line of include command
	 */
	public IncludeNotFoundException( String fileName, int line ){
		this.fileName = fileName;
		this.line = line;
	}
	
	/**
	 * Name of the library file
	 */
	private final String fileName;
	
	/**
	 * Line of include command
	 */
	private final int line;
	
	/**
	 * @return The name of the library file
	 */
	public String getFileName(){
		return this.fileName;
	}
	
	/**
	 * @return The line of the include command
	 */
	public int getLine(){
		return this.line;
	}

}
