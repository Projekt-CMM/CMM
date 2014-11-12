package at.jku.ssw.cmm.gui.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import at.jku.ssw.cmm.gui.GUImain;

/**
 * Listener for text manipulation control elements (at the moment only the breakpoint)
 * 
 * @author fabian
 *
 */
public class RightPanelBreakpointListener implements MouseListener {

	/**
	 * Listener for text manipulation control elements (at the moment only the breakpoint)
	 * 
	 * @param main mainification interface of the main GUI
	 * @param target Target button (breakpoint button)
	 */
	public RightPanelBreakpointListener( GUImain main, JButton target ){
		this.main = main;
		this.target = target;
	}
	
	/**
	 * mainification interface of the main GUI
	 */
	private final GUImain main;
	
	/**
	 * Target button (breakpoint button)
	 */
	private final JButton target;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		//Toggle breakpoint
		if( target.isEnabled() )
			main.toggleBreakPoint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}
