package at.jku.ssw.cmm.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import at.jku.ssw.cmm.gui.event.SourcePaneKeyListener;
import at.jku.ssw.cmm.gui.event.WindowComponentListener;
import at.jku.ssw.cmm.gui.event.WindowEventListener;
import at.jku.ssw.cmm.gui.file.FileManagerCode;
import at.jku.ssw.cmm.gui.file.SaveDialog;
import at.jku.ssw.cmm.gui.include.ExpandSourceCode;
import at.jku.ssw.cmm.gui.init.InitLeftPanel;
import at.jku.ssw.cmm.gui.init.InitMenuBar;
import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.gui.mod.GUIrPanelMod;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the main function which also initializes and controls the main GUI.
 * 
 * @author fabian
 *
 */
public class GUImain implements GUImainMod {

	/**
	 * Launches the program and initiates the main window.
	 * 
	 * @param args
	 *            The shell arguments.
	 */
	public static void main(String[] args) {
		GUImain app = new GUImain(new GUImainSettings());
		app.start();
	}

	private JFrame jFrame;

	private GUIrightPanel rightPanelControl;

	private RSyntaxTextArea jSourcePane;

	private JTextArea jInputPane;

	private JTextArea jOutputPane;

	private final GUImainSettings settings;

	private SaveDialog saveDialog;

	//TODO make codeRegister thread safe
	private List<Object[]> codeRegister;

	private int inputHighlightOffset;

	private Object readLoopLock;

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
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	private void start() {
		
		if( SwingUtilities.isEventDispatchThread() )
			System.out.println("GUI runnung on EDT.");

		// Initialize the window
		this.jFrame = new JFrame("C-- Entwicklungsumgebung");
		this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.jFrame.getContentPane().setPreferredSize(
				new Dimension(this.settings.getSizeX(), this.settings
						.getSizeY()));
		this.jFrame.setMinimumSize(new Dimension(600, 400));

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
		if (this.settings.hasPath())
			this.jSourcePane.setText(FileManagerCode.readSourceCode(new File(
					this.settings.getPath())));

		// Text area for input
		this.jInputPane = InitLeftPanel.initInputPane(jPanelLeft);

		// Text area for output
		this.jOutputPane = InitLeftPanel.initOutputPane(jPanelLeft);

		cp.add(jPanelLeft, BorderLayout.LINE_START);

		// Right part of the GUI
		this.rightPanelControl = new GUIrightPanel(cp, (GUImainMod) this);
		// this.rightPanelControl.setRightPanel(0);

		// Initialize the save dialog object
		this.saveDialog = new SaveDialog(this.jFrame, this.jSourcePane,
				this.settings);

		// Initialize the window listener
		this.jFrame.addWindowListener(new WindowEventListener(this.jFrame,
				this.settings, this.saveDialog));

		// Initialize window component listener
		this.jFrame.addComponentListener(new WindowComponentListener(
				this.jFrame, this.jSourcePane, this.settings,
				this.rightPanelControl));

		// Initialize the source panel listener
		this.jSourcePane.addKeyListener(new SourcePaneKeyListener(
				(GUIrPanelMod) this.rightPanelControl, this.jSourcePane));

		// Menubar
		InitMenuBar.initFileM(this.jFrame, this.jSourcePane, this.settings,
				this.rightPanelControl, this.saveDialog);

		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.pack();
		this.jFrame.setVisible(true);

		// Variable initialization
		this.inputHighlightOffset = 0;
		this.readLoopLock = new Object();
	}

	private void highlightInputPane() {
		Highlighter high = this.jInputPane.getHighlighter();
		high.removeAllHighlights();

		try {
			high.addHighlight(0, this.inputHighlightOffset,
					DefaultHighlighter.DefaultPainter);
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * --- The methods below are implemented from the interface "GUImainMod" ---
	 * * --- ...see there for comments and descriptions ---
	 */
	@Override
	public void repaint() {
		jFrame.repaint();
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}

	@Override
	public void setWinFileName(String name) {
		this.jFrame.setTitle("C-- Entwicklungsumgebung - " + name);
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
		File f = new File(this.settings.getPath());
		if( f.getParentFile() != null )
			return f.getParentFile().getAbsolutePath();
		return null;
	}

	@Override
	public void highlightSourceCode(int line, int col) {

		// Line out of source code range (includes)
		if (line <= (int) this.codeRegister.get(0)[0])
			return;

		// Correct offset in source code (offset caused by includes)
		else
			line = ExpandSourceCode
					.correctLine(line, (int) this.codeRegister.get(0)[0],
							this.codeRegister.size());

		int i, l = 0;
		final String code = this.jSourcePane.getText();

		synchronized (readLoopLock) {
			for (i = 0; l < line - 1; i++) {
				if (code.charAt(i) == '\n')
					l++;
			}
		}

		final int i_copy = i, l_copy = l;

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (i_copy < code.length()
						&& l_copy < jSourcePane.getLineCount())
					jSourcePane.select(i_copy, i_copy);
				else
					jSourcePane.select(code.length(), code.length());
			}
		});
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
	public void lockInput() {

		this.jSourcePane.setEditable(false);
		this.jInputPane.setEditable(false);
		this.jOutputPane.setEditable(false);
	}

	@Override
	public void unlockInput() {

		this.jSourcePane.setEditable(true);
		this.jInputPane.setEditable(true);
		this.jOutputPane.setEditable(true);
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
	public void setBreakPoint(int line) {

		String code = this.jSourcePane.getText();

		for (int i = 0, index = 0; i < line; i++) {
			index = code.indexOf("\n", index);
		}

		// TODO this
	}
}
