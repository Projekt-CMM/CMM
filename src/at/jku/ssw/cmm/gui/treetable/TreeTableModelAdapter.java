package at.jku.ssw.cmm.gui.treetable;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/**
 * Data model for the table of the tree table. Also initializes the tree expansion listener.
 * <br>
 * <i>NOTE: </i> This code has been adapted from a tutorial, see {@link http://www.hameister.org/JavaSwingTreeTable.html}
 * 
 * @author fabian
 *
 */
public class TreeTableModelAdapter extends AbstractTableModel {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JTree tree;
	AbstractTreeTableModel treeTableModel;

	public TreeTableModelAdapter(AbstractTreeTableModel treeTableModel, JTree tree) {
		this.tree = tree;
       	this.treeTableModel = treeTableModel;

       	tree.addTreeExpansionListener(new TreeExpansionListener() {
    	   public void treeExpanded(TreeExpansionEvent event) {
               	fireTableDataChanged();
           	}

           	public void treeCollapsed(TreeExpansionEvent event) {
           		fireTableDataChanged();
           	}
       	});
	}


    
   public int getColumnCount() {
       return treeTableModel.getColumnCount();
   }

   public String getColumnName(int column) {
       return treeTableModel.getColumnName(column);
   }

   public Class<?> getColumnClass(int column) {
       return treeTableModel.getColumnClass(column);
   }

   public int getRowCount() {
       return tree.getRowCount();
   }

   public Object nodeForRow(int row) {
       TreePath treePath = tree.getPathForRow(row);
       return treePath.getLastPathComponent();
   }

   public Object getValueAt(int row, int column) {
       return treeTableModel.getValueAt(nodeForRow(row), column);
   }

   public boolean isCellEditable(int row, int column) {
       return treeTableModel.isCellEditable(nodeForRow(row), column);
   }

   public void setValueAt(Object value, int row, int column) {
       treeTableModel.setValueAt(value, nodeForRow(row), column);
   }
}
