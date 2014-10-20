package at.jku.ssw.cmm.gui.treetable;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/**
 * This class saves the root node and checks if there are child nodes available.
 * The event listeners forward structure changes to the tree.
 * <br>
 * <i>NOTE: </i> This code has been adapted from a tutorial, see {@link http://www.hameister.org/JavaSwingTreeTable.html}
 * 
 * @author fabian
 *
 */
public abstract class AbstractTreeTableModel implements TreeTableModel {
    protected DataNode root;
    protected EventListenerList listenerList = new EventListenerList();
 
    private static final int CHANGED = 0;
    private static final int INSERTED = 1;
    private static final int REMOVED = 2;
    private static final int STRUCTURE_CHANGED = 3;
 
    public AbstractTreeTableModel(DataNode root) {
        this.root = root;
    }
 
    public DataNode getRoot() {
        return root;
    }
 
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }
 
    public void valueForPathChanged(TreePath path, Object newValue) {
    }
 
    /**
     * Usually not called
     */
    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }
 
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }
 
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }
 
    private void fireTreeNode(int changeType, Object source, Object[] path, int[] childIndices, Object[] children) {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = new TreeModelEvent(source, path, childIndices, children);
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
 
                switch (changeType) {
                case CHANGED:
                    ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
                    break;
                case INSERTED:
                    ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
                    break;
                case REMOVED:
                    ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
                    break;
                case STRUCTURE_CHANGED:
                    ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
                    break;
                default:
                    break;
                }
 
            }
        }
    }
 
    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(CHANGED, source, path, childIndices, children);
    }
 
    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(INSERTED, source, path, childIndices, children);
    }
 
    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(REMOVED, source, path, childIndices, children);
    }
 
    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeNode(STRUCTURE_CHANGED, source, path, childIndices, children);
    }
 
}
