package at.jku.ssw.cmm.gui.debug;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.gui.datastruct.ReadCallStack;
import at.jku.ssw.cmm.gui.event.debug.PanelRunListener;
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

		this.listener = new PanelRunListener(this.modifier, this);
		this.compileManager = new CMMwrapper(this.modifier, this);
		
		this.initRunMode();

		jVarPanel = new JPanel();
		jVarPanel.setBorder(new TitledBorder(_("Variables")));
		jVarPanel.setLayout(new BoxLayout(jVarPanel, BoxLayout.PAGE_AXIS));
		this.varView = new TableView( this, jVarPanel, popup );
		this.jRightPanel.add(jVarPanel);
		
		this.stepTarget = -1;
		this.sourceCodeBeginLine = 0;

		this.listener.reset();
		
		this.cp.add(this.jRightPanel, BorderLayout.CENTER);
		
		this.breakpoints = new ArrayList<>();
	}
	
	public static final byte VM_TABLE = 0;
	public static final byte VM_TREETABLE = 1;
	
	private final JComponent cp;
	
	private JPanel jVarPanel;

	// Interface for main GUI manipulations
	private final GUImainMod modifier;

	// Interface of the right panel (edit text, compile
	// error, interpreter)
	private final JPanel jRightPanel;

	private VariableView varView;

	// Wrapper class with listeners for the "run" mode. Also contains the debug
	// and I/O stream interface
	// for the interpreter as well as (interpreter) "run" mode event routines.
	private final PanelRunListener listener;

	// Wrapper class for the compiler. Also initiates the interpreter thread.
	private final CMMwrapper compileManager;

	// Error position data
	private int line;
	private int col;

	/* --- run mode objects --- */
	// starts or resumes interpreting
	private JButton jButtonPlay;

	// resumes interpreting for one step
	private JButton jButtonStep;

	private JButton jButtonStepOver;

	private JButton jButtonStepOut;

	// Stops interpreting
	private JButton jButtonStop;

	// Regulates interpreter speed
	private JSlider jSlider;
	private JLabel jLabelTimer;

	// Runtime error labels
	private JLabel jRuntimeErrorLabel1;
	private JLabel jRuntimeErrorLabel2;
	private JLabel jRuntimeErrorLabel3;

	
	
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
	 * Initializes the objects of the "run" mode, which is active during
	 * interpreting
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT.</i><br>
	 * As this is a initialization method, it should generally not be called
	 * outside the constructor.
	 * </hr>
	 */
	private void initRunMode() {

		this.jRightPanel.setLayout(new BoxLayout(this.jRightPanel,
				BoxLayout.PAGE_AXIS));

		// Sub-panel with switches and buttons
		JPanel pane1 = new JPanel();
		pane1.setBorder(new EmptyBorder(5, 5, 5, 5));
		pane1.setMinimumSize(new Dimension(100, 72));

		/* ---------- PLAY | PAUSE buttons ---------- */
		jButtonPlay = new JButton("\u25B6");
		this.jButtonPlay.addMouseListener(this.listener.playButtonHandler);
		pane1.add(jButtonPlay);

		jButtonStep = new JButton("\u25AE\u25B6");
		this.jButtonStep.addMouseListener(this.listener.stepButtonHandler);
		pane1.add(jButtonStep);

		jButtonStepOver = new JButton("\u21B7");
		this.jButtonStepOver
				.addMouseListener(this.listener.stepOverButtonHandler);
		pane1.add(jButtonStepOver);
		this.jButtonStepOver.setVisible(false);

		jButtonStepOut = new JButton("\u21B5");
		this.jButtonStepOut
				.addMouseListener(this.listener.stepOutButtonHandler);
		pane1.add(jButtonStepOut);
		this.jButtonStepOut.setVisible(false);

		jButtonStop = new JButton("\u25A0");
		this.jButtonStop.addMouseListener(this.listener.stopButtonHandler);
		pane1.add(jButtonStop);

		/* --- RUNTIME ERROR LABELS --- */
		this.jRuntimeErrorLabel1 = new JLabel(_("Runtime Error")+":");
		pane1.add(this.jRuntimeErrorLabel1);
		this.jRuntimeErrorLabel1.setVisible(false);

		this.jRuntimeErrorLabel2 = new JLabel("...");
		pane1.add(this.jRuntimeErrorLabel2);
		this.jRuntimeErrorLabel2.setVisible(false);
		
		this.jRuntimeErrorLabel3 = new JLabel("...");
		pane1.add(this.jRuntimeErrorLabel3);
		this.jRuntimeErrorLabel3.setVisible(false);

		/* ---------- SLIDER ---------- */
		jLabelTimer = new JLabel("1.0 sec");
		pane1.add(jLabelTimer);

		jSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 1);
		jSlider.setMajorTickSpacing(1);
		jSlider.setMinorTickSpacing(1);
		jSlider.setPaintTicks(true);
		jSlider.addChangeListener(this.listener.sliderListener);
		jSlider.setValue(3);
		pane1.add(jSlider);

		this.jRightPanel.add(pane1);
		// Sub-panel end

		
		
		/* ---------- TREE TABLE for CALL STACK and LOCALS (optional) ---------- */
		/*JLabel jLabelTreeTable = new JLabel(_("Variables"));
		this.visToggle.registerComponent(1, jLabelTreeTable);
		this.jPanelVarInfo.add(jLabelTreeTable);
		
		this.varTreeTableModel = new TreeTableDataModel(ReadCallStackHierarchy.createDataStructure());
		
		this.varTreeTable = new TreeTable(this.varTreeTableModel);
		
		JScrollPane p = new JScrollPane(this.varTreeTable);
		p.setVisible(false);
		this.jPanelVarInfo.add(p);
		this.visToggle.registerComponent(1, p);
		// Sub-panel end
		
		this.visToggle.setVisible(0);*/

		
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

		// Set standard mode elements invisible
		this.jButtonPlay.setVisible(false);
		this.jButtonStepOver.setVisible(false);
		this.jButtonStepOut.setVisible(false);
		this.jLabelTimer.setVisible(false);
		this.jSlider.setVisible(false);

		// Make error mode elements visible
		this.jRuntimeErrorLabel1.setVisible(true);
		this.jRuntimeErrorLabel1.setText(title);
		this.jRuntimeErrorLabel2.setVisible(true);
		this.jRuntimeErrorLabel2.setText(message);
		this.jRuntimeErrorLabel3.setVisible(true);
		this.jRuntimeErrorLabel3.setText("... in line " + this.line);

		// Change step button to view button
		this.jButtonStep.setVisible(true);
		this.jButtonStep.setText("View");
	}

	/**
	 * Goes back from "runtime error" mode to "ready" mode. Both are sub-modes
	 * of "run" mode
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void unsetRunTimeError() {

		// Set standard mode elements visible
		this.jButtonPlay.setVisible(true);
		this.jLabelTimer.setVisible(true);
		this.jSlider.setVisible(true);

		// Hide error mode elements
		this.jRuntimeErrorLabel1.setVisible(false);
		this.jRuntimeErrorLabel2.setVisible(false);
		this.jRuntimeErrorLabel3.setVisible(false);

		// Change view button to step button
		this.jButtonStep.setText("\u25AE\u25B6");
	}

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
		
		this.listener.reset();

		this.callStackSize = 0;

		this.listener.stepComplete();
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
			this.listener.stepComplete();
			this.stepTarget = -1;
			return true;
		}

		return false;
	}
	
	public void updateCallStackSize(){
		this.callStackSize = ReadCallStack.readCallStack().size();
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
	 * Sets the "play/pause" button in "run" mode to display PAUSE
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void setPause() {
		this.jButtonPlay.setText("\u25AE\u25AE");
	}

	/**
	 * Sets the "play/pause" button in "run" mode to display PLAY
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void setPlay() {
		this.jButtonPlay.setText("\u25B6");
	}

	/**
	 * Locks the "step" button in "run" mode so that it can non be pressed
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void lockStepButton() {
		this.jButtonStep.setEnabled(false);
	}

	/**
	 * Unlocks the "step" button in "run" mode so that it can be used again
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void unlockStepButton() {
		this.jButtonStep.setEnabled(true);
	}

	/**
	 * Locks the "stop" button in "run" mode so that it can not be pressed.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void lockStopButton() {
		this.jButtonStop.setEnabled(false);
	}

	/**
	 * Unlocks the "stop" button in "run" mode so that it can be used again
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void unlockStopButton() {
		this.jButtonStop.setEnabled(true);
	}

	/**
	 * Sets the label for the execution delay in "run" mode to the given value.
	 * Adds "sec" postfix automatically.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 * 
	 * @param s
	 *            delay time in seconds
	 */
	public void setTimerLabelSeconds(double s) {
		this.jLabelTimer.setText("" + s + " " + _("sec"));
	}

	/**
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 * 
	 * @return The current value of the interpreter speed slider
	 */
	public int getInterpreterSpeedSlider() {
		return this.jSlider.getValue();
	}

	/**
	 * Makes the "step over" button at the top of the right panel in the main
	 * GUI visible.
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 */
	public void setStepOverButton() {
		if (this.listener.isPauseMode()) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					jButtonStepOver.setVisible(true);
				}
			});
		}
	}

	/**
	 * Makes the "step over" button at the top of the right panel in the main
	 * GUI visible and hides the "nest step" button. Used for functions from
	 * include files, as these shall not be visualized step by step.
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 */
	public void setStepOverButtonAlone() {
		if (this.listener.isPauseMode()) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					jButtonStepOver.setVisible(true);
					jButtonStep.setVisible(false);
				}
			});
		}
	}

	/**
	 * Hides the "step over button at the top of the right panel in the main GUI
	 * and makes the "next step" button visible. This is the default state,
	 * displayed for every AST node, except function calls and assignments with
	 * calls.
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 */
	public void unsetStepOverButton() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				jButtonStepOver.setVisible(false);
				jButtonStep.setVisible(true);
			}
		});
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
		this.listener.stepOver();
		System.out.println("[interpreter][GUIdebugPanel]Stepping over...");
	}

	public void updateStepOutButton() {
		if (this.callStackSize > 1) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					jButtonStepOut.setVisible(true);
				}
			});
		} else {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					jButtonStepOut.setVisible(false);
				}
			});
		}
	}

	public void stepOut() {
		if (this.callStackSize > 1) {
			this.stepTarget = this.callStackSize - 1;
			this.listener.stepOver();
			System.out.println("[interpreter][GuidebugPanel]Stepping out...");
		}
	}
	
	public boolean isCodeChangeAllowed(){
		return true;
	}
	
	public List<Integer> getBreakPoints(){
		return this.breakpoints;
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
					listener.setErrorMode(_("Preprocessor error"), _("Include file not found") + ": \"" + e1.getFileName() + "\"", e1.getLine(), 0);
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
			this.listener.setErrorMode("Compiler error", e.msg, e.line, e.col);
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
		return this.compileManager.runInterpreter(listener, new IOstream(this.modifier));
	}
}
