package at.jku.ssw.cmm.gui.debug;

import static at.jku.ssw.cmm.gettext.Language._;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import at.jku.ssw.cmm.gui.event.debug.PanelRunListener;
import at.jku.ssw.cmm.gui.mod.GUImainMod;

/**
 * This is the small panel with the "play", "pause", etc. buttons of the "debug" tab on the
 * right side of the main GUI.
 * 
 * @author fabian
 *
 */
public class GUIcontrolPanel {
	
	public GUIcontrolPanel( JPanel panel, GUIdebugPanel debug, GUImainMod mod ){
		
		this.debug = debug;
		this.panel = panel;
		this.listener = new PanelRunListener(mod, debug);
		
		this.initRunMode();
	}
	
	private JPanel panel;
	
	private final GUIdebugPanel debug;
	
	/* --- run mode objects --- */
	// starts or resumes interpreting
	private JButton jButtonPlay;

	// resumes interpreting for one step
	private JButton jButtonStep;

	private JButton jButtonStepOver;

	private JButton jButtonStepOut;

	// Stops interpreting
	private JButton jButtonStop;

	// Regulates interpreter speed
	private JSlider jSlider;
	private JLabel jLabelTimer;

	// Runtime error labels
	private JLabel jRuntimeErrorLabel1;
	private JLabel jRuntimeErrorLabel2;
	private JLabel jRuntimeErrorLabel3;
	
	// Wrapper class with listeners for the "run" mode. Also contains the debug
	// and I/O stream interface
	// for the interpreter as well as (interpreter) "run" mode event routines.
	private final PanelRunListener listener;
	
	/**
	 * Initializes the objects of the "run" mode, which is active during
	 * interpreting
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT.</i><br>
	 * As this is a initialization method, it should generally not be called
	 * outside the constructor.
	 * </hr>
	 */
	private void initRunMode() {

		// Sub-panel with switches and buttons
		
		/* ---------- PLAY | PAUSE buttons ---------- */
		jButtonPlay = new JButton("\u25B6");
		this.jButtonPlay.addMouseListener(this.listener.playButtonHandler);
		this.panel.add(jButtonPlay);

		jButtonStep = new JButton("\u25AE\u25B6");
		this.jButtonStep.addMouseListener(this.listener.stepButtonHandler);
		this.panel.add(jButtonStep);

		jButtonStepOver = new JButton("\u21B7");
		this.jButtonStepOver
				.addMouseListener(this.listener.stepOverButtonHandler);
		this.panel.add(jButtonStepOver);
		this.jButtonStepOver.setVisible(false);

		jButtonStepOut = new JButton("\u21B5");
		this.jButtonStepOut
				.addMouseListener(this.listener.stepOutButtonHandler);
		this.panel.add(jButtonStepOut);
		this.jButtonStepOut.setVisible(false);

		jButtonStop = new JButton("\u25A0");
		this.jButtonStop.addMouseListener(this.listener.stopButtonHandler);
		this.panel.add(jButtonStop);

		/* --- RUNTIME ERROR LABELS --- */
		this.jRuntimeErrorLabel1 = new JLabel(_("Runtime Error")+":");
		this.panel.add(this.jRuntimeErrorLabel1);
		this.jRuntimeErrorLabel1.setVisible(false);

		this.jRuntimeErrorLabel2 = new JLabel("...");
		this.panel.add(this.jRuntimeErrorLabel2);
		this.jRuntimeErrorLabel2.setVisible(false);
		
		this.jRuntimeErrorLabel3 = new JLabel("...");
		this.panel.add(this.jRuntimeErrorLabel3);
		this.jRuntimeErrorLabel3.setVisible(false);

		/* ---------- SLIDER ---------- */
		jLabelTimer = new JLabel("1.0 sec");
		this.panel.add(jLabelTimer);

		jSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 1);
		jSlider.setMajorTickSpacing(1);
		jSlider.setMinorTickSpacing(1);
		jSlider.setPaintTicks(true);
		jSlider.setValue(3);
		jSlider.addChangeListener(this.listener.sliderListener);
		
		this.panel.add(jSlider);
		// Sub-panel end
	}
	/**
	 * Sets the right panel of the main GUI to the "runtime error" mode, which
	 * is a sub-mode of "run" mode.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 * 
	 * @param message
	 *            The error message from the interpreter
	 */
	public void setRuntimeErrorMode(String title, String message, int line, int col ) {

		// Set standard mode elements invisible
		this.jButtonPlay.setVisible(false);
		this.jButtonStepOver.setVisible(false);
		this.jButtonStepOut.setVisible(false);
		this.jLabelTimer.setVisible(false);
		this.jSlider.setVisible(false);

		// Make error mode elements visible
		this.jRuntimeErrorLabel1.setVisible(true);
		this.jRuntimeErrorLabel1.setText(title);
		this.jRuntimeErrorLabel2.setVisible(true);
		this.jRuntimeErrorLabel2.setText(message);
		this.jRuntimeErrorLabel3.setVisible(true);
		this.jRuntimeErrorLabel3.setText("... in line " + line);

		// Change step button to view button
		this.jButtonStep.setVisible(true);
		this.jButtonStep.setText(_("View"));
	}

	/**
	 * Goes back from "runtime error" mode to "ready" mode. Both are sub-modes
	 * of "run" mode
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void unsetRunTimeError() {

		// Set standard mode elements visible
		this.jButtonPlay.setVisible(true);
		this.jLabelTimer.setVisible(true);
		this.jSlider.setVisible(true);

		// Hide error mode elements
		this.jRuntimeErrorLabel1.setVisible(false);
		this.jRuntimeErrorLabel2.setVisible(false);
		this.jRuntimeErrorLabel3.setVisible(false);

		// Change view button to step button
		this.jButtonStep.setText("\u25AE\u25B6");
	}
	
	public PanelRunListener getListener(){
		return this.listener;
	}
	
	/**
	 * Sets the "play/pause" button in "run" mode to display PAUSE
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void setPause() {
		this.jButtonPlay.setText("\u25AE\u25AE");
	}

	/**
	 * Sets the "play/pause" button in "run" mode to display PLAY
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void setPlay() {
		this.jButtonPlay.setText("\u25B6");
	}

	/**
	 * Locks the "step" button in "run" mode so that it can non be pressed
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void lockStepButton() {
		this.jButtonStep.setEnabled(false);
	}

	/**
	 * Unlocks the "step" button in "run" mode so that it can be used again
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void unlockStepButton() {
		this.jButtonStep.setEnabled(true);
	}

	/**
	 * Locks the "stop" button in "run" mode so that it can not be pressed.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void lockStopButton() {
		this.jButtonStop.setEnabled(false);
	}

	/**
	 * Unlocks the "stop" button in "run" mode so that it can be used again
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void unlockStopButton() {
		this.jButtonStop.setEnabled(true);
	}

	/**
	 * Sets the label for the execution delay in "run" mode to the given value.
	 * Adds "sec" postfix automatically.
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 * 
	 * @param s
	 *            delay time in seconds
	 */
	public void setTimerLabelSeconds(double s) {
		this.jLabelTimer.setText("" + s + " " + _("sec"));
	}

	/**
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 * 
	 * @return The current value of the interpreter speed slider
	 */
	public int getInterpreterSpeedSlider() {
		return this.jSlider.getValue();
	}

	/**
	 * Makes the "step over" button at the top of the right panel in the main
	 * GUI visible.
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 */
	public void setStepOverButton() {
		if (this.listener.isPauseMode()) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					jButtonStepOver.setVisible(true);
				}
			});
		}
	}

	/**
	 * Makes the "step over" button at the top of the right panel in the main
	 * GUI visible and hides the "nest step" button. Used for functions from
	 * include files, as these shall not be visualized step by step.
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 */
	public void setStepOverButtonAlone() {
		if (this.listener.isPauseMode()) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					jButtonStepOver.setVisible(true);
					jButtonStep.setVisible(false);
				}
			});
		}
	}

	/**
	 * Hides the "step over button at the top of the right panel in the main GUI
	 * and makes the "next step" button visible. This is the default state,
	 * displayed for every AST node, except function calls and assignments with
	 * calls.
	 * 
	 * <hr>
	 * <i>THREAD SAFE, asynchronously invoked. </i>Exception may stop EDT!
	 * <hr>
	 */
	public void unsetStepOverButton() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				jButtonStepOver.setVisible(false);
				jButtonStep.setVisible(true);
			}
		});
	}
	
	public void updateStepOutButton() {
		if (this.debug.getCallStackSize() > 1) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					jButtonStepOut.setVisible(true);
				}
			});
		} else {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					jButtonStepOut.setVisible(false);
				}
			});
		}
	}
}
