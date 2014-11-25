package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import at.jku.ssw.cmm.gui.event.SourceCodeListener;
import at.jku.ssw.cmm.gui.file.FileManagerCode;
import at.jku.ssw.cmm.gui.include.ExpandSourceCode;
import at.jku.ssw.cmm.gui.init.InitLeftPanel;
import at.jku.ssw.cmm.gui.init.JInputDataPane;

public class GUIleftPanel {

	public GUIleftPanel(GUImain main) {
		this.main = main;
		
		// Source code component register
		this.codeRegister = new ArrayList<>();
	}

	private final GUImain main;

	private JPanel jStatePanel;
	private JLabel jStateLabel;

	/**
	 * The text panel with the source code.
	 */
	private RSyntaxTextArea jSourcePane;

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

	private SourceCodeListener codeListener;

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


	/**
	 * Initializes the left part of the main GUI. This class is responsible for all
	 * the objects which are initialized here, so this method should be called at when
	 * loading the GUI.
	 * 
	 * @return A jPanel with all components of the main GUI
	 */
	public JSplitPane init() {
		// Left part of the GUI
		JSplitPane jPanelLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		jPanelLeft.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Panel to display the GUI mode
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
		
		this.jStatePanel = new JPanel();
		this.jStatePanel.setBorder(BorderFactory.createLoweredBevelBorder());

		this.jStateLabel = new JLabel();

		this.jStatePanel.add(this.jStateLabel);

		panel1.add(this.jStatePanel);

		// Text area (text pane) for source code
		this.jSourcePane = InitLeftPanel.initCodePane(panel1,
				this.main.getSettings());

		// Text area for input
		this.jInputPane = InitLeftPanel.initInputPane(panel2);

		// Text area for output
		this.jOutputPane = InitLeftPanel.initOutputPane(panel2);
		
		panel2.setMinimumSize(new Dimension(200, 150));
		panel2.setPreferredSize(new Dimension(200, 200));
		panel2.setMaximumSize(new Dimension(2000, 2000));
		
		jPanelLeft.setTopComponent(panel1);
		jPanelLeft.setBottomComponent(panel2);
		jPanelLeft.setDividerLocation(0.4);
		jPanelLeft.setResizeWeight(1.0);

		// Read last opened files
		if (this.main.hasPath()) {
			this.jSourcePane.setText(FileManagerCode.readSourceCode(new File(
					this.main.getFileNameAndPath())));
			this.jInputPane.setText(FileManagerCode.readInputData(new File(
					this.main.getFileNameAndPath())));
		}

		// Variable initialization
		this.inputHighlightOffset = 0;

		// Initialize the source panel listener
		this.codeListener = new SourceCodeListener(this.main);
		this.jSourcePane.getDocument().addDocumentListener(this.codeListener);
		// TODO
		// this.jInputPane.getDocument().addDocumentListener(this.codeListener);

		return jPanelLeft;
	}

	/**
	 * Highlights the already used characters of the input text area. Usually
	 * called while interpreter is working.
	 * 
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
	 * <hr>
	 * <i>THREAD SAFE by default</i>
	 * <hr>
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

		// Line out of user source code range (#include)
		if (line <= (int) this.codeRegister.get(0)[0])
			return;

		// Correct offset in source code (offset caused by includes)
		line = ExpandSourceCode.correctLine(line, (int) this.codeRegister.get(0)[0],
							this.codeRegister.size());

		// Do highlighting
		this.highlightSourceCodeDirectly(line);
	}
	
	/**
	 * Moves the cursor to the given line in the source code (highlights the whole line).<br>
	 * <br><b> Parameter line is direct. </b>
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
	 */
	public void increaseInputHighlighter() {

		this.inputHighlightOffset++;

		java.awt.EventQueue.invokeLater(new Runnable() {
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
	
	public void setReadyMode() {
		
		this.unlockInput();
		
		this.jStatePanel.setBackground(Color.LIGHT_GRAY);
		this.jStateLabel.setText("--- " + _("text edit mode") + " ---");
	}

	public void setErrorMode(int line) {
		
		this.unlockInput();
		
		this.jStatePanel.setBackground(new Color(255, 131, 131));
		this.jStateLabel.setText("! ! ! " + _("error") + " " + (line >= 0 ? _("in line") + " " + line : "") + " ! ! !");
		
		if( line >= 0 )
			this.highlightSourceCodeDirectly(line);
	}

	public void setRunMode() {
		
		this.lockInput();

		this.jStatePanel.setBackground(Color.GREEN);
		this.jStateLabel.setText(">>> " + _("automatic debug mode") + " >>>");
	}

	public void setPauseMode() {
		
		this.lockInput();
		
		this.jStatePanel.setBackground(Color.YELLOW);
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
	
	public RSyntaxTextArea getSourcePane(){
		return this.jSourcePane;
	}
	
	public JInputDataPane getInputPane(){
		return this.jInputPane;
	}
}
