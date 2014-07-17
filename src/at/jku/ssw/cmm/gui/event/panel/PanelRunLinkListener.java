package at.jku.ssw.cmm.gui.event.panel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.GUIrightPanel;

public class PanelRunLinkListener implements MouseListener {
	
	public PanelRunLinkListener( GUIrightPanel master, String name, int type, int address, boolean global ){
		this.master = master;
		this.name = name;
		this.type = type;
		this.address = address;
		this.global = global;
	}
	
	private final GUIrightPanel master;
	private final int type;
	private final String name;
	private final int address;
	private final boolean global;

	@Override
	public void mouseClicked(MouseEvent e) {

		System.out.println("View clicked: " + name);
		this.master.selectStruct(name, address, type, global);
	}

	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}
