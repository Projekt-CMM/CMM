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
 
package at.jku.ssw.cmm.gui.event.debug;

import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextArea;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.popup.ComponentPopup;
import at.jku.ssw.cmm.gui.popup.ImagePopup;

/**
 * Listener for the string popup button in the variable tree table.
 * Invokes popup if clicked.
 * 
 * @author fabian
 *
 */
public class StringPopupListener implements MouseListener {

	/**
	 * Listener for the string popup button in the variable tree table.
	 * Invokes popup if clicked.
	 * 
	 * @param popup Popup interface of the main GUI.
	 * 		This interface is necessary to invoke a popup.
	 * @param text The text to be displayed in the popup (content of the string)
	 */
	public StringPopupListener( GUImain main, String text ){
		this.main = main;
		this.text = text;
	}
	
	/**
	 * Popup interface of the main GUI.
	 * This interface is necessary to invoke a popup.
	 */
	private final GUImain main;
	
	/**
	 * The text to be displayed in the popup (content of the string)
	 */
	private final String text;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		//Invoke popup
		JTextArea ta = new JTextArea(text);
		ta.setEditable(false);
		ComponentPopup.createPopUp(main, ta, e.getX(), e.getY(), 60, 70, ImagePopup.SOUTH);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}

}
