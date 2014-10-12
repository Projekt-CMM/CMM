package at.jku.ssw.cmm.gui.debug;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.gui.datastruct.ReadCallStack;
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

		this.cp = cp;
		
		this.modifier = mod;
		
		this.popup = popup;

		this.jRightPanel = new JPanel();
		this.jRightPanel.setLayout(new BorderLayout());

		this.compileManager = new CMMwrapper(this.modifier, this);
		
		this.jControlPanel = new JPanel();
		this.jControlPanel.setBorder(new TitledBorder(_("Control elements")));
		this.ctrlPanel = new GUIcontrolPanel( this.jControlPanel, this, mod );
		this.ctrlPanel.getListener().reset();
		this.jRightPanel.add(jControlPanel, BorderLayout.PAGE_START);
		
		this.jVarPanel = new JPanel();
		this.jVarPanel.setBorder(new TitledBorder(_("Variables")));
		this.jVarPanel.setLayout(new BoxLayout(this.jVarPanel, BoxLayout.PAGE_AXIS));
		this.varView = new TreeTableView(jVarPanel, mod.getFileName());
		this.jRightPanel.add(jVarPanel, BorderLayout.CENTER);
		
		this.stepTarget = -1;
		this.sourceCodeBeginLine = 0;
		
		this.cp.add(this.jRightPanel, BorderLayout.CENTER);
		
		this.breakpoints = new ArrayList<>();
	}
	
	/**
	 * Main component of the main GUI
	 */
	private final JComponent cp;
	
	/**
	 * Panel with variable tree table
	 */
	private JPanel jVarPanel;
	
	/**
	 * Panel with control buttons, i.e. PLAY/PAUSE, STEP, STEP OVER, ...
	 */
	private JPanel jControlPanel;
	
	/**
	 * The manager object for the control panel.
	 */
	private final GUIcontrolPanel ctrlPanel;

	/**
	 * Interface for main GUI manipulations
	 */
	private final GUImainMod modifier;

	/**
	 * Interface of the right panel (edit text, compile error, interpreter)
	 */
	private final JPanel jRightPanel;
	
	/**
	 * Interface for popup operations. Used by static methods which invoke popups.
	 */
	private final PopupInterface popup;

	//TODO add a comment
	private TreeTableView varView;

	/**
	 * Wrapper class for the compiler. Also initiates the interpreter thread.
	 */
	private final CMMwrapper compileManager;

	/**
	 * Line of the currently displayed error (if necessary)
	 */
	private int line;
	
	/**
	 * Column of the currently displayed error (if necessary)
	 */
	private int col;

	/**
	 * The Call stack size which has to be reached so that stepping over a function is finished.
	 */
	private int stepTarget;
	
	/**
	 * The current call stack size.
	 */
	private int callStackSize;

	/**
	 * Begin of source code, without includes
	 */
	private int sourceCodeBeginLine;
	
	/**
	 * List of breakpoints
	 */
	private final List<Integer> breakpoints;

	/**
	 * <i>THREAD SAFE by default </i>
	 * 
	 * @return The first line of the original source code, without includes and
	 *         include code.
	 */
	public int getBeginLine() {
		return this.sourceCodeBeginLine;
	}
	
	/**
	 * <i>THREAD SAFE by default </i>
	 * 
	 * @return The line of the currently displayed error (if necessary)
	 */
	public int getErrorLine() {
		return this.line;
	}
	
	/**
	 * <i>THREAD SAFE by default </i>
	 * 
	 * @return The column of the currently displayed error
	 */
	public int getErrorCol() {
		return this.col;
	}
	
	/**
	 * <i>THREAD SAFE by default </i>
	 * 
	 * @return The line of the currently displayed error in the users's source code.
	 * This is the error line which is highlighted in the source code panel of the main GUI.
	 */
	public int getCompleteErrorLine() {
		return ExpandSourceCode.correctLine(this.line, this.sourceCodeBeginLine, this.modifier.getSourceCodeRegister().size());
	}
	
	/**
	 * Resets the control panel and the variable tree table (consequently the whole debug panel GUI).
	 * Should be called after resetting interpreter so that the GUI does no longer display variable values.
	 * 
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 */
	public void resetInterpreterData() {
		
		this.ctrlPanel.getListener().reset();

		this.callStackSize = 0;

		this.ctrlPanel.getListener().stepComplete();
		this.stepTarget = -1;

		this.modifier.resetInputHighlighter();
		
		this.varView.standby(this.modifier.getFileName());
	}
	
	/**
	 * Updates the variable table, call stack or tree table according to the current view mode.
	 */
	public void updateVariableTables(){
		varView.update(compileManager, this.modifier.getFileName(), popup);
	}
	
	/**
	 * Sets the call stack counter variable to the given value
	 * <br><br>
	 * <i>THREAD SAFE by default </i>
	 * 
	 * @param size The current size of the call stack.
	 */
	void setCallStackSize( int size ){
		this.callStackSize = size;
	}
	
	/**
	 * <i>THREAD SAFE by default </i>
	 * <br><br>
	 * Updates the call stack counter variable automatically by reading the call stack memory.
	 */
	public void updateCallStackSize(){
		this.callStackSize = ReadCallStack.readCallStack().size();
	}
	
	/**
	 * <i>THREAD SAFE by default </i>
	 * 
	 * @return The current size of the call stack. Updates automatically.
	 */
	public int getCallStackSize() {
		
		this.updateCallStackSize();
		return this.callStackSize;
	}
	
	/**
	 * Sets the right panel of the main GUI to the "runtime error" mode, which
	 * is a sub-mode of "run" mode.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 * 
	 * @param message
	 *            The error message from the interpreter
	 */
	public void setRuntimeErrorMode(String title, String message, int line, int col ) {
		this.line = line - this.sourceCodeBeginLine + this.modifier.getSourceCodeRegister().size();
		this.col = col;
		
		System.out.println("Error found: " + line + " -> " + this.line );
		
		this.ctrlPanel.setRuntimeErrorMode(title, message, this.line, this.col);
	}
	
	/**
	 * Checks if the function which has to be skipped (as part of the
	 * "step over" routine) is finished. Eventually exits the "step over" mode.
	 * Also updates the local variable "int callStackSize", which is important
	 * for function skipping ("step over"). Does <b>not</b> update the call
	 * stack list of the main GUI. Take care that at least one of the following
	 * methods (all are thread save) is called at each interpreter step
	 * <ul>
	 * <li>public void updateCallStack()</li>
	 * <li>public void updateCallStackSize()</li>
	 * <li>public boolean checkForStepEnd()</li>
	 * </ul>
	 * 
	 * <br><br>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * 
	 * @return TRUE if "step over" has ended, otherwise FALSE
	 */
	public boolean checkForStepEnd() {

		this.callStackSize = ReadCallStack.readCallStack().size();

		if (this.callStackSize <= this.stepTarget) {
			System.out.println("[interpreter][GUIdebugPanel]Step completed");
			this.ctrlPanel.getListener().stepComplete();
			this.stepTarget = -1;
			return true;
		}

		return false;
	}

	/**
	 * <br><br>
	 * <i>THREAD SAFE by default </i>
	 * 
	 * @return A reference to the right panel's compiler wrapper object, see
	 *         {@link CMMwrapper}
	 */
	public CMMwrapper getCompileManager() {
		return this.compileManager;
	}

	

	/**
	 * Initiates stepping over the next function, <b>not</b> the current
	 * function. However, stepping over will abort if function is not entered
	 * with the next AST node.
	 * 
	 * <br><br>
	 * <i>THREAD SAFE, calls synchronized function </i>
	 */
	public void stepOver() {
		this.stepTarget = this.callStackSize;
		this.ctrlPanel.getListener().stepOver();
		System.out.println("[interpreter][GUIdebugPanel]Stepping over...");
	}

	/**
	 * Initiates stepping out of the current function.
	 * 
	 * <br><br>
	 * <i>THREAD SAFE, calls synchronized function </i>
	 */
	public void stepOut() {
		if (this.callStackSize > 1) {
			this.stepTarget = this.callStackSize - 1;
			this.ctrlPanel.getListener().stepOver();
			System.out.println("[interpreter][GuidebugPanel]Stepping out...");
		}
	}
	
	/**
	 * <i>THREAD SAFE by default</i>
	 * 
	 * @return TRUE if source code can be modified, otherwise FALSE (during interpreting cmm program)
	 */
	public boolean isCodeChangeAllowed(){
		return true;
	}
	
	/**
	 * <i>THREAD SAFE by default</i>
	 * 
	 * @return A list of all breakpoints
	 */
	public List<Integer> getBreakPoints(){
		return this.breakpoints;
	}
	
	/**
	 * <i>THREAD SAFE by default</i>
	 * 
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
					this.modifier.getSourceCodeRegister(),
					this.breakpoints);
		} catch (final IncludeNotFoundException e1) {
			// An include file could not be found
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					ctrlPanel.getListener().setErrorMode(_("Preprocessor error"), _("Include file not found") + ": \"" + e1.getFileName() + "\"", e1.getLine(), 0);
				}
			});
			return;
		}

		/* --- Code statistics --- */
		System.out.println("\n-------------------------------------\nUsed input files: ");
		for (Object[] o : this.modifier.getSourceCodeRegister()) {
			System.out.println("" + o[2] + ", line " + o[0] + " - " + o[1]);
		}
		System.out.println("-------------------------------------");

		this.sourceCodeBeginLine = (int) this.modifier.getSourceCodeRegister()
				.get(0)[0];
		System.out.println("Source code begins @ line "
				+ this.sourceCodeBeginLine + "\n");

		for(int i : this.breakpoints){
			System.out.println("line " + i);
		}
		System.out.println("-------------------------------------");

		int i = 1;
		for (String s : sourceCode.split("\n")) {
			System.out.println("" + i + ": " + s);
			i++;
		}

		System.out.println("-------------------------------------");
		/* --- end of statistics ---*/

		// Compile
		at.jku.ssw.cmm.compiler.Error e = compileManager.compile(sourceCode);
		
		// compiler returns errors
		if( e != null ) {
			this.ctrlPanel.getListener().setErrorMode("Compiler error", e.msg, e.line, e.col);
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
		return this.compileManager.runInterpreter(ctrlPanel.getListener(), new IOstream(this.modifier));
	}
}
