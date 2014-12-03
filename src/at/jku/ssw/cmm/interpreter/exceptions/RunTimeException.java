package at.jku.ssw.cmm.interpreter.exceptions;

import at.jku.ssw.cmm.compiler.Node;

public class RunTimeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RunTimeException( String msg, Node node, int line){
		this.msg = msg;
		this.node = node;
		this.line = line;
	}
	
	private final String msg;
	private final Node node;
	private final int line;
	
	public String getMessage(){
		return msg;
	}
	
	public Node getNode(){
		return node;
	}

	public int getLine(){
		return line;
	}
	
}
