package at.jku.ssw.cmm.filemanagenment;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import at.jku.ssw.cmm.quests.datastructs.Settings;

public class SettingsContentHandler {
	
	private Settings settingsvar;
	private NodeHandler handler = new NodeHandler();
	
	public Settings Parse(String file) throws SAXException, IOException{	
		settingsvar = new Settings();
		
		
		 DOMParser parser = new DOMParser();
		 parser.parse(file);
		 Document doc = parser.getDocument();
		 
		 NodeList root = doc.getChildNodes();		 
		 Node settings = handler.getNode("settings",root);
		 
		 
		 //For getting the Main <settings>
		 NodeList nodes = settings.getChildNodes();
			try{
 				Settings.setDebug(Boolean.parseBoolean(handler.getNodeValue("debug",nodes).get(0)));
 			}catch(IndexOutOfBoundsException e){
 				System.out.println("No Spell Checking min Level set");
 			}
		 
			
		//For getting the Settings in <rewardsettings>	
		Node rewardsettings = handler.getNodeList("rewardsettings",settings.getChildNodes()).get(0);
		nodes = rewardsettings.getChildNodes();
		 			
		 			try{
		 				settingsvar.setBackground(Integer.parseInt(handler.getNodeValue("background",nodes).get(0)));
		 			}catch(IndexOutOfBoundsException e){
		 				System.out.println("No Background mi Level set");
		 			}
		 			try{
		 				settingsvar.setSounds(Integer.parseInt(handler.getNodeValue("sounds",nodes).get(0)));
		 			}catch(IndexOutOfBoundsException e){
		 				System.out.println("No Sound min Level set.");
		 			}
		 			try{
		 				settingsvar.setColor_picker(Integer.parseInt(handler.getNodeValue("color_picker",nodes).get(0)));
		 			}catch(IndexOutOfBoundsException e){
		 				System.out.println("No Color Picker min Level set");
		 			}
		 			try{
		 				settingsvar.setAuto_complete(Integer.parseInt(handler.getNodeValue("auto_complete",nodes).get(0)));
		 			}catch(IndexOutOfBoundsException e){
		 				System.out.println("No Auto Complete min Level set");
		 			}
		 			try{
		 				settingsvar.setBackground(Integer.parseInt(handler.getNodeValue("spell_checking",nodes).get(0)));
		 			}catch(IndexOutOfBoundsException e){
		 				System.out.println("No Spell Checking min Level set");
		 			}
		return settingsvar;
	}
	
	
	
	//TODO Creating file if not existing
	public void Write(String file){
		
	}
	

	
}
