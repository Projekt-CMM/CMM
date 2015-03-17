/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
package at.jku.ssw.cmm.gui.event.debug;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JEditorPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.debugger.Debugger;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.debug.GUIcontrolPanel;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.popup.ComponentPopup;
import at.jku.ssw.cmm.gui.popup.ImagePopup;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.compiler.Node;
import at.jku.ssw.cmm.compiler.Strings;
import at.jku.ssw.cmm.compiler.Struct;

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
	
	/**
	 * The start address of the previous node's function
	 */
	private int lastAdress;

	/* --- functional methods --- */
	/**
	 * Sets the right panel to READY mode (see documentation)
	 */
	public void setReadyMode(){
		
		// Reset mode flags
		this.run = false;
		this.keepRunning = false;
		this.wait = false;
		
		// Reset timer
		if( this.timer != null )
			this.timer.cancel();

		// Read debugger speed slider (useful for initializing
		this.delay = master.getControlPanel().getInterpreterSpeedSlider() - 1;
		this.lastNode = null;
		
		// Reset last address
		this.lastAdress = 0;
	}
	
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

	/* --- debugger interpreter listeners --- */
	@Override
	public boolean step(final Node arg0, List<Integer> readVariables, List<Integer> changedVariables) {
		
		// Check if debugger is in ready mode -> stop interpreter
		if( this.isReadyMode() ){
			DebugShell.out(State.ERROR, Area.DEBUGGER, "Interpreter is running although GUI is in editor mode");
			return false;
		}
		
		// Debugger log
		DebugShell.out(State.LOG, Area.DEBUGGER, "Step: " + arg0);
		
		// Set debugger to pause mode if this is a wait command
		if( !this.isPauseMode() && arg0.kind == Node.WAIT ) {
			this.master.setPauseMode();
		}
		
		//if( arg0.kind == Node.RETURN ) {}
		if( arg0 != null && arg0.kind == Node.CALL && this.master.getControlPanel().showReturnValues() ) {
			String rval = "";
			
			switch(arg0.type.kind){
			case Struct.INT: rval += Memory.getIntReturnValue(); break;
			case Struct.FLOAT: rval += Memory.getFloatReturnValue(); break;
			case Struct.BOOL: rval += Memory.getBoolReturnValue(); break;
			case Struct.STRING: rval += Strings.get(Memory.getIntReturnValue()); break;
			case Struct.CHAR: rval += Memory.getBoolReturnValue(); break;
			}
			
			JEditorPane ep = new JEditorPane();
			ep.setText(rval);
			ep.setMinimumSize(new Dimension(1, 1));
			ep.setPreferredSize(new Dimension(10, 10));
			Rectangle r = null;
			try {
				r = this.main.getLeftPanel().getPositionInSource(arg0.line, arg0.col);
			} catch (BadLocationException e) {
				DebugShell.out(State.WARNING, Area.DEBUGGER, "Bad location when initializing return popup: " + e.getMessage());
			}
			if( r != null ) {
				int px = (int) r.getX() + (int)main.getLeftPanel().getSourcePane().getLocationOnScreen().getX() - (int)main.getJFrame().getLocationOnScreen().getX();
				int py = (int) r.getY() + (int)main.getLeftPanel().getSourcePane().getLocationOnScreen().getY() - (int)main.getJFrame().getLocationOnScreen().getY();
				ComponentPopup.createPopUp(main, ep, (int)(px + 0.5*(arg0.col+3)*main.getSettings().getCodeSize()), py-(int)(main.getSettings().getCodeSize()*0.9), rval.length()>10?130:80, rval.length()>10?70:35, ImagePopup.SOUTH);
			
				// Set debugger to pause mode
				this.master.setPauseMode();
			}
		}
		
		// Update latest node's line
		this.lastNode = arg0;

		/* --- Quick run mode --- */
		if( this.isRunMode() && this.delay == 0 ){
			// Delay the interpreter for 10ms so that the GUI is still able to work
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// Timer delay failed
				DebugShell.out(State.ERROR, Area.DEBUGGER, "Failed to delay interpreter thread!");
				e.printStackTrace();
			}
			return this.keepRunning;
		}
		
		/* --- Source code line update --- */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Highlight the current line in the source code
				main.getLeftPanel().highlightSourceCode(arg0.line);
			}
		});
		
		/* --- Variable table update --- */
		this.master.updateVariableTables(Memory.getFramePointer() != this.lastAdress);
		this.lastAdress = Memory.getFramePointer();
		
		//Highlight the changed variables
		for( int i : changedVariables )
			this.master.highlightVariable(i, true);

		/* --- Initialize auto-debugging delay timer --- */
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

		// Wait until timer runs out or user presses "next step" button
		waitForUserReply();

		this.timer = null;

		return this.keepRunning;
	}
	
	/* --- thread synchronization --- */
	/**
	 * This method blocks the program until another thread invokes the method
	 * {@link userReply()} <b> Do not call this method </b> unless you know what
	 * you do!
	 * 
	 * <br>
	 * <i>SYNCHRONIZED | THREAD SAFE </i>
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
	 * <br>
	 * <i>SYNCHRONIZED | THREAD SAFE </i>
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
	
	/**
	 * Listener for F5 -> play/pause
	 */
	public ActionListener F5_run = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
	        runButtonPerformed();
	    }
	};
	
	private void runButtonPerformed(){

		// Ready -> Start interpreting in run mode
		if (isReadyMode()) {
			
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
	
	public ActionListener F6_step = new ActionListener() {

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
	
	public ActionListener F7_stop = new ActionListener() {

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
