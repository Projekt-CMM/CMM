package at.jku.ssw.cmm.gui.quest;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import at.jku.ssw.cmm.gui.utils.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.Package;

public class GUIquestMain {
	
	public GUIquestMain(){
		
	}

	private JFrame jFrame;
	
	public void start(){
		if( SwingUtilities.isEventDispatchThread() )
			System.out.println("[EDT Analyse] Quest GUI runnung on EDT.");

		// Initialize the window
		this.jFrame = new JFrame(_("C Compact - Quest Manager"));
		this.jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.jFrame.getContentPane().setPreferredSize(new Dimension(600, 640));
		this.jFrame.setMinimumSize(new Dimension(400, 400));
		
		// base class for all swing components, except the top level containers
		JComponent cp = (JComponent) this.jFrame.getContentPane();
		cp.setLayout(new BorderLayout());
		
		//Panel with Quests and Packages
		JPanel jQuestPane = new JPanel();
		jQuestPane.setLayout(new BorderLayout());
		jQuestPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		//Title
		JLabel jLabel1 = new JLabel(_("Packages and Quests"));
		jQuestPane.add(jLabel1, BorderLayout.PAGE_START);
		
		//create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        //Not visible
         
        //create the child nodes
        //DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("Vegetables");
        //DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Fruits"); //TODO
        
	        Profile p = Profile.getActiveProfile();
	        
	        if(p != null){
	        	List<String> s = Quest.ReadFolderNames("packages");
	        	
	        	
	        	for(int i = 0; i < s.size(); i++){
	        		Package package1 = Profile.ReadPackageQuests(p, s.get(i));
	        		
	        		//Main Nodes:
	        		DefaultMutableTreeNode packages = new DefaultMutableTreeNode(package1.getTitle());
	        		
	        		//Only adding Packages which are used..
	        		if(package1 != null && package1.getQuestList() != null)
	        				root.add(packages);
	        		
	        		for(Quest q: package1.getQuestList()){
	        			packages.add(new DefaultMutableTreeNode(q.getTitle()));
	        		}
	        	}
	        }

	        
        //add the child nodes to the root node
        //root.add(vegetableNode);
        //root.add(fruitNode);
         
        //create the tree by passing in the root node
        JTree jPackageTree = new JTree(root);
       // jPackageTree.addMouseListener(l);
        jPackageTree.setBorder(new EmptyBorder(5, 5, 5, 5));
        jPackageTree.setRootVisible(false);
        jQuestPane.add(jPackageTree, BorderLayout.CENTER);
        
        cp.add(jQuestPane, BorderLayout.LINE_START);
        
        //Panel with Quests and Packages
      	JPanel jDescPane = new JPanel();
      	jDescPane.setLayout(new BorderLayout());
      	jDescPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      		
      	//Title
      	JLabel jLabel2 = new JLabel(_("Description"));
      	jDescPane.add(jLabel2, BorderLayout.PAGE_START);
      	
      	JScrollPane editorScrollPane = LoadStatics.loadHTMLdoc("profileTest/index.html", "profileTest/doxygen.css");
      	//Quest description
      	editorScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        jDescPane.add(editorScrollPane, BorderLayout.CENTER);

        cp.add(jDescPane, BorderLayout.CENTER);
        
        //Progress Bar
        JProgressBar progressBar;
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(35);
        progressBar.setStringPainted(true);
        progressBar.setString(_("Total Quest Progress") + ": 35%");
        
        cp.add(progressBar, BorderLayout.PAGE_END);
		
		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}
}
