package at.jku.ssw.cmm.gui.event.debug;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.debugger.Debugger;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.compiler.Node;

/**
 * This class has two important tasks:
 * <ol>
 * <li>This is a listener for all buttons and the slider on the top of the right
 * panel of the main GUI. It therefore connects the user's actions with the
 * interpreter.</li>
 * <li>This is why, this class is also the listener for the interpreter debugger
 * interface which is responsible for visualizing steps of the program and
 * displaying runtime error messages.
 * </ol>
 * 
 * Special care has to be taken about thread safety, as functions invoked by the
 * interpreter run in a different thread from the awt event dispatcher thread.
 * 
 * @author fabian
 *
 */
public class PanelRunListener implements Debugger {

	/**
	 * This class monitors the user actions on the interpreter runtime buttons
	 * (top of right panel) and executes queries from the interpreter.
	 * 
	 * @param modMain
	 *            Interface for main GUI manipulations, eg. source code
	 *            highlighting
	 * @param master
	 *            Reference to the right panel wrapper class
	 */
	public PanelRunListener(GUImainMod modMain, GUIdebugPanel master) {
		this.modMain = modMain;
		this.master = master;

		this.run = false;
		this.keepRunning = false;
		this.wait = false;

		this.stepOver = false;

		// Default value of the slider
		this.delay = 2;
	}

	/**
	 * Interface for main GUI manipulations, eg. source code highlighting
	 */
	private final GUImainMod modMain;

	/**
	 * Reference to the right panel wrapper class
	 */
	private final GUIdebugPanel master;

	/**
	 * FALSE = pause | TRUE = play
	 */
	private boolean run;

	/**
	 * TRUE -> Interpreter is still running | FALSE -> runtime error or ready
	 * See right panel mode definitions (in the documentation)
	 */
	private boolean keepRunning;

	/**
	 * TRUE -> interpreter may carry on | FALSE -> interpreter is waiting for user action
	 * Do not set this variable from directly. Use the synchronized methods!
	 */
	private boolean wait;

	/**
	 * TRUE -> interpreter is in step over mode | FALSE -> nothing happens
	 */
	private boolean stepOver;

	/**
	 * The delay between two interpreter steps in run mode
	 */
	private int delay;

	/**
	 * Reference to the timer which is scheduling the run mode delay <br>
	 * "null" if unused
	 */
	private Timer timer;
	
	/**
	 * The last node in the user's source code which has been processed
	 */
	private Node lastNode;

	/* --- functional methods --- */
	/**
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 */
	public void reset() {
		this.run = false;
		this.keepRunning = false;
		this.wait = false;

		this.stepOver = false;

		this.delay = master.getControlPanel().getInterpreterSpeedSlider() - 1;

		master.getControlPanel().setTimerLabelSeconds(delayScale(delay));

		if (this.timer != null)
			this.timer.cancel();

		this.master.getControlPanel().unsetRunTimeError();
		this.master.getControlPanel().unsetStepOverButton();
		this.master.getControlPanel().unsetStepOutButton();

		this.master.getControlPanel().lockStopButton();
		this.master.getControlPanel().unlockStepButton();
		this.master.getControlPanel().setPlay();
		
		this.master.getControlPanel().standby();
		
		this.lastNode = null;

		DebugShell.out(State.LOG, Area.DEBUGMODE, "setting ready by reset");
		
		this.modMain.setReadyMode();
	}

	/**
	 * Sets the right panel to RUN mode (see documentation)
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	private void setRunMode() {
		this.run = true;
		this.keepRunning = true;

		this.master.getControlPanel().unlockStopButton();
		this.master.getControlPanel().lockStepButton();
		this.master.getControlPanel().setPause();
		
		this.master.getControlPanel().running();

		DebugShell.out(State.LOG, Area.DEBUGMODE, "setting run, delay = " + this.delay);
		
		this.modMain.setRunMode();
	}

	/**
	 * Sets the right panel to PAUSE mode (see documentation)
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	private void setPauseMode() {
		this.run = false;
		this.keepRunning = true;

		this.master.getControlPanel().unlockStopButton();
		this.master.getControlPanel().unlockStepButton();
		this.master.getControlPanel().setPlay();
		
		this.master.getControlPanel().running();

		DebugShell.out(State.LOG, Area.DEBUGMODE, "setting pause");
		
		this.modMain.setPauseMode();
	}

	/**
	 * Sets the right panel to READY mode (see documentation)
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void setReadyMode() {

		this.master.resetInterpreterData();
	}

	/**
	 * Sets the right panel to ERROR mode (see documentation)
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void setErrorMode(String title, String message, int line, int col, boolean view) {
		this.run = true;
		this.keepRunning = false;

		this.master.getControlPanel().unlockStepButton();
		this.master.getControlPanel().unlockStopButton();

		this.master.getControlPanel().setRuntimeErrorMode(title + ": ", message, line, col, view);

		DebugShell.out(State.LOG, Area.DEBUGMODE, "setting error");
		
		this.modMain.setErrorMode(line);
	}

	/**
	 * <hr>
	 * <i>THREAD SAFE by default</i>
	 * </hr>
	 * 
	 * @return TRUE if right GUI panel is in RUN mode, otherwise FALSE
	 */
	public boolean isRunMode() {
		if (this.run && this.keepRunning)
			return true;
		return false;
	}

	/**
	 * <hr>
	 * <i>THREAD SAFE by default</i>
	 * </hr>
	 * 
	 * @return TRUE if right GUI panel is in PAUSE mode, otherwise FALSE
	 */
	public boolean isPauseMode() {
		if (!this.run && this.keepRunning)
			return true;
		return false;
	}

	/**
	 * <hr>
	 * <i>THREAD SAFE by default</i>
	 * </hr>
	 * 
	 * @return TRUE if right GUI panel is in READY mode, otherwise FALSE
	 */
	public boolean isReadyMode() {
		if (!this.run && !this.keepRunning)
			return true;
		return false;
	}

	/**
	 * <hr>
	 * <i>THREAD SAFE by default</i>
	 * </hr>
	 * 
	 * @return TRUE if right GUI panel is in ERROR mode, otherwise FALSE
	 */
	public boolean isErrorMode() {
		if (this.run && !this.keepRunning)
			return true;
		return false;
	}

	/* --- step over methods --- */
	/**
	 * Initializes the "step over" mode within the right GUI panel. <b>Do not
	 * call this method, </b> for a complete initialization call {@link
	 * GUIrightPanel.stepOver()}
	 * 
	 * <hr>
	 * <i>SYNCHRONIZED | THREAD SAFE </i>
	 * <hr>
	 */
	synchronized public void stepOver() {
		this.stepOver = true;
	}

	/**
	 * Terminates the "step over" mode. Does clean up and exits safe.
	 * 
	 * <hr>
	 * <i>SYNCHRONIZED | THREAD SAFE </i>
	 * <hr>
	 */
	synchronized public void stepComplete() {
		this.stepOver = false;
		this.master.getControlPanel().unsetStepOverButton();
	}

	/* --- debugger interpreter listeners --- */
	@Override
	public void abort(final String message, final Node node) {

		// TODO is this thread safe???
		if (this.timer != null)
			timer.cancel();
		
		this.master.setErrorLine(node.line);

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				setErrorMode("Runtime error", message, node.line, 0, false);
			}
		});
	}

	@Override
	public boolean step(final Node arg0) {

		DebugShell.out(State.LOG, Area.DEBUGGER, "" + arg0);
		
		//Update latest node's line
		this.lastNode = arg0;
		this.master.updateCallStackSize();

		/* --- Node #1: Step over mode? --- */
		if (this.stepOver) {
			// -> Node #1 - YES
			/* --- Node #2: Step over finished? --- */
			if (!this.master.checkForStepEnd()) {
				// -> Mode #2 - NO
				return this.keepRunning;
			}
		}

		// -> Node #1 - NO | Node #2 - YES
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				modMain.highlightSourceCode(arg0.line);
			}
		});

		/* --- Node #3: Quick mode? --- */
		if (this.isRunMode() && this.delay == 0) {
			
			/* --- Node #4: Passing a breakpoint --- */
			if( !this.master.getBreakPoints().isEmpty() && arg0.line >= this.master.getBreakPoints().get(0)-1 ){
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						setPauseMode();
					}
				});
				DebugShell.out(State.LOG, Area.DEBUGGER, "Stopped at breakpoint: "  + arg0.line + " - " + this.master.getBreakPoints().get(0) );
				this.master.getBreakPoints().remove(0);
			}
			
			//Delay the interpreter for 10ms so that the GUI is still able to work
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				DebugShell.out(State.ERROR, Area.DEBUGGER, "Failed to delay interpreter thread!");
				e.printStackTrace();
			}
			return this.keepRunning;
		}

		// -> Node #3 - DEFAULT
		/* --- Update Block --- */
		this.master.getControlPanel().updateStepOutButton();//*.next -> main()
		
		/* --- Node #5 - Variable value changed --- */
		this.master.updateVariableTables(this.master.callStackChanged());
		
		this.master.highlightVariable(this.master.getCompileManager().getRequest().getLastChangedAddress());
		
		this.timer = null;

		/* --- Node #6: Pause or Run mode --- */
		if (this.isPauseMode()) {
			/* --- Node #7: Create Buttons --- */
			if( arg0.kind == Node.ASSIGN )
				this.doStepOverButtonCheck(arg0.right);
			else
				this.doStepOverButtonCheck(arg0);
		}
		/* --- Node #6: Pause or Run mode --- */
		else if (this.isRunMode() && this.delay > 0) {

			/* --- Node #9: Step over if external function call --- */
			if( arg0.kind == Node.ASSIGN )
				this.doStepOverRunCheck(arg0.right);
			else
				this.doStepOverRunCheck(arg0);

			this.timer = new Timer();
			// Start timer
			this.timer.schedule(new TimerTask() {
				@Override
				public void run() {
					userReply();
				}
			}, (int)(delayScale(delay)*1000) );
		}
		/* --- Node #6: Default exit --- */
		else {
			throw new IllegalStateException();
		}

		waitForUserReply();

		this.timer = null;

		return keepRunning;
	}
	
	/**
	 * Checks if step over button shall be visible or not. Eventually toggles visibility.
	 * 
	 * @param n The current node in the abstract syntax tree
	 */
	private void doStepOverButtonCheck( Node n ){
		//Is function call? (NOT predefined function)
		if( n.kind == Node.CALL && n.obj.ast != null ){
			//Calls function from include
			if( n.obj.ast.line <= this.master.getBeginLine() )
				this.master.getControlPanel().setStepOverButtonAlone();
			//Calls local function
			else
				this.master.getControlPanel().setStepOverButton();
		}
		//No valid function call -> no step over button
		else
			this.master.getControlPanel().unsetStepOverButton();
	}
	
	/**
	 * This method shall always be called when the interpreter has to step over a function
	 * for any reson. It checks wheather stepping over is possible and initiates the stepping
	 * over process.
	 * 
	 * @param n The current node in the abstract syntax tree
	 */
	private void doStepOverRunCheck( Node n ){
		//Is function call? (NOT predefined function) from external file
		if( n.kind == Node.CALL && n.obj.ast != null && n.obj.ast.line <= this.master.getBeginLine() ){
			this.master.stepOver();
		}
	}
	
	/* --- thread synchronization --- */
	/**
	 * This method blocks the program until another thread invokes the method
	 * {@link userReply()} <b> Do not call this method </b> unless you know what
	 * you do!
	 * 
	 * <hr>
	 * <i>SYNCHRONIZED | THREAD SAFE </i>
	 * <hr>
	 */
	synchronized private void waitForUserReply() {
		try {
			while (!wait)
				wait();
			wait = false;
		} catch (InterruptedException e) {
			return;
		}
	}

	/**
	 * This method unblocks the interpreter and enables it to execute the next
	 * step.
	 * 
	 * <hr>
	 * <i>SYNCHRONIZED | THREAD SAFE </i>
	 * <hr>
	 */
	synchronized private void userReply() {
		wait = true;
		notify();
	}

	/* --- debugger panel listeners --- */

	/**
	 * Mouse listener for the "play/pause" button
	 */
	public MouseListener playButtonHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			
			JButton button = (JButton)e.getSource();
			if( !button.isEnabled() )
				return;

			// Ready -> Start interpreting in run mode
			if (!keepRunning && !run) {
				if( master.runInterpreter() )
					setRunMode();
			}

			// Run -> pause interpreting
			else if (keepRunning && run) {
				setPauseMode();

				if (timer != null)
					timer.cancel();
			}

			// Pause -> re-run interpreting
			else if (keepRunning && !run) {
				setRunMode();
				userReply();
				
				//Remove already passed breakpoints if in fast run mode
				if( delay == 0 )
					master.updateBreakPoints(lastNode.line);
				//Check if there is a function to jump over
				else
					doStepOverRunCheck(lastNode);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	};

	/**
	 * Mouse listener for the "next step" button
	 */
	public MouseListener stepButtonHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			
			JButton button = (JButton)e.getSource();
			if( !button.isEnabled() )
				return;
			
			DebugShell.out(State.LOG, Area.DEBUGGER, "Step: keep = " + keepRunning + ", run = " + run);

			// Ready mode -> start interpreting in pause mode
			if (isReadyMode()) {
				if( master.runInterpreter() )
					setPauseMode();
			}

			// Pause mode -> next step
			else if (isPauseMode()){
				userReply();
			}

			// Error mode -> step button has "view" function
			else if (isErrorMode()) {
				modMain.highlightSourceCode(master.getErrorLine());
				DebugShell.out(State.LOG, Area.DEBUGGER, "Highlighting: " + master.getErrorLine() + ", " + master.getCompleteErrorLine() );
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	};

	/**
	 * Mouse listener for the "step over" button
	 */
	public MouseListener stepOverButtonHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			
			JButton button = (JButton)e.getSource();
			if( !button.isEnabled() )
				return;

			master.stepOver();
			userReply();
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	};

	/**
	 * Mouse listener for the "step out" button
	 */
	public MouseListener stepOutButtonHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			
			JButton button = (JButton)e.getSource();
			if( !button.isEnabled() )
				return;

			master.stepOut();
			userReply();
		}

		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	};

	/**
	 * Mouse listener for the "stop" button
	 */
	public MouseListener stopButtonHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			
			JButton button = (JButton)e.getSource();
			if( !button.isEnabled() )
				return;

			// Stop interpreter out of RUN or PAUSE mode
			if (keepRunning) {
				setReadyMode();
				userReply();
			}

			// Get out of ERROR mode
			else if (!keepRunning && run) {
				master.getControlPanel().unsetRunTimeError();
				setReadyMode();

				// Has to be done here, as interpreter has exited in error mode
				modMain.unlockInput();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	};

	/**
	 * Mouse listener for the run mode speed slider
	 */
	public ChangeListener sliderListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {

			delay = master.getControlPanel().getInterpreterSpeedSlider() - 1;
			master.getControlPanel().setTimerLabelSeconds(delayScale(delay));
			
			if( delay == 0 && lastNode != null ){
				master.updateBreakPoints(lastNode.line);
			}
		}
	};
	
	private static double delayScale( int slider ){
		switch( slider ){
		case 1: return 0.05;
		case 2: return 0.1;
		case 3: return 0.2;
		case 4: return 0.5;
		case 5: return 0.75;
		case 6: return 1;
		case 7: return 1.5;
		case 8: return 2;
		case 9: return 3;
		default: return 0;
		}
	}

}
