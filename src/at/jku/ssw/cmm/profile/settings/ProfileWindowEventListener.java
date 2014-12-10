package at.jku.ssw.cmm.profile.settings;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.properties.GUImainSettings;
import at.jku.ssw.cmm.launcher.GUILauncherMain;
import at.jku.ssw.cmm.profile.Profile;

/**
 * Event Listener for the ProfileSettingsList. Used for triggering an event when the
 * user closes the window. The save dialog call and exit manager itself is
 * located in a separate static method because by this it can also be called if
 * the window is closed by some other event or reason.
 * 
 * @author peda
 *
 */

public class ProfileWindowEventListener implements WindowListener {

	/**
	 * GUIprofileSettings Reference
	 */
	private final Profile profile;
	
	
	/**
	 * A reference to the jFrame
	 */
	private final JFrame jFrame; 
	
	/**
	 * Constructor
	 * @param gui
	 */
	public ProfileWindowEventListener(Profile profile, JFrame jFrame){
		this.profile = profile;
		this.jFrame = jFrame;
	}
	
	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	/**
	 * Closing the current jFrame and opening the right Frame
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		GUIprofileSettings.dispose(profile, jFrame);		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

}
