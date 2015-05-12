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
 
package at.jku.ssw.cmm.launcher;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.properties.GUImainSettings;

public class FindProfileListener extends MouseAdapter{

	private final JFrame jFrame;
	private final GUImainSettings settings;
	

	public FindProfileListener( JFrame jFrame, GUImainSettings settings ) {
		this.jFrame = jFrame;
		this.settings = settings;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		try {
			
			settings.setProfile(GUIProfileManager.selectProfile());	
		    settings.writeXMLsettings();

			jFrame.dispose();
			
			GUImain app = new GUImain(settings);
			app.start(false);
			
		} catch (ProfileSelectionException e) {
			
		}

		
	}

}
