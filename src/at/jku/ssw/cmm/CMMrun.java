package at.jku.ssw.cmm;

import at.jku.ssw.cmm.compiler.Compiler;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.event.debug.PanelRunListener;
import at.jku.ssw.cmm.interpreter.Interpreter;
import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;
import at.jku.ssw.cmm.interpreter.exceptions.StackOverflowException;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.interpreter.memory.MethodContainer;

/**
 * This class starts the separate interpreter thread. For thread synchronization
 * see {@link PanelRunListener.java}
 * 
 * @author fabian
 *
 */
public class CMMrun extends Thread {

	/**
	 * This class starts the separate interpreter thread. For thread
	 * synchronization see {@link PanelRunListener.java}
	 * 
	 * @param debug
	 *            Interface for debug replies (Interpreter calls "step" method
	 *            after each operation)
	 * @param compiler
	 *            The compiler containing the syntax tree and the symbol table
	 * @param stream
	 *            Interface for the I/O stream. Output messages are shown in the
	 *            "output" text area of the main GUI. Input messages have to be
	 *            entered in the "input" text field in the main GUI
	 * @param reply
	 *            Interface which enables the thread to clean up after exiting
	 *            by itself, see {@link CMMwrapper}
	 */
	public CMMrun(Compiler compiler, Interpreter interpreter, CMMwrapper reply,
			GUIdebugPanel debug) {
		this.compiler = compiler;
		this.interpreter = interpreter;
		this.reply = reply;
		this.debug = debug;
	}

	// The compiler containing the syntax tree and the symbol table
	private final Compiler compiler;

	/**
	 * Interface which enables the thread to clean up after exiting by itself,
	 * see CMMwrapper
	 */
	private final CMMwrapper reply;

	/**
	 * A reference to the interpreter which is the main task of this thread
	 */
	private final Interpreter interpreter;

	/**
	 * A reference to the debug panel class which contains all objects of the
	 * tab "debug" on the right side of the main GUI.
	 */
	private final GUIdebugPanel debug;

	/**
	 * Starts the interpreter thread
	 */
	@Override
	public void run() {

		// Allocating memory for interpreter
		Memory.initialize();

		// Get main function from symbol table
		Obj main = compiler.getSymbolTable().find("main");

		System.out.println("[thread] interpreter thread started");

		// Run main function
		try {
			interpreter.run(main);
		}
		// Thrown when runtime error occurs
		catch (final RunTimeException e) {

			System.err.println("[ERROR] Interpreter thread threw RunTimeException");

			// Clean thread data partly up; leave variable data for GUI runtime
			// error mode
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {

					debug.setErrorMode(e.getMessage(), -1);
				}
			});
			reply.setNotRunning();
			return;
		} catch (Exception e) {
			System.err.println("unknown interpreter error occurred");
			e.printStackTrace();
		}

		// Exit message
		System.out.println("[thread] Interpreter thread exited");

		// Set running flag to false so that the interpreter can be started again
		reply.setNotRunning();
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Set main GUI to ready mode (where the user can edit source code)
				debug.setReadyMode();
			}
		});
	}
}
