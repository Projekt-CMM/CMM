package at.jku.ssw.cmm.gui.treetable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;

/**
 * This class basically controls the rendering of the tree table, its functionalities include:
 * <ul>
 * <li> Table and Tree have equal row heights </li>
 * <li> Table and Tree have equal heights </li>
 * <li> Table cells have the right background color if selected </li>
 * <li> Table objects of the tree must have an offset to the right, depending on the hierarchy </li>
 * </ul>
 * <br>
 * <i>NOTE: </i> This code has been adapted from a tutorial, see {@link http://www.hameister.org/JavaSwingTreeTable.html}
 * 
 * @author fabian
 *
 */
public class TreeTableCellRenderer extends JTree implements TableCellRenderer {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * The last line that has been changed
     */
    protected int visibleRow;
     
    private TreeTable treeTable;
     
    public TreeTableCellRenderer(TreeTable treeTable, TreeModel model) {
        super(model);
        super.setCellRenderer(new TreeRenderer());
        this.treeTable = treeTable;
        
        setRowHeight(getRowHeight()+1);
    }
 
    /**
     * Sets the height of the tree rows to the same as the height of the table rows
     */
    public void setRowHeight(int rowHeight) {
        if (rowHeight > 0) {
            super.setRowHeight(rowHeight);
            if (treeTable != null && treeTable.getRowHeight() != rowHeight) {
                treeTable.setRowHeight(getRowHeight()+1);
            }
        }
    }
 
    /**
     * Sets the tree to the same height as the table
     */
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, 0, w, treeTable.getHeight());
    }
 
    /**
     * Controls the offset of the nodes
     */
    public void paint(Graphics g) {
         
        g.translate(0, -visibleRow * getRowHeight());
        
        super.paint(g);
    }
     
    /**
     * @return The renderer for the cell with the right background color
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	
    	if( isSelected ){
			if( table.getValueAt(row, 0).toString().endsWith(")") )
				setBackground(new Color(0, 159, 153));
			
			else if( table.getValueAt(row, 1).toString().endsWith(""+DataNode.CHANGE_TAG) )
				setBackground(new Color(215, 200, 0));
			
			else if( table.getValueAt(row, 1).toString().endsWith(""+DataNode.READ_TAG) )
				setBackground(new Color(30, 180, 0));
			
			else if ( table.getValueAt(row, 2).toString().equals("undef") )
	    		setBackground(new Color(200, 200, 200));
			
			else
				setBackground(table.getSelectionBackground());
		}
		else{
			if( table.getValueAt(row, 0).toString().endsWith(")") )
				setBackground(Color.CYAN);
			
			else if( table.getValueAt(row, 1).toString().endsWith(""+DataNode.CHANGE_TAG) )
				setBackground(Color.YELLOW);
			
			else if( table.getValueAt(row, 1).toString().endsWith(""+DataNode.READ_TAG) )
				setBackground(Color.GREEN);
			
			else if ( table.getValueAt(row, 2).toString().equals("undef") )
	    		setBackground(new Color(240, 240, 240));
			
			else
				setBackground(table.getBackground());
		}
    	
        visibleRow = row;
        return this;
    }
}
