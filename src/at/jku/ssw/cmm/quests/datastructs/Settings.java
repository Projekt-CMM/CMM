package at.jku.ssw.cmm.quests.datastructs;

public class Settings {
	private boolean debug;
	
	Settings(boolean debug){
		this.debug = debug;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	
	
}
