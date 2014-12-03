package at.jku.ssw.cmm.interpreter.memory;

public class MemoryInformation {
	public boolean isInitialized;
	public String varName;
	//public int size;
	
	public MemoryInformation() {
		this.initialize();
	}
	
	public void initialize() {
		isInitialized = false;
		varName = null;
		//size = 0;
	}
}
