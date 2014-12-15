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

import javax.swing.JButton;

/**
 * Defines the complete data model as well as column names and column data types
 * <br>
 * <i>NOTE: </i> This code has been adapted from a tutorial, see {@link http://www.hameister.org/JavaSwingTreeTable.html}
 * 
 * @author fabian
 *
 */
public class TreeTableDataModel extends AbstractTreeTableModel {
    // Column names
    static protected String[] columnNames = { _("Name"), _("Type"), _("Value") };
 
    // Column types
    static protected Class<?>[] columnTypes = { TreeTableModel.class, String.class, Object.class };
 
    public TreeTableDataModel(DataNode rootNode) {
        super(rootNode);
        root = rootNode;
    }
    
    public DataNode getRoot(){
    	return root;
    }

	public Object getChild(Object parent, int index) {
        return ((DataNode) parent).getChildren().get(index);
    }
 
 
    public int getChildCount(Object parent) {
        return ((DataNode) parent).getChildren().size();
    }
 
 
    public int getColumnCount() {
        return columnNames.length;
    }
 
 
    public String getColumnName(int column) {
        return columnNames[column];
    }
 
 
    public Class<?> getColumnClass(int column) {
        return columnTypes[column];
    }
 
    public Object getValueAt(Object node, int column) {
        switch (column) {
        case 0:
            return ((DataNode) node).getName();
        case 1:
            return ((DataNode) node).getType();
        case 2:
            return ((DataNode) node).getValue();
        default:
            break;
        }
        return null;
    }
 
    public boolean isCellEditable(Object node, int column) {
        //First column must be editable, so that the user can open a jTree node
    	if( column == 0 || (column == 2 && node instanceof JButton) )
        	return true;
    	//Other cells are not editable
        else
        	return false;
    }
 
    public void setValueAt(Object aValue, Object node, int column) {
    }
 
}
