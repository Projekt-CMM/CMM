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
		Component c = (Component)defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if(value instanceof Component)
			return (Component)value;
		else if( table.getValueAt(row, 0).toString().endsWith(")") )
			c.setBackground(Color.CYAN);
		else if( table.getValueAt(row, 1).toString().endsWith(""+DataNode.CHANGE_TAG) )
			c.setBackground(Color.YELLOW);
		else if ( table.getValueAt(row, 2).toString().equals("undef") )
    		c.setBackground(new Color(240, 240, 240));
		else
			c.setBackground(table.getBackground());
		
		return c;
	}
}
