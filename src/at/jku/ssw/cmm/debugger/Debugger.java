package at.jku.ssw.cmm.debugger;

import java.util.List;

import at.jku.ssw.cmm.compiler.Node;

public interface Debugger {

	boolean step(Node arg0, List<Integer> readVariables, List<Integer> changedVariables);
}
