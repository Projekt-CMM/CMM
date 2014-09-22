package at.jku.ssw.cmm.filemanagenment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.quests.datastructs.Quest;
import at.jku.ssw.cmm.quests.datastructs.Reward;
import at.jku.ssw.cmm.quests.datastructs.Settings;


public class Handler{
	private String initPath  = "packages";
	private String packagePath = "default";
	private String questsPath = "quests";
	private String rewardPath = "rewards";
	private String profilePath = "profile";
	
	private String sep = System.getProperty("file.separator");
	
	private ArrayList<String> folderNames = new ArrayList<>();
	private ArrayList<String> fileNames = new ArrayList<>();
	private ArrayList<Quest> allQuests = new ArrayList<>();
	
	public Handler(){
	}
	
	public Handler(String questPath, String rewardPath, String profilePath){
		this.questsPath = questPath;
		this.rewardPath = rewardPath;
		this.profilePath = profilePath;
	}
	public Handler(String initPath,String questPath, String rewardPath,  String profilePath){
		this(questPath, rewardPath, profilePath);
		this.initPath = initPath;
	}
	
	public Handler( String packagePath, String initPath,String questPath, String rewardPath,  String profilePath){
		this(initPath, questPath, rewardPath, profilePath);
		this.packagePath = packagePath;
	}
	
	/**
	 * @see Reading the Folders for getting the Packages
	 */
	public void init(){
		ReadFolderNames();
		ReadSettings("settings.xml");
	}
	
	/**
	 * 
	 * Reads the packages and returns them
	 * @return ArrayList<String> with Package-FolderNames
	 */
	
	private ArrayList<String> ReadFolderNames(){
	 	folderNames.clear();
	 	
			File folder = new File(initPath);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {		
		  if(listOfFiles[i].isDirectory() && !listOfFiles[i].getName().contains(profilePath)){
			  folderNames.add(listOfFiles[i].getName());  
		  }
		 }
		return folderNames;
	}
	
	/**
	 * 
	 * Reads the File Names of the Folder
	 * @param packagePath
	 * @param subfolder
	 * @return ArrayList<String> of FileNames
	 * @Format packagePath + sep + subfolder
	 */
	
	public ArrayList<String> ReadFileNames(String packagePath, String subfolder){
		fileNames.clear();
		
		File folder = new File(initPath + sep + packagePath + sep + subfolder);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {		
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".xml"))
				fileNames.add(listOfFiles[i].getName());  
			 }		
		
		return fileNames;
	}

	/**
	 * Reads All Quests in all Packages
	 * @return ArrayList<Quest> 
	 */
	
	public ArrayList<Quest> ReadAllQuests(){
		ArrayList<Quest> allquests = new ArrayList<>();
		ArrayList<String> packageNames = ReadFolderNames();
		
		for(int i = 0; i < packageNames.size(); i++){
			allquests.addAll(ReadAllPackageQuests(packageNames.get(i)));
		}
		return allquests;
	}
	
	/**
	 * Sorts the Certain QuestList:
	 * @Form:
	 * + inprogress <br>
	 * + open <br>
	 * + done <br>
	 * + closed <br>
	 */
	
	public ArrayList<Quest> SortQuestList(ArrayList<Quest> quests){
		
		Quest quest;
		int a=0;
		int x;
		
		for(x = 0; x < quests.size(); x++){
			if(quests.get(x).getStatus() != null && quests.get(x).getStatus().contains(Quest.STATUS_INPROGRESS)){
				quest = quests.get(x);
				quests.remove(x);
				quests.add(0, quest);
				a++;
				}
			}
		for(x = a;x<quests.size();x++){
			if(quests.get(x).getStatus() != null && quests.get(x).getStatus().contains(Quest.STATIS_OPENED)){
				quest = quests.get(x);
				quests.remove(x);
				quests.add(a, quest);
				a++;
				}
			}
		for(x = a; x < quests.size(); x++){
			if(quests.get(x).getStatus() != null && quests.get(x).getStatus().contains(Quest.STATUS_DONE)){
				quest = quests.get(x);
				quests.remove(x);
				quests.add(a,quest);
			}
		}
		
		
	    return quests;
	}
	
/**
 * 
 * TODO: Next Time Unused, due to Status update in Quests
 * @param profile
 * @return All finished Quests
 */
	public ArrayList<Quest> ReadAllFinishedQuests(Profile profile){
		ArrayList<Quest> questlist = new ArrayList<>();

		for(int i = 0; i < profile.getFinishedQuestNames().size(); i++)
			questlist.add(ReadQuest(profile.getFinishedQuestNames().get(i)));
		
		return questlist;
		
	}
	
	/**
	 *TODO: Next Time unused, due to Status update in Quests
	 * @param profile
	 * @return All selectable Quests
	 */
		public ArrayList<Quest> ReadAllSelectableQuests(Profile profile){
			ArrayList<Quest> questlist = new ArrayList<>();

			for(int i = 0; i < profile.getSelectableQuestNames().size(); i++)
				questlist.add(ReadQuest(profile.getSelectableQuestNames().get(i)));
			
			return questlist;
			
		}
	
	/**
	 *TODO: Not generally necessary
	 * 
	 * Reads all Quests in one Package
	 * @param packagePath
	 * @return ArrayList<Quest> containing a list of Quests
	 * @Format packagePath: packageFolderName
	 */
	
	public ArrayList<Quest> ReadAllPackageQuests(String packagePath){
		ArrayList<Quest> allQuests = new ArrayList<>();
		ReadFileNames(packagePath, questsPath);
		
		for(int i = 0; i < fileNames.size(); i++){
				allQuests.add(ReadQuest( packagePath, questsPath, fileNames.get(i)));
		}
		return allQuests;
	}
	/**
	 * 
	 * Reads one Quest, mainly for the profile paths
	 * @param path
	 * @return Quest 
	 * @Format path: package + sep + questPath + sep + quest.xml
	 */
	
	public Quest ReadQuest(String path){
		String[] s = path.split("/");
		
		return ReadQuest(s[0],s[1],s[2]);
	}
	
	/**
	 * 
	 * Reads one Quest
	 * @param packagePath
	 * @param questsPath
	 * @param filename
	 * @return Quest
	 * 
	 * @Format packagepath + sep + questspath + sep + quest.xml
	 */
	
	public Quest ReadQuest(String packagePath, String questsPath, String filename){	 		
		String file =  packagePath + sep + questsPath + sep +filename;
		QuestContentHandler handler = new QuestContentHandler();
		
		try {	
		return handler.Parse(initPath,file);
		
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
	/**
	 * 
	 * Reads the Settings File, The Settings File contains various Informations about the Reward - Levels
	 * @param settingsFile
	 * @return Settings
	 * @Format settingsFile
	 */
	
	public Settings ReadSettings(String settingsFile){
		String file = initPath + sep + settingsFile;
		SettingsContentHandler handler = new SettingsContentHandler();
		
		try {
			return handler.Parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	/**
	 * 
	 * Reads one Profile <br />
	 * of corse without loading the Quests or something like that, containing the Profile
	 * @param profilePath
	 * @param profileName
	 * @return	Profile
	 * @Format name.xml
	 */
	
	public Profile ProfileRead( String profileName){
		String file = profileName;
		ProfileContentHandler handler = new ProfileContentHandler();
		
		try {
			return handler.Parse(initPath , profilePath, file);
		}catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Reads all Profile Names and returns a String ArrayList
	 * @return ArrayList<String>
	 */
	
	public ArrayList<String> ReadAllProfileNames(){
		fileNames.clear();
		
		File folder = new File(initPath + sep + profilePath );
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {		
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".xml"))
				fileNames.add(listOfFiles[i].getName());  
			 }		
		
		return fileNames;
	}
	
	/**
	 * 
	 * Storing the Profile in the right File
	 * @param profilePath
	 * @param profileFileName
	 * @param profile
	 * @Format profilepath + sep + profileFileName
	 */
	
	public void ProfileStore(String profileFileName, Profile profile){
		String file = initPath + sep + profilePath + sep + profileFileName;
		ProfileContentHandler handler = new ProfileContentHandler();
		
		handler.Write(profile, file);
	}
	
/**
 * For Reading a Reward directly from a Quest, not from a Path
 * @param quest
 * @return
 */
	public Reward ReadReward(Quest quest){
		return ReadReward(quest.getPackagePath(), quest.getRewardPath());
	}
	
	
	/**
	 * Reads one Reward
	 * @param packagePath
	 * @param filename
	 * @return Reward
	 * @Format packagePath + sep + rewardPath + sep + reward.xml
	 */
	public Reward ReadReward(String packagePath, String filename) {
		String file = packagePath + sep + rewardPath + sep + filename;
		RewardContentHandler handler = new RewardContentHandler();
		
		try {
			return handler.Parse(initPath, file);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * Reads the Finished Profile Rewards
	 * @param profile
	 * @return a list of rewards
	 */
	
	public ArrayList<Reward> ReadAllFinishedRewards(Profile profile){
	ArrayList<Reward> rewards = new ArrayList<>();
		
	Quest quest;
	
		for(int i = 0; i < profile.getFinishedQuestNames().size(); i++){
			quest = ReadQuest(profile.getFinishedQuestNames().get(i));
			rewards.add(ReadReward(quest)); 
		}
		 
		return rewards;
	}
	
	/**
	 * TODO In the Future not more nessesary, due to quest.xml Extendsions
	 * Reads the Selectable Profile Rewards
	 * @param profile
	 * @return a list of rewards
	 */
	
	public ArrayList<Reward> ReadAllSelectableRewards(Profile profile){
	ArrayList<Reward> rewards = new ArrayList<>();
		
	Quest quest;
	
		for(int i = 0; i < profile.getSelectableQuestNames().size(); i++){
			quest = ReadQuest(profile.getSelectableQuestNames().get(i));
			rewards.add(ReadReward(quest)); 
		}
		
		return rewards;
	}	
	
	/**
	 * Returns a <String> Arraylist with all Packages - FolderNames
	 * @return String ArrayList of FolderNames
	 */
	public ArrayList<String> getFolderNames(){
		ReadFolderNames();
		return folderNames;
		
	}

	/**
	 * For returning the Inital-Path
	 * @return the initPath
	 */
	public String getInitPath() {
		return initPath;
	}

	/**
	 * For setting the Initial-Path
	 * @param initPath the initPath to set
	 */
	public void setInitPath(String initPath) {
		this.initPath = initPath;
	}

	/**
	 * For getting the Package Path
	 * @return the packagePath
	 */
	public String getPackagePath() {
		return packagePath;
	}

	/**
	 * For setting the Package Path
	 * @param packagePath the packagePath to set
	 */
	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}

	/**
	 * @return the questsPath
	 */
	public String getQuestsPath() {
		return questsPath;
	}

	/**
	 * @param questsPath the questsPath to set
	 */
	public void setQuestsPath(String questsPath) {
		this.questsPath = questsPath;
	}

	/**
	 * @return the rewardPath
	 */
	public String getRewardPath() {
		return rewardPath;
	}

	/**
	 * @param rewardPath the rewardPath to set
	 */
	public void setRewardPath(String rewardPath) {
		this.rewardPath = rewardPath;
	}

	/**
	 * @return the fileNames
	 */
	public ArrayList<String> getFileNames() {
		return fileNames;
	}

	/**
	 * @param fileNames the fileNames to set
	 */
	public void setFileNames(ArrayList<String> fileNames) {
		this.fileNames = fileNames;
	}

	/**
	 * @return the allQuests
	 */
	public ArrayList<Quest> getAllactiveQuests() {
		return allQuests;
	}

	/**
	 * @param allQuests the allQuests to set
	 */
	public void setAllactiveQuests(ArrayList<Quest> allQuests) {
		this.allQuests = allQuests;
	}

	/**
	 * @param folderNames the folderNames to set
	 */
	public void setFolderNames(ArrayList<String> folderNames) {
		this.folderNames = folderNames;
	}

	/**
	 * @return the profilePath
	 */
	public String getProfilePath() {
		return profilePath;
	}

	/**
	 * @param profilePath the profilePath to set
	 */
	public void setProfilePath(String profilePath) {
		this.profilePath = profilePath;
	}
	
	
	
	
}