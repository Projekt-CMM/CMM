package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.gui.file.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.settings.CentralPanel;

public class ProfilePanel2 {
	
	public ProfilePanel2( GUImain main ) {
		this.main = main;
	}
	
	private final GUImain main;
	
	private JLabel jProfilePicture;
	private JTextField jProfileName;
	
	public JPanel init() {
		
		JPanel masterPanel = new JPanel();
		masterPanel.setLayout(new BorderLayout());
		
		masterPanel.add(this.initProfileInfo(), BorderLayout.PAGE_START);
		masterPanel.add(new CentralPanel(this.main.getSettings().getProfile(), null), BorderLayout.CENTER);
		
		return masterPanel;
	}
	
	private JPanel initProfileInfo() {
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(_("Profile Information")));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		Profile profile = this.main.getSettings().getProfile();
		
		//Init Profile text field
		this.jProfileName = new JTextField(profile.getName());
		panel.add(this.jProfileName);
		
		//Load Profile Image
		String path = profile.getInitPath() + profile.getProfileimage();
		System.out.println("Path: " + path);
		this.jProfilePicture = new JLabel();
		this.jProfilePicture.setIcon(LoadStatics.loadIcon(path, 120, 120));
		panel.add(this.jProfilePicture);
		
		return panel;
	}

}
