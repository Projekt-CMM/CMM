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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePopup extends JPanel {

	private static final long serialVersionUID = -892555477960117047L;

	public static final int AUTO = 0;
	public static final int NORTH = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public static final int EAST = 4;
	
	public static final int EDGE_OFFSET = 10;
	
	public ImagePopup( int x, int y, int w, int h, int centerX, int centerY, int orientation ) {
		super();
		
		this.centerX = centerX-x;
		this.centerY = centerY-y;//+(orientation==SOUTH?10:0);//;+(orientation==NORTH||orientation==SOUTH?(orientation==NORTH?+EDGE_OFFSET:-EDGE_OFFSET):0);
		this.orientation = orientation;
		
		System.out.println("Orientation: " + orientation + ", cx: " + centerX + ", cy: " + centerY);

		super.setBorder(BorderFactory.createRaisedBevelBorder());
		super.setBackground(Color.WHITE);
		super.setLayout(new BorderLayout());
        setVisible(true);
        
        switch( orientation ) {
        case NORTH:
        	super.setBounds(x, y, w, h);
        	this.image = new ImageIcon("images/edge_n.png").getImage();
        	break;
        case SOUTH:
        	super.setBounds(x, y, w, h);
        	this.image = new ImageIcon("images/edge_s.png").getImage();
        	break;
        case WEST:
        	super.setBounds(x, y, w, h);
        	this.image = new ImageIcon("images/edge_w.png").getImage();
        	break;
        case EAST:
        	super.setBounds(x, y, w, h);
        	this.image = new ImageIcon("images/edge_e.png").getImage();
        	break;
        default:
        	this.image = null;
        	break;
        }
        
    }

    private final Image image;
    
    private final int centerX;
    private final int centerY;
    
    private final int orientation;

    @Override
    public void paintComponent(Graphics g) {
        
    	switch( this.orientation ) {
    	case NORTH:
    		g.setClip(g.getClipBounds().x, g.getClipBounds().y-10, g.getClipBounds().width, g.getClipBounds().height+10);
    		break;
    	case SOUTH:
    		g.setClip(g.getClipBounds().x, g.getClipBounds().y, g.getClipBounds().width+10, g.getClipBounds().height+10);
    		break;
    	case WEST:
    		g.setClip(g.getClipBounds().x-10, g.getClipBounds().y, g.getClipBounds().width+10, g.getClipBounds().height);
    		break;
    	case EAST:
    		g.setClip(g.getClipBounds().x, g.getClipBounds().y, g.getClipBounds().width+10, g.getClipBounds().height);
    		break;
    	}
    	super.paintComponent(g);
    	super.paintBorder(g);
    	
    	switch( this.orientation ) {
    	case NORTH:
    		g.drawImage(image, centerX-10, centerY, image.getWidth(null), image.getHeight(null), this);
    		break;
    	case SOUTH:
    		g.drawImage(image, centerX-10, centerY-15, image.getWidth(null), image.getHeight(null), this);
    		break;
    	case WEST:
    		g.drawImage(image, centerX, centerY-10, image.getWidth(null), image.getHeight(null), this);
    		break;
    	case EAST:
    		g.drawImage(image, centerX-15, centerY-10, image.getWidth(null), image.getHeight(null), this);
    		break;
    	}
    }
    
    @Override
    public void paintBorder(Graphics g) {
    	
    }
}
