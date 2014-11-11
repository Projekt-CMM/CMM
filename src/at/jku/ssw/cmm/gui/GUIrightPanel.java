package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.mod.GUImainMod;
import at.jku.ssw.cmm.gui.popup.PopupInterface;

/**
 * This class is responsible for the right panel of the main GUI. The right panel contains
 * a tabbed pane with two tabs:
 * <ul>
 * 		<li>Debugger panel: Contains control buttons for the debugger and the variable tree table.</li>
 * 		<li>Quest/Profile info panel: Contains information about the user and his current quest.</li>
 * </ul>
 * Also, the right panel contains basic control elements for the main GUI, eg. the breakpoint button.
 * 
 * @author fabian
 *
 */
public class GUIrightPanel {
	
	/**
	 * This class is responsible for the right panel of the main GUI. The right panel contains
	 * a tabbed pane with two tabs:
	 * <ul>
	 * 		<li>Debugger panel: Contains control buttons for the debugger and the variable tree table.</li>
	 * 		<li>Quest/Profile info panel: Contains information about the user and his current quest.</li>
	 * </ul>
	 * Also, the right panel contains basic control elements for the main GUI, eg. the breakpoint button.
	 * 
	 * @param cp
	 *            Main component of the main GUI
	 * @param mod
	 *            Interface for main GUI manipulations
	 */
	public GUIrightPanel(JComponent cp, GUImainMod mod, PopupInterface popup) {
		
		//this.jRightContainer.add(this.initCommonPanel(),BorderLayout.PAGE_START);
		
		//Tabbed Pane
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(new EmptyBorder(0, 0, 5, 5));
		
			//Initialize Debug Panel
			JPanel jDebugPanel = new JPanel();
			jDebugPanel.setLayout(new BorderLayout());
			debugPanel = new GUIdebugPanel( jDebugPanel, mod, popup );
	        tabbedPane.add(jDebugPanel, _("Debug"));
	
			//Initialize Quest Panel
			JPanel jQuestPanel = new JPanel();
			if( GUImain.ADVANCED_GUI ){
				jQuestPanel.setLayout(new BorderLayout());
				tabbedPane.add(jQuestPanel, _("Quest"));
				questPanel = new GUIquestPanel( jQuestPanel, mod );
			}
		
		// Add tabbed pane to main panel
		cp.add(tabbedPane, BorderLayout.CENTER);
	}
	
	/**
	 * A reference to the debug panel.
	 */
	private final GUIdebugPanel debugPanel;
	
	/**
	 * A reference to the quest/profile info panel.
	 */
	private GUIquestPanel questPanel;
	
	/**
	 * @return A reference to the debug panel manager
	 */
	public GUIdebugPanel getDebugPanel(){
		return this.debugPanel;
	}
	
	/**
	 * @return A reference to the quest/profile info panel manager
	 */
	public GUIquestPanel getQuestPanel(){
		return this.questPanel;
	}
}
