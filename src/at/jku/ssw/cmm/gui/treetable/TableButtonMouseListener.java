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
 
package at.jku.ssw.cmm.gui.treetable;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.treetable.context.InitContextMenu;
import at.jku.ssw.cmm.gui.treetable.var.VarDataNode;

public class TableButtonMouseListener implements MouseListener {
	
	public TableButtonMouseListener(GUImain main, TreeTable<?> t) {
		this.main = main;
		this.treeTable = t;
	}
	
	private final GUImain main;
	private final TreeTable<?> treeTable;

	private void forwardEventToButton(MouseEvent e, int row, int column) {

		Object value;
		JButton button;
		MouseEvent buttonEvent;

		if (row >= this.treeTable.getTable().getRowCount() || row < 0
				|| column >= this.treeTable.getTable().getColumnCount() || column < 0)
			return;
		
		value = this.treeTable.getTable().getValueAt(row, column);
		
		if (!(value instanceof JButton))
			return;

		button = (JButton) value;

		buttonEvent = (MouseEvent) SwingUtilities.convertMouseEvent(this.treeTable.getTable(), e, button);
		button.dispatchEvent(buttonEvent);
		// This is necessary so that when a button is pressed and released
		// it gets rendered properly. Otherwise, the button may still appear
		// pressed down when it has been released.
		this.treeTable.getTable().repaint();
	}

	public void mouseClicked(MouseEvent e) {
		forwardEventToButton(e,
				this.treeTable.getTable().rowAtPoint(e.getPoint()),
				this.treeTable.getTable().columnAtPoint(e.getPoint()));
	}

	public void mouseEntered(MouseEvent e) {
		
		forwardEventToButton(e,
				this.treeTable.getTable().rowAtPoint(e.getPoint()),
				this.treeTable.getTable().columnAtPoint(e.getPoint()));
	}

	public void mouseExited(MouseEvent e) {
		
		forwardEventToButton(e,
				this.treeTable.getTable().rowAtPoint(e.getPoint()),
				this.treeTable.getTable().columnAtPoint(e.getPoint()));
	}

	public void mousePressed(MouseEvent e) {
		
		int row = this.treeTable.getTable().rowAtPoint(e.getPoint());
		int col = this.treeTable.getTable().columnAtPoint(e.getPoint());
		
		// Right mouse clicked
		if (SwingUtilities.isRightMouseButton(e)
				&& this.treeTable.getTreeModel().getRoot() instanceof VarDataNode) {
			if (row >= 0 && col >= 0) {
				
				String name = (String)this.treeTable.getTable().getValueAt(row, 0);
				
				VarDataNode node = (VarDataNode) this.treeTable.getModelAdapter().nodeForRow(row);
				
				if( node.getDeclarationLine() >= 0 || node.getCallLine() >= 0 ){
					InitContextMenu.initContextMenu(main, name, node.getDeclarationLine(), node.getCallLine()).show(e.getComponent(), e.getX(), e.getY());
				}
			}

		} else {
			System.out.println("mouse entered column");
			
			//Check in clicked reference TODO add reference highlighting
			if( this.treeTable.getTable().getValueAt(row, 2) instanceof String && 
					((String)this.treeTable.getTable().getValueAt(row, 2)).equals(_("reference")) ){
				
				VarDataNode node = (VarDataNode) this.treeTable.getModelAdapter().nodeForRow(row);
				
				System.out.println("mouse entered reference: " + node.getName() + " on " + node.getAddress());
			}
		}
		
		forwardEventToButton(e,	row, col);
	}

	public void mouseReleased(MouseEvent e) {
		forwardEventToButton(e,
				this.treeTable.getTable().rowAtPoint(e.getPoint()),
				this.treeTable.getTable().columnAtPoint(e.getPoint()));
	}
}
