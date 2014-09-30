package at.jku.ssw.cmm.gui.mod;

import java.util.List;

public interface GUImainMod {
	
	/**
	 * Repaints the main GUI.
	 * Note: Method from interface <i>GUImod</i>
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 */
	public void repaint();
	
	/**
	 * Sets the title of the main GUI window.
	 * Note: Method from interface <i>GUImod</i>
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 */
	void updateWinFileName();
	
	/**
	 * Note: Method from interface <i>GUImod</i>
	 * 
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @return The source code written in the source code text area of the main GUI
	 */
	public String getSourceCode();
	
	/**
	 * The source code register is a list object which saves where the specific parts of the compiled
	 * code (not the code on the screen) are from. The data is saved as follows:<br>
	 * The list contains arrays of three objects which save:
	 * <ol>
	 * <li>The start line (in the compiled code) of the sequence</li>
	 * <li>The end line</li>
	 * <li>The origin as String, eg. "test2.cmm" or "original file"</li>
	 * </ol>
	 * 
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @return The source code register list
	 */
	public List<Object[]> getSourceCodeRegister();
	
	String getWorkingDirectory();
	
	/**
	 * Moves the cursor to a specific line in the source code
	 * (highlights the whole line - variable "col" is useless at the moment)<br>
	 * Note: Method from interface <i>GUImod</i>
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param line The line which is considered to be highlighted
	 * @param col The column position [actually useless]
	 */
	public void highlightSourceCode( int line, int col );
	
	void increaseInputHighlighter();

	void resetInputHighlighter();
	
	/**
	 * Makes all text fields of the main GUI uneditable. Should happen before interpreter starts running
	 * so that the source code can't be changed during runtime.
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 */
	public void lockInput();
	
	/**
	 * Makes all text fields of the main GUI editable. Should happen after the interpreter has
	 * finished running.
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 */
	public void unlockInput();
	
	/**
	 * Shows the given String on the output text area of the main GUI. Used for the output
	 * stream of the interpreter.
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param s The output stream as String
	 */
	public void outputStream( String s );
	
	/**
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @return The text in the input text area of the main GUI
	 */
	public String getInputStream();

	void toggleBreakPoint();
	
	public void startQuestGUI();
	
	public void selectProfile();
}
