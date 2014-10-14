package at.jku.ssw.cmm.gui.datastruct;

import java.util.Stack;

import at.jku.ssw.cmm.compiler.Node;

public class SyntaxTreeUtils {

	public static Stack<String> getVariablePath( Node arg0, String fileName ){
		
		//A simple variable
		if( arg0.kind == Node.ASSIGN && arg0.left.kind == Node.IDENT ){
			return getVarPath(arg0, fileName);
		}
		//Structure
		else if( arg0.kind == Node.ASSIGN && arg0.left.kind == Node.DOT ){
			//System.err.println( "New Value: " + arg0.left.left.obj.name + ", " + arg0.left.right.obj.name );
			return getStructPath(arg0, fileName);
		}
		//Array
		else if( arg0.kind == Node.ASSIGN && arg0.left.kind == Node.INDEX ){
			System.err.println( "New Value: " + arg0.left.left.obj.name );
			return getArrayPath(arg0, fileName);
		}
		
		return null;
	}
	
	private static Stack<String> getVarPath( Node arg0, String fileName ){
		
		Stack<String> path = new Stack<>();
		
		path.add(arg0.left.obj.name);
		
		if( arg0.left.obj.level != 0 )
			path.add("main()");
		
		path.add("file12b.cmm");
		
		return path;
	}
	
	private static Stack<String> getStructPath( Node arg0, String fileName ){
		
		Stack<String> path = new Stack<>();
		
		path.add(arg0.left.left.obj.name);
		
		if( arg0.left.left.obj.level != 0 )
			path.add(ReadCallStack.readCallStack().pop()+"()");
		
		path.add(fileName);
		
		return path;
	}
	
	private static Stack<String> getArrayPath( Node arg0, String fileName ){
		
		Stack<String> path = new Stack<>();
		
		return path;
	}
}
