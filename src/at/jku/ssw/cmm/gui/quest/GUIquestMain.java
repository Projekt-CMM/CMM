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
	
	public GUIquestMain(){
		
	}

	private JFrame jFrame;
	
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
		
		//create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        //create the child nodes
        DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("Vegetables");
        DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Fruits");
 
        //add the child nodes to the root node
        root.add(vegetableNode);
        root.add(fruitNode);
         
        //create the tree by passing in the root node
        JTree jPackageTree = new JTree(root);
        jPackageTree.setBorder(new EmptyBorder(5, 5, 5, 5));
        jQuestPane.add(jPackageTree, BorderLayout.CENTER);
        
        cp.add(jQuestPane, BorderLayout.LINE_START);
        
        //Panel with Quests and Packages
      	JPanel jDescPane = new JPanel();
      	jDescPane.setLayout(new BorderLayout());
      	jDescPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      		
      	//Title
      	JLabel jLabel2 = new JLabel("Description");
      	jDescPane.add(jLabel2, BorderLayout.PAGE_START);
      	
      	//Quest description
      	JScrollPane editorScrollPane = LoadStatics.loadHTMLdoc("profileTest/index.html", "profileTest/doxygen.css");
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
}
