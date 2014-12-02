package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
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

import at.jku.ssw.cmm.gettext.Language;
import at.jku.ssw.cmm.profile.XMLWriteException;

/**
 * Contains configuration data for the main GUI. The main GUI has a reference to
 * an object of this class. The configuration is saved in the "settings.xml" file
 * in the program's working directory.
 * 
 * @author fabian
 *
 */
public class GUImainSettings {

	/**
	 * The name of the file where the config data und user's settings are saved
	 */
	private static final String CONFIG_FILE = "settings.xml";

	/**
	 * The XML tag name for the root object in the settings file
	 */
	private static final String XML_SETTINGS = "settings";
	
	/**
	 * The XML tag name of the last used language translation
	 */
	private static final String XML_LANGUAGE = "language";
	
	/**
	 * The XML tage name for the list of recently opened files or profiles
	 */
	private static final String XML_LASTFILE = "lastfile";
	
	/**
	 * The maximum number of recently opened files which are saved in the settings
	 */
	private static final int MAX_LASTFILES = 10;

	/**
	 * Contains configuration data for the main GUI
	 * 
	 * @param advancedGUI TRUE if profile options shall be shown in the GUI,
	 * 						FALSE if C Compact shall launch the IDE only
	 */
	public GUImainSettings( boolean advancedGUI ) {
		this.advancedGUI = advancedGUI;
		this.readConfigXML();
	}
	
	/**
	 * The recent files or profiles, latest at the begin
	 */
	private List<String> lastFiles;
	
	/**
	 * The current *.cmm file. <br>
	 * This is also used for the current file if list of last files
	 * is used for recent profiles.
	 */
	private String currentFile;
	
	/**
	 * The language currently chosen
	 */
	private String lastLanguage;
	
	/**
	 * If true, GUI options for quest and profile functions are shown. <br>
	 * If false, quest/profile GUI is hidden.
	 */
	private final boolean advancedGUI;

	/**
	 * Set the path of the current cmm file.
	 * 
	 * @param p
	 *            The new path of the cmm file. "null" means that
	 *            the file has not yet been saved.
	 */
	public void setCMMFilePath(String p) {
		if( p == null )
			this.currentFile = null;
		else{
			if( !this.advancedGUI )
				this.lastFiles.add(0, p);
			this.currentFile = p;
		}
	}

	/**
	 * @return The path of the current cmm file.
	 */
	public String getCMMFilePath() {
		if( !this.hasCMMFilePath() )
			return null;
					
		return this.currentFile;
	}

	/**
	 * @return TRUE if the config contains a working path for the current cmm
	 *         file <br>
	 *         FALSE if the current cmm file has not yet been saved
	 */
	public boolean hasCMMFilePath() {
		if (this.currentFile != null)
			return true;
		return false;
	}
	
	/**
	 * @return The language code of the currently selected translation,
	 * 			for example "en" -> English, "de" -> German
	 */
	public String getLanguage(){
		return this.lastLanguage;
	}
	
	/**
	 * Changes the currently selected language.<br>
	 * <b> Note: </b> This method does not automatically load the new language;
	 * it just saves the new language code to the settings.
	 * 
	 * @param language The language code of the new translation,
	 * 			for example "en" -> English, "de" -> German
	 */
	public void setLanguage( String language ){
		this.lastLanguage = language;
	}
	
	public List<String> getRecentFiles(){
		return this.lastFiles;
	}
	
	/**
	 * @return TRUE if profile options shall be shown in the GUI,
	 * 			FALSE if C Compact shall launch the IDE only
	 */
	public boolean hasAdvancedGUI() {
		return this.advancedGUI;
	}

	/**
	 * Reads the settings from the "settings.xml" file
	 * This should only be done once in the constructor.
	 */
	public void readConfigXML() {
		
		this.lastFiles = new ArrayList<>();

		try{
			//Load settings.xml file
			File fXmlFile = new File(CONFIG_FILE);
			
			//Create document builder to parse XML file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = null;
			dBuilder = dbFactory.newDocumentBuilder();
			
			//Parse XML document
			Document doc = null;
			doc = dBuilder.parse(fXmlFile);
	
			//This is not necessary but avoids errors
			doc.getDocumentElement().normalize();
	
			//Find the important information in the settings
			this.findXMLsettings(doc.getDocumentElement());
		}
		//Something went wrong
		catch( SAXException | IOException | ParserConfigurationException e ){
			
			//Show error message with information about the error
			JOptionPane.showMessageDialog(new JFrame(), _("The file") + " \"settings.xml\" " +
					_("has not been found") + "\n" + _("The program returned") + ": " + e.getMessage(),
					_("Configuration file not found"), JOptionPane.ERROR_MESSAGE);
			
			//Initialize default settings
			this.currentFile = null;
			this.lastLanguage = Language.DEFAULT_LANGUAGE;
		}
		
		//Initialize the current *.cmm file
		if( this.lastFiles.size() > 0 && !this.advancedGUI )
			//Take first file from recent files as latest file
			this.currentFile = this.lastFiles.get(0);
		else
			//No recent file
			this.currentFile = null;
	}

	/**
	 * Searches all nodes of the given XML document for useful information about
	 * the user's settings. Works recursively.
	 * 
	 * @param node The root ode of the parsed settings XML document
	 */
	private void findXMLsettings(Node node) {

		//Contains language information?
		if (node.getNodeName().equals(XML_LANGUAGE))
			this.lastLanguage = node.getTextContent();

		//Contains information about recent files or profiles?
		else if (node.getNodeName().equals(XML_LASTFILE))
			lastFiles.add(node.getTextContent());

		//Iterate through child nodes
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				// calls this method for all the children which are of type Element
				findXMLsettings(currentNode);
			}
		}
	}

	/**
	 * Writes the user's settings to the "settings.xml" file
	 */
	public void writeXMLsettings() {
		
		//Declare and create document factory
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder icBuilder;

		try {
			//Initialize document builder
			icBuilder = icFactory.newDocumentBuilder();
			Document doc = icBuilder.newDocument();
			
			//Create root element of XML document
			Element mainRootElement = doc.createElementNS(CONFIG_FILE, XML_SETTINGS);
			doc.appendChild(mainRootElement);

			//Add language information element
			if( this.lastLanguage != null )
				mainRootElement.appendChild(writeNode(doc, mainRootElement, XML_LANGUAGE, this.lastLanguage));
			
			//Add infromation about recent files / profiles
			if( this.lastFiles != null && !this.lastFiles.isEmpty() ){
				
				//Only add a certain number of recent files
				for( int i = 0; i < this.lastFiles.size() && i < MAX_LASTFILES; i++ ){
					mainRootElement.appendChild(writeNode(doc, mainRootElement, XML_LASTFILE, this.lastFiles.get(i)));
				}
			}
			
			//Transform XML document to DOM source
            DOMSource source = new DOMSource(doc);
            
            //Initialize transformer
            Transformer transformer;
			transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            //Writing into file
            if(source != null){
            	StreamResult result = new StreamResult(new File(CONFIG_FILE));
            	transformer.transform(source, result);
            }

		}
		//Something went wrong
		catch (ParserConfigurationException
				| TransformerFactoryConfigurationError
				| XMLWriteException
				| DOMException
				| TransformerException e) {
			
			//Show error message with information about the error
			JOptionPane.showMessageDialog(new JFrame(),
					_("C Compact was not able to save your personal settings") + "\n" +
					_("The program returned") + ": " + e.getMessage(),
					_("Unable to save your settings"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Writes a XML data node to the given document
	 * 
	 * @param doc The document object
	 * @param element The root element of the XML tree
	 * @param name The name of the new element
	 * @param value The value (data) of the new element
	 * @return The new node to be appended to another node
	 * @throws XMLWriteException
	 */
	private Node writeNode(Document doc, Element element, String name, String value) throws XMLWriteException{
        if(value == null)
        	throw new XMLWriteException();
    	
	    	Element node = doc.createElement(name);
	        node.appendChild(doc.createTextNode(value));
	        return node;
    }
}
