package at.jku.ssw.cmm.gui.event;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import at.jku.ssw.cmm.gui.GUImainSettings;
import at.jku.ssw.cmm.gui.file.SaveDialog;

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
	 * @param settings
	 *            A reference to the main GUI's configuration object
	 * @param saveDialog
	 *            A reference to the main window's save dialog
	 */
	public WindowEventListener(JFrame jFrame, GUImainSettings settings,
			SaveDialog saveDialog) {
		this.jFrame = jFrame;
		this.settings = settings;
		this.saveDialog = saveDialog;
	}

	/**
	 * A reference to the main GUI's configuration object
	 */
	private final GUImainSettings settings;

	/**
	 * The frame of the main GUI window
	 */
	private final JFrame jFrame;

	/**
	 * A reference to the main window's save dialog
	 */
	private final SaveDialog saveDialog;

	@Override
	public void windowOpened(WindowEvent e) {
	}

	// User is closing program
	@Override
	public void windowClosing(WindowEvent e) {

		// Call save and close manager
		doSaveCloseProgram(this.jFrame, this.settings, this.saveDialog);
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

	public static void doSaveCloseProgram(JFrame jFrame, GUImainSettings settings, SaveDialog saveDialog) {
		// Warning if current file is not saved -> opens a warning dialog
		if (settings.getPath() == null) {

			// Custom button text
			Object[] options = { _("Yes"), _("No") };

			// Init warning dialog with two buttons
			int n = JOptionPane.showOptionDialog(jFrame,
					_("Do you want to save the new file?"),
					_("Closing the C Compact IDE"), JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, // do not use a custom
														// Icon
					options, // the titles of buttons
					options[0]); // default button title

			if (n == JOptionPane.YES_OPTION) {
				// Open a save dialog to save current source code
				if( saveDialog.doSaveAs() )
					updateAndExit(jFrame, settings);
				
				return;
			} else if (n == JOptionPane.NO_OPTION)
				updateAndExit(jFrame, settings);
			else
				return;
		}

		// Warning if last changes are not saved -> opens a warning dialog
		else if (jFrame.getTitle().endsWith("*")) {

			// Custom button text
			Object[] options = { _("Save now"), _("Close without saving") };

			// Init warning dialog with two buttons
			int n = JOptionPane.showOptionDialog(jFrame,
					_("The current file has not yet been saved!"),
					_("Closing the C Compact IDE"), JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null, // do not use a custom
														// Icon
					options, // the titles of buttons
					options[0]); // default button title

			if (n == JOptionPane.YES_OPTION) {
				// Save the last changes to current file path
				if(saveDialog.directSave())
					return;
				updateAndExit(jFrame, settings);
			} else if (n == JOptionPane.NO_OPTION)
				updateAndExit(jFrame, settings);
			else
				return;
		}

		updateAndExit(jFrame, settings);
	}

	private static void updateAndExit(JFrame jFrame, GUImainSettings settings) {

		// ...and saved
		System.out.println("[up to date]");
		settings.writeXMLsettings();

		System.exit(0);
	}

}
