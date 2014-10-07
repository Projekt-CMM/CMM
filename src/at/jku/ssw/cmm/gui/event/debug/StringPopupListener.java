package at.jku.ssw.cmm.gui.event.debug;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.popup.PopupInterface;
import at.jku.ssw.cmm.gui.popup.StringPopup;

public class StringPopupListener implements MouseListener {

	public StringPopupListener( PopupInterface popup, String text ){
		this.popup = popup;
		this.text = text;
	}
	
	private final PopupInterface popup;
	private final String text;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		StringPopup.createPopUp(popup, text, e.getLocationOnScreen().x, e.getLocationOnScreen().y);
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
