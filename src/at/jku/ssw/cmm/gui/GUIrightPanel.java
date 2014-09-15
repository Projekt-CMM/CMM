package at.jku.ssw.cmm.gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.gui.event.RightPanelEventListener;
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
		
		this.listener = new RightPanelEventListener(mod);
		
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
	
	// Main container for the right panel. All interface changes happen inside
	// this JPanel
	private final JPanel jRightContainer;
	
	private final GUIdebugPanel debugPanel;
	
	private final GUIquestPanel questPanel;
	
	private final RightPanelEventListener listener;
	
	/* --- top panel objects --- */
	// Breakpoint button
	private JButton jButtonBreakPoint;
	
	// TODO Add breakpoint functions
	private JPanel initCommonPanel() {
		JPanel jTopPanel = new JPanel();

		this.jButtonBreakPoint = new JButton("\u2326");
		this.jButtonBreakPoint.addMouseListener(this.listener.breakPointHandler);
		jTopPanel.add(this.jButtonBreakPoint);

		return jTopPanel;
	}
	
	public GUIdebugPanel getDebugPanel(){
		return this.debugPanel;
	}
}
