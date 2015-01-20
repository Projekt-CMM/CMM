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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import at.jku.ssw.cmm.gui.treetable.var.VarDataNode;

public class TableButtonRenderer implements TableCellRenderer {
	private TableCellRenderer defaultRenderer;

	public TableButtonRenderer(TableCellRenderer renderer) {
		defaultRenderer = renderer;
	}
	  
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component c = (Component)defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if(value instanceof Component)
			return (Component)value;
		
		else if( isSelected ){
			if( table.getValueAt(row, 0).toString().endsWith(")") )
				c.setBackground(new Color(0, 159, 153));
			
			else if( table.getValueAt(row, 1).toString().endsWith(""+VarDataNode.CHANGE_TAG) )
				c.setBackground(new Color(215, 200, 0));
			
			else if( table.getValueAt(row, 1).toString().endsWith(""+VarDataNode.READ_TAG) )
				c.setBackground(new Color(30, 180, 0));
			
			else if ( table.getValueAt(row, 2).toString().equals("undef") )
	    		c.setBackground(new Color(200, 200, 200));
			
			else
				c.setBackground(table.getSelectionBackground());
		}
		else{
			if( table.getValueAt(row, 0).toString().endsWith(")") )
				c.setBackground(Color.CYAN);
			
			else if( table.getValueAt(row, 1).toString().endsWith(""+VarDataNode.CHANGE_TAG) )
				c.setBackground(Color.YELLOW);
			
			else if( table.getValueAt(row, 1).toString().endsWith(""+VarDataNode.READ_TAG) )
				c.setBackground(Color.GREEN);
			
			else if ( table.getValueAt(row, 2).toString().equals("undef") )
	    		c.setBackground(new Color(240, 240, 240));
			
			else
				c.setBackground(table.getBackground());
		}
		
		return c;
	}
}
