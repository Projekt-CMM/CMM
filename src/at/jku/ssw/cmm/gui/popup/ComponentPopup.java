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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.gui.GUImain;

public class ComponentPopup {

	/*public static void createPopUp( GUImain main, JComponent component, int x, int y, String path ){
		
		ImagePopup popup = new ImagePopup(path);
		//popup.setBounds(main.getGlassPane().getMousePosition().x-271, main.getGlassPane().getMousePosition().y-151, 310, 151);
		popup.setBounds(x-271, y-151, 310, 151);
		
		//JTextArea ta = new JTextArea( text );
		JScrollPane scrollPane = new JScrollPane(component);
		scrollPane.setBounds(10, 10, 298, 117);
		scrollPane.setPreferredSize(new Dimension(298, 117));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		popup.add(scrollPane);
		
		main.invokePopup(popup, main.getGlassPane().getMousePosition().x-271, main.getGlassPane().getMousePosition().y-151, 310, 143);
	}*/
	
	/*public static void createPopUp( GUImain main, JComponent component, int w, int h ){
		createPopUp( main, component, main.getGlassPane().getMousePosition().x, main.getGlassPane().getMousePosition().y, w, h );
	}*/
	
	public static void createPopUp( GUImain main, JComponent component, int x, int y, int w, int h, int orientation ){
		
		ImagePopup popup = null;
		int x_abs=0;
		int y_abs=0;
		
		System.out.println("x: " + x + ", y: " + y);
		
		switch( orientation ) {
		case ImagePopup.NORTH:
			popup = new ImagePopup(x_abs=x-w/2, y_abs=y+ImagePopup.EDGE_OFFSET, w, h, x, y, orientation);
			break;
		case ImagePopup.SOUTH:
			popup = new ImagePopup(x_abs=x-w/2, y_abs=y-h-ImagePopup.EDGE_OFFSET, w, h, x, y, orientation);
			break;
		case ImagePopup.WEST:
			popup = new ImagePopup(x_abs=x+ImagePopup.EDGE_OFFSET, y_abs=y-h/2, w, h, x, y, orientation);
			break;
		case ImagePopup.EAST:
			popup = new ImagePopup(x_abs=x-w-ImagePopup.EDGE_OFFSET, y_abs=y-h/2, w, h, x, y, orientation);
			break;
		}
		
		JScrollPane scrollPane = new JScrollPane(component);
		scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		popup.add(scrollPane, BorderLayout.CENTER);
		
		main.invokePopup(popup, x_abs, y_abs, w, h);
	}
}
