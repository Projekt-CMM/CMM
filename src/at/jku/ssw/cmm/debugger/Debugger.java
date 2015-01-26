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

import java.util.List;

import at.jku.ssw.cmm.compiler.Node;

/**
 * Through this interface, the interpreter thread can communicate with the debugger
 * of C Compact. Any class which works as debugger must implement this interface.
 * 
 * @author fabian
 */
public interface Debugger {

	/**
	 * When the interpreter processes a node in the abstract syntax tree, which is important
	 * for debugging, it calls this method and passes the current node.
	 * <br>
	 * With this method, the interpreter thread can (and will) be frozen until it returns.
	 * This effect is used to pause the interpreter during debugger steps.
	 * 
	 * @param arg0 The currnet node
	 * @param readVariables A list of all variables which have been read since the last call
	 * 			of this method.
	 * @param changedVariables A list of all variables which have been written since the last
	 * 			call of this method.
	 * @return TRUE if the program shall proceed debugging, FALSE if the interpreter shall exit
	 */
	boolean step(Node arg0, List<Integer> readVariables, List<Integer> changedVariables);
}
