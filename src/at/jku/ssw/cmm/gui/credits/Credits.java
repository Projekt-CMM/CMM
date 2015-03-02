package at.jku.ssw.cmm.gui.credits;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * This class is responsible for the "credits" window of C Compact.
 * As we do not want anyone to change the credits information
 * (except under the terms of GNU GPL3 of course), we attached the
 * credits text to the projekt resources.
 * 
 * @author fabian
 */
public class Credits {

	/**
	 * Initializes and shows a window displaying credits information
	 */
	public void start() {
		
		// Initialize window
		JFrame jFrame = new JFrame(_("About C Compact"));
		jFrame.setLocationRelativeTo(null);
		jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jFrame.setPreferredSize(new Dimension(400, 400));
		jFrame.setResizable(false);
		
		//Load resource for stylesheet
		java.net.URL cssURL = null;
		javax.swing.text.Document doc = null;
		try {
			cssURL = getClass().getResource("/at/jku/ssw/cmm/gui/credits/credits.css").toURI().toURL();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		// Import stylesheet
		if (cssURL != null) {
			StyleSheet s = new StyleSheet();
			s.importStyleSheet(cssURL);
			HTMLEditorKit kit = new HTMLEditorKit();
			kit.setStyleSheet(s);
			doc = kit.createDefaultDocument();
		}
		
		// Load credits text
		java.net.URL htmlURL = null;
		try {
			htmlURL = getClass().getResource("/at/jku/ssw/cmm/gui/credits/credits.html").toURI().toURL();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		// Initialize text pane for credits information
		JEditorPane editorPane = new JEditorPane();
		
		// Set style information
		if( doc != null )
			editorPane.setDocument(doc);
		
		// Load credits text into text pane
		try {
			editorPane.setPage(htmlURL);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		// Add text pane to window
		jFrame.setLayout(new BorderLayout());
		jFrame.add(editorPane, BorderLayout.CENTER);

		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		jFrame.setResizable(false);
		jFrame.pack();
		jFrame.setVisible(true);
	}

}
