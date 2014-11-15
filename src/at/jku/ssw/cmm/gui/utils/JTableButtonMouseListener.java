package at.jku.ssw.cmm.gui.utils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.treetable.DataNode;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.context.InitContextMenu;

public class JTableButtonMouseListener implements MouseListener {
	
	public JTableButtonMouseListener(GUImain main, TreeTable t) {
		this.main = main;
		this.treeTable = t;
	}
	
	private final GUImain main;
	private final TreeTable treeTable;

	private void forwardEventToButton(MouseEvent e) {

		TableColumnModel columnModel = this.treeTable.getTable().getColumnModel();
		int column = columnModel.getColumnIndexAtX(e.getX());
		int row = e.getY() / this.treeTable.getTable().getRowHeight();
		Object value;
		JButton button;
		MouseEvent buttonEvent;

		if (row >= this.treeTable.getTable().getRowCount() || row < 0
				|| column >= this.treeTable.getTable().getColumnCount() || column < 0)
			return;

		value = this.treeTable.getTable().getValueAt(row, column);

		if (!(value instanceof JButton))
			return;

		button = (JButton) value;

		buttonEvent = (MouseEvent) SwingUtilities.convertMouseEvent(this.treeTable.getTable(), e, button);
		button.dispatchEvent(buttonEvent);
		// This is necessary so that when a button is pressed and released
		// it gets rendered properly. Otherwise, the button may still appear
		// pressed down when it has been released.
		this.treeTable.getTable().repaint();
	}

	public void mouseClicked(MouseEvent e) {
		forwardEventToButton(e);
	}

	public void mouseEntered(MouseEvent e) {
		forwardEventToButton(e);
	}

	public void mouseExited(MouseEvent e) {
		forwardEventToButton(e);
	}

	public void mousePressed(MouseEvent e) {
		
		// Right mouse clicked
		if (SwingUtilities.isRightMouseButton(e)) {

			int row = this.treeTable.getTable().rowAtPoint(e.getPoint());
			int col = this.treeTable.getTable().columnAtPoint(e.getPoint());
			if (row >= 0 && col >= 0) {
				
				String name = (String)this.treeTable.getTable().getValueAt(row, 0);
				
				DataNode node = (DataNode) this.treeTable.getModelAdapter().nodeForRow(row);
				
				System.out.println("Clicked Data Node: " + node.print());
				
				if( node.getDeclarationLine() >= 0 ){
					InitContextMenu.initContextMenu(main, name, node.getDeclarationLine(), 20).show(e.getComponent(), e.getX(), e.getY());
				}
			}

		} else
			forwardEventToButton(e);
	}

	public void mouseReleased(MouseEvent e) {
		forwardEventToButton(e);
	}
}
