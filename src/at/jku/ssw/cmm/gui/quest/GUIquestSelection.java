package at.jku.ssw.cmm.gui.quest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.treetable.example.TreeTableExample;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;

public class GUIquestSelection {
    
	private JFrame frame;
	private JPanel jDescPanel;
	private JPanel jTablePanel;
	private JTable jTable;
	private JSplitPane splitpane;
	
	private Quest currentQuest;
	
	private String path = null;
	private final GUImain main;
	
	private JButton openButton;
	
	private QuestListener listener = new QuestListener(this);
	
	public GUIquestSelection(GUImain main){
		this.main = main;
	}
	
	
	/*public static void main(String args[]){
		GUIquestSelection topic = new GUIquestSelection( null);
		topic.inittable();
		topic.initDescription();
		topic.initFrame();
	}*/
	
	public void init(){
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

		frame.validate();
		frame.repaint();
	}
	
	public void initFrame(){
        frame = new JFrame("Topic");
        frame.setPreferredSize(new Dimension(600, 400));
        frame.add(jTablePanel,BorderLayout.LINE_START);
        frame.add(jDescPanel,BorderLayout.LINE_END);
     
        splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        // Hier setzen wir links unser rotes JPanel und rechts das gelbe
        splitpane.setLeftComponent(jTablePanel);
        splitpane.setRightComponent(jDescPanel);
        splitpane.setResizeWeight(0.5);
        
        frame.add(splitpane,BorderLayout.CENTER);
        
        frame.pack();
        frame.setVisible(true);
	}
	
	public JPanel initDescription(){
		jDescPanel = new JPanel();
		jDescPanel.setLayout(new BorderLayout()); 
		
		 //5-zeiliges und 20-spaltiges Textfeld wird erzeugt
        JTextArea textfeld = new JTextArea();
 
        //Text für das Textfeld wird gesetzt
        textfeld.setText("Lorem ipsum dolor sit amet, " +
        		"consetetur sadipscing elitr, sed diam nonumy " +
        		"eirmod tempor invidunt ut labore et " +
        		"dolore magna aliquyam erat, sed diam voluptua. " +
        		"At vero eos et accusam et justo duo dolores et " +
                        "ea rebum.");
        //Zeilenumbruch wird eingeschaltet
        textfeld.setLineWrap(true);
 
        //Zeilenumbrüche erfolgen nur nach ganzen Wörtern
        textfeld.setWrapStyleWord(true);
 
        //Ein JScrollPane, der das Textfeld beinhaltet, wird erzeugt
        JScrollPane scrollpane = new JScrollPane(textfeld);
        //scrollpane.setPreferredSize(new Dimension(200,200));
		scrollpane.setMinimumSize(new Dimension(200,200));
		
		scrollpane.setBorder(new TitledBorder("Beschreibung"));
 
        //Scrollpane wird unserem Panel hinzugefügt
        jDescPanel.add(scrollpane);
        
        return jDescPanel;
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
		
		System.out.println(questList.getQuestList());
		List<Quest> must = new ArrayList<>();
		List<Quest> extra =  new ArrayList<>();
		
		if(questList != null){
			for(Quest q: questList.getQuestList()){
				if(!q.isOptional()){
					extra.add(q);
				}else{
					must.add(q);
				}
			}
		}

		
		
		//jTable = new JTable(data, columnNames);
		DefaultTableModel model = new DefaultTableModel(); 
	    jTable = new JTable(model);
		
		//Setting the Column Names
		model.addColumn("Must");
		model.addColumn("Extra");
		
		//Adding Data
		int count = 0;

		while(extra.size() > count || must.size() > count){
			Quest extraQuest = new Quest();
			Quest mustQuest = new Quest();
	
			if(extra.size() > count)
				extraQuest = extra.get(count);
			else
				extraQuest.setTitle("");
			if(must.size() > count)
				mustQuest = must.get(count);
			else
				mustQuest.setTitle("");
			
			model.addRow(new Object[]{extraQuest, mustQuest});
			count++;
		}
		
		jTable.setFillsViewportHeight(true);
		jTable.setMinimumSize(new Dimension(200,200));
		jTable.setPreferredSize(new Dimension(200,200));
		
		jTable.setCellSelectionEnabled(true);
	    ListSelectionModel cellSelectionModel = jTable.getSelectionModel();
	    //cellSelectionModelhttp://java-tutorial.org/jsplitpane.html.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	    cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Object obj = null;
				
		        int[] selectedRow = jTable.getSelectedRows();
		        int[] selectedColumns = jTable.getSelectedColumns();

		        for (int i = 0; i < selectedRow.length; i++) {
		          for (int j = 0; j < selectedColumns.length; j++) {
		        	  obj = jTable.getValueAt(selectedRow[i], selectedColumns[j]);
		          }
		        }
		        
		        if(obj instanceof Quest && obj != null){
		        	Quest q = (Quest)obj;
		        	System.out.println("Selected: " + q.getTitle());
		        	setCurrentQuest(q);
		        	
		        	openButton.setEnabled(true);
		        	
		        }
		        else
		        	openButton.setEnabled(false);
			}
		});
		
		scroll = new JScrollPane(jTable);
		//scroll.setPreferredSize(new Dimension(200,200));
		scroll.setMinimumSize(new Dimension(200,200));
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
