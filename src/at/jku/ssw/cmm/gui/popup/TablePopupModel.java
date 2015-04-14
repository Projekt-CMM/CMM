package at.jku.ssw.cmm.gui.popup;

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class TablePopupModel implements TableModel {
	
	public TablePopupModel( List<Object> info ) {
		this.info = info;
	}
	
	private final List<Object> info;

	@Override
	public void addTableModelListener(TableModelListener arg0) {}

	@Override
	public Class<?> getColumnClass(int arg0) {
		//All columns have the same class
		return info.get(0).getClass();
	}

	@Override
	public int getColumnCount() {
		
		if( !info.isEmpty() && info.get(0) instanceof List ){
			@SuppressWarnings("unchecked")
			List<Object> l = (List<Object>)(info.get(0));
			return l.size()+1;
		}
		else
			return info.size();
	}
	
	@Override
	public int getRowCount() {
		
		if( !info.isEmpty() && info.get(0) instanceof List )
			return info.size();
		else
			return 1;
	}

	@Override
	public String getColumnName(int arg0) {
		if( !info.isEmpty() && info.get(0) instanceof List && arg0 == 0 )
			return " ";
		else if( info.get(0) instanceof List )
			return "[" + (arg0-1) + "]";
		else
			return "[" + arg0 + "]";
	}

	@Override
	/* --- arg1 = x, arg0 = y --- */
	public Object getValueAt(int arg0, int arg1) {
		if( !info.isEmpty() && info.get(0) instanceof List && arg1 == 0 )
			return "[" + arg0 + "]";
		else if( info.get(0) instanceof List ){
			@SuppressWarnings("unchecked")
			List<Object> l = (List<Object>)(info.get(arg0));
			return l.get(arg1-1);
		}
		else{
			return info.get(arg1);
		}
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {}
	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {}
}
