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
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.text.JTextComponent;

/**
 * This listener monitors the mouse cursor and changes its look if it is
 * over a defined object (eg. text cursor for text fields)
 * 
 * @author fabian
 */
public class CursorListener implements MouseListener {
	
	/**
	 * This listener monitors the mouse cursor and changes its look if it is
	 * over a defined object (eg. text cursor for text fields)
	 * 
	 * @param frame The JFrame of the main GUI window
	 * @param component The component whichs mouse events are monitored
	 * @param cursor The cursor we want to see when the mouse is over "component"
	 */
	public CursorListener( JFrame frame, Object component, Cursor cursor ){
		this.component = component;
		this.frame = frame;
		this.cursor1 = cursor;
		this.cursor2 = null;
	}
	
	public CursorListener( JFrame frame, Object component, Cursor cursor1, Cursor cursor2 ){
		this.component = component;
		this.frame = frame;
		this.cursor1 = cursor1;
		this.cursor2 = cursor2;
	}
	
	/**
	 * The JFrame of the main GUI window
	 */
	private final JFrame frame;
	
	/**
	 * The component whichs mouse events are monitored
	 */
	private final Object component;
	
	/**
	 * The cursor we want to see when the mouse is over "component"
	 */
	private final Cursor cursor1;
	private final Cursor cursor2;

	@Override
	public void mouseEntered(MouseEvent e) {
		// Disable custom cursor if component is text element ...
		if( this.component != null && this.component instanceof JTextComponent ) {
			JTextComponent c = (JTextComponent)this.component;
			
			// ... and it is not editable
			if( !c.isEditable() )
				return;
		}
		
		// Two different cursors for SplitPane
		if( this.component != null && this.component instanceof JSplitPane ) {
			JSplitPane sp = (JSplitPane)this.component;
			
			if( sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
				this.frame.setCursor(this.cursor1);
			else
				this.frame.setCursor(this.cursor2);
		}
		
		// Set default custom cursor
		else
			this.frame.setCursor(this.cursor1);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}
