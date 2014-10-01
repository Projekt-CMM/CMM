package at.jku.ssw.cmm.profile;

import java.io.File;
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
	private List<String> nextQuest;	//nächste Quest
	private int xp;							//die zu bekommenden XP bei geschaffter Quest.
	private boolean description;			//Beschreibung: description.html
	private boolean style;					//Stylesheet: style.css
	private String state;					//Status wird mit Profil abgeglichen
	private Date date;						//Datum der letzten Bearbeitung
		
	//Ordner der Quest
	private String initPath;				//Inizialisierungspfad:
	private String packagePath;				//Package-Ordner
	private String questPath;				//Quest-Ordner
	
	//Temporary file data
	private String folderName;
	private String fileName;	
	
	public static final String
		FILE_DESCRIPTION = "description.html",
		FILE_STYLE = "style.css",
		FiLE_QUEST = "quest.xml";
	
	public static final String
		XML_QUEST = "quest",
		XML_TITLE = "title",
		XML_XP = "xp",
		XML_TOKEN = "token",
		XML_NEXTQUEST = "nextquest",
		XML_STATE = "state";

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
	 * For Reading All Quests
	 * @param AllPackagesPath: the initial Path of all Packages
	 * @return allQuests: containing all Quests sorted TODO
	 */
	public static List<Quest> ReadAllQuests(String allPackagesPath){
		List<String> packageFolderNames = ReadFolderNames(allPackagesPath);
		List<Quest> allQuests = new ArrayList<>();
		
		if(packageFolderNames != null){
			for(int i = 0; i < packageFolderNames.size();i++){
				if(ReadPackageQuests(allPackagesPath, packageFolderNames.get(i)) != null)
					allQuests.addAll(ReadPackageQuests(allPackagesPath, packageFolderNames.get(i)));
			}
			
			return allQuests;
		}
		return null;
	}

	/**
	 * For reading all Quests in one Package
	 * @param allPackagesPath: the initial Path of the Packages
	 * @param packagePath: the folder Name of the Package
	 * @return packageQuests: all Quests of one package
	 */
	public static List<Quest> ReadPackageQuests(String allPackagesPath, String packagePath){
		
		List<String> questFolderNames = ReadFolderNames(allPackagesPath + sep + packagePath);
		List<Quest> packageQuests = new ArrayList<>();
		
		for(int i = 0;i < questFolderNames.size(); i++){
			Quest quest;
					quest = ReadQuest(allPackagesPath, packagePath, questFolderNames.get(i));
					
					if(quest!= null)
						packageQuests.add(quest);
					
			}
				
		
		
		return packageQuests;
	}

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
			if(fileNames != null && fileNames.contains(Quest.FiLE_QUEST)){
				//Foldername = questtitle
				quest.setTitle(questFolder);
				
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
				
			}else{
				return null;
			}
	}
	
	private static Quest ReadQuestXML(String path, Quest quest) throws ParserConfigurationException, SAXException, IOException{

		List<String> nextQuest = new ArrayList<>();
		
		File file = new File(path + sep + Quest.FiLE_QUEST);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName(Quest.XML_QUEST);
		
		for (int i = 0; i < nList.getLength(); i++) {
			 
			Node nNode = nList.item(i);
	 
			//System.out.println("\nCurrent Element:" + nNode.getNodeName());
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				//quest.setTitle(eElement.getElementsByTagName(Quest.XML_TITLE).item(0).getTextContent());
				quest.setXp(Integer.parseInt(eElement.getElementsByTagName(Quest.XML_XP).item(0).getTextContent()));
				try{
				String s = eElement.getElementsByTagName(Quest.XML_STATE).item(0).getTextContent();
					if(s.contains(Quest.STATE_SELECTABLE) || s.contains(Quest.STATE_LOCKED))
						quest.setState(eElement.getElementsByTagName(Quest.XML_STATE).item(0).getTextContent());
					else
						throw new NullPointerException();
						
				}catch(NullPointerException e){
					//setting to status locked
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
		
		//TODO Sort
		
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
		//Excludes Profile Folder and execludes FileNames
		  if(listOfFiles[i].isFile() && listOfFiles[i].isAbsolute() != true){
			  fileNames.add(listOfFiles[i].getName());  
		  }
		 }
		
			return fileNames;
	}


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
	 * @return the folderName
	 */
	public String getFolderName() {
		return folderName;
	}

	/**
	 * @param folderName the folderName to set
	 */
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	 * 
	 * @return String Value of Date
	 */
	public String getStringDate(){
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Quest.TIME_FORMAT);
		return DATE_FORMAT.format(date);
	}	
	
	
	
}

