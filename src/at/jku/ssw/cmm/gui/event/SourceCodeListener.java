package at.jku.ssw.cmm.gui.event;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import at.jku.ssw.cmm.gui.GUImain;

/**
 * Listener for changes in the source code text area
 * 
 * @author fabian
 *
 */
public class SourceCodeListener implements DocumentListener {
	
	/**
	 * Listener for changes in the source code text area
	 * 
	 * @param master A reference to the main GUI
	 */
	public SourceCodeListener( GUImain master ){
		this.master = master;
	}
	
	/**
	 * A reference to the main GUI
	 */
	private final GUImain master;

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		
		//Mark file as changed if code is typed, inserted or deleted
		master.setFileChanged();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {}

	@Override
	public void removeUpdate(DocumentEvent arg0) {}

}
