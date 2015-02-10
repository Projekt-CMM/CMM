package at.jku.ssw.cmm.gui.quest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.file.LoadStatics;
import at.jku.ssw.cmm.gui.treetable.example.TreeTableExample;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.quest.GUITestPanel;

public class GUIquestSelection {
    
	//private static final JTable treeTable = null;
	private JFrame frame;
	private JPanel jDescPane;
	private JPanel jTablePanel;
	private JTable jTable;
	private JSplitPane splitpane;
	
	private Quest currentQuest;
	
	private JEditorPane editorScrollPane;
	
	private String path = null;
	
	private final GUImain main;
	private final GUITestPanel questPanel;
	
	private JButton openButton;
	
	private QuestListener listener ;
	
	public GUIquestSelection(GUImain main,GUITestPanel questPanel){
		this.main = main;
		this.questPanel = questPanel;
	}
	
	public void init(){
		listener = new QuestListener(this,questPanel);
		if(path != null)
			inittable();
		else
			jTablePanel = new TreeTableExample(this).getTreePanel();
		
		initDescription();
		initFrame();
	}
	
	
	/**
	 * Replacing  Packages JTree with the packages List
	 */
	public void changetoQuestTable(){
		splitpane.remove(jTablePanel);
		splitpane.setLeftComponent(inittable());

		frame.validate();
		frame.repaint();
	}
	
	/**
	 * Replacing the Quests Table with the Packages List
	 */
	public void changetoPackagesTable(){
		splitpane.remove(jTablePanel);
		splitpane.setLeftComponent(new TreeTableExample(this).getTreePanel());

		try {
			this.editorScrollPane.setPage(LoadStatics.getHTMLUrl("packages/default/de.html"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		frame.validate();
		frame.repaint();
	}
	
	public void initFrame(){
        frame = new JFrame("Topic");
        frame.setPreferredSize(new Dimension(800, 400));
        frame.add(jTablePanel,BorderLayout.LINE_START);
        frame.add(jDescPane,BorderLayout.LINE_END);
     
        splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        // Hier setzen wir links unser rotes JPanel und rechts das gelbe
        splitpane.setLeftComponent(jTablePanel);
        splitpane.setRightComponent(jDescPane);
        splitpane.setResizeWeight(0);
        
        frame.add(splitpane,BorderLayout.CENTER);
        
        frame.pack();
        frame.setVisible(true);
	}
	
	public JPanel initDescription(){
        //Panel with Quests and Packages
      	jDescPane = new JPanel();
      	jDescPane.setLayout(new BorderLayout());
      	jDescPane.setBorder(new TitledBorder("Description"));
		
	      //Description Text Panel
  		this.editorScrollPane = new JEditorPane();
  		this.editorScrollPane.setEditable(false);
  		this.editorScrollPane.setContentType("text/html");
  	
  	
	try {
		this.editorScrollPane.setPage(LoadStatics.getHTMLUrl("packages/default/de.html"));
	} catch (IOException e) {
		
		e.printStackTrace();
	}
	this.editorScrollPane.setDocument(LoadStatics.readStyleSheet("packages/default/style.css"));
	
		//Quest description
		editorScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		jDescPane.add(editorScrollPane, BorderLayout.CENTER);
    
    
        return jDescPane;
	}
	
	public JPanel inittable(){
		jTablePanel = new JPanel();
		jTablePanel.setLayout(new BorderLayout()); 
		
		JScrollPane scroll;
		
		//Getting all Quests with and without the Profile
		at.jku.ssw.cmm.profile.Package questList = null;
		
		if(path != null && main.getSettings().getProfile() != null){
			questList = Profile.ReadPackageQuests(main.getSettings().getProfile(), path);
		}
		
		if(questList == null)
			return jTablePanel;
		
		
		//jTable = new JTable(data, columnNames);
		DefaultTableModel model = new DefaultTableModel(){
			private static final long serialVersionUID = -192877460720449116L;

			//Disable Editing
			@Override
	        public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };       
		}; 
		
	    jTable = new JTable(model);
		
		//Setting the Column Names
	    model.addColumn("\u2714");
		model.addColumn("Type");
		model.addColumn("Title");
		
	
		jTable.getColumnModel().getColumn(0).setMaxWidth(20);
		jTable.getColumnModel().getColumn(1).setMinWidth(100);
		jTable.getColumnModel().getColumn(2).setMinWidth(100);
		
		jTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION); 
		
		
		//Adding Data
		List<Quest> qList = questList.getQuestList();
		
		if(qList != null)
		for(Quest q : qList){
			
			String exersice = q.getAttribute();
			String finished;
			
			if(q.getState().equals(Quest.STATE_FINISHED))
				finished = "\u2714";
			else 
				finished = "";
			
			model.addRow(new Object[]{finished,exersice,q});
		}
		
		jTable.setCellSelectionEnabled(true);
	    ListSelectionModel cellSelectionModel = jTable.getSelectionModel();
	    //TODO Select full row
	    
	    cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
	    	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Object obj = null;
				
		        int[] selectedRow = jTable.getSelectedRows();

		        
		        for (int i = 0; i < selectedRow.length; i++) {
		        	  obj = jTable.getValueAt(selectedRow[i], 2);
		        }

		        
		        if(!e.getValueIsAdjusting()){
			        if(obj instanceof Quest && obj != null ){
			        	Quest q = (Quest)obj;
			        	System.out.println("Selected: " + q.getTitle());
			        	setCurrentQuest(q);
			        	
			        	String path = q.getInitPath() + Quest.sep + q.getPackagePath() + Quest.sep + q.getQuestPath();
			        	
			        	 if(q.isDescription() && q.isStyle()){
					        	displayURL(path + Quest.sep + Quest.FILE_DESCRIPTION, path + Quest.sep + Quest.FILE_STYLE);
					      
					        //When the Quest only has a description
					        }else if(q.isDescription())
					        	displayURL(path + Quest.sep + Quest.FILE_DESCRIPTION);
			        	 
			        	openButton.setEnabled(true);
			        	
			        }
			        else{
			        	displayURL("packages/default/de.html"); 
			        	openButton.setEnabled(false);
			        }
				}
			}
		});
		
		scroll = new JScrollPane(jTable);
		//scroll.setPreferredSize(new Dimension(200,200));
		scroll.setMinimumSize(new Dimension(200,200));
		scroll.setPreferredSize(new Dimension(200,200));
		scroll.setBorder(new TitledBorder("Aufgaben"));
		//scroll.add(jTabel);
		
		jTablePanel.add(scroll);
		jTablePanel.add(buttonPanel(questList),BorderLayout.SOUTH);
		jTablePanel.add(topPanel(questList),BorderLayout.NORTH);
		
		return jTablePanel;
	}
	
	private JPanel buttonPanel(at.jku.ssw.cmm.profile.Package p){
		JPanel ctrlPanel = new JPanel(new BorderLayout());
		
		int[] count = p.getQuestCount();
		
		
		JProgressBar progressBar;
        progressBar = new JProgressBar(0, count[2]);
        progressBar.setValue(count[2]);
        System.out.println("1." + count[0] + " 2." + count[2]);
        progressBar.setStringPainted(true);      
        
        //progressBar.setString(_("Total Quest Progress") + finishedquests + " von " + allQuests);
        
        ctrlPanel.add(progressBar, BorderLayout.CENTER);
        
        //Open Button
        openButton = new JButton("Open");
        //openButton.addActionListener(this);
        openButton.setEnabled(false);
        openButton.addMouseListener(listener.openQuest);
        
        openButton.setLayout(null);
        openButton.setLocation(0,0);
        ctrlPanel.add(openButton, BorderLayout.LINE_END);
        
		return ctrlPanel;
	}
	
	private JPanel topPanel(at.jku.ssw.cmm.profile.Package p){
		JPanel topPanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(p.getTitle());
		
		topPanel.add(label, BorderLayout.LINE_END);
		
		JButton button =  new JButton("\u21E6" + " Back to Packages");
		button.addMouseListener(listener.backToPackagesButton);
		topPanel.add(button, BorderLayout.LINE_START);
		
		return topPanel;
	}
	
	
	public void displayURL(String file){
		displayURL(file,"packages/default/style.css");
	}
	
	public void displayURL(String file, String style) {
		jDescPane.remove(editorScrollPane);
		
		
		try {
			if(this.editorScrollPane.getPage() != null && !this.editorScrollPane.getPage().equals(LoadStatics.getHTMLUrl(file))){
				this.editorScrollPane.setPage(LoadStatics.getHTMLUrl(file));
				this.editorScrollPane.setDocument(LoadStatics.readStyleSheet(style));
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		//editorScrollPane = LoadStatics.loadHTMLdoc(file, style);
      	editorScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      	jDescPane.add(editorScrollPane, BorderLayout.CENTER);
      	jDescPane.updateUI();
		
	}
	
	/**
	 * Setting the Quest Path
	 * @param path
	 */
	public void setPath(String path){
		this.path = path;
	}
	
	public GUImain getGUImain(){
		return main;
	}
	
	public JFrame getFrame(){
		return this.frame;
	}
	
	public Quest getCurrentQuest(){
		return currentQuest;
	}
	
	public void setCurrentQuest(Quest currentQuest){
		this.currentQuest = currentQuest;
	}

}
