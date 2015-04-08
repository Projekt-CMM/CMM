/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
package at.jku.ssw.cmm;

import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.compiler.Tab;
import at.jku.ssw.cmm.debugger.IOstream;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.event.debug.PanelRunListener;
import at.jku.ssw.cmm.interpreter.Interpreter;

/**
 * Wrapper class for the interpreter thread starter and manager class for the
 * compiler. Takes care that the interpreter is not executed two times at once
 * and that it is not started before compiling.
 * 
 * @author fabian
 *
 */
public class CMMwrapper {

	/**
	 * Wrapper class for the interpreter thread starter and manager class for
	 * the compiler. Takes care that the interpreter is not executed two times
	 * at once and that it is not started before compiling.
	 * 
	 * @param main
	 *            Interface for main GUI manipulations, for example syntax line
	 *            highlighting
	 * @param rPanel
	 *            Interface for manipulating the right GUI panel, in this case
	 *            for showing error messages
	 */
	public CMMwrapper(GUImain main, GUIdebugPanel debug) {

		//Init local references
		this.main = main;
		this.debug = debug;
		
		this.table = null;
	}

	/**
	 * Interpreter thread class
	 * (inherits from Thread, starts interpreter)
	 */
	private CMMrun thread;
	
	private Tab table;

	/**
	 * The interpreter is reinitialized every time the debugger is started
	 */
	private Interpreter interpreter;

	/**
	 * Interface for main GUI manipulations, for example syntax line highlighting
	 */
	private final GUImain main;

	/**
	 * A reference to the debug panel class which contains all objects of the
	 * tab "debug" on the right side of the main GUI.
	 */
	private final GUIdebugPanel debug;

	/**
	 * Runs interpreter in a separate thread. For thread synchronization see
	 * {@link PanelRunListener.java}. Also locks the three text fields of the
	 * main GUI (syntax, in, out) so that they can not be edited during runtime.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 * 
	 * @param listener
	 *            Interface for debug replies (Interpreter calls "step" method
	 *            after each operation)
	 * @param stream
	 *            Interface for the I/O stream. Output messages are shown in the
	 *            "output" text area of the main GUI. Input messages have to be
	 *            entered in the "input" text field in the main GUI
	 */
	public boolean runInterpreter(PanelRunListener listener, IOstream stream, Tab table) {

		// Check if another interpreter thread is already running
		if ( table != null && this.thread == null ) {
			
			this.table = table;

			// Reset the output text panel
			this.main.getLeftPanel().resetOutputTextPane();

			this.interpreter = new Interpreter(listener, stream);

			// Create new interpreter object
			this.thread = new CMMrun(table, interpreter, this, debug);

			// Run interpreter thread
			this.thread.start();

			return true;
		}
		// Another thread is already running
		else {
			// Error message
			DebugShell.out(State.ERROR, Area.INTERPRETER, "Already running or not compiled!");

			return false;
		}
	}

	/**
	 * This method unregisters the interpreter thread and therefore enables to
	 * start it again. <br>
	 * <b>WARNING: This method does NOT kill the thread. </b> It just cleans up
	 * the things the thread can't do by itself. Do not call this method unless
	 * you really know what you do! <br>
	 * This should be called at following points by default:
	 * <ul>
	 * <li>When the interpreter thread ends</li>
	 * <li>When a runtime error has occurred and the user quits the error mode
	 * of the right panel in the main GUI.</li>
	 * </ul>
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 * 
	 * @param success
	 *            TRUE if the interpreter finished with no errors, FALSE if
	 *            there has been a runtime error. In this case, this method does
	 *            not delete the interpreter data so that the user can still
	 *            read variables and the call stack from the main GUI. Then,
	 *            this method is called a second time and cleans everything up.
	 */
	public void setNotRunning() {

		DebugShell.out(State.LOG, Area.INTERPRETER, "Interpreter thread unregistered");
		this.thread = null;
	}

	/**
	 * <hr>
	 * <i>THREAD SAFE by default</i>
	 * <hr>
	 * 
	 * @return The compiler's symbol table
	 */
	public Tab getSymbolTable() {
		return this.table;
	}

	/**
	 * <hr>
	 * <i>THREAD SAFE by default</i>
	 * <hr>
	 * 
	 * @return TRUE if an interpreter thread is already running,<br>
	 *         FALSE if not
	 */
	public boolean isRunning() {
		return this.thread != null;
	}
}
