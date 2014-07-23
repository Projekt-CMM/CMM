package at.jku.ssw.cmm.quests.datastructs;

public class Quest {
	String title;
	String description;
	String cmmProgramm;
	String pattern;
	String reward;
	int level; //in Stars
	String nextQuest;
	
	public Quest(){
		
	}

	/**
	 * @return the title of the Function
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the cmmProgramm
	 */
	public String getCmmProgramm() {
		return cmmProgramm;
	}

	/**
	 * @param cmmProgramm the cmmProgramm to set
	 */
	public void setCmmProgramm(String cmmProgramm) {
		this.cmmProgramm = cmmProgramm;
	}

	/**
	 * @return the programm pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param plattern the plattern to set
	 */
	public void setPattern(String plattern) {
		this.pattern = plattern;
	}

	/**
	 * @return the reward
	 */
	public String getReward() {
		return reward;
	}

	/**
	 * @param reward the reward to set
	 */
	public void setReward(String reward) {
		this.reward = reward;
	}

	/**
	 * @return the Difficulty - level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level Difficulty - level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the nextQuest
	 */
	public String getNextQuest() {
		return nextQuest;
	}

	/**
	 * @param nextQuest the nextQuest to set
	 */
	public void setNextQuest(String nextQuest) {
		this.nextQuest = nextQuest;
	}
	
	
}
