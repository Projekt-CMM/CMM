package at.jku.ssw.cmm.gui.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import at.jku.ssw.cmm.gui.GUImain;

public class ErrorModeCloseListener implements MouseListener {
	
	public ErrorModeCloseListener( GUImain main, JPanel panel, JButton button ){
		this.main = main;
		this.panel = panel;
		this.button = button;
	}
	
	private final GUImain main;
	private final JPanel panel;
	private final JButton button;
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		main.setReadyMode();
		panel.remove(button);
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
