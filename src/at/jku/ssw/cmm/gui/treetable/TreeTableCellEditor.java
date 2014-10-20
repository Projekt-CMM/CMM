package at.jku.ssw.cmm.gui.treetable;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;

/**
 * Forwards a double click in the first column to the tree so that a node is expanded when the user clicks at it.
 * <br>
 * <i>NOTE: </i> This code has been adapted from a tutorial, see {@link http://www.hameister.org/JavaSwingTreeTable.html}
 * 
 * @author fabian
 *
 */
public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final JTree tree;
    private final JTable table;
 
    public TreeTableCellEditor(JTree tree, JTable table) {
        this.tree = tree;
        this.table = table;
    }
 
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
    	if( c == 0 )
        	return this.tree;
        else
        	return this.table;
    }
 
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            int colunm1 = 0;
            MouseEvent me = (MouseEvent) e;
            MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(), me.getX() - table.getCellRect(0, colunm1, true).x, me.getY(), 2, me.isPopupTrigger());
            tree.dispatchEvent(newME);
        }
        return false;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
 
}
