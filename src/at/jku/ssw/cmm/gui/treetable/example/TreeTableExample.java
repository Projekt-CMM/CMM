package at.jku.ssw.cmm.gui.treetable.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.List;

import static at.jku.ssw.cmm.gettext.Language._;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.gui.quest.GUIquestSelection;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;
import at.jku.ssw.cmm.gui.treetable.TreeTableModel;
import at.jku.ssw.cmm.gui.treetable.var.TableButtonRenderer;
import at.jku.ssw.cmm.gui.treetable.var.TreeUtils;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.Package;

public class TreeTableExample {
	
	private final GUIquestSelection main;
	
	public TreeTableExample(GUIquestSelection mainSelection){
		this.main = mainSelection;
	}

	
	private TreeTable<DataNodeExample> treeTable;
	private TreeTableDataModel<DataNodeExample> treeTableModel;
	
	private static final String[] columnNames = { _("Name"), _("Progress"), "\u2692" };
	private static final Class<?>[] columnTypes = { TreeTableModel.class, JProgressBar.class, JButton.class };

	public JPanel getTreePanel(){
		
		//Create the tree
		DataNodeExample root = new DataNodeExample(_("Packages"), "", "");
		
		root = getFolderView("packages", root,0);
		//root = addNodes(null, new File("packages"));
		
		treeTableModel = new TreeTableDataModel<>(root, columnNames, columnTypes);
		
		treeTable = new TreeTable<>(treeTableModel);
		
		treeTable.getCellRenderer().setRootVisible(false);
		
		//Setting the Mouse Listener
		treeTable.addMouseListener(new PackagesTableMouseListener(main, treeTable));
		
		//Setting the Sizes of the last Columns
		treeTable.getColumnModel().getColumn(2).setMinWidth(50);
		treeTable.getColumnModel().getColumn(2).setMaxWidth(100);
		
		//Setting the size of the middle Column
		treeTable.getColumnModel().getColumn(1).setMinWidth(100);
		
		//Setting the size of the First Column
		treeTable.getColumnModel().getColumn(0).setMinWidth(200);
	
		
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
		master.setBorder(new TitledBorder(_("Packages")));
		
		master.add(p, BorderLayout.CENTER);
		master.setMinimumSize(new Dimension(400,0));
		return master;
	}
	
	
private DataNodeExample getFolderView(String path, DataNodeExample node, int layer){	
	List<String> subFolders = Quest.ReadFolderNames(path);
	if(subFolders == null)
		return node; //Maybe return null?
	
	DataNodeExample subNode = null;
	
	for(String subfolder : subFolders){
		
		TreeTableListener tListener = new TreeTableListener(path + File.separator + subfolder,main, subfolder);

		if(isPackage(path + File.separator + subfolder)){
			
			at.jku.ssw.cmm.profile.Package p = Profile.ReadPackageQuests(main.getGUImain().getSettings().getProfile(), path + File.separator + subfolder);
			System.out.println("Checking Package"+path + File.separator + subfolder);
			if(p != null){
				int[] qCount = p.getQuestCount();
				
				JProgressBar b = new JProgressBar(0, qCount[2]);
				b.setStringPainted(true);
				b.setValue(qCount[1]);
				b.setString(qCount[1] + " of " + qCount[2] + " ");
	
				JButton button = new JButton("\u21E8");
				
				button.addMouseListener(tListener.mouseListener);
				
				//Adding the Current Node
				
				subNode = new DataNodeExample(tListener, b, button);
				getFolderView(path + File.separator + subfolder, subNode, ++layer);
				node.addChild(subNode);
			}
			
		}else
			if(!Quest.isPathQuest(path + File.separator + subfolder) && Quest.containsQuests(path + File.separator + subfolder)){
				subNode = new DataNodeExample(tListener, "", "");
					getFolderView(path + File.separator + subfolder, subNode, ++layer);
					node.addChild(subNode);
				
			}
		
		

					
	}
	return node;
}				

private boolean isPackage(String path){

	//Reading all FolderNames
	List<String> subFolders = Quest.ReadFolderNames(path);
	
	//Searching for a Quest
	if(subFolders != null)
		for(String sub : subFolders){
			if(Quest.isPathQuest(path + File.separator + sub)){
				return true;
			}
		}
	
	
	return false;
}

	
}
