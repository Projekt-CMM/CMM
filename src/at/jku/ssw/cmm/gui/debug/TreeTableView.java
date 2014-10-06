package at.jku.ssw.cmm.gui.debug;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.gui.datastruct.ReadCallStackHierarchy;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;

public class TreeTableView extends VariableView{
	
	public TreeTableView( JPanel panel ){
		
		this.panel = panel;
		
		this.init();
	}
	
	// Main panel
	private JPanel panel;
	
	// Tree table for variables
	private TreeTable varTreeTable;
	private TreeTableDataModel varTreeTableModel;
	
	@Override
	public void init() {
		
		/* ---------- TREE TABLE for CALL STACK and LOCALS (optional) ---------- */
		this.varTreeTableModel = new TreeTableDataModel(ReadCallStackHierarchy.createDataStructure());
		
		this.varTreeTable = new TreeTable(this.varTreeTableModel);
		
		JScrollPane p = new JScrollPane(this.varTreeTable);
		this.panel.add(p);
		// Sub-panel end
	}

	@Override
	public void update(CMMwrapper compiler) {
		
		this.varTreeTable.setTreeModel(ReadCallStackHierarchy.readSymbolTable(compiler, null));
	}

	@Override
	public void standby() {
		
		this.varTreeTable.setTreeModel(new TreeTableDataModel(ReadCallStackHierarchy.createDataStructure()));
	}

}
