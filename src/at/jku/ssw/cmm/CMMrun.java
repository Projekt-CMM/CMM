package at.jku.ssw.cmm;

import at.jku.ssw.cmm.compiler.Compiler;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.gui.event.panel.PanelRunListener;
import at.jku.ssw.cmm.gui.interpreter.IOstream;
import at.jku.ssw.cmm.gui.mod.CMMrunnableMod;
import at.jku.ssw.cmm.interpreter.Interpreter;
import at.jku.ssw.cmm.interpreter.excpetions.StackOverflowException;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.interpreter.memory.MethodContainer;

/**
 * This class starts the separate interpreter thread. For thread synchronization see {@link PanelRunListener.java}
 * 
 * @author fabian
 *
 */
public class CMMrun extends Thread {
	
	/**
	 * This class starts the separate interpreter thread. For thread synchronization see {@link PanelRunListener.java}
	 * 
	 * @param debug Interface for debug replies (Interpreter calls "step" method after each operation)
	 * @param compiler The compiler containing the syntax tree and the symbol table
	 * @param stream Interface for the I/O stream. Output messages are shown in the "output" text area of the main GUI.
	 * 			Input messages have to be entered in the "input" text field in the main GUI
	 * @param reply Interface which enables the thread to clean up after exiting by itself, see {@link CMMwrapper}
	 */
	public CMMrun ( PanelRunListener debug, Compiler compiler, IOstream stream, CMMrunnableMod reply ){
		this.debug = debug;
		this.compiler = compiler;
		this.stream = stream;
		this.reply = reply;
	}
	
	//Interface for debug replies (Interpreter calls "step" method after each operation)
	private final PanelRunListener debug;
	
	//The compiler containing the syntax tree and the symbol table
	private final Compiler compiler;
	
	//Interface for the I/O stream.
	//Output messages are shown in the "output" text area of the main GUI.
	//Input messages have to be entered in the "input" text field in the main GUI
	private final IOstream stream;
	
	//Interface which enables the thread to clean up after exiting by itself, see CMMwrapper
	private final CMMrunnableMod reply;

	/**
	 * Starts the interpreter thread
	 * 
	 * <hr><i>THREAD SAVE, opens its own thread, used functions are invoked by EDT</i><hr>
	 */
	@Override
	public void run() {
		
		//Start message
		System.out.println("[thread] Interpreter thread started");
		
		//Allocating memory for interpreter
		Memory.initialize();
		
		//Initializing interpreter
		Interpreter interpreter = new Interpreter( debug, stream, compiler.getStringStorage() );
				
		//Get main function from symbol table
		Obj main = compiler.getSymbolTable().find("main");
				
		//Try to open main function
		try {
			Memory.openStackFrame(main.ast.line, MethodContainer.getMethodId("main"), main.size);
		} catch (StackOverflowException e1) {
			throw new IllegalStateException(e1);
		}
		
		System.out.println("[thread] Running interpreter");
				
		//Run main function
		try{
			interpreter.run(main.ast);
		}
		//Thrown when runtime error occurs
		catch( IllegalStateException e ){
			
			System.out.println("[ERROR] Interpreter thread threw IllegalStateException");
			
			//Clean thread data partly up; leave variable data for GUI runtime error mode
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					reply.setNotRunning( false );
				}
			});
			return;
		}
		
		//Exit message
		System.out.println("[thread] Interpreter thread exited");
		
		//Clean up thread data
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				reply.setNotRunning( true );
			}
		});
	}
}