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

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

/**
 * Event Listener for the ProfileSettingsList. Used for triggering an event when the
 * user closes the window. The save dialog call and exit manager itself is
 * located in a separate static method because by this it can also be called if
 * the window is closed by some other event or reason.
 * 
 * @author peda
 *
 */

public class ProfileWindowEventListener implements WindowListener {

	/**
	 * GUIprofileSettings Reference
	 */
	private final GUIprofileSettings gui;
	
	
	/**
	 * A reference to the jFrame
	 */
	private final JFrame jFrame; 
	
	/**
	 * Constructor
	 * @param gui
	 */
	public ProfileWindowEventListener(GUIprofileSettings gui, JFrame jFrame){
		this.gui = gui;
		this.jFrame = jFrame;
	}
	
	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	/**
	 * Closing the current jFrame and opening the right Frame
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		gui.dispose(jFrame);		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

}
