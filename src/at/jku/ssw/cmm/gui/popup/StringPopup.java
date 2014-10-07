package at.jku.ssw.cmm.gui.popup;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StringPopup {

	public static void createPopUp( PopupInterface popupInterface, String text, int x, int y ){
		
		ImagePopup popup = new ImagePopup("images/popup2.png");
		popup.setBounds(popupInterface.getGlassPane().getMousePosition().x-221, popupInterface.getGlassPane().getMousePosition().y-151, 260, 151);
		
		JTextArea ta = new JTextArea( text );
		JScrollPane scrollPane = new JScrollPane(ta);
		scrollPane.setBounds(10, 10, 240, 117);
		scrollPane.setPreferredSize(new Dimension(240, 117));
		popup.add(scrollPane);
		
		popupInterface.invokePopup(popup, popupInterface.getGlassPane().getMousePosition().x-221, popupInterface.getGlassPane().getMousePosition().y-151, 260, 143);
	}
}
