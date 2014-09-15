package at.jku.ssw.cmm;

import at.jku.ssw.cmm.compiler.Compiler;
import at.jku.ssw.cmm.compiler.Error;
import at.jku.ssw.cmm.compiler.Tab;
import at.jku.ssw.cmm.gui.GUIdebugPanel;
import at.jku.ssw.cmm.gui.event.debug.PanelRunListener;
import at.jku.ssw.cmm.gui.interpreter.IOstream;
import at.jku.ssw.cmm.gui.mod.CMMrunnableMod;
import at.jku.ssw.cmm.gui.mod.GUImainMod;

/**
 * Wrapper class for the interpreter thread starter and manager class for the compiler.
 * Takes care that the interpreter is not executed two times at once and that it is
 * not started before compiling.
 * 
 * @author fabian
 *
 */
public class CMMwrapper implements CMMrunnableMod {
	
	/**
	 * Wrapper class for the interpreter thread starter and manager class for the compiler.
	 * Takes care that the interpreter is not executed two times at once and that it is
	 * not started before compiling.
	 * 
	 * @param modifier Interface for main GUI manipulations, for example syntax line highlighting
	 * @param rPanel Interface for manipulating the right GUI panel, in this case for showing error messages
	 */
	public CMMwrapper( GUImainMod modifier, GUIdebugPanel rPanel ){
		
		this.modifier = modifier;
		this.rPanel = rPanel;
		
		this.running = false;
	}
	
	//TRUE if interpreter is already running
	//Take care that thesetNotRunning() method is called the end of the interpreter thread
	//otherwise the interpreter thread can't be launched again
	private boolean running;
	
	//Interpreter thread class
	private CMMrun interpreter;
	
	//Compiler class
	private Compiler compiler;
	
	//Interface for main GUI manipulations, for example syntax line highlighting
	private final GUImainMod modifier;
	
	//Interface for manipulating the right GUI panel, in this case for showing error messages
	private final GUIdebugPanel rPanel;
	
	/**
	 * Runs interpreter in a seperate thread. For thread synchronisation see {@link PanelRunListener.java}.
	 * Also locks the three text fields of the main GUI (syntax, in, out)
	 * so that they can not be edited during runtime.
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param listener Interface for debug replies (Interpreter calls "step" method after each operation)
	 * @param stream Interface for the I/O stream. Output messages are shown in the "output" text area of the main GUI.
	 * 			Input messages have to be entered in the "input" text field in the main GUI
	 */
	public void runInterpreter( PanelRunListener listener, IOstream stream ) {
		
		//Check if another interpreter thread is already running
		if( this.compiler != null && !this.running ){
			
			//Make all text areas of the main GUI uneditable so that the code can not be changed during runtime 
			this.modifier.lockInput();
			
			//Set the running flag
			this.running = true;
			
			//Create new interpreter object
			this.interpreter = new CMMrun(listener, compiler, stream, this);
			
			//Run interpreter thread
			this.interpreter.start();
		}
		//Another thread is already running
		else
			//Error message
			System.out.println("[ERROR] Already running or not compiled!");
	}
	
	/**
	 * Compiles the source code given as parameter. This method has to be run before interpreting.
	 * The interpreter method above checks if there is a compiler, however it does not check whether
	 * the data given is valid.
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param code The source code as String
	 * @return The compiler errors (null if no errors)
	 */
	public Error compile( String code ) {
		
		//Object for the compiler is allocated
		this.compiler = new Compiler();
		
		//No debug modes
		compiler.debug[0] = false;
		compiler.debug[1] = false;
				
		//Compile current file
		this.compiler.compile( code );
				
		//Error displaying and error count
		Error e = this.compiler.getError();
		
		//An error occurred
		if( e != null ){
			//Delete compiler data so that the interpreter can't be started
			this.compiler = null;
			
			//Return the errors
			return e;
		}
		
		return null;
	}

	/**
	 * This method unregisters the interpreter thread and therefore enables to start it again. <br>
	 * <b>WARNING: This method does NOT kill the thread. </b> It just cleans up the things the thread
	 * can't do by itself. Do not call this method unless you really know what you do! <br>
	 * This should be called at following points by default:
	 * <ul>
	 * <li> When the interpreter thread ends </li>
	 * <li> When a runtime error has occurred and the user quits the error mode of the right panel
	 *    	in the main GUI. </li>
	 * </ul>
	 *    
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param success TRUE if the interpreter finished with no errors, FALSE if there has been a runtime error.
	 * 					In this case, this method does not delete the interpreter data so that the user can
	 * 					still read variables and the call stack from the main GUI.
	 * 					Then, this method is called a second time and cleans everything up. 
	 */
	@Override
	public void setNotRunning( boolean success ) {
		
		System.out.println("Interpreter thread unregistered");
		this.running = false;
		
		if( success ){
			this.rPanel.resetInterpreterData();
			this.modifier.unlockInput();
		}
	}
	
	/**
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @return The compiler's symbol table
	 */
	public Tab getSymbolTable(){
		return this.compiler.getSymbolTable();
	}
	
	/**
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @return TRUE if an interpreter thread is already running,<br>
	 * FALSE if not
	 */
	public boolean isRunning(){
		return this.running;
	}
}
