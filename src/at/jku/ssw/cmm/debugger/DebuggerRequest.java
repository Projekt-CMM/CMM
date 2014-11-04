package at.jku.ssw.cmm.debugger;

public interface DebuggerRequest {
	public int getLastChangedAddress();
	public int getCurrentAddress();
}
