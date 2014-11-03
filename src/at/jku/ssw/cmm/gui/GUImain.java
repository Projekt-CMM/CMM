package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import at.jku.ssw.cmm.gui.event.SourceCodeListener;
import at.jku.ssw.cmm.gui.event.WindowComponentListener;
import at.jku.ssw.cmm.gui.event.WindowEventListener;
import at.jku.ssw.cmm.gui.file.FileManagerCode;
import at.jku.ssw.cmm.gui.file.SaveDialog;
import at.jku.ssw.cmm.gui.include.ExpandSourceCode;
import at.jku.ssw.cmm.gui.init.InitLeftPanel;
import at.jku.ssw.cmm.gui.init.InitMenuBar;
import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.gui.popup.PopupCloseListener;
import at.jku.ssw.cmm.gui.popup.PopupInterface;
import at.jku.ssw.cmm.gui.quest.GUIquestMain;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.XMLReadingException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the main function which also initializes and controls the main GUI.
 * 
 * @author fabian
 *
 */
public class GUImain implements GUImainMod, PopupInterface {

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

	/**
	 * A reference to the right panel control class. <br>
	 * The right panel contains the debugger and profile/quest info tabs.
	 */
	private GUIrightPanel rightPanelControl;

	/**
	 * The text panel with the source code.
	 */
	private RSyntaxTextArea jSourcePane;

	/**
	 * The text panel with input data for the cmm program.
	 */
	private JTextPane jInputPane;

	/**
	 * The text panel for the output stream of the cmm program.
	 */
	private JTextArea jOutputPane;

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

	// TODO make codeRegister thread safe
	/**
	 * A list which contains the lines of all libraries in the complete source
	 * code. When a library is loaded, its code is pasted to the source code of
	 * the cmm file and this complete code is given to the compiler.
	 * 
	 * codeRegister contains information about the start and end line of each
	 * library file. The array in the list is initialized as follows:
	 * <ul>
	 * <li>Start line</li>
	 * <li>End line</li>
	 * <li>File Name</li>
	 * </ul>
	 * 
	 * <i>Note: Please use ExpandSourcecode.correctLine() determine the line in
	 * the text area from the line of the complete source code. The parameters
	 * are.
	 * <ul>
	 * <li><b>int line:</b> the line in the complete source code.</li>
	 * <li><b>int codeStart:</b> the line where the original source code starts.
	 * Use this.codeRegister.get(0)[0]</li>
	 * <li><b>int files:</b> The total number of library files. Use
	 * this.codeRegister.size()</li>
	 * </ul>
	 */
	private List<Object[]> codeRegister;

	/**
	 * When input data from the input source pane is read by the cmm program,
	 * the input data has to be marked as "already read". This happens with
	 * simple highlighting. <br>
	 * The variable inputHightlightOffset is the number of characters of the
	 * input string which has already been used.
	 */
	private int inputHighlightOffset;

	private MenuBarControl menuBarControl;

	private SourceCodeListener codeListener;

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
	public static final String VERSION = "C Compact Alpha 1.1 dev";

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
			System.out.println("[EDT Analyse] Main GUI runnung on EDT.");

		// Load translations
		// Language.loadLanguage("de.po");

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
		JComponent cp = (JComponent) this.jFrame.getContentPane();
		cp.setLayout(new BorderLayout());

		// Source code component register
		this.codeRegister = new ArrayList<>();

		// Left part of the GUI
		JPanel jPanelLeft = new JPanel();
		jPanelLeft.setLayout(new BoxLayout(jPanelLeft, BoxLayout.PAGE_AXIS));
		jPanelLeft.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Text area (text pane) for source code
		this.jSourcePane = InitLeftPanel
				.initCodePane(jPanelLeft, this.settings);

		// Text area for input
		this.jInputPane = InitLeftPanel.initInputPane(jPanelLeft);

		// Text area for output
		this.jOutputPane = InitLeftPanel.initOutputPane(jPanelLeft);

		// Read last opened files
		if (this.settings.hasPath()) {
			this.jSourcePane.setText(FileManagerCode.readSourceCode(new File(
					this.settings.getPath())));
			this.jInputPane.setText(FileManagerCode.readInputData(new File(
					this.settings.getPath())));
		}
		this.updateWinFileName();

		cp.add(jPanelLeft, BorderLayout.LINE_START);

		// Right part of the GUI
		this.rightPanelControl = new GUIrightPanel(cp, (GUImainMod) this,
				(PopupInterface) this);

		// Initialize the save dialog object
		this.saveDialog = new SaveDialog(this.jFrame, this.jSourcePane,
				this.jInputPane, this.settings);

		// Initialize the window listener
		this.jFrame.addWindowListener(new WindowEventListener(this.jFrame,
				this.settings, this.saveDialog));

		// Initialize window component listener
		this.jFrame.addComponentListener(new WindowComponentListener(
				this.jFrame, this.jSourcePane, this.settings,
				this.rightPanelControl.getDebugPanel()));

		// Initialize the source panel listener
		this.codeListener = new SourceCodeListener(this);
		this.jSourcePane.getDocument().addDocumentListener(this.codeListener);
		// TODO
		// this.jInputPane.getDocument().addDocumentListener(this.codeListener);

		// Menubar
		this.menuBarControl = new MenuBarControl();
		InitMenuBar.initFileM(this.jFrame, this.jSourcePane, this.jInputPane,
				this, this.settings, this.rightPanelControl.getDebugPanel(),
				this.menuBarControl, this.saveDialog);

		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.pack();
		this.jFrame.setVisible(true);

		// Variable initialization
		this.inputHighlightOffset = 0;

		if (test)
			System.exit(0);
	}

	/**
	 * Highlights the already used characters of the input text area. Usually
	 * called while interpreter is working.
	 * 
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 */
	private void highlightInputPane() {

		DefaultStyledDocument document = new DefaultStyledDocument();

		StyleContext context = new StyleContext();

		// Highlighting style
		Style highlightStyle = context.addStyle("highlight", null);
		StyleConstants.setBackground(highlightStyle, Color.YELLOW);
		StyleConstants.setBold(highlightStyle, true);

		// Default style
		Style defaultStyle = context.addStyle("default", null);

		// Do highlighting
		try {
			document.insertString(0,
					this.jInputPane.getText().substring(inputHighlightOffset),
					defaultStyle);
			document.insertString(0,
					this.jInputPane.getText()
							.substring(0, inputHighlightOffset), highlightStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		// TODO document.addDocumentListener(this.codeListener);
		this.jInputPane.setDocument(document);
		this.jInputPane.repaint();
	}

	/*
	 * --- The methods below are implemented from the interface "GUImainMod" ---
	 * --- ...see there for comments and descriptions ---
	 */
	@Override
	public void repaint() {
		jFrame.repaint();
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}

	@Override
	public void updateWinFileName() {
		if (this.settings.getPath() == null) {
			this.jFrame.setTitle(VERSION + " - " + _("Unnamed"));
		} else
			this.jFrame.setTitle(VERSION + " - " + this.settings.getPath());
	}

	@Override
	public String getFileName() {

		// final String sep = System.getProperty("file.separator");
		String s = this.settings.getPath();

		if (s == null)
			return _("Unnamed");

		File file = new File(s);
		s = file.getName();

		return s;
	}

	@Override
	public String getFileNameAndPath() {

		return this.settings.getPath();
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

	@Override
	public String getSourceCode() {
		return this.jSourcePane.getText();
	}

	@Override
	public List<Object[]> getSourceCodeRegister() {
		return this.codeRegister;
	}

	@Override
	public String getWorkingDirectory() {
		if (this.settings.getPath() == null)
			return null;

		File f = new File(this.settings.getPath());
		if (f.getParentFile() != null)
			return f.getParentFile().getAbsolutePath();
		return null;
	}

	@Override
	public void highlightSourceCode(int line) {

		// Line out of user source code range (#include)
		if (line <= (int) this.codeRegister.get(0)[0])
			return;

		// Correct offset in source code (offset caused by includes)
		else
			line = ExpandSourceCode
					.correctLine(line, (int) this.codeRegister.get(0)[0],
							this.codeRegister.size());

		int i, l = 0;
		final String code = this.jSourcePane.getText();

		// TODO readLoopLock
		for (i = 0; l < line - 1; i++) {
			if (code.charAt(i) == '\n')
				l++;
		}

		final int i_copy = i, l_copy = l;

		if (i_copy < code.length() && l_copy < jSourcePane.getLineCount())
			jSourcePane.select(i_copy, i_copy);
		else
			jSourcePane.select(code.length(), code.length());
	}

	@Override
	public void increaseInputHighlighter() {

		this.inputHighlightOffset++;

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				highlightInputPane();
			}
		});
	}

	@Override
	public void resetInputHighlighter() {
		this.inputHighlightOffset = 0;

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				highlightInputPane();
			}
		});
	}

	@Override
	public void resetOutputTextPane() {

		this.jOutputPane.setText("");
	}

	@Override
	public void lockInput() {

		this.jSourcePane.setEditable(false);
		this.jInputPane.setEditable(false);
		this.jOutputPane.setEditable(false);

		this.rightPanelControl.lockInput();

		this.menuBarControl.lockAll();
	}

	@Override
	public void unlockInput() {

		this.jSourcePane.setEditable(true);
		this.jInputPane.setEditable(true);
		this.jOutputPane.setEditable(true);

		this.rightPanelControl.unlockInput();

		this.menuBarControl.unlockAll();
	}

	@Override
	public void outputStream(String s) {

		this.jOutputPane.append(s);
	}

	@Override
	public String getInputStream() {

		return this.jInputPane.getText();
	}

	@Override
	public void toggleBreakPoint() {

		int start = this.jSourcePane.getSelectionStart();
		int end = this.jSourcePane.getSelectionEnd();
		String code = this.jSourcePane.getText();

		for (int i = start; i >= 0; i--) {
			if (code.charAt(i) == '\n') {
				code = code.substring(0, i + 1) + BREAKPOINT
						+ code.substring(i + 1);
				start = start + 1;
				end = end + 1;
				break;
			} else if (code.charAt(i) == BREAKPOINT) {
				code = code.substring(0, i) + code.substring(i + 1);
				start = start - 1;
				end = end - 1;
				break;
			}
		}

		this.jSourcePane.setText(code);
		this.jSourcePane.select(start, end);
	}

	@Override
	public void startQuestGUI() {
		System.out.println("[log][GUImain]Opening Quest Selection Window...");
		// open profile selector on empty profile
		if (Profile.getActiveProfile() == null)
			selectProfile();

		new GUIquestMain().start();
	}

	@Override
	public void selectProfile() {
		System.out.println("[log][GUImain]Opening Profile Selection Window...");
		JFileChooser chooser = new JFileChooser(_("Select a profile..."));
		chooser.setFileFilter(new FileNameExtensionFilter(
				_("C Compact Profile"), "xml"));
		chooser.showOpenDialog(jFrame);
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);

		if (chooser.getSelectedFile() != null
				&& chooser.getSelectedFile().getPath() != null) {
			String s = chooser.getSelectedFile().getAbsolutePath();
			try {
				Profile.setActiveProfile(Profile.ReadProfile(s.substring(0,
						s.indexOf(Profile.sep + Profile.FILE_PROFILE))));
			} catch (XMLReadingException e) {
				e.printStackTrace();
			}
			System.out.println("Profile Chooser Path:"
					+ chooser.getSelectedFile().getAbsolutePath());
		}
	}

	@Override
	public void saveIfNecessary() {

		if (this.settings.getPath() == null)
			this.saveDialog.doSaveAs();
		else
			this.saveDialog.directSave();

		this.setFileSaved();
		this.updateWinFileName();
	}

	/*
	 * --- The methods below are implemented from the interface "PopupInterface"
	 * --- --- ...see there for comments and descriptions ---
	 */

	@Override
	public JPanel getGlassPane() {

		return ((JPanel) this.jFrame.getGlassPane());
	}

	@Override
	public void invokePopup(JPanel popup, int x, int y, int width, int height) {

		((JPanel) this.jFrame.getGlassPane()).add(popup);
		((JPanel) this.jFrame.getGlassPane())
				.addMouseListener(new PopupCloseListener(((JPanel) this.jFrame
						.getGlassPane()), popup, x, y, width, height));
		((JPanel) this.jFrame.getGlassPane()).validate();
		((JPanel) this.jFrame.getGlassPane()).repaint();
	}
}
