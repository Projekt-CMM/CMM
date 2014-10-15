package at.jku.ssw.cmm.profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Package {
	//File Seperator
	private static String sep = System.getProperty("file.separator");
	
	//Variablen abgeleitet von der package.xml
	private String title;					//Titel des Packages
	private int minLevel;					//Minimaler Level
	
	//Ordner der Quest
	private String initPath;				//Inizialisierungspfad:
	private String packagePath;				//Package-Ordner
	private List<Quest> questList;
	
	private static final String
		FILE_PACKAGE = "package.xml";
	
	private static final String
		XML_PACKAGE = "package",
		XML_TITLE = "title",
		XML_MINLEVEL = "minlevel";
	
	public static Package readPackage(String initPath, String packagePath){
		Package package1;

		
		package1 = readPackageFile(initPath, packagePath);
		package1.setQuestList(readPackageQuests(initPath,packagePath,package1));
		

		
		
		return package1;
	}
	
	/**
	 * Reads a Package + package.xml
	 * @param initPath: mostly "packagees" path
	 * @param packagePath: the folder name of the Package
	 * @return Package
	 * @throws XMLReadingException 
	 */
	private static Package readPackageFile(String initPath, String packagePath){
		Package package1 = new Package();
		
		//Setting Paths of the Files
		package1.setInitPath(initPath);
		package1.setPackagePath(packagePath);
		
		
		try {
			package1 = readPackageXML(initPath, packagePath, package1);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			
			//When an Error occurs, setting Package Levels
			package1.setMinLevel(0);
			package1.setTitle(packagePath);
		}
		
		return package1;
	}
	
	private static Package readPackageXML(String initPath,String packagePath, Package package1) throws ParserConfigurationException, SAXException, IOException{
		
		File file = new File(initPath + sep + packagePath + sep + Package.FILE_PACKAGE);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		
		//Initialize file
		Document doc = null;
		
		try{
			doc = dBuilder.parse(file);;
		}catch(FileNotFoundException e){
				System.err.println(file + " not found!");		
				return package1;
			}			
			
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName(Package.XML_PACKAGE);
		
		for (int i = 0; i < nList.getLength(); i++) {
			 
			Node nNode = nList.item(i);
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				try{
					package1.setTitle(eElement.getElementsByTagName(Package.XML_TITLE).item(0).getTextContent());
				}catch(NullPointerException e){
					//If No Title exists setting title to FoderName
					package1.setTitle(packagePath);
				}
				
				try{
				package1.setMinLevel(Integer.parseInt(eElement.getElementsByTagName(Package.XML_MINLEVEL).item(0).getTextContent()));
				}catch(NullPointerException e){
					//Setting Min-Level to 0
					package1.setMinLevel(0);
				}
			}
		}
		
		return package1;
	}
	
	/**
	 * For reading all Quests in one Package
	 * @param package1 
	 * @param allPackagesPath: the initial Path of the Packages
	 * @param packagePath: the folder Name of the Package
	 * @return packageQuests: all Quests of one package
	 */
	private static List<Quest> readPackageQuests(String allPackagesPath, String packagePath, Package package1){
		
		List<String> questFolderNames = Quest.ReadFolderNames(allPackagesPath + sep + packagePath);
		List<Quest> packageQuests = new ArrayList<>();
		
		for(int i = 0;i < questFolderNames.size(); i++){
			Quest quest;
					quest = Quest.ReadQuest(allPackagesPath, packagePath, questFolderNames.get(i));
					
					
					if(quest!= null && !quest.isDescription()){
						//min Package level is exported on the quest
						quest.setMinLevel(package1.getMinLevel());
						packageQuests.add(quest);			
					}

					
			}
				
		
		
		return packageQuests;
	}
	
	/**
	 * For Reading All Quests
	 * @param AllPackagesPath: the initial Path of all Packages
	 * @return allQuests: containing all Quests sorted TODO
	 */
	public static List<Quest> ReadAllQuests(String allPackagesPath){
		List<Package> packgeList = new ArrayList<>();
		
		List<String> packageFolderNames = Quest.ReadFolderNames(allPackagesPath);
		List<Quest> allQuests = new ArrayList<>();
		
		if(packageFolderNames != null){
			for(int i = 0; i < packageFolderNames.size();i++){
				Package package1 = readPackageFile(allPackagesPath, packageFolderNames.get(i));
				List<Quest> tempQuests = readPackageQuests(allPackagesPath, packageFolderNames.get(i),package1);
				
				if( tempQuests != null){
					package1.getQuestList().addAll(tempQuests);
				}
			}
			
			return allQuests;
		}
		return null;
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
	 * @return the questList
	 */
	public List<Quest> getQuestList() {
		return questList;
	}

	/**
	 * @param questList the questList to set
	 */
	public void setQuestList(List<Quest> questList) {
		this.questList = questList;
	}
	
	
}
