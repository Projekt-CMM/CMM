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

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.debugger.InitTreeTableData;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.treetable.TableButtonMouseListener;
import at.jku.ssw.cmm.gui.treetable.TableButtonRenderer;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;
import at.jku.ssw.cmm.gui.treetable.TreeTableModel;

public class TreeTableView{
	
	public TreeTableView( GUImain main, JPanel panel, String fileName ){
		
		this.main = main;
		this.panel = panel;
		this.forceUpdate = true;
		
		this.init(fileName);
	}
	
	private static final String[] columnNames = { _("Name"), _("Type"), _("Value") };
	private static final Class<?>[] columnTypes = { TreeTableModel.class, String.class, Object.class };
	
	private final GUImain main;
	
	// Main panel
	private JPanel panel;
	
	// Tree table for variables
	private TreeTable<VarDataNode> varTreeTable;
	private TreeTableDataModel<VarDataNode> varTreeTableModel;
	
	private boolean forceUpdate;
	
	/**
	 * Initializes the variable view table/tree table, etc
	 * 
	 * @param panel
	 */
	public void init( String fileName ) {
		
		/* ---------- TREE TABLE for CALL STACK and LOCALS ---------- */
		this.varTreeTableModel = new TreeTableDataModel<VarDataNode>(InitTreeTableData.createDataStructure(fileName), columnNames, columnTypes);
		
		this.varTreeTable = new TreeTable<>(this.varTreeTableModel);
		this.varTreeTable.getCellRenderer().setCellRenderer(new TreeStructImageRenderer());
		
		this.varTreeTable.getTableHeader().setToolTipText("<html><b>" + _("Variable table columns") + "</b><br>" +
        		_("You can change the width a column by<br>dragging and sliding its border.") + "</html>");
        
        TableButtonRenderer buttonRenderer = new TableButtonRenderer(this.varTreeTable.getDefaultRenderer(JButton.class));
        this.varTreeTable.getColumnModel().getColumn(1).setCellRenderer(buttonRenderer);
        this.varTreeTable.getColumnModel().getColumn(2).setCellRenderer(buttonRenderer);
		
		JScrollPane p = new JScrollPane(this.varTreeTable);
		this.panel.add(p, BorderLayout.CENTER);
	}

	/**
	 * Updates variable values and call stack
	 * 
	 * @param compiler
	 */
	public void update(final CMMwrapper compiler, final String fileName, final boolean completeUpDate ) {
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				if( completeUpDate || forceUpdate ){
					varTreeTable.setTreeModel(InitTreeTableData.readSymbolTable(compiler, main, fileName, columnNames, columnTypes));
					varTreeTable.addMouseListener(new TableButtonMouseListener(main, varTreeTable));
						
					forceUpdate = false;
				}
				else{
					InitTreeTableData.updateTreeTable(varTreeTable.getTreeModel(), (VarDataNode)varTreeTable.getCellRenderer().getModel().getRoot(), compiler, main, fileName);
					varTreeTable.updateTreeModel();
				}
				
				//varTreeTable.revalidate();
				//varTreeTable.repaint();
				
				System.out.println("Current Function: "+ varTreeTable.getTreeModel().getCurrentFunction());
				
				if(varTreeTable.getTreeModel().getCurrentFunction() != -1) {
					Stack<String> s = new Stack<>();
					s.push(main.getSettings().getCMMFile());
					s.push(varTreeTable.getTreeModel().getCurrentFunction() + "()");
					System.out.println("Expand Stack : " + s);
					TreeUtils.expandByAddressAndPattern(varTreeTable, varTreeTable.getTreeModel().getCurrentFunction(), TreeUtils.HIGHLIGHT_NOT, "[\\w]+[\\(\\)]");
				}
			}
		});
	}

	/**
	 * Deletes all variable values from tables; tables are shown blank
	 */
	public void standby( String fileName ) {
		
		this.varTreeTable.setTreeModel(new TreeTableDataModel<>(InitTreeTableData.createDataStructure(fileName), TreeTableView.columnNames, TreeTableView.columnTypes));
		this.forceUpdate = true;
		
		
		
		DebugShell.out(State.LOG, Area.GUI, "treetable standby");
	}
	
	/**
	 * Highlights the variable with the given address in the variable tree table
	 * 
	 * @param adr The address of the variable which shall be highlighted
	 * @param changed TRUE if highlighting changed variables,
	 * 		FALSE if highlighting read variables
	 */
	public void highlightVariable( int adr, boolean changed ){
		TreeUtils.expandByAddress(varTreeTable, adr, TreeUtils.HIGHLIGHT_CHANGED);
		varTreeTable.repaint();
	}
	
	public void updateFontSize(){
		varTreeTable.setFont(varTreeTable.getFont().deriveFont((float)this.main.getSettings().getVarSize()));
		varTreeTable.getCellRenderer().setFont(varTreeTable.getCellRenderer().getFont().deriveFont((float)this.main.getSettings().getVarSize()));
		varTreeTable.repaint();
	}
}
