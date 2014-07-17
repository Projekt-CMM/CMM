package at.jku.ssw.cmm.debugger;

import at.jku.ssw.cmm.compiler.Node;

public class DebuggerMock implements Debugger {

	@Override
	public boolean step(Node node) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void abort(String message, Node node) {
		// TODO Auto-generated method stub

	}

}
