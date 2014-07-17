package at.jku.ssw.cmm;

import at.jku.ssw.cmm.debugger.StdInOut;

public class StdInoutMock implements StdInOut {

	@Override
	public char in() {
		return '\0';
	}

	@Override
	public void out(char arg0) {
		System.out.print(arg0);
	}

}
