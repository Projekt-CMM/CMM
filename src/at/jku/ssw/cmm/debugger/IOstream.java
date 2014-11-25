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
		
		if( this.inputStream.size() > 0 )
			this.inputStream.add('\0');
			
	}

	// Interface for main GUI manipulations
	private final GUImain main;

	// List with all input stream characters
	private final List<Character> inputStream;

	@Override
	public char in() throws RunTimeException {

		char c;
		
		if( this.inputStream.size() == 1 ){
			this.inputStream.remove(0);
			return '\0';
		}

		try {
			c = this.inputStream.get(0);
			this.inputStream.remove(0);
		} catch (Exception e) {
			
			throw new RunTimeException("no input data", null);
		}

		this.main.getLeftPanel().increaseInputHighlighter();
		return c;
	}

	@Override
	public void out(final char arg0) {

		System.out.println("Stream from interpreter: " + arg0);

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				main.getLeftPanel().outputStream("" + arg0);
			}
		});
	}

}
