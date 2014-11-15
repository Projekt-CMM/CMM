package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.gui.debug.ErrorTable;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.utils.LoadStatics;

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
	public GUIrightPanel(JComponent cp, GUImain main) {
		
		//this.jRightContainer.add(this.initCommonPanel(),BorderLayout.PAGE_START);
		
		//Tabbed Pane
		tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(new EmptyBorder(0, 0, 5, 5));
		
			//Initialize Debug Panel
			JPanel jDebugPanel = new JPanel();
			jDebugPanel.setLayout(new BorderLayout());
			debugPanel = new GUIdebugPanel( jDebugPanel, main );
	        tabbedPane.add(jDebugPanel, _("Debug"));
	        
	        //Initialize error panel
	        this.errorMap = new ErrorTable(GUImain.LANGUAGE);
	
			//Initialize Quest Panel
			JPanel jQuestPanel = new JPanel();
			if( GUImain.ADVANCED_GUI ){
				jQuestPanel.setLayout(new BorderLayout());
				tabbedPane.add(jQuestPanel, _("Quest"));
				questPanel = new GUIquestPanel( jQuestPanel, main );
			}
		
		// Add tabbed pane to main panel
		cp.add(tabbedPane, BorderLayout.CENTER);
	}
	
	private final JTabbedPane tabbedPane;
	
	/**
	 * A reference to the debug panel.
	 */
	private final GUIdebugPanel debugPanel;
	
	private final ErrorTable errorMap;
	
	/**
	 * A reference to the quest/profile info panel.
	 */
	private GUIquestPanel questPanel;
	
	private JPanel errorPanel;
	
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
	
	public void showErrorPanel( String msg ){
		if( this.tabbedPane.getTabCount() == (GUImain.ADVANCED_GUI ? 2 : 1) ){
			errorPanel = new JPanel();
			errorPanel.setLayout(new BorderLayout());
			errorPanel.setBackground(Color.RED);
			errorPanel.add(LoadStatics.loadHTMLdoc(this.errorMap.getErrorHTML(msg), "error/style.css"), BorderLayout.CENTER);
			tabbedPane.add(errorPanel, _("Error"), 1);
			
			tabbedPane.setSelectedIndex(1);
		}
		else{
			errorPanel.removeAll();
			errorPanel.add(LoadStatics.loadHTMLdoc(this.errorMap.getErrorHTML(msg), "error/style.css"), BorderLayout.CENTER);
			tabbedPane.repaint();
			
			tabbedPane.setSelectedIndex(1);
		}
	}
	
	public void hideErrorPanel(){
		if( this.tabbedPane.getTabCount() == (GUImain.ADVANCED_GUI ? 3 : 2) )
			tabbedPane.remove(1);
	}
}
