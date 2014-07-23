package at.jku.ssw.cmm.quests.file;


import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import at.jku.ssw.cmm.quests.datastructs.Quest;

public class QuestContentHandler implements ContentHandler {

	  private String currentValue;
	  private Quest quest;
	  
	  
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	    currentValue = new String(ch, start, length);
		
	}
	
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		
		if(localName.equals("quest"))
		    quest = new Quest();
		
	}
	
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		//Title
		if(localName.equals("title"))
			quest.setTitle(currentValue);
		
		//CMM - Program - Description
		if(localName.equals("description"))
			quest.setDescription(currentValue);
		
		//CMM - Program - LSG
		if(localName.equals("cmmprogramm"))
			quest.setCmmProgramm(currentValue);
		
		//CMM - Program - Pattern
		if(localName.equals("plattern"))
			quest.setPattern(currentValue);
		
		//Reward when finished
		if(localName.equals("reward"))
			quest.setReward(currentValue);
		
		//Difficulty Level
		if(localName.equals("level"))
			quest.setLevel(Integer.parseInt(currentValue));
		
		//Next Quest Name
		if(localName.equals("nextquest"))
			quest.setNextQuest(currentValue);
	}
	

	/**
	 * @return the quest
	 */
	public Quest getQuest() {
		return quest;
	}
	
	@Override
	public void endDocument() throws SAXException {
		// Method not used!
		
	}
	
	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// Method not used!
		
	}
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// Method not used!
		
	}
	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		// Method not used!
		
	}
	@Override
	public void setDocumentLocator(Locator locator) {
		// Method not used!
		
	}
	@Override
	public void skippedEntity(String name) throws SAXException {
		// Method not used!
		
	}
	@Override
	public void startDocument() throws SAXException {
		// Method not used!
		
	}
	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// Method not used!
		
	}
	  
}
