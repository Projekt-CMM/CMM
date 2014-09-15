package at.jku.ssw.cmm.gui.event.debug;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import at.jku.ssw.cmm.gui.GUIdebugPanel;

public class PanelRunLinkListener implements MouseListener {
	
	public PanelRunLinkListener( GUIdebugPanel master, String name, int type, int address, boolean global, JButton b ){
		this.master = master;
		this.name = name;
		this.type = type;
		this.address = address;
		this.global = global;
		this.b = b;
	}
	
	private final GUIdebugPanel master;
	private final int type;
	private final String name;
	private final int address;
	private final boolean global;
	private final JButton b;

	@Override
	public void mouseClicked(MouseEvent e) {

		System.out.println("View clicked: " + name);
		
		this.master.selectStruct(name, address, type, global, b.getLocation().x, b.getLocation().y );
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
