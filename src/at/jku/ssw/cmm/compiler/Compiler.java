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
 
package at.jku.ssw.cmm.compiler;

/*--------------------------------------------------------------------------------
Compiler   C-- compiler
========   ============
This is the top class of the C-- compiler. It contains methods for compiling
C-- programs, retrieving the symbol table and the errors. The AST of every
procedure is stored in the procedure's object in the symbol table.

Debug switches:
0: dump the symbol table
1: dump the AST
--------------------------------------------------------------------------------*/
import java.io.ByteArrayInputStream;

public final class Compiler {

	private Parser parser;
	public  boolean[] debug = new boolean[10]; // debug switches

	// Compile the source code and build an AST as well as a symbol table
	public void compile(String source) {
		Scanner scanner = new Scanner(new ByteArrayInputStream(source.getBytes()));
		parser = new Parser(scanner);
		parser.debug = debug;
		parser.Parse();
	}

	// Retrieve the symbol table after compilation
	public Tab getSymbolTable() {
		return parser.tab;
	}

	// Retrieve the string storage
	public Strings getStringStorage() {
		return parser.strings;
	}
		
	// Retrieve the first error (or null)
	// More errors are linked via a "next" pointer
	public Error getError() {
		return parser.errors.head;
	}

}
