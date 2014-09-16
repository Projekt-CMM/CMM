package at.jku.ssw.cmm.gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.gui.event.RightPanelBreakpointListener;
import at.jku.ssw.cmm.gui.mod.GUImainMod;

public class GUIrightPanel {
	
	/**
	 * 
	 * @param cp
	 *            Main component of the main GUI
	 * @param mod
	 *            Interface for main GUI manipulations
	 */
	public GUIrightPanel(JComponent cp, GUImainMod mod) {
		
		this.mod = mod;
		
		//Main right panel
		this.jRightContainer = new JPanel();
		this.jRightContainer.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.jRightContainer.setLayout(new BorderLayout());
		
		this.jRightContainer.add(this.initCommonPanel(),
				BorderLayout.PAGE_START);
		
		//Tabbed Pane
		JTabbedPane tabbedPane = new JTabbedPane();
		
			//Debug Panel
			JPanel jDebugPanel = new JPanel();
			jDebugPanel.setLayout(new BorderLayout());
			tabbedPane.add(jDebugPanel, "Debug");
			debugPanel = new GUIdebugPanel( jDebugPanel, mod );
	
			//Quest Panel
			JPanel jQuestPanel = new JPanel();
			jQuestPanel.setLayout(new BorderLayout());
			tabbedPane.add(jQuestPanel, "Quest");
			questPanel = new GUIquestPanel( jQuestPanel, mod );
		
		this.jRightContainer.add(tabbedPane);
		cp.add(this.jRightContainer, BorderLayout.CENTER);
	}
	
	private final GUImainMod mod;
	
	// Main container for the right panel. All interface changes happen inside
	// this JPanel
	private final JPanel jRightContainer;
	
	private final GUIdebugPanel debugPanel;
	
	@SuppressWarnings("unused")
	private final GUIquestPanel questPanel;
	
	/* --- top panel objects --- */
	// Breakpoint button
	private JButton jButtonBreakPoint;
	
	// TODO Add breakpoint functions
	private JPanel initCommonPanel() {
		JPanel jTopPanel = new JPanel();

		this.jButtonBreakPoint = new JButton("\u2326");
		RightPanelBreakpointListener listener = new RightPanelBreakpointListener(this.mod, this.jButtonBreakPoint);
		this.jButtonBreakPoint.addMouseListener(listener);
		jTopPanel.add(this.jButtonBreakPoint);

		return jTopPanel;
	}
	
	public GUIdebugPanel getDebugPanel(){
		return this.debugPanel;
	}
	
	public void lockInput(){
		this.jButtonBreakPoint.setEnabled(false);
	}
	
	public void unlockInput(){
		this.jButtonBreakPoint.setEnabled(true);
	}
}
