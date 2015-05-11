package at.jku.ssw.cmm.gui.event.debug;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JTable;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.popup.ComponentPopup;
import at.jku.ssw.cmm.gui.popup.ImagePopup;
import at.jku.ssw.cmm.gui.popup.TablePopupModel;
import at.jku.ssw.cmm.gui.popup.TablePopupRenderer;

/**
 * This is a listener class for the array popup
 * 
 * @author fabian
 */
public class ArrayPopupListener implements MouseListener {
	
	public ArrayPopupListener( GUImain main, List<Object> info ) {
		this.main = main;
		this.info = info;
	}
	
	private final GUImain main;
	private final List<Object> info;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		TablePopupModel model = new TablePopupModel(info);
		JTable table = new JTable(model);
		try{
			table.getColumn(" ").setCellRenderer(new TablePopupRenderer());
		}catch(IllegalArgumentException e1){}
		
		table.getTableHeader().setReorderingAllowed(false);

		for( int i = 0; i < table.getColumnModel().getColumnCount(); i++ ){
			table.getColumnModel().getColumn(i).setMinWidth(24);
			table.getColumnModel().getColumn(i).setPreferredWidth(28);
		}
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		//Invoke popup
		if( main.getGlassPane().getMousePosition() != null )
			ComponentPopup.createPopUp(main, table, main.getGlassPane().getMousePosition().x, main.getGlassPane().getMousePosition().y, 250, 120, ImagePopup.SOUTH, 0.85);
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
