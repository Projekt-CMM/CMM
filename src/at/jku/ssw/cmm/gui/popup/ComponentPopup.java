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

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.gui.GUImain;

public class ComponentPopup {
	
	public static void createPopUp( GUImain main, JComponent component, int x, int y, int w, int h, int orientation ) {
		createPopUp(main, component, x, y, w, h, orientation, 0.5);
	}

	public static void createPopUp( GUImain main, JComponent component, int x, int y, int w, int h, int orientation, double weight ){
		
		ImagePopup popup = null;
		int x_abs=0;
		int y_abs=0;
		
		DebugShell.out(State.LOG, Area.GUI, "Creating popup on x: " + x + ", y: " + y);
		
		switch( orientation ) {
		case ImagePopup.NORTH:
			popup = new ImagePopup(x_abs=x-(int)(w*weight), y_abs=y+ImagePopup.EDGE_OFFSET, w, h, x, y, orientation);
			break;
		case ImagePopup.SOUTH:
		default:
			popup = new ImagePopup(x_abs=x-(int)(w*weight), y_abs=y-h-ImagePopup.EDGE_OFFSET, w, h, x, y, orientation);
			break;
		case ImagePopup.WEST:
			popup = new ImagePopup(x_abs=x+ImagePopup.EDGE_OFFSET, y_abs=y-(int)(w*weight), w, h, x, y, orientation);
			break;
		case ImagePopup.EAST:
			popup = new ImagePopup(x_abs=x-w-ImagePopup.EDGE_OFFSET, y_abs=y-(int)(w*weight), w, h, x, y, orientation);
			break;
		}
		
		JScrollPane scrollPane = new JScrollPane(component);
		scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		popup.add(scrollPane);//, BorderLayout.CENTER);
		
		main.invokePopup(popup, x_abs, y_abs, w, h);
	}
}
