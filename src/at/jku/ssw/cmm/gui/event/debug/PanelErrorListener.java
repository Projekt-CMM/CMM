package at.jku.ssw.cmm.gui.event.debug;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.mod.GUImainMod;

/**
 * This class is a listener for the right panel of the main GUI in the "error" mode
 * (interface for compiling errors). Actually, this class only watches the "view" button,
 * which highlights the line of source code where the error is considered to be.
 * 
 * @author fabian
 *
 */
public class PanelErrorListener implements MouseListener {
	
	/**
	 * This class is a listener for the right panel of the main GUI in the "error" mode
	 * (interface for compiling errors). Actually, this class only watches the "view" button,
	 * which highlights the line of source code where the error is considered to be.
	 * 
	 * @param p A reference to the right GUI panel
	 * @param modifier An interface for modifications on the main GUI
	 */
	public PanelErrorListener( GUIdebugPanel p, GUImainMod modifier ){
		this.modifier = modifier;
		this.p = p;
	}

	/**
	 * An interface for modifications on the main GUI
	 */
	private final GUImainMod modifier;
	
	/**
	 * A reference to the right GUI panel
	 */
	private final GUIdebugPanel p;

	@Override
	public void mouseClicked(MouseEvent e) {
		
		//Debug message on the shell
		System.out.println( "Showing error on line " + p.getErrorLine() + ", col " + p.getErrorCol() );
		
		//Highlight the given line in the source code panel
		this.modifier.highlightSourceCode(p.getCompleteErrorLine());
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
