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

		this.listener = new PanelRunListener(mod, debug);

		this.initRunMode(panel);
	}
	
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
	
	/**
	 * Start value of the JSlider which controls the automatic debugging speed in run mode
	 */
	public static final int SLIDER_START = 5;

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

		/* ---------- BUTTONS ---------- */
		//"play" button
		jButtonPlay = new JButton("\u25B6");
		this.jButtonPlay.addMouseListener(this.listener.playButtonHandler);
		panel.add(jButtonPlay);

		//"next step" button
		jButtonStep = new JButton("\u25AE\u25B6");
		this.jButtonStep.addMouseListener(this.listener.stepButtonHandler);
		panel.add(jButtonStep);

		//"pause" button
		jButtonStop = new JButton("\u25A0");
		this.jButtonStop.addMouseListener(this.listener.stopButtonHandler);
		this.jButtonStop.setToolTipText("<html><b>" + _("stop") + "</b><br>"
				+ _("return to text edit mode immediately") + "</html>");
		panel.add(jButtonStop);

		/* ---------- SLIDER ---------- */
		jLabelTimer = new JLabel("0.50 sec");
		panel.add(jLabelTimer);

		jSlider = new JSlider(JSlider.HORIZONTAL, 1, 9, 1);
		jSlider.setMajorTickSpacing(1);
		jSlider.setMinorTickSpacing(1);
		jSlider.setPaintTicks(true);
		jSlider.setValue(SLIDER_START);
		jSlider.addChangeListener(this.listener.sliderListener);
		jSlider.setToolTipText("<html><b>" + _("debugger step delay")
				+ "</b><br>" + _("change delay") + "</html>");

		panel.add(jSlider);
		

		panel.setPreferredSize(new Dimension(300, 100));
		panel.setMinimumSize(new Dimension(200, 100));
	}
	
	/**
	 * Sets the control element panel to READY mode
	 * (this is when the user can edit the source code)
	 * <br>
	 * <b> DO NOT CALL THIS METHOD </b>
	 * <br>
	 * If you need to change the mode, call the method setReadyMode() in <i>GUIdebugPanel.java</i>
	 */
	public void setReadyMode(){
		this.jButtonPlay.setEnabled(true);
		this.jButtonStep.setEnabled(true);
		this.jButtonStop.setEnabled(false);
		
		this.jButtonPlay.setText("\u25B6");
		
		this.jButtonStep.setToolTipText("<html><b>" + _("compile and step")
				+ "</b><br>"
				+ _("compile source code and<br>run debugger step by step")
				+ "</html>");
		
		this.jButtonPlay.setToolTipText("<html><b>" + _("compile and run")
				+ "</b><br>" + _("compile source code<br>and run debugger")
				+ "</html>");
	}
	
	/**
	 * Sets the control element panel to ERROR mode
	 * (this is when an error occurred)
	 * <br>
	 * <b> DO NOT CALL THIS METHOD </b>
	 * <br>
	 * If you need to change the mode, call the method setReadyMode() in <i>GUIdebugPanel.java</i>
	 */
	public void setErrorMode(){
		this.jButtonPlay.setEnabled(false);
		this.jButtonStep.setEnabled(false);
		this.jButtonStop.setEnabled(true);
	}
	
	
	/**
	 * Sets the control element panel to RUN mode, also called auto-debug mode
	 * (this is when the debugger steps with a defined delay)
	 * <br>
	 * <b> DO NOT CALL THIS METHOD </b>
	 * <br>
	 * If you need to change the mode, call the method setReadyMode() in <i>GUIdebugPanel.java</i>
	 */
	public void setRunMode(){
		this.jButtonPlay.setEnabled(true);
		this.jButtonStep.setEnabled(false);
		this.jButtonStop.setEnabled(true);
		
		this.jButtonPlay.setText("\u25AE\u25AE");
		
		this.jButtonStep.setToolTipText("<html><b>" + _("pause")
				+ "</b><br>"
				+ _("pause automatic debugging")
				+ "</html>");
		
		this.jButtonPlay.setToolTipText("<html><b>" + _("next step")
				+ "</b><br>" + _("proceed to next step manually")
				+ "</html>");
	}
	
	/**
	 * Sets the control element panel to PAUSE mode
	 * (in debug mode, auto-debugging paused or step-by-step debugging)
	 * <br>
	 * <b> DO NOT CALL THIS METHOD </b>
	 * <br>
	 * If you need to change the mode, call the method setReadyMode() in <i>GUIdebugPanel.java</i>
	 */
	public void setPauseMode(){
		this.jButtonPlay.setEnabled(true);
		this.jButtonStep.setEnabled(true);
		this.jButtonStop.setEnabled(true);
		
		this.jButtonPlay.setText("\u25B6");
		
		this.jButtonStep.setToolTipText("<html><b>" + _("play")
				+ "</b><br>"
				+ _("run automatic debugging")
				+ "</html>");
		
		this.jButtonPlay.setToolTipText("<html><b>" + _("next step")
				+ "</b><br>" + _("proceed to next step manually")
				+ "</html>");
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
	 * Sets the label for the execution delay in "run" mode to the given value.
	 * Adds "sec" postfix automatically.
	 * 
	 * @param s
	 *            delay time in seconds
	 */
	public void setTimerLabelSeconds(double s) {
		this.jLabelTimer.setText(String.format("%1$.2f %2$s", s, _("sec")));
	}

	/**
	 * @return The current value of the interpreter speed slider
	 */
	public int getInterpreterSpeedSlider() {
		return this.jSlider.getValue();
	}
}
