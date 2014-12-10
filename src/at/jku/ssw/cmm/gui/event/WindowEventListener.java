package at.jku.ssw.cmm.gui.event;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.properties.GUImainSettings;

/**
 * Event listener for the main GUI window. Used for triggering an event when the
 * user closes the window. The save dialog call and exit manager itself is
 * located in a separate static method because by this it can also be called if
 * the window is closed by some other event or reason.
 * 
 * @author fabian
 *
 */
public class WindowEventListener implements WindowListener {

	/**
	 * Event listener for the main GUI window. Used for triggering an event when
	 * the user closes the window. The save dialog call and exit manager itself
	 * is located in a separate static method because by this it can also be
	 * called if the window is closed by some other event or reason.
	 * 
	 * @param jFrame
	 *            The frame of the main GUI window
	 * @param main
	 *            A reference to the main GUI
	 */
	public WindowEventListener(JFrame jFrame, GUImain main) {
		this.jFrame = jFrame;
		this.main = main;
	}

	/**
	 * A reference to the main frame of the GUI
	 */
	private final JFrame jFrame;

	/**
	 * A reference to the main GUI
	 */
	private final GUImain main;

	@Override
	public void windowOpened(WindowEvent e) {
	}

	// User is closing program
	@Override
	public void windowClosing(WindowEvent e) {

		if (main.getSaveManager().safeCheck(_("Closing C Compact")))
			updateAndExit(jFrame, main.getSettings());
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	public static void updateAndExit(JFrame jFrame, GUImainSettings settings) {

		// ...and saved
		System.out.println("[up to date]");
		settings.writeXMLsettings();

		System.exit(0);
	}

}
