package at.jku.ssw.cmm.gui.event.panel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import at.jku.ssw.cmm.debugger.Debugger;
import at.jku.ssw.cmm.gui.GUIrightPanel;
import at.jku.ssw.cmm.gui.exception.InvalidRunModeException;
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
	public PanelRunListener(GUImainMod modMain, GUIrightPanel master) {
		this.modMain = modMain;
		this.master = master;

		this.run = false;
		this.keepRunning = false;
		this.wait = false;

		this.stepOver = false;

		// Default value of the slider
		this.delay = 2;
	}

	// Interface for main GUI manipulations, eg. source code highlighting
	private final GUImainMod modMain;

	// Reference to the right panel wrapper class
	private final GUIrightPanel master;

	// FALSE = pause | TRUE = play
	private boolean run;

	// TRUE -> Interpreter is still running | FALSE -> runtime error or ready
	// See right panel mode definitions (in the documentation)
	private boolean keepRunning;

	// TRUE -> interpreter may carry on | FALSE -> interpreter is waiting for
	// user action
	// Do not set this variable from directly. Use the synchronized methods.
	private boolean wait;

	// TRUE -> interpreter is in step over mode | FALSE -> nothing happens
	private boolean stepOver;

	// The delay between two interpreter steps in run mode
	// time [seconds] = delay/2
	private int delay;

	// Reference to the timer which is scheduling the run mode delay
	// "null" if unused
	private Timer timer;

	// The line where the last runtime error logged occurred
	private int errorLine;

	/* --- functional methods --- */
	/**
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void reset() {
		this.run = false;
		this.keepRunning = false;
		this.wait = false;

		this.stepOver = false;

		this.delay = master.getInterpreterSpeedSlider() - 1;

		master.setTimerLabelSeconds((double) (delay) / 2);

		if (this.timer != null)
			this.timer.cancel();

		this.master.unsetRunTimeError();
		this.master.unsetStepOverButton();

		this.master.lockStopButton();
		this.master.unlockStepButton();
		this.master.setPlay();

		System.out.println("[mode] setting ready by reset");
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

		this.master.unlockStopButton();
		this.master.lockStepButton();
		this.master.setPause();

		System.out.println("[mode] setting run");
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

		this.master.unlockStopButton();
		this.master.unlockStepButton();
		this.master.setPlay();

		System.out.println("[mode] setting pause");
	}

	/**
	 * Sets the right panel to READY mode (see documentation)
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	private void setReadyMode() {
		this.reset();

		this.master.resetInterpreterData();
	}

	/**
	 * Sets the right panel to ERROR mode (see documentation)
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	private void setErrorMode(String message) {
		this.run = true;
		this.keepRunning = false;

		this.master.unlockStepButton();
		this.master.unlockStopButton();

		this.master.setRuntimeErrorMode(message);

		System.out.println("[mode] setting error");
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
		this.master.unsetStepOverButton();
	}

	/* --- debugger interpreter listeners --- */
	@Override
	public void abort(final String message, final Node node) {

		// TODO is this thread safe???
		if (this.timer != null)
			timer.cancel();

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				setErrorMode(message + " in line " + node.line);
			}
		});
	}

	@Override
	public boolean step(final Node arg0) {

		System.out.println("[node] " + arg0);

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
				modMain.highlightSourceCode(arg0.line, 1);
			}
		});

		errorLine = arg0.line;

		/* --- Node #3: Quick mode? --- */
		if (this.isRunMode() && this.delay == 0) {
			this.master.updateCallStackSize();
			return this.keepRunning;
		}

		// -> Node #3 - DEFAULT
		/* --- Update Block --- */
		this.master.selectFunction();
		this.master.globalSelectRoot();

		this.master.updateGlobals();
		this.master.updateCallStack();
		this.master.updateLocals();

		this.master.updateStepOutButton();

		this.timer = null;

		/* --- Node #4: Pause or Run mode --- */
		if (this.isPauseMode()) {
			/* --- Node #5: Create Buttons --- */
			switch (arg0.kind) {
			case Node.ASSIGN:
				// Call with assignment, for example | i = add( i, 1 );
				if (arg0.right.kind == Node.CALL) {
					// Calling a function from include file -> step over by
					// default
					if (arg0.obj.ast != null
							&& arg0.right.obj.ast.line <= this.master
									.getBeginLine())
						this.master.setStepOverButtonAlone();
					// Calling a local function -> show step over button
					else
						this.master.setStepOverButton();
				}
				// Assignment without function call -> reset buttons
				else
					this.master.unsetStepOverButton();
				break;
			case Node.CALL:
				// Calling a function from include file -> step over by default
				if (arg0.obj.ast != null
						&& arg0.obj.ast.line <= this.master.getBeginLine())
					this.master.setStepOverButtonAlone();
				// Calling a local function -> show step over button
				else if (arg0.obj.ast != null)
					this.master.setStepOverButton();
				else
					this.master.unsetStepOverButton();
				break;
			default:
				// No function call possible -> show normal step button
				this.master.unsetStepOverButton();
				break;
			}
		}
		/* --- Node #4: Pause or Run mode --- */
		else if (this.isRunMode() && this.delay > 0) {

			/* --- Node #5: Step over if external function call --- */
			if (arg0.kind == Node.ASSIGN && arg0.right.kind == Node.CALL) {
				if (arg0.obj.ast != null
						&& arg0.right.obj.ast.line <= this.master
								.getBeginLine()) {
					System.out.println("Special call: " + arg0.right.kind
							+ ", line 0" + arg0.right.obj.ast.line);
					this.master.stepOver();
				}
			}
			if (arg0.obj.ast != null && arg0.kind == Node.CALL
					&& arg0.obj.ast.line <= this.master.getBeginLine()) {
				this.master.stepOver();
			}

			this.timer = new Timer();
			// Start timer
			this.timer.schedule(new TimerTask() {
				@Override
				public void run() {
					userReply();
				}
			}, 500 * delay);
		}
		/* --- Node #4: Default exit --- */
		else {
			try {
				throw new InvalidRunModeException();
			} catch (InvalidRunModeException e) {
				e.printStackTrace();
			}
		}

		waitForUserReply();

		this.timer = null;

		return keepRunning;
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

			// Ready -> Start interpreting in run mode
			if (!keepRunning && !run) {
				master.runInterpreter();
				setRunMode();
			}

			// Run -> pause interpreting
			else if (keepRunning && run) {
				setPauseMode();

				if (timer != null)
					timer.cancel();
			}

			// Pause -> run interpreting
			else if (keepRunning && !run) {
				setRunMode();
				userReply();
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

			// Ready mode -> start interpreting in pause mode
			if (!keepRunning && !run) {
				master.runInterpreter();
				setPauseMode();
			}

			// Pause mode -> next step
			else if (keepRunning && !run)
				userReply();

			// Error mode -> step button has "view" function
			else if (!keepRunning && run) {
				modMain.highlightSourceCode(errorLine, 0);
				System.out.println("Highlighting error line..." + errorLine);
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

			master.stepOut();
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
	 * Mouse listener for the "stop" button
	 */
	public MouseListener stopButtonHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {

			// Stop interpreter out of RUN or PAUSE mode
			if (keepRunning) {
				setReadyMode();
				userReply();
			}

			// Get out of ERROR mode
			else if (!keepRunning && run) {
				master.unsetRunTimeError();
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

			delay = master.getInterpreterSpeedSlider() - 1;
			master.setTimerLabelSeconds((double) (delay) / 2);
		}
	};

}
