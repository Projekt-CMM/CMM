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
 
package at.jku.ssw.cmm.gui.event;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.properties.GUImainSettings;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.XMLWriteException;

/**
 * Event listener for the main GUI window. Used for triggering an event when the
 * user closes the window. The save dialog call and exit manager itself is
 * located in a separate static method because by this it can also be called if
 * the window is closed by some other event or reason.
 * 
 * @author fabian
 *
 */
public class WindowEventListener implements WindowListener {

	/**
	 * Event listener for the main GUI window. Used for triggering an event when
	 * the user closes the window. The save dialog call and exit manager itself
	 * is located in a separate static method because by this it can also be
	 * called if the window is closed by some other event or reason.
	 * 
	 * @param jFrame
	 *            The frame of the main GUI window
	 * @param main
	 *            A reference to the main GUI
	 */
	public WindowEventListener(JFrame jFrame, GUImain main) {
		this.jFrame = jFrame;
		this.main = main;
		this.settings = main.getSettings();
	}
	
	public WindowEventListener(JFrame jFrame, GUImainSettings settings) {
		this.jFrame = jFrame;
		this.main = null;
		this.settings = settings;
	}

	/**
	 * A reference to the main frame of the GUI
	 */
	private final JFrame jFrame;

	/**
	 * A reference to the main GUI
	 */
	private final GUImain main;
	
	private final GUImainSettings settings;

	@Override
	public void windowOpened(WindowEvent e) {
	}

	// User is closing program
	@Override
	public void windowClosing(WindowEvent e) {

		if (main == null || main.getSaveManager().safeCheck(_("Closing C Compact")))
			updateAndExit(jFrame, settings);
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	public static void updateAndExit(JFrame jFrame, GUImainSettings settings) {

		// ...and saved
		System.out.println("[up to date]");
		settings.writeXMLsettings();

		System.exit(0);
	}

}
