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
 
package at.jku.ssw.cmm.gui.treetable.context;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import at.jku.ssw.cmm.gui.GUImain;

public class ContextMenuListener implements ActionListener {

	public ContextMenuListener( GUImain main, String name, int line ){
		this.main = main;
		this.name = name;
		this.line = line;
	}
	
	private final GUImain main;
	private final String name;
	private final int line;
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		System.out.println("Action performed: " + name + ", " + line);
		this.main.getLeftPanel().highlightSourceCode(this.line);
	}
}
