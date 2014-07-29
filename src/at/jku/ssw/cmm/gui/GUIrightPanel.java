package at.jku.ssw.cmm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.gui.datastruct.ReadCallStack;
import at.jku.ssw.cmm.gui.datastruct.ReadCallStackHierarchy;
import at.jku.ssw.cmm.gui.datastruct.ReadSymbolTable;
import at.jku.ssw.cmm.gui.datastruct.StructureContainer;
import at.jku.ssw.cmm.gui.datastruct.VarTableModel;
import at.jku.ssw.cmm.gui.event.panel.PanelRunBrowseListener;
import at.jku.ssw.cmm.gui.event.panel.PanelRunStackListener;
import at.jku.ssw.cmm.gui.event.panel.PanelRunListener;
import at.jku.ssw.cmm.gui.exception.IncludeNotFoundException;
import at.jku.ssw.cmm.gui.include.ExpandSourceCode;
import at.jku.ssw.cmm.gui.init.StringPopup;
import at.jku.ssw.cmm.gui.interpreter.IOstream;
import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;
import at.jku.ssw.cmm.gui.utils.JTableButtonMouseListener;
import at.jku.ssw.cmm.gui.utils.JTableButtonRenderer;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.interpreter.memory.MethodContainer;

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
public class GUIrightPanel {

	/**
	 * 
	 * @param cp
	 *            Main component of the main GUI
	 * @param mod
	 *            Interface for main GUI manipulations
	 */
	public GUIrightPanel(JComponent cp, GUImainMod mod) {

		this.cp = cp;
		
		this.modifier = mod;

		this.jRightContainer = new JPanel();
		this.jRightContainer.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.jRightContainer.setLayout(new BorderLayout());
		
		this.visToggle = new VarTableVisToggle();

		this.jRightPanel = new JPanel[3];
		//this.jRightPanel[0] = new JPanel();
		//this.jRightPanel[1] = new JPanel();
		this.jRightPanel[2] = new JPanel();

		this.listener = new PanelRunListener(this.modifier, this);
		this.compileManager = new CMMwrapper(this.modifier, this);

		this.jRightContainer.add(this.initCommonPanel(),
				BorderLayout.PAGE_START);

		this.initRunMode();

		this.jRightContainer.add(jRightPanel[2], BorderLayout.CENTER);
		cp.add(this.jRightContainer, BorderLayout.CENTER);

		this.viewMode = VM_TABLE;
		this.stepTarget = -1;
		this.sourceCodeBeginLine = 0;

		this.listener.reset();

		this.globalVarBrowser = new Stack<>();
		this.localVarBrowser = new Stack<>();

		this.globalVarLock = new Object();
		this.localVarLock = new Object();
	}
	
	private final JComponent cp;

	// Interface for main GUI manipulations
	private final GUImainMod modifier;

	// Main container for the right panel. All interface changes happen inside
	// this JPanel
	private final JPanel jRightContainer;

	// Interfaces of the three modes of the right panel (edit text, compile
	// error, interpreter)
	private final JPanel jRightPanel[];

	// Variable view mode of the right panel (table and call stack, tree table, ...)
	private byte viewMode;
	
	public static final byte VM_TABLE = 0;
	public static final byte VM_TREE = 1;

	// Wrapper class with listeners for the "run" mode. Also contains the debug
	// and I/O stream interface
	// for the interpreter as well as (interpreter) "run" mode event routines.
	private final PanelRunListener listener;

	// Wrapper class for the compiler. Also initiates the interpreter thread.
	private final CMMwrapper compileManager;

	/* --- top panel objects --- */
	// Breakpoint button
	private JButton jButtonBreakPoint;

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

	// Panel containing tables for variables and the call stack list
	// These objects are in an separate panel so that they can be removed during
	// runtime
	private JPanel jPanelVarInfo;

	private JLabel jLabel1;
	private JButton jButtonB1;

	// Label saying "local variables". Can be edited so that it also shows the
	// function name
	private JLabel jLabel3;
	private JButton jButtonB2;

	// Table of global variables
	private JTable jTableGlobal;

	// Data model for the global variables tables
	private VarTableModel jTableGlobalModel;

	// Table of local variables
	private JTable jTableLocal;

	// Data model for the global variables tables
	private VarTableModel jTableLocalModel;

	// Call stack list object
	private JList<Object> jCallStack;
	
	private JLabel jLabel2;

	// SelectedFunction
	private final Stack<StructureContainer> globalVarBrowser;
	private final Stack<StructureContainer> localVarBrowser;
	
	// Tree table for variables
	private TreeTable varTreeTable;
	private TreeTableDataModel varTreeTableModel;

	// Synchronized lock objects for variable browsers
	private final Object globalVarLock;
	private final Object localVarLock;

	// Step over counter
	private int stepTarget;
	private int callStackSize;

	// Begin of source code, without includes
	private int sourceCodeBeginLine;
	
	//Manager for setting visible and invisible tables of different view modes
	private final VarTableVisToggle visToggle;

	// TODO this
	private JPanel initCommonPanel() {
		JPanel jTopPanel = new JPanel();

		this.jButtonBreakPoint = new JButton("\u2326");
		jTopPanel.add(this.jButtonBreakPoint);

		return jTopPanel;
	}

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

		// Panel layout
		this.jRightPanel[2].setBorder(BorderFactory
				.createLineBorder(Color.DARK_GRAY));
		this.jRightPanel[2].setLayout(new BoxLayout(this.jRightPanel[2],
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
		this.jRuntimeErrorLabel1 = new JLabel("Runtime Error:");
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

		this.jRightPanel[2].add(pane1);
		// Sub-panel end

		// Sub-panel with variable information tables and call stack list
		this.jPanelVarInfo = new JPanel();
		this.jPanelVarInfo.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.jPanelVarInfo.setLayout(new BoxLayout(this.jPanelVarInfo,
				BoxLayout.PAGE_AXIS));

		/* ---------- TABLE FOR GLOBAL VARs ---------- */
		JPanel paneGlobal1 = new JPanel();
		paneGlobal1.setBorder(new EmptyBorder(5, 5, 5, 5));

		jLabel1 = new JLabel("Global variables");
		jLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
		paneGlobal1.add(jLabel1);

		jButtonB1 = new JButton("\u2190");
		jButtonB1.addMouseListener(new PanelRunBrowseListener(this, true));
		paneGlobal1.add(jButtonB1);
		this.jButtonB1.setVisible(false);

		if( this.visToggle == null )
			System.out.println("noll error");
		this.visToggle.registerComponent(0, paneGlobal1);
		this.jPanelVarInfo.add(paneGlobal1);

		this.jTableGlobalModel = new VarTableModel();
		this.jTableGlobal = new JTable(this.jTableGlobalModel);

		TableCellRenderer defaultRenderer;
		defaultRenderer = this.jTableGlobal.getDefaultRenderer(JButton.class);
		this.jTableGlobal.getColumn("Value").setCellRenderer(
				new JTableButtonRenderer(defaultRenderer));

		this.jTableGlobal.addMouseListener(new JTableButtonMouseListener(
				this.jTableGlobal));

		JScrollPane scrollPane1 = new JScrollPane(this.jTableGlobal);
		this.jTableGlobal.setFillsViewportHeight(true);
		this.jPanelVarInfo.add(scrollPane1);
		
		this.visToggle.registerComponent(0, scrollPane1);

		/* ---------- CALL STACK ---------- */
		jLabel2 = new JLabel("Call Stack");
		jLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.jPanelVarInfo.add(jLabel2);
		
		this.visToggle.registerComponent(0, jLabel2);

		Object[] space = { " ", " ", " ", " ", " " };

		this.jCallStack = new JList<Object>(space);
		this.jCallStack.setBorder(BorderFactory
				.createLineBorder(Color.DARK_GRAY));
		this.jCallStack.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only
																				// one
																				// item
																				// selectable
		this.jCallStack.setLayoutOrientation(JList.VERTICAL);
		this.jCallStack.setVisibleRowCount(10);
		JScrollPane scrollPane2 = new JScrollPane(this.jCallStack);
		this.jPanelVarInfo.add(scrollPane2);
		
		this.visToggle.registerComponent(0, scrollPane2);

		/* ---------- TABLE FOR LOCAL VARs ---------- */
		JPanel paneLocal1 = new JPanel();
		paneLocal1.setBorder(new EmptyBorder(5, 5, 5, 5));

		jLabel3 = new JLabel("Local variables");
		jLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);
		paneLocal1.add(jLabel3);

		jButtonB2 = new JButton("\u2190");
		jButtonB2.addMouseListener(new PanelRunBrowseListener(this, false));
		paneLocal1.add(jButtonB2);
		this.jButtonB2.setVisible(false);

		this.jPanelVarInfo.add(paneLocal1);
		this.visToggle.registerComponent(0, paneLocal1);
		
		this.jTableLocalModel = new VarTableModel();
		this.jTableLocal = new JTable(this.jTableLocalModel);

		defaultRenderer = this.jTableLocal.getDefaultRenderer(JButton.class);
		this.jTableLocal.setDefaultRenderer(JButton.class,
				new JTableButtonRenderer(defaultRenderer));

		this.jTableLocal.addMouseListener(new JTableButtonMouseListener(
				this.jTableLocal));

		JScrollPane scrollPane3 = new JScrollPane(this.jTableLocal);
		this.jTableLocal.setFillsViewportHeight(true);
		this.jPanelVarInfo.add(scrollPane3);
		this.visToggle.registerComponent(0, scrollPane3);

		this.jRightPanel[2].add(this.jPanelVarInfo);
		
		/* ---------- TREE TABLE for CALL STACK and LOCALS (optional) ---------- */
		JLabel jLabelTreeTable = new JLabel("Variables");
		this.visToggle.registerComponent(1, jLabelTreeTable);
		this.jPanelVarInfo.add(jLabelTreeTable);
		
		this.varTreeTableModel = new TreeTableDataModel(ReadCallStackHierarchy.createDataStructure());
		
		this.varTreeTable = new TreeTable(this.varTreeTableModel);
		
		JScrollPane p = new JScrollPane(this.varTreeTable);
		p.setVisible(false);
		this.jPanelVarInfo.add(p);
		this.visToggle.registerComponent(1, p);
		// Sub-panel end
		
		this.visToggle.setVisible(0);

		/* --- Listener initialization --- */
		PanelRunStackListener dataListener = new PanelRunStackListener(this,
				this.jCallStack);
		this.jCallStack.addMouseListener(dataListener.jCallStackListener);
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
		
		this.line = line;
		this.col = col;

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
	
	public void setViewMode( byte mode ){
		if( this.viewMode != mode )
			this.viewMode = mode;
		else
			return;
		
		switch( mode ){
		case VM_TABLE:
			this.visToggle.setVisible(0);
			
			if( this.compileManager.isRunning() ){
				this.selectFunction();
				this.globalSelectRoot();
	
				this.updateGlobals();
				this.updateCallStack();
				this.updateLocals();
			}
			break;
		case VM_TREE:
			this.visToggle.setVisible(1);
			
			if( this.compileManager.isRunning() )
				this.updateTreeTable(true);
			break;
		default:
			throw new IllegalStateException("Invalid view mode in variable browser");
		}
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

	public int getErrorCol() {
		return this.col;
	}
	
	public void resetInterpreterData() {

		this.jTableGlobalModel.reset();
		this.jTableGlobal.setModel(this.jTableGlobalModel);

		this.jTableLocalModel.reset();
		this.jTableLocal.setModel(this.jTableLocalModel);

		this.varTreeTable.reset();
		
		this.listener.reset();

		this.callStackSize = 0;

		this.listener.stepComplete();
		this.stepTarget = -1;

		this.jTableGlobal.repaint();
		this.jTableLocal.repaint();
		Object[] o = {};
		this.jCallStack.setListData(o);

		this.modifier.resetInputHighlighter();
	}

	/**
	 * Reads the global variables from the call stack and saves them to the
	 * global variables table. This method is made thread safe as it is
	 * regularly called from the interpreter thread.
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 */
	public void updateGlobals() {

		if (this.globalVarBrowser.peek().getType() == StructureContainer.GLOBAL) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					synchronized (globalVarLock) {
						jLabel1.setText("Global Variables");
					}
				}
			});
		} else {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					synchronized (globalVarLock) {
						jLabel1.setText(globalVarBrowser.peek().getName());
					}
				}
			});
		}

		final GUIrightPanel reference = this;

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				synchronized (globalVarLock) {
					ReadSymbolTable.readGlobals(compileManager,
							jTableGlobalModel, reference, globalVarBrowser
									.peek().getName(), globalVarBrowser.peek()
									.getType(), globalVarBrowser.peek()
									.getAddress());

				}

				jTableGlobal.repaint();
			}
		});
	}

	/**
	 * Updates the call stack list in the main GUI. Also updates the local
	 * variable "int callStackSize", which is important for function skipping
	 * ("step over"). Take care that at least one of the following methods (all
	 * are thread save) is called at each interpreter step
	 * <ul>
	 * <li>public void updateCallStack()</li>
	 * <li>public void updateCallStackSize()</li>
	 * <li>public boolean checkForStepEnd()</li>
	 * </ul>
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 */
	public void updateCallStack() {
		final Stack<String> stack = ReadCallStack.readCallStack();
		this.callStackSize = stack.size();

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				jCallStack.setListData(stack.toArray());
			}
		});
	}

	/**
	 * Updates the local variable "int callStackSize", which is important for
	 * function skipping ("step over"). Does <b>not</b> update the call stack
	 * list of the main GUI. Take care that at least one of the following
	 * methods (all are thread save) is called at each interpreter step
	 * <ul>
	 * <li>public void updateCallStack()</li>
	 * <li>public void updateCallStackSize()</li>
	 * <li>public boolean checkForStepEnd()</li>
	 * </ul>
	 * 
	 * <hr>
	 * <i>THREAD SAFE by default </i>Exception may stop EDT!
	 * <hr>
	 */
	public void updateCallStackSize() {

		this.callStackSize = ReadCallStack.readCallStack().size();
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
			System.out.println("Step completed");
			this.listener.stepComplete();
			this.stepTarget = -1;
			return true;
		}

		return false;
	}

	/**
	 * Reads the local variables from the call stack and saves them to the local
	 * variables table. This method is made thread safe as it is regularly
	 * called from the interpreter thread.
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 */
	public void updateLocals() {

		if (this.localVarBrowser.peek().getType() == StructureContainer.FUNC) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					synchronized (localVarLock) {
						jLabel3.setText("Local Variables @ "
								+ localVarBrowser.peek().getName());
					}
				}
			});
		} else {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					synchronized (localVarLock) {
						jLabel3.setText(localVarBrowser.peek().getName()
								+ " @ "
								+ localVarBrowser.firstElement().getName());
					}
				}
			});
		}

		final GUIrightPanel reference = this;

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				synchronized (localVarLock) {
					ReadSymbolTable.readLocals(compileManager,
							jTableLocalModel, reference, localVarBrowser.peek()
									.getName(), localVarBrowser.peek()
									.getType(), localVarBrowser.peek()
									.getAddress());
				}

				jTableLocal.repaint();
			}
		});
	}

	/**
	 * Local variable table in the right panel of the main GUI will display the
	 * variables of the given function.
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 * 
	 * @param name
	 *            Name of the function
	 * @param index
	 *            Index of the function (in the call stack; the last function
	 *            called has the index 0)
	 */
	public void selectFunction(String name, int index) {
		synchronized (localVarLock) {
			this.localVarBrowser.clear();
			this.localVarBrowser.push(new StructureContainer(name,
					StructureContainer.FUNC, index));
			this.updateLocals();
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				jButtonB2.setVisible(false);
			}
		});
	}

	/**
	 * Sets the variable browsing tree of the local variable table in the right
	 * panel of the main GUI to the current function. This means, the table
	 * displays the local variables of the function which is currently on the
	 * top of the call stack.
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 */
	public void selectFunction() {
		synchronized (localVarLock) {
			this.localVarBrowser.clear();
			this.localVarBrowser.push(new StructureContainer(
					MethodContainer.getMethodName(Memory.loadInt(Memory.getFramePointer() - 8)),
					StructureContainer.FUNC, 0));
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				jButtonB2.setVisible(false);
			}
		});
	}

	/**
	 * Sets one of the variable tables of the right panel of the main GUI to
	 * display content of an array or a structure.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 * 
	 * @param name
	 *            Name of the struct/array
	 * @param address
	 *            Call stack address of the structure
	 * @param type
	 *            Type of the variable browser entry, see
	 *            {@link StructureContainer}
	 * @param global
	 *            TRUE if the struct/array shall be shown in the global
	 *            variables table,<br>
	 *            FALSE if the struct/array shall be shown on the local
	 *            variables table
	 */
	public void selectStruct(String name, int address, int type, boolean global, int x, int y ) {
		//TODO popup
		if( type == StructureContainer.STRING ){
			StringPopup.createPopUp(this.cp, "Hello world", x, y);
		}
		else{
			if (global) {
				synchronized (globalVarLock) {
					this.globalVarBrowser.add(new StructureContainer(name, type,
							address));
					this.jButtonB1.setVisible(true);
					this.updateGlobals();
				}
			} else {
				synchronized (localVarLock) {
					this.localVarBrowser.add(new StructureContainer(name, type,
							address));
					this.jButtonB2.setVisible(true);
					this.updateLocals();
				}
			}
		}
	}

	/**
	 * Sets the global variable browser back to the lowest layer (leaves structs
	 * and arrays). Does not update the global variable table, therefore thread
	 * safe by default.
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 */
	public void globalSelectRoot() {
		synchronized (globalVarLock) {
			this.globalVarBrowser.clear();
			this.globalVarBrowser.add(new StructureContainer(
					"Global variables", StructureContainer.GLOBAL, Memory
							.getGlobalPointer()));
		}
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				jButtonB1.setVisible(false);
			}
		});
	}

	/**
	 * Goes one layer back in the [global/local] variable browser (leaves the
	 * currently displayed array or structure). Eventually removes the "back"
	 * button (if the browser came to the lowest level).
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 * 
	 * @param global
	 *            TRUE if command refers to the global variables table,<br>
	 *            FALSE if command refers to the local variables table
	 */
	public void browseBack(boolean global) {
		if (global) {
			synchronized (globalVarLock) {
				this.globalVarBrowser.pop();
				this.updateGlobals();

				if (this.globalVarBrowser.size() <= 1) {
					this.jButtonB1.setVisible(false);
				}
			}
		} else {
			synchronized (localVarLock) {
				this.localVarBrowser.pop();
				this.updateLocals();

				if (this.localVarBrowser.size() <= 1) {
					this.jButtonB2.setVisible(false);
				}
			}
		}
	}
	
	/**
	 * Reads the call stack, local variables and global variables and saves it to
	 * the tree table.
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 * 
	 * @param render TRUE if the tree table data has to be re-rendered
	 * (if there are more or less nodes than before)<br>
	 * FALSE if the tree table data gets updated
	 * (no new entries, just new values)
	 */
	public void updateTreeTable( final boolean render ){
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				if( render )
					varTreeTable.setTreeModel(ReadCallStackHierarchy.readSymbolTable(compileManager));
				else
					varTreeTable.updateTreeModel(ReadCallStackHierarchy.readSymbolTable(compileManager));
			}
		});
	}
	
	/**
	 * Updates the variable table, call stack or tree table according to the current view mode.
	 */
	public void updateVariableTables( boolean render ){
		if( this.viewMode == VM_TABLE ){
			this.selectFunction();
			this.globalSelectRoot();

			this.updateGlobals();
			this.updateCallStack();
			this.updateLocals();
		}
		else if( this.viewMode == VM_TREE ){
			this.updateTreeTable(render);
		}
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
	 * Locks the "stop" button in "run" mode so that it can non be pressed.
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
		this.jLabelTimer.setText("" + s + " sec");
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
		System.out.println("Stepping over...");
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
			System.out.println("Stepping out...");
		}
	}
	
	public boolean isCodeChangeAllowed(){
		return true;
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

		try {
			sourceCode = ExpandSourceCode.expand(sourceCode,
					this.modifier.getWorkingDirectory(),
					this.modifier.getSourceCodeRegister());
		} catch (final IncludeNotFoundException e1) {
			// An include file could not be found
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					setRuntimeErrorMode("Preprocessor error:", "Include file not found: \"" + e1.getFileName()
							+ "\"", e1.getLine(), 0);
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

		System.out.println("-------------------------------------");

		int i = 1;
		for (String s : sourceCode.split("\n")) {
			System.out.println("" + i + ": " + s);
			i++;
		}

		System.out.println("-------------------------------------");

		// Compile
		at.jku.ssw.cmm.compiler.Error e = compileManager.compile(sourceCode);

		// no errors
		if (e == null) {
			//TODO this.setRightPanel(2);
			return;
		}
		// compiler returns errors
		else {
			setRuntimeErrorMode("Compiler error: ", e.msg, e.line, e.col);

			// Count errors
			/*int errCount = 0;
			while (e != null) {
				System.out.println("line " + e.line + ", col " + e.col + ": "
						+ e.msg);
				errCount++;
				e = e.next;
			}*/

			// Display total errors
			//TODO setErrorCount(errCount);

			// Set error panel
			//TODO this.setRightPanel(1);
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
	public void runInterpreter() {
		this.compile();
		this.compileManager.runInterpreter(listener,
				new IOstream(this.modifier));
	}
}
