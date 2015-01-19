package at.jku.ssw.cmm.gui;
 
import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.gui.event.quest.QuestPanelListener;
import at.jku.ssw.cmm.gui.utils.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.Token;
import at.jku.ssw.cmm.profile.settings.TokenListRenderer;

/**
 * This class contains all initialization and management methods for the second pane of the
 * right panel of the GUI. This panel contains information about the current profile and the
 * selected quest (including the quest description).
 * 
 * @author fabian
 *
 */
public class GUIprofilePanel {
	
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
	public GUIprofilePanel(JPanel cp, GUImain mod) {

		this.jPanel = cp;
		this.mod = mod;

		cp.setLayout(new GridBagLayout());
	    c = new GridBagConstraints();
		
		this.loadImages();
		this.initObejcts();
		
	}
	
	//GUIMAIN reference
	private final GUImain mod;
	
	//Basic Panel
	private final JPanel jPanel;
	
	//Profile information title
	private JLabel jProfileTitle;
	
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
	
	/**
	 * This is the JList showing all of the user's achievement tokens. The list
	 * contains JPanels with left-aligned FlowLayout. The Panels contain the
	 * token image and a description text.
	 */
	private JList<JPanel> achievements;
	
	/**
	 * Initializes lots of Swing objects for the profile and quest info
	 */
	private void initObejcts(){
	
		Profile profile = mod.getSettings().getProfile();
		
		if(profile != null){
			this.jProfileTitle = new JLabel(_("Profile"));
			this.jProfileName = new JLabel(_("Name") + ": \n" + profile.getName());
			this.jProfileLevel = new JLabel(_("Level") + ": " + 1);
		}else{
			this.jProfileTitle = new JLabel(_("No Profile choosen"));
			this.jProfileName = new JLabel();
			this.jProfileLevel = new JLabel();
		}
	
	}
	
	/**
	 * Loads the profile image and the player's reward tokens
	 */
	private void loadImages(){

	}
	
	
	private void initPanel(){
		JPanel jProfile = new JPanel();
		
		JPanel profileImage = new JPanel();
		profileImage.setLayout(new BorderLayout());
			profileImage.add(jProfilePicture, BorderLayout.CENTER);
		
		jProfile.add(profileImage,BorderLayout.LINE_START);
			
			
		JPanel profileInfos = new JPanel();
		profileInfos.setLayout(new BorderLayout());
			profileInfos.add(jProfileName,BorderLayout.NORTH);
			profileInfos.add(jProfileLevel,BorderLayout.CENTER);
		
		jProfile.add(profileInfos,BorderLayout.LINE_END);
		
		//Adding it to the top of the Profile
		jPanel.add(jProfile, BorderLayout.NORTH);
	}
	
	private void tokensPanel(){
		//Create border with title
		jPanel.setBorder(new TitledBorder(_("Achievements")));

				//The list to show the user's achievement tokens
				this.achievements = new JList<>(initList(mod.getSettings().getProfile()));
				this.achievements.setCellRenderer(new TokenListRenderer(
						(DefaultListModel<JPanel>) this.achievements.getModel()));
				this.achievements.setBackground(jPanel.getBackground());

				//The scroll pane to be able to scroll the achievement list (if necessary)
				JScrollPane scroll = new JScrollPane(this.achievements);
				scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
				scroll.setMinimumSize(new Dimension(100, 100));
				scroll.setPreferredSize(new Dimension(200, 200));
				scroll.setMaximumSize(new Dimension(500, 500));
				scroll.setBackground(jPanel.getBackground());

				//Add list to this panel
				jPanel.add(scroll, BorderLayout.CENTER);
		
	}
	
	/**
	 * This static method creates an example ListModel with some tokens and text
	 * as demonstration for the token list.
	 * <b>Remove this method if there is a proper function which reads achievement tokens
	 * from profile.</b>
	 * 
	 * @return ListModel (List data) with example achievements
	 */
	
	private static DefaultListModel<JPanel> initList(Profile profile) {
		DefaultListModel<JPanel> model = new DefaultListModel<>();

		JPanel panel;
		
		List<Token> allTokens = Profile.readProfileTokens(profile);
		
		
		for(Token t : allTokens){
			panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));
			panel.add(LoadStatics.loadImage(t.getInitPath() + Quest.sep + t.getImagePath(),false));
			panel.add(new JLabel(t.getTitle()));
			model.addElement(panel);
			
		}
		
		return model;
	}
	
	
	/**
	 * Refreshes the Right Quest Panel with new Data
	 * @param profile
	 * @param quest
	 * @param questPanel
	 */
	public void RefreshProfile(Profile profile){
		
		this.setjProfileName(profile.getName());
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
}
