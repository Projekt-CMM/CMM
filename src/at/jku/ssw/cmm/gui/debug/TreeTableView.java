package at.jku.ssw.cmm.gui.debug;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.gui.datastruct.ReadCallStackHierarchy;
import at.jku.ssw.cmm.gui.popup.PopupInterface;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;

public class TreeTableView{
	
	public TreeTableView( JPanel panel, String fileName ){
		
		this.panel = panel;
		
		this.init(fileName);
	}
	
	// Main panel
	private JPanel panel;
	
	// Tree table for variables
	private TreeTable varTreeTable;
	private TreeTableDataModel varTreeTableModel;
	
	/**
	 * Initializes the variable view table/tree table, etc
	 * 
	 * @param panel
	 */
	public void init( String fileName ) {
		
		/* ---------- TREE TABLE for CALL STACK and LOCALS (optional) ---------- */
		this.varTreeTableModel = new TreeTableDataModel(ReadCallStackHierarchy.createDataStructure(fileName));
		
		this.varTreeTable = new TreeTable(this.varTreeTableModel);
		
		JScrollPane p = new JScrollPane(this.varTreeTable);
		this.panel.add(p);
		// Sub-panel end
	}

	/**
	 * Updates variable values and call stack
	 * 
	 * @param compiler
	 */
	public void update( CMMwrapper compiler, String fileName, PopupInterface popup ) {
		
		this.varTreeTable.setTreeModel(ReadCallStackHierarchy.readSymbolTable(compiler, popup, fileName));
	}

	/**
	 * Deletes all variable values from tables; tables are shown blank
	 */
	public void standby( String fileName ) {
		
		this.varTreeTable.setTreeModel(new TreeTableDataModel(ReadCallStackHierarchy.createDataStructure(fileName)));
	}

}
