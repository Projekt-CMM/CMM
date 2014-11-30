package at.jku.ssw.cmm.debugger;

import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;

/**
 * This interface controls the input and output stream of the interpreter
 * 
 * @author fabian
 */
public interface StdInOut {
	
	/**
	 * Reads a character from the input stream and passes it to the interpreter
	 * 
	 * @return
	 * @throws RunTimeException
	 */
	public char in() throws RunTimeException;
	
	/**
	 * Passes a character from the interpreter to the output stream of the GUI
	 * 
	 * @param arg0
	 */
	public void out(char arg0);
}
