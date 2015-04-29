package at.jku.ssw.cmm.quest.success;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.gui.file.LoadStatics;
import at.jku.ssw.cmm.profile.Token;

public class SuccessPopup extends JFrame {
	
	public static void main(String[] args) {
		Token newToken = Token.readToken("packages/1. Versuch/tokens", "start.xml");
		new SuccessPopup("Tolle Quest", newToken, newToken);
	}

	private static final long serialVersionUID = -5237673141059442404L;
	
	public SuccessPopup( String questName, Token oldToken, Token newToken ) {
		
		// Initialize Window
		super(_("Test successful"));
		super.setMinimumSize(new Dimension(300, 150));
		super.setMaximumSize(new Dimension(300, 400));
		//super.setResizable(false);
		super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
		super.setLayout(new BorderLayout());
		
		// Window contents
		JEditorPane textPane = new JEditorPane();
		textPane.setEditable(false);
		textPane.setContentType("text/html");
		try {
			textPane.setPage(LoadStatics.getHTMLUrl(newToken.getInitPath() + File.separator + newToken.getSuccessDoc()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		// Make window visible
		super.add(textPane, BorderLayout.CENTER);
		super.setVisible(true);
	}
	
}
/*
 * textPane.setText(
				
				// Headline
				"<h2>" + _("Quest completed") + "</h2>" +
				
				// Congratulations
				"<p>" + _("Congratulations!") + " " + _("You have completed the quest") +
				" \"" + questName + "\" " + _("with success") + "</p><hr/>" +
				
				// Add tokens if the user achieves one
				(newToken==null?"":
					"<img src='addProfile.png'></img><img src='addProfile.png'></img>"
				)
		);
 */