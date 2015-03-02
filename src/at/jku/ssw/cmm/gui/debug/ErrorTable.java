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
 
package at.jku.ssw.cmm.gui.debug;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;

/**
 * This class links low-level error messages from comiler, interpreter
 * or GUI to the error html documents which are displayed to support the
 * user in finding problems in his source code.
 * <br>
 * The linking information is extracted from "error/table.xml". Depending
 * on the language chosen, different subfolders of "error" are used to build
 * the error message path, eg "error/en/..." for english error information.
 * 
 * @author fabian
 */
public class ErrorTable {

	/**
	 * This class links low-level error messages from comiler, interpreter
	 * or GUI to the error html documents which are displayed to support the
	 * user in finding problems in his source code
	 * 
	 * @param language The language chosen by the user
	 */
	public ErrorTable( String language ) {
		
		this.language = language;
		
		// Read error message linking data
		this.readErrorTable();
	}

	/**
	 * The language chosen by the user
	 */
	private String language;
	
	/**
	 * All error message linking information available is stored in this
	 * HashMap. The key is the error message from the system, the data is
	 * the name of the error file.
	 */
	private Map<String, String> errorMap;

	/**
	 * Links an error message to the according error description document
	 * in the right laguage
	 * 
	 * @param msg The error message
	 * @return The relative path to the error description document
	 */
	public String getErrorHTML(String msg) {
		
		// Return default description if error is undefined
		if( msg == null )
			return "error" + File.separator + language + File.separator + this.errorMap.get("default");

		// Look for the right error document
		for (Map.Entry<String, String> entry : this.errorMap.entrySet()) {
			
			// Regex matiching is allowed
			if( Pattern.matches( entry.getKey(), msg ) ){
				
				// Assemble the complete path to the error file
				return "error" + File.separator + language + File.separator + entry.getValue();
			}
		}

		return "error" + File.separator + language + File.separator + this.errorMap.get("default");
	}

	/**
	 * Reads the error linking data from the linking source file
	 */
	private void readErrorTable() {
		this.errorMap = new HashMap<>();
		
		// Select default language if given language does not exist
		if( !new File("error" + File.separator + language).exists() )
			this.language = "en";

		// Open linking table file
		File fXmlFile = new File("error/table.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			doc.getDocumentElement().normalize();

			// Every linking information is tagged <error>
			NodeList nList = ((org.w3c.dom.Document) doc)
					.getElementsByTagName("error");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Element eElement = (Element) nList.item(temp);
				this.errorMap.put(eElement.getAttribute("id"), eElement
						.getElementsByTagName("file").item(0).getTextContent());
			}
		} catch (ParserConfigurationException e1) {
			DebugShell.out(State.ERROR, Area.ERROR, "Parser configuration exception when reading error table");
		} catch (IOException e) {
			DebugShell.out(State.ERROR, Area.ERROR, "I/O exception when reading error table");
		} catch (SAXException e) {
			DebugShell.out(State.ERROR, Area.ERROR, "SAX exception when reading error table");
		}
	}
}
