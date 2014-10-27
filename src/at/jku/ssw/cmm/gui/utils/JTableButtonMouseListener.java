package at.jku.ssw.cmm.gui.utils;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.gui.datastruct.InitTreeTableData;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.gui.treetable.context.InitContextMenu;

public class JTableButtonMouseListener implements MouseListener {
	
	public JTableButtonMouseListener(GUImainMod main, GUIdebugPanel debug, JTable t) {
		this.main = main;
		this.debug = debug;
		this.table = t;
	}
	
	private final GUImainMod main;
	private final GUIdebugPanel debug;
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
				
				String name = (String)table.getValueAt(row, 0);
				Obj obj;
				
				System.out.println("clicked -> " + name);
				
				//Clicked root node
				if( name.endsWith(".cmm") || name.equals(_("Unnamed")) ){
					return;
				}
				//Variable clicked is a function
				else if( name.endsWith("()") ){
					name = name.substring(0, name.indexOf("("));
					
					obj = InitTreeTableData.findNodeByName(debug.getCompileManager().getSymbolTable().curScope.locals, name);
				}
				//Variable clicked is a variable
				else{
					return;
					/*obj = InitTreeTableData.findNodeByName(debug.getCompileManager().getSymbolTable().curScope.locals,
							this.getFunction(row));
					System.out.println("Function node: " + obj.name + ", " + obj.ast.line);
					System.out.println("Looking for: " + name);
					obj = InitTreeTableData.findNodeByName(obj.locals, name);*/
				}
					
				
					
				System.out.println("-> " + name + ", " + obj.ast.line );
					
				InitContextMenu.initContextMenu(main, name, obj.ast.line, 20).show(e.getComponent(), e.getX(), e.getY());
			}

		} else
			forwardEventToButton(e);
	}

	public void mouseReleased(MouseEvent e) {
		forwardEventToButton(e);
	}
	
	private String getFunction( int line ){
		
		String name;
		
		for( ; line >= 0; line -- ){
			
			name = (String)(table.getValueAt(line, 0));
			System.out.println("Checking " + name );
			if( name.endsWith("()") ){
				System.out.println("Returning " + name.substring(0, name.indexOf("(")) );
				return name.substring(0, name.indexOf("("));
			}
		}
		
		return null;
	}
}
