/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
package at.jku.ssw.cmm.gui.properties;

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

import at.jku.ssw.cmm.gui.ProfilePanel2;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.XMLWriteException;
import at.jku.ssw.cmm.quest.GUITestPanel;

/**
 * Contains configuration data for the main GUI. The main GUI has a reference to
 * an object of this class. The configuration is saved in the "settings.xml"
 * file in the program's working directory.
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

	private static final String XML_PROPERTIES = "properties";

	/**
	 * The XML tag name of the last used language translation
	 */
	private static final String XML_LANGUAGE = "language";

	/**
	 * The XML tag name for the list of recently opened <b>files</b>
	 */
	private static final String XML_LASTFILE = "lastfile";

	/**
	 * The XML tag name for the list of recently opened <b>profiles</b>
	 */
	private static final String XML_PROFILE = "profile";

	private static final String XML_CODESIZE = "codesize";
	private static final String XML_TEXTSIZE = "textsize";
	private static final String XML_VARSIZE = "varsize";
	private static final String XML_VAROFFSET = "varoffset";
	private static final String XML_DESCSIZE = "descsize";

	/**
	 * The maximum number of recently opened files which are saved in the
	 * settings
	 */
	private static final int MAX_LASTFILES = 10;

	/**
	 * Contains configuration data for the main GUI
	 */
	public GUImainSettings() {
		this.readConfigXML();
	}
	
	public GUImainSettings(Profile p) {
		this.currentProfile = p;
		this.readConfigXML();
	}

	/**
	 * The recent files or profiles, latest at the begin
	 */
	private List<String> lastFiles;

	/**
	 * The current *.cmm file. <br>
	 * This is also used for the current file if list of last files is used for
	 * recent profiles.
	 */
	private String currentFile;

	private List<String> lastProfiles;
	private Profile currentProfile;

	/**
	 * The language currently chosen
	 */
	private String lastLanguage;

	private int codeSize;
	private int textSize;
	private int varSize;
	private int varOffset;
	private int descSize;
	
	/**
	 * Reference to the Profile Panel
	 */
	private ProfilePanel2 profilePanel;
	
	/**
	 * Set the path of the current cmm file.
	 * 
	 * @param p
	 *            The new path of the cmm file. "null" means that the file has
	 *            not yet been saved.
	 */
	public void setCMMFilePath(String p) {
		if (p == null) {
			this.currentFile = null;
			this.lastFiles.add(0, _("Unnamed"));
			this.currentFile = _("Unnamed");
		} else {
			this.lastFiles.remove(p);
			this.lastFiles.remove(_("Unnamed"));
			this.lastFiles.add(0, p);
			this.currentFile = p;
		}
	}

	/**
	 * 
	 * @return Name of the current file without path, eg "file2.cmm"
	 */
	public String getCMMFile() {

		if (this.currentFile == null)
			return _("Unnamed");

		File file = new File(this.currentFile);

		return file.getName();
	}

	/**
	 * @return The complete path to the directory where the currently edited
	 *         *.cmm file is saved
	 */
	public String getWorkingDirectory() {
		if (this.currentFile == null)
			return null;

		File f = new File(this.currentFile);
		if (f.getParentFile() != null)
			return f.getParentFile().getAbsolutePath();
		return null;
	}

	/**
	 * @return The path of the current cmm file.
	 */
	public String getCMMFilePath() {
		if (!this.hasCMMFilePath())
			return null;

		return this.currentFile;
	}

	/**
	 * @return TRUE if the config contains a working path for the current cmm
	 *         file <br>
	 *         FALSE if the current cmm file has not yet been saved
	 */
	public boolean hasCMMFilePath() {
		if (this.currentFile != null && !this.currentFile.equals(_("Unnamed")))
			return true;
		return false;
	}

	/**
	 * @return The language code of the currently selected translation, for
	 *         example "en" -> English, "de" -> German
	 */
	public String getLanguage() {
		return this.lastLanguage;
	}

	/**
	 * Changes the currently selected language.<br>
	 * <b> Note: </b> This method does not automatically load the new language;
	 * it just saves the new language code to the settings.
	 * 
	 * @param language
	 *            The language code of the new translation, for example "en" ->
	 *            English, "de" -> German
	 */
	public void setLanguage(String language) {
		this.lastLanguage = language;
	}

	public List<String> getRecentFiles() {
		return this.lastFiles;
	}

	public void setCodeSize(int size) {
		this.codeSize = size;
	}

	public void setTextSize(int size) {
		this.textSize = size;
	}

	public void setVarSize(int size) {
		this.varSize = size;
	}

	public void setVarOffset(int offset) {
		this.varOffset = offset;
	}
	
	public void setDescSize(int size) {
		this.descSize = size;
	}
	
	public void setProfilePanel2(ProfilePanel2 profilePanel){
		this.profilePanel = profilePanel;
	}

	public int getCodeSize() {
		return this.codeSize;
	}

	public int getTextSize() {
		return this.textSize;
	}

	public int getVarSize() {
		return this.varSize;
	}

	public int getVarOffset() {
		return this.varOffset;
	}
	
	public int getDescSize() {
		return this.descSize;
	}

	/**
	 * @return TRUE if profile options shall be shown in the GUI, FALSE if C
	 *         Compact shall launch the IDE only
	 */
	public boolean hasProfile() {
		return this.currentProfile != null;
	}

	public Profile getProfile() {
		return this.currentProfile;
	}
	
	public void setProfile( Profile p ) {
		if(profilePanel != null && !profilePanel.getJProfileName().getText().isEmpty())
			p.setName(profilePanel.getJProfileName().getText());
		
		this.currentProfile = p;
		if( p != null ){
			if(this.lastProfiles.contains(p.getInitPath()))
				this.lastProfiles.remove(p.getInitPath());
			this.lastProfiles.add(0, p.getInitPath());
			System.out.println("ADDED PROFILE: " + p.getInitPath());
		}
		else
			System.err.println("WARNING: Profile is null");
	}
	
	public List<String> getRecentProfiles() {
		return lastProfiles;
	}

	/**
	 * Reads the settings from the "settings.xml" file This should only be done
	 * once in the constructor.
	 */
	public void readConfigXML() {

		this.lastFiles = new ArrayList<>();
		this.lastProfiles = new ArrayList<>();
		this.codeSize = 16;
		this.textSize = 16;
		this.varOffset = 0;
		this.varSize = 16;
		this.descSize = 0;

		try {
			// Load settings.xml file
			File fXmlFile = new File(CONFIG_FILE);

			// Create document builder to parse XML file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = null;
			dBuilder = dbFactory.newDocumentBuilder();

			// Parse XML document
			Document doc = null;
			doc = dBuilder.parse(fXmlFile);

			// This is not necessary but avoids errors
			doc.getDocumentElement().normalize();

			// Find the important information in the settings
			this.findXMLsettings(doc.getDocumentElement());
		}
		// Something went wrong, eg. settings not found
		catch (SAXException | IOException | ParserConfigurationException e) {

			// Initialize default settings
			this.currentFile = null;
			this.lastLanguage = null;
		}

		if(currentProfile == null){
			// Initialize the current *.cmm file
			if (this.lastFiles.size() > 0)
				// Take first file from recent files as latest file
				this.currentFile = this.lastFiles.get(0);
			else
				// No recent file
				this.currentFile = null;
		}

			
		
	}
	
	/**
	 * Reading the Profile and setting the last opened Quest
	 */
	public void setCurrentQuestFile(){
		if(currentProfile == null)
			return;
		
			Quest q = Profile.ReadLastQuest(currentProfile);
			if(q != null && q.getCmmFilePath() != null){
				currentProfile.setCurrentQuest(q);
				this.currentFile = q.getCmmFilePath();
			}
	}

	/**
	 * Searches all nodes of the given XML document for useful information about
	 * the user's settings. Works recursively.
	 * 
	 * @param node
	 *            The root ode of the parsed settings XML document
	 */
	private void findXMLsettings(Node node) {

		// Contains information about recent files?
		if (node.getNodeName().equals(XML_LASTFILE))
			lastFiles.add(node.getTextContent());
		// Contains information about recent profiles?
		else if (node.getNodeName().equals(XML_PROFILE))
			lastProfiles.add(node.getTextContent());
		// Contains language information?
		else if (node.getNodeName().equals(XML_LANGUAGE))
			this.lastLanguage = node.getTextContent();
		// Is font size of source code?
		else if (node.getNodeName().equals(XML_CODESIZE))
			this.codeSize = Integer.parseInt(node.getTextContent());
		// Is font size of text panels?
		else if (node.getNodeName().equals(XML_TEXTSIZE))
			this.textSize = Integer.parseInt(node.getTextContent());
		// Is font size of variable tables?
		else if (node.getNodeName().equals(XML_VARSIZE))
			this.varSize = Integer.parseInt(node.getTextContent());
		// Is font size of source code?
		else if (node.getNodeName().equals(XML_VAROFFSET))
			this.varOffset = Integer.parseInt(node.getTextContent());
		// Is font size of description documents?
		else if (node.getNodeName().equals(XML_DESCSIZE))
			this.descSize = Integer.parseInt(node.getTextContent());

		// Iterate through child nodes
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				// calls this method for all the children which are of type
				// Element
				findXMLsettings(currentNode);
			}
		}
	}

	/**
	 * Writes the user's settings to the "settings.xml" file
	 */
	public void writeXMLsettings() {

		// Declare and create document factory
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder icBuilder;

		try {
			// Initialize document builder
			icBuilder = icFactory.newDocumentBuilder();
			Document doc = icBuilder.newDocument();

			// Create root element of XML document
			Element mainRootElement = doc.createElementNS(CONFIG_FILE,
					XML_SETTINGS);
			doc.appendChild(mainRootElement);

			// Create properties sub-node
			Element properties = doc.createElement(XML_PROPERTIES);

			// Add language information element
			if (this.lastLanguage != null)
				properties.appendChild(writeNode(doc, XML_LANGUAGE,
						this.lastLanguage));

			// Add font size information
			properties.appendChild(writeNode(doc, XML_CODESIZE, ""
					+ this.codeSize));
			properties.appendChild(writeNode(doc, XML_TEXTSIZE, ""
					+ this.textSize));
			properties.appendChild(writeNode(doc, XML_VARSIZE, ""
					+ this.varSize));
			properties.appendChild(writeNode(doc, XML_VAROFFSET, ""
					+ this.varOffset));
			properties.appendChild(writeNode(doc, XML_DESCSIZE, ""
					+ this.descSize));

			// Add properties to main root element
			mainRootElement.appendChild(properties);

			// Add infromation about recent files
			if (this.lastFiles != null && !this.lastFiles.isEmpty()) {

				// Only add a certain number of recent files
				for (int i = 0; i < this.lastFiles.size() && i < MAX_LASTFILES; i++) {
					if (!this.lastFiles.get(i).equals(_("Unnamed")) && this.getProfile() == null)
						mainRootElement.appendChild(writeNode(doc, XML_LASTFILE, this.lastFiles.get(i)));
				}
			}
			
			//Adding information about the last quest opened in the Profile
			if(currentProfile != null && currentProfile.getCurrentQuest() != null){
				Profile.UpdateOpen(currentProfile, currentProfile.getCurrentQuest());
			}

			// Add infromation about recent profiles
			if (this.lastProfiles != null && !this.lastProfiles.isEmpty()) {

				// Only add a certain number of recent profiles
				for (int i = 0; i < this.lastProfiles.size() && i < MAX_LASTFILES; i++) {
					System.out.println("Adding: " + this.lastProfiles.get(i));
					mainRootElement.appendChild(writeNode(doc, XML_PROFILE, this.lastProfiles.get(i)));
				}
			}

			// Transform XML document to DOM source
			DOMSource source = new DOMSource(doc);

			// Initialize transformer
			Transformer transformer;
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			// Writing into file
			if (source != null) {
				StreamResult result = new StreamResult(new File(CONFIG_FILE));
				transformer.transform(source, result);
			}

		}
		// Something went wrong
		catch (ParserConfigurationException
				| TransformerFactoryConfigurationError | XMLWriteException
				| DOMException | TransformerException e) {

			// Show error message with information about the error
			JOptionPane.showMessageDialog(
					new JFrame(),
					_("C Compact was not able to save your personal settings")
							+ "\n" + _("The program returned") + ": "
							+ e.getMessage(),
					_("Unable to save your settings"),
					JOptionPane.ERROR_MESSAGE);
			
			System.err.println("ERROR: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Writes a XML data node to the given document
	 * 
	 * @param doc
	 *            The document object
	 * @param element
	 *            The root element of the XML tree
	 * @param name
	 *            The name of the new element
	 * @param value
	 *            The value (data) of the new element
	 * @return The new node to be appended to another node
	 * @throws XMLWriteException
	 */
	private Node writeNode(Document doc, String name, String value)
			throws XMLWriteException {
		if (value == null)
			throw new XMLWriteException();

		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}
	
	public void updateDescPane(GUITestPanel questPanel){
		if(getProfile() == null || getProfile().getCurrentQuest() == null)
			return;
		
		Quest lastClickedQuest = getProfile().getCurrentQuest();
    	String path = lastClickedQuest.getInitPath() + Quest.sep + lastClickedQuest.getPackagePath() + Quest.sep + lastClickedQuest.getQuestPath();
		
    	if(lastClickedQuest.isDescription() && lastClickedQuest.isStyle()){
			questPanel.setDescDoc(path + Quest.sep + Quest.FILE_DESCRIPTION, path + Quest.sep + Quest.FILE_STYLE);
        	questPanel.setjQuestTitle(getProfile().getCurrentQuest().getTitle());
      
        	
		//When the Quest only has a description
        }else if(lastClickedQuest.isDescription()){
        	questPanel.setDescDoc(path + Quest.sep + Quest.FILE_DESCRIPTION,"packages/default/style.css");
        	questPanel.setjQuestTitle(getProfile().getCurrentQuest().getTitle());		        
		}
	}
}
