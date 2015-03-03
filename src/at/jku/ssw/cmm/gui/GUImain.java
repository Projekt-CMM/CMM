/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.gettext.Language;
import at.jku.ssw.cmm.gui.event.CursorListener;
import at.jku.ssw.cmm.gui.event.MenuBarEventListener;
import at.jku.ssw.cmm.gui.event.WindowEventListener;
import at.jku.ssw.cmm.gui.file.SaveDialog;
import at.jku.ssw.cmm.gui.init.InitMenuBar;
import at.jku.ssw.cmm.gui.popup.PopupCloseListener;
import at.jku.ssw.cmm.gui.properties.GUImainSettings;
import at.jku.ssw.cmm.gui.quest.GUIquestSelection;
import at.jku.ssw.cmm.launcher.GUILauncherMain;

/**
 * Contains the main function which also initializes and controls the main GUI.
 * 
 * @author fabian
 *
 */
public class GUImain {

	/**
	 * Launches the program and initiates the main window.
	 * 
	 * @param args
	 *            The shell arguments.
	 */
	public static void main(String[] args) {
		
		GUImainSettings settings = new GUImainSettings(null);
		
		final GUImain app = new GUImain(settings);
		
		// Load translations
		Language.loadLanguage(settings.getLanguage() + ".po");

		boolean test = false;
		for (String s : args)
			if (s.equals("-t"))
				test = true;
		
		final boolean t = test;
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	app.start(t);
            }
		});
		

	}

	/**
	 * The frame of the window which contains the main GUI.
	 */
	private JFrame jFrame;

	/**
	 * A reference to the left panel control class. <br>
	 * The left panel contains text areas for the source code and
	 * debugger I/O data.
	 */
	private GUIleftPanel leftPanelControl;

	/**
	 * A reference to the right panel control class. <br>
	 * The right panel contains the debugger and profile/quest info tabs.
	 */
	private GUIrightPanel rightPanelControl;

	/**
	 * A reference to the general settings object which contains the path of the
	 * current file, the current screen size and the window name. <br>
	 * Will be replaced by individual profile settings in future.
	 */
	private final GUImainSettings settings;

	/**
	 * A reference to the save dialog class which manages saving the current cmm
	 * file.
	 */
	private SaveDialog saveDialog;

	/**
	 * A reference to the class which controls the menu bar
	 */
	private MenuBarControl menuBarControl;

	/**
	 * Unicode character of the breakpoint.
	 */
	public static final char BREAKPOINT = '\u2326';

	/**
	 * The current version of C Compact, used as window title.
	 */
	public static final String VERSION = "C Compact Alpha 1.3";

	/**
	 * Constructor requires specific configuration for the window (settings)
	 * 
	 * @param settings
	 *            Configuration object for the main GUI.
	 */
	public GUImain(GUImainSettings settings) {
		this.settings = settings;
	}

	/**
	 * Initializes and launches the main GUI and therefore the main part of the
	 * program. <b>This is not the static main function!</b> Running this method
	 * requires calling a constructor with configuration data before (see above
	 * in code).
	 * 
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * 
	 * @param test
	 *            TRUE if program shall exit after init (for GUI test)
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 */
	public void start(boolean test) {
		//Setting the last Quest of the Profile
		settings.setCurrentQuestFile();
		
		
		// EDT Thread analysis
		if (SwingUtilities.isEventDispatchThread())
			DebugShell.out(State.LOG, Area.SYSTEM, "main GUI running on EDT.");

		// Initialize the window
		this.jFrame = new JFrame(VERSION);
		this.jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.jFrame.getContentPane().setPreferredSize(new Dimension(800, 500));
		this.jFrame.setMinimumSize(new Dimension(600, 400));
		this.jFrame.setLocation(10, 10);

		// Initialize glass pane -> used for popups
		JPanel glassPane = new JPanel();
		glassPane.setOpaque(false);
		glassPane.setLayout(null);

		// Assign glass pane to jFrame
		jFrame.setGlassPane(glassPane);
		jFrame.getGlassPane().setVisible(true);

		// Initialize the split pane which separates the text fields from
		// the debugging, error and quest panels
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.jFrame.setContentPane(sp);

		// Initialize left panel (text fields, ...)
		this.leftPanelControl = new GUIleftPanel(this);
		sp.setLeftComponent(this.leftPanelControl.init(this.jFrame));

		// Initialize right part of the GUI
		this.rightPanelControl = new GUIrightPanel(this);
		sp.setRightComponent(this.rightPanelControl.init());
		
		// Set split pane properties
		sp.setPreferredSize(new Dimension(800, 500));
		sp.setDividerLocation(0.6);
		sp.setResizeWeight(1.0);
		
		sp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		  .put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "none");
		
		BasicSplitPaneUI ui = (BasicSplitPaneUI)sp.getUI();
		BasicSplitPaneDivider divider = ui.getDivider();
		divider.addMouseListener(
			new CursorListener(this.jFrame, divider, new Cursor(Cursor.E_RESIZE_CURSOR))
		);

		// Initialize the save dialog object
		this.saveDialog = new SaveDialog(this.jFrame,
				this.leftPanelControl.getSourcePane(),
				this.leftPanelControl.getInputPane(), this.getSettings());

		// Initialize the window listeners
		this.jFrame.addWindowListener(new WindowEventListener(this.jFrame, this));
		
		// Initialize cursor listeners
		// These listeners change the cursors for a specific component,
		// eg. a text field gets a text cursor
		this.leftPanelControl.getSourcePane().addMouseListener(
			new CursorListener(this.jFrame, this.leftPanelControl.getSourcePane(), new Cursor(Cursor.TEXT_CURSOR))
		);
		
		this.leftPanelControl.getInputPane().addMouseListener(
			new CursorListener(this.jFrame, this.leftPanelControl.getInputPane(), new Cursor(Cursor.TEXT_CURSOR))
		);
		
		this.leftPanelControl.getOutputPane().addMouseListener(
			new CursorListener(this.jFrame, this.leftPanelControl.getOutputPane(), new Cursor(Cursor.TEXT_CURSOR))
		);

		// Initialize the menubar event listener
		MenuBarEventListener listener = new MenuBarEventListener(this.jFrame, this);

		this.menuBarControl = new MenuBarControl(listener);

		InitMenuBar.initFileM(this.jFrame, this, this.menuBarControl, listener, this.rightPanelControl.getDebugPanel().getControlPanel());
		
		// Set debug panel to ready mode
		this.rightPanelControl.getDebugPanel().setReadyMode();
		
		this.menuBarControl.updateRecentFiles(this.settings.getRecentFiles(), this.settings.getCMMFilePath());
		this.menuBarControl.updateUndoRedo(this.leftPanelControl.getSourcePane(), false);

		// Update window name
		this.updateWinFileName();

		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.pack();
		this.jFrame.setVisible(true);

		// Exit if this was just a test
		if (test)
			System.exit(0);
	}

	/**
	 * Repaints the main GUI
	 * <br>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 */
	public void repaint() {
		jFrame.repaint();
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}
	
	/**
	 * @return The JFrame of the main GUI
	 */
	public JFrame getJFrame() {
		return this.jFrame;
	}

	/**
	 * Updates the title of the main window so that it displays the current file's name
	 * <br>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 */
	public void updateWinFileName() {
		if (this.getSettings().getCMMFilePath() == null) {
			this.jFrame.setTitle(VERSION + " - " + _("Unnamed"));
		} else {
			this.jFrame.setTitle(VERSION + " - "
					+ this.getSettings().getCMMFilePath());
			this.menuBarControl.updateRecentFiles(this.settings
					.getRecentFiles(), this.settings.getCMMFilePath());
		}
	}

	/**
	 * This method should be called by document listeners when the source code
	 * or the input data has been changed by the user
	 */
	public void setFileChanged() {
		if (!this.jFrame.getTitle().endsWith("*"))
			this.jFrame.setTitle(this.jFrame.getTitle() + "*");
	}

	/**
	 * This method should be called when the currently edited file has been saved
	 */
	public void setFileSaved() {
		if (this.jFrame.getTitle().endsWith("*")) {
			this.jFrame.setTitle(this.jFrame.getTitle().substring(0,
					this.jFrame.getTitle().length() - 1));
		}
	}
	
	/**
	 * @return TRUE if there haven been changes on the source code or input data
	 * 			since the last safe, otherwise FALSE
	 */
	public boolean isFileChanged() {
		return this.jFrame.getTitle().endsWith("*");
	}

	/**
	 * @return The controller class for the left panel
	 */
	public GUIleftPanel getLeftPanel() {
		return this.leftPanelControl;
	}
	
	/**
	 * @return The controller class for the right panel
	 */
	public GUIrightPanel getRightPanel() {
		return this.rightPanelControl;
	}

	/**
	 * Invokes the Quest GUI window
	 */
	public void startQuestGUI() {
		DebugShell
				.out(State.LOG, Area.GUI, "Opening Quest Selection Window...");
		// open profile selector on empty profile

		// Select Profile if there is no active Profile
		if (this.getSettings().getProfile() == null) {
			this.dispose();
			new GUILauncherMain();
			// selectProfile();
		}

		// Ignoring Quest GUI if there is no active Profile
		if (this.getSettings().getProfile() != null)
			new GUIquestSelection(this, this.rightPanelControl.getTestPanel()).init();
		
		
	}

	/**
	 * @return The glass pane of the main window which is used for popups
	 */
	public JPanel getGlassPane() {

		return ((JPanel) this.jFrame.getGlassPane());
	}

	/**
	 * Creates a popup containing the given panel
	 * 
	 * @param popup The content of the popup
	 * @param x The x position of the popup
	 * @param y The y position of the popup
	 * @param width The width of the popup
	 * @param height The height of the popup
	 */
	public void invokePopup(JPanel popup, int x, int y, int width, int height) {

		((JPanel) this.jFrame.getGlassPane()).add(popup);
		((JPanel) this.jFrame.getGlassPane())
				.addMouseListener(new PopupCloseListener(((JPanel) this.jFrame
						.getGlassPane()), popup, x, y, width, height));
		((JPanel) this.jFrame.getGlassPane()).validate();
		((JPanel) this.jFrame.getGlassPane()).repaint();
	}

	/**
	 * Sets main GUI and all child components to ready mode
	 */
	public void setReadyMode() {
		
		this.menuBarControl.updateUndoRedo(this.leftPanelControl.getSourcePane(), false);

		this.leftPanelControl.setReadyMode();
		this.rightPanelControl.hideErrorPanel();
	}

	/**
	 * Sets main GUI and all child components to error mode
	 */
	public void setErrorMode(String msg, String file, int line) {
		
		this.menuBarControl.updateUndoRedo(this.leftPanelControl.getSourcePane(), false);
		this.leftPanelControl.setErrorMode(file, this.rightPanelControl.showErrorPanel(msg), line);
	}

	/**
	 * Sets main GUI and all child components to run mode
	 */
	public void setRunMode() {
		
		this.menuBarControl.updateUndoRedo(this.leftPanelControl.getSourcePane(), true);

		this.leftPanelControl.setRunMode();
		this.rightPanelControl.hideErrorPanel();
	}

	/**
	 * Sets main GUI and all child components to pause mode
	 */
	public void setPauseMode() {
		
		this.menuBarControl.updateUndoRedo(this.leftPanelControl.getSourcePane(), true);

		this.leftPanelControl.setPauseMode();
		this.rightPanelControl.hideErrorPanel();
	}

	/**
	 * Makes all text fields of the main GUI uneditable. Should happen before
	 * interpreter starts running so that the source code can't be changed
	 * during runtime.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void lockInput() {

		this.menuBarControl.lockAll();
	}

	/**
	 * Makes all text fields of the main GUI editable. Should happen after the
	 * interpreter has finished running.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void unlockInput() {
		this.menuBarControl.unlockAll();
	}

	/**
	 * @return The save manager class which controls saving files
	 * 		and the save dialogues
	 */
	public SaveDialog getSaveManager() {
		return this.saveDialog;
	}

	/**
	 * @return The global settings object
	 */
	public GUImainSettings getSettings() {
		return settings;
	}

	/**
	 * Cleanly closes the main window and asks the user to save his/her work.
	 * Do NEVER call <i>jFrame.dispose()</i> directly; use this method.
	 */
	public void dispose() {
		saveDialog.safeCheck(_("Disposing"));
		
		this.jFrame.dispose();
	}

	/**
	 * @return TRUE if the GUI is running in profile mode,
	 * 			FALSE if C Compact has been launched without
	 * 			selecting a user profile.
	 */
	public boolean hasAdvancedGUI() {
		return this.settings.hasProfile();
	}
}
