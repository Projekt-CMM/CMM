package at.jku.ssw.cmm.gui.treetable;

import java.util.Stack;

import javax.swing.tree.TreePath;

public class TreeUtils {

	/**
	 * Expand or collapse all nodes
	 *
	 * @param tree
	 * @param expand
	 */
	public static void expandAll(TreeTable tree, boolean expand) {
		
		Stack<DataNode> path = new Stack<>();
		
		expandAll(tree.getCellRenderer(), (DataNode)tree.getCellRenderer().getModel().getRoot(), path, expand);
	}
	
	/**
	* Expand all tree nodes
	*
	* @param tree
	*         subject tree
	* @param parent
	*         parent tree path
	* @param expand
	*         expand or collapse
	*/
	private static void expandAll(TreeTableCellRenderer tree, DataNode parent, Stack<DataNode> path, boolean expand) {
		
		DataNode node = parent;
		
		System.out.println("[TreeTable][Expand] Starting expanding: " + node.getName());
		
		path.push(node);
		
		System.out.println("Current path is: " + path.toString());
		
		if (node.getChildCount() >= 0) {
			
			for (DataNode e : node.getChildren()) {
				
				expandAll(tree, e, path, expand);
			}
		}
	
		// Expansion or collapse must be done bottom-up
		if (expand) {
			System.out.println("[TreeTable][Expand] get expanded: " + parent);
			tree.expandPath(new TreePath(path.toArray()));
			path.pop();
		} else {
			System.out.println("[TreeTable][Expand] get collapsed: " + parent);
			tree.collapsePath(new TreePath(path.toArray()));
			path.pop();
		}
	}
}
