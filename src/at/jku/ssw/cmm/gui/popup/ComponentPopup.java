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
 
package at.jku.ssw.cmm.gui.popup;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import at.jku.ssw.cmm.gui.GUImain;

public class ComponentPopup {

	public static void createPopUp( GUImain main, JComponent component, int x, int y ){
		
		ImagePopup popup = new ImagePopup("images/popup3.png");
		popup.setBounds(main.getGlassPane().getMousePosition().x-271, main.getGlassPane().getMousePosition().y-151, 310, 151);
		
		//JTextArea ta = new JTextArea( text );
		JScrollPane scrollPane = new JScrollPane(component);
		scrollPane.setBounds(10, 10, 298, 117);
		scrollPane.setPreferredSize(new Dimension(298, 117));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		popup.add(scrollPane);
		
		main.invokePopup(popup, main.getGlassPane().getMousePosition().x-271, main.getGlassPane().getMousePosition().y-151, 310, 143);
	}
}
