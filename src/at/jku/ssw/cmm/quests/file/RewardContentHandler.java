package at.jku.ssw.cmm.quests.file;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import at.jku.ssw.cmm.quests.datastructs.Reward;

public class RewardContentHandler implements ContentHandler {

	  private String currentValue;
	  private Reward reward;
	  
	  
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	    currentValue = new String(ch, start, length);
		
	}
	
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		
		if(localName.equals("reward"))
			reward = new Reward();
		
		
		
	}
	
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		//Title
		if(localName.equals("title"))
			reward.setTitle(currentValue);
		
		//Reward Type
		if(localName.equals("type"))
			reward.setType(currentValue);
		
		//Reward Type
		if(localName.equals("xp"))
			reward.setXp(Integer.parseInt(currentValue));
		
		//CMM - Program - Description
		if(localName.equals("description"))
			reward.setDescription(currentValue);
		
		//CMM - Program - LSG
		if(localName.equals("image"))
			reward.setImage(currentValue);
	}
	

	
	
	/**
	 * @return the reward
	 */
	public Reward getReward() {
		return reward;
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
