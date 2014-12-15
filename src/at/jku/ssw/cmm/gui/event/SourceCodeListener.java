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

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import at.jku.ssw.cmm.gui.GUImain;

/**
 * Listener for changes in the source code text area
 * 
 * @author fabian
 *
 */
public class SourceCodeListener implements DocumentListener {
	
	/**
	 * Listener for changes in the source code text area
	 * 
	 * @param master A reference to the main GUI
	 */
	public SourceCodeListener( GUImain master ){
		this.master = master;
	}
	
	/**
	 * A reference to the main GUI
	 */
	private final GUImain master;

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		
		//Mark file as changed if code is typed, inserted or deleted
		master.setFileChanged();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {}

	@Override
	public void removeUpdate(DocumentEvent arg0) {}

}
