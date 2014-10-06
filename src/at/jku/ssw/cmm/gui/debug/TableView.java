package at.jku.ssw.cmm.gui.debug;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.Color;
import java.awt.Component;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.compiler.Strings;
import at.jku.ssw.cmm.gui.datastruct.ReadCallStack;
import at.jku.ssw.cmm.gui.datastruct.ReadSymbolTable;
import at.jku.ssw.cmm.gui.datastruct.StructureContainer;
import at.jku.ssw.cmm.gui.datastruct.VarTableModel;
import at.jku.ssw.cmm.gui.event.debug.PanelRunBrowseListener;
import at.jku.ssw.cmm.gui.event.debug.PanelRunStackListener;
import at.jku.ssw.cmm.gui.popup.PopupInterface;
import at.jku.ssw.cmm.gui.popup.StringPopup;
import at.jku.ssw.cmm.gui.utils.JTableButtonMouseListener;
import at.jku.ssw.cmm.gui.utils.JTableButtonRenderer;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.interpreter.memory.MethodContainer;

public class TableView extends VariableView {
	
	public TableView( GUIdebugPanel main, JPanel panel, PopupInterface popup ){
		
		this.main = main;
		
		this.localVarBrowser = new Stack<>();
		this.globalVarBrowser = new Stack<>();
		
		this.globalVarLock = new Object();
		this.localVarLock = new Object();
		
		this.popup = popup;
		
		this.panel = panel;
		
		this.init();
	}
	
	private final JPanel panel;

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
		
	private JScrollPane jTableGlobalContainer;

	// Table of local variables
	private JTable jTableLocal;

	// Data model for the global variables tables
	private VarTableModel jTableLocalModel;
	private JScrollPane jTableLocalContainer;

	// Call stack list object
	private JList<Object> jCallStack;
		
	private JLabel jLabel2;

	// SelectedFunction
	private final Stack<StructureContainer> globalVarBrowser;
	private final Stack<StructureContainer> localVarBrowser;
	
	// Synchronized lock objects for variable browsers
	private final Object globalVarLock;
	private final Object localVarLock;
	
	// Reference to the debug panel
	private final GUIdebugPanel main;
	
	private final PopupInterface popup;
	
	CMMwrapper compileManager;

	@Override
	public void init() {
		
		/* ---------- TABLE FOR GLOBAL VARs ---------- */
		JPanel paneGlobal1 = new JPanel();
		paneGlobal1.setBorder(new EmptyBorder(5, 5, 5, 5));

		jLabel1 = new JLabel(_("Global variables"));
		jLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
		paneGlobal1.add(jLabel1);

		jButtonB1 = new JButton("\u2190");
		jButtonB1.addMouseListener(new PanelRunBrowseListener(this, true));
		paneGlobal1.add(jButtonB1);
		this.jButtonB1.setVisible(false);

		panel.add(paneGlobal1);

		this.jTableGlobalModel = new VarTableModel();
		this.jTableGlobal = new JTable(this.jTableGlobalModel);

		TableCellRenderer defaultRenderer;
		defaultRenderer = this.jTableGlobal.getDefaultRenderer(JButton.class);
		this.jTableGlobal.getColumn("Value").setCellRenderer(
				new JTableButtonRenderer(defaultRenderer));

		this.jTableGlobal.addMouseListener(new JTableButtonMouseListener(
				this.jTableGlobal));

		this.jTableGlobalContainer = new JScrollPane(this.jTableGlobal);
		this.jTableGlobal.setFillsViewportHeight(true);
		panel.add(this.jTableGlobalContainer);

		/* ---------- CALL STACK ---------- */
		jLabel2 = new JLabel(_("Call Stack"));
		jLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(jLabel2);

		Object[] space = { " ", " ", " ", " ", " " };

		this.jCallStack = new JList<Object>(space);
		this.jCallStack.setBorder(BorderFactory
				.createLineBorder(Color.DARK_GRAY));
		this.jCallStack.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.jCallStack.setLayoutOrientation(JList.VERTICAL);
		this.jCallStack.setVisibleRowCount(10);
		JScrollPane scrollPane2 = new JScrollPane(this.jCallStack);
		panel.add(scrollPane2);

		/* ---------- TABLE FOR LOCAL VARs ---------- */
		JPanel paneLocal1 = new JPanel();
		paneLocal1.setBorder(new EmptyBorder(5, 5, 5, 5));

		jLabel3 = new JLabel(_("Local variables"));
		jLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);
		paneLocal1.add(jLabel3);

		jButtonB2 = new JButton("\u2190");
		jButtonB2.addMouseListener(new PanelRunBrowseListener(this, false));
		paneLocal1.add(jButtonB2);
		this.jButtonB2.setVisible(false);

		panel.add(paneLocal1);
		
		this.jTableLocalModel = new VarTableModel();
		this.jTableLocal = new JTable(this.jTableLocalModel);

		defaultRenderer = this.jTableLocal.getDefaultRenderer(JButton.class);
		this.jTableLocal.setDefaultRenderer(JButton.class,
				new JTableButtonRenderer(defaultRenderer));

		this.jTableLocal.addMouseListener(new JTableButtonMouseListener(
				this.jTableLocal));

		this.jTableLocalContainer = new JScrollPane(this.jTableLocal);
		this.jTableLocal.setFillsViewportHeight(true);
		panel.add(this.jTableLocalContainer);

		/* --- Listener initialization --- */
		PanelRunStackListener dataListener = new PanelRunStackListener(this, this.jCallStack);
		this.jCallStack.addMouseListener(dataListener.jCallStackListener);
	}

	@Override
	public void update(CMMwrapper compiler) {
		
		this.compileManager = compiler;
		
		this.globalSelectRoot();
		this.selectFunction();
		
		this.updateCallStackSize();
		
		this.updateCallStack();
		this.updateGlobals();
		this.updateLocals();
	}

	@Override
	public void standby() {
		
		this.jTableGlobalModel.reset();
		this.jTableGlobal.setModel(this.jTableGlobalModel);

		this.jTableLocalModel.reset();
		this.jTableLocal.setModel(this.jTableLocalModel);
		
		this.jTableGlobal.repaint();
		this.jTableLocal.repaint();
		Object[] o = {};
		this.jCallStack.setListData(o);
		
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

		final TableView reference = this;

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
		this.main.setCallStackSize(stack.size());

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

		this.main.setCallStackSize(ReadCallStack.readCallStack().size());
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
	public void updateLocals( ) {

		if (this.localVarBrowser.peek().getType() == StructureContainer.FUNC) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					synchronized (localVarLock) {
						jLabel3.setText(_("Local Variables") + " @ "
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

		final TableView reference = this;

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

		if( type == StructureContainer.STRING ){
			StringPopup.createPopUp(this.popup, this.panel, Strings.get(Memory.loadStringAddress(address)), x, y);
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
					_("Global variables"), StructureContainer.GLOBAL, Memory
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

}
