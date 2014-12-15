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
 
package at.jku.ssw.cmm.interpreter.exceptions;

import at.jku.ssw.cmm.compiler.Node;

public class RunTimeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RunTimeException( String msg, Node node, int line){
		this.msg = msg;
		this.node = node;
		this.line = line;
	}
	
	private final String msg;
	private final Node node;
	private final int line;
	
	public String getMessage(){
		return msg;
	}
	
	public Node getNode(){
		return node;
	}

	public int getLine(){
		return line;
	}
	
}
