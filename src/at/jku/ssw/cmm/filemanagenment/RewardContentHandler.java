package at.jku.ssw.cmm.filemanagenment;


import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import java.io.IOException;
import java.util.List;

import at.jku.ssw.cmm.quests.datastructs.Reward;

public class RewardContentHandler  {
	
	
	
	Reward reward;
	NodeList nodeList;
	private NodeHandler handler = new NodeHandler();
	
	public Reward Parse(String initPath, String file) throws ParserConfigurationException, SAXException, IOException{	
		
		reward = new Reward();
		reward.setPath(file);
		
		 DOMParser parser = new DOMParser();
		 parser.parse(initPath + System.getProperty("file.separator") + file);
		 Document doc = parser.getDocument();
		 
		 NodeList root = doc.getChildNodes();		 
		 List<Node> profile = handler.getNodeList(Reward.XML_REWARD,root);
		  
		 for(int x = 0; x < profile.size();x++){	
		 			Node currentProfile = profile.get(x);
		 			NodeList nodes = currentProfile.getChildNodes();
		 			
		 			try{
		 				
		 				reward.setTitle(handler.getNodeValue(Reward.XML_TITLE,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: Title not found");
					}
		 			
		 			try{
		 				reward.setDescription(handler.getNodeValue(Reward.XML_DESCRIPTION,nodes).get(0));
		 			}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: Description not found");
					}
		 			try{
		 				reward.setImage(handler.getNodeValue(Reward.XML_IMAGE,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: Image not found");
					}
		 			
		 			try{
		 				reward.setType(handler.getNodeValue(Reward.XML_TYLE,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: Type not found");
					}
		 			try{
		 				reward.setXp(Integer.parseInt(handler.getNodeValue(Reward.XML_XP,nodes).get(0)));
					}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: XP not found");
					}
		 			
		 }
		return reward;
	}	
	
}
