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

public class ErrorTable {

	public ErrorTable() {
		this.readErrorTable();
	}

	private Map<String, String> errorMap;

	public String getErrorHTML(String msg) {
		
		if( msg == null )
			return this.errorMap.get("default");

		for (Map.Entry<String, String> entry : this.errorMap.entrySet()) {
			if( Pattern.matches( entry.getKey(), msg ) ){
				return entry.getValue();
			}
		}
		
		System.err.println(msg);

		return this.errorMap.get("default");
	}

	private void readErrorTable() {
		this.errorMap = new HashMap<>();

		File fXmlFile = new File("error/table.xml");
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

		NodeList nList = ((org.w3c.dom.Document) doc)
				.getElementsByTagName("error");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Element eElement = (Element) nList.item(temp);
			this.errorMap.put(eElement.getAttribute("id"), eElement
					.getElementsByTagName("file").item(0).getTextContent());
		}
	}
}
