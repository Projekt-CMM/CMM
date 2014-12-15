package at.jku.ssw.cmm.profile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Token {

	/**
	 * The File Seperator of the System
	 */
	public static String sep = System.getProperty("file.separator");
	
	/**
	 * The Title of the Token
	 */
	private String title;
	
	/**
	 * Path of the Token
	 */
	private String path;
	
	/**
	 * The Description of the Token
	 */
	private String description;
	
	/**
	 * A relative Path to the token image
	 */
	private String imagePath;
	
	/**
	 * Static Strings, for reading the XML
	 */
	public static final String
		XML_TOKEN="token",
		XML_TITLE = "title",
		XML_DESCRIPTION = "description",
		XML_IMAGEPATH = "imagepath";
	
	/**
	 * FolderName of the Tokens
	 */
	public static final String
		FOLDER_TOKENS = "tokens";
	
	/**
	 * @param path: the path of the <token>.xml
	 * Reads a Token and looks if the given Files are correct
	 */
	public static Token readToken(String path){
		
		Token token = new Token();
		
		try {
			//Reads the Token fully
			readTokenXML(path, token);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			//Do something
			return null;
		}
		
		//TODO looks if the image is not corrupted..
		
		return token;
	}
		
	/**
	 * @param path: of the <token>.xml
	 * @param token: the token
	 * Reads the "<token>.xml" file
	 */
	private static Token readTokenXML(String path, Token token) throws ParserConfigurationException, SAXException, IOException{

		File file = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = null;
		try{
		 doc = dBuilder.parse(file);
		
		}catch(FileNotFoundException e){
			System.err.println(file + " not found!");
			
			//Returning broken Token
			return null;
		}	
		
		//Make the doc readable
		doc.getDocumentElement().normalize();
		
		//Getting the main Node
		NodeList nList = doc.getElementsByTagName(Token.XML_TOKEN);
		
		for (int i = 0; i < nList.getLength(); i++) {
			 
			Node nNode = nList.item(i);
	 
			//System.out.println("\nCurrent Element:" + nNode.getNodeName());
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
				try{
				token.setTitle(eElement.getElementsByTagName(Token.XML_TITLE).item(0).getTextContent());
				}catch(NullPointerException e){
					//No Title found
					token.setTitle(null);
				}
				
				try{
					token.setDescription(eElement.getElementsByTagName(Token.XML_DESCRIPTION).item(0).getTextContent());
					}catch(NullPointerException e){
						//No Description found
						token.setDescription(null);
					}
				
				try{
					token.setImagePath(eElement.getElementsByTagName(Token.XML_IMAGEPATH).item(0).getTextContent());
					}catch(NullPointerException e){
						//No Token ImagePath found
						token.setImagePath(null);
					}
				}
			}
		
		return token;
		
	}
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	

	
	
}
