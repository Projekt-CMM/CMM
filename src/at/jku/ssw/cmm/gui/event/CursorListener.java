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

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class CursorListener implements MouseMotionListener {
	
	public CursorListener( RSyntaxTextArea sourcePane ){
		this.sourcePane = sourcePane;
	}
	
	private final RSyntaxTextArea sourcePane;

	@Override
	public void mouseDragged(MouseEvent e) {
		
		final int x = e.getX();
        final int y = e.getY();
        // only display a hand if the cursor is over the items
        if (sourcePane != null) {
        	if( sourcePane.contains(x, y))
        		sourcePane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        	else
        		sourcePane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }	
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
		
	}
}
