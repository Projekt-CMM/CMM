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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImagePreviewPanel extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private int width, height;
	private ImageIcon icon;
	private Image image;
	private static final int ACCSIZE = 155;
	private Color bg;
	
	private JLabel label = new JLabel("");

	public ImagePreviewPanel() {
		setPreferredSize(new Dimension(ACCSIZE, -1));
		bg = getBackground();
		
		this.add(label);

	}

	public void propertyChange(PropertyChangeEvent e) {
		String propertyName = e.getPropertyName();

		// Make sure we are responding to the right event.
		if (propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
			File selection = (File) e.getNewValue();
			String name;

			if (selection == null)
				return;
			else
				name = selection.getAbsolutePath();

			if ((name != null) && (name.toLowerCase().endsWith(".jpg")
					|| name.toLowerCase().endsWith(".jpeg")
					|| name.toLowerCase().endsWith(".gif")
					|| name.toLowerCase().endsWith(".png"))) {
				icon = new ImageIcon(name);
				
				label.setText(selection.getName());
				
				image = icon.getImage();
				scaleImage();
				repaint();
			}
		}
	}

	private void scaleImage() {
		width = image.getWidth(this);
		height = image.getHeight(this);
		double ratio = 1.0;

		if (width >= height) {
			ratio = (double) (ACCSIZE - 5) / width;
			width = ACCSIZE - 5;
			height = (int) (height * ratio);
		} else {
			if (getHeight() > 150) {
				ratio = (double) (ACCSIZE - 5) / height;
				height = ACCSIZE - 5;
				width = (int) (width * ratio);
			} else {
				ratio = (double) getHeight() / height;
				height = getHeight();
				width = (int) (width * ratio);
			}
		}

		image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
	}

	public void paintComponent(Graphics g) {
		g.setColor(bg);
		g.fillRect(0, 0, ACCSIZE, getHeight());
		g.drawImage(image, getWidth() / 2 - width / 2 + 5, getHeight() / 2
				- height / 2, this);
	}

}
