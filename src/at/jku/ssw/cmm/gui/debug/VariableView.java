package at.jku.ssw.cmm.gui.debug;

import at.jku.ssw.cmm.CMMwrapper;

public abstract class VariableView {

	/**
	 * Initializes the variable view table/tree table, etc
	 * 
	 * @param panel
	 */
	public abstract void init();
	
	/**
	 * Updates variable values and call stack
	 * 
	 * @param compiler
	 */
	public abstract void update( CMMwrapper compiler );
	
	/**
	 * Deletes all variable values from tables; tables are shown blank
	 */
	public abstract void standby();
}
