package at.jku.ssw.cmm.gui.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import at.jku.ssw.cmm.gettext.Language;
import at.jku.ssw.cmm.gui.GUImain;

public class LanguageListener implements ActionListener{
	
	public LanguageListener( GUImain main, String fileName ){
		this.main = main;
		this.fileName = fileName;
	}
	
	private final GUImain main;
	private final String fileName;

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("[log] changing language: " + fileName);
		Language.loadLanguage(fileName);
		main.repaint();
	}
	
}
