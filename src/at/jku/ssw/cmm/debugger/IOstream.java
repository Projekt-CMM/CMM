package at.jku.ssw.cmm.debugger;

import java.util.LinkedList;
import java.util.List;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;
import at.jku.ssw.cmm.debugger.StdInOut;

/**
 * Connects the I/O functions of the Interpreter with the GUI
 * 
 * @author fabian
 *
 */
public class IOstream implements StdInOut {

	/**
	 * Connects the I/O functions of the Interpreter with the GUI
	 * 
	 * @param modifier
	 *            Interface for main GUI manipulations
	 * @param panelRunListener 
	 */
	public IOstream(GUImain main) {
		this.main = main;

		// Get input stream characters
		this.inputStream = new LinkedList<>();
		for (char c : this.main.getLeftPanel().getInputStream().toCharArray()) {
			this.inputStream.add(c);
		}
		
		// Add a stop bit to input string if there are any characters, see (1)
		// This is important to avoid bugs with the library function scanf()
		if( this.inputStream.size() > 0 )
			this.inputStream.add('\0');
			
	}

	/**
	 * Interface for main GUI manipulations
	 */
	private final GUImain main;

	/**
	 * List with all input stream characters
	 */
	private final List<Character> inputStream;

	@Override
	public char in() throws RunTimeException {

		char c;
		
		// Remove '\0' character at the end of the input stream
		// This has to be done separately because it can not be highlighted in
		// the input text are, see constructor of this class (1)
		if( this.inputStream.size() == 1 ){
			this.inputStream.remove(0);
			return '\0';
		}

		try {
			// Read and remove next character
			c = this.inputStream.get(0);
			this.inputStream.remove(0);
		} catch (Exception e) {
			
			// Throw interpreter runtime error if no more input data available
			throw new RunTimeException("no input data", null, 0);
		}
		
		// Highlight the characters which have just been read in the input
		// text area of the main GUI
		this.main.getLeftPanel().increaseInputHighlighter();
		
		return c;
	}

	@Override
	public void out(final char arg0) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Add given character to output text area of the main GUI
				main.getLeftPanel().outputStream("" + arg0);
			}
		});
	}

}
