package at.jku.ssw.cmm.gui.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import at.jku.ssw.cmm.gui.mod.GUImainMod;

public class RightPanelBreakpointListener implements MouseListener {

	public RightPanelBreakpointListener( GUImainMod mod, JButton target ){
		this.mod = mod;
		this.target = target;
	}
	
	private final GUImainMod mod;
	private final JButton target;
	
	@Override
	public void mouseClicked(MouseEvent e) {
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
