package at.jku.ssw.cmm.gui.mod;

public interface GUIrPanelMod {
	
	/**
	 * Changes the mode of the right panel in the main GUI: <br>
	 * ID 0 = "edit" mode - editing source code<br>
	 * ID 1 = "error" mode - compiler errors<br>
	 * ID 2 = "run" mode - interpreter runs<br>
	 * 
	 * <b>WARNING: This method does NOT kill the interpreter thread or clean up data of other modes.</b>
	 * Do not only use this method if you want to change the right panel mode.
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param id The mode ID described above
	 */
	//public void setRightPanel( int id );
	
	/**
	 * Sets the right panel of the GUI to "error" mode and displays the error message given. This method
	 * will work cleanly if you call it form the "edit" mode. It will probably not work propery from "run" mode
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param msg Error message as String
	 * @param line Line where the error occurred
	 * @param col Column where the error occurred
	 */
	public void setError( String msg, int line, int col );
	
	/**
	 * Sets the title of the right GUI panel in error mode. This method does NOT set the right panel to
	 * error mode, however it does actually not matter whether you call this method before or after
	 * "setError( msg, line, col )"
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param n Total number of errors
	 */
	public void setErrorCount( int n );
	
	/**
	 * <hr><i>THREAD SAFE</i><hr>
	 * 
	 * @return The line where the last compile error registered occurred
	 */
	public int getErrorLine();
	
	/**
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @return The column where the last compile error occurred
	 */
	public int getErrorCol();
	
	/**
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @return ID of the current mode of the right GUI panel ( edit | error | run )
	 */
	public int getPanelMode();
	
	/**
	 * Resets the GUI data models which is set during interpreting. This data contains:
	 * Global variables table, local variables table, call stack list
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 */
	public void resetInterpreterData();
}
