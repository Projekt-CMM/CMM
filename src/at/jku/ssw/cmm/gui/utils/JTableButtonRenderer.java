package at.jku.ssw.cmm.gui.utils;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class JTableButtonRenderer implements TableCellRenderer {
	private TableCellRenderer defaultRenderer;

	public JTableButtonRenderer(TableCellRenderer renderer) {
		defaultRenderer = renderer;
	}
	  
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if(value instanceof Component)
			return (Component)value;
		
		else if( table.getValueAt(row, 0).toString().endsWith(")") ){
			System.out.println("Coloring: " + table.getValueAt(row, 0).toString() + " from " + row );
			Component c = (Component)defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setBackground(Color.CYAN);
			return c;
		}
		
		Component c = (Component)defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setBackground(table.getBackground());
		return c;
	}
}
