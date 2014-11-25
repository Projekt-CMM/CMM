package at.jku.ssw.cmm.debugger;

import java.util.List;

import at.jku.ssw.cmm.compiler.Node;

public class DebuggerMock implements Debugger {

	@Override
	public boolean step(Node arg0, List<Integer> readVariables, List<Integer> changedVariables) {

		return true;
	}


}
