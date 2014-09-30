package at.jku.ssw.cmm.gui.event;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.file.SaveDialog;

public class MainKeyListener implements KeyListener {
	
	public MainKeyListener(GUImain main, SaveDialog saveDialog){
		
		this.main = main;
		this.saveDialog = saveDialog;
	}
	
	private final GUImain main;
	private final SaveDialog saveDialog;

	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			saveDialog.directSave();
			main.setFileSaved();
        }
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

}
