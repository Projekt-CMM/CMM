package at.jku.ssw.cmm.gui.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import at.jku.ssw.cmm.gui.mod.GUImainMod;

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
	 * @param mod Modification interface of the main GUI
	 * @param target Target button (breakpoint button)
	 */
	public RightPanelBreakpointListener( GUImainMod mod, JButton target ){
		this.mod = mod;
		this.target = target;
	}
	
	/**
	 * Modification interface of the main GUI
	 */
	private final GUImainMod mod;
	
	/**
	 * Target button (breakpoint button)
	 */
	private final JButton target;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		//Toggle breakpoint
		if( target.isEnabled() )
			mod.toggleBreakPoint();
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
