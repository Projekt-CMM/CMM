package at.jku.ssw.cmm.filemanagenment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

import org.w3c.dom.Element;

import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.quests.datastructs.Settings;

public class ProfileContentHandler {
	
	private Profile profilevar;
	private String sep = System.getProperty("file.separator");
	private ArrayList<String> finished = new ArrayList<>();
	private	ArrayList<String> selectable = new ArrayList<>();
	private NodeHandler handler = new NodeHandler();

	/**
	 * @param file2 
	 * @param
	 * Username to Search: <b>String nick</b>; File Name: <b>String file</b>
	 * 
	 * @return
	 * Parsed Profile, Datatype <b>Profile</b>
	 * @throws IOException 
	 * @throws SAXException 
	 */
	
	public Profile Parse(String initPath, String profilePath, String file) throws SAXException, IOException {	
		try{
		profilevar = new Profile();
		profilevar.setPath(file);	

		 DOMParser parser = new DOMParser();
		 parser.parse(initPath + sep + profilePath + sep + file);
		 
		 Document doc = parser.getDocument();
		 NodeList root = doc.getChildNodes();		

		 List<Node> profile = handler.getNodeList(Profile.XML_PROFILE,root);
 
		 for(int x = 0; x < profile.size();x++){	
		 			Node currentProfile = profile.get(x);
		 			NodeList nodes = currentProfile.getChildNodes();
		 			
		 			try{
		 				profilevar.setNick(handler.getNodeValue(Profile.XML_NICK,nodes).get(0));
		 			}catch(IndexOutOfBoundsException e){
		 				if(Settings.debug)System.out.println("No Name set - setting name to not set");
		 				profilevar.setNick("not set");
		 			}
		 			
		 			try{
		 				profilevar.setXp(Integer.parseInt(handler.getNodeValue(Profile.XML_XP,nodes).get(0)));
		 			}catch(IndexOutOfBoundsException e){
		 				if(Settings.debug)System.out.println("Curently no XP set - setting XP to 0");
		 				profilevar.setXp(0);
		 			}
		 			try{
		 				profilevar.setOpenedQuest(handler.getNodeValue(Profile.XML_OPENEDQUEST,nodes).get(0));
		 			}catch(IndexOutOfBoundsException e){
		 				if(Settings.debug)System.out.println("No Name set - setting name to not set");
		 				profilevar.setOpenedQuest("no opened quest");
		 			}
		 			try{
						for(int i = 0; i< handler.getNodeValue(Profile.XML_SELECTABLE,nodes).size();i++)
		 				selectable.add(handler.getNodeValue(Profile.XML_SELECTABLE,nodes).get(i));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("No selectable Quests");
		 			}
		 			try{
		 				for(int i = 0; i< handler.getNodeValue(Profile.XML_FINISHED,nodes).size();i++)
		 				finished.add(handler.getNodeValue(Profile.XML_FINISHED,nodes).get(i));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("No finished Quests");
		 			}
		 			
		 			try{
		 				profilevar.setSelectedImage(handler.getNodeValue(Profile.XML_SELECTEDIMAGE,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("No Current Profile Image - setting Profile Image to none");
		 				profilevar.setSelectedImage("none");
		 			}
		 			
		 }
		
		profilevar.setSelectable(selectable);
		profilevar.setFinishedQuests(finished);
		return profilevar;
		
		//Creates a new Profile if the file is not excising
		}catch(SAXParseException e){
			Write(profilevar, initPath + sep + profilePath + sep + file);
			Parse(initPath,  profilePath,  file);
			return profilevar;
		}	
}
	
	/**
	 * 
	 * @param profile
	 * @param filePath
	 */
	
	public void Write(Profile profile, String filePath){
		 try {
			 
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				Document doc = docBuilder.newDocument();
				Element profilevar = doc.createElement(Profile.XML_PROFILE);
				doc.appendChild(profilevar);
		 
				// setting the Username
				Element nick = doc.createElement(Profile.XML_NICK);
				if(profile.getNick() != null)
					nick.appendChild(doc.createTextNode(profile.getNick()));
				else
					nick.appendChild(doc.createTextNode("not set"));
				profilevar.appendChild(nick);
				
				// setting the Current Profile image
				Element selectedimage = doc.createElement(Profile.XML_SELECTEDIMAGE);
				if(profile.getSelectedImage() != null)
					selectedimage.appendChild(doc.createTextNode(profile.getSelectedImage()));
				else
					selectedimage.appendChild(doc.createTextNode("not set"));
				profilevar.appendChild(selectedimage);
		 
				// XP elements
				Element xp = doc.createElement(Profile.XML_XP);
				xp.appendChild(doc.createTextNode(profile.getXp()+""));
				profilevar.appendChild(xp);
				
				// Opened-Quest
				Element openedQuest = doc.createElement(Profile.XML_OPENEDQUEST);
				if(profile.getOpenedQuest() != null)
					openedQuest.appendChild(doc.createTextNode(profile.getOpenedQuest()));
				else
					openedQuest.appendChild(doc.createTextNode("no opened Quest"));
				
				profilevar.appendChild(openedQuest);				
				
				// Selectable quest elements				
				if(profile.getSelectableQuestNames() != null)
				for(int i = 0; i < profile.getSelectableQuestNames().size();i++){
					Element selectable = doc.createElement(Profile.XML_SELECTABLE);
					selectable.appendChild(doc.createTextNode(profile.getSelectableQuestNames().get(i)));
					profilevar.appendChild(selectable);
				}	
				
				// Finished elements	
				if(profile.getSelectableQuestNames() != null)
				for(int i = 0; i < profile.getFinishedQuestNames().size();i++){
					Element finished = doc.createElement(Profile.XML_FINISHED);
					finished.appendChild(doc.createTextNode(profile.getFinishedQuestNames().get(i)));
					profilevar.appendChild(finished);
				}
		 
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(filePath));
				
				//For formatting the Results
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				//For saving the Result
				transformer.transform(source, result);
		 
				if(Settings.debug)System.out.println("File saved! in: " + filePath);
		 
			  } catch (ParserConfigurationException  | TransformerException pce) {
				pce.printStackTrace();
			  }
			
	}
	

	
}
