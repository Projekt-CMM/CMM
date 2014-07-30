package at.jku.ssw.cmm.quests.datastructs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Quest {
	
	public static final String
	XML_QUEST = "quest",
	XML_IMAGE = "image",
	XML_CMMPROGRAMM = "cmmprogramm",
	XML_PATTERN = "pattern",
	XML_REWARD = "reward",
	XML_LEVEL = "level",
	XML_NEXTQUEST = "nextquest",
	XML_DESCRIPTION = "description",
	XML_TITLE = "title",
	XML_STATUS = "status",
	XML_SOLUTION = "solution",
	XML_DATE = "date";
	
	public static final String
	STATUS_CLOSED = "closed",
	STATIS_OPENED = "opened",
	STATUS_INPROGRESS = "inprogress",
	STATUS_DONE = "done",
	STATUS_START = "start";

	
	private String sep = System.getProperty("file.separator");
	private String path;
	private String title;
	private String description;
	private String image;
	private String cmmProgramm;
	private String pattern;
	private String rewardPath;
	private int level;
	private String nextQuest;
	private String status;
	private String solution;
	private String date;
	
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
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPackagePath(){
		return path.split(sep)[0];
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the solution
	 */
	public String getSolution() {
		return solution;
	}
	/**
	 * @param solution the solution to set
	 */
	public void setSolution(String solution) {
		this.solution = solution;
	}
	/**
	 * @return the time
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param time the time to set
	 */
	public void setnewDate() {
		  DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy/ HH:mm:ss");
		  Date date = new Date();
		  
		  this.date = dateFormat.format(date);
		
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	
	
	
}
