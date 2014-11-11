package at.jku.ssw.cmm.gui.debug;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.utils.LoadStatics;

public class GUIinfoPanel {

	public GUIinfoPanel( JPanel panel, GUImain main){
		
		this.jVarPanel = new JPanel();
		this.jVarPanel.setBorder(new TitledBorder(_("Variables")));
		this.jVarPanel.setLayout(new BorderLayout());
		
		this.jDescPanel = new JPanel();
		this.jDescPanel.setBorder(new TitledBorder(_("Error Description")));
		this.jDescPanel.setLayout(new BorderLayout());
		
		this.varView = new TreeTableView(main, this.jVarPanel,
				main.getFileName());
		
		panel.add(jVarPanel, BorderLayout.CENTER);
		panel.add(jDescPanel, BorderLayout.PAGE_END);
		
		this.jDescPanel.setVisible(false);
		
		this.errorMap = ReadErrorTable.readErrorTable();
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
	
	private JScrollPane desc;
	
	private final Map<String,String> errorMap;
	
	public void setToTable(){
		if( this.jDescPanel.isVisible() ){
			this.jDescPanel.setVisible(false);
			this.jVarPanel.setVisible(true);
		}
	}
	
	public void setToDesc(String msg){
		if( this.jVarPanel.isVisible() ){
			
			String path = null;
			
			try{
				path = errorMap.get(msg);
			}catch(Exception e){
				System.err.println("Error not found: " + msg);
			}
			
			System.out.println("file is " + path);
			
			if( path == null ){
				System.err.println("Error not found: " + msg);
				try{
					path = errorMap.get("default");
				}catch(Exception e){
					System.err.println("Default error not found");
					return;
				}
			}
			if( path == null ){
				System.err.println("Default error not found");
				return;
			}
			
			System.out.println("opening file " + path);
			
			desc = LoadStatics.loadHTMLdoc(path, "error/style.css");
			this.jDescPanel.add(desc, BorderLayout.CENTER);
			
			this.jVarPanel.setVisible(false);
			this.jDescPanel.setVisible(true);
		}
	}
	
	public TreeTableView getVarView(){
		return this.varView;
	}
}
