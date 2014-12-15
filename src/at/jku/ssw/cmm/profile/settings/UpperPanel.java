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
 
package at.jku.ssw.cmm.profile.settings;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.gui.utils.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;

/**
 * The upper panel of the profile settings GUI contains all the user's data: the
 * profile image, the nickname, the level and some statistics.
 * 
 * @author fabian
 *
 */
public class UpperPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * The upper panel of the profile settings GUI contains all the user's data:
	 * the profile image, the nickname, the level and some statistics.
	 * 
	 * @param profile
	 *            The profile which is currently displayed and edited
	 * @param listener
	 *            The listener for the profile settings GUI
	 */
	public UpperPanel(Profile profile, ProfileSettingsListener listener) {

		// Call super constructor (JPanel)
		super();

		// Save local variables
		this.profile = profile;
		this.listener = listener;

		// Initialize all components of this panel
		this.init();
	}

	/**
	 * The profile which is currently displayed and edited
	 */
	private final Profile profile;
	
	/**
	 * The listener for the profile settings GUI
	 */
	private final ProfileSettingsListener listener;
	
	/**
	 * Profile pic Path String
	 */
	private String profilePicPath;
	
	// GridBagConstraints
	private GridBagConstraints c;

	private JTextField jTextField;
	
	private JLabel profilePicture = new JLabel();

	/**
	 * Initializes all components of this panel. <b>Should only be called once
	 * in constructor</b>
	 */
	private void init() {
		
		// Init gridBagLayout
		super.setLayout(new BorderLayout());
		this.setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel inside = new JPanel();

		inside.setLayout(new GridBagLayout());
		inside.setBackground(Color.WHITE);
		inside.setBorder(BorderFactory.createLoweredBevelBorder());
		c = new GridBagConstraints();

		loadProfilePic(this.profile);
		profilePicture.setToolTipText("<html><b>" + _("click to change image")
				+ "</b><br>" + _("click here and select your<br>profile image")
				+ "</html>");
		profilePicture.addMouseListener(listener.profileImageListener);
		this.add(profilePicture, BorderLayout.LINE_START);

		// Initialize common layout parameters
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5; // request any extra vertical space
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5, 5, 5, 5);

		inside.add(new JLabel(_("Name:")), setLayoutPosition(0, 0, 1, 1));
		this.jTextField = new JTextField(profile == null ? ""
				: profile.getName());
		this.jTextField.setToolTipText("<html><b>" + _("profile name")
				+ "</b><br>" + _("you can edit your name here") + "</html>");
		inside.add(this.jTextField, setLayoutPosition(1, 0, 1, 1));
		inside.add(new JLabel(_("Level") + ": "
				+ (profile == null ? "0" : profile.getLevel())),
				setLayoutPosition(0, 1, 2, 1));

		inside.add(new JLabel("Stat 1:"), setLayoutPosition(0, 2, 1, 1));
		inside.add(new JLabel("Stat 2:"), setLayoutPosition(1, 2, 1, 1));
		inside.add(new JLabel("Stat 3:"), setLayoutPosition(0, 3, 1, 1));
		inside.add(new JLabel("Stat 4:"), setLayoutPosition(1, 3, 1, 1));

		this.add(inside, BorderLayout.CENTER);
	}

	/**
	 * Writes all the below given data to the layout constrains. Used to define
	 * the position and size of a component in the GridBag Layout.
	 * 
	 * @param c
	 *            Layout Constrains.
	 * @param x
	 *            Position of the grid (x)
	 * @param y
	 *            Position of the grid (y)
	 * @param width
	 *            How many columns the component is wide
	 * @param height
	 *            How many lines the component is high
	 * @return The modified Layout Constrains. Actually not necessary as the
	 *         parameter c is called by reference, however you can use the
	 *         return value directly inside another function call.
	 */
	private GridBagConstraints setLayoutPosition(int x, int y, int width,
			int height) {
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.gridheight = height;

		return c;
	}

	/**
	 * @return The (new) name of the profile which is currently edited/created.
	 *         This is not the name which is saved in the profile.xml file, this
	 *         is the text from the JTextField "name"
	 */
	public String getProfileName() {

		return this.jTextField.getText();
	}
	
	/**
	 * The choosen Profile Pic Path, in the image selection window
	 * @return profilePicPath
	 */
	public String getProfilePic(){
		
		return profilePicPath;
	}
	
	/**
	 * load the current profile Image
	 */
	public void loadProfilePic(Profile profile){
		if (profile == null || profile.getProfileimage() == null)
			profilePicture.setIcon(LoadStatics.loadIcon(Profile.IMAGE_DEFAULT,
					128, 128));
		else
			//Loading fixed profile image
			if(profile.getInitPath() != null){
				ImageIcon icon = LoadStatics.loadIcon(profile.getInitPath()
						+ File.separator + profile.getProfileimage(), 128, 128);
					profilePicture.setIcon(icon);
			}
		
		if(profilePicture.getIcon().getIconHeight() == -1 && profilePicture.getIcon().getIconWidth() == -1)
			profilePicture.setText(_("Corrupted Avatar"));
				

		profilePicture.revalidate();
		profilePicture.repaint();
	}
	
	/**
	 * Refreshes the Profile pic, TODO
	 */
	public void refreshProfilePic(String profilePicPath){
		
		System.out.println("Current ProfilePic: " + profile.getProfileimage());
		profilePicture.setIcon(LoadStatics.loadIcon(profilePicPath,  128, 128));
		
		//Setting for later usage
		if(profilePicture.getIcon().getIconHeight() == -1 && profilePicture.getIcon().getIconWidth() == -1)
			profilePicture.setText(_("Corrupted Image"));
		else{
			//Only saving if the file is useable, TODO Error field
			this.profilePicPath = profilePicPath;
			profilePicture.setText("");
		}
		
	}

}
