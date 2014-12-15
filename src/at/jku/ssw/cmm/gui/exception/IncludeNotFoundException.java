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
 
package at.jku.ssw.cmm.gui.exception;

/**
 * This exception may be thrown by the preprocessor if an include file can't be found
 * 
 * @author fabian
 *
 */
public class IncludeNotFoundException extends Exception {

	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = -124;
	
	/**
	 * This exception may be thrown by the preprocessor if an include file can't be found
	 * 
	 * @param fileName Name of the library file
	 * @param line Line of include command
	 */
	public IncludeNotFoundException( String fileName, int line ){
		this.fileName = fileName;
		this.line = line;
	}
	
	/**
	 * Name of the library file
	 */
	private final String fileName;
	
	/**
	 * Line of include command
	 */
	private final int line;
	
	/**
	 * @return The name of the library file
	 */
	public String getFileName(){
		return this.fileName;
	}
	
	/**
	 * @return The line of the include command
	 */
	public int getLine(){
		return this.line;
	}

}
