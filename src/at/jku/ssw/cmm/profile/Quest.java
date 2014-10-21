package at.jku.ssw.cmm.profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Quest {

	//File Seperator
	private static String sep = System.getProperty("file.separator");
	
	//Variablen abgeleitet von der quest.xml
	private String title;					//Titel der Quest
	private String token;					//Token welches man beim Abschluss bekommt
	private List<String> nextQuest;			//nächste Quest
	private int level;						//den zu setzendesn Level bei geschaffter Quest.
	private int minLevel;					//Level Anforderung - vom Package vorgegeben
	private boolean description;			//Beschreibung: description.html
	private boolean style;					//Stylesheet: style.css
	private String state;					//Status wird mit Profil abgeglichen
	private String reward;					//Reward welcher beim Abschluss einer Quest angezeigt wird.
	private Date date;						//Datum der letzten Bearbeitung
		
	//Ordner der Quest
	private String initPath;				//Inizialisierungspfad:
	private String packagePath;				//Package-Ordner
	private String questPath;				//Quest-Ordner
	
	public static final String
		FILE_DESCRIPTION = "description.html",
		FILE_STYLE = "style.css",
		FiLE_QUEST = "quest.xml";
	
	public static final String
		XML_QUEST = "quest",
		XML_TITLE = "title",
		XML_LEVEL = "level",
		XML_TOKEN = "token",
		XML_NEXTQUEST = "nextquest",
		XML_STATE = "state",
		XML_REWARD = "reward";

	public static final String
		STATE_LOCKED = "locked",
		STATE_SELECTABLE = "selectable",
		STATE_INPROGRESS = "inprogress",
		STATE_OPEN = "open",
		STATE_FINISHED = "finished";
	
	//Time Format
	public static final String
	TIME_FORMAT = "dd-MM-yyyy:HH:mm:SS";
		
	/**
	 * Reads one Quest
	 * @param allPackagesPath: the path where all Packages are saved
	 * @param packagePath: the foldername of the packagePath
	 * @param questPath: the foldername of the quests folder
	 * @return quest
	 */
	public static Quest ReadQuest(String allPackagesFolder, String packageFolder, String questFolder) {	
			Quest quest = new Quest();
			String path = allPackagesFolder + sep + packageFolder + sep + questFolder;
			
			List<String> fileNames = ReadFileNames(path);
			
			//Folder has to have any Files and a Quest File.
			
				//Foldername = questtitle was earlier so
				//quest.setTitle(questFolder);
				
				if(fileNames.contains(Quest.FILE_DESCRIPTION))
					quest.setDescription(true);
				if(fileNames.contains(Quest.FILE_STYLE))
					quest.setStyle(true);
				
				//Setting Quest Paths for later use:
				quest.setInitPath(allPackagesFolder);
				quest.setPackagePath(packageFolder);
				quest.setQuestPath(questFolder);
				
				try {
					quest = ReadQuestXML(path,quest);
				} catch (ParserConfigurationException | SAXException | IOException e) {
					e.printStackTrace();
				}
				
				return quest;
				

	}
	
	private static Quest ReadQuestXML(String path, Quest quest) throws ParserConfigurationException, SAXException, IOException{

		List<String> nextQuest = new ArrayList<>();
		
		File file = new File(path + sep + Quest.FiLE_QUEST);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = null;
		try{
		 doc = dBuilder.parse(file);
		
		}catch(FileNotFoundException e){
			System.err.println(file + " not found!");
			quest.setTitle(quest.getQuestPath());
			quest.setLevel(-1);
			
			return quest;
		}	
		
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName(Quest.XML_QUEST);
		
		for (int i = 0; i < nList.getLength(); i++) {
			 
			Node nNode = nList.item(i);
	 
			//System.out.println("\nCurrent Element:" + nNode.getNodeName());
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				try{
				quest.setTitle(eElement.getElementsByTagName(Quest.XML_TITLE).item(0).getTextContent());
				}catch(NullPointerException e){
					//No Title found
					quest.setTitle(null);
				}
				
				try{
				quest.setLevel(Integer.parseInt(eElement.getElementsByTagName(Quest.XML_LEVEL).item(0).getTextContent()));
				}catch(NullPointerException e){
					//No Level found
					quest.setLevel(-1);
				}
				
				try{
					quest.setToken(eElement.getElementsByTagName(Quest.XML_TOKEN).item(0).getTextContent());
				}catch(NullPointerException e){
					//No Token found
					quest.setToken(null);
				}
				
				try{
					quest.setReward(eElement.getElementsByTagName(Quest.XML_REWARD).item(0).getTextContent());
				}catch(NullPointerException e){
					//No Token found
					quest.setReward(null);
				}

				try{
				String s = eElement.getElementsByTagName(Quest.XML_STATE).item(0).getTextContent();
					if(s.contains(Quest.STATE_SELECTABLE) || s.contains(Quest.STATE_LOCKED))
						quest.setState(eElement.getElementsByTagName(Quest.XML_STATE).item(0).getTextContent());
					else
						throw new NullPointerException();
						
				}catch(NullPointerException e){
					//No state found
					
					//System.out.println("No / Wrong State set!");
					quest.setState(Quest.STATE_LOCKED);
				}
				for(int x = 0; x < eElement.getElementsByTagName(Quest.XML_NEXTQUEST).getLength(); x++){
					nextQuest.add(eElement.getElementsByTagName(Quest.XML_NEXTQUEST).item(x).getTextContent());
				}
				
			}
		}
		quest.setNextQuest(nextQuest);
		
		return quest;
		
	}

	/**
	 * Reads all Folders on the specific path, excluded tokens folder
	 * @param folderPath
	 * @return List<String> folderNames / or null on non existing folder
	 */
	public static List<String> ReadFolderNames(String path) {
		List<String> folderNames = new ArrayList<>();
		
		File folder = new File(path);
		if(!folder.exists())
			return null;
		
		File[] listOfFiles = folder.listFiles(); //get all File and Folder - Names
		
		for (int i = 0; i < listOfFiles.length; i++) {	
		//Excludes Profile Folder and execludes FileNames
		  if(listOfFiles[i].isDirectory() && !listOfFiles[i].getName().equals("tokens")){
			  folderNames.add(listOfFiles[i].getName());  
		  }
		 }
		
			return folderNames;
	}

	/**
	 * Reads all Files of the specific folder
	 * @param folderPath
	 * @return List<Strin> fileNames / or null on non existing folder
	 */
	public static List<String> ReadFileNames(String path) {
		
		List<String> fileNames = new ArrayList<>();
		
		File folder = new File(path);
		if(!folder.exists())
			return null;
		
		File[] listOfFiles = folder.listFiles(); //get all File and Folder - Names
		
		for (int i = 0; i < listOfFiles.length; i++) {	
		//Excludes Profile Folder and execludes FileNames, relative and absolute paths are allowed.
		  if(listOfFiles[i].isFile()){
			  fileNames.add(listOfFiles[i].getName());  
		  }
		 }
			return fileNames;
	}


	/**
	 * A title can be null
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
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * A token can be null
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Level can be -1
	 * @return the Level
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
	 * @return the questPath
	 */
	public String getQuestPath() {
		return questPath;
	}

	/**
	 * @param questPath the questPath to set
	 */
	public void setQuestPath(String questPath) {
		this.questPath = questPath;
	}



	/**
	 * @return the packagePath
	 */
	public String getPackagePath() {
		return packagePath;
	}

	/**
	 * @param packagePath the packagePath to set
	 */
	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}

	/**
	 * @return the initPath
	 */
	public String getInitPath() {
		return initPath;
	}

	/**
	 * @param initPath the initPath to set
	 */
	public void setInitPath(String initPath) {
		this.initPath = initPath;
	}

	/**
	 * @return the style
	 */
	public boolean isStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(boolean style) {
		this.style = style;
	}

	/**
	 * @return the description
	 */
	public boolean isDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(boolean description) {
		this.description = description;
	}

	/**
	 * Can be null
	 * @return the nextQuest
	 */
	public List<String> getNextQuest() {
		return nextQuest;
	}

	/**
	 * @param nextQuest the nextQuest to set
	 */
	public void setNextQuest(List<String> nextQuest) {
		this.nextQuest = nextQuest;
	}

	/**
	 * Can be null
	 * @return the time as String
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param time the time to set
	 */
	public void setnewDate() {
		date = new Date();
		
	}
	
	public void setDate(Date date){
		this.date = date;
	}
	/**
	 * @param date the date to set
	 */
	public void setStringDate(String date) {
		try {
			this.date = new SimpleDateFormat(Quest.TIME_FORMAT, Locale.ENGLISH).parse(date);
		} catch (ParseException e) {
			//if(Settings.debug)System.out.println("Unknown Data Format, setting new Date");
			setnewDate();
		}
	}
	
	/**Quest
	 * Can be null
	 * @return String Value of Date
	 */
	public String getStringDate(){
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Quest.TIME_FORMAT);
		return DATE_FORMAT.format(date);
	}

	/**
	 * Can be null
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
	 * @return the minLevel
	 */
	public int getMinLevel() {
		return minLevel;
	}

	/**
	 * @param minLevel the minLevel to set
	 */
	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}	
	
	
	
}
