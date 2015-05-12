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
	 * FileName of the Token
	 */
	private String relPath;
	
	/**
	 * Initial Path of the Token
	 */
	private String initPath;
	
	/**
	 * The Description of the Token
	 */
	private String description;
	
	/**
	 * A relative Path to the token image
	 */
	private String imagePath;
	
	private String successDoc;
	
	/**
	 * Static Strings, for reading the XML
	 */
	public static final String
		XML_TOKEN="token",
		XML_TITLE = "title",
		XML_DESCRIPTION = "description",
		XML_IMAGEPATH = "imagepath",
		XML_SUCCESS = "success";
	
	/**
	 * FolderName of the Tokens
	 */
	public static final String
		FOLDER_TOKENS = "tokens";
	
	/**
	 * @param path: the path of the <token>.xml
	 * Reads a Token and looks if the given Files are correct
	 */
	public static Token readToken(String initPath, String relPath){
		
		Token token = new Token();
		
		//Setting the Relative Path
		token.setRelPath(relPath);
		token.setInitPath(initPath);
		
		//Full Path
		String path = initPath + Quest.sep + relPath;
		
		try {
			//Reads the Token fully
			File file = new File(path);
			
			readTokenXML(file, token);
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
	private static Token readTokenXML(File file, Token token) throws ParserConfigurationException, SAXException, IOException{

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
			
				try{
					token.setSuccessDoc(eElement.getElementsByTagName(Token.XML_SUCCESS).item(0).getTextContent());
					}catch(NullPointerException e){
						//No Token ImagePath found
						token.setSuccessDoc(null);
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

	public String getRelPath() {
		return relPath;
	}

	public void setRelPath(String relPath) {
		this.relPath = relPath;
	}

	public String getInitPath() {
		return initPath;
	}

	public void setInitPath(String initPath) {
		this.initPath = initPath;
	}
	
	public void setSuccessDoc(String doc) {
		this.successDoc = doc;
	}

	public String getSuccessDoc() {
		return this.successDoc;
	}
	
}
