package at.jku.ssw.cmm.gui.utils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.gui.treetable.context.InitContextMenu;

public class JTableButtonMouseListener implements MouseListener {
	
	public JTableButtonMouseListener(GUImainMod main, JTable t) {
		this.main = main;
		this.table = t;
	}
	
	private final GUImainMod main;
	private final JTable table;

	private void forwardEventToButton(MouseEvent e) {

		TableColumnModel columnModel = table.getColumnModel();
		int column = columnModel.getColumnIndexAtX(e.getX());
		int row = e.getY() / table.getRowHeight();
		Object value;
		JButton button;
		MouseEvent buttonEvent;

		if (row >= table.getRowCount() || row < 0
				|| column >= table.getColumnCount() || column < 0)
			return;

		value = table.getValueAt(row, column);

		if (!(value instanceof JButton))
			return;

		button = (JButton) value;

		buttonEvent = (MouseEvent) SwingUtilities.convertMouseEvent(table, e,
				button);
		button.dispatchEvent(buttonEvent);
		// This is necessary so that when a button is pressed and released
		// it gets rendered properly. Otherwise, the button may still appear
		// pressed down when it has been released.
		table.repaint();
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

			int row = table.rowAtPoint(e.getPoint());
			int col = table.columnAtPoint(e.getPoint());
			if (row >= 0 && col >= 0) {
				InitContextMenu.initContextMenu(main, "hello", 10, 20).show(e.getComponent(), e.getX(), e.getY());
			}

		} else
			forwardEventToButton(e);
	}

	public void mouseReleased(MouseEvent e) {
		forwardEventToButton(e);
	}
}
