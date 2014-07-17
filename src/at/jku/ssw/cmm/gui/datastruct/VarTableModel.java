package at.jku.ssw.cmm.gui.datastruct;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class VarTableModel extends AbstractTableModel {
	
	public VarTableModel(){
		this.data = new ArrayList<>();
	}

	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String[] columnNames = {"Name", "Type", "Value"};
	private List<Object[]> data;

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return this.data.get(row)[col];
	}

	/**
	* JTable uses this method to determine the default renderer/
	* editor for each cell.  If we didn't implement this method,
	* then the last column would contain text ("true"/"false"),
	* rather than a check box.
	*/
	public Class<? extends Object> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/**
	 * Don't need to implement this method unless your table's
	 * editable.
	 */
	public boolean isCellEditable(int row, int col) {
		//Note that the data/cell address is constant,
		//no matter where the cell appears on screen.
		return false;
	}

	/**
	 * Don't need to implement this method unless your table's
	 * data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		this.data.get(row)[col] = value;
		fireTableCellUpdated(row, col);
	}
	
	public void addRow( Object[] o ){
		this.data.add(o);
	}
	
	public void reset(){
		this.data = new ArrayList <>();
	}
}
