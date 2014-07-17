package at.jku.ssw.cmm.gui.utils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

public class JTableButtonMouseListener implements MouseListener {
	  private JTable table;

	  private void forwardEventToButton(MouseEvent e) {
	    TableColumnModel columnModel = table.getColumnModel();
	    int column = columnModel.getColumnIndexAtX(e.getX());
	    int row = e.getY() / table.getRowHeight();
	    Object value;
	    JButton button;
	    MouseEvent buttonEvent;

	    if(row >= table.getRowCount() || row < 0 || column >= table.getColumnCount() || column < 0)
	    	return;

	    value = table.getValueAt(row, column);

	    if(!(value instanceof JButton))
	      return;

	    button = (JButton)value;

	    buttonEvent = (MouseEvent)SwingUtilities.convertMouseEvent(table, e, button);
	    button.dispatchEvent(buttonEvent);
	    // This is necessary so that when a button is pressed and released
	    // it gets rendered properly.  Otherwise, the button may still appear
	    // pressed down when it has been released.
	    table.repaint();
	  }

	  public JTableButtonMouseListener(JTable t) {
	    table = t;
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
	    forwardEventToButton(e);
	  }

	  public void mouseReleased(MouseEvent e) {
	    forwardEventToButton(e);
	  }
	}
