package at.jku.ssw.cmm.gui.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.GUImain;

public class SourcePaneListener implements MouseListener {
	
	public SourcePaneListener( GUImain main ) {
		this.main = main;
		line = -1;
	}
	
	private final GUImain main;
	
	private int line;
	
	public void setLine(int line) {
		this.line = line;
	}
	
	private void undoChange() {
		
		if( this.line >= 0 ) {
		
			if( this.main.getRightPanel().getDebugPanel().getControlPanel().getListener().isReadyMode() ) {
				this.main.getLeftPanel().setReadyHighlighter();
			}
			if( this.main.getRightPanel().getDebugPanel().getControlPanel().getListener().isPauseMode() ||
					this.main.getRightPanel().getDebugPanel().getControlPanel().getListener().isRunMode()) {
				this.main.getLeftPanel().highlightSourceCodeDirectly(line);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		undoChange();
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		undoChange();
	}
}
