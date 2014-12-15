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
 
package at.jku.ssw.cmm.quest.exception;

public class CompilerErrorException extends Exception {

	private static final long serialVersionUID = -4173237209260462454L;
	
	public CompilerErrorException( String message, at.jku.ssw.cmm.compiler.Error e ){
		this.message = message;
		this.e = e;
	}
	
	private final String message;
	private final at.jku.ssw.cmm.compiler.Error e;
	
	public String getMessage(){
		return this.message;
	}
	
	public at.jku.ssw.cmm.compiler.Error getError(){
		return this.e;
	}

}
