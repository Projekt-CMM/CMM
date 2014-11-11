package at.jku.ssw.cmm.gui.datastruct;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.compiler.Node;
import at.jku.ssw.cmm.compiler.Obj;

/**
 * Contains utility functions for analyzing the symbol table.
 * All the methods in this class are used to find path to a data node
 * in the tree table. This is usually necessary when a variable has been
 * changed and the node of that variable shall collapse.
 * 
 * @author fabian
 *
 */
public class SyntaxTreePath {
	
	private static final String NOTINITIALIZED = "?";

	/**
	 * Analyzes the abstract syntax tree node given and returns the path to the variable
	 * which is assigned or changed in this node (if any).
	 * 
	 * @param arg0 The node of the abstract syntax tree which has to be analysed
	 * @param fileName The current name of the file
	 * @param compiler A reference to the compiler wrapper obejct
	 * @return The path to the changed variable as stack. Highest node (file name) at the peek.
	 * 		Returns null if no variable has been changed.
	 */
	public static Stack<String> getVariablePath( Node arg0, String fileName, CMMwrapper compiler ){
		
		//A simple variable
		if( arg0.kind == Node.ASSIGN && arg0.left.kind == Node.IDENT ){
			DebugShell.out(State.LOG, Area.READVAR, "[tableUtils] analysing var");
			return getVarPath(arg0, fileName);
		}
		//Structure
		else if( arg0.kind == Node.ASSIGN && arg0.left.kind == Node.DOT ){
			DebugShell.out(State.LOG, Area.READVAR, "[tableUtils] analysing struct");
			return complexSearch(arg0, fileName, compiler);
		}
		//Array
		else if( arg0.kind == Node.ASSIGN && arg0.left.kind == Node.INDEX ){
			DebugShell.out(State.LOG, Area.READVAR, "[tableUtils] analysing array");
			return complexSearch(arg0, fileName, compiler);
		}
		
		return null;
	}
	
	/**
	 * Gets the variable path of a simple variable which is either inside a function
	 * or a global variable.
	 * 
	 * @param arg0 The node of the abstract syntax tree which has to be analyzed
	 * @param fileName The current name of the file
	 * @return The path to the changed variable as stack. Highest node (file name) at the peek.
	 */
	private static Stack<String> getVarPath( Node arg0, String fileName ){
		
		//Initialize the stack
		Stack<String> path = new Stack<>();
		
		//Add variable name
		path.add(arg0.left.obj.name);
		
		//Add name of function (if no global variable
		if( arg0.left.obj.level != 0 )
			path.add(ReadCallStack.readCallStack().get(0)+"()");
		
		//Add file name (root node in tree table)
		path.add("file12b.cmm");
		
		return path;
	}
	
	private static Stack<String> complexSearch( Node n, String fileName, CMMwrapper compiler ){
		
		Stack<String> path = new Stack<>();
		
		Node ident;
		Obj table;
		
		List<Integer> addressList = new LinkedList<>();
		
		//Get identifier with structure table
		for( ident = n.left; ident != null; ident = ident.left )
			if( ident.kind == Node.IDENT )
				break;
		
		table = ident.obj.type.fields;
		
		//Read complete path
		for( ; n.left != null; n = n.left ){
			//Struct
			if( n.left.kind == Node.DOT ) {
				//for( int val = n.left.right.val; val > 0; val = val - table.type.size, table = table.next );
				
				addressList.add(n.left.right.val);
				path.add(NOTINITIALIZED);
				//table = table.type.fields;
			}
			//Array
			else if( n.left.kind == Node.INDEX ){
				System.out.println("Array index is: "  + n.left.right.val);
				path.add("[" + n.left.right.val + "]");
			}
		}
		
		//nextLevel(n, table, path);
		getLowestStructVar(path, table, addressList);
		
		//Add variable name
		path.add(ident.obj.name);
				
		//Add name of function (if no global variable
		if( ident.obj.level != 0 )
			path.add(ReadCallStack.readCallStack().get(0)+"()");
				
		//Add file name (root node in tree table)
		path.add("file12b.cmm");
		
		return path;
	}
	
	/**
	 * Searches the variables of a struct (and those of a sub-struct) for a variable which is
	 * defined through the address list given.
	 * 
	 * @param path A stack of Strings to save the complete path to (hierarchically highest
	 * 		element at the peek)
	 * @param obj A node of the symbol table which contains the struct to be searched
	 * @param val An address list which contains the addresses of the wanted variable the
	 * 		sub-structs 
	 */
	private static void getLowestStructVar( Stack<String> path, Obj obj, List<Integer> val ){

		for( int i = path.size()-1; i >= 0; i-- ){
			
			if( path.get(i).equals(NOTINITIALIZED) ){
				
				while( val.get(val.size()-1) > 0 ){
					val.set(val.size()-1, val.get(val.size()-1) - obj.type.size);
					obj = obj.next;
				}
				
				path.set(i, obj.name);

				val.remove(val.size()-1);
				obj = obj.type.fields;
				
			}
		}
	}
}
