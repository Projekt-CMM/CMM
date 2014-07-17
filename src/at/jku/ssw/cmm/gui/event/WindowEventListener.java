package at.jku.ssw.cmm.gui.event;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import at.jku.ssw.cmm.gui.GUImainSettings;
import at.jku.ssw.cmm.gui.file.SaveDialog;

/**
 * Event listener for the main GUI window. Used for triggering an event when the user closes the window.
 * The save dialog call and exit manager itself is located in a separate static method because by
 * this it can also be called if the window is closed by some other event or reason.
 * 
 * @author fabian
 *
 */
public class WindowEventListener implements WindowListener {
	
	public WindowEventListener( JFrame jFrame, GUImainSettings settings, SaveDialog saveDialog ){
		this.jFrame = jFrame;
		this.settings = settings;
		this.saveDialog = saveDialog;
	}
	
	//A reference to the main GUI's configuration object
	private final GUImainSettings settings;
	
	//The frame of the main GUI window
	private final JFrame jFrame;
	
	//A reference to the main window's save dialog
	private final SaveDialog saveDialog;

	@Override
	public void windowOpened(WindowEvent e) {
		// Auto-generated method stub
		
	}

	//User is closing program
	@Override
	public void windowClosing(WindowEvent e) {
		
		//Call save and close manager
		doSaveCloseProgram( this.jFrame, this.settings, this.saveDialog );
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// Auto-generated method stub
		
	}
	
	public static void doSaveCloseProgram( JFrame jFrame, GUImainSettings settings, SaveDialog saveDialog ){
		//Warning if current file is not saved -> opens a warning dialog
		if( settings.getPath() == null ){
					
			//Custom button text
			Object[] options = {"Save now", "Close without saving"};
					
			//Init warning dialog with two buttons
			int n = JOptionPane.showOptionDialog( jFrame,
				"The current file has not yet been saved!",
				"Closing the C-- IDE",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,     			//do not use a custom Icon
				options,  			//the titles of buttons
				options[0]); 		//default button title
					
				//What did the user decide for?
				switch( n ){
					case JOptionPane.YES_OPTION:
						//Open a save dialog to save current source code
						saveDialog.doSaveAs();
						break;
					case JOptionPane.NO_OPTION:
						//User does not want to save
						System.exit(0);
						break;
				}
			}
				
			//Configuration data of the window is updated...
			settings.setSizeX(jFrame.getWidth());
			settings.setSizeY(jFrame.getHeight());
			settings.setPosX(jFrame.getX());
			settings.setPosY(jFrame.getY());
				
			// ...and saved
			settings.saveConfigFile();
	}

}
