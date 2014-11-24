package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.Dimension;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.gettext.Language;
import at.jku.ssw.cmm.gui.event.CursorListener;
import at.jku.ssw.cmm.gui.event.WindowComponentListener;
import at.jku.ssw.cmm.gui.event.WindowEventListener;
import at.jku.ssw.cmm.gui.file.SaveDialog;
import at.jku.ssw.cmm.gui.init.InitMenuBar;
import at.jku.ssw.cmm.gui.popup.PopupCloseListener;
import at.jku.ssw.cmm.gui.quest.GUIquestMain;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.XMLReadingException;

import java.io.File;

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
		GUImain app = new GUImain(new GUImainSettings());

		boolean test = false;

		for (String s : args)
			if (s.equals("-t"))
				test = true;

		app.start(test);
	
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
	 * If true, GUI options for quest and profile functions are shown. <br>
	 * If false, quest/profile GUI is hidden.
	 */
	public static final boolean ADVANCED_GUI = false;

	/**
	 * The current version of C Compact, used as window title.
	 */
	public static final String VERSION = "C Compact Alpha 1.2 (Build 0)";
	
	public static final String LANGUAGE = "de";

	/**
	 * Constructor requires specific configuration for the window (settings)
	 * 
	 * @param settings
	 *            Configuration object for the main GUI.
	 */
	private GUImain(GUImainSettings settings) {
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
	 */
	private void start(boolean test) {
		
		if (SwingUtilities.isEventDispatchThread())
			DebugShell.out(State.LOG, Area.SYSTEM, "main GUI running on EDT.");

		// Load translations
		Language.loadLanguage(LANGUAGE + ".po");

		// Initialize the window
		this.jFrame = new JFrame(VERSION);
		this.jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.jFrame.getContentPane().setPreferredSize(new Dimension(800, 500));
		this.jFrame.setMinimumSize(new Dimension(600, 400));
		this.jFrame.setLocation(10, 10);

		JPanel glassPane = new JPanel();
		glassPane.setOpaque(false);
		glassPane.setLayout(null);

		jFrame.setGlassPane(glassPane);
		jFrame.getGlassPane().setVisible(true);

		// base class for all swing components, except the top level containers
		//JComponent cp = (JComponent) this.jFrame.getContentPane();
		//cp.setLayout(new BorderLayout());
		
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.jFrame.setContentPane(sp);

		
		this.updateWinFileName();

		this.leftPanelControl = new GUIleftPanel(this);
		
		sp.setLeftComponent(this.leftPanelControl.init());

		// Right part of the GUI
		this.rightPanelControl = new GUIrightPanel();
		sp.setRightComponent(this.rightPanelControl.init(this));
		
		sp.setPreferredSize(new Dimension(800, 500));
		sp.setDividerLocation(0.6);
		sp.setOneTouchExpandable(true);
		sp.setResizeWeight(1.0);

		// Initialize the save dialog object
		this.saveDialog = new SaveDialog(this.jFrame, this.leftPanelControl.getSourcePane(),
				this.leftPanelControl.getInputPane(), this.getSettings());

		// Initialize the window listener
		this.jFrame.addWindowListener(new WindowEventListener(this.jFrame,
				this.getSettings(), this.saveDialog));
		
		// Initialize window component listener
		this.jFrame.addComponentListener(new WindowComponentListener(this.jFrame, this.leftPanelControl.getSourcePane(), this.leftPanelControl.getInputPane(), this.getSettings()));

		this.jFrame.addMouseMotionListener(new CursorListener(this.leftPanelControl.getSourcePane()));
		
		// Menubar
		this.menuBarControl = new MenuBarControl();
		InitMenuBar.initFileM(this.jFrame, this.leftPanelControl.getSourcePane(), this.leftPanelControl.getInputPane(),
				this, this.getSettings(), this.rightPanelControl.getDebugPanel(),
				this.menuBarControl, this.saveDialog);
		
		this.rightPanelControl.getDebugPanel().setReadyMode();

		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.pack();
		this.jFrame.setVisible(true);

		if (test)
			System.exit(0);
	}

	

	/**
	 * Repaints the main GUI.
	 * Note: Method from interface <i>GUImod</i>
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 */
	public void repaint() {
		jFrame.repaint();
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}

	/**
	 * Sets the title of the main GUI window.
	 * Note: Method from interface <i>GUImod</i>
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 */
	public void updateWinFileName() {
		if (this.getSettings().getPath() == null) {
			this.jFrame.setTitle(VERSION + " - " + _("Unnamed"));
		} else
			this.jFrame.setTitle(VERSION + " - " + this.getSettings().getPath());
	}

	/**
	 * 
	 * @return Name of the current file without path, eg "file2.cmm"
	 */
	public String getFileName() {

		// final String sep = System.getProperty("file.separator");
		String s = this.getSettings().getPath();

		if (s == null)
			return _("Unnamed");

		File file = new File(s);
		s = file.getName();

		return s;
	}

	/**
	 * 
	 * @return Name of the current cmm file with path, eg "demo/file2.cmm" <br>
	 * <i>WARNING: File path can be absolute or relative</i>
	 */
	public String getFileNameAndPath() {

		return this.getSettings().getPath();
	}
	
	public boolean hasPath(){
		return this.getSettings().hasPath();
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

	public GUIleftPanel getLeftPanel(){
		return this.leftPanelControl;
	}

	/**
	 * @return The complete path to the directory where the currently edited *.cmm file is saved
	 */
	public String getWorkingDirectory() {
		if (this.getSettings().getPath() == null)
			return null;

		File f = new File(this.getSettings().getPath());
		if (f.getParentFile() != null)
			return f.getParentFile().getAbsolutePath();
		return null;
	}

	/**
	 * Invokes the Quest GUI window
	 */
	public void startQuestGUI(){
		DebugShell.out(State.LOG, Area.GUI, "Opening Quest Selection Window...");
		//open profile selector on empty profile

		//Select Profile if there is no active Profile
		if(Profile.getActiveProfile() == null)
			selectProfile();
		
		//Ignoring Quest GUI if there is no active Profile
		if(Profile.getActiveProfile() != null)
			new GUIquestMain(this.rightPanelControl.getQuestPanel()).start();
	}

	/**
	 * TODO Profile Preview
	 * Invokes the profile selection dialog
	 */
	public void selectProfile(){
		DebugShell.out(State.LOG, Area.GUI, "Opening Profile Selection Window...");
		JFileChooser chooser = new JFileChooser("Select a profile...");
		//chooser.setFileFilter(new FileNameExtensionFilter("C Compact Profile", "xml"));
				
		//Only Directorys can be choosen
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		//TODO
		//chooser.setAccessory(new ProfilePreview(chooser));
		
		//Disable Renaming etc.
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		
		//chooser.showOpenDialog(jFrame);		
	    int ret = chooser.showDialog(null, null);
	    
	    if (ret == JFileChooser.APPROVE_OPTION) 
			if(chooser.getSelectedFile() != null && chooser.getSelectedFile().getPath() != null ){
				
				String path = chooser.getSelectedFile().getAbsolutePath();
				try {
					Profile.setActiveProfile(Profile.ReadProfile(path));
					GUIquestPanel questPanel = this.rightPanelControl.getQuestPanel();
					
					questPanel.RefreshProfile(Profile.getActiveProfile());
					
				} catch (XMLReadingException | IndexOutOfBoundsException e) {
					
					System.err.println(path + " Wrong Profile Choosen - no profile.xml found");
	        		
					JFrame frame = new JFrame("Warnung");
	        		JOptionPane.showMessageDialog(frame,"Falsches Profil ausgewaehlt.","Warnung:",
	        			    JOptionPane.WARNING_MESSAGE);
	        		
					//Open the Selection Window again
					selectProfile();
				}
				DebugShell.out(State.LOG, Area.GUI, "Profile Chooser Path:"
						+ chooser.getSelectedFile().getAbsolutePath());
			}
	}

	/**
	 * Saves the current *.cmm file if there are unsaved changes
	 */
	public void saveIfNecessary() {

		if (this.getSettings().getPath() == null)
			this.saveDialog.doSaveAs();
		else
			this.saveDialog.directSave();

		this.setFileSaved();
		this.updateWinFileName();
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
		
		System.out.println(" <<< Hiding error panel ");

		this.leftPanelControl.setReadyMode();
		this.rightPanelControl.hideErrorPanel();
	}

	public void setErrorMode(String msg, int line) {
		
		System.out.println(" >>> Showing error panel ");
		
		this.leftPanelControl.setErrorMode(msg, line);
		this.rightPanelControl.showErrorPanel(msg);
	}

	public void setRunMode() {
		
		System.out.println(" <<< Hiding error panel ");
		
		this.leftPanelControl.setRunMode();
		this.rightPanelControl.hideErrorPanel();
	}

	public void setPauseMode() {
		
		System.out.println(" <<< Hiding error panel ");
		
		this.leftPanelControl.setPauseMode();
		this.rightPanelControl.hideErrorPanel();
	}
	
	/**
	 * Makes all text fields of the main GUI uneditable. Should happen before interpreter starts running
	 * so that the source code can't be changed during runtime.
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 */
	public void lockInput() {
		this.menuBarControl.lockAll();
	}
	
	/**
	 * Makes all text fields of the main GUI editable. Should happen after the interpreter has
	 * finished running.
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
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
}
