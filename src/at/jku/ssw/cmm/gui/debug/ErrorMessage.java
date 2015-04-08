package at.jku.ssw.cmm.gui.debug;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
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
		
		if(common != null)
			common = common.replace("#xxxx", id);
		
		//jeb.setContentType("text/html");//set content as html
		final JEditorPane editorPane = new JEditorPane();

        // Enable use of custom set fonts
        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);  
        editorPane.setFont(new Font("Arial", Font.BOLD, 13));

        editorPane.setPreferredSize(new Dimension(520,100));
        editorPane.setContentType("text/html");
        System.out.println(message + common);
        editorPane.setText("" + message + "<br>" + common + ""); //TODO SET text
        
        editorPane.setEditable(false);//so its not editable
        editorPane.setOpaque(false);//so we dont see whit background
        
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hle) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                    System.out.println(hle.getURL());
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.browse(hle.getURL().toURI());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JOptionPane.showMessageDialog(null,
                new JScrollPane(editorPane),
                "Error Message",
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
