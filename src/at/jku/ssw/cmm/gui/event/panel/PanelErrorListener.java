package at.jku.ssw.cmm.gui.event.panel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.gui.mod.GUIrPanelMod;

/**
 * This class is a listener for the right panel of the main GUI in the "error" mode
 * (interface for compiling errors). Actually, this class only watches the "view" button,
 * which highlights the line of source code where the error is considered to be.
 * 
 * @author fabian
 *
 */
public class PanelErrorListener implements MouseListener {
	
	public PanelErrorListener( GUIrPanelMod p, GUImainMod modifier ){
		this.modifier = modifier;
		this.p = p;
	}

	//An interface for modifications on the main GUI
	private final GUImainMod modifier;
	
	//An interface for modifications on the right panel of the main GUI
	private final GUIrPanelMod p;

	@Override
	public void mouseClicked(MouseEvent e) {
		
		//Debug message on the shell
		System.out.println( "Showing error on line " + p.getErrorLine() + ", col " + p.getErrorCol() );
		
		//Highlight the given line in the source code panel
		this.modifier.highlightSourceCode(p.getErrorLine(), p.getErrorCol());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Auto-generated method stub
		
	}

}
