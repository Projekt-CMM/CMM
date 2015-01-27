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

import static at.jku.ssw.cmm.gettext.Language._;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import at.jku.ssw.cmm.gui.GUImain;

public class InitContextMenu {

	public static JPopupMenu initContextMenu( GUImain main, String name, int decl, int call ) {
		
		JPopupMenu menu = new JPopupMenu("hello");
		
		if( decl > -1 ) {
			JMenuItem id = new JMenuItem(_("Jump to Declaration"));
			menu.add(id);
			id.addActionListener(new ContextMenuListener(main, name, decl));
		}
		
		if( call > -1 ) {
			JMenuItem ic = new JMenuItem(_("Jump to Call"));
			menu.add(ic);
			ic.addActionListener(new ContextMenuListener(main, name, call));
		}
		
		menu.repaint();
		
		return menu;
	}
}
