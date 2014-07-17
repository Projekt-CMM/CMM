package at.jku.ssw.cmm.gui.event;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import at.jku.ssw.cmm.gui.GUImainSettings;
import at.jku.ssw.cmm.gui.GUIrightPanel;

/**
 * Event listener for the main window. Controls automatic resizing of the source code text pane.
 * 
 * @author fabian
 *
 */
public class WindowComponentListener implements ComponentListener {
	
	/**
	 * 
	 * @param jFrame The main window frame
	 * @param jSourcePane The text pane for the source code
	 * @param settings The main window configuration object.
	 */
	public WindowComponentListener( JFrame jFrame, RSyntaxTextArea jSourcePane, GUImainSettings settings, GUIrightPanel rPanel ){
		this.jFrame = jFrame;
		this.jSourcePane = jSourcePane;
		this.settings = settings;
		this.rPanel = rPanel;
	}
	
	private final JFrame jFrame;
	private final RSyntaxTextArea jSourcePane;
	private final GUImainSettings settings;
	@SuppressWarnings("unused")
	private final GUIrightPanel rPanel;

	@Override
	public void componentResized(ComponentEvent e) {
		
		//If the window is resized, the configuration object is updated...
		this.settings.setSizeX( this.jFrame.getWidth() );
		this.settings.setSizeY( this.jFrame.getHeight() );
		
		// ...and the source code panel is resized as well.
		this.jSourcePane.setColumns( settings.getSourceSizeX() );
		this.jSourcePane.setRows( settings.getSourceSizeY() );
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// Auto-generated method stub
		
	}

}
