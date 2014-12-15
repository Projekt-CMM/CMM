package at.jku.ssw.cmm.profile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

import at.jku.ssw.cmm.gui.utils.LoadStatics;

public class Profile {
	
	//File Seperator
	public static String sep = System.getProperty("file.separator");
		
	
	//General Profile Fields
	private String name;			//OrdnerName = ProfilName
	private int xp;					//Anzahl der XP des Benutzers
	private String profileimage;	//Profile Image
	private String current;			//Current Quest, bzw. current File
	private boolean master;			//Master
	
	//Folder Names
	private String profilePath;		//inizialer Profile Pfad
	private String packagesPath;	//Pfad der Quest Packages
	
	//Temporary variables
	private List<Quest> profileQuests;
	
	//Static final Strings
	public static final String
		FILE_PROFILE = "profile.cp",
		FILE_PACKAGESPATH = "packages",
		FILE_PROFILEIMAGE = "avatar",
		FILE_BEFORE_PROFILE = "profile_";
	
	public static final String
		FILE_EXTENDSION = "cp";
	
	public static final String
		XML_NAME = "name",
		XML_XP = "xp",
		XML_PROFILE = "profile",
		XML_STATE = "state",
		XML_QUEST = "quest",
		XML_ID = "id",
		XML_PACKAGE = "package",
		XML_DATE ="date",
		XML_TOKEN = "token",
		XML_PROFILEIMAGE = "profileimage",
		XML_CURRENT = "current",
		XML_MASTER = "master";
	
	public static final String 
		IMAGE_DEFAULT = "images/prodef.png";

	private static Profile activeProfile;
	
	public Profile(){}
	
	public Profile(String profilePath, String packagePath){
		this.profilePath = profilePath;
		this.packagesPath = packagePath;
	}

	/**
	 * Returning Sorted Package Quests:
	 * Sorting: (by date)
	 * 	- OPEN			
	 *  - INPROGRESS
	 *  - SELECTABLE
	 *  - FINISHED
	 *  - LOCKED
	 * @param profile
	 * @param allPackagesPath:  the PackageFolderName of the Profile mostly "packages"
	 * @param packagePath: 		the PackagePath of the Current Profile
	 * @return	Package with status updated
	 */
	
	public static Package ReadPackageQuests(Profile profile, String packagePath){
		return ReadPackageQuests( profile,Profile.FILE_PACKAGESPATH, packagePath);
	}	
	
/**
 * Returning Sorted Profile + Package Quests:
 * Sorting: (by date)
 * 	- OPEN			
 *  - INPROGRESS
 *  - SELECTABLE
 *  - FINISHED
 *  - LOCKED
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
				if(profileQuest.getQuestPath().equals(packageQuest.getQuestPath()) && profileQuest.getPackagePath().equals(packageQuest.getPackagePath())){
					packageQuest.setState(profileQuest.getState());
					packageQuest.setDate(profileQuest.getDate());
				}
			}
		}
		//Sorts the Quests and Returns it
		package1.setQuestList(sortQuestList(packageQuests));
		return package1;
		
	}
	
	/**
	 * Reading a Profile fully, the "profile.xml"
	 * @param profilePath: without the profile.xml
	 * @param packagesPath
	 * @return profile
	 * @throws XMLReadingException 
	 */
	public static Profile ReadProfile(String profilePath) throws XMLReadingException {
		 return ReadProfile( profilePath, Profile.FILE_PACKAGESPATH);
	}
	
	/**
	 * Reading a Profile fully, the "profile.xml"
	 * @param profilePath: without the profile.xml
	 * @param packagesPath
	 * @return profile
	 * @throws XMLReadingException 
	 */
	public static Profile ReadProfile(String profilePath, String packagesPath) throws XMLReadingException {
		
		List<String> fileNames = Quest.ReadFileNames(profilePath);
		
		if(!fileNames.contains(Profile.FILE_PROFILE))
			throw new XMLReadingException();
		
		Profile profile = new Profile(profilePath,packagesPath);
		
		//setting default Name, can be changed by .profile
		profile.setName(profilePath.split(sep)[profilePath.split(sep).length-1]);
		profile.setInitPath(profilePath);
		
		String path = profilePath + sep + Profile.FILE_PROFILE;
		
		try {
			profile = ReadProfileXML(path, profile);
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
	private static Profile ReadProfileXML(String path, Profile profile) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
		        .newInstance();
		    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		    Document document = docBuilder.parse(new File(path));
		    profile = readxml(document.getDocumentElement(), profile);
		    
	
			return profile;
	}
	
	/**
	 * Sub-Klass of ReadProfileXML
	 * @param node
	 * @param profile
	 * @return profile
	 */
	private static Profile readxml(Node node, Profile profile) {
	    //System.out.println(node.getNodeName());
	    
		List<Quest> questList = new ArrayList<>();
		Quest quest = new Quest();
			if(node.getNodeName().equals(Profile.XML_NAME))
				 profile.setName(node.getTextContent());
		
			if(node.getNodeName().equals(Profile.XML_XP))
				 profile.setXp(Integer.parseInt(node.getTextContent()));
			
			if(node.getNodeName().equals(Profile.XML_MASTER))
				if(node.getTextContent().equals("true"))
					profile.setMaster(true);
							
			if(node.getNodeName().equals(Profile.XML_PROFILEIMAGE))
        		profile.setProfileimage(node.getTextContent());
			
			if(node.getNodeName().equals(Profile.XML_CURRENT))
				profile.setCurrent(node.getTextContent());
			
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
				        	if(currentNode.getNodeName().equals(Profile.XML_TOKEN)){ //token
				        		//TODO make tokens variable
				        		quest.setToken(Token.readToken(currentNode.getTextContent()));
				        	}
				       	}
				    }
				    
				    //When the Profile Quests wasn't set
				    if(profile.getProfileQuests() != null)
				    	questList = profile.getProfileQuests();
				    
				    //if all Components are there than only the Quest will be added to ProfileQuests
				    if(quest != null && quest.getQuestPath() != null && quest.getPackagePath() != null)
				    	questList.add(quest);

				    profile.setProfileQuests(questList);
				}
			}
			
			
			  NodeList nodeList = node.getChildNodes();
			    for (int i = 0; i < nodeList.getLength(); i++) {
			        Node currentNode = nodeList.item(i);
			        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
			            //calls this method for all the children which is Element
			        	profile = readxml(currentNode, profile);
			        }
		    }
				 
	
		    
		    return profile;
			
		}
	
	/**
	 * Writing / Saving the Profile to the specific path
	 * @param profile
	 * @param profilePath
	 * @throws XMLWriteException 
	 */
	public static void writeProfile(Profile profile) throws XMLWriteException{
		if(profile == null || profile.getInitPath() == null)
			throw new XMLWriteException();
		
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        
 

        String path = profile.getInitPath() + sep + Profile.FILE_PROFILE;
        
            try {
				icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElementNS(path, Profile.XML_PROFILE);
            doc.appendChild(mainRootElement);
            
           if(profile.getName() != null) 
        	   mainRootElement.appendChild(writeProfileElements(doc, mainRootElement, Profile.XML_NAME, profile.getName()));
          
           
           if(profile.getXp() != 0) 
        	   mainRootElement.appendChild(writeProfileElements(doc, mainRootElement, Profile.XML_XP, profile.getXp() + ""));
           else
               mainRootElement.appendChild(writeProfileElements(doc, mainRootElement, Profile.XML_XP, 0 + ""));
           
           //TODO Check
           if(profile.isMaster())
               mainRootElement.appendChild(writeProfileElements(doc, mainRootElement, Profile.XML_MASTER, profile.isMaster() +""));
        	   
           if(profile.getProfileimage() != null)
            	mainRootElement.appendChild(writeProfileElements(doc, mainRootElement, Profile.XML_PROFILEIMAGE, profile.getProfileimage()));
            
            if(profile.getCurrent() != null)
                mainRootElement.appendChild(writeProfileElements(doc, mainRootElement, Profile.XML_CURRENT, profile.getCurrent()));
            
            if(profile.getProfileQuests() != null)
            // append xp and state to root element
            for(Quest q: profile.getProfileQuests())
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
        //TODO must be variable
        if(quest.getToken() != null)
        	state.appendChild(writeProfileElements(doc, state, Profile.XML_TOKEN, quest.getToken().getPath()));
        
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
	public static Profile changeQuestStateToInprogress(Profile profile, Quest quest) throws XMLWriteException{
		return changeQuestState(profile, quest, Quest.STATE_INPROGRESS);
	}
	
	/**
	 * Adding the specific Quest to the Profile and sets the quest to selectable
	 * @param profile
	 * @param quest
	 * @return profile
	 * @throws XMLWriteException
	 */
	public static Profile changeQuestStateToSelectable(Profile profile, Quest quest) throws XMLWriteException{
		return changeQuestState(profile, quest, Quest.STATE_SELECTABLE);
	}

	/**
	 * Adding the specific Quest to the Profile and sets the quest to finished
	 * @param profile
	 * @param quest
	 * @return profile
	 * @throws XMLWriteException
	 */
	public static Profile changeQuestStateToFinished(Profile profile, Quest quest) throws XMLWriteException{
		return changeQuestState(profile, quest, Quest.STATE_FINISHED);
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
	public static Profile changeQuestState(Profile profile, Quest quest, String state) throws XMLWriteException{
		
		//Returning old Profile if Quest is Null
		if(quest == null)
			return profile;
		
		//TODO Copy Tokens into the right Profile
		if(state.equals(Quest.STATE_FINISHED)){
			if(quest.getToken() != null){
				String questTokenPathFile = quest.getInitPath()+ sep + quest.getPackagePath() + sep + Quest.FOLDER_TOKENS + sep + quest.getToken();
				String profileTokenPath = profile.getInitPath() + sep + Profile.FILE_PACKAGESPATH + sep +  quest.getPackagePath() + sep + Quest.FOLDER_TOKENS ;
			
				try {
					File dir = new File(profileTokenPath);
					dir.mkdirs();
					//Copying the <token>.xml
					LoadStatics.copyFileUsingStream(new File(questTokenPathFile), new File(profileTokenPath + sep + quest.getToken().getPath()));
					
					//Copying the <tokenimage.png>
					LoadStatics.copyFileUsingStream(new File(questTokenPathFile), new File(profileTokenPath + sep + quest.getToken().getImagePath()));
				} catch (IOException e) {
					System.err.println("Token could not be copyed");
				}
			}
		}
		
		
		//Setting a new Date
		quest.setnewDate();
		quest.setState(state);
		List<Quest> questList;
		
		//getting Profile Quests
		if(profile.getProfileQuests() != null)
			questList = profile.getProfileQuests();
		else 
			questList = new ArrayList<>();
	
		//only if Title & Package already exists
		for(int i = 0; i < questList.size(); i++){
			if(questList.get(i).getQuestPath().equals(quest.getQuestPath()) && questList.get(i).getPackagePath().equals(quest.getPackagePath())){
				
				questList.set(i, quest);
				profile.setProfileQuests(questList);
				
				//save Profile
				writeProfile(profile);
				
				System.out.println("Quest State changed!");
				return profile;
			}
		}

		//if the Variable is not existing in the profile
		questList.add(quest);
		profile.setProfileQuests(questList);
		
		//save Profile
		writeProfile(profile);
			
		return profile;
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
	public static Profile changeProfileImage(Profile profile, String sourcePath) throws IOException, XMLWriteException{
		String extension = sourcePath.substring(sourcePath.lastIndexOf('.'), sourcePath.length());
		
		
		//TODO add all file extensions
		if(sourcePath.endsWith(".bmp") ||
				sourcePath.endsWith(".jpg") ||
				sourcePath.endsWith(".png") ||
				sourcePath.endsWith(".jpeg") ||
				sourcePath.endsWith(".wbmp") ||
				sourcePath.endsWith(".gif") ){
			
				String destPath = profile.getInitPath() + Profile.sep + Profile.FILE_PROFILEIMAGE + extension;
				
				File source  = new File(sourcePath);
				File dest = new File(destPath);
				
				//Copying the File
				LoadStatics.copyFileUsingStream(source, dest);
				profile.setProfileimage(dest.getName());
				
		}else
			throw new IOException();
		
		//Returning the new profile with the correct imagePath
		return profile;
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
	 * For adding XP
	 * @param xp
	 */
	public void addXp(int xp){
		this.xp = this.xp + xp;
	}
	
	/**
	 * For subtracting xp
	 * @param sub
	 */
	public void subXp(int xp){
		this.xp = this.xp - xp;
		
		if(this.xp <= 0)
			this.xp = 0;
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
	 * <b>Calculating and returns the Level</b>
	 * TODO better Level Calculation
	 * @return the Level
	 */
	public int getLevel(){
		return (int) Math.sqrt(xp); //square of 2	
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
	 * @return the activeProfile
	 */
	public static Profile getActiveProfile() {
		return activeProfile;
	}

	/**
	 * @param activeProfile the activeProfile to set
	 */
	public static void setActiveProfile(Profile activeProfile) {
		Profile.activeProfile = activeProfile;
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

	
	
	
}
