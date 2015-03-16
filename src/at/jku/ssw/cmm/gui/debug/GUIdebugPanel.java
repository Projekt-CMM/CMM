/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */

package at.jku.ssw.cmm.gui.debug;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.debugger.IOstream;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.treetable.var.TreeTableView;
import at.jku.ssw.cmm.preprocessor.exception.PreprocessorException;
import at.jku.ssw.cmm.preprocessor.Preprocessor;

/**
 * This class controls the panel in the "debug" tab in the right part of the
 * main GUI. This class forms an interface between the debugger and the main
 * GUI. <br>
 * Moreover, it contains the debugging control elements (play/step/stop button)
 * and the variable tree table.
 * 
 * @author fabian
 *
 */
public class GUIdebugPanel {

	/**
	 * This class controls the panel in the "debug" tab in the right part of the
	 * main GUI. This class forms an interface between the debugger and the main
	 * GUI.
	 * 
	 * @param cp
	 *            Main component of the main GUI
	 * @param main
	 *            Reference to the main GUI
	 */
	public GUIdebugPanel(JPanel cp, GUImain main) {
		this.main = main;

		cp.setLayout(new BorderLayout());

		// Initialize the control elements
		this.jControlPanel = new JPanel();
		this.jControlPanel.setBorder(new TitledBorder(_("Control elements")));
		this.ctrlPanel = new GUIcontrolPanel(this.jControlPanel, this, main);
		cp.add(jControlPanel, BorderLayout.PAGE_START);

		// Initialize the variable tree table
		JPanel jVarPanel = new JPanel();
		jVarPanel.setBorder(new TitledBorder(_("Variables")));
		jVarPanel.setLayout(new BorderLayout());

		this.varView = new TreeTableView(main, jVarPanel, main.getSettings()
				.getCMMFile());

		cp.add(jVarPanel, BorderLayout.CENTER);

		this.breakpoints = new ArrayList<>();

		this.compileManager = new CMMwrapper(this.main, this);
	}

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
	private final TreeTableView varView;

	/**
	 * Interface for main GUI manipulations
	 */
	private final GUImain main;

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
		return (int) this.main.getLeftPanel().getSourceCodeRegister().get(0)[0];
	}

	/**
	 * @return A list of all breakpoints
	 */
	public List<Integer> getBreakPoints() {
		return this.breakpoints;
	}

	/**
	 * Deletes all breakpoints before the given line. Used when the user
	 * switches from any maine to "fast run" maine; so that the interpreter does
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
		this.varView.standby(this.main.getSettings().getCMMFile());
	}

	/**
	 * Highlights the variable with the given address in the variable tree table
	 * 
	 * @param adr
	 *            The address of the variable to be highlighted
	 * @param changed
	 *            TRUE if highlighting changed variables, FALSE if highlighting
	 *            read variables
	 */
	public void highlightVariable(final int adr, final boolean changed) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				varView.highlightVariable(adr, changed);
			}
		});
	}

	/**
	 * Updates the variable table, call stack or tree table according to the
	 * current view maine.
	 */
	public void updateVariableTables(boolean completeUpDate) {
		this.varView.update(compileManager, this.main.getSettings()
				.getCMMFile(), completeUpDate);
	}

	/**
	 * Updates the font size of the text in the variable tree table
	 */
	public void updateFontSize() {
		this.varView.updateFontSize();
	}

	/**
	 * Sets the main GUI to ready mode. This is when the user can edit his
	 * source code, save and open files, etc.
	 */
	public void setReadyMode() {

		DebugShell.out(State.LOG, Area.GUI, "Setting main GUI to ready mode");

		this.ctrlPanel.setReadyMode();
		this.ctrlPanel.getListener().setReadyMode();
		this.main.setReadyMode();

		// Mode-specific
		this.main.getLeftPanel().resetInputHighlighter();
		this.varView.standby(this.main.getSettings().getCMMFile());

		// Input lock
		this.main.unlockInput();
	}

	/**
	 * Sets the main GUI to error mode
	 * 
	 * @param html
	 *            The error message
	 * @param file
	 *            The file where the error ocurred (null if not in a library)
	 * @param line
	 *            The line where the error ocurred
	 * @param keepTable
	 *            FALSE if the variable tree table shall be reset immediately,
	 *            otherwise TRUE
	 */
	public void setErrorMode(String html, String file, int line,
			boolean keepTable) {

		DebugShell.out(State.LOG, Area.GUI, "Setting main GUI to error mode, "
				+ (keepTable ? "keeping" : "discarding") + " table");

		// Set debugging control elements to ready mode
		this.ctrlPanel.setReadyMode();
		this.ctrlPanel.getListener().setReadyMode();

		// Set main GUI (and its child elements) to error mode
		this.main.setErrorMode(html, file, Integer.parseInt(Preprocessor
				.returnFileAndNumber(line, this.main.getLeftPanel()
						.getSourceCodeRegister())[1].toString()));

		// Eventually reset variable tree table
		if (!keepTable)
			this.varView.standby(this.main.getSettings().getCMMFile());

		// Unlock input methods -> user can edit source code again
		this.main.unlockInput();
	}

	/**
	 * Sets the main GUI to run mode (debugger is running automatically)
	 */
	public void setRunMode() {
		this.ctrlPanel.setRunMode();
		this.ctrlPanel.getListener().setRunMode();
		this.main.setRunMode();

		// Input lock
		this.main.lockInput();
	}

	/**
	 * Sets the main GUI to pause mode (user is debugging step by step)
	 */
	public void setPauseMode() {
		this.ctrlPanel.setPauseMode();
		this.ctrlPanel.getListener().setPauseMode();
		this.main.setPauseMode();

		// Input lock
		this.main.lockInput();
	}

	/**
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
	public GUIcontrolPanel getControlPanel() {
		return this.ctrlPanel;
	}

	// TODO Invoke this method as side task
	// TODO make thread safe and update comments
	/**
	 * Runs the compiler via the compiler wrapper class, see {@link CMMwrapper}.
	 * Automatically switches to "error" maine if necessary.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT.</i> This
	 * method is not thread safe as it changes the right panel of the main GUI.
	 * <hr>
	 */
	private boolean compile() {

		// Get source code from source code text panel
		String sourceCode = this.main.getLeftPanel().getSourceCode();
		
		// Replace tabs with spaces (used for correct position calculation)
		sourceCode = sourceCode.replace("\t",
				new String(new char[this.main.getLeftPanel().getSourcePane().getTabSize()+1]).replace("\0", " ")
		);

		// Assemble complete source code using preprocessor
		try {
			sourceCode = Preprocessor.expand(sourceCode, this.main
					.getSettings().getWorkingDirectory(), this.main
					.getLeftPanel().getSourceCodeRegister(), this.breakpoints);
		}
		// Preprocessor exception, reason ins known
		catch (PreprocessorException e1) {

			// Reset source code file register
			Object[] e = { 1, 0, null };
			this.main.getLeftPanel().getSourceCodeRegister().clear();
			this.main.getLeftPanel().getSourceCodeRegister().add(e);

			// Show preprocessor error
			this.setErrorMode(e1.getMessage(), e1.getFile(), e1.getLine(),
					false);

			return false;
		} catch (IOException e1) {
			// Reset source code file register
			Object[] e = { 1, 0, null };
			this.main.getLeftPanel().getSourceCodeRegister().clear();
			this.main.getLeftPanel().getSourceCodeRegister().add(e);

			// Display default message for preprocessor error
			this.setErrorMode("Preprocessor.IOException", "", -1, false);

			return false;
		}
		// Preprocessor error, reason is unknown
		catch (Exception e1) {

			// Reset source code file register
			Object[] e = { 1, 0, null };
			this.main.getLeftPanel().getSourceCodeRegister().clear();
			this.main.getLeftPanel().getSourceCodeRegister().add(e);

			// Display default message for preprocessor error
			this.setErrorMode("Preprocessor." + e1, "", -1, false);

			return false;
		}

		/* --- Code statistics in the shell --- */
		DebugShell.out(State.STAT, Area.COMPILER,
				"\n-------------------------------------\nUsed input files: ");
		for (Object[] o : this.main.getLeftPanel().getSourceCodeRegister()) {
			DebugShell.out(State.STAT, Area.COMPILER, "" + o[2] + ", line "
					+ o[0] + " - " + o[1]);
		}
		DebugShell.out(State.STAT, Area.COMPILER,
				"-------------------------------------");

		DebugShell.out(State.STAT, Area.COMPILER,
				"Source code begins @ line "
						+ (int) this.main.getLeftPanel()
								.getSourceCodeRegister().get(0)[0] + "\n");

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
		/* --- end of code statistics --- */

		// Compile the source code
		at.jku.ssw.cmm.compiler.Error e = null;
		try {
			e = compileManager.compile(sourceCode);
		}
		// In case of compiler crash
		catch (Exception e1) {
			this.setErrorMode("Compiler." + e1, null, -1, false);
		}

		// compiler returned errors
		if (e != null) {
			this.setErrorMode(e.msg, null, e.line, false);
			return false;
		}

		return true;
	}

	/**
	 * Compiles the source code and starts the interpreter thread via the
	 * compiler wrapper class. Initializes the call stack.
	 * 
	 * <br>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 */
	public boolean runInterpreter() {

		// Save the current file
		//this.main.getSaveManager().directSave();
		//this.main.setFileSaved();
		//this.main.updateWinFileName();
		//this.updateFileName();

		// Compile and run
		if (this.compile())
			return this.compileManager.runInterpreter(ctrlPanel.getListener(),
					new IOstream(this.main));
		else
			return false;
	}
}
