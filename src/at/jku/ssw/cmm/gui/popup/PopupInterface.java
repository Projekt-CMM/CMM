package at.jku.ssw.cmm.gui.popup;

import javax.swing.JPanel;

public interface PopupInterface {
	
	public JPanel getGlassPane();
	
	public void invokePopup( JPanel popup, int x, int y, int width, int height );
}
