package at.jku.ssw.cmm.quest.success;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;

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
		Token newToken = new Token();
		newToken.setDescription("Blabla");
		newToken.setImagePath("addProfile.png");
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
		textPane.setText(
				
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
				
		// Make window visible
		super.add(textPane, BorderLayout.CENTER);
		super.setVisible(true);
	}
	
}/*
		
		// Initialize Window
		super(_("Test successful"));
		super.setMinimumSize(new Dimension(300, 150));
		super.setMaximumSize(new Dimension(300, 400));
		//super.setResizable(false);
		super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		super.setLayout(new BorderLayout());
		
		// Window Contents
		JPanel panel1 = new JPanel();
		panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel1.setLayout(new BorderLayout());
		
		JLabel title = new JLabel(_("Quest completed"));
		title.setFont(title.getFont().deriveFont(16.f));
		panel1.add(title, BorderLayout.PAGE_START);
		
		JLabel desc = new JLabel("<html>" +_("You have successfully completed the quest") + " \""
				+ questName + "\".<br/>" + _("Congratulations!") + (newToken == null ? "" : "<hr/>") + "</html>");
		panel1.add(desc, BorderLayout.CENTER);
		
		if( newToken != null ) {
			JPanel panel2 = new JPanel();
			panel2.setLayout(new BorderLayout());
			panel2.setBorder(new EmptyBorder(5, 0, 5, 5));
			
			JLabel congrats = new JLabel(newToken.getDescription());
			panel2.add(congrats, BorderLayout.PAGE_START);
			
			panel2.add(this.getTokenGraphics(oldToken, newToken));
			
			panel1.add(panel2, BorderLayout.PAGE_END);
		}
		
		JPanel panel3 = new JPanel();
		panel3.add(new JButton(_("OK")));
		
		// Make window visible
		super.add(panel1, BorderLayout.CENTER);
		super.add(panel3, BorderLayout.PAGE_END);
		super.setVisible(true);
	}
	
	private JPanel getTokenGraphics(Token oldToken, Token newToken) {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		if( oldToken == null )
			panel.add(LoadStatics.loadImage(newToken.getImagePath(), false, 50, 50), BorderLayout.CENTER);
		else {
			panel.add(LoadStatics.loadImage(oldToken.getImagePath(), false, 50, 50), BorderLayout.LINE_START);
			panel.add(LoadStatics.loadImage("images/arrow.png", false), BorderLayout.CENTER);
			panel.add(LoadStatics.loadImage(newToken.getImagePath(), false, 50, 50), BorderLayout.LINE_END);
		}
		
		return panel;
	}
}*/
