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
 
package at.jku.ssw.cmm.launcher;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import at.jku.ssw.cmm.gettext.Language;
import at.jku.ssw.cmm.gui.GUIExecutable;
import at.jku.ssw.cmm.gui.event.WindowEventListener;
import at.jku.ssw.cmm.gui.file.LoadStatics;
import at.jku.ssw.cmm.gui.properties.GUILanguage;
import at.jku.ssw.cmm.gui.properties.GUImainSettings;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.ProfileNotFoundException;
import at.jku.ssw.cmm.profile.XMLReadingException;


public class GUILauncherMain implements ActionListener, GUIExecutable{
	
	private JPanel jGlobalPanel;
	
	private JFrame jFrame;
	
	private final GUImainSettings settings;
	
	
	public static void main(String[] args) {
		
		GUImainSettings settings = new GUImainSettings(null);
		
		final GUILauncherMain app = new GUILauncherMain(settings);
		
		// Get the user's language
		if( settings.getLanguage() == null ) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new GUILanguage((GUIExecutable)app).start();
				}
			});
		}
				
		else {
			// Load translations
			Language.loadLanguage(settings.getLanguage() + ".po");
					
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					app.start(false);
				}
			});
		}
	}
	
	public GUILauncherMain(GUImainSettings settings){
		this.settings = settings;
	}
	
	@Override
	public void start(boolean test) {
		
		this.jFrame = new JFrame("C Compact Launcher");
		this.jFrame.setMinimumSize(new Dimension(700,481));
		
		
		
		// Load translations
		if( !Language.languageLoaded() )
			Language.loadLanguage(settings.getLanguage() + ".po");
		
		jGlobalPanel = new JPanel();
		jGlobalPanel.setLayout(new BorderLayout());
		jGlobalPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		
		//Block contains Welcome Block and CMM Logo
		this.addWelcomeBlock();
		
		//Block contains 
		this.addProfilePanel();
		this.addBottomPanel();	
		
		this.jFrame.setResizable(true);
		this.jFrame.add(jGlobalPanel);
		this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.jFrame.addWindowListener(new WindowEventListener(this.jFrame, settings));
		this.jFrame.setVisible(true);
	}

	/**
	 * Adding a Panel whitch contains:
	 * Welcome Message and logo
	 */
	private void addWelcomeBlock(){
		JPanel jWelcomePanel = new JPanel(new BorderLayout());
		
		//setting the welcome label
		JLabel welcomeMessage = new JLabel(_("Welcome"));
		jWelcomePanel.add(welcomeMessage,BorderLayout.LINE_START);
		
		//setting the size of the welcome message
		welcomeMessage.setFont (welcomeMessage.getFont().deriveFont (64.0f));
		
		
		JPanel logo = new JPanel();
		//Path for the logo image
		logo.add(new JLabel(LoadStatics.loadIcon("images/logo.png", 75, 75)));
		
		jWelcomePanel.add(logo,BorderLayout.LINE_END);
	
		
		jGlobalPanel.add(jWelcomePanel,BorderLayout.PAGE_START);
	}
	
	/**
	 * Adding selection Bar + Last Profiles Preview
	 */
	private void addProfilePanel(){
		JPanel jProfilePanel = new JPanel(new BorderLayout());
		JPanel jSelectProfilePanel = new JPanel(new BorderLayout());
		
		//Profile selection Bar
			JLabel jSelectProfileLabel = new JLabel(_("Select Profile") + ":");
			jSelectProfilePanel.add(jSelectProfileLabel, BorderLayout.LINE_START);
			
			JPanel jRightButtons = new JPanel(new FlowLayout());
			jRightButtons.setBackground(Color.WHITE);
			
				//creating the "find" button
				JButton jFindProfile = new JButton(_("Find"));
				jFindProfile.addMouseListener(new FindProfileListener(this.jFrame, settings));
				jFindProfile.setToolTipText("<html><b>" + _("Open existing profile") + "</b><br>" +_("Open an existing profile which is not<br>listed below by selecting its directory.") + "</html>");
				jRightButtons.add(jFindProfile);
				
				//creating "new" button
				JButton jCreateProfile = new JButton(_("New"));
				jCreateProfile.addMouseListener(new AddProfileListener(this.jFrame, settings));
				jCreateProfile.setToolTipText("<html><b>" + _("Create new profile") + "</b><br>" + _("Create a new profile and save it<br>anywhere on your computer.") + "</html>");
				jRightButtons.add(jCreateProfile);
				
				//creating "launch without profile" button
				JButton jBlankLaunch = new JButton(_("Launch without profile"));
				jBlankLaunch.addMouseListener(new BlankLaunchListener(this.jFrame, settings));
				jBlankLaunch.setToolTipText("<html><b>" + _("Launch C Compact without profile") + "</b><br>" + _("In this mode, you will not be able to do quests,<br>however you can still use all other features of C Compact.") + "</html>");
				jRightButtons.add(jBlankLaunch);
			
			//adding the buttons to the selection Panel
			jSelectProfilePanel.add(jRightButtons,BorderLayout.LINE_END);
			
			jSelectProfilePanel.setBackground(Color.white);
			jSelectProfilePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		jProfilePanel.add(jSelectProfilePanel, BorderLayout.PAGE_START);
		
		
		//creating profile Preview Panel
		JPanel jPreviewPanel = new JPanel();
			
			
			jPreviewPanel.setBackground(Color.white);
			jPreviewPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			
				JPanel jPreviewProfile = new JPanel();
				
				//Show recent profiles
				for(int i = 0; i < this.settings.getRecentProfiles().size(); i++) {
					try {
						jPreviewProfile.add(addProfilePreview(Profile.ReadProfile(this.settings.getRecentProfiles().get(i))));
					} catch (XMLReadingException | ProfileNotFoundException e) {
						this.settings.getRecentProfiles().remove(i);
						i--;
					}
				}
				
				//No recent profiles
				if( this.settings.getRecentProfiles().size() == 0 ){
					//setting the welcome label
					JLabel noProfilesMessage = new JLabel(_("No recent profiles"));
					jPreviewProfile.add(noProfilesMessage);
					
					//setting the size of the welcome message
					noProfilesMessage.setFont(noProfilesMessage.getFont().deriveFont (32.0f));
				}
				
			JScrollPane scrollPane = new JScrollPane(jPreviewProfile);
			
			//Scrollbar only Horizontal activated
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane.getHorizontalScrollBar().addAdjustmentListener(new ScrollBarRepainter(scrollPane));
			//scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
			
		jProfilePanel.add(scrollPane);
		
		//Adding panel to Global panel	
		jGlobalPanel.add(jProfilePanel,BorderLayout.CENTER);
	}
	
	private JPanel addProfilePreview(Profile profile){
		JPanel jMarginPanel = new JPanel(new BorderLayout());

		
		//jMarginPanel.setMinimumSize(new Dimension(200,300));
		//jMarginPanel.setPreferredSize(new Dimension(200,300));
		//jMarginPanel.setMinimumSize(new Dimension(200,300));
		
		jMarginPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 10, 10, 10), new EtchedBorder()));
		
		JPanel jProfile = new JPanel(new BorderLayout());
		jProfile.setBackground(Color.WHITE);
		jProfile.setBorder(new EmptyBorder(10, 10, 10, 10));
		//jProfile.setBorder(BorderFactory.createRaisedBevelBorder());
		
		JPanel jProfileTop = new JPanel(new BorderLayout());
			jProfileTop.setBackground(Color.WHITE);
			
			if(!profile.isMaster()){	
				
				JLabel name = new JLabel(_("Name") + ": " + profile.getName());
			
			
				jProfileTop.add(name,BorderLayout.PAGE_START);
			}
			
		jProfile.add(jProfileTop, BorderLayout.PAGE_START);
		//TODO Add Achievement Count
		JPanel profilePicPanel = new JPanel();
		
		
		profilePicPanel.setPreferredSize(new Dimension(200,200));
		profilePicPanel.setMinimumSize(new Dimension(200,200));
		
		//profilePicPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		
		//TODO save images in labels
		if( profile.getProfileimage() == null )
			profilePicPanel.add(new JLabel(LoadStatics.loadIcon(Profile.FILE_DEFAULTIMAGE, 200, 200)));//LoadStatics.loadImage(Profile.FILE_DEFAULTIMAGE, false, 200, 200));
		else
			profilePicPanel.add(new JLabel(LoadStatics.loadIcon(profile.getInitPath() + File.separator + profile.getProfileimage(), 200, 200)));//profilePicPanel.add(LoadStatics.loadImage(profile.getInitPath() + File.separator + profile.getProfileimage(), false, 200, 200));
		
		profilePicPanel.addMouseListener(new LauncherListener(settings,this.jFrame, profile));
		profilePicPanel.setToolTipText("<html><b>" + _("Select this profile") + "</b><br>" + _("Click image to change start<br>C Compact with this profile") + "</html>");
			
		jProfile.add(profilePicPanel, BorderLayout.CENTER);
		
			/*JButton openProfile = new JButton(_("Open"));
			openProfile.setToolTipText("<html><b>" + _("Launch C Compact") + "</b><br>" + _("using this profile") + "</html>");
			openProfile.addMouseListener(new LauncherListener(settings,(JFrame)this, profile));
		jProfile.add(openProfile,BorderLayout.PAGE_END);*/
			
		jMarginPanel.add(jProfile,BorderLayout.CENTER);
		
		
		return jMarginPanel	;
		
	}
	
	private void addBottomPanel(){
		JPanel jFinishPanel = new JPanel(new BorderLayout());
		
		String[] languages = {"English", "Deutsch"};
		JComboBox<String> jLanguageChooser = new JComboBox<>(languages);
		jLanguageChooser.setSelectedIndex(1);
		jLanguageChooser.setMinimumSize(new Dimension(100,30));
		jLanguageChooser.setPreferredSize(new Dimension(100,30));
		jLanguageChooser.addActionListener(this);
		
		jFinishPanel.add(jLanguageChooser,BorderLayout.LINE_START);
		
		switch( this.settings.getLanguage() ){
		case "en": jLanguageChooser.setSelectedIndex(0); break;
		case "de": jLanguageChooser.setSelectedIndex(1); break;
		}
	
	
	//JButton jStartButton = new JButton("Start");
		//jFinishPanel.add(jStartButton,BorderLayout.LINE_END);
	
		
		jFinishPanel.setBackground(Color.white);
		jFinishPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

	jGlobalPanel.add(jFinishPanel,BorderLayout.PAGE_END);
	//Page end Finished
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		@SuppressWarnings("unchecked")
		JComboBox<String> combo = (JComboBox<String>)arg0.getSource();
		
		switch( (String)combo.getSelectedItem() ){
		case "English":
			this.settings.setLanguage("en");
			System.out.println("en");
			break;
		case "Deutsch":
			this.settings.setLanguage("de");
			System.out.println("ger");
			break;
		}
		
		// Load translations
		Language.loadLanguage(settings.getLanguage() + ".po");
		
		this.jGlobalPanel.repaint();
	}

	@Override
	public GUImainSettings getSettings() {
		return this.settings;
	}

	@Override
	public void saveAndDispose() {
		this.jFrame.dispose();
	}

}
