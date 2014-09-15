package at.jku.ssw.cmm.gui.event.debug;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.GUIdebugPanel;
import at.jku.ssw.cmm.gui.mod.GUImainMod;

public class PanelToolBarListener {

	public PanelToolBarListener( GUImainMod modifier, GUIdebugPanel master ){
		this.modifier = modifier;
		this.master = master;
	}
	
	private final GUImainMod modifier;
	private final GUIdebugPanel master;
	
	public MouseListener breakPointButtonHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			
			
		}

		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	};
}
