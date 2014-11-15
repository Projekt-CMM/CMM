package at.jku.ssw.cmm.gui.event.debug;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.popup.StringPopup;

/**
 * Listener for the string popup button in the variable tree table.
 * Invokes popup if clicked.
 * 
 * @author fabian
 *
 */
public class StringPopupListener implements MouseListener {

	/**
	 * Listener for the string popup button in the variable tree table.
	 * Invokes popup if clicked.
	 * 
	 * @param popup Popup interface of the main GUI.
	 * 		This interface is necessary to invoke a popup.
	 * @param text The text to be displayed in the popup (content of the string)
	 */
	public StringPopupListener( GUImain main, String text ){
		this.main = main;
		this.text = text;
	}
	
	/**
	 * Popup interface of the main GUI.
	 * This interface is necessary to invoke a popup.
	 */
	private final GUImain main;
	
	/**
	 * The text to be displayed in the popup (content of the string)
	 */
	private final String text;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		//Invoke popup
		StringPopup.createPopUp(main, text, e.getLocationOnScreen().x, e.getLocationOnScreen().y);
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
