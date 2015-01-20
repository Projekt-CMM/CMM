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

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import at.jku.ssw.cmm.gui.treetable.var.TreeStructImageRenderer;
import at.jku.ssw.cmm.gui.treetable.var.VarDataNode;

/**
 * A jTree embedded in a table
 * <br>
 * <i>NOTE: </i> This code has been adapted from a tutorial, see {@link http://www.hameister.org/JavaSwingTreeTable.html}
 * 
 * @author fabian
 *
 */
public class TreeTable<TreeNode extends DataNode> extends JTable {
	 
	private static final long serialVersionUID = 1L;
	
	private TreeTableCellRenderer tree;
	private TreeTableDataModel<TreeNode> dataModel;
	private TreeTableModelAdapter<TreeNode> modelAdapter;
     
     
    public TreeTable( TreeTableDataModel<TreeNode> treeTableModel ){
        super();
        
        //Do not allow column reordering
        getTableHeader().setReorderingAllowed(false);
        
        //Do not show the table grid
        setShowGrid(false);
        
        this.setTreeModel(treeTableModel);
 
        //No spacing
        setIntercellSpacing(new Dimension(0, 0));
    }
    
    public void setTreeModel( TreeTableDataModel<TreeNode> treeTableModel ){
    	
    	int[] colWidth = new int[super.getColumnCount()];
    	TableCellRenderer[] renderer = new TableCellRenderer[super.getColumnCount()];
    	for( int i = 0; i < super.getColumnCount(); i++){
    		colWidth[i] = super.getColumnModel().getColumn(i).getWidth();
    		renderer[i] = super.getCellRenderer(0, i);
    	}
    	
    	this.dataModel = treeTableModel;
    	
    	//Create JTree
    	if( treeTableModel.getRoot() instanceof VarDataNode )
    		tree = new TreeTableCellRenderer(this, treeTableModel, new TreeStructImageRenderer());
    	else
    		tree = new TreeTableCellRenderer(this, treeTableModel, new TreeRenderer());
        //TODO tree.setFont(tree.getFont().deriveFont((float)this.main.getSettings().getVarSize()));
        
        this.modelAdapter = new TreeTableModelAdapter<>(treeTableModel, tree);
        super.setModel(this.modelAdapter);
         
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
        for( int i = 0; i < super.getColumnCount() && colWidth.length > 0; i++){
    		super.getColumnModel().getColumn(i).setPreferredWidth(colWidth[i]);
    		if( i > 0 )
    			super.getColumnModel().getColumn(i).setCellRenderer(renderer[i]);
    	}
        
        this.repaint();
    }
    
    public void updateTreeModel(){
    	
    	int[] colWidth = new int[super.getColumnCount()];
    	TableCellRenderer[] renderer = new TableCellRenderer[super.getColumnCount()];
    	for( int i = 0; i < super.getColumnCount(); i++){
    		colWidth[i] = super.getColumnModel().getColumn(i).getWidth();
    		renderer[i] = super.getCellRenderer(0, i);
    	}
    	
    	//Update tree table data
    	this.modelAdapter = new TreeTableModelAdapter<>(this.dataModel, tree);
        super.setModel(this.modelAdapter);
        
    	for( int i = 0; i < super.getColumnCount(); i++){
    		super.getColumnModel().getColumn(i).setPreferredWidth(colWidth[i]);
    		if( i > 0 )
    			super.getColumnModel().getColumn(i).setCellRenderer(renderer[i]);
    	}
        
        this.repaint();
    }
    
    public TreeTableDataModel<TreeNode> getTreeModel(){
    	return this.dataModel;
    }
    
    public TreeTableCellRenderer getCellRenderer(){
    	return this.tree;
    }
    
    public JTable getTable(){
    	return this;
    }
    
    public TreeTableModelAdapter<TreeNode> getModelAdapter(){
    	return this.modelAdapter;
    }
    
    public void updateFontSize(){
    	//super.setRowHeight(32);
    	//TODO super.setFont(super.getFont().deriveFont((float)this.main.getSettings().getVarSize()));
    	this.updateTreeModel();
    }

}
