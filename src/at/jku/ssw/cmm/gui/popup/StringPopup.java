package at.jku.ssw.cmm.gui.popup;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import at.jku.ssw.cmm.gui.GUImain;

public class StringPopup {

	public static void createPopUp( GUImain main, String text, int x, int y ){
		
		ImagePopup popup = new ImagePopup("images/popup2.png");
		popup.setBounds(main.getGlassPane().getMousePosition().x-221, main.getGlassPane().getMousePosition().y-151, 260, 151);
		
		JTextArea ta = new JTextArea( text );
		JScrollPane scrollPane = new JScrollPane(ta);
		scrollPane.setBounds(10, 10, 240, 117);
		scrollPane.setPreferredSize(new Dimension(240, 117));
		popup.add(scrollPane);
		
		main.invokePopup(popup, main.getGlassPane().getMousePosition().x-221, main.getGlassPane().getMousePosition().y-151, 260, 143);
	}
}
