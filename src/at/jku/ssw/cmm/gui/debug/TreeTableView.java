package at.jku.ssw.cmm.gui.debug;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.gui.datastruct.InitTreeTableData;
import at.jku.ssw.cmm.gui.popup.PopupInterface;
import at.jku.ssw.cmm.gui.treetable.DataNode;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;

public class TreeTableView{
	
	public TreeTableView( JPanel panel, String fileName ){
		
		this.panel = panel;
		this.forceUpdate = true;
		
		this.init(fileName);
	}
	
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
	public void update( CMMwrapper compiler, String fileName, PopupInterface popup, boolean completeUpDate ) {
		
		if( completeUpDate || this.forceUpdate ){
			System.out.println("[treetable][update] complete variable structure update");
			this.varTreeTable.setTreeModel(InitTreeTableData.readSymbolTable(compiler, popup, fileName));
			this.forceUpdate = false;
		}
		else{
			System.out.println("[treeTable][update] updating variable values");
			InitTreeTableData.updateTreeTable(this.varTreeTable.getTreeModel(), (DataNode)this.varTreeTable.getCellRenderer().getModel().getRoot(), compiler, popup, fileName);
			this.varTreeTable.updateTreeModel();
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

}
