package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

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
import at.jku.ssw.cmm.gui.quest.GUIquestMain;
import at.jku.ssw.cmm.launcher.GUILauncherMain;
import at.jku.ssw.cmm.profile.Profile;

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
		final GUImain app = new GUImain(new GUImainSettings(null));

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

	private MenuBarControl menuBarControl;

	/**
	 * Unicode character of the breakpoint.
	 */
	public static final char BREAKPOINT = '\u2326';

	/**
	 * The current version of C Compact, used as window title.
	 */
	public static final String VERSION = "C Compact Alpha 1.2 (Build 4)";

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
		/*try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
        
        // set SeaGlass laf if available
        /*try {
            UIManager.installLookAndFeel("SeaGlass", "com.seaglasslookandfeel.SeaGlassLookAndFeel");
            UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
        } catch (Exception e) {
            System.err.println("Seaglass LAF not available using Ocean.");
            try {
                UIManager.setLookAndFeel(new MetalLookAndFeel());
            } catch (Exception e2) {
                System.err.println("Unable to use Ocean LAF using default.");
            }
        }*/

		/*try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		    	System.out.println(info.getName());
		        if ("GTK+".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}*/
		
		// EDT Thread analysis
		if (SwingUtilities.isEventDispatchThread())
			DebugShell.out(State.LOG, Area.SYSTEM, "main GUI running on EDT.");

		// Load translations
		Language.loadLanguage(this.settings.getLanguage() + ".po");

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
		sp.setLeftComponent(this.leftPanelControl.init());

		// Initialize right part of the GUI
		this.rightPanelControl = new GUIrightPanel(this);
		sp.setRightComponent(this.rightPanelControl.init());
		
		// Set split pane properties
		sp.setPreferredSize(new Dimension(800, 500));
		sp.setDividerLocation(0.6);
		sp.setResizeWeight(1.0);

		// Initialize the save dialog object
		this.saveDialog = new SaveDialog(this.jFrame,
				this.leftPanelControl.getSourcePane(),
				this.leftPanelControl.getInputPane(), this.getSettings());

		// Initialize the window listeners
		this.jFrame.addWindowListener(new WindowEventListener(this.jFrame, this));
		this.jFrame.addMouseMotionListener(new CursorListener(
				this.leftPanelControl.getSourcePane()));

		// Initialize the menubar
		MenuBarEventListener listener = new MenuBarEventListener(this.jFrame,
				this.leftPanelControl.getSourcePane(),
				this.leftPanelControl.getInputPane(), this, this.getSettings(),
				this.rightPanelControl.getDebugPanel(), this.saveDialog, this.settings.getProfile());

		this.menuBarControl = new MenuBarControl(listener);

		InitMenuBar.initFileM(this.jFrame, this, this.menuBarControl, listener);

		this.menuBarControl.updateRecentFiles(this.settings.getRecentFiles(), this.settings.getCMMFilePath());

		// Set debug panel to ready mode
		this.rightPanelControl.getDebugPanel().setReadyMode();

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
	 * Repaints the main GUI. Note: Method from interface <i>GUImod</i>
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void repaint() {
		jFrame.repaint();
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}

	/**
	 * Sets the title of the main GUI window. Note: Method from interface
	 * <i>GUImod</i>
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
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

	public void setFileChanged() {
		if (!this.jFrame.getTitle().endsWith("*"))
			this.jFrame.setTitle(this.jFrame.getTitle() + "*");
	}

	public void setFileSaved() {
		if (this.jFrame.getTitle().endsWith("*")) {
			this.jFrame.setTitle(this.jFrame.getTitle().substring(0,
					this.jFrame.getTitle().length() - 1));
		}
	}

	public GUIleftPanel getLeftPanel() {
		return this.leftPanelControl;
	}
	
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
		if (Profile.getActiveProfile() == null) {
			this.dispose();
			GUILauncherMain.init();
			// selectProfile();
		}

		// Ignoring Quest GUI if there is no active Profile
		if (Profile.getActiveProfile() != null)
			new GUIquestMain(this.rightPanelControl.getQuestPanel()).start();
	}

	public JPanel getGlassPane() {

		return ((JPanel) this.jFrame.getGlassPane());
	}

	public void invokePopup(JPanel popup, int x, int y, int width, int height) {

		((JPanel) this.jFrame.getGlassPane()).add(popup);
		((JPanel) this.jFrame.getGlassPane())
				.addMouseListener(new PopupCloseListener(((JPanel) this.jFrame
						.getGlassPane()), popup, x, y, width, height));
		((JPanel) this.jFrame.getGlassPane()).validate();
		((JPanel) this.jFrame.getGlassPane()).repaint();
	}

	public void setReadyMode() {

		this.leftPanelControl.setReadyMode();
		this.rightPanelControl.hideErrorPanel();
	}

	public void setErrorMode(String msg, int line) {

		this.leftPanelControl.setErrorMode(line);
		this.rightPanelControl.showErrorPanel(msg);
	}

	public void setRunMode() {

		this.leftPanelControl.setRunMode();
		this.rightPanelControl.hideErrorPanel();
	}

	public void setPauseMode() {

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

	public SaveDialog getSaveManager() {
		return this.saveDialog;
	}

	public GUImainSettings getSettings() {
		return settings;
	}

	// TODO Save dialog
	public void dispose() {
		saveDialog.safeCheck(_("Disposing"));
		
		this.jFrame.dispose();
	}

	public boolean hasAdvancedGUI() {
		return this.settings.hasProfile();
	}
}
