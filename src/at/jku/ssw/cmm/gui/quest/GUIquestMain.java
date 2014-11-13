package at.jku.ssw.cmm.gui.quest;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import at.jku.ssw.cmm.gui.GUIquestPanel;
import at.jku.ssw.cmm.gui.utils.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.Package;

public class GUIquestMain implements TreeSelectionListener, ActionListener {
	
	private JTree jPackageTree;
	private JPanel jDescPane;
	private Quest lastClickedQuest;
	
	private JButton openButton;
	
	private JFrame jFrame;
	private JScrollPane editorScrollPane;
	
	private final GUIquestPanel questPanel;
	
	public GUIquestMain(GUIquestPanel questPanel){
		this.questPanel = questPanel;
	}
	
	public void start(){
		if( SwingUtilities.isEventDispatchThread() )
			System.out.println("[EDT Analyse] Quest GUI runnung on EDT.");

		//Only for testing purporses TODO
		//GUIProfileManager.createNewProfile();
		
		this.jFrame = new JFrame("C Compact - Quest Manager");
		this.jFrame.setMinimumSize(new Dimension(500, 400));
		this.jFrame.setLocationRelativeTo(null);
		this.jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.jFrame.setLayout(new BorderLayout());
		
		//Panel
		JPanel jTreePanel = new JPanel(); 
		jTreePanel.setBorder(new TitledBorder("Packages and Quest")); //Space between fields
        jTreePanel.setLayout(new BorderLayout());
        jTreePanel.setMinimumSize(new Dimension(200, 200));
        jTreePanel.setPreferredSize(new Dimension(200, 200));
		
		//Initialize Node
		DefaultMutableTreeNode rootNode = null;
	    DefaultMutableTreeNode packageNode = null;
		
		//create the root node
        rootNode = new DefaultMutableTreeNode("Root");

        //Getting the active Profile
	    Profile p = Profile.getActiveProfile();
	    
	    //Counting Quests..
	    int finishedquests = 0;
	    int allQuests = 0;

	    if(p != null){
	    //Getting all Packages Folder Names
	       List<String> s = Quest.ReadFolderNames("packages");
	       
	        	for(int i = 0; i < s.size(); i++){
	        		Package package1 = Profile.ReadPackageQuests(p, s.get(i));
	        		
	        		if(package1 != null){
	        			//Main Nodes:
	        			packageNode = new DefaultMutableTreeNode(package1);
		        		
		        		//Only adding Packages which are used..
		        				rootNode.add(packageNode);
		        		
		        		for(Quest q: package1.getQuestList()){
		        			packageNode.add(new DefaultMutableTreeNode(q));
		        			
		        			allQuests++;
		        			if(q.getState().equals(Quest.STATE_FINISHED))
		        				finishedquests++;
		        		}
	        			
	        		}
	        	}
	        }
	        
         
        //create the tree by passing in the root node 
        jPackageTree = new JTree(rootNode);
	    JScrollPane scrollTree = new JScrollPane( jPackageTree );
	    
        //jPackageTree.setScrollsOnExpand(true);
        
        
        jPackageTree.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        //Disable Root visibility
        jPackageTree.setRootVisible(false);
        //Setting File Choosing style d
        jPackageTree.putClientProperty("JTree.lineStyle", "Horizontal");
        jTreePanel.add(scrollTree, BorderLayout.CENTER );
        
        jFrame.add(jTreePanel, BorderLayout.WEST);
        
        //Panel with Quests and Packages
      	jDescPane = new JPanel();
      	jDescPane.setLayout(new BorderLayout());
      	jDescPane.setBorder(new TitledBorder("Description"));
      	
      	//Where the tree is initialized:
      	jPackageTree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
      	jPackageTree.addTreeSelectionListener(this);
      	
      	//Load standart html document
      	editorScrollPane = LoadStatics.loadHTMLdoc("packages/default/de.html", "packages/default/style.css");
      	//Quest description
      	editorScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        jDescPane.add(editorScrollPane, BorderLayout.CENTER);

        jFrame.add(jDescPane, BorderLayout.CENTER);
     
        
        //Control Panel Bottom
        JPanel ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new BorderLayout());

        //Progress Bar
        JProgressBar progressBar;
        progressBar = new JProgressBar(0, allQuests);
        progressBar.setValue(finishedquests);
        System.out.println("1." + allQuests + " 2." + finishedquests);
        progressBar.setStringPainted(true);      
        
        //progressBar.setString(_("Total Quest Progress") + finishedquests + " von " + allQuests);
        
        ctrlPanel.add(progressBar, BorderLayout.CENTER);

        
        //Open Button
        openButton = new JButton("Open");
        openButton.addActionListener(this);
        openButton.setEnabled(false);
        
        openButton.setLayout(null);
        openButton.setLocation(0,0);
        ctrlPanel.add(openButton, BorderLayout.LINE_END);
        
        jFrame.add(ctrlPanel, BorderLayout.PAGE_END);
		
		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.pack();
		this.jFrame.setVisible(true);
		
		
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
			//Returns the last path element of the selection.
		//This method is useful only when the selection model allows a single selection.
		    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		                       jPackageTree.getLastSelectedPathComponent();

		    if (node == null)
		    //Nothing is selected.     
		    return;

		    Object nodeInfo = node.getUserObject();
		    
		    //Compatible with Quest
		    if (nodeInfo instanceof Quest) {
			    if (node.isLeaf()) {
			        Quest quest = (Quest)nodeInfo;
			        lastClickedQuest = quest;
			        
			    	if(!lastClickedQuest.getState().equals(Quest.STATE_LOCKED))
			    		openButton.setEnabled(true);
			    	else
			    		openButton.setEnabled(false);
			        
		        	String path = quest.getInitPath() + Quest.sep + quest.getPackagePath() + Quest.sep + quest.getQuestPath();
		        	
		        	//When the Quest has a description and a style
			        if(quest.isDescription() && quest.isStyle()){
			        	displayURL(path + Quest.sep + Quest.FILE_DESCRIPTION, path + Quest.sep + Quest.FILE_STYLE);
			       //When the Quest only has a description
			        }else if(quest.isDescription())
			        	displayURL(path + Quest.sep + Quest.FILE_DESCRIPTION);
			       
			    } else {
			        displayURL("packages/default/de.html"); 
			    }
			    
			//Compatible with Package 
		    }else if(nodeInfo instanceof Package){	  
		    		openButton.setEnabled(false);
		    	
				        Package package1 = (Package)nodeInfo;
			        	String path = package1.getInitPath() + Package.sep + package1.getPackagePath();
				     
			        //The package only has a description	
				    if(package1.isDescription()){
				        	displayURL(path  + Package.sep + Quest.FILE_DESCRIPTION);
				    
				    //Has a stylesheet and description
				    } else if(package1.isDescription() && package1.isStyle())
				    	displayURL(path + Package.sep + Quest.FILE_DESCRIPTION);
				    	else{
				    		displayURL("packages/default/de.html"); 
				    	}
		    }
		
	}

	private void displayURL(String file){
		displayURL(file,"packages/default/style.css");
	}
	
	private void displayURL(String file, String style) {
		jDescPane.remove(editorScrollPane);
		
		editorScrollPane = LoadStatics.loadHTMLdoc(file, style);
      	editorScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        jDescPane.add(editorScrollPane, BorderLayout.CENTER);
        jDescPane.updateUI();
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == this.openButton){
        	
        	if(lastClickedQuest != null && !lastClickedQuest.getState().equals(Quest.STATE_LOCKED)){
        		Quest.currentQuest = lastClickedQuest;
        		
	        	jFrame.dispose();
        		
	        	String path = lastClickedQuest.getInitPath() + Quest.sep + lastClickedQuest.getPackagePath() + Quest.sep + lastClickedQuest.getQuestPath();

	        	if(lastClickedQuest.isDescription() && lastClickedQuest.isStyle()){
        			this.questPanel.setDescDoc(path + Quest.sep + Quest.FILE_DESCRIPTION, path + Quest.sep + Quest.FILE_STYLE);
		      
        		//When the Quest only has a description
		        }else if(lastClickedQuest.isDescription())
		        	this.questPanel.setDescDoc(path + Quest.sep + Quest.FILE_DESCRIPTION,"packages/default/style.css");
        		
        		//TODO
	        	//this.questPanel.setjQuestInfo(LoadStatics.loadHTMLdoc(file, style));
        		}
        	
        	else{
        		JFrame frame = new JFrame("Warnung");
        		JOptionPane.showMessageDialog(frame,
        			    "Bitte wähle eine Quest aus.",
        			    "Keine Quest ausgewählt:",
        			    JOptionPane.WARNING_MESSAGE);
        	}

        		
        
        }
		
	}

}
