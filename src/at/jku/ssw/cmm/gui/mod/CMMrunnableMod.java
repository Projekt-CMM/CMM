package at.jku.ssw.cmm.gui.mod;

public interface CMMrunnableMod {
	
	/**
	 * This method unregisters the interpreter thread and therefore enables to start it again. <br>
	 * <b>WARNING: This method does NOT kill the thread. </b> It just cleans up the things the thread
	 * can't do by itself. Do not call this method unless you really know what you do! <br>
	 * This should be called at following points by default:
	 *  - When the interpreter thread ends
	 *  - When a runtime error has occurred and the user quits the error mode of the right panel
	 *    in the main GUI.
	 * 
	 * @param success TRUE if the interpreter finished with no errors, FALSE if there has been a runtime error.
	 * 					In this case, this method does not delete the interpreter data so that the user can
	 * 					still read variables and the call stack from the main GUI.
	 * 					Then, this method is called a second time and cleans everything up. 
	 */
	public void setNotRunning( boolean success );
}
