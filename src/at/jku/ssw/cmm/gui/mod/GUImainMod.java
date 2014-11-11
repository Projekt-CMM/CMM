package at.jku.ssw.cmm.gui.mod;

import java.util.List;

import at.jku.ssw.cmm.gui.file.SaveDialog;

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
	 * 
	 * @return Name of the current file without path, eg "file2.cmm"
	 */
	String getFileName();
	
	/**
	 * 
	 * @return Name of the current cmm file with path, eg "demo/file2.cmm" <br>
	 * <i>WARNING: File path can be absolute or relative</i>
	 */
	String getFileNameAndPath();
	
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
	
	/**
	 * @return The complete path to the directory where the currently edited *.cmm file is saved
	 */
	String getWorkingDirectory();
	
	/**
	 * Moves the cursor to a specific line in the <b>user's</b> source code
	 * (highlights the whole line - variable "col" is useless at the moment)<br>
	 * Note: Method from interface <i>GUImod</i>
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param line The line which is considered to be highlighted
	 * @param col The column position [actually useless]
	 */
	public void highlightSourceCode( int line );
	
	/*
	 * Moves the cursor to a specific line in the <b>complete</b> source code
	 * (highlights the whole line - variable "col" is useless at the moment)<br>
	 * Note: Method from interface <i>GUImod</i>
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param line The line which is considered to be highlighted
	 * @param col The column position [actually useless]
	 */
	//public void highlightSourceCodeAbs( int line );
	
	/**
	 * Increments the input highlighter (input text area), which marks the already
	 * read characters, by one.
	 */
	void increaseInputHighlighter();

	/**
	 * Sets the input highlighter to 0.
	 */
	void resetInputHighlighter();
	
	/**
	 * Resets the output text panel so that there is no text displayed
	 */
	void resetOutputTextPane();
	
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

	/**
	 * Adds a breakpoint to the current line in the source code if there isn't yet any.
	 * Otherwise removes the breakpoint from the current line.
	 */
	void toggleBreakPoint();
	
	/**
	 * Invokes the Quest GUI window
	 */
	public void startQuestGUI();
	
	/**
	 * Invokes the profile selection dialog
	 */
	public void selectProfile();
	
	/**
	 * Saves the current *.cmm file if there are unsaved changes
	 */
	public void saveIfNecessary();
	
	public void setReadyMode();
	public void setErrorMode( int line );
	public void setRunMode();
	public void setPauseMode();

	public void setFileSaved();
	public SaveDialog getSaveManager();
}
