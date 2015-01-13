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
 
package at.jku.ssw.cmm.gui.treetable.var;

import java.util.Stack;

import javax.swing.tree.TreePath;

import at.jku.ssw.cmm.gui.treetable.DataNode;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.TreeTableCellRenderer;

public class TreeUtils {

	/**
	 * Expand or collapse all nodes
	 *
	 * @param tree
	 * @param expand
	 */
	public static void expandAll(TreeTable<VarDataNode> tree, boolean expand) {
		
		Stack<VarDataNode> path = new Stack<>();
		
		expandAll(tree.getCellRenderer(), (VarDataNode)tree.getCellRenderer().getModel().getRoot(), path, expand);
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
	private static void expandAll(TreeTableCellRenderer tree, VarDataNode parent, Stack<VarDataNode> path, boolean expand) {
		
		VarDataNode node = parent;
		
		//System.out.println("[TreeTable][Expand] Starting expanding: " + node.getName());
		
		path.push(node);
		
		//System.out.println("Current path is: " + path.toString());
		
		if (node.getChildCount() >= 0) {
			for (DataNode e : node.getChildren()) {
				
				expandAll(tree, (VarDataNode)e, path, expand);
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
	
	public static VarDataNode expandPath(TreeTable<VarDataNode> tree, Stack<String> stack){
		
		VarDataNode root = (VarDataNode)tree.getCellRenderer().getModel().getRoot();
		Stack<VarDataNode> path = new Stack<>();
		path.push(root);
		if( stack.peek().endsWith(".cmm") )
			stack.pop();
		return expandPath( tree.getCellRenderer(), (VarDataNode)root, stack, path );
	}
	
	private static VarDataNode expandPath(TreeTableCellRenderer tree, VarDataNode node, Stack<String> stack, Stack<VarDataNode> path){
		
		if( stack.size() <= 0 )
			return node;
		
		VarDataNode lowest = null;
		
		String name = stack.pop();
		
		//System.out.println("[TreeTable][Expand] looking for: " + name);
		
		if (node.getChildCount() >= 0) {
			for (DataNode e : node.getChildren()) {
				if( ((VarDataNode)e).getName().equals(name) ){
					//System.out.println("[TreeTable][Expand] entering new level: " + e.print());
					path.push((VarDataNode)e);
					lowest = expandPath(tree, (VarDataNode)e, stack, path);
				}
			}
		}
		
		//System.out.println("[TreeTable][Expand] get expanded: " + path);
		tree.expandPath(new TreePath(path.toArray()));
		
		path.pop();
		
		return lowest;
	}
	
	public static void expandByAddress(TreeTable<VarDataNode> tree, int address, boolean changed){
		//System.err.println("Starting search: " + address);
		VarDataNode root = (VarDataNode)tree.getCellRenderer().getModel().getRoot();
		
		Stack<VarDataNode> path = new Stack<>();
		path.push(root);
		
		expandByAddress(tree.getCellRenderer(), root, address, path, changed);
	}
	
	private static boolean expandByAddress(TreeTableCellRenderer tree, VarDataNode node, int address, Stack<VarDataNode> path, boolean changed){
		
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
				
				path.push((VarDataNode)e);
				if(expandByAddress(tree, (VarDataNode)e, address, path, changed)){
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
