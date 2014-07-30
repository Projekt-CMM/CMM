package at.jku.ssw.cmm.profile;

import java.util.ArrayList;

import at.jku.ssw.cmm.filemanagenment.Handler;
import at.jku.ssw.cmm.quests.datastructs.Settings;

public class Profile {
	private String path;
	private String nick;
	private int xp;
	private String selectedImage;
	private String openedQuest;
	
	private ArrayList<String> finished;
	private ArrayList<String> selectable;
	public boolean[] activeRewards = new boolean[7];
	
	public static final int
		SET_BACKGROND = 0,
		SET_SOUNDS = 1,
		SET_PROFILE_IMAGE = 2,
		SET_ONLY_XP = 3,
		SET_COLOR_PICKER = 4,
		SET_AUTO_COMPLETE = 5,
		SET_SPELL_CHECKING = 6;
	
	public static final String
		XML_PROFILE = "profile",
		XML_NICK = "nick",
		XML_XP = "xp",
		XML_FINISHED = "finished",
		XML_SELECTEDIMAGE = "selectedimage",
		XML_OPENEDQUEST = "open",
		XML_SELECTABLE = "selectable";
		
	
	/**
	 * @return the Profiles Nick Name
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * @param nick setting the Profiles Nick Name
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * @return the current XP Status
	 */
	public int getXp() {
		return xp;
	}

	/**
	 * @param xp XP to set
	 */
	public void setXp(int xp) {
		this.xp = xp;
	}
	
	/**
	 * @param xp XP to add
	 */
	public void addXP(int xp){
		this.xp += xp;
	}

	/**
	 * @param xp XP to sub
	 */
	public void subXP(int xp){
		this.xp -= xp;
	}	
	
	/**
	 * @return a List of Finished Quests
	 * @see Please initialisize the Handler before calling this
	 */
	public ArrayList<String> getFinishedQuestNames() {
		return finished;
	}

	/**
	 * @param finished the finished to set
	 * @Format packagePath + sep + questsPath + sep + quest.xml
	 */
	public void setFinishedQuests(ArrayList<String> finished) {
		this.finished = finished;
	}

	
	/**
	 * @return the current Profile Image
	 * @Format images + sep + image.png
	 */
	public String getSelectedImage() {
		return selectedImage;
	}

	/**
	 * @param for setting the current Profile Image
	 * @Format images + sep + image.png
	 */
	public void setSelectedImage(String selectedImage) {
		this.selectedImage = selectedImage;
	}

	/**
	 * <b>Calculating the Level and returns</b>
	 * @return the level
	 */
	public int getLevel() {
		return calculateLevel(); //for Calculating the Level
	}

	private int calculateLevel(){
		return (int) Math.sqrt(xp); //square of 2
	}	

	/**
	 * <b>Reading the settingsfile and returns boolean</b>
	 * SettingsFile contains: InitPath + sep + settings.xml
	 * @return boolean Array of Rewards
	 * Please Node: Use <b>Profile.*</b> for getting the right Selection
	 * Instead of * a List of Options is displayed. 
	 */
	
	public boolean[] getActiveRewards(String settingsfile){
		Handler handler = new Handler();
		
		Settings settings = handler.ReadSettings(settingsfile);
		
		//Set the whole Array on false
		for(int i = 0; i < activeRewards.length; i++)
			activeRewards[i] = false;
		
		//AutoComplete
		if(settings.getAuto_complete() <= getLevel())
			activeRewards[Profile.SET_AUTO_COMPLETE] = true;
		
		//BackGround
		if(settings.getBackground() <= getLevel())
			activeRewards[Profile.SET_BACKGROND] = true;
		
		//ColorPicker
		if(settings.getColor_picker() <= getLevel())
			activeRewards[Profile.SET_COLOR_PICKER] = true;
		
		//Sounds
		if(settings.getSounds() <= getLevel())
			activeRewards[Profile.SET_SOUNDS] = true;
		
		//Spell_Checking
		if(settings.getSpell_checking() <= getLevel())
			activeRewards[Profile.SET_SPELL_CHECKING] = true;
		
		
		return activeRewards;
		
	}
	
	/**
	 * 
	 * For adding a finished Quest to the Profile
	 * @param finished QuestsPAth
	 */
	public void AddFinished(String finishedPath){
		if(!finished.contains(finishedPath))
			finished.add(finishedPath);
	}
	
	/**
	 * Deleting a finished Quest in the Profile
	 * @param String
	 */
	public void DelFinished(String finishedPath){
		finished.remove(finishedPath);
	}
	
	/**
	 * @return the path
	 * @Format profilePath + sep + profile.xml
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
  	 * @Format profilePath + sep + profile.xml
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the openedQuest
	 */
	public String getOpenedQuest() {
		return openedQuest;
	}

	/**
	 * @param openedQuest the openedQuest to set
	 */
	public void setOpenedQuest(String openedQuest) {
		this.openedQuest = openedQuest;
	}

	
	/**
	 * @return the selectable Quests
	 */
	public ArrayList<String> getSelectableQuestNames() {
		return selectable;
	}

	/**
	 * @param selectable the selectable Quests to set
	 */
	public void setSelectable(ArrayList<String> selectable) {
		this.selectable = selectable;
	}

	
	/**
	 * 
	 * For adding a finished Quest to the Profile
	 * @param finished QuestsPAth
	 */
	public void AddSelectable(String selectablePath){
		if(!finished.contains(selectablePath))
			finished.add(selectablePath);
	}
	
	/**
	 * Deleting a finished Quest in the Profile
	 * @param String
	 */
	public void DelSelectable(String selectablePath){
		finished.remove(selectablePath);
	}
	
	
	
	
}
