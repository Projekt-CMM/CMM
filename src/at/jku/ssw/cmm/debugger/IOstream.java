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
 
package at.jku.ssw.cmm.debugger;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;
import at.jku.ssw.cmm.debugger.StdInOut;

/**
 * Connects the I/O functions of the interpreter with the GUI
 * 
 * @author fabian
 *
 */
public class IOstream implements StdInOut {

	/**
	 * Connects the I/O functions of the interpreter with the GUI
	 * 
	 * @param modifier
	 *            Interface for main GUI manipulations
	 * @param panelRunListener 
	 */
	public IOstream(GUImain main) {
		this.main = main;

		// Get input stream characters
		this.inputStream = new LinkedList<>();
		for (char c : this.main.getLeftPanel().getInputStream().toCharArray()) {
			this.inputStream.add(c);
		}
		
		// Add a stop bit to input string if there are any characters, see (1)
		// This is important to avoid bugs with the library function scanf()
		if( this.inputStream.size() > 0 )
			this.inputStream.add('\0');
			
	}

	/**
	 * Interface for main GUI manipulations
	 */
	private final GUImain main;

	/**
	 * List with all input stream characters
	 */
	private final List<Character> inputStream;

	/**
	 * Reads the next character from the input text area and returns it to the interpreter
	 * 
	 * @return The next character
	 * @throws RuntimeException if the text area has no more characters left
	 */
	@Override
	public char in() throws RunTimeException {

		char c;
		
		// Remove '\0' character at the end of the input stream
		// This has to be done separately because it can not be highlighted in
		// the input text are, see constructor of this class (1)
		if( this.inputStream.size() == 1 ){
			this.inputStream.remove(0);
			return '\0';
		}

		try {
			// Read and remove next character
			c = this.inputStream.get(0);
			this.inputStream.remove(0);
		} catch (Exception e) {
			
			// Throw interpreter runtime error if no more input data available
			throw new RunTimeException("no input data", null, 0);
		}
		
		// Highlight the characters which have just been read in the input
		// text area of the main GUI
		try {
			this.main.getLeftPanel().increaseInputHighlighter();
		}
		// This may fail as the interpreter thread is paused until the upper command is done
		catch (InvocationTargetException|InterruptedException e) {
			DebugShell.out(State.ERROR, Area.DEBUGGER, "Failed to delay interpreter thread for input highlighting");
			e.printStackTrace();
		}
		
		return c;
	}

	/**
	 * Prints a character from the interpreter thread onto the output text field.
	 * 
	 * @param arg0 The character of the output stream
	 */
	@Override
	public void out(final char arg0) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Add given character to output text area of the main GUI
				main.getLeftPanel().outputStream("" + arg0);
			}
		});
	}

}
