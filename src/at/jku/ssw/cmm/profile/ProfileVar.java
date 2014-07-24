package at.jku.ssw.cmm.profile;

import java.util.ArrayList;

public class ProfileVar {
	private String name;
	private int xp;
	
	private ArrayList<String> finished;

	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the xp
	 */
	public int getXp() {
		return xp;
	}

	/**
	 * @param xp the xp to set
	 */
	public void setXp(int xp) {
		this.xp = xp;
	}

	/**
	 * @return the finished
	 */
	public ArrayList<String> getFinished() {
		return finished;
	}

	/**
	 * @param finished the finished to set
	 */
	public void setFinished(ArrayList<String> finished) {
		this.finished = finished;
	}
	
	
	
}
