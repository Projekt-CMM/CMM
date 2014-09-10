package at.jku.ssw.cmm.gui.treetable;

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
    static protected String[] columnNames = { "Name", "Type", "Value" };
 
    // Column types
    static protected Class<?>[] columnTypes = { TreeTableModel.class, String.class, Integer.class };
 
    public TreeTableDataModel(DataNode rootNode) {
        super(rootNode);
        root = rootNode;
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
        return true; // Important to activate TreeExpandListener
    }
 
    public void setValueAt(Object aValue, Object node, int column) {
    }
 
}
