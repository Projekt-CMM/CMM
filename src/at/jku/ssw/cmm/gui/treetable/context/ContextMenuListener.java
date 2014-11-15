package at.jku.ssw.cmm.gui.treetable.context;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import at.jku.ssw.cmm.gui.GUImain;

public class ContextMenuListener implements ActionListener {

	public ContextMenuListener( GUImain main, String name, int line ){
		this.main = main;
		this.name = name;
		this.line = line;
	}
	
	private final GUImain main;
	private final String name;
	private final int line;
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		System.out.println("Action performed: " + name + ", " + line);
		this.main.getLeftPanel().highlightSourceCode(this.line);
	}
}
