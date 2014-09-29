package at.jku.ssw.cmm.profile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
	public static String sep = System.getProperty("file.separator");
	
	//General Variables
	private String title;
	private String state;
	private String token;
	private ArrayList<String> nextQuest;
	private int xp;
	private boolean style;
	private boolean description;
	
	//Temporary
	private String questPath;
	private String packagePath;
	private ArrayList<String> FileNames;
	
	//Folders and Files
	private String initPath;
	private String folderName;
	private String fileName;
	
	public static final String
		FILE_DESCRIPTION = "description.html",
		FILE_STYLE = "style.css";
	
	public static final String
		XML_QUEST = "quest",
		XML_TITLE = "title",
		XML_XP = "xp",
		XML_TOKEN = "token",
		XML_NEXTQUEST = "nextquest",
		XML_STATE = "state";

	/**
	 * For Reading All Quests
	 * @param AllPackagesPath: the initial Path of all Packages
	 * @return allQuests: containing all Quests sorted TODO
	 */
	public static ArrayList<Quest> ReadAllQuests(String allPackagesPath){
		ArrayList<String> packageFolderNames = ReadFolderNames(allPackagesPath);
		ArrayList<Quest> allQuests = new ArrayList<>();
		
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
	public static ArrayList<Quest> ReadPackageQuests(String allPackagesPath, String packagePath){
		
		ArrayList<String> questFolderNames = ReadFolderNames(allPackagesPath + sep + packagePath);
		ArrayList<Quest> packageQuests = new ArrayList<>();
		
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
	public static Quest ReadQuest(String allPackagesPath, String packagePath, String questPath) {	
			Quest quest = new Quest();
			String path = allPackagesPath + sep + packagePath + sep + questPath;
			
			ArrayList<String> fileNames = ReadFileNames(path);
			if(fileNames != null){
				if(fileNames.contains(Quest.FILE_DESCRIPTION))
					quest.setDescription(true);
				if(fileNames.contains(Quest.FILE_STYLE))
					quest.setStyle(true);
				
				//Setting Quest Paths for later use:
				quest.setTitle(questPath);
				quest.setInitPath(allPackagesPath);
				quest.setPackagePath(packagePath);
				quest.setQuestPath(questPath);//TODO remove quest path variable (no longer used)
				
				try {
					quest = ReadQuestXML(path,quest);
				} catch (ParserConfigurationException | SAXException
						| IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return quest;
				
			}else{
				return null;
			}
	}
	
	private static Quest ReadQuestXML(String path, Quest quest) throws ParserConfigurationException, SAXException, IOException{

		ArrayList<String> nextQuest = new ArrayList<>();
		
		File file = new File(path + sep + "quest.xml");
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
				quest.setState(eElement.getElementsByTagName(Quest.XML_STATE).item(0).getTextContent());
				
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
	 * @return ArrayList<String> folderNames / or null on non existing folder
	 */
	public static ArrayList<String> ReadFolderNames(String path) {
		ArrayList<String> folderNames = new ArrayList<>();
		
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
	 * @return ArrayList<Strin> fileNames / or null on non existing folder
	 */
	public static ArrayList<String> ReadFileNames(String path) {
		ArrayList<String> fileNames = new ArrayList<>();
		
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
	 * @return the fileNames
	 */
	public ArrayList<String> getFileNames() {
		return FileNames;
	}

	/**
	 * @param fileNames the fileNames to set
	 */
	public void setFileNames(ArrayList<String> fileNames) {
		FileNames = fileNames;
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
	public ArrayList<String> getNextQuest() {
		return nextQuest;
	}

	/**
	 * @param nextQuest the nextQuest to set
	 */
	public void setNextQuest(ArrayList<String> nextQuest) {
		this.nextQuest = nextQuest;
	}	
	
	
}

