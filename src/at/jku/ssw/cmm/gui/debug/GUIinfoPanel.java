package at.jku.ssw.cmm.gui.debug;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.gui.mod.GUImainMod;

public class GUIinfoPanel {

	public GUIinfoPanel( JPanel panel, GUImainMod modifier){
		
		this.jVarPanel = new JPanel();
		this.jVarPanel.setBorder(new TitledBorder(_("Variables")));
		this.jVarPanel.setLayout(new BorderLayout());
		
		this.jDescPanel = new JPanel();
		this.jDescPanel.setBorder(new TitledBorder(_("Error Description")));
		this.jDescPanel.setLayout(new BorderLayout());
		
		this.varView = new TreeTableView(modifier, this.jVarPanel,
				modifier.getFileName());
		
		panel.add(jVarPanel, BorderLayout.CENTER);
		panel.add(jDescPanel, BorderLayout.PAGE_END);
		
		this.jDescPanel.setVisible(false);
	}
	
	/**
	 * Panel with variable tree table
	 */
	private JPanel jVarPanel;
	
	private JPanel jDescPanel;
	
	/**
	 * The manager object for the variable tree table.
	 */
	private final TreeTableView varView;
	
	public void setToTable(){
		if( this.jDescPanel.isVisible() ){
			this.jDescPanel.setVisible(false);
			this.jVarPanel.setVisible(true);
		}
	}
	
	public void setToDesc(){
		if( this.jVarPanel.isVisible() ){
			this.jVarPanel.setVisible(false);
			this.jDescPanel.setVisible(true);
		}
	}
	
	public TreeTableView getVarView(){
		return this.varView;
	}
}
