package at.jku.ssw.cmm.gui.debug;

import java.util.Stack;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.gui.datastruct.InitTreeTableData;
import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.gui.popup.PopupInterface;
import at.jku.ssw.cmm.gui.treetable.DataNode;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;
import at.jku.ssw.cmm.gui.treetable.TreeUtils;

public class TreeTableView{
	
	public TreeTableView( GUImainMod main, JPanel panel, String fileName ){
		
		this.main = main;
		this.panel = panel;
		this.forceUpdate = true;
		
		this.init(fileName);
	}
	
	private final GUImainMod main;
	
	// Main panel
	private JPanel panel;
	
	// Tree table for variables
	private TreeTable varTreeTable;
	private TreeTableDataModel varTreeTableModel;
	
	private boolean forceUpdate;
	
	/**
	 * Initializes the variable view table/tree table, etc
	 * 
	 * @param panel
	 */
	public void init( String fileName ) {
		
		/* ---------- TREE TABLE for CALL STACK and LOCALS (optional) ---------- */
		this.varTreeTableModel = new TreeTableDataModel(InitTreeTableData.createDataStructure(fileName));
		
		this.varTreeTable = new TreeTable(this.main, this.varTreeTableModel);
		
		JScrollPane p = new JScrollPane(this.varTreeTable);
		this.panel.add(p);
		// Sub-panel end
	}

	/**
	 * Updates variable values and call stack
	 * 
	 * @param compiler
	 */
	public void update(final CMMwrapper compiler, final String fileName, final PopupInterface popup, boolean completeUpDate ) {
		
		if( completeUpDate || this.forceUpdate ){
			System.out.println("[treetable][update] complete variable structure update");
			
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					varTreeTable.setTreeModel(InitTreeTableData.readSymbolTable(compiler, popup, fileName));
				}
			});
			this.forceUpdate = false;
		}
		else{
			System.out.println("[treeTable][update] updating variable values");
			InitTreeTableData.updateTreeTable(this.varTreeTable.getTreeModel(), (DataNode)this.varTreeTable.getCellRenderer().getModel().getRoot(), compiler, popup, fileName);
			
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					varTreeTable.updateTreeModel();
				}
			});
			this.varTreeTable.repaint();
		}
	}

	/**
	 * Deletes all variable values from tables; tables are shown blank
	 */
	public void standby( String fileName ) {
		
		this.varTreeTable.setTreeModel(new TreeTableDataModel(InitTreeTableData.createDataStructure(fileName)));
		this.forceUpdate = true;
		
		System.out.println("[treetable] standby");
	}
	
	public void highlightVariable( Stack<String> path ){
		System.out.println("[treetable] last variable changed " + path);
		TreeUtils.expandPath(varTreeTable, path).markChanged();
	}

}
