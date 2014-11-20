package at.jku.ssw.cmm.profile.settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.gui.utils.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;

public class UpperPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public UpperPanel( Profile profile, ProfileSettingsListener listener ) {

		super();

		this.profile = profile;
		this.listener = listener;

		this.init();
	}

	private final Profile profile;
	private final ProfileSettingsListener listener;

	// GridBagConstraints
	private GridBagConstraints c;
	
	private JTextField jTextField;

	private void init() {
		
		//Init gridBagLayout
		super.setLayout(new BorderLayout());
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JPanel inside = new JPanel();
		
		inside.setLayout(new GridBagLayout());
		inside.setBackground(Color.WHITE);
		inside.setBorder(BorderFactory.createLoweredBevelBorder());
		c = new GridBagConstraints();
		
		JLabel profilePicture;
		
		if( this.profile == null )
			profilePicture = LoadStatics.loadImage("images/prodef.png", true, 128, 128);
		else
			profilePicture = LoadStatics.loadImage(profile.getInitPath() + File.separator + "icon.png", true, 128, 128);
		
		profilePicture.setToolTipText("<html><b>click to change image</b><br>click here and select your<br>profile image</html>");
		profilePicture.addMouseListener(listener.profileImageListener);
		this.add(profilePicture, BorderLayout.LINE_START);
		
		//Initialize common layout parameters
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;   //request any extra vertical space
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5,5,5,5);
			
		inside.add(new JLabel("Name:"), setLayoutPosition(0, 0, 1, 1));
		this.jTextField = new JTextField(profile == null ? "" : profile.getName());
		this.jTextField.setToolTipText("<html><b>profile name</b><br>you can edit your name here</html>");
		inside.add(this.jTextField, setLayoutPosition(1, 0, 1, 1));
		inside.add(new JLabel("Level: " + (profile == null ? "0" : profile.getLevel()) ), setLayoutPosition(0, 1, 2, 1));
		
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
	private GridBagConstraints setLayoutPosition(int x,
			int y, int width, int height) {
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.gridheight = height;

		return c;
	}

	public String getProfileName() {

		return this.jTextField.getText();
	}

}
