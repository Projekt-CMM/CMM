package at.jku.ssw.cmm.gui.event;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.file.SaveDialog;

/**
 * Listener for global hotkeys (eg. ctrl+s)
 * 
 * @author fabian
 *
 */
public class MainKeyListener implements KeyListener {
	
	/**
	 * Listener for global hotkeys (eg. ctrl+s)
	 * 
	 * @param main Reference to the main GUI
	 * @param saveDialog Reference to the file saving and file save dialog object
	 */
	public MainKeyListener(GUImain main, SaveDialog saveDialog){
		
		this.main = main;
		this.saveDialog = saveDialog;
	}
	
	/**
	 * Reference to the main GUI
	 */
	private final GUImain main;
	
	/**
	 * Reference to the file saving and file save dialog object
	 */
	private final SaveDialog saveDialog;

	@Override
	public void keyPressed(KeyEvent e) {
		//Hotkey: ctrl+s
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
