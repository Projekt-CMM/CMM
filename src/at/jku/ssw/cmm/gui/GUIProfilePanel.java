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
 
package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.gui.event.quest.QuestPanelListener;
import at.jku.ssw.cmm.gui.utils.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;

/**
 * This class contains all initialization and management methods for the second pane of the
 * right panel of the GUI. This panel contains information about the current profile and the
 * selected quest (including the quest description).
 * 
 * @author fabian
 *
 */
public class GUIProfilePanel {
	
	//GridBagConstraints
	private GridBagConstraints c;
	
	/**
	 * This class contains all initialization and management methods for the second pane of the
	 * right panel of the GUI. This panel contains information about the current profile and the
	 * selected quest (including the quest description).
	 * <br>
	 * The Constructor also does basic initialization
	 * 
	 * @param cp
	 *            Main component of the main GUI
	 * @param mod
	 *            Interface for main GUI manipulations
	 */
	public GUIProfilePanel(JPanel cp, GUImain main) {

		this.cp = cp;
		this.main = main;
		
		this.listener = new QuestPanelListener(main);

		cp.setLayout(new GridBagLayout());
	    c = new GridBagConstraints();
		
		this.loadImages();
		this.initObejcts();
		this.initPanel(c);
		
	}
	
	//Basic Panel
	private final JPanel cp;
	private final GUImain main;
	
	private final QuestPanelListener listener;
	
	//Profile information title
	private JLabel jProfileTitle;
	
	//"Select Profile"-Button
	//private JButton jProfileSelectButton;
	
	//Profile picture
	private JLabel jProfilePicture = new JLabel();
	
	//Profile name
	private JLabel jProfileName;
	
	//Achievements
	private JPanel jProfileAchievements;
	
	//Profile level
	private JLabel jProfileLevel;
	
	//Profile level progress (XP)
	private JProgressBar jProfileXP;
	
	//Quest Title
	private JLabel jQuestTitle;
	
	//"Select quest"-Button
	private JButton jQuestSelectButton;
	
	//Editor pane with quest Information
	private JEditorPane jQuestInfo;
	
	/**
	 * Initializes lots of Swing objects for the profile and quest info
	 */
	private void initObejcts(){
	
		if(this.main.getSettings().getProfile() != null){
		//Labels
			this.jProfileTitle = new JLabel(_("Profile Information"));
			this.jProfileName = new JLabel(_("Name") + ": " + this.main.getSettings().getProfile().getName());
			this.jProfileLevel = new JLabel(_("Level") + ": " + this.main.getSettings().getProfile().getLevel());
		}else{
			this.jProfileTitle = new JLabel(_("No Profile choosen"));
			this.jProfileName = new JLabel();
			this.jProfileLevel = new JLabel();
		}
		
		this.jQuestTitle = new JLabel(_("Current Quest"));
		
		//Buttons
		//this.jProfileSelectButton = new JButton(_("Select Profile"));
		//this.jProfileSelectButton.addMouseListener(listener.profileHandler);
		this.jQuestSelectButton = new JButton(_("Select Quest"));
		this.jQuestSelectButton.addMouseListener(listener.questHandler);
		
		//Level progress scroll bar
		if(this.main.getSettings().getProfile() != null)
			this.jProfileLevel = new JLabel(_("Level") + ": " + this.main.getSettings().getProfile().getLevel());
		else
			this.jProfileLevel = new JLabel();

		
		//this.jProfileXP = new JProgressBar(0, 100);
		//this.jProfileXP.setValue(35);
		//this.jProfileXP.setStringPainted(true);
		//this.jProfileXP.setString("2560/7860 XP");
		
		//Quest info panel
		//this.jQuestInfo = LoadStatics.loadHTMLdoc("packages/default/Hallo Welt/index.html", "profileTest/doxygen.css");
	}
	
	/**
	 * Loads the profile image and the player's reward tokens
	 */
	private void loadImages(){
		Profile profile = this.main.getSettings().getProfile();
		
		//Load Profile Image
		if(this.main.getSettings().getProfile() != null && this.main.getSettings().getProfile().getProfileimage() != null){
			String path = profile.getInitPath() + Profile.sep + profile.getProfileimage();
			System.out.println(path);
			
			this.jProfilePicture.setIcon(LoadStatics.loadIcon(path, 120, 120));
		}
		else 
			this.jProfilePicture = new JLabel();
		
		//Loading profile achievement tokens
		this.jProfileAchievements = new JPanel();
		JLabel imageBuffer;
		
		imageBuffer = LoadStatics.loadImage("profileTest/tokens/Icon_Craft.png");
		this.jProfileAchievements.add(imageBuffer);
		
		imageBuffer = LoadStatics.loadImage("profileTest/tokens/Icon_Deko.png");
		this.jProfileAchievements.add(imageBuffer);
		
		imageBuffer = LoadStatics.loadImage("profileTest/tokens/Icon_Fisch.png");
		this.jProfileAchievements.add(imageBuffer);
		
		imageBuffer = LoadStatics.loadImage("profileTest/tokens/Icon_Haus.png");
		this.jProfileAchievements.add(imageBuffer);
		
		imageBuffer = LoadStatics.loadImage("profileTest/tokens/Icon_Segel.png");
		this.jProfileAchievements.add(imageBuffer);
	}
	
	/**
	 * Plots all components to the panel. This method is the last step of the initialization
	 * of the profile information panel. Should only be called from the constructor.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT.</i><br>
	 * </hr>
	 * 
	 * @param c
	 */
	private void initPanel( GridBagConstraints c ){

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;   //request any extra vertical space
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5,5,5,5);
		
		
		cp.add(this.jProfileTitle, this.setLayoutPosition(c, 0, 0, 2, 1));
		//cp.add(this.jProfileSelectButton, this.setLayoutPosition(c, 1, 0, 1, 1));
		
		cp.add(this.jProfilePicture, this.setLayoutPosition(c, 0, 1, 1, 4));
		
		cp.add(this.jProfileName, this.setLayoutPosition(c, 1, 1, 1, 1));
		cp.add(this.jProfileAchievements, this.setLayoutPosition(c, 1, 2, 1, 1));
		cp.add(this.jProfileLevel, this.setLayoutPosition(c, 1, 3, 1, 1));
		//cp.add(this.jProfileXP, this.setLayoutPosition(c, 1, 4, 1, 1));
		
		cp.add(this.jQuestTitle, this.setLayoutPosition(c, 0, 5 , 1, 1));
		cp.add(this.jQuestSelectButton, this.setLayoutPosition(c, 1, 5, 1, 1));
		
		//Description Text Panel
		this.jQuestInfo = new JEditorPane();
		this.jQuestInfo.setEditable(false);
		this.jQuestInfo.setContentType("text/html");
		
      	JScrollPane scroll = new JScrollPane(this.jQuestInfo);
      	scroll.setBorder(new EmptyBorder(5, 5, 5, 5));
      	c.weighty = 1;
      	c.weightx = 1;
      	c.ipadx = 200;
      	c.ipady = 500;
      	cp.add(scroll, this.setLayoutPosition(c, 0, 6, 2, 1));
	}
	
	/**
	 * Writes all the below given data to the layout constrains. Used to define the position
	 * and size of a component in the GridBag Layout.
	 * 
	 * @param c Layout Constrains.
	 * @param x Position of the grid (x)
	 * @param y Position of the grid (y)
	 * @param width How many columns the component is wide
	 * @param height How many lines the component is high
	 * @return The modified Layout Constrains. Actually not necessary as the parameter c is
	 * called by reference, however you can use the return value directly inside another function call.
	 */
	private GridBagConstraints setLayoutPosition( GridBagConstraints c, int x, int y, int width, int height ){
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.gridheight = height;
		
		return c;
	}
	
	
	/**
	 * Refreshes the Right Quest Panel with new Data
	 * @param profile
	 * @param quest
	 * @param questPanel
	 */
	public void RefreshProfile(Profile profile){
		//TODO
		
		this.setjProfileName(profile.getName());
		this.setjProfileLevel(profile.getLevel());
	}

	/**
	 * @return the jProfileTitle
	 */
	public JLabel getjProfileTitle() {
		return jProfileTitle;
	}

	/**
	 * @param jProfileTitle the jProfileTitle to set
	 */
	public void setjProfileTitle(JLabel jProfileTitle) {
		this.jProfileTitle.repaint();
		this.jProfileTitle = jProfileTitle;
	}

	/**
	 * @return the jProfilePicture
	 */
	public JLabel getjProfilePicture() {
		return jProfilePicture;
	}

	/**
	 * @param jProfilePicture the jProfilePicture to set
	 */
	public void setjProfilePicture(JLabel jProfilePicture) {
		this.jProfilePicture.repaint();
		this.jProfilePicture = jProfilePicture;
	}

	/**
	 * @return the jProfileName
	 */
	public JLabel getjProfileName() {
		return jProfileName;
	}

	/**
	 * @param jProfileName the jProfileName to set
	 */
	public void setjProfileName(String profileName) {
		this.jProfileName.setText("Profile: " + profileName);
	}

	/**
	 * @return the jProfileAchievements
	 */
	public JPanel getjProfileAchievements() {
		return jProfileAchievements;
	}

	/**
	 * @param jProfileAchievements the jProfileAchievements to set
	 */
	public void setjProfileAchievements(JPanel jProfileAchievements) {
		this.jProfileAchievements.repaint();
		this.jProfileAchievements = jProfileAchievements;
	}

	/**
	 * @return the jProfileLevel
	 */
	public JLabel getjProfileLevel() {
		return jProfileLevel;
	}

	/**
	 * @param i the jProfileLevel to set
	 */
	public void setjProfileLevel(int i) {
		this.jProfileLevel.setText("Level: " + i);
	}

	/**
	 * @return the jProfileXP
	 */
	public JProgressBar getjProfileXP() {
		return jProfileXP;
	}

	/**
	 * @param jProfileXP the jProfileXP to set
	 */
	public void setjProfileXP(JProgressBar jProfileXP) {
		this.jProfileXP.repaint();
		this.jProfileXP = jProfileXP;
	}

	/**
	 * @return the jQuestTitle
	 */
	public JLabel getjQuestTitle() {
		return jQuestTitle;
	}

	/**x
	 * @param jQuestTitle the jQuestTitle to set
	 */
	public void setjQuestTitle(String questTitle) {
		this.jQuestTitle.setText(questTitle);

	}

	public void setDescDoc( String html, String css ){
		
		try {
			this.jQuestInfo.setPage(LoadStatics.getHTMLUrl(html));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		this.jQuestInfo.setDocument(LoadStatics.readStyleSheet(css));
	}
}
