package at.jku.ssw.cmm.gui.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.mod.GUImainMod;

public class RightPanelEventListener {

	public RightPanelEventListener( GUImainMod mod ){
		this.mod = mod;
	}
	
	private final GUImainMod mod;
	
	public MouseListener breakPointHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			
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
	};
}
