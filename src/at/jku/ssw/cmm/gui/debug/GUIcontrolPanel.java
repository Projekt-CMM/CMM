package at.jku.ssw.cmm.gui.debug;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import at.jku.ssw.cmm.gui.event.debug.PanelRunListener;
import at.jku.ssw.cmm.gui.mod.GUImainMod;

/**
 * This is the small panel with the "play", "pause", etc. buttons of the "debug"
 * tab on the right side of the main GUI.
 * 
 * @author fabian
 *
 */
public class GUIcontrolPanel {

	/**
	 * This is the small panel with the "play", "pause", etc. buttons of the
	 * "debug" tab on the right side of the main GUI.
	 * 
	 * @param panel
	 *            The main panel which contains all the conntrolPanel buttons
	 * @param debug
	 *            A reference to the debug panel which contains this
	 *            controlPanel
	 * @param mod
	 *            A reference to the main GUI modification interface
	 */
	public GUIcontrolPanel(JPanel panel, GUIdebugPanel debug, GUImainMod mod) {

		this.debug = debug;
		this.listener = new PanelRunListener(mod, debug);

		this.initRunMode(panel);
	}

	/**
	 * A reference to the debug panel which contains this controlPanel.
	 */
	private final GUIdebugPanel debug;
	
	/**
	 * Wrapper class with listeners for the "run" mode. Also contains the debug
	 * and I/O stream interface for the interpreter as well as (interpreter)
	 * "run" mode event routines.
	 */
	private final PanelRunListener listener;

	/* --- run mode objects --- */
	/**
	 * This button starts or pauses interpreting.
	 */
	private JButton jButtonPlay;

	/**
	 * This button resumes interpreting for one step.
	 */
	private JButton jButtonStep;

	/**
	 * Used for stepping over a specific function.
	 */
	private JButton jButtonStepOver;

	/**
	 * Used for stepping out of a function.
	 */
	private JButton jButtonStepOut;

	/**
	 * Stops the interpreter. Also used to get out of an error message mode.
	 */
	private JButton jButtonStop;

	/**
	 * Slider for interpreter speed (regulates the period of each interpreter
	 * step in RUN mode).
	 */
	private JSlider jSlider;

	/**
	 * Displays the current interpreter step period length in seconds.
	 */
	private JLabel jLabelTimer;

	public static final boolean ADVANCED_CTRL = false;

	/**
	 * Initializes the objects of the "run" mode, which is active during
	 * interpreting
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT.</i><br>
	 * As this is a initialization method, it should generally not be called
	 * outside the constructor.
	 * </hr>
	 */
	private void initRunMode( JPanel panel ) {

		// Sub-panel with switches and buttons

		/* ---------- PLAY | PAUSE buttons ---------- */
		jButtonPlay = new JButton("\u25B6");
		this.jButtonPlay.addMouseListener(this.listener.playButtonHandler);
		this.jButtonPlay.setToolTipText("<html><b>" + _("compile and run")
				+ "</b><br>" + _("compile source code<br>and run debugger")
				+ "</html>");
		panel.add(jButtonPlay);

		jButtonStep = new JButton("\u25AE\u25B6");
		this.jButtonStep.addMouseListener(this.listener.stepButtonHandler);
		this.jButtonStep.setToolTipText("<html><b>" + _("compile and step")
				+ "</b><br>"
				+ _("compile source code and<br>run debugger step by step")
				+ "</html>");
		panel.add(jButtonStep);

		if (ADVANCED_CTRL) {
			jButtonStepOver = new JButton("\u21B7");
			this.jButtonStepOver
					.addMouseListener(this.listener.stepOverButtonHandler);
			this.jButtonStepOver
					.setToolTipText("<html><b>"
							+ _("step over")
							+ "</b><br>"
							+ _("step over the function<br>which would begin next step")
							+ "</html>");
			panel.add(jButtonStepOver);
			this.jButtonStepOver.setEnabled(false);

			jButtonStepOut = new JButton("\u21B5");
			this.jButtonStepOut
					.addMouseListener(this.listener.stepOutButtonHandler);
			this.jButtonStepOut
					.setToolTipText("<html><b>"
							+ _("step out")
							+ "</b><br>"
							+ _("step out of the current function<br>and return to super function immediately")
							+ "</html>");
			panel.add(jButtonStepOut);
			this.jButtonStepOut.setEnabled(false);
		}

		jButtonStop = new JButton("\u25A0");
		this.jButtonStop.addMouseListener(this.listener.stopButtonHandler);
		this.jButtonStop.setToolTipText("<html><b>" + _("stop") + "</b><br>"
				+ _("return to text edit mode immediately") + "</html>");
		panel.add(jButtonStop);

		/* ---------- SLIDER ---------- */
		jLabelTimer = new JLabel("1.0 sec");
		panel.add(jLabelTimer);

		jSlider = new JSlider(JSlider.HORIZONTAL, 1, 9, 1);
		jSlider.setMajorTickSpacing(1);
		jSlider.setMinorTickSpacing(1);
		jSlider.setPaintTicks(true);
		jSlider.setValue(5);
		jSlider.addChangeListener(this.listener.sliderListener);
		jSlider.setToolTipText("<html><b>" + _("debugger step delay")
				+ "</b><br>" + _("change delay") + "</html>");

		panel.add(jSlider);
		// Sub-panel end

		panel.setPreferredSize(new Dimension(300, 100));
		panel.setMinimumSize(new Dimension(200, 100));
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
	public void setRuntimeErrorMode(String title, String message, int line, int col) {

		if (ADVANCED_CTRL) {
			this.jButtonStepOver.setVisible(false);
			this.jButtonStepOut.setVisible(false);
		}
		
		this.jButtonPlay.setEnabled(false);
		this.jButtonStep.setEnabled(false);
		this.jButtonStop.setEnabled(true);
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


		if (ADVANCED_CTRL) {
			this.jButtonStepOver.setVisible(true);
			this.jButtonStepOut.setVisible(true);
		}
	}

	/**
	 * @return The listener for the control elements which also
	 * implements the Interpreter step request interface.
	 * {@see at.jku.ssw.cmm.Debugger}
	 */
	public PanelRunListener getListener() {
		return this.listener;
	}

	/**
	 * Sets the "play/pause" button in "run" mode to display PAUSE
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void setButtonPause() {
		this.jButtonPlay.setText("\u25AE\u25AE");
		this.jButtonPlay.setToolTipText("<html><b>" + _("pause") + "</b><br>"
				+ _("interrupt automatic debugging") + "</html>");
	}

	/**
	 * Sets the "play/pause" button in "run" mode to display PLAY
	 * 
	 * <hr>
	 * <i>NOT THREAD SAFE, do not call from any other thread than EDT</i>
	 * <hr>
	 */
	public void setButtonPlay() {
		this.jButtonPlay.setText("\u25B6");
		this.jButtonPlay.setToolTipText("<html><b>" + _("run") + "</b><br>"
				+ _("run automatic debugging") + "</html>");
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
		this.jLabelTimer.setText(String.format("%1$.2f %2$s", s, _("sec")));
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
	@SuppressWarnings("unused")
	public void setStepOverButton() {
		if (this.listener.isPauseMode() && ADVANCED_CTRL) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					jButtonStepOver.setEnabled(true);
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
	@SuppressWarnings("unused")
	public void setStepOverButtonAlone() {
		if (this.listener.isPauseMode() && ADVANCED_CTRL) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					jButtonStepOver.setEnabled(true);
					jButtonStep.setEnabled(false);
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
				if (ADVANCED_CTRL)
					jButtonStepOver.setEnabled(false);
				jButtonStep.setEnabled(true);
			}
		});
	}

	public void unsetStepOutButton() {
		if (ADVANCED_CTRL) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					jButtonStepOut.setEnabled(false);
				}
			});
		}
	}

	public void updateStepOutButton() {
		if (ADVANCED_CTRL) {
			if (this.debug.getCallStackSize() > 1) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						jButtonStepOut.setEnabled(true);
					}
				});
			} else {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						jButtonStepOut.setEnabled(false);
					}
				});
			}
		}
	}

	public void standby() {

		this.jButtonPlay.setToolTipText("<html><b>" + _("compile and run")
				+ "</b><br>" + _("compile source code<br>and run debugger")
				+ "</html>");
		this.jButtonStep.setToolTipText("<html><b>" + _("compile and step")
				+ "</b><br>"
				+ _("compile source code and<br>run debugger step by step")
				+ "</html>");
	}

	public void running() {

		this.jButtonStep.setToolTipText("<html><b>" + _("next step")
				+ "</b><br>" + _("proceed to next step") + "</html>");
	}

	public void unlockPlayButton() {
		
		this.jButtonPlay.setEnabled(true);
	}
}
