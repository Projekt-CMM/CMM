package at.jku.ssw.cmm.gui.debug;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.gui.exception.IncludeNotFoundException;
import at.jku.ssw.cmm.gui.include.ExpandSourceCode;
import at.jku.ssw.cmm.gui.interpreter.IOstream;
import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.gui.popup.PopupInterface;

/**
 * Controls the right panel of the main GUI. This is a bit more complex, as this
 * panel can have three different states with different user interfaces. The
 * three different states are initialized into an array of JPanels of which is
 * only shown one at a time. The three different states are: <br>
 * - user is typing code -> right panel shows a "compile" button see method
 * "private void initEditMode()" <br>
 * - compiler returned error messages -> right panel shows message, total errors
 * and a "view error" button see method "private void initErrorMode()" <br>
 * - interpreter is running -> right panel shows tables and lists for variables
 * as well as buttons for controlling the interpreter see method
 * "private void initRunMode()"
 * 
 * @author fabian
 *
 */
public class GUIdebugPanel {

	/**
	 * 
	 * @param cp
	 *            Main component of the main GUI
	 * @param mod
	 *            Interface for main GUI manipulations
	 */
	public GUIdebugPanel(JPanel cp, GUImainMod mod, PopupInterface popup) {

		// Constructor parameter init
		this.modifier = mod;
		this.popup = popup;

		cp.setLayout(new BorderLayout());

		this.compileManager = new CMMwrapper(this.modifier, this);

		this.jControlPanel = new JPanel();
		this.jControlPanel.setBorder(new TitledBorder(_("Control elements")));
		this.ctrlPanel = new GUIcontrolPanel(this.jControlPanel, this, mod);
		cp.add(jControlPanel, BorderLayout.PAGE_START);

		this.infoManager = new GUIinfoPanel(cp, this.modifier);

		this.breakpoints = new ArrayList<>();
	}

	/**
	 * Panel with control buttons, i.e. PLAY/PAUSE, STEP, STEP OVER, ...
	 */
	private JPanel jControlPanel;

	/**
	 * The manager object for the control panel.
	 */
	private final GUIcontrolPanel ctrlPanel;

	private final GUIinfoPanel infoManager;

	/**
	 * Interface for main GUI manipulations
	 */
	private final GUImainMod modifier;

	/**
	 * Interface for popup operations. Used by static methods which invoke
	 * popups.
	 */
	private final PopupInterface popup;

	/**
	 * Wrapper class for the compiler. Also initiates the interpreter thread.
	 */
	private final CMMwrapper compileManager;

	/**
	 * List of breakpoints
	 */
	private final List<Integer> breakpoints;

	/**
	 * @return The first line of the original source code, without includes and
	 *         include code.
	 */
	public int getBeginLine() {
		return (int) this.modifier.getSourceCodeRegister().get(0)[0];
	}

	/**
	 * @return A list of all breakpoints
	 */
	public List<Integer> getBreakPoints() {
		return this.breakpoints;
	}

	/**
	 * Deletes all breakpoints before the given line. Used when the user
	 * switches from any mode to "fast run" mode; so that the interpreter does
	 * not stop at breakpoints which should already have been passed.
	 * 
	 * @param line
	 *            The current line
	 */
	public void updateBreakPoints(int line) {
		for (int i = 0; i < this.breakpoints.size(); i++) {
			if (this.breakpoints.get(i) <= line)
				this.breakpoints.remove(i);
		}
	}

	/**
	 * Updates the file name in the default root node of the variable tree
	 * table.
	 */
	public void updateFileName() {
		this.infoManager.getVarView().standby(this.modifier.getFileName());
	}

	/**
	 * Highlights the variable with the given address in the variable tree table
	 * 
	 * @param adr
	 *            The address of the variable to be highlighted
	 */
	public void highlightVariable(final int adr) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				infoManager.getVarView().highlightVariable(adr);
			}
		});
	}

	/**
	 * Updates the variable table, call stack or tree table according to the
	 * current view mode.
	 */
	public void updateVariableTables(boolean completeUpDate) {
		this.infoManager.getVarView().update(compileManager, this.modifier.getFileName(), popup,completeUpDate);
	}

	public void setReadyMode() {
		this.ctrlPanel.setReadyMode();
		this.ctrlPanel.getListener().setReadyMode();
		this.modifier.setReadyMode();

		// Mode-specific
		this.modifier.resetInputHighlighter();
		this.infoManager.getVarView().standby(this.modifier.getFileName());
		
		//Input lock
		this.modifier.unlockInput();
		
		this.infoManager.setToTable();
	}

	public void setErrorMode(String msg, int line) {

		this.ctrlPanel.setErrorMode();
		this.ctrlPanel.getListener().setErrorMode();
		this.modifier.setErrorMode(line);
		
		//Input lock
		this.modifier.unlockInput();
		
		this.infoManager.setToDesc(msg);
	}

	public void setRunMode() {
		this.ctrlPanel.setRunMode();
		this.ctrlPanel.getListener().setRunMode();
		this.modifier.setRunMode();
		
		//Input lock
		this.modifier.lockInput();
	}

	public void setPauseMode() {
		this.ctrlPanel.setPauseMode();
		this.ctrlPanel.getListener().setPauseMode();
		this.modifier.setPauseMode();
		
		//Input lock
		this.modifier.lockInput();
	}
	
	/**
	 * <br>
	 * <br>
	 * <i>THREAD SAFE by default </i>
	 * 
	 * @return A reference to the right panel's compiler wrapper object, see
	 *         {@link CMMwrapper}
	 */
	public CMMwrapper getCompileManager() {
		return this.compileManager;
	}

	/**
	 * @return A reference to the control panel manager class
	 */
	public GUIcontrolPanel getControlPanel(){
		return this.ctrlPanel;
	}

	// TODO Invoke this method as side task
	// TODO make thread safe and update comments
	/**
	 * Runs the compiler via the compiler wrapper class, see {@link CMMwrapper}.
	 * Automatically switches to "error" mode if necessary.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT.</i> This
	 * method is not thread safe as it changes the right panel of the main GUI.
	 * <hr>
	 */
	public void compile() {

		String sourceCode = this.modifier.getSourceCode();
		this.breakpoints.clear();

		try {
			sourceCode = ExpandSourceCode.expand(sourceCode,
					this.modifier.getWorkingDirectory(),
					this.modifier.getSourceCodeRegister(), this.breakpoints);
		} catch (final IncludeNotFoundException e1) {

			Object[] e = { 1, 0, null };
			this.modifier.getSourceCodeRegister().clear();
			this.modifier.getSourceCodeRegister().add(e);

			// An include file could not be found
			this.setErrorMode(
					_("Include file not found") + ": \"" + e1.getFileName()
							+ "\"", e1.getLine());
			return;
		}

		/* --- Code statistics --- */
		DebugShell.out(State.STAT, Area.COMPILER,
				"\n-------------------------------------\nUsed input files: ");
		for (Object[] o : this.modifier.getSourceCodeRegister()) {
			DebugShell.out(State.STAT, Area.COMPILER, "" + o[2] + ", line "
					+ o[0] + " - " + o[1]);
		}
		DebugShell.out(State.STAT, Area.COMPILER,
				"-------------------------------------");

		DebugShell.out(State.STAT, Area.COMPILER, "Source code begins @ line "
				+ (int) this.modifier.getSourceCodeRegister().get(0)[0] + "\n");

		for (int i : this.breakpoints) {
			DebugShell.out(State.STAT, Area.COMPILER, "line " + i);
		}
		DebugShell.out(State.STAT, Area.COMPILER,
				"-------------------------------------");

		int i = 1;
		for (String s : sourceCode.split("\n")) {
			DebugShell.out(State.STAT, Area.COMPILER, "" + i + ": " + s);
			i++;
		}

		DebugShell.out(State.STAT, Area.COMPILER,
				"-------------------------------------");
		/* --- end of statistics --- */

		// Compile
		at.jku.ssw.cmm.compiler.Error e = compileManager.compile(sourceCode);

		// compiler returns errors
		if (e != null) {
			this.setErrorMode(e.msg, e.line);
		}
	}

	/**
	 * Starts the interpreter thread via the compiler wrapper class. Initializes
	 * the call stack.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public boolean runInterpreter() {
		this.compile();
		return this.compileManager.runInterpreter(ctrlPanel.getListener(),
				new IOstream(this.modifier, this));
	}
}
