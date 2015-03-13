package at.jku.ssw.cmm.gui.debug;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;

public class ErrorMessage {

	public void showErrorMessage(JFrame frame, String id, String language) {

		// Open linking table file
		InputStream stream = getClass().getResourceAsStream("/at/jku/ssw/cmm/gui/debug/systemerror.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		
		String title = null;
		String message = null;
		String common = null;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(stream);

			doc.getDocumentElement().normalize();
			
			// Get common additional text
			NodeList nList = ((org.w3c.dom.Document) doc)
					.getElementsByTagName("common");
			
			common = this.getCorrectMessage(nList.item(0), language);

			// Every linking information is tagged <error>
			nList = ((org.w3c.dom.Document) doc)
					.getElementsByTagName("error");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				if( ((Element) nList.item(temp)).getAttribute("id").equals(id) ) {
					NodeList childList = nList.item(temp).getChildNodes();
					
					for (int i = 0; i < childList.getLength(); i++) {
						if( childList.item(i).getNodeName().equals("title") )
							title = this.getCorrectMessage(childList.item(i), language);
						if( childList.item(i).getNodeName().equals("message") )
							message = this.getCorrectMessage(childList.item(i), language);
					}
				}
			}
			
		} catch (ParserConfigurationException e1) {
			DebugShell.out(State.ERROR, Area.ERROR,
					"Parser configuration exception when reading INTERNAL error table");
		} catch (IOException e) {
			DebugShell.out(State.ERROR, Area.ERROR,
					"I/O exception when reading INTERNAL error table");
		} catch (SAXException e) {
			DebugShell.out(State.ERROR, Area.ERROR,
					"SAX exception when reading INTERNAL error table");
		}
		
		common = common.replace("#xxxx", id);
			
		JOptionPane.showMessageDialog(frame,
				   message + "\n" + common,
				   title,
				   JOptionPane.ERROR_MESSAGE);
		
	}
	
	private String getCorrectMessage(Node n, String language){
		
		NodeList nList = n.getChildNodes();
		for( int temp = 0; temp < nList.getLength(); temp++ ) {
			if( nList.item(temp).getNodeName().equals(language) )
				return nList.item(temp).getTextContent();
		}
		return null;
	}
}
