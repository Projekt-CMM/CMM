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
Scope   Scope in the C-- symbol table
=====   =============================
There are 3 scopes in the C-- symbol table:
- universe: contains predeclared names
- global scope: contains globally declared names
- local scope: contains local names of a procedure
The scopes are linked by the "outer" pointer.
--------------------------------------------------------------------------------*/

public class Scope {
	public Scope outer;		// to outer scope
	public Obj   locals;	// to local variables of this scope
	public int   size;    // total size of variables in this scope
}