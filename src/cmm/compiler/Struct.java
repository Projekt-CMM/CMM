package cmm.compiler;

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
		STRUCT = 6;
	public int    kind;		  // NONE, INT, FLOAT, CHAR, ARR, STRUCT
	public int    size;     // size of this type in bytes
	public int    elements; // ARR: number of elements
	public Struct elemType; // ARR: element type
	public Obj    fields;   // STRUCT: fields

	public Struct(int kind) {
		this.kind = kind;
		switch (kind) {
			case INT:   size = 4; break;
			case FLOAT: size = 4; break;
			case CHAR:  size = 1; break;
			case BOOL:  size = 1; break;
			default:    size = 0; break;
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