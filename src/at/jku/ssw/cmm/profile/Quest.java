/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
package at.jku.ssw.cmm.profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.ParseException;
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

	/**
	 * The File Seperator of the System
	 */
	public static String sep = System.getProperty("file.separator");
	
	/**
	 * Main Title of the Quest
	 */
	private String title;					

	/**
	 * TODO Token of the Quest
	 */
	private Token token;							

	/**
	 * All languages are collected in an List<String>
	 * etc.
	 */
	private boolean description;			

	/**
	 * true if the quest has a style.css
	 */
	private boolean style;			
	
	/**
	 * true if the Quest is optional
	 */
	private boolean optional;
	
	/**
	 * true if the Quest has a ref.cmm
	 */
	private boolean ref;
	
	/**
	 * true if the Quest has a input.txt
	 */
	private boolean input;
	
	/**
	 * State of the Quest, this can be: locked, selectable, inprogress, open or finished
	 */
	private String state;					

	/**
	 * Reward which is shown on Quest finish
	 */
	private String rewardPath;					

	/**
	 * Last edited Date
	 */
	private Date date;						
		
	/**
	 * Initial path of the Quest
	 */
	private String initPath;				

	/**
	 * Relative Path of the Package Folder
	 * Packages folderName of the Quest
	 */
	private String packagePath;				
	
	/**
	 * Path of the Quest
	 */
	private String questPath;
	
	
	/**
	 * Files and folderNames
	 */
	public static final String
		FILE_DESCRIPTION = "description.html",
		FILE_STYLE = "style.css",
		FiLE_QUEST = "quest.xml",
		FILE_REF = "ref.cmm",
		FILE_INPUT_TXT = "input.txt",
		FILE_INPUT_CMM = "input.cmm",
		FOLDER_TOKENS = "tokens";
	
	/**
	 * Strings for reading the XML file
	 */
	public static final String
		XML_QUEST = "quest",
		XML_TITLE = "title",
		XML_LEVEL = "level",
		XML_TOKEN = "token",
		XML_NEXTQUEST = "nextquest",
		XML_STATE = "state",
		XML_REWARD = "reward",
		XML_OPTIONAL = "optional";

	/**
	 * Strings for the correct State
	 */
	public static final String
		STATE_LOCKED = "locked",
		STATE_SELECTABLE = "selectable",
		STATE_INPROGRESS = "inprogress",
		STATE_OPEN = "open",
		STATE_FINISHED = "finished";
	
	/**
	 * The Time format for saving Quests, or getting Quests
	 */
	public static final String TIME_FORMAT = "dd-MM-yyyy:HH:mm:SS";
		
	/**
	 * Reads only one Quest, but only the .xml file.
	 * @param allPackagesPath: the path where all Packages are saved
	 * @param packagePath: the foldername of the packagePath
	 * @param questPath: the foldername of the quests folder
	 * @return quest
	 */
	public static Quest ReadQuest(String allPackagesFolder, String packageFolder, String questFolder) {	
			
			Quest quest = new Quest();
			String path = allPackagesFolder + sep + packageFolder + sep + questFolder;
			
			//Read all FileNames of the current path set before
			List<String> fileNames = ReadFileNames(path);
			
			
			//if the files are there, setting them to true
				if(fileNames.contains(Quest.FILE_DESCRIPTION))
					quest.setDescription(true);	
				if(fileNames.contains(Quest.FILE_STYLE))
					quest.setStyle(true);
				if(fileNames.contains(Quest.FILE_REF))
					quest.setRef(true);
				if(fileNames.contains(Quest.FILE_INPUT_TXT) || fileNames.contains(Quest.FILE_INPUT_CMM))
					quest.setInput(true);
				
				//Setting Quest Paths for later use:
				quest.setInitPath(allPackagesFolder);
				quest.setPackagePath(packageFolder);
				quest.setQuestPath(questFolder);
				
				try {
					//Reading the XML File
					quest = ReadQuestXML(path,quest);
					System.out.println("ReadQuest" + quest.getQuestPath());
				} catch (ParserConfigurationException | SAXException | IOException e) {
					System.out.println("ReadQuest Error:" + quest.getQuestPath());
					//For Debugging diseases
					e.printStackTrace();
				}
				
				
				//Returning the loaded Quest
				if(quest.isDescription() && quest.isInput() && quest.isRef())
					return quest;
				else
					return null;
				
				
				

	}
	
	/**
	 * Only reads the .xml File and adding the current files into the quest
	 * @param path: Path of the Profile
	 * @param quest: quest file without .xml
	 * @return the quest file
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static Quest ReadQuestXML(String path, Quest quest) throws ParserConfigurationException, SAXException, IOException{
		
		File file = new File(path + sep + Quest.FiLE_QUEST);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = null;
		try{
		 doc = dBuilder.parse(file);
		
		}catch(FileNotFoundException e){
			System.err.println(file + " not found!" + " trying to open Quest without .xml");
			quest.setTitle(quest.getQuestPath());
			quest.setState(STATE_SELECTABLE);
			
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
					String relPath = eElement.getElementsByTagName(Quest.XML_TOKEN).item(0).getTextContent();
					String tokeninitPath = quest.getInitPath() + sep + quest.packagePath + sep + Quest.FOLDER_TOKENS;
					
					//Reading the spezific <token>.xml file, on error setting to null
					quest.setToken(Token.readToken(tokeninitPath, relPath));
					
				}catch(NullPointerException e){
					//No Token found
					quest.setToken(null);
				}
				
				try{
					quest.setRewardPath(eElement.getElementsByTagName(Quest.XML_REWARD).item(0).getTextContent());
				}catch(NullPointerException e){
					//No Token found
					quest.setRewardPath(null);
				}
				
				try{
					quest.setOptional(Boolean.parseBoolean(eElement.getElementsByTagName(Quest.XML_OPTIONAL).item(0).getTextContent()));
				}catch(NullPointerException e){
					//No Token found
					quest.setOptional(false);
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
					//quest.setState(Quest.STATE_LOCKED);
					quest.setState(Quest.STATE_SELECTABLE);
				}
				
			}
		}
		
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
		  if(listOfFiles[i].isDirectory() && 
				  !listOfFiles[i].getName().equals(Quest.FOLDER_TOKENS)){
			  folderNames.add(listOfFiles[i].getName());  
		  }
		 }
		
		//Sort by Name
			Collections.sort(folderNames);
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

    public String toString() {
    	//TODO
        return "[" + state + "] " + title;
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
	public Token getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(Token token) {
		this.token = token;
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
	public String getRewardPath() {
		return rewardPath;
	}

	/**
	 * @param reward the reward to set
	 */
	public void setRewardPath(String rewardPath) {
		this.rewardPath = rewardPath;
	}

	public boolean isRef() {
		return ref;
	}

	public void setRef(boolean ref) {
		this.ref = ref;
	}

	public boolean isInput() {
		return input;
	}

	public void setInput(boolean input) {
		this.input = input;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}	
	
	
	
	
	
}

