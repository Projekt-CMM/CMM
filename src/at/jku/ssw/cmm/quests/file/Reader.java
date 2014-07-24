package at.jku.ssw.cmm.quests.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


import at.jku.ssw.cmm.quests.datastructs.Quest;
import at.jku.ssw.cmm.quests.datastructs.Reward;


public class Reader{
	private String initPath  = "packages";
	private String packagePath = "default";
	private String questsPath = "quests";
	private String rewardPath = "rewards";
	
	private String sep = System.getProperty("file.separator");
	
	private ArrayList<String> folderNames = new ArrayList<>();
	private ArrayList<String> fileNames = new ArrayList<>();
	
	private ArrayList<Quest> allQuests = new ArrayList<>();
	
	public Reader(){
	}
	
	public Reader(String questspath, String rewardpath){
		this.questsPath = questspath;
		this.rewardPath = rewardpath;
	}
	
	public Reader(String questPath, String rewardPath, String packagePath, String initPath){
		this.questsPath = questPath;
		this.rewardPath = rewardPath;
		this.packagePath = packagePath;
		this.initPath = initPath;
	}
	
	public void init(){
		ReadFolderNames();
	}
	
	private ArrayList<String> ReadFolderNames(){
	 	folderNames.clear();
	 	
			File folder = new File(initPath);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {		
		  if(listOfFiles[i].isDirectory()){
			  folderNames.add(listOfFiles[i].getName());  
			System.out.println(listOfFiles[i].getName());
		  }
		 }
		return folderNames;
		
	}
	
	private void ReadFileNames(String packagePath, String subfolder){
		fileNames.clear();
		
		File folder = new File(initPath + sep + packagePath + sep + subfolder);
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < listOfFiles.length; i++) {		
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".xml"))
				fileNames.add(listOfFiles[i].getName());  
			 }		
	}
	public ArrayList<Quest> ReadAllQuests(String packagePath){
		ReadFileNames(packagePath, questsPath);
		
		for(int i = 0; i < fileNames.size(); i++)
			try {
				allQuests.add(ReadQuest(packagePath, fileNames.get(i)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return allQuests;
	}
	
	private Quest ReadQuest(String packagePath, String filename)throws SAXException, FileNotFoundException, IOException{
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		QuestContentHandler handler = new QuestContentHandler();
		

		//Pfad zur XML Datei
	      FileReader reader = new FileReader(initPath + sep + packagePath + sep + questsPath + sep +filename);
	      InputSource inputSource = new InputSource(reader);
	      
	      //Handler wird übergeben
	      xmlReader.setContentHandler(handler );

	      //Parsen wird gestartet
	      xmlReader.parse(inputSource);

	      //Aktuelle Quest wird zurueckgegeben
	     return handler.getQuest();        
}
	
	public Reward ReadReward(String packagePath, String filename) {
		try {		
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		RewardContentHandler handler = new RewardContentHandler();
		

		//Pfad zur XML Datei
	      FileReader reader = new FileReader(initPath + sep + packagePath + sep + rewardPath + sep + filename);
	      InputSource inputSource = new InputSource(reader);
	      
	      //Handler wird übergeben
	      xmlReader.setContentHandler(handler);

	      //Parsen wird gestartet
			xmlReader.parse(inputSource);
			
			//Return Value
		     return handler.getReward();
			
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			
		return null; //TODO Throw new

	}
	
	public ArrayList<String> getFolderNames(){
		return folderNames;
		
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
	
	
	
}