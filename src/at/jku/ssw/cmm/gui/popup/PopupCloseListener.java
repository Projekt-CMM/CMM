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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PopupCloseListener implements MouseListener {
	
	public PopupCloseListener( JFrame frame, JPanel glassPane, JPanel target, int x, int y, int w, int h ){
		this.jFrame = frame;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.glassPane = glassPane;
		this.target = target;
	}
	
	private final JFrame jFrame;
	private final int x, y, w, h;
	private final JPanel glassPane;
	private final JPanel target;

	@Override
	public void mouseClicked(MouseEvent e) {

		//Mouse click outside popup -> close and remove popup and this listener
		if( !( e.getX() >= x && e.getX() <= x+w && e.getY() >= y && e.getY() <= y+h ) ){
			this.glassPane.remove(this.target);
			this.glassPane.removeMouseListener(this);
			this.glassPane.validate();
			this.glassPane.repaint();
			
			this.glassPane.setVisible(false);
		
			Point containerPoint = e.getPoint();
	
	        // find the component that under this point
	        Component component = SwingUtilities.getDeepestComponentAt(
	                    jFrame,
	                    containerPoint.x,
	                    containerPoint.y);
	
	        // return if nothing was found
	        if (component == null) {
	            return;
	        }
	
	        // convert point relative to the target component
	        Point componentPoint = SwingUtilities.convertPoint(
	            jFrame,
	            e.getPoint(),
	            component);
	
	        // redispatch the event
	        component.dispatchEvent(new MouseEvent(component,
	            e.getID(),
	            e.getWhen(),
	            e.getModifiers(),
	            componentPoint.x,
	            componentPoint.y,
	            e.getClickCount(),
	            e.isPopupTrigger()));
	        
	        this.glassPane.setVisible(true);
		}
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
