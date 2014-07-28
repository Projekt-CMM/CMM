package at.jku.ssw.cmm.filemanagenment;


import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import java.io.IOException;
import java.util.List;

import at.jku.ssw.cmm.quests.datastructs.Quest;

public class QuestContentHandler  {
	
	
	
	Quest quest;
	NodeList nodeList;
	private NodeHandler handler = new NodeHandler();
	
	public Quest Parse(String initPath, String file) throws ParserConfigurationException, SAXException, IOException{	
		
		quest = new Quest();
		quest.setPath(file);	
		
		 DOMParser parser = new DOMParser();
		 parser.parse(initPath + System.getProperty("file.separator") + file);
		 Document doc = parser.getDocument();
		 
		 NodeList root = doc.getChildNodes();		 
		 List<Node> profile = handler.getNodeList(Quest.XML_QUEST,root);
		  
		 for(int x = 0; x < profile.size();x++){	
		 			Node currentProfile = profile.get(x);
		 			NodeList nodes = currentProfile.getChildNodes();
		 			
		 			try{
		 				quest.setTitle(handler.getNodeValue(Quest.XML_TITLE,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: Title not found");
					}
		 			
		 			try{
		 				quest.setDescription(handler.getNodeValue(Quest.XML_DESCRIPTION,nodes).get(0));
		 			}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: Description not found");
					}
		 			try{
		 				quest.setImage(handler.getNodeValue(Quest.XML_IMAGE,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: Image not found");
					}
		 			
		 			try{
		 				quest.setCmmProgramm(handler.getNodeValue(Quest.XML_CMMPROGRAMM,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: CmmProgramm not found");
					}
		 			try{
		 				quest.setPattern(handler.getNodeValue(Quest.XML_PATTERN,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: Pattern not found");
					}
		 			try{
		 				quest.setRewardPath(handler.getNodeValue(Quest.XML_REWARD,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: Reward not found");
					}
		 			try{
		 				quest.setLevel(Integer.parseInt(handler.getNodeValue(Quest.XML_LEVEL,nodes).get(0)));
					}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: Level not found");
					}
		 			try{
		 				quest.setNextQuest(handler.getNodeValue(Quest.XML_NEXTQUEST,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						System.out.println("QuestHandler: Nextquest not found");
					}
		 			
		 }
		return quest;
	}	
	
}
