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
 
package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import at.jku.ssw.cmm.gui.debug.ErrorMessage;
import at.jku.ssw.cmm.gui.event.CursorListener;
import at.jku.ssw.cmm.gui.event.SourceCodeListener;
import at.jku.ssw.cmm.gui.file.FileManagerCode;
import at.jku.ssw.cmm.gui.init.InitLeftPanel;
import at.jku.ssw.cmm.gui.init.JInputDataPane;
import at.jku.ssw.cmm.preprocessor.Preprocessor;

/**
 * This class controls initializations and interaction of the left panel
 * of the main GUI. This panel contains the text area for the source code
 * ant the text areas for debugging in- and output.
 * 
 * @author fabian
 */
public class GUIleftPanel {

	/**
	 * This class controls initializations and interaction of the left panel
	 * of the main GUI. This panel contains the text area for the source code
	 * ant the text areas for debugging in- and output.
	 * 
	 * @param main A reference to the main GUI class
	 */
	public GUIleftPanel(GUImain main) {
		this.main = main;
		
		// Source code component register
		this.codeRegister = new ArrayList<>();
	}

	/**
	 * A reference to the main GUI class
	 */
	private final GUImain main;

	/**
	 * The panel which visualizes the current state of the debugger,
	 * eg. text editor, step-by-step debugging, error, ...
	 */
	private JPanel jStatePanel;
	
	/**
	 * The label which prints the current state of the debugger onto
	 * the state panel
	 */
	private JLabel jStateLabel;

	/**
	 * The text panel with the source code.
	 */
	private RSyntaxTextArea jSourcePane;
	private JPanel jSourceCodeContainer;

	/**
	 * The text panel with input data for the cmm program.
	 */
	private JInputDataPane jInputPane;

	/**
	 * The text panel for the output stream of the cmm program.
	 */

	private JTextArea jOutputPane;
	/**
	 * When input data from the input source pane is read by the cmm program,
	 * the input data has to be marked as "already read". This happens with
	 * simple highlighting. <br>
	 * The variable inputHightlightOffset is the number of characters of the
	 * input string which has already been used.
	 */
	private int inputHighlightOffset;

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
	private final List<Object[]> codeRegister;
	
	JSplitPane outerPane;
	JSplitPane innerPane;


	/**
	 * Initializes the left part of the main GUI. This class is responsible for all
	 * the objects which are initialized here, so this method should be called at when
	 * loading the GUI.
	 * 
	 * @return A jPanel with all components of the main GUI
	 */
	public JSplitPane init(JFrame jFrame) {
		// Split panel for the left part of the GUI
		outerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		outerPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Panel for the source code text area
		jSourceCodeContainer = new JPanel();
		jSourceCodeContainer.setLayout(new BorderLayout());//new BoxLayout(jSourceCodeContainer, BoxLayout.PAGE_AXIS));
		
		// Panel for the I/O text areas
		innerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		innerPane.setDividerLocation(0.5);
		innerPane.setResizeWeight(0.5);
		
		// Initialize the panel which visualizes the debugger state
		this.jStatePanel = new JPanel();
		this.jStatePanel.setBorder(BorderFactory.createLoweredBevelBorder());
		
		this.jStatePanel.setMinimumSize(new Dimension(20, 30));
		this.jStatePanel.setPreferredSize(new Dimension(30, 30));
		this.jStatePanel.setMaximumSize(new Dimension(8000, 30));

		// Initialize the label which prints the debugger's state
		this.jStateLabel = new JLabel();
		this.jStatePanel.add(this.jStateLabel);

		// Add debugger state panel to source code panel
		jSourceCodeContainer.add(this.jStatePanel, BorderLayout.PAGE_START);

		// Text area (text pane) for source code
		this.jSourcePane = InitLeftPanel.initCodePane(jSourceCodeContainer);

		// Text area for input
		this.jInputPane = InitLeftPanel.initInputPane(innerPane);

		// Text area for output
		this.jOutputPane = InitLeftPanel.initOutputPane(innerPane);
		
		// Update text panel font sizes
		this.updateFontSize();
		
		// Properties of I/O text fields' master panel
		innerPane.setMinimumSize(new Dimension(200, 150));
		innerPane.setPreferredSize(new Dimension(200, 200));
		innerPane.setMaximumSize(new Dimension(2000, 2000));
		
		// Properties of the splitPanel
		outerPane.setTopComponent(jSourceCodeContainer);
		outerPane.setBottomComponent(innerPane);
		outerPane.setDividerLocation(0.4);
		outerPane.setResizeWeight(1.0);
		
		// Custom cursor for split pane divider
		BasicSplitPaneUI ui = (BasicSplitPaneUI)outerPane.getUI();
		BasicSplitPaneDivider divider = ui.getDivider();
		divider.addMouseListener(
			new CursorListener(jFrame, divider, new Cursor(Cursor.S_RESIZE_CURSOR))
		);
		
		// Disable F6 keyboard shortcut for 
		outerPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		  .put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "none");

		// Open latest file and show it's contents in the source code text area
		if (this.main.getSettings().hasCMMFilePath()) {
			try {
				this.jSourcePane.setText(FileManagerCode.readSourceCode(new File(
						this.main.getSettings().getCMMFilePath())));
			} catch (IOException e) {
				new ErrorMessage().showErrorMessage(jFrame, "#2011", main.getSettings().getLanguage());
			}
			//TODO prevent source code panel from undoing setText
			//TODO clear undo list when loading new file
			this.jInputPane.setText(FileManagerCode.readInputData(new File(
					this.main.getSettings().getCMMFilePath())));
		}

		// Variable initialization
		this.inputHighlightOffset = 0;

		// Initialize the source panel listener
		this.jSourcePane.getDocument().addDocumentListener(new SourceCodeListener(this.main));
		// TODO I/O text fields do not yet have document listeners
		// this.jInputPane.getDocument().addDocumentListener(this.codeListener);
		
		this.jSourcePane.setCurrentLineHighlightColor(new Color(0xFFA1A1));
		
		return outerPane;
	}
	
	/**
	 * Updates the font size of all text areas (source code and I/O) in the left panel.
	 * Takes font size as saved in the global settings object.
	 */
	public void updateFontSize(){
		this.jInputPane.setFont(this.jInputPane.getFont().deriveFont((float)this.main.getSettings().getTextSize()));
		this.jOutputPane.setFont(this.jOutputPane.getFont().deriveFont((float)this.main.getSettings().getTextSize()));
		
		this.jSourcePane.setFont(this.jSourcePane.getFont().deriveFont((float)this.main.getSettings().getCodeSize()));
	}

	/**
	 * Highlights the already characters of the input text area which have
	 * already been read. Usually called while interpreter is working.
	 * 
	 * <br>
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

	/**
	 * Note: Method from interface <i>GUImod</i>
	 * 
	 * <br>
	 * <i>THREAD SAFE by default</i>
	 * 
	 * @return The source code written in the source code text area of the main
	 *         GUI
	 */
	public String getSourceCode() {
		return this.jSourcePane.getText();
	}

	/**
	 * Moves the cursor to the given line in the source code (highlights the whole line).<br>
	 * <br><b> Parameter line is absolute. </b>
	 * This means that you have to take the line number in the complete source code (including
	 * libraries). Otherwise take <i>highlightSourceCodeDirectly()</i>.
	 * 
	 * @param line The line which shall be highlighted.
	 */
	public void highlightSourceCode(int line) {

		// Correct offset in source code (offset caused by includes)
		Object[] objLine = Preprocessor.returnFileAndNumber(line, this.main.getLeftPanel().getSourceCodeRegister());

		if(objLine[0].equals("main") && (int)objLine[1] >= 0)
			// Do highlighting
			this.highlightSourceCodeDirectly((int)objLine[1]);
	}
	
	/**
	 * Moves the cursor to the given line in the source code (highlights the whole line).<br>
	 * <br><b> Parameter line is relative. </b>
	 * This means that you have to take the line number in the user's source code (without
	 * libraries). Otherwise take <i>highlightSourceCode()</i>.
	 * 
	 * @param line The line which shall be highlighted.
	 */
	public void highlightSourceCodeDirectly( int line ){

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

	/**
	 * Increments the input highlighter (input text area), which marks the
	 * already read characters, by one.
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 */
	public void increaseInputHighlighter() throws InvocationTargetException, InterruptedException {

		this.inputHighlightOffset++;

		java.awt.EventQueue.invokeAndWait(new Runnable() {
			public void run() {
				highlightInputPane();
			}
		});
	}

	/**
	 * Sets the input highlighter to 0.
	 */
	public void resetInputHighlighter() {
		this.inputHighlightOffset = 0;

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				highlightInputPane();
			}
		});
	}

	/**
	 * Resets the output text panel so that there is no text displayed
	 */
	public void resetOutputTextPane() {

		this.jOutputPane.setText("");
	}

	/**
	 * Makes all text fields of the main GUI uneditable. Should happen before
	 * interpreter starts running so that the source code can't be changed
	 * during runtime.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void lockInput() {

		this.jSourcePane.setEditable(false);
		this.jSourcePane.setBackground(new Color(230, 230, 230));
		this.jInputPane.setEditable(false);
		this.jInputPane.setBackground(new Color(230, 230, 230));
		this.jOutputPane.setEditable(false);
		this.jOutputPane.setBackground(new Color(230, 230, 230));
	}

	/**
	 * Makes all text fields of the main GUI editable. Should happen after the
	 * interpreter has finished running.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void unlockInput() {
		
		this.jSourcePane.setEditable(true);
		this.jSourcePane.setBackground(Color.WHITE);
		this.jInputPane.setEditable(true);
		this.jInputPane.setBackground(Color.WHITE);
		this.jOutputPane.setEditable(true);
		this.jOutputPane.setBackground(Color.WHITE);
	}
	
	/**
	 * Sets left panel to read mode.
	 * This means, the user can edit the source code and the input data.
	 */
	public void setReadyMode() {
		
		this.unlockInput();
		
		this.jSourcePane.setCurrentLineHighlightColor(Color.LIGHT_GRAY);
		
		this.jStatePanel.setBackground(Color.LIGHT_GRAY);
		this.jStateLabel.setText("--- " + _("text edit mode") + " ---");
	}

	/**
	 * Sets the left panel to error mode.
	 * In this mode, the state panel on the top displays the location of
	 * the error and the user can edit the source code in order to fix
	 * the problem.
	 * 
	 * @param line
	 */
	public void setErrorMode(String file, String[] title, int line) {
		
		this.unlockInput();
		
		this.resetInputHighlighter();
		
		this.jSourcePane.setCurrentLineHighlightColor(new Color(0xFFA1A1));
		
		// Correct offset in source code (offset caused by includes)
		Object[] objLine = Preprocessor.returnFileAndNumber(line, this.main.getLeftPanel().getSourceCodeRegister());
		
		this.jStatePanel.setBackground(new Color(255, 131, 131));
		// TODO parse filename from Parser (when library error)
		this.jStateLabel.setText("<html>! ! ! " + (title[0] == null ? _("error") : title[0]) +
			(file == null || file != "main" ? "" : " in file " + file) + " " +
			(line >= 0 ? _("in line") + " " + (int)objLine[1] : "") +
			(title[1] == null ? "" : " " + title[1]) + " ! ! !</html>");
		
		if(objLine[0].equals("main") && (int)objLine[1] >= 0)
			// Do highlighting
			this.highlightSourceCodeDirectly((int)objLine[1]);
	}

	/**
	 * Sets the left panel to run mode. This is when the debugger steps through the
	 * source code automatically. Input methods on text areas are locked.
	 */
	public void setRunMode() {
		
		this.lockInput();
		
		this.jSourcePane.setCurrentLineHighlightColor(new Color(0x92FC9B));

		this.jStatePanel.setBackground(new Color(0x92FC9B));
		this.jStateLabel.setText(">>> " + _("automatic debug mode") + " >>>");
	}

	/**
	 * Sets the left panel to pause mode (while step-by-step debugging).
	 * Input methods on text areas are locked.
	 */
	public void setPauseMode() {
		
		this.lockInput();
		
		this.jSourcePane.setCurrentLineHighlightColor(new Color(0xEFDD1E));
		
		this.jStatePanel.setBackground(new Color(0xEFDD1E));
		this.jStateLabel.setText("||| " + _("pause or step by step mode") + " |||");
	}

	/**
	 * Shows the given String on the output text area of the main GUI. Used for
	 * the output stream of the interpreter.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 * 
	 * @param s
	 *            The output stream as String
	 */
	public void outputStream(String s) {

		this.jOutputPane.append(s);
	}

	/**
	 * <hr>
	 * <i>THREAD SAFE by default</i>
	 * <hr>
	 * 
	 * @return The text in the input text area of the main GUI
	 */
	public String getInputStream() {

		return this.jInputPane.getText();
	}

	/**
	 * The source code register is a list object which saves where the specific
	 * parts of the compiled code (not the code on the screen) are from. The
	 * data is saved as follows:<br>
	 * The list contains arrays of three objects which save:
	 * <ol>
	 * <li>The start line (in the compiled code) of the sequence</li>
	 * <li>The end line</li>
	 * <li>The origin as String, eg. "test2.cmm" or "original file"</li>
	 * </ol>
	 * 
	 * <hr>
	 * <i>THREAD SAFE by default</i>
	 * <hr>
	 * 
	 * @return The source code register list
	 */
	public List<Object[]> getSourceCodeRegister() {
		return this.codeRegister;
	}
	
	/**
	 * @return The text area displaying the source code
	 */
	public RSyntaxTextArea getSourcePane(){
		return this.jSourcePane;
	}
	
	public Rectangle getPositionInSource( int line, int col ) throws BadLocationException {
		
		// Correct offset in source code (offset caused by includes)
		Object[] objLine = Preprocessor.returnFileAndNumber(line, this.main.getLeftPanel().getSourceCodeRegister());
		
		if(!objLine[0].equals("main"))
			throw new BadLocationException("Wrong file", 1);
		
		return this.jSourcePane.modelToView(this.jSourcePane.getLineStartOffset((int)objLine[1])+col);
	}
	
	/**
	 * @return The text area displaying the input data for the debugger
	 */
	public JInputDataPane getInputPane(){
		return this.jInputPane;
	}
	
	public JTextArea getOutputPane(){
		return this.jOutputPane;
	}
	
	public void setOrientation(int orientation) {
		switch(orientation) {
		default:
			swap(true);
			this.outerPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			this.innerPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			this.outerPane.setDividerLocation(0.6);
			this.outerPane.setResizeWeight(1.0);
			this.innerPane.setDividerLocation(0.5);
			break;
		case 1:
			swap(true);
			this.outerPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			this.innerPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			this.outerPane.setDividerLocation(0.6);
			this.outerPane.setResizeWeight(1.0);
			this.innerPane.setDividerLocation(0.5);
			break;
		case 2:
			swap(false);
			this.outerPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			this.innerPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			this.outerPane.setDividerLocation(0.3);
			this.outerPane.setResizeWeight(0.0);
			this.innerPane.setDividerLocation(0.5);
			break;
		case 3:
			swap(true);
			this.outerPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			this.innerPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			this.outerPane.setDividerLocation(0.6);
			this.outerPane.setResizeWeight(1.0);
			this.innerPane.setDividerLocation(0.5);
			break;
		}
	}
	
	private void swap(boolean def) {
		
		// Reset splitPane
		this.outerPane.setTopComponent(null);
		this.outerPane.setBottomComponent(null);
		
		// Decide what to do
		if( def ) {
			this.outerPane.setTopComponent(this.jSourceCodeContainer);
			this.outerPane.setBottomComponent(this.innerPane);
		}
		else {
			this.outerPane.setBottomComponent(this.jSourceCodeContainer);
			this.outerPane.setTopComponent(this.innerPane);
		}
	}
	
	public int getOuterPaneOrientation() {
		return this.outerPane.getOrientation();
	}
	
	public int getInnerPaneOrientation() {
		return this.innerPane.getOrientation();
	}
}
