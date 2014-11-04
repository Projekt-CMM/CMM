package at.jku.ssw.cmm.gui.debug;

import java.util.Stack;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
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
			DebugShell.out(State.LOG, Area.READVAR, "complete variable structure update");
			
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					varTreeTable.setTreeModel(InitTreeTableData.readSymbolTable(compiler, popup, fileName));
				}
			});
			this.forceUpdate = false;
			this.varTreeTable.revalidate();
			this.varTreeTable.repaint();
		}
		else{
			DebugShell.out(State.LOG, Area.READVAR, "[treeTable][update] updating variable values");
			InitTreeTableData.updateTreeTable(this.varTreeTable.getTreeModel(), (DataNode)this.varTreeTable.getCellRenderer().getModel().getRoot(), compiler, popup, fileName);
			
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					varTreeTable.updateTreeModel();
				}
			});
			this.varTreeTable.revalidate();
			this.varTreeTable.repaint();
		}
	}

	/**
	 * Deletes all variable values from tables; tables are shown blank
	 */
	public void standby( String fileName ) {
		
		this.varTreeTable.setTreeModel(new TreeTableDataModel(InitTreeTableData.createDataStructure(fileName)));
		this.forceUpdate = true;
		
		DebugShell.out(State.LOG, Area.GUI, "treetable standby");
	}
	
	public void highlightVariable( Stack<String> path ){
		DebugShell.out(State.LOG, Area.GUI, "last variable changed " + path);
		TreeUtils.expandPath(varTreeTable, path).markChanged();
	}
	
	public void highlightVariable( int adr ){
		TreeUtils.expandByAddress(varTreeTable, adr);
		varTreeTable.repaint();
	}
	
	

}
