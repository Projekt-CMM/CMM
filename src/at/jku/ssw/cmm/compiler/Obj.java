package at.jku.ssw.cmm.compiler;

/*--------------------------------------------------------------------------------
Obj   Object node of the C-- symbol table
===   ===================================
Every declared name in a C-- program is represented by an Obj node holding
information about this object.
--------------------------------------------------------------------------------*/

public class Obj {
	public static final int // object kinds
		CON    = 0,
		VAR    = 1,
		TYPE   = 2,
		PROC   = 3;
	public int     kind;      // CON, VAR, TYPE, PROC
	public String  name;      // object name
	public Struct  type;      // object type
	public Obj     next;      // next local object in this scope

	public int     val;       // CON: int or char value
	public float   fVal;      // CON: float value

	public int     adr;       // VAR: address
	public int     level;     // VAR: declaration level
	public boolean isRef;     // VAR: ref parameter
	
	public int     line;	  // VAR, CON: line of declaration

	public Node    ast;       // PROC: AST of this procedure
	public int     size;      // PROC: frame size in bytes
	public int     nPars;     // PROC: number of formal parameters
	public Obj     locals;    // PROC: parameters and local objects
	public boolean isForward; // PROC: is it a forward declaration

	public Obj(int kind, String name, Struct type, int line) {
		this.kind = kind; 
		this.name = name; 
		this.type = type; 
		this.line = line;
	}
}