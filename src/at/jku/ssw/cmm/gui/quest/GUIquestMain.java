package at.jku.ssw.cmm.gui.quest;

import java.awt.BorderLayout;
import java.awt.Dimension;

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

public class GUIquestMain {
	
	public static final String QUEST_TREE_ROOT = "root";
	public static final String PACKAGE_DIRECTORY = "packages";
	
	public GUIquestMain(){
		
	}

	private JFrame jFrame;
	
	private JScrollPane editorScrollPane;
	
	public void start(){
		if( SwingUtilities.isEventDispatchThread() )
			System.out.println("[EDT Analyse] Quest GUI runnung on EDT.");

		// Initialize the window
		this.jFrame = new JFrame("C Compact - Quest Manager");
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
		JLabel jLabel1 = new JLabel("Packages and Quests");
		jQuestPane.add(jLabel1, BorderLayout.PAGE_START);
         
        //Quest tree
        final JTree jPackageTree = new JTree(this.initQuestTree());
        jPackageTree.setShowsRootHandles(false);
        jPackageTree.setBorder(new EmptyBorder(5, 5, 5, 5));
        jQuestPane.add(jPackageTree, BorderLayout.CENTER);
        
        jPackageTree.getSelectionModel().addTreeSelectionListener(new QuestTreeListener(this, jPackageTree));
        
        cp.add(jQuestPane, BorderLayout.LINE_START);
        
        //Panel with Quests and Packages
      	JPanel jDescPane = new JPanel();
      	jDescPane.setLayout(new BorderLayout());
      	jDescPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      		
      	//Title
      	JLabel jLabel2 = new JLabel("Description");
      	jDescPane.add(jLabel2, BorderLayout.PAGE_START);
      	
      	//Quest description
      	editorScrollPane = LoadStatics.loadHTMLdoc( PACKAGE_DIRECTORY + "/default.html", PACKAGE_DIRECTORY + "/default.css");
      	editorScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        jDescPane.add(editorScrollPane, BorderLayout.CENTER);

        cp.add(jDescPane, BorderLayout.CENTER);
        
        //Progress Bar
        JProgressBar progressBar;
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(35);
        progressBar.setStringPainted(true);
        progressBar.setString("Total Quest Progress: 35%");
        
        cp.add(progressBar, BorderLayout.PAGE_END);
		
		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}
	
	private DefaultMutableTreeNode initQuestTree(){
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(QUEST_TREE_ROOT);
		
		/*for( String p : Quest.ReadFolderNames(PACKAGE_DIRECTORY) ){
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(p);
			
			for( Quest q : Quest.ReadPackageQuests(PACKAGE_DIRECTORY, p) ){
				node.add(new DefaultMutableTreeNode(q.getTitle()));
			}
			
			root.add(node);
		}*/
		
		return root;
	}
	
	public void setDescriptionText( String html, String css ){
		editorScrollPane = LoadStatics.loadHTMLdoc(html, css);
	}
}
