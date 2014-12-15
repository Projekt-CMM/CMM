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
 
package at.jku.ssw.cmm.gui.init;

import java.awt.Dimension;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

public class JInputDataPane extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7416447987376490746L;
	
	boolean wrap = true;

	public JInputDataPane() {
		super();
	}

	public JInputDataPane(boolean wrap) {
		super();
		this.wrap = wrap;
	}

	public JInputDataPane(StyledDocument doc) {
		super(doc);
	}

	public boolean getScrollableTracksViewportWidth() {
		if (wrap)
			return super.getScrollableTracksViewportWidth();
		else
			return false;
	}

	public void setSize(Dimension d) {
		if (!wrap) {
			if (d.width < getParent().getSize().width)
				d.width = getParent().getSize().width;
		}
		super.setSize(d);
	}

	/**
	 * Sets the line-wrapping policy of the JInputDataPane
	 * By default this property is true
	 * @param wrap
	 */
	void setLineWrap(boolean wrap) {
		setVisible(false);
		this.wrap = wrap;
		setVisible(true);
	}

}
