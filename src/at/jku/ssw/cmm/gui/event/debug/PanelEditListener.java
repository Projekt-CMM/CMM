package at.jku.ssw.cmm.gui.event.debug;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.GUIdebugPanel;

/**
 * This class is a listener for the right panel of the main GUI in edit mode.
 * Actually, this listener just watches the "compile" button.
 * 
 * @author fabian
 *
 */
public class PanelEditListener implements MouseListener {
	
	public PanelEditListener( GUIdebugPanel master ){
		this.master = master;
	}
	
	//Reference to the panel manager
	private final GUIdebugPanel master;

	@Override
	public void mouseClicked(MouseEvent e) {
		//User clicked "compile" -> run compiler
		master.compile();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Auto-generated method stub
		
	}
	
}
