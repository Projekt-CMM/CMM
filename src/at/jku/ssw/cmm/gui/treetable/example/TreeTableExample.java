package at.jku.ssw.cmm.gui.treetable.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.gui.treetable.TableButtonRenderer;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;
import at.jku.ssw.cmm.gui.treetable.TreeTableModel;
import at.jku.ssw.cmm.profile.Quest;

public class TreeTableExample {

	public static void main(String[] args) {
		new TreeTableExample().init();
	}

	private JFrame jFrame;
	
	private TreeTable<DataNodeExample> treeTable;
	private TreeTableDataModel<DataNodeExample> treeTableModel;
	
	private static final String[] columnNames = { "Name", "Progress", "x" };
	private static final Class<?>[] columnTypes = { TreeTableModel.class, JProgressBar.class, JButton.class };

	public void init() {
		//Initializes the window for the treeTable example
		this.jFrame = new JFrame("TreeTable Example");
		this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.jFrame.getContentPane().setPreferredSize(new Dimension(800, 500));
		this.jFrame.setMinimumSize(new Dimension(600, 400));
		this.jFrame.setLocation(10, 10);
		
		System.out.println("Starting treeTable example");
		
		//Create the tree
		DataNodeExample root = new DataNodeExample("Packages", "", "");
		
		root = getFolderView("packages", root,0);
		//root = addNodes(null, new File("packages"));
		
		treeTableModel = new TreeTableDataModel<>(root, columnNames, columnTypes);
		
		treeTable = new TreeTable<>(treeTableModel);
		//treeTable.getTableHeader().setVisible(true);
		
		//Setting the Sizes of the last Columns
		treeTable.getColumnModel().getColumn(2).setMinWidth(50);
		treeTable.getColumnModel().getColumn(2).setMaxWidth(100);
		
		//Setting the size of the middle Column
		treeTable.getColumnModel().getColumn(1).setMinWidth(100);
	
		
		//Setting the Types of the Columns
        this.treeTable.getColumnModel().getColumn(2).setCellRenderer(
        		new TableButtonRenderer(this.treeTable.getDefaultRenderer(JButton.class))
        );
        this.treeTable.getColumnModel().getColumn(1).setCellRenderer(
        		new TableButtonRenderer(this.treeTable.getDefaultRenderer(JProgressBar.class))
        );
		
		//treeTable.updateTreeModel();
		
		JScrollPane p = new JScrollPane(treeTable);
		//p.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel master = new JPanel();
		master.setLayout(new BorderLayout());
		master.setBorder(new TitledBorder("Tree table"));
		
		master.add(p, BorderLayout.CENTER);
		this.jFrame.add(master);

		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}
	
private DataNodeExample getFolderView(String path, DataNodeExample node, int layer){
	
	
	List<String> subFolders = Quest.ReadFolderNames(path);
	if(subFolders == null)
		return node; //Maybe return null?
	
	DataNodeExample subNode = null;
	
	for(String subfolder : subFolders){
		
		if(isPackage(path + File.separator + subfolder)){
			
			JProgressBar b = new JProgressBar(0, 100);
			JButton button = new JButton("\u21E8");
			button.addMouseListener(new TreeTableListener().mouseListener);
			
			//Adding the Current Node
			
			subNode = new DataNodeExample(subfolder, b, button);
		}else{
			subNode = new DataNodeExample(subfolder, "", "");
		}
		
		subNode = getFolderView(path + File.separator + subfolder, subNode, ++layer);
		//subNode.setQuestFlag(flag);
		if(subNode != null)
		node.addChild(subNode);
					
	}
	return node;
}				

private boolean isPackage(String path){
	
	System.out.println("Checking Package " + path);
	//Reading all FolderNames
	List<String> subFolders = Quest.ReadFolderNames(path);
	
	//Searching for a Quest
	if(subFolders != null)
		for(String sub : subFolders){
			if(isPathQuest(path + File.separator + sub)){
				System.out.println("Package Path" + path + File.separator + sub);
				return true;
			}
		}
	
	
	return false;
}

/**
 * Checks if the Path contains a Quest
 * @param path
 * @return
 */
private boolean isPathQuest(String path){
	
	List<String> fileNames = Quest.ReadFileNames(path);
	
	//Hide Folders which are Quest Folders
	if(fileNames != null && fileNames.contains(Quest.FILE_REF) &&
			fileNames.contains(Quest.FILE_DESCRIPTION) &&
			fileNames.contains(Quest.FILE_INPUT_CMM)){
		
		//Only adding Quest Nodes
		return true;
	}else
	
	return false;
}
	
}
