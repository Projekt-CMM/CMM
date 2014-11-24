package at.jku.ssw.cmm.debugger;

import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;

public interface StdInOut {
	public char in() throws RunTimeException;
	public void out(char arg0);
}
