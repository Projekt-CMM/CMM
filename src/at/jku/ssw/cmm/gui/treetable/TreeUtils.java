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
		
		//System.out.println("[TreeTable][Expand] Starting expanding: " + node.getName());
		
		path.push(node);
		
		//System.out.println("Current path is: " + path.toString());
		
		if (node.getChildCount() >= 0) {
			for (DataNode e : node.getChildren()) {
				
				expandAll(tree, e, path, expand);
			}
		}
	
		// Expansion or collapse must be done bottom-up
		if (expand) {
			//System.out.println("[TreeTable][Expand] get expanded: " + parent);
			tree.expandPath(new TreePath(path.toArray()));
			path.pop();
		} else {
			//System.out.println("[TreeTable][Expand] get collapsed: " + parent);
			tree.collapsePath(new TreePath(path.toArray()));
			path.pop();
		}
	}
	
	public static DataNode expandPath(TreeTable tree, Stack<String> stack){
		
		DataNode root = (DataNode)tree.getCellRenderer().getModel().getRoot();
		Stack<DataNode> path = new Stack<>();
		path.push(root);
		if( stack.peek().endsWith(".cmm") )
			stack.pop();
		return expandPath( tree.getCellRenderer(), root, stack, path );
	}
	
	private static DataNode expandPath(TreeTableCellRenderer tree, DataNode node, Stack<String> stack, Stack<DataNode> path){
		
		if( stack.size() <= 0 )
			return node;
		
		DataNode lowest = null;
		
		String name = stack.pop();
		
		//System.out.println("[TreeTable][Expand] looking for: " + name);
		
		if (node.getChildCount() >= 0) {
			for (DataNode e : node.getChildren()) {
				if( e.getName().equals(name) ){
					//System.out.println("[TreeTable][Expand] entering new level: " + e.print());
					path.push(e);
					lowest = expandPath(tree, e, stack, path);
				}
			}
		}
		
		//System.out.println("[TreeTable][Expand] get expanded: " + path);
		tree.expandPath(new TreePath(path.toArray()));
		
		path.pop();
		
		return lowest;
	}
	
	public static void expandByAddress(TreeTable tree, int address, boolean changed){
		//System.err.println("Starting search: " + address);
		DataNode root = (DataNode)tree.getCellRenderer().getModel().getRoot();
		
		Stack<DataNode> path = new Stack<>();
		path.push(root);
		
		expandByAddress(tree.getCellRenderer(), root, address, path, changed);
	}
	
	private static boolean expandByAddress(TreeTableCellRenderer tree, DataNode node, int address, Stack<DataNode> path, boolean changed){
		
		//System.out.println("Checking search: " + node.print());
		
		if( node.getAddress() == address ){
			System.out.println("Located: " + node.print());
			if( changed )
				node.markChanged();
			else
				node.markRead();
			return true;
		}
		
		if (node.getChildCount() >= 0) {
			for (DataNode e : node.getChildren()) {
				
				path.push(e);
				if(expandByAddress(tree, e, address, path, changed)){
					//System.out.println("Expanding: " + path.toArray());
					
					tree.expandPath(new TreePath(path.toArray()));
					path.pop();
					return true;
				}
				path.pop();
			}
		}
		
		return false;
	}
}
