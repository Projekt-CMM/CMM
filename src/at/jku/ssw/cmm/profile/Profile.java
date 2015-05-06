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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import at.jku.ssw.cmm.gui.file.LoadStatics;

public class Profile {
	
	/**
	 * System specific file separator
	 */
	public static String sep = System.getProperty("file.separator");

	/**
	 * The Name of the Profile
	 */
	private String name;						

	/**
	 * relative Path to the profile image
	 */
	private String profileimage;	

	/**
	 * Current Quest, or current file
	 */
	private String current;			

	/**
	 * is this Profile a master or not, not used that time
	 */
	private boolean master;	
	
	
	/**
	 * initial Profile path
	 */
	private String profilePath;
	
	private Quest quest;
	
	//Temporary variables
	/**
	 * name of the packages Folder
	 */
	private String packagesPath;
	
	/**
	 * list of profile Quests
	 */
	private List<Quest> profileQuests;
	
	//Static final Strings
	public static final String
		FILE_PROFILE = "profile.cp",
		FILE_PACKAGESPATH = "packages",
		FILE_PROFILEIMAGE = "avatar",
		FILE_BEFORE_PROFILE = "profile_",
		FILE_DEFAULTIMAGE = "images/prodef.png";
	
	public static final String
		FILE_EXTENDSION = "cp";
	
	public static final String
		XML_NAME = "name",
		XML_LEVEL = "level",
		XML_PROFILE = "profile",
		XML_STATE = "state",
		XML_QUEST = "quest",
		XML_ID = "id",
		XML_PACKAGE = "package",
		XML_DATE ="date",
		XML_TOKEN = "token",
		XML_PROFILEIMAGE = "profileimage",
		XML_CURRENT = "current",
		XML_MASTER = "master",
		XML_FILEPATH = "filepath";
	
	public static final String 
		IMAGE_DEFAULT = "images/prodef.png";
	
	public Profile(){}
	
	public Profile(String profilePath, String packagePath){
		this.profilePath = profilePath;
		this.packagesPath = packagePath;
	}

	/**
	 * Returning Sorted Package Quests:
	 * Sorting: (by date and Name)
	 * @param profile
	 * @param allPackagesPath:  the PackageFolderName of the Profile mostly "packages"
	 * @param packagePath: 		the PackagePath of the Current Profile
	 * @return	Package with status updated
	 */
	
	public static Package ReadPackageQuests(Profile profile, String packagePath){
		//initial Packages Path
		String first = packagePath.substring(0,packagePath.indexOf(File.separator));
		
		//the Packages Path
		String last = packagePath.substring(1+packagePath.indexOf(File.separator));
		
		return ReadPackageQuests( profile,first, last);
	}	
	
public static Quest ReadLastQuest(Profile profile){

	if(profile.getProfileQuests() != null)
		for(Quest q: profile.getProfileQuests()){
			if(q != null && q.getState().equals(Quest.STATE_OPEN)){
				Quest quest = Quest.ReadQuest("packages", q.getPackagePath(), q.getQuestPath());
				if(quest != null){
					updateQuestVariables(quest,q);
					return quest;
				}
			}
		}
	
	
	
	return null;
}

/**
 * Updates the opened Quest in the profile, if the current Quest is not finished
 * @param profile
 * @param quest
 * @return the profile
 */
public static Profile UpdateOpen(Profile profile, Quest quest){
	if(profile.getProfileQuests() != null)
		for(Quest q: profile.getProfileQuests()){
			if(q.getState().equals(Quest.STATE_OPEN)){
				q.setState(Quest.STATE_INPROGRESS);
			}
			if(q.getPackagePath().equals(quest.getPackagePath()) &&
					q.getQuestPath().equals(quest.getQuestPath()) &&
					!q.getState().equals(Quest.STATE_FINISHED)){
				q.setnewDate();
				q.setState(Quest.STATE_OPEN);
			}
		}
	
	try {
		profile.writeProfile();
	} catch (XMLWriteException e) {
		e.printStackTrace();
	}
	
	return profile;
}
	
/**
 * Returning Sorted Profile + Package Quests:
 * Sorting: (by date and Name)
 * @param profile
 * @param allPackagesPath:  the PackageFolderName of the Profile mostly "packages"
 * @param packagePath: 		the PackagePath of the Current Profile
 * @return	List<Quest> packageQuests with status updated
 */
	public static Package ReadPackageQuests(Profile profile, String allPackagesPath, String packagePath){
		if(profile == null)
			return null;
		
		Package package1 = Package.readPackage(allPackagesPath, packagePath);
		List<Quest> packageQuests = package1.getQuestList();
		List<Quest> profileQuests = profile.getProfileQuests();
		
		if(packageQuests == null)
			return null;
		
		if(profileQuests == null){
			package1.setQuestList(sortQuestList(packageQuests));
			return package1;
		}
				
		for(Quest packageQuest : packageQuests){
			for(Quest profileQuest : profileQuests){
				if(profileQuest.getQuestPath().equals(packageQuest.getQuestPath()) 
						&& profileQuest.getPackagePath().equals(packageQuest.getPackagePath())){
					updateQuestVariables(packageQuest, profileQuest);	
				}
			}
		}
		
		for(Quest quest: packageQuests){
			if(quest.getPreviousFolder() != null)
			for(Quest prevQ : packageQuests){
				if(quest.getPreviousFolder().equals(prevQ.getQuestPath())){
					if(prevQ.getState().equals(Quest.STATE_FINISHED) ){
						if(!quest.getState().equals(Quest.STATE_OPEN) && 
								!quest.getState().equals(Quest.STATE_INPROGRESS) && 
								!quest.getState().equals(Quest.STATE_FINISHED))
						quest.setState(Quest.STATE_SELECTABLE);
						System.err.println("Test");
					}
				}
				
			}
		}
		
		
		//Sorts the Quests and Returns it
		//TODO Sort on Click
		//package1.setQuestList(sortQuestList(packageQuests));
		
		return package1;
		
	}
	
	/**
	 * Updates the Current Quest with the Profile Quest
	 * @param packageQuest
	 * @param profileQuest
	 */
	private static void updateQuestVariables(Quest packageQuest, Quest profileQuest){
		packageQuest.setState(profileQuest.getState());
		packageQuest.setDate(profileQuest.getDate());
		packageQuest.setCmmFilePath(profileQuest.getCmmFilePath());
	}

	/**
	 * Reads all finished Quests and returns a List of all Tokens
	 * @param profile
	 * @return List<Token>
	 */
	public List<Token> readProfileTokens(){
		List<Token> allTokens = new ArrayList<Token>();
		
		if(getProfileQuests() == null)
			return null;
		
		//Reads all Tokens
		for(Quest q : getProfileQuests()){
			if(q.getState().equals(Quest.STATE_FINISHED) && q.getToken() != null){
				allTokens.add(q.getToken());
			}
		}
			
		return allTokens;
	}
	
	/**
	 * Reading a Profile fully, the "profile.xml"
	 * @param profilePath: without the profile.xml
	 * @param packagesPath
	 * @return profile
	 * @throws XMLReadingException 
	 * @throws ProfileNotFoundException 
	 */
	public static Profile ReadProfile(String profilePath) throws XMLReadingException, ProfileNotFoundException {
		 return ReadProfile( profilePath, Profile.FILE_PACKAGESPATH);
	}
	
	/**
	 * Reading a Profile fully, the "profile.xml"
	 * @param profilePath: without the profile.xml
	 * @param packagesPath
	 * @return profile
	 * @throws XMLReadingException 
	 * @throws ProfileNotFoundException 
	 */
	public static Profile ReadProfile(String profilePath, String packagesPath) throws XMLReadingException, ProfileNotFoundException {
		
		List<String> fileNames = Quest.ReadFileNames(profilePath);
		
		if( fileNames == null || fileNames.isEmpty() )
			throw new ProfileNotFoundException();
		
		if(!fileNames.contains(Profile.FILE_PROFILE))
			throw new XMLReadingException();
		
		Profile profile = new Profile(profilePath,packagesPath);
		
		//setting default Name, can be changed by .profile
		File file = new File(profilePath);
		profile.setName(file.getName());//TODO quick  fix
		profile.setInitPath(profilePath);
		
		String path = profilePath + sep + Profile.FILE_PROFILE;
		
		try {
			profile.ReadProfileXML(path);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

			
		return profile;
	}
	
	/**
	 * For reading the .xml file of the Profile "profile.xml"
	 * @param path: of the profile.xml
	 * @param profile
	 * @return fully working profile
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void ReadProfileXML(String path) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
		        .newInstance();
		    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		    Document document = docBuilder.parse(new File(path));
		    readxml(document.getDocumentElement());  
	}
	
	/**
	 * Sub-Klass of ReadProfileXML
	 * @param node
	 * @param profile
	 * @return profile
	 */
	private void readxml(Node node) {
	    //System.out.println(node.getNodeName());
	    
		List<Quest> questList = new ArrayList<>();
		Quest quest = new Quest();
			if(node.getNodeName().equals(Profile.XML_NAME))
				 setName(node.getTextContent());
			
			if(node.getNodeName().equals(Profile.XML_MASTER))
				if(node.getTextContent().equals("true"))
					setMaster(true);
							
			if(node.getNodeName().equals(Profile.XML_PROFILEIMAGE))
        		setProfileimage(node.getTextContent());
			
			if(node.getNodeName().equals(Profile.XML_CURRENT))
				setCurrent(node.getTextContent());
			
			if(node.getNodeName().equals(Profile.XML_STATE)){
				String s = node.getAttributes().item(0).getTextContent();
			if(s.equals(Quest.STATE_FINISHED) || s.equals(Quest.STATE_INPROGRESS) || s.equals(Quest.STATE_SELECTABLE) || s.equals(Quest.STATE_OPEN)){
				
				quest.setState(s); //setting the quest State
				
				//Iterate through the Nodes
				 NodeList nodeList = node.getChildNodes();
				    for (int i = 0; i < nodeList.getLength(); i++) {
				        Node currentNode = nodeList.item(i);
				        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				        	if(currentNode.getNodeName().equals(Profile.XML_QUEST)) //quest
				        		quest.setQuestPath(currentNode.getTextContent());
				        	
				        	if(currentNode.getNodeName().equals(Profile.XML_PACKAGE)) //package
				        		quest.setPackagePath(currentNode.getTextContent());
				        	
				        	if(currentNode.getNodeName().equals(Profile.XML_DATE)) //date
				        		quest.setStringDate(currentNode.getTextContent());
				        	
				        	if(currentNode.getNodeName().equals(Profile.XML_FILEPATH)) //cmmfilepath
				        		quest.setCmmFilePath(currentNode.getTextContent());
				        	
				        	if(currentNode.getNodeName().equals(Profile.XML_TOKEN)){ //token
				        		
				        		//getting the relative and absolute Path of the Token
				        		String relPath = currentNode.getTextContent();
				        		String absoluteInitPath = getInitPath() + sep + getPackagesPath() + sep + quest.getPackagePath() + sep + Quest.FOLDER_TOKENS;				        		
				        		
				        		quest.setToken(Token.readToken(absoluteInitPath, relPath ));
				        	}
				       	}
				    }
				    
				    //When the Profile Quests wasn't set
				    if(getProfileQuests() != null)
				    	questList = getProfileQuests();
				    
				    //if all Components are there than only the Quest will be added to ProfileQuests
				    if(quest != null && quest.getQuestPath() != null && quest.getPackagePath() != null)
				    	questList.add(quest);

				    setProfileQuests(questList);
				}
			}
			
			
			  NodeList nodeList = node.getChildNodes();
			    for (int i = 0; i < nodeList.getLength(); i++) {
			        Node currentNode = nodeList.item(i);
			        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
			            //calls this method for all the children which is Element
			        	readxml(currentNode);
			        }
		    }
		}
	
	/**
	 * Writing / Saving the Profile to the specific path
	 * @param profile
	 * @param profilePath
	 * @throws XMLWriteException 
	 */
	public void writeProfile() throws XMLWriteException{
		if(getInitPath() == null)
			throw new XMLWriteException();
		
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        
 

        String path = getInitPath() + sep + Profile.FILE_PROFILE;
        
            try {
				icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElementNS(path, Profile.XML_PROFILE);
            doc.appendChild(mainRootElement);
            
           if(getName() != null) 
        	   mainRootElement.appendChild(writeProfileElements(doc, mainRootElement, Profile.XML_NAME, getName()));
           
           //TODO Check
           if(isMaster())
               mainRootElement.appendChild(writeProfileElements(doc, mainRootElement, Profile.XML_MASTER, isMaster() +""));
        	   
           if(getProfileimage() != null)
            	mainRootElement.appendChild(writeProfileElements(doc, mainRootElement, Profile.XML_PROFILEIMAGE, getProfileimage()));
            
            if(getCurrent() != null)
                mainRootElement.appendChild(writeProfileElements(doc, mainRootElement, Profile.XML_CURRENT, getCurrent()));
            
            if(getProfileQuests() != null)
            // append xp and state to root element
            for(Quest q: getProfileQuests())
            	mainRootElement.appendChild(writeProfileState(doc, q));

            // output DOM XML to console
            Transformer transformer;
			transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            
            //Writing into file
            	StreamResult result = new StreamResult(new File(path));
            	transformer.transform(source, result);
            
            
            //Debug output
            //System.out.println("\nXML DOM Created Successfully..");
            
			} catch (ParserConfigurationException | TransformerConfigurationException
					| TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			} catch (TransformerException | DOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	
	private static Node writeProfileState(Document doc, Quest quest) throws DOMException, XMLWriteException{
        Element state = doc.createElement(Profile.XML_STATE);
        state.setAttribute("id", quest.getState());
        state.appendChild(writeProfileElements(doc, state, Profile.XML_QUEST, quest.getQuestPath()));
        state.appendChild(writeProfileElements(doc, state, Profile.XML_PACKAGE, quest.getPackagePath()));
        state.appendChild(writeProfileElements(doc, state, Profile.XML_DATE, quest.getStringDate()));
        if(quest.getCmmFilePath() != null)
        	state.appendChild(writeProfileElements(doc, state, Profile.XML_FILEPATH, quest.getCmmFilePath()));

        //TODO check this in a real programm
        if(quest.getToken() != null)
        	state.appendChild(writeProfileElements(doc, state, Profile.XML_TOKEN, quest.getToken().getRelPath()));
        
        return state;
	}
	
    private static Node writeProfileElements(Document doc, Element element, String name, String value) throws XMLWriteException{
        if(value == null)
        	throw new XMLWriteException();
    	
	    	Element node = doc.createElement(name);
	        node.appendChild(doc.createTextNode(value));
	        return node;
	        
        	
        
    }
    /**
     * Sorts the List of quests, at first status, opened, and inprogress, others can be find at the bottom of the list, also sorts by date
     * @param quests
     * @return
     */
	private static List<Quest> sortQuestList(List<Quest> quests){
		//Sorting List, up tp Status, Opened, Inprogress, others are at bottom list
		final List<String> definedOrder =  Arrays.asList(Quest.STATE_LOCKED,Quest.STATE_FINISHED,Quest.STATE_SELECTABLE, Quest.STATE_INPROGRESS,Quest.STATE_OPEN);
		Comparator<Quest> statecomparator = new Comparator<Quest>(){
			   
			@Override
			   /*
			    * Sort the List by State
			    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			    */
				public int compare(final Quest o1, final Quest o2){
			    	
			        	return Integer.valueOf(definedOrder.indexOf(o2.getState())).compareTo(Integer.valueOf(definedOrder.indexOf(o1.getState())));
			        	
			    }};

			    
			    //Sort the ArrayList by Date
			    Comparator<Quest> timecomparator = new Comparator<Quest>(){
					    	@Override
					    	public int compare(final Quest o1, final Quest o2){
									Date date1 = o1.getDate();
									Date date2 = o2.getDate();
											
					    		if(o1.getDate() != null && o2.getDate() != null && o1.getState()!= null && o2.getState() != null){
					    			if(o1.getState().equals(o2.getState())){
					    				if(o1.getState().equals(Quest.STATE_INPROGRESS)){
					    					return date2.compareTo(date1);
					    				}
					    				else{
					    					return date1.compareTo(date2);					    					
					    				}
					    			}
					    		}
								return 0;
				   	}
			    };
			    
			//Opening the Sorting Method
			Collections.sort(quests,statecomparator);
			Collections.sort(quests,timecomparator);
				
	    return quests;
	}
	
	/**
	 * Adding the specific Quest to the Profile and sets the quest to inprogress
	 * @param profile
	 * @param quest
	 * @return profile
	 * @throws XMLWriteException
	 */
	public Profile changeQuestStateToInprogress( Quest quest) throws XMLWriteException{
		return changeQuestState( quest, Quest.STATE_INPROGRESS);
	}

	/**
	 * Adding the specific Quest to the Profile and sets the quest to finished
	 * @param profile
	 * @param quest
	 * @return profile
	 * @throws XMLWriteException
	 */
	public Profile changeQuestStateToFinished(Quest quest) throws XMLWriteException{
		return changeQuestState( quest, Quest.STATE_FINISHED);
	}
	
	/**
	 * Updating the Current Profile
	 * @param profile
	 * @param quest
	 * @return profile
	 * @throws XMLWriteException
	 */
	public Profile updateProfileQuestPath( Quest quest) throws XMLWriteException{
		return changeQuestState( quest, null);
	}
	
/**
 * Changes the State of the Quest, adding new Date and returning new Profile Object.
 * On wrong Quest file, returning the old Profile
 * @param profile
 * @param quest
 * @param state:
 * - Quest.State_SELECTABLE
 * - Quest.State_INPROGRESS
 * - Quest.State_FINISHED
 * @return profile
 * @throws XMLWriteException 
 */
	public Profile changeQuestState( Quest quest, String state) throws XMLWriteException{
		
		//Returning old Profile if Quest is Null
		if(quest == null)
			return this;
		
		//TODO Tokens
		if(state != null && state.equals(Quest.STATE_FINISHED)){
			if(quest.getToken() != null){
				String questTokenPathFile = quest.getInitPath()+ sep + quest.getPackagePath() + sep + Quest.FOLDER_TOKENS;
				String profileTokenPath = this.getInitPath() + sep + Profile.FILE_PACKAGESPATH + sep +  quest.getPackagePath() + sep + Quest.FOLDER_TOKENS ;
			
				try {
				
					//Copying the image of the token <image.png>
					if(quest.getToken().getImagePath() != null)
						LoadStatics.copyFileUsingStream(new File(questTokenPathFile + sep + quest.getToken().getImagePath()), new File(profileTokenPath + sep + quest.getToken().getImagePath()));
					
					//Copying the <token>.xml
					LoadStatics.copyFileUsingStream(new File(questTokenPathFile + sep + quest.getToken().getRelPath()), new File(profileTokenPath + sep + quest.getToken().getRelPath()));
				} catch (IOException e) {
					System.err.println("Token could not be copyed");
				}
			}
		}
		
		//New Quest List
		List<Quest> questList;
		
		//Setting a new Date only if the State has changed
		if(state != null){
			quest.setnewDate();
			quest.setState(state);
		}
		
		//Only to handle wrong usage of the method
		if(quest.getDate() == null)
			quest.setnewDate();
		
		//getting Profile Quests
		if(this.getProfileQuests() != null)
			questList = this.getProfileQuests();
		else 
			questList = new ArrayList<>();
	
		//only if Title & Package already exists
		for(int i = 0; i < questList.size(); i++){
			if(questList.get(i).getQuestPath().equals(quest.getQuestPath()) && questList.get(i).getPackagePath().equals(quest.getPackagePath())){
				
				questList.set(i, quest);
				this.setProfileQuests(questList);
				
				//save Profile
				this.writeProfile();
				
				System.out.println("Quest State changed!");
				return this;
			}
		}

		//if the Variable is not existing in the profile
		questList.add(quest);
		this.setProfileQuests(questList);
		
		//save Profile
		this.writeProfile();
			
		return this;
	}
	
	/**
	 * 
	 * TODO more file Types
	 * 
	 * Deleting old Profile Pic and Copying new Profile pic to the choosen destination + writes into the Profile
	 * Useable file Types:
	 * bmp jpg jpeg wbmp png gif
	 * @param profile
	 * @param sorce
	 * @return Profile
	 * @throws IOException 
	 * @throws XMLWriteException 
	 */
	public void changeProfileImage( String sourcePath) throws IOException, XMLWriteException{
		String extension = sourcePath.substring(sourcePath.lastIndexOf('.'), sourcePath.length());
		
		File oldImage = new File(getInitPath() + File.separator + getProfileimage());
		
		//TODO add all file extensions
		if(sourcePath.endsWith(".bmp") ||
				sourcePath.endsWith(".jpg") ||
				sourcePath.endsWith(".png") ||
				sourcePath.endsWith(".jpeg") ||
				sourcePath.endsWith(".wbmp") ||
				sourcePath.endsWith(".gif") ){
			
				String destPath = getInitPath() + Profile.sep + Profile.FILE_PROFILEIMAGE + extension;
				
				File source  = new File(sourcePath);
				File dest = new File(destPath);
				
				//Copying the File
				LoadStatics.copyFileUsingStream(source, dest);
				setProfileimage(dest.getName());
				writeProfile();
				
				//Deleting old image
				if(!oldImage.getPath().equals(dest.getPath()))
					oldImage.delete();
				
		}else
			throw new IOException();
	}
	
	
    public String toString() {
        return name;
    }
	
	/**
	 * @return the initPath
	 */
	public String getInitPath() {
		return profilePath;
	}
	
	/**
	 * @param initPath the initPath to set
	 */
	public void setInitPath(String initPath) {
		this.profilePath = initPath;
	}
	
	/**
	 * @return the packagesPath
	 */
	public String getPackagesPath() {
		return packagesPath;
	}
	
	/**
	 * @param packagesPath the packagesPath to set
	 */
	public void setPackagesPath(String packagesPath) {
		this.packagesPath = packagesPath;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the profileQuests
	 */
	public List<Quest> getProfileQuests() {
		return profileQuests;
	}

	/**
	 * @param profileQuests the profileQuests to set
	 */
	public void setProfileQuests(List<Quest> profileQuests) {
		this.profileQuests = profileQuests;
	}

	/**
	 * @return the profileimage
	 */
	public String getProfileimage() {
		return profileimage;
	}

	/**
	 * @param profileimage the profileimage to set
	 */
	public void setProfileimage(String profileimage) {
		this.profileimage = profileimage;
	}

	/**
	 * @return the current
	 */
	public String getCurrent() {
		return current;
	}

	/**
	 * @param current the current to set
	 */
	public void setCurrent(String current) {
		this.current = current;
	}

	/**
	 * @return the master
	 */
	public boolean isMaster() {
		return master;
	}

	/**
	 * @param master the master to set
	 */
	public void setMaster(boolean master) {
		this.master = master;
	}

	public void setCurrentQuest( Quest quest ){
		this.quest = quest;
	}
	
	public Quest getCurrentQuest(){
		return this.quest;
	}
}
