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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.gui.file.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.Token;

/**
 * This class is the central panel of the profile settings GUI. It contains a
 * JList (including JScrollPane) with the user's achievement tokens.
 * 
 * @author fabian
 *
 */
public class CentralPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * This class is the central panel of the profile settings GUI. It contains
	 * a JList (including JScrollPane) with the user's achievement tokens.
	 * 
	 * @param profile
	 *            The profile which is currently displayed and edited
	 * @param listener
	 *            The listener for the profile settings GUI
	 */
	public CentralPanel(Profile profile) {

		// Call super constructor (JPanel)
		super();

		// Save local variables
		this.profile = profile;

		// Initialize all components of this panel
		this.init();
	}

	/**
	 * The profile which is currently displayed and edited
	 */
	private final Profile profile;

	/**
	 * This is the JList showing all of the user's achievement tokens. The list
	 * contains JPanels with left-aligned FlowLayout. The Panels contain the
	 * token image and a description text.
	 */
	private JList<JPanel> achievements;

	/**
	 * Initializes all components of this panel. <b>Should only be called once
	 * in constructor</b>
	 */
	private void init() {

		//Set layout of this panel
		this.setLayout(new BorderLayout());
		
		//Create border with title
		this.setBorder(new TitledBorder(_("Achievements")));

		//The list to show the user's achievement tokens
		this.achievements = new JList<>(initList(profile));
		this.achievements.setCellRenderer(new TokenListRenderer(
				(DefaultListModel<JPanel>) this.achievements.getModel()));
		this.achievements.setBackground(this.getBackground());

		//The scroll pane to be able to scroll the achievement list (if necessary)
		JScrollPane scroll = new JScrollPane(this.achievements);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setMinimumSize(new Dimension(100, 100));
		scroll.setPreferredSize(new Dimension(200, 200));
		scroll.setMaximumSize(new Dimension(500, 500));
		scroll.setBackground(this.getBackground());

		//Add list to this panel
		this.add(scroll, BorderLayout.CENTER);
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
		
		List<Token> allTokens = profile.readProfileTokens();
		
		
		if(allTokens != null){
			List<Token> addedTokens = new ArrayList<Token>();
			for(Token t : allTokens){
				if(testToken(addedTokens, t)){
					panel = new JPanel();
					panel.setLayout(new FlowLayout(FlowLayout.LEFT));
					panel.add(new JLabel(LoadStatics.loadIcon(t.getInitPath() + Quest.sep + t.getImagePath(), 32, 32)));
					panel.setToolTipText("<html><b><p width=\"200\">" + t.getDescription()
							+ "</p></b></html>");
					
					panel.add(new JLabel(t.getTitle()));
					model.addElement(panel);
				}
				
			}
		}
		
		return model;
	}
	
	/**
	 * Test if the Token is already in the List
	 * @param tl
	 * @param t
	 * @return
	 */
	private static boolean testToken(List<Token> tl, Token t){
		
		if(tl != null && t != null){
		for(Token token: tl){
			if(token.getInitPath().equals(t.getInitPath())
					&&token.getRelPath().equals(t.getRelPath())){
				return false;
			}
		}}
		
		tl.add(t);

		return true;
	}

}
