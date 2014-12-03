package at.jku.ssw.cmm;

/**
 * The debug shell is a wrapper class for a simple console text output stream with
 * System.out.println("..."). As the whole project has a lot of partly unnecessary
 * state messages, the debug shell can block some messages.
 * 
 * Every message has an area (the part of the project where it was called) and
 * a state (difference between errors, warnings and log messages).
 * 
 * @author fabian
 */
public class DebugShell {
	
	public static State maxLogLevel = State.LOG;
	
	/**
	 * Write the given text to the system's output stream. This method should replace
	 * System.out.println("...").
	 * 
	 * @param state The kind of message (error, warning, log, ...)
	 * @param area The area where the message originates from (compiler, gui, ...)
	 * @param msg The message/log itself
	 */
	public static void out( State state, Area area, String msg ){
		if( state == State.ERROR || state == State.WARNING )//state == State.STAT && area == Area.COMPILER )
			System.out.println( State.getName(state) + Area.getName(area) + " " + msg );
	}
	
	/**
	 * Enumeration for the kind of message (error, warning, log, ...)
	 * 
	 * @author fabian
	 */
	public enum State{
		LOG,			// Default message
		ERROR,			// Error message (is always displayed)
		WARNING,		// Warning (not as serious as errors)
		STAT;			// Special kind of log, used for default status report
		
		/**
		 * Assigns a prefix to the given message state
		 * @param s The state of the message, eg. State.LOG
		 * @return The message prefix, eg. [log]
		 */
		protected static String getName( State s ){
			switch( s ){
			case ERROR:
				return "[error]";
			case LOG:
				return "[log]";
			case WARNING:
				return "[warning]";
			case STAT:
				return "[Stat]";
			default:
				return "[]";
			}
		}
	}
	
	/**
	 * Enumeration for the area where the message comes from (gui, compiler, ...)
	 * 
	 * @author fabian
	 */
	public enum Area{
		SYSTEM,			// System log, for example thread messages
		GUI,			// Messages about the GUI
		COMPILER,		// Log from the compiler
		PREPROCESSOR,	// Log from the preprocessor
		INTERPRETER,	// Log from the interpreter itself (Interpreter.java)
		DEBUGGER,		// Messages from the debugger (thread and GUI)		
		READVAR,		// Log from loops and recursive methods which initialize parts of the GUI
		ERROR;			// Messages from the part of the GUI which is responsible for displaying
						// error messages to the user
		
		/**
		 * Assigns a prefix to the given message area
		 * @param s The area of the message, eg. Area.GUI
		 * @return The message prefix, eg. [GUI]
		 */
		protected static String getName( Area s ){
			switch( s ){
			case SYSTEM:
				return "[System]";
			case GUI:
				return "[GUI]";
			case COMPILER:
				return "[Compiler]";
			case PREPROCESSOR:
				return "[Preprocessor]";
			case INTERPRETER:
				return "[Interpreter]";
			case DEBUGGER:
				return "[Debugger]";
			case READVAR:
				return "[UpdateVar]";
			case ERROR:
				return "[error]";
			default:
				return "[]";
			}
		}
	}
}
