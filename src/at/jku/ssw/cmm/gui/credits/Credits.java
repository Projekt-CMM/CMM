package at.jku.ssw.cmm.gui.credits;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;

public class Credits {

	public Credits() {

	}
	
	private JFrame jFrame;

	public void start() {
		// Initialize window
		this.jFrame = new JFrame(_("About C Compact"));
		this.jFrame.setLocationRelativeTo(null);
		this.jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.jFrame.setPreferredSize(new Dimension(400, 400));
		this.jFrame.setResizable(false);
		
		java.net.URL htmlURL = null;
		try {
			//htmlURL = getClass().getResource("/at/jku/ssw/cmm/gui/credits/credits.html").toURI().toURL();
			htmlURL = new File("src/at/jku/ssw/cmm/gui/credits/credits.html").toURI().toURL();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return;
		} /*catch (URISyntaxException e) {
			e.printStackTrace();
		}*/
		
		JEditorPane editorPane = new JEditorPane();
		try {
			editorPane.setPage(htmlURL);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		this.jFrame.setLayout(new BorderLayout());
		this.jFrame.add(editorPane, BorderLayout.CENTER);

		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.setResizable(false);
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}

}
