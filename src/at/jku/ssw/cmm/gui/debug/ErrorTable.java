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

public class ErrorTable {

	public ErrorTable( String language ) {
		
		this.language = language;
		this.readErrorTable();
	}

	private String language;
	
	private Map<String, String> errorMap;

	public String getErrorHTML(String msg) {
		
		if( msg == null )
			return "error" + File.separator + language + File.separator + this.errorMap.get("default");

		for (Map.Entry<String, String> entry : this.errorMap.entrySet()) {
			if( Pattern.matches( entry.getKey(), msg ) ){
				return "error" + File.separator + language + File.separator + entry.getValue();
			}
		}
		
		System.err.println(msg);

		return "error" + File.separator + language + File.separator + this.errorMap.get("default");
	}

	private void readErrorTable() {
		this.errorMap = new HashMap<>();
		
		if( !new File("error" + File.separator + language).exists() )
			this.language = "en";

		File fXmlFile = new File("error/table.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			doc.getDocumentElement().normalize();

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
