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
