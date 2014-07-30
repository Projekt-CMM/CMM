package at.jku.ssw.cmm.filemanagenment;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.quests.datastructs.Quest;
import at.jku.ssw.cmm.quests.datastructs.Settings;

public class QuestContentHandler  {
	
	
	
	Quest quest;
	NodeList nodeList;
	private String sep = System.getProperty("file.separator");
	private NodeHandler handler = new NodeHandler();
	public Quest Parse(String initPath, String file) throws ParserConfigurationException, SAXException, IOException{	
	
		
		quest = new Quest();
		quest.setPath(file);	
		
		 DOMParser parser = new DOMParser();
		 parser.parse(initPath + sep + file);
		 Document doc = parser.getDocument();
		 
		 NodeList root = doc.getChildNodes();		 
		 List<Node> profile = handler.getNodeList(Quest.XML_QUEST,root);
		  
		 for(int x = 0; x < profile.size();x++){	
		 			Node currentProfile = profile.get(x);
		 			NodeList nodes = currentProfile.getChildNodes();
		 			
		 			//Title
		 			try{
		 				quest.setTitle(handler.getNodeValue(Quest.XML_TITLE,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("QuestHandler: Title not found");
						
					}
		 			
		 			//Description
		 			try{
		 				quest.setDescription(handler.getNodeValue(Quest.XML_DESCRIPTION,nodes).get(0));
		 			}catch(IndexOutOfBoundsException e){
		 				if(Settings.debug)System.out.println("QuestHandler: Description not found");
					}
		 			
		 			//Image
		 			try{
		 				quest.setImage(handler.getNodeValue(Quest.XML_IMAGE,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("QuestHandler: Image not found");
					}
		 			
		 			//CmmProgramm
		 			try{
		 				quest.setCmmProgramm(handler.getNodeValue(Quest.XML_CMMPROGRAMM,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("QuestHandler: CmmProgramm not found");
					}
		 			
		 			//Pattern
		 			try{
		 				quest.setPattern(handler.getNodeValue(Quest.XML_PATTERN,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("QuestHandler: Pattern not found");
					}
		 			
		 			//Reward
		 			try{
		 				quest.setRewardPath(handler.getNodeValue(Quest.XML_REWARD,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("QuestHandler: Reward not found");
					}
		 			
		 			//Level
		 			try{
		 				quest.setLevel(Integer.parseInt(handler.getNodeValue(Quest.XML_LEVEL,nodes).get(0)));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("QuestHandler: Level not found");
					}
		 			
		 			//NextQuest
		 			try{
		 				quest.setNextQuest(handler.getNodeValue(Quest.XML_NEXTQUEST,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("QuestHandler: Nextquest not found");
					}
		 			
		 			//Solution
		 			try{
		 				quest.setSolution(handler.getNodeValue(Quest.XML_SOLUTION,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("QuestHandler: No sollution set");
					}
		 			//Status
		 			try{
		 				quest.setStatus(handler.getNodeValue(Quest.XML_STATUS,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("QuestHandler: No status set");
					}
		 			
		 			//Date
		 			try{
		 				quest.setDate(handler.getNodeValue(Quest.XML_DATE,nodes).get(0));
					}catch(IndexOutOfBoundsException e){
						if(Settings.debug)System.out.println("QuestHandler: No date set");
					}
		 }
		 
		 Write(quest, initPath + sep + file);
		 
		return quest;
	}	
	
	
	public void Write(Quest quest, String filePath){
		 try {
			 
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				Document doc = docBuilder.newDocument();
				Element questvar = doc.createElement(Quest.XML_QUEST);
				doc.appendChild(questvar);
		 
				// setting the Title
				Element title = doc.createElement(Quest.XML_TITLE);
				if(quest.getTitle() != null)
					title.appendChild(doc.createTextNode(quest.getTitle()));
				else
					title.appendChild(doc.createTextNode("not set"));
				questvar.appendChild(title);

				// setting the Description
				Element description = doc.createElement(Quest.XML_DESCRIPTION);
				if(quest.getDescription() != null)
					description.appendChild(doc.createTextNode(quest.getDescription()));
				else
					description.appendChild(doc.createTextNode("not set"));
				questvar.appendChild(description);
				
				// setting the Image
				Element image = doc.createElement(Quest.XML_IMAGE);
				if(quest.getImage() != null)
					image.appendChild(doc.createTextNode(quest.getImage()));
				else
					image.appendChild(doc.createTextNode("not set"));
				questvar.appendChild(image);
				
				// setting the CmmProgramm
				Element cmmProgramm = doc.createElement(Quest.XML_CMMPROGRAMM);
				if(quest.getCmmProgramm() != null)
					cmmProgramm.appendChild(doc.createTextNode(quest.getCmmProgramm()));
				else
					cmmProgramm.appendChild(doc.createTextNode("not set"));
				questvar.appendChild(cmmProgramm);
				
				// setting the Pattern
				Element pattern = doc.createElement(Quest.XML_PATTERN);
				if(quest.getPattern() != null)
					pattern.appendChild(doc.createTextNode(quest.getPattern()));
				else
					pattern.appendChild(doc.createTextNode("not set"));
				questvar.appendChild(pattern);
		 
				// setting the Reward
				Element reward = doc.createElement(Quest.XML_REWARD);
				if(quest.getRewardPath() != null)
					reward.appendChild(doc.createTextNode(quest.getRewardPath()));
				else
					reward.appendChild(doc.createTextNode("not set"));
				questvar.appendChild(reward);
				
				// setting the Level
				Element level = doc.createElement(Quest.XML_LEVEL);
				level.appendChild(doc.createTextNode(quest.getLevel() + ""));
				questvar.appendChild(level);
				
				// setting the nextQuest
				Element nextquest = doc.createElement(Quest.XML_NEXTQUEST);
				if(quest.getNextQuest() != null)
					nextquest.appendChild(doc.createTextNode(quest.getNextQuest()));
				else
					nextquest.appendChild(doc.createTextNode("not set"));
				questvar.appendChild(nextquest);
				
				// setting the nextQuest
				Element solution = doc.createElement(Quest.XML_SOLUTION);
				if(quest.getSolution() != null){
					solution.appendChild(doc.createTextNode(quest.getSolution()));
					questvar.appendChild(solution);
				}
				
				// setting the Status
				Element status = doc.createElement(Quest.XML_STATUS);
				if(quest.getStatus() != null){
					status.appendChild(doc.createTextNode(quest.getStatus()));
					questvar.appendChild(status);
				}
				
				// setting the Status
				Element date = doc.createElement(Quest.XML_DATE);
				if(quest.getDate() != null){
					date.appendChild(doc.createTextNode(quest.getDate()));
					questvar.appendChild(date);
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
