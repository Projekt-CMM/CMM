package at.jku.ssw.cmm.gui.debug;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;

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

		this.jRightPanel = new JPanel();
		this.jRightPanel.setLayout(new BorderLayout());

		this.compileManager = new CMMwrapper(this.modifier, this);
		
		this.jControlPanel = new JPanel();
		this.jControlPanel.setBorder(new TitledBorder(_("Control elements")));
		//this.jControlPanel.setLayout(new BoxLayout(this.jControlPanel, BoxLayout.LINE_AXIS));
		this.ctrlPanel = new GUIcontrolPanel( this.jControlPanel, this, mod );
		this.ctrlPanel.getListener().reset();
		
		this.jVarPanel = new JPanel();
		this.jVarPanel.setBorder(new TitledBorder(_("Variables")));
		this.jVarPanel.setLayout(new BoxLayout(this.jVarPanel, BoxLayout.PAGE_AXIS));
		this.varView = new TableView( this, this.jVarPanel, popup );
		this.jRightPanel.add(jVarPanel, BorderLayout.CENTER);
		this.jRightPanel.add(jControlPanel, BorderLayout.PAGE_START);
		
		this.stepTarget = -1;
		this.sourceCodeBeginLine = 0;
		
		this.cp.add(this.jRightPanel, BorderLayout.CENTER);
		
		this.breakpoints = new ArrayList<>();
	}
	
	public static final byte VM_TABLE = 0;
	public static final byte VM_TREETABLE = 1;
	
	private final JComponent cp;
	
	private JPanel jVarPanel;
	private JPanel jControlPanel;

	// Interface for main GUI manipulations
	private final GUImainMod modifier;

	// Interface of the right panel (edit text, compile
	// error, interpreter)
	private final JPanel jRightPanel;

	private VariableView varView;
	
	private final GUIcontrolPanel ctrlPanel;

	// Wrapper class for the compiler. Also initiates the interpreter thread.
	private final CMMwrapper compileManager;

	// Error position data
	private int line;
	private int col;
	
	// Tree table for variables
		private TreeTable varTreeTable;
		private TreeTableDataModel varTreeTableModel;

	// Step over counter
	private int stepTarget;
	private int callStackSize;

	// Begin of source code, without includes
	private int sourceCodeBeginLine;
	
	// List of Breakpoints
	private final List<Integer> breakpoints;

	/**
	 * <hr>
	 * <i>THREAD SAFE by default </i>
	 * <hr>
	 * 
	 * @return The first line of the original source code, without includes and
	 *         include code.
	 */
	public int getBeginLine() {
		return this.sourceCodeBeginLine;
	}
	
	public int getErrorLine() {
		return this.line;
	}
	
	public int getCompleteErrorLine() {
		return this.line + this.sourceCodeBeginLine - this.modifier.getSourceCodeRegister().size();
	}

	public int getErrorCol() {
		return this.col;
	}
	
	public void resetInterpreterData() {
		
		this.ctrlPanel.getListener().reset();

		this.callStackSize = 0;

		this.ctrlPanel.getListener().stepComplete();
		this.stepTarget = -1;

		this.modifier.resetInputHighlighter();
		
		this.varView.standby();
	}
	
	/**
	 * Updates the variable table, call stack or tree table according to the current view mode.
	 */
	public void updateVariableTables(){
		varView.update(compileManager);
	}
	
	void setCallStackSize( int size ){
		this.callStackSize = size;
	}
	
	public void setViewMode( byte mode ){
		switch( mode ){
		case VM_TREETABLE:
			//TODO treetable
			break;
		default:
			this.jRightPanel.remove(this.jVarPanel);
			jVarPanel = new JPanel();
			jVarPanel.setBorder(new TitledBorder(_("Variables")));
			jVarPanel.setLayout(new BoxLayout(jVarPanel, BoxLayout.PAGE_AXIS));
			this.varView = new TableView( this, jVarPanel, null );
			this.jRightPanel.add(jVarPanel);
			break;
		}
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
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
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
	
	public void updateCallStackSize(){
		this.callStackSize = ReadCallStack.readCallStack().size();
	}
	
	public int getCallStackSize() {
		
		this.updateCallStackSize();
		return this.callStackSize;
	}

	/**
	 * <hr>
	 * <i>THREAD SAFE by default </i>
	 * <hr>
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
	 * <hr>
	 * <i>THREAD SAFE, calls synchronized function </i>
	 * <hr>
	 */
	public void stepOver() {
		this.stepTarget = this.callStackSize;
		this.ctrlPanel.getListener().stepOver();
		System.out.println("[interpreter][GUIdebugPanel]Stepping over...");
	}

	public void stepOut() {
		if (this.callStackSize > 1) {
			this.stepTarget = this.callStackSize - 1;
			this.ctrlPanel.getListener().stepOver();
			System.out.println("[interpreter][GuidebugPanel]Stepping out...");
		}
	}
	
	public boolean isCodeChangeAllowed(){
		return true;
	}
	
	public List<Integer> getBreakPoints(){
		return this.breakpoints;
	}
	
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

		System.out
				.println("\n-------------------------------------\nUsed input files: ");
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
