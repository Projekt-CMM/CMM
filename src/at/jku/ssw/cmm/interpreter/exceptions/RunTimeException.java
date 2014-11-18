package at.jku.ssw.cmm.interpreter.exceptions;

import at.jku.ssw.cmm.compiler.Node;

public class RunTimeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RunTimeException( String msg, Node node ){
		this.msg = msg;
		this.node = node;
	}
	
	private final String msg;
	private final Node node;
	
	public String getMessage(){
		return msg;
	}
	
	public Node getNode(){
		return node;
	}

}
