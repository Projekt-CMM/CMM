package at.jku.ssw.cmm.gui.interpreter;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import at.jku.ssw.cmm.debugger.StdInOut;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;

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
	public IOstream(GUImain main, GUIdebugPanel debug) {
		this.main = main;
		this.debug = debug;

		// Get input stream characters
		this.inputStream = new LinkedList<>();
		for (char c : this.main.getLeftPanel().getInputStream().toCharArray()) {
			this.inputStream.add(c);
		}
	}

	// Interface for main GUI manipulations
	private final GUImain main;
	
	private final GUIdebugPanel debug;

	// List with all input stream characters
	private final List<Character> inputStream;

	@Override
	public char in() {

		char c;

		try {
			c = this.inputStream.get(0);
			this.inputStream.remove(0);
		} catch (Exception e) {

			try {
				java.awt.EventQueue.invokeAndWait(new Runnable() {
					public void run() {
						debug.setErrorMode("no input data", -1);
					}
				});
			} catch (InvocationTargetException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return '\0';
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
