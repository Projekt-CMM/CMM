package at.jku.ssw.cmm.gui.event.debug;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.debugger.Debugger;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.debug.GUIcontrolPanel;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.interpreter.Interpreter;
import at.jku.ssw.cmm.interpreter.memory.Memory;
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
	 * @param main
	 *            Interface for main GUI manipulations, eg. source code
	 *            highlighting
	 * @param master
	 *            Reference to the right panel wrapper class
	 */
	public PanelRunListener(GUImain main, GUIdebugPanel master) {
		this.main = main;
		this.master = master;

		this.run = false;
		this.keepRunning = false;
		this.wait = false;

		// Default value of the slider
		this.delay = GUIcontrolPanel.SLIDER_START;
	}

	/**
	 * Interface for main GUI manipulations, eg. source code highlighting
	 */
	private final GUImain main;

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
	
	private int lastAdress;

	/* --- functional methods --- */
	/**
	 * Sets the right panel to READY mode (see documentation)
	 */
	public void setReadyMode(){
		this.run = false;
		this.keepRunning = false;
		this.wait = false;
		
		if( this.timer != null )
			this.timer.cancel();

		this.delay = master.getControlPanel().getInterpreterSpeedSlider() - 1;
		this.lastNode = null;
		
		this.lastAdress = 0;
	}
	
	/**
	 * Sets the right panel to ERROR mode (see documentation)
	 */
	/*public void setErrorMode(){
		this.run = true;
		this.keepRunning = false;
	}*/
	
	/**
	 * Sets the right panel to RUN mode (see documentation)
	 */
	public void setRunMode(){
		this.run = true;
		this.keepRunning = true;
	}
	
	/**
	 * Sets the right panel to PAUSE mode (see documentation)
	 */
	public void setPauseMode(){
		this.run = false;
		this.keepRunning = true;
	}

	/**
	 * @return TRUE if right GUI panel is in RUN mode, otherwise FALSE
	 */
	public boolean isRunMode() {
		if (this.run && this.keepRunning)
			return true;
		return false;
	}

	/**
	 * @return TRUE if right GUI panel is in PAUSE mode, otherwise FALSE
	 */
	public boolean isPauseMode() {
		if (!this.run && this.keepRunning)
			return true;
		return false;
	}

	/**
	 * @return TRUE if right GUI panel is in READY mode, otherwise FALSE
	 */
	public boolean isReadyMode() {
		if (!this.run && !this.keepRunning)
			return true;
		return false;
	}

	/**
	 * @return TRUE if right GUI panel is in ERROR mode, otherwise FALSE
	 */
	public boolean isErrorMode() {
		if (this.run && !this.keepRunning)
			return true;
		return false;
	}

	/* --- debugger interpreter listeners --- */
	@Override
	public boolean step(final Node arg0, List<Integer> readVariables, List<Integer> changedVariables) {
		
		if( this.isReadyMode() || this.isErrorMode() ){
			System.err.println("Interpreter is running although GUI is on editor mode");
			return false;
		}

		DebugShell.out(State.LOG, Area.DEBUGGER, "" + arg0);
		
		//Update latest node's line
		this.lastNode = arg0;

		// -> Node #1 - NO | Node #2 - YES
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				main.getLeftPanel().highlightSourceCode(arg0.line);
			}
		});

		/* --- Node #3: Quick mode? --- */
		if (this.isRunMode() && this.delay == 0) {
			
			//Delay the interpreter for 10ms so that the GUI is still able to work
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				DebugShell.out(State.ERROR, Area.DEBUGGER, "Failed to delay interpreter thread!");
				e.printStackTrace();
			}
			return this.keepRunning;
		}
		
		/* --- Node #5 - Variable value changed --- */
		this.master.updateVariableTables(Memory.getFramePointer() != this.lastAdress);
		this.lastAdress = Memory.getFramePointer();
		
		for( int i : changedVariables )
			this.master.highlightVariable(i);
		
		this.timer = null;

		/* --- Node #6: Pause or Run mode --- */
		if (this.isRunMode() && this.delay > 0) {

			this.timer = new Timer();
			// Start timer
			this.timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if( isRunMode() || isPauseMode() )
						userReply();
				}
			}, (int)(delayScale(delay)*1000) );
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
			runButtonPerformed();
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
	
	public Action F5_run = new AbstractAction() {
		
		private static final long serialVersionUID = 7572186456956931071L;

		public void actionPerformed(ActionEvent e) {
	        runButtonPerformed();
	    }
	};
	
	private void runButtonPerformed(){

		// Ready -> Start interpreting in run mode
		if (isReadyMode()||isErrorMode()) {
			
			master.setRunMode();
			master.runInterpreter();
		}

		// Run -> pause interpreting
		else if (isRunMode()) {
			master.setPauseMode();

			if (timer != null)
				timer.cancel();
		}

		// Pause -> re-run interpreting
		else if (isPauseMode()) {
			master.setRunMode();
			userReply();
			
			//Remove already passed breakpoints if in fast run mode
			if( delay == 0 )
				master.updateBreakPoints(lastNode.line);
		}
	}

	/**
	 * Mouse listener for the "next step" button
	 */
	public MouseListener stepButtonHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			
			stepButtonPerformed();
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
	
	public Action F6_step = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {

	        stepButtonPerformed();
	    }
	};
	
	public void stepButtonPerformed(){
		
		//Not possible in run mode
		if( this.isRunMode() )
			return;

		// Ready mode -> start interpreting in pause mode
		if (isReadyMode()) {
			master.setPauseMode();
			master.runInterpreter();
		}

		// Pause mode -> next step
		else if (isPauseMode()){
			userReply();
		}
	}

	/**
	 * Mouse listener for the "stop" button
	 */
	public MouseListener stopButtonHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			stopButtonPerformed();
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
	
	public Action F7_stop = new AbstractAction() {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
	        stopButtonPerformed();
	    }
	};
	
	public void stopButtonPerformed(){
		
		//This action is not possible in ready mode
		if( this.isReadyMode() )
			return;
		
		// Stop interpreter out of RUN or PAUSE mode
		if (keepRunning) {
			master.setReadyMode();
			//master.getCompileManager().setNotRunning();
			userReply();
		}
	}

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
			}//TODO breakpoints
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
