package at.jku.ssw.cmm.gui.utils;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import at.jku.ssw.cmm.gui.treetable.DataNode;

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
			Component c = (Component)defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setBackground(Color.CYAN);
			return c;
		}
		
		else if( table.getValueAt(row, 1).toString().endsWith(""+DataNode.CHANGE_TAG) ){
			Component c = (Component)defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setBackground(Color.YELLOW);
			return c;
		}
		
		Component c = (Component)defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setBackground(table.getBackground());
		return c;
	}
}
