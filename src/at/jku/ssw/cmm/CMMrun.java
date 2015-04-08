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
import at.jku.ssw.cmm.compiler.Tab;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.event.debug.PanelRunListener;
import at.jku.ssw.cmm.interpreter.Interpreter;
import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;
import at.jku.ssw.cmm.interpreter.memory.Memory;

/**
 * This class starts the separate interpreter thread. For thread synchronization
 * see {@link PanelRunListener.java}
 * 
 * @author fabian
 *
 */
public class CMMrun extends Thread {

	/**
	 * This class starts the separate interpreter thread. For thread
	 * synchronization see {@link PanelRunListener.java}
	 * 
	 * @param debug
	 *            Interface for debug replies (Interpreter calls "step" method
	 *            after each operation)
	 * @param compiler
	 *            The compiler containing the syntax tree and the symbol table
	 * @param stream
	 *            Interface for the I/O stream. Output messages are shown in the
	 *            "output" text area of the main GUI. Input messages have to be
	 *            entered in the "input" text field in the main GUI
	 * @param reply
	 *            Interface which enables the thread to clean up after exiting
	 *            by itself, see {@link CMMwrapper}
	 */
	public CMMrun(Tab table, Interpreter interpreter, CMMwrapper reply,
			GUIdebugPanel debug) {
		this.table = table;
		this.interpreter = interpreter;
		this.reply = reply;
		this.debug = debug;
	}

	/**
	 * ...
	 */
	private final Tab table;

	/**
	 * Interface which enables the thread to clean up after exiting by itself,
	 * see CMMwrapper
	 */
	private final CMMwrapper reply;

	/**
	 * A reference to the interpreter which is the main task of this thread
	 */
	private final Interpreter interpreter;

	/**
	 * A reference to the debug panel class which contains all objects of the
	 * tab "debug" on the right side of the main GUI.
	 */
	private final GUIdebugPanel debug;

	/**
	 * Starts the interpreter thread
	 */
	@Override
	public void run() {

		// Allocating memory for interpreter
		Memory.initialize();

		System.out.println("[thread] interpreter thread started");

		// Run main function
		try {
			interpreter.run(table);
		}
		// Thrown when runtime error occurs
		catch (final RunTimeException e) {

			DebugShell.out(DebugShell.State.ERROR, Area.INTERPRETER, "Interpreter thread threw RunTimeException");

			// Clean thread data partly up; leave variable data for GUI runtime
			// error mode
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					if ( e.getNode() != null && e.getNode().line > 0)
						debug.setErrorMode(e.getMessage(), null, e.getNode().line, true);
					else if( e.getLine() > 0 )
						debug.setErrorMode(e.getMessage(), null, e.getLine(), true);
					else
						debug.setErrorMode(e.getMessage(), null, -1, true);
				}
			});
			reply.setNotRunning();
			return;
		} catch (Exception e) {
			// print detailed StackTrace
			if (DebugShell.maxLogLevel == DebugShell.State.LOG) {
				e.printStackTrace();
			}

			// Clean thread data partly up; leave variable data for GUI runtime
			// error mode
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					debug.setErrorMode("unknown interpreter exception", null, -1, true);
				}
			});
			reply.setNotRunning();
			return;
		}

		// Exit message
		System.out.println("[thread] Interpreter thread exited");

		// Set running flag to false so that the interpreter can be started
		// again
		reply.setNotRunning();

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Set main GUI to ready mode (where the user can edit source
				// code)
				debug.setReadyMode();
			}
		});
	}
}
