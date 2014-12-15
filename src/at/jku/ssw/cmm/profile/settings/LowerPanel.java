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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.profile.Profile;

/**
 * This panel is part of the profile settings GUI. This panel is displayed at
 * the bottom of the window and contains three components: a progress bar for
 * the total quest progress, the "cancel" button and the "save" button.
 * 
 * @author fabian
 *
 */
public class LowerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * This panel is part of the profile settings GUI. This panel is displayed
	 * at the bottom of the window and contains three components: a progress bar
	 * for the total quest progress, the "cancel" button and the "save" button.
	 * 
	 * @param profile The profile which is currently displayed and edited
	 * @param listener The listener for the profile settings GUI
	 */
	public LowerPanel(Profile profile, ProfileSettingsListener listener) {

		//Call super constructor (JPanel)
		super();

		//Save local variables
		this.profile = profile;
		this.listener = listener;

		//Initialize all components of this panel
		this.init();
	}

	@SuppressWarnings("unused")
	/**
	 * The profile which is currently displayed and edited
	 */
	private final Profile profile;
	
	/**
	 * The listener for the profile settings GUI
	 */
	private final ProfileSettingsListener listener;

	/**
	 * The "cancel" button
	 */
	private JButton jButtonCancel;
	
	/**
	 * The "save" button
	 */
	private JButton jButtonSave;

	/**
	 * The "total quest progress" progress bar
	 */
	private JProgressBar jQuestProgress;

	/**
	 * Initializes all components of this panel.
	 * <b>Should only be called once in constructor</b>
	 */
	private void init() {

		//Set layout of this panel
		this.setLayout(new BorderLayout());
		this.setBorder(new EmptyBorder(5, 5, 5, 5));

		//Progress bar "total quest progress"
		this.jQuestProgress = new JProgressBar(0, 10);
		this.jQuestProgress.setValue(5);
		this.jQuestProgress.setName("Hello world");
		this.add(this.jQuestProgress, BorderLayout.CENTER);

		//Extra panel for the two buttons "save" and "cancel"
		JPanel buttonPanel = new JPanel();

		//Initialize the "cancel" button
		this.jButtonCancel = new JButton(_("Cancel"));
		buttonPanel.add(this.jButtonCancel);
		this.jButtonCancel.addMouseListener(this.listener.cancelButtonListener);

		//Initialize the "save" button
		this.jButtonSave = new JButton(_("Save"));
		buttonPanel.add(this.jButtonSave);
		this.jButtonSave.addMouseListener(this.listener.saveButtonListener);

		//Add extra button panel to main panel (this class)
		this.add(buttonPanel, BorderLayout.LINE_END);
	}

}
