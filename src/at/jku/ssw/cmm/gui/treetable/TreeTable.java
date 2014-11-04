package at.jku.ssw.cmm.gui.treetable;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTable;

import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.gui.utils.JTableButtonMouseListener;
import at.jku.ssw.cmm.gui.utils.JTableButtonRenderer;

/**
 * A jTree embedded in a table
 * <br>
 * <i>NOTE: </i> This code has been adapted from a tutorial, see {@link http://www.hameister.org/JavaSwingTreeTable.html}
 * 
 * @author fabian
 *
 */
public class TreeTable extends JTable {
	 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final GUImainMod main;
	
	private TreeTableCellRenderer tree;
	private TreeTableDataModel dataModel;
	private final JTableButtonRenderer buttonRenderer;
	private TreeTableModelAdapter modelAdapter;
     
     
    public TreeTable( GUImainMod main, TreeTableDataModel treeTableModel ){
        super();
        
        this.main = main;
        
        //Initialize button renderer
        this.buttonRenderer = new JTableButtonRenderer(super.getDefaultRenderer(JButton.class));
 
        //Initialize tree
        this.setTreeModel(treeTableModel);
        
        //Do not show the table grid
        setShowGrid(false);
 
        //No spacing
        setIntercellSpacing(new Dimension(0, 0));
    }
    
    public void setTreeModel( TreeTableDataModel treeTableModel ){
    	
    	this.dataModel = treeTableModel;
    	
    	//Create JTree
        tree = new TreeTableCellRenderer(this, treeTableModel);
        
        
        this.modelAdapter = new TreeTableModelAdapter(treeTableModel, tree);
        super.setModel(this.modelAdapter);
        super.getColumn("Value").setCellRenderer(this.buttonRenderer);
        super.getColumn("Type").setCellRenderer(this.buttonRenderer);
        this.addMouseListener(new JTableButtonMouseListener(this.main, this));
         
        //Selection of tree and table at once
        TreeTableSelectionModel selectionModel = new TreeTableSelectionModel();
        
        // -> tree
        tree.setSelectionModel(selectionModel);
        
        // -> table
        setSelectionModel(selectionModel.getListSelectionModel());
 
        tree.setRootVisible(true); 
        
        //Renderer for the tree
        setDefaultRenderer(TreeTableModel.class, tree);
        
        //Editor for the tree table
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor(tree, this));
        
        this.repaint();
    }
    
    public TreeTableDataModel getTreeModel(){
    	return this.dataModel;
    }
    
    public void updateTreeModel(){
        
    	this.modelAdapter = new TreeTableModelAdapter(this.dataModel, tree);
        super.setModel(this.modelAdapter);
    	super.getColumn("Value").setCellRenderer(this.buttonRenderer);
    	super.getColumn("Type").setCellRenderer(this.buttonRenderer);
        
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

}
