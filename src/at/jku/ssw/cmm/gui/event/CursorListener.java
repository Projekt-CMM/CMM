package at.jku.ssw.cmm.gui.event;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class CursorListener implements MouseMotionListener {
	
	public CursorListener( RSyntaxTextArea sourcePane ){
		this.sourcePane = sourcePane;
	}
	
	private final RSyntaxTextArea sourcePane;

	@Override
	public void mouseDragged(MouseEvent e) {
		
		final int x = e.getX();
        final int y = e.getY();
        // only display a hand if the cursor is over the items
        if (sourcePane != null && sourcePane.contains(x, y)) {
        	sourcePane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        } else {
        	sourcePane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
		
	}
}
