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
 
package at.jku.ssw.cmm.gui.debug;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.event.debug.PanelRunListener;

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
	 * @param main
	 *            A reference to the main GUI mainification interface
	 */
	public GUIcontrolPanel(JPanel panel, GUIdebugPanel debug, GUImain main) {

		this.listener = new PanelRunListener(main, debug);

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
	 * The menu item in the main GUI menubar which does exactly the same
	 * as the "play" button
	 */
	private JMenuItem jMenuItemPlay;

	/**
	 * This button resumes interpreting for one step.
	 */
	private JButton jButtonStep;
	
	/**
	 * The menu item in the main GUI menubar which does exactly the same
	 * as the "step" button
	 */
	private JMenuItem jMenuItemStep;

	/**
	 * Stops the interpreter. Also used to get out of an error message mode.
	 */
	private JButton jButtonStop;
	
	/**
	 * The menu item in the main GUI menubar which does exactly the same
	 * as the "stop" button
	 */
	private JMenuItem jMenuItemStop;

	/**
	 * Slider for interpreter speed (regulates the period of each interpreter
	 * step in RUN mode).
	 */
	private JSlider jSlider;

	/**
	 * Displays the current interpreter step period length in seconds.
	 */
	private JLabel jLabelTimer;
	
	private JCheckBox jReturnBox;
	
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
		
		panel.setToolTipText("<html><b>" + _("Control elements") + "</b><br>" + _("With the elements int this panel, you can<br>run and debug your source code") + "</html>");

		/* ---------- BUTTONS ---------- */
		//"play" button
		jButtonPlay = new JButton("\u25B6");
		this.jButtonPlay.addMouseListener(this.listener.playButtonHandler);
		panel.add(jButtonPlay);

		//"next step" button
		jButtonStep = new JButton("\u25AE\u25B6");
		this.jButtonStep.addMouseListener(this.listener.stepButtonHandler);
		panel.add(jButtonStep);

		//"stop" button
		jButtonStop = new JButton("\u25A0");
		this.jButtonStop.addMouseListener(this.listener.stopButtonHandler);
		this.jButtonStop.setToolTipText("<html><b>" + _("stop") + " (F7)" + "</b><br>"
				+ _("return to text edit mode immediately") + "</html>");
		panel.add(jButtonStop);

		/* ---------- SLIDER ---------- */
		jLabelTimer = new JLabel("0.50 " + _("sec"));
		jLabelTimer.setToolTipText("<html><b>" + _("debugger step delay") + "</b><br>" + _("change this value with<br>the slider below") + "</html>");
		panel.add(jLabelTimer);

		jSlider = new JSlider(JSlider.HORIZONTAL, 1, 9, 1);
		jSlider.setMajorTickSpacing(1);
		jSlider.setMinorTickSpacing(1);
		jSlider.setPaintTicks(true);
		jSlider.setValue(SLIDER_START);
		jSlider.addChangeListener(this.listener.sliderListener);
		jSlider.setToolTipText("<html><b>" + _("debugger step delay")
				+ "</b><br>" + _("change automatic debugging<br>delay with this slider") + "</html>");

		panel.add(jSlider);
		
		/* ---------- CHECKBOX ---------- */
		this.jReturnBox = new JCheckBox(_("Show return values"));
		//TODO this is currently disabled: panel.add(this.jReturnBox);

		panel.setPreferredSize(new Dimension(300, 100));
		panel.setMinimumSize(new Dimension(200, 100));
	}
	
	/**
	 * Sets the control element panel to READY mode
	 * (this is when the user can edit the source code)
	 * <br>
	 * <b> DO NOT CALL THIS METHOD </b>
	 * <br>
	 * If you need to change the mode, call the method setReadymode() in <i>GUIdebugPanel.java</i>
	 */
	public void setReadyMode(){
		this.jButtonPlay.setEnabled(true);
		this.jButtonStep.setEnabled(true);
		this.jButtonStop.setEnabled(false);
		
		this.jMenuItemPlay.setEnabled(true);
		this.jMenuItemStep.setEnabled(true);
		this.jMenuItemStop.setEnabled(false);
		
		this.jButtonPlay.setText("\u25B6");
		
		this.jButtonPlay.setToolTipText("<html><b>" + _("compile and run") + " (F5)" 
				+ "</b><br>" + _("compile source code<br>and run debugger")
				+ "</html>");
		this.jMenuItemPlay.setText(_("compile and run"));
		
		this.jButtonStep.setToolTipText("<html><b>" + _("compile and step") + " (F6)" 
				+ "</b><br>"
				+ _("compile source code and<br>run debugger step by step")
				+ "</html>");
		this.jMenuItemStep.setText(_("compile and step"));
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
		
		this.jMenuItemPlay.setEnabled(false);
		this.jMenuItemStep.setEnabled(false);
		this.jMenuItemStop.setEnabled(true);
	}
	
	
	/**
	 * Sets the control element panel to RUN mode, also called auto-debug mode
	 * (this is when the debugger steps with a defined delay)
	 * <br>
	 * <b> DO NOT CALL THIS METHOD </b>
	 * <br>
	 * If you need to change the mode, call the method setReadymode() in <i>GUIdebugPanel.java</i>
	 */
	public void setRunMode(){
		this.jButtonPlay.setEnabled(true);
		this.jButtonStep.setEnabled(false);
		this.jButtonStop.setEnabled(true);
		
		this.jMenuItemPlay.setEnabled(true);
		this.jMenuItemStep.setEnabled(false);
		this.jMenuItemStop.setEnabled(true);
		
		this.jButtonPlay.setText("\u25AE\u25AE");
		
		this.jButtonPlay.setToolTipText("<html><b>" + _("pause") + " (F5)" 
				+ "</b><br>"
				+ _("pause automatic debugging")
				+ "</html>");
		this.jMenuItemPlay.setText(_("pause"));
		
		this.jButtonStep.setToolTipText("<html><b>" + _("next step") + " (F6)"
				+ "</b><br>" + _("proceed to next step manually")
				+ "</html>");
		this.jMenuItemStep.setText(_("next step"));
	}
	
	/**
	 * Sets the control element panel to PAUSE mode
	 * (in debug mode, auto-debugging paused or step-by-step debugging)
	 * <br>
	 * <b> DO NOT CALL THIS METHOD </b>
	 * <br>
	 * If you need to change the mode, call the method setReadymode() in <i>GUIdebugPanel.java</i>
	 */
	public void setPauseMode(){
		this.jButtonPlay.setEnabled(true);
		this.jButtonStep.setEnabled(true);
		this.jButtonStop.setEnabled(true);
		
		this.jMenuItemPlay.setEnabled(true);
		this.jMenuItemStep.setEnabled(true);
		this.jMenuItemStop.setEnabled(true);
		
		this.jButtonPlay.setText("\u25B6");
		
		this.jButtonPlay.setToolTipText("<html><b>" + _("play") + " (F5)" 
				+ "</b><br>"
				+ _("run automatic debugging")
				+ "</html>");
		this.jMenuItemPlay.setText(_("play"));
		
		this.jButtonStep.setToolTipText("<html><b>" + _("next step") + " (F6)" 
				+ "</b><br>" + _("proceed to next step manually")
				+ "</html>");
		this.jMenuItemStep.setText(_("next step"));
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
	
	/**
	 * Initializes the menu items of the main GUI menubar for debug mode controls
	 * 
	 * @param play Menuitem for "play"
	 * @param step Menuitem for "step"
	 * @param stop Menuitem for "stop"
	 */
	public void initMenuItems( JMenuItem play, JMenuItem step, JMenuItem stop){
		this.jMenuItemPlay = play;
		this.jMenuItemStep = step;
		this.jMenuItemStop = stop;
	}
	
	public boolean showReturnValues() {
		return this.jReturnBox.isSelected();
	}
}
