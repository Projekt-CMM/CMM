package at.jku.ssw.cmm.gui.debug;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
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

		//Constructor parameter init
		this.modifier = mod;
		this.popup = popup;

		cp = new JPanel();
		cp.setLayout(new BorderLayout());

		this.compileManager = new CMMwrapper(this.modifier, this);
		
		this.jControlPanel = new JPanel();
		this.jControlPanel.setBorder(new TitledBorder(_("Control elements")));
		this.ctrlPanel = new GUIcontrolPanel( this.jControlPanel, this, mod );
		this.ctrlPanel.getListener().reset();
		cp.add(jControlPanel, BorderLayout.PAGE_START);
		
		this.jVarPanel = new JPanel();
		this.jVarPanel.setBorder(new TitledBorder(_("Variables")));
		this.jVarPanel.setLayout(new BoxLayout(this.jVarPanel, BoxLayout.PAGE_AXIS));
		this.varView = new TreeTableView(this.modifier, this.jVarPanel, mod.getFileName());
		cp.add(jVarPanel, BorderLayout.CENTER);
		
		this.stepTarget = -1;
		
		this.breakpoints = new ArrayList<>();
	}
	
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
	 * The manager object for the variable tree table.
	 */
	private TreeTableView varView;

	/**
	 * Interface for main GUI manipulations
	 */
	private final GUImainMod modifier;
	
	/**
	 * Interface for popup operations. Used by static methods which invoke popups.
	 */
	private final PopupInterface popup;
	
	/**
	 * Wrapper class for the compiler. Also initiates the interpreter thread.
	 */
	private final CMMwrapper compileManager;

	/**
	 * Line of the currently displayed error (if necessary)
	 */
	private int line;

	/**
	 * The Call stack size which has to be reached so that stepping over a function is finished.
	 */
	private int stepTarget;
	
	/**
	 * The current call stack size.
	 */
	private int callStackSize;
	
	/**
	 * The previous call stack size
	 */
	private int previousCallStackSize;
	
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
	 * @return The line of the currently displayed error in the users's source code.
	 * This is the error line which is highlighted in the source code panel of the main GUI.
	 */
	private int getCompleteErrorLine() {
		return ExpandSourceCode.correctLine(this.line, (int) this.modifier.getSourceCodeRegister().get(0)[0], this.modifier.getSourceCodeRegister().size());
	}
	
	/**
	 * Resets the control panel and the variable tree table (consequently the whole debug panel GUI).
	 * Should be called after resetting interpreter so that the GUI does no longer display variable values.
	 */
	//TODO rework
	public void resetInterpreterData() {
		
		this.ctrlPanel.getListener().reset();

		this.callStackSize = 0;
		this.previousCallStackSize = 0;

		this.ctrlPanel.getListener().stepComplete();
		this.stepTarget = -1;

		this.modifier.resetInputHighlighter();
		
		this.varView.standby(this.modifier.getFileName());
	}
	
	/**
	 * Highlights the variable with the given address in the variable tree table
	 * 
	 * @param adr The address of the variable to be highlighted
	 */
	public void highlightVariable( final int adr ){
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					varView.highlightVariable(adr);
				}
			});
	}
	
	/**
	 * Updates the variable table, call stack or tree table according to the current view mode.
	 */
	public void updateVariableTables( boolean completeUpDate ){
		varView.update(compileManager, this.modifier.getFileName(), popup, completeUpDate);
	}
	
	/**
	 * <i>THREAD SAFE by default </i>
	 * <br><br>
	 * Updates the call stack counter variable automatically by reading the call stack memory.
	 */
	//TODO rework
	public void updateCallStackSize(){
		this.previousCallStackSize = this.callStackSize;
		this.callStackSize = ReadCallStack.readCallStack().size();
	}
	
	/**
	 * <i>THREAD SAFE by default </i>
	 * 
	 * @return The current size of the call stack. Updates automatically.
	 */
	//TODO rework
	public int getCallStackSize() {
		
		return this.callStackSize;
	}
	
	/**
	 * <i>THREAD SAFE by default </i>
	 * 
	 * @return TRUE if the size of the call stack has changed with the last update
	 */
	//TODO rework
	public boolean callStackChanged(){
		return this.callStackSize != this.previousCallStackSize;
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
	//TODO rework
	public boolean checkForStepEnd() {

		if (this.callStackSize <= this.stepTarget) {
			DebugShell.out(State.LOG, Area.INTERPRETER, "Step completed");
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
	//TODO rework
	public void stepOver() {
		this.stepTarget = this.callStackSize;
		this.ctrlPanel.getListener().stepOver();
		DebugShell.out(State.LOG, Area.INTERPRETER, "Stepping over...");
	}

	/**
	 * Initiates stepping out of the current function.
	 * 
	 * <br><br>
	 * <i>THREAD SAFE, calls synchronized function </i>
	 */
	//TODO rework
	public void stepOut() {
		if (this.callStackSize > 1) {
			this.stepTarget = this.callStackSize - 1;
			this.ctrlPanel.getListener().stepOver();
			DebugShell.out(State.LOG, Area.INTERPRETER, "Stepping out...");
		}
	}
	
	/**
	 * <i>THREAD SAFE by default</i>
	 * 
	 * @return TRUE if source code can be modified, otherwise FALSE (during interpreting cmm program)
	 */
	//TODO what the hell?
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
	 * Deletes all breakpoints before the given line. Used when the user switches from any mode
	 * to "fast run" mode; so that the interpreter does not stop at breakpoints which should already
	 * have been passed.
	 * 
	 * @param line The current line
	 */
	public void updateBreakPoints( int line ){
		for( int i = 0; i < this.breakpoints.size(); i++ ){
			if( this.breakpoints.get(i) <= line )
				this.breakpoints.remove(i);
		}
	}
	
	/**
	 * Updates the file name in the default root node of the variable tree table.
	 */
	public void updateFileName(){
		this.varView.standby(this.modifier.getFileName());
	}
	
	/**
	 * @return A reference to the control panel manager class
	 */
	//TODO rework
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
			
			this.line = e1.getLine();
			
			Object[] e = {1,0,null};
			this.modifier.getSourceCodeRegister().clear();
			this.modifier.getSourceCodeRegister().add(e);
			
			// An include file could not be found
			ctrlPanel.getListener().setErrorMode(_("Preprocessor error"), _("Include file not found") + ": \"" + e1.getFileName() + "\"", e1.getLine(), 0);
			return;
		}

		/* --- Code statistics --- */
		DebugShell.out(State.STAT, Area.COMPILER, "\n-------------------------------------\nUsed input files: ");
		for (Object[] o : this.modifier.getSourceCodeRegister()) {
			DebugShell.out(State.STAT, Area.COMPILER, "" + o[2] + ", line " + o[0] + " - " + o[1]);
		}
		DebugShell.out(State.STAT, Area.COMPILER, "-------------------------------------");

		DebugShell.out(State.STAT, Area.COMPILER, "Source code begins @ line "
				+ (int) this.modifier.getSourceCodeRegister().get(0)[0] + "\n");

		for(int i : this.breakpoints){
			DebugShell.out(State.STAT, Area.COMPILER, "line " + i);
		}
		DebugShell.out(State.STAT, Area.COMPILER, "-------------------------------------");

		int i = 1;
		for (String s : sourceCode.split("\n")) {
			DebugShell.out(State.STAT, Area.COMPILER, "" + i + ": " + s);
			i++;
		}

		DebugShell.out(State.STAT, Area.COMPILER, "-------------------------------------");
		/* --- end of statistics ---*/

		// Compile
		at.jku.ssw.cmm.compiler.Error e = compileManager.compile(sourceCode);
		
		// compiler returns errors
		if( e != null ) {
			this.line = e.line;
			this.ctrlPanel.getListener().setErrorMode("Compiler error", e.msg, this.getCompleteErrorLine(), e.col);
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
		return this.compileManager.runInterpreter(ctrlPanel.getListener(), new IOstream(this.modifier, this.ctrlPanel.getListener()));
	}
}
