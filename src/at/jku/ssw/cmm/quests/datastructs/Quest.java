package at.jku.ssw.cmm.quests.datastructs;

public class Quest {
	private String title;
	private String description;
	private String image;
	private String cmmProgramm;
	private String pattern;
	private String rewardPath;
	private int level; //in Stars
	private String nextQuest;
	/**
	 * @return the title
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
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
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
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}
	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	/**
	 * @return the rewardPath
	 */
	public String getRewardPath() {
		return rewardPath;
	}
	/**
	 * @param rewardpath the rewardPath to set
	 */
	public void setRewardPath(String rewardpath) {
		this.rewardPath = rewardpath;
	}
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
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
