package at.jku.ssw.cmm.gui.datastruct;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import at.jku.ssw.cmm.CMMwrapper;
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
public class SymbolTableUtils {

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
			return getVarPath(arg0, fileName);
		}
		//Structure
		else if( arg0.kind == Node.ASSIGN && arg0.left.kind == Node.DOT ){
			//System.err.println( "New Value: " + arg0.left.left.obj.name + ", " + arg0.left.right.obj.name );
			return getStructPath(arg0, fileName, compiler);
		}
		//Array
		else if( arg0.kind == Node.ASSIGN && arg0.left.kind == Node.INDEX ){
			return getArrayPath(arg0, fileName);
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
	
	/**
	 * Finds a variable from the syntax tree node given in a structure or
	 * a construction of nested structures.
	 * 
	 * @param arg0 The node of the abstract syntax tree which has to be analyzed
	 * @param fileName The current name of the file
	 * @param compiler A refenece to the compiler wrapper object
	 * @return The path to the changed variable as stack. Highest node (file name) at the peek.
	 */
	private static Stack<String> getStructPath( Node arg0, String fileName, CMMwrapper compiler ){
		
		//Initialize the stack
		Stack<String> path = new Stack<>();
		
		//The address list is filled with the address path to the variable
		//we're looking for.
		List<Integer> adressList = new LinkedList<>();

		Node node = arg0.left.left;
		
		//Hierarchically highest node of the structure
		adressList.add(arg0.left.right.val);
		
		//Add further struct addresses if this is a nested struct
		while( node.obj == null ){
			adressList.add(node.right.val);
			node = node.left;
		}
		
		//Name of the (root) structure
		String structName = node.obj.name;
		
		//Goes down into nested structs and finds out the path to the wanted variable
		getLowestStructVar(path, node.type.fields, adressList);
		path.add(structName);
		
		//Add function name to absolute path (if no global struct)
		if( node.obj.level != 0 )
			path.add(ReadCallStack.readCallStack().get(0)+"()");
		
		//Add file name (root node in tree table)
		path.add(fileName);
		
		//Debug message
		System.out.println("[syntax][final] path is: " + path);
		
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

		while( val.get(val.size()-1) > 0 ){
			System.out.println("[syntax] checking: " + obj.name + " with size " + obj.type.size + ", " + val.get(val.size()-1) + " | " + val);
			val.set(val.size()-1, val.get(val.size()-1) - obj.type.size);
			obj = obj.next;
			System.out.println("[syntax] new size: " + val.get(val.size()-1));
		}
		
		System.out.println("[syntax] adding: " + obj.name);
		path.add(0, obj.name);
		
		if( val.size() > 1 ){
			System.out.println("[syntax] next level: " + obj.name);
			val.remove(val.size()-1);
			getLowestStructVar( path, obj.type.fields, val );
		}
	}
	
	/**
	 * Finds out the path (in the tree table) to an array index variable which is changed in
	 * the given node of the abstract syntax tree.<br>
	 * Supports multi-dimensional arrays.
	 * 
	 * arg0 The node of the abstract syntax tree which has to be analyzed
	 * @param fileName The current name of the file
	 * @return The path to the changed variable as stack. Highest node (file name) at the peek.
	 */
	private static Stack<String> getArrayPath( Node arg0, String fileName ){
		
		//Initialize variable path stack
		Stack<String> path = new Stack<>();
		
		//Iterate down the array hierarchy
		Node n;
		for( n = arg0.left; n.left != null; n = n.left ){
			if( n.right != null ){
				path.add("[" + n.right.val + "]");
				System.out.println("[readArray] added to path: " + n.right.val);
			}
		}
		
		//Add the array itself to the path
		path.add(n.obj.name);
		
		//Add the name of the current function to the path (if array is not global)
		if( n.obj.level != 0 )
			path.add(ReadCallStack.readCallStack().get(0)+"()");
		
		//Add file name (root node in tree table)
		path.add("file12b.cmm");
		
		return path;
	}
}
