package at.jku.ssw.cmm.gui.popup;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class PopupCloseListener implements MouseListener {
	
	public PopupCloseListener( JPanel glassPane, JPanel target, int x, int y, int w, int h ){
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.glassPane = glassPane;
		this.target = target;
	}
	
	private final int x, y, w, h;
	private final JPanel glassPane;
	private final JPanel target;

	@Override
	public void mouseClicked(MouseEvent e) {

		//Mouse click outside popup -> close and remove popup and this listener
		if( !( e.getX() >= x && e.getX() <= x+w && e.getY() >= y && e.getY() <= y+h ) ){
			this.glassPane.remove(this.target);
			this.glassPane.removeMouseListener(this);
			this.glassPane.validate();
			this.glassPane.repaint();
		}
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
