package at.jku.ssw.cmm.gui.datastruct;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.compiler.Node;
import at.jku.ssw.cmm.compiler.Obj;

public class SyntaxTreeUtils {

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
	
	private static Stack<String> getVarPath( Node arg0, String fileName ){
		
		Stack<String> path = new Stack<>();
		
		path.add(arg0.left.obj.name);
		
		if( arg0.left.obj.level != 0 )
			path.add(ReadCallStack.readCallStack().get(0)+"()");
		
		path.add("file12b.cmm");
		
		return path;
	}
	
	private static Stack<String> getStructPath( Node arg0, String fileName, CMMwrapper compiler ){
		
		Stack<String> path = new Stack<>();
		List<Integer> adressList = new LinkedList<>();

		Node node = arg0.left.left;
		adressList.add(arg0.left.right.val);
		while( node.obj == null ){
			adressList.add(node.right.val);
			node = node.left;
		}
		
		String structName = node.obj.name;
		
		getLowestStructVar(path, node.type.fields, adressList);
		path.add(structName);
		
		if( node.obj.level != 0 )
			path.add(ReadCallStack.readCallStack().get(0)+"()");
		
		path.add(fileName);
		
		System.out.println("[syntax][final] path is: " + path);
		
		return path;
	}
	
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
	
	private static Stack<String> getArrayPath( Node arg0, String fileName ){
		
		Stack<String> path = new Stack<>();
		
		path.add("[" + arg0.left.right.val + "]");
		path.add(arg0.left.left.obj.name);
		
		if( arg0.left.left.obj.level != 0 )
			path.add(ReadCallStack.readCallStack().get(0)+"()");
		
		path.add("file12b.cmm");
		
		return path;
	}
}
