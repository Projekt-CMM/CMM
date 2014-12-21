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

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTable;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.utils.TableButtonMouseListener;
import at.jku.ssw.cmm.gui.utils.TableButtonRenderer;

/**
 * A jTree embedded in a table
 * <br>
 * <i>NOTE: </i> This code has been adapted from a tutorial, see {@link http://www.hameister.org/JavaSwingTreeTable.html}
 * 
 * @author fabian
 *
 */
public class TreeTable extends JTable {
	 
	private static final long serialVersionUID = 1L;
	
	private final GUImain main;
	
	private TreeTableCellRenderer tree;
	private TreeTableDataModel dataModel;
	private final TableButtonRenderer buttonRenderer;
	private TreeTableModelAdapter modelAdapter;
     
     
    public TreeTable( GUImain main, TreeTableDataModel treeTableModel ){
        super();
        
        super.getTableHeader().setToolTipText("<html><b>" + _("Variable table columns") + "</b><br>" +
        		_("You can change the width a column by<br>dragging and sliding its border.") + "</html>");
        
        this.main = main;
        
        //Initialize button renderer
        this.buttonRenderer = new TableButtonRenderer(super.getDefaultRenderer(JButton.class));
 
        //Initialize tree
        this.setTreeModel(treeTableModel);
        
        //Do not show the table grid
        setShowGrid(false);
 
        //No spacing
        setIntercellSpacing(new Dimension(0, 0));
        
        //No column swapping / reordering
        super.getTableHeader().setReorderingAllowed(false);
        
        //Update font size
        this.updateFontSize();
    }
    
    public void setTreeModel( TreeTableDataModel treeTableModel ){
    	
    	int width1 = 0;
    	int width2 = 0;
    	int width3 = 0;
    	
    	//Save old column width
    	if( this.modelAdapter != null ){
	    	width1 = super.getColumn(_("Name")).getWidth();
	    	width2 = super.getColumn(_("Type")).getWidth();
	    	width3 = super.getColumn(_("Value")).getWidth();
    	}
    	
    	this.dataModel = treeTableModel;
    	
    	//Create JTree
        tree = new TreeTableCellRenderer(this, treeTableModel, this.main);
        tree.setFont(tree.getFont().deriveFont((float)this.main.getSettings().getVarSize()));
        
        this.modelAdapter = new TreeTableModelAdapter(treeTableModel, tree);
        super.setModel(this.modelAdapter);
        super.getColumn(_("Value")).setCellRenderer(this.buttonRenderer);
        super.getColumn(_("Type")).setCellRenderer(this.buttonRenderer);
        this.addMouseListener(new TableButtonMouseListener(this.main, this));
         
        //Selection of tree and table at once
        TreeTableSelectionModel selectionModel = new TreeTableSelectionModel();
        
        // -> tree
        tree.setSelectionModel(selectionModel);
        
        // -> table
        setSelectionModel(selectionModel.getListSelectionModel());
 
        tree.setRootVisible(true); 
        
        //Renderer for the the table
        setDefaultRenderer(TreeTableModel.class, tree);
        
        //Editor for the tree table
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor(tree, this));
        
        //Set columns to old width
        if( width1 > 0 && width2 > 0 && width3 > 0 ){
        	super.getColumn(_("Name")).setPreferredWidth(width1);
        	super.getColumn(_("Type")).setPreferredWidth(width2);
        	super.getColumn(_("Value")).setPreferredWidth(width3);
        }
        
        this.repaint();
    }
    
    public TreeTableDataModel getTreeModel(){
    	return this.dataModel;
    }
    
    public void updateTreeModel(){
    	
    	//Save old column width
    	int width1 = super.getColumn(_("Name")).getWidth();
    	int width2 = super.getColumn(_("Type")).getWidth();
    	int width3 = super.getColumn(_("Value")).getWidth();
    	
    	//Update tree table data
    	this.modelAdapter = new TreeTableModelAdapter(this.dataModel, tree);
        super.setModel(this.modelAdapter);
        
        tree.setFont(tree.getFont().deriveFont((float)this.main.getSettings().getVarSize()));
        
        //Re-initialize listeners
    	super.getColumn(_("Value")).setCellRenderer(this.buttonRenderer);
    	super.getColumn(_("Type")).setCellRenderer(this.buttonRenderer);
    	
    	//Set columns to old width
    	super.getColumn(_("Name")).setPreferredWidth(width1);
    	super.getColumn(_("Type")).setPreferredWidth(width2);
    	super.getColumn(_("Value")).setPreferredWidth(width3);
        
        this.repaint();
    }
    
    public void reset(){
    	
    	TreeTableDataModel empty = new TreeTableDataModel(new DataNode("", "", "", new ArrayList<DataNode>(), -1, -1));
    	
    	this.setTreeModel(empty);
    }
    
    public TreeTableCellRenderer getCellRenderer(){
    	return this.tree;
    }
    
    public JTable getTable(){
    	return this;
    }
    
    public TreeTableModelAdapter getModelAdapter(){
    	return this.modelAdapter;
    }
    
    public void updateFontSize(){
    	//super.setRowHeight(32);
    	super.setFont(super.getFont().deriveFont((float)this.main.getSettings().getVarSize()));
    	this.updateTreeModel();
    }

}
