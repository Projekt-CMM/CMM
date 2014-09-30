package at.jku.ssw.cmm.gui.event;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import at.jku.ssw.cmm.gui.GUImain;

public class SourceCodeListener implements DocumentListener {
	
	public SourceCodeListener( GUImain master ){
		this.master = master;
	}
	
	private final GUImain master;

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		
		master.setFileChanged();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {}

	@Override
	public void removeUpdate(DocumentEvent arg0) {}

}
