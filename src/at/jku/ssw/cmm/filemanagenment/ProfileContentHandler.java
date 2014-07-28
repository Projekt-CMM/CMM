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

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

import org.w3c.dom.Element;

import at.jku.ssw.cmm.profile.Profile;

public class ProfileContentHandler {
	
	private Profile profilevar;
	private String sep = System.getProperty("file.separator");
	private ArrayList<String> finished = new ArrayList<>();
	private NodeHandler handler = new NodeHandler();

	/**
	 * @param file2 
	 * @param
	 * Username to Search: <b>String nick</b>; File Name: <b>String file</b>
	 * 
	 * @return
	 * Parsed Profile, Datatype <b>Profile</b>
	 */
	
	public Profile Parse(String initPath, String profilePath, String file) throws ParserConfigurationException, SAXException, IOException{	
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
		 				System.out.println("No Name set - setting name to not set");
		 				profilevar.setNick("not set");
		 			}
		 			
		 			try{
		 				profilevar.setXp(Integer.parseInt(handler.getNodeValue(Profile.XML_XP,nodes).get(0)));
		 			}catch(IndexOutOfBoundsException e){
		 				System.out.println("Curently no XP set - setting XP to 0");
		 				profilevar.setXp(0);
		 			}
		 			try{
		 				for(int i = 0; i< handler.getNodeValue(Profile.XML_FINISHED,nodes).size();i++)
		 				finished.add(handler.getNodeValue("finished",nodes).get(i));
					}catch(IndexOutOfBoundsException e){
		 				System.out.println("No finished Quests");
		 			}
		 			
		 			try{
		 				profilevar.setSelectedImage(handler.getNodeValue(Profile.XML_SELECTEDIMAGE,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
		 				System.out.println("No Current Profile Image - setting Profile Image to none");
		 				profilevar.setSelectedImage("none");
		 			}
		 			
		 }

		profilevar.setFinishedQuests(finished);
		return profilevar;
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
		 
				// firstname elements
				Element firstname = doc.createElement("xp");
				firstname.appendChild(doc.createTextNode(profile.getXp()+""));
				profilevar.appendChild(firstname);
		 
				for(int i = 0; i < profile.getFinishedQuestNames().size();i++){
					// Finished elements
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
		 
				System.out.println("File saved! in: " + filePath);
		 
			  } catch (ParserConfigurationException  | TransformerException pce) {
				pce.printStackTrace();
			  }
			
	}
	

	
}
