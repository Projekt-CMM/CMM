package at.jku.ssw.cmm.gui.event;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import at.jku.ssw.cmm.gui.mod.GUIrPanelMod;

/**
 * This class is a listener which is used as key listener for the source code panel
 * in the main GUI. At the moment unused
 * 
 * @author fabian
 *
 */
public class SourcePaneKeyListener implements KeyListener{

	public SourcePaneKeyListener( GUIrPanelMod p, RSyntaxTextArea jSourceTextArea ){
		this.p = p;
		this.jSourceTextArea = jSourceTextArea;
	}
	
	//Interface for changing states and graphical objects of the right panel of the main GUI.
	private final GUIrPanelMod p;
	
	private final RSyntaxTextArea jSourceTextArea;

	@Override
	public void keyTyped(KeyEvent e) {
		
		if( p.getPanelMode() != 0 && this.jSourceTextArea.isEditable() )
			p.setRightPanel(0);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Auto-generated method stub
		
	}
	
	
}
