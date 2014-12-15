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
Struct   Structure of a C-- type
======   =======================
A Struct holds information about a C-- type. There are 3 primitive types
(int, float, char) and 2 structured types (arrays, structures). The type Bool
results from compare operations, but there is no boolean type in C--.
--------------------------------------------------------------------------------*/

public class Struct {
	public static final int // structure kinds
		NONE   = 0,
		INT    = 1,
		FLOAT  = 2,
		CHAR   = 3,
		BOOL   = 4,
		ARR    = 5,
		STRUCT = 6,
		STRING = 7;
	public int    kind;		  // NONE, INT, FLOAT, CHAR, ARR, STRUCT, STRING
	public int    size;     // size of this type in bytes
	public int    elements; // ARR: number of elements
	public Struct elemType; // ARR: element type
	public Obj    fields;   // STRUCT: fields

	public Struct(int kind) {
		this.kind = kind;
		switch (kind) {
			case INT:
				size = 4;
				break;
			case FLOAT:
				size = 4;
				break;
			case CHAR:
				size = 2;
				break;
			case BOOL:
				size = 1;
				break;
			case STRING:
				size = 4;
				break;
			default:
				size = 0;
				break;
		}
	}

	public Struct(int kind, int elements, Struct elemType) {
		this.kind = kind;
		this.elements = elements;
		this.elemType = elemType;
		size = elements * elemType.size;
	}

	/**
	 * Checks whether this type is a primitive type
	 * 
	 * @return true if kind is a primitive
	 */
	public boolean isPrimitive() {
		return kind == INT || kind == FLOAT || kind == CHAR || kind == BOOL;
	}

}