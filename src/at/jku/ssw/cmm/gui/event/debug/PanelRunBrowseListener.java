package at.jku.ssw.cmm.gui.event.debug;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.debug.TableView;

public class PanelRunBrowseListener implements MouseListener {
	
	public PanelRunBrowseListener( TableView master, boolean global ){
		this.master = master;
		this.global = global;
	}
	
	private final TableView master;
	private final boolean global;

	@Override
	public void mouseClicked(MouseEvent e) {
		
		this.master.browseBack(this.global);
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
