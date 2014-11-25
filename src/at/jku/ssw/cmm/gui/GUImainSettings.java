package at.jku.ssw.cmm.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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

import at.jku.ssw.cmm.profile.XMLWriteException;

/**
 * Contains configuration data for the main GUI. The main GUI has a reference to
 * an object of this class. The configuration is saved in the "config.cfg" file
 * in the program's working directory. For loading the config data, see the
 * method readConfigFile below. For saving config data, see
 * {@link WindowEventListener} <br>
 * The config data includes variables like:
 * <ul>
 * <li>screen resolution</li>
 * <li>directory of the current c-- file</li>
 * </ul>
 * 
 * @author fabian
 *
 */
public class GUImainSettings {

	public static final String settings_XML = "settings.xml";

	public static final String XML_SETTINGS = "settings";
	public static final String XML_LANGUAGE = "language";
	public static final String XML_LASTPROFILE = "lastopened";

	/**
	 * Contains configuration data for the main GUI.
	 */
	public GUImainSettings() {
		this.readConfigXML();
	}

	/**
	 * Path of the current c-- file (null if no current file available)
	 */
	private String path;

	private List<String> lastProfiles;
	private String lastLanguage;

	/**
	 * Set the path of the current c-- file.
	 * 
	 * @param p
	 *            The new path of the c-- file. Loading null or "*" means that
	 *            no working directory is registered.
	 */
	// TODO profile
	public void setPath(String p) {
		if (p == "#" || p == null)
			this.path = null;
		else {
			this.path = p.endsWith(".cmm") ? p : p + ".cmm";
		}
	}

	/**
	 * @return The path of the current c-- file.
	 */
	// TODO profile
	public String getPath() {
		return this.path;
	}

	/**
	 * @return TRUE if the config contains a working path for the current c--
	 *         file <br>
	 *         FALSE if the working path is null or "*"
	 */
	// TODO profile
	public boolean hasPath() {
		if (this.path == null)
			return false;
		return true;
	}

	public void readConfigXML() {

		if (!new File(settings_XML).exists())
			return;

		File fXmlFile = new File(settings_XML);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Document doc = null;
		try {
			doc = dBuilder.parse(fXmlFile);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		doc.getDocumentElement().normalize();

		this.lastProfiles = new ArrayList<>();

		this.findXMLsettings(doc.getDocumentElement());

		System.out.println(" >>> language: " + this.lastLanguage);

		for (String s : this.lastProfiles)
			System.out.println(" >>> recent profile: " + s);
	}

	private void findXMLsettings(Node node) {

		if (node.getNodeName().equals(XML_LANGUAGE))
			this.lastLanguage = node.getTextContent();

		else if (node.getNodeName().equals(XML_LASTPROFILE))
			lastProfiles.add(node.getTextContent());

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				// calls this method for all the children which is Element
				findXMLsettings(currentNode);
			}
		}
	}

	public void writeXMLsettings() {
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder icBuilder;

		String path = settings_XML;

		try {
			icBuilder = icFactory.newDocumentBuilder();
			Document doc = icBuilder.newDocument();
			Element mainRootElement = doc.createElementNS(path,XML_SETTINGS);
			doc.appendChild(mainRootElement);

			if( this.lastLanguage != null )
				mainRootElement.appendChild(writeNode(doc, mainRootElement, XML_LANGUAGE, this.lastLanguage));
			
			if( this.lastProfiles != null && !this.lastProfiles.isEmpty() ){
				for( String profile : this.lastProfiles ){
					mainRootElement.appendChild(writeNode(doc, mainRootElement, XML_LASTPROFILE, profile));
				}
			}
			
			// output DOM XML to console
            Transformer transformer;
			transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            
            //Writing into file
            if(source != null){
            	StreamResult result = new StreamResult(new File(path));
            	transformer.transform(source, result);
            }

		} catch (ParserConfigurationException
				| TransformerFactoryConfigurationError
				| XMLWriteException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Node writeNode(Document doc, Element element, String name, String value) throws XMLWriteException{
        if(value == null)
        	throw new XMLWriteException();
    	
	    	Element node = doc.createElement(name);
	        node.appendChild(doc.createTextNode(value));
	        return node;
    }
}
