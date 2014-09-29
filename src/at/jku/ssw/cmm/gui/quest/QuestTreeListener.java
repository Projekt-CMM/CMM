package at.jku.ssw.cmm.gui.quest;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import at.jku.ssw.cmm.profile.Quest;

public class QuestTreeListener implements TreeSelectionListener{

	public QuestTreeListener( GUIquestMain gui, JTree tree ){
		this.gui = gui;
		this.tree = tree;
	}
	
	private final GUIquestMain gui;
	private final JTree tree;
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		String questName = selectedNode.getUserObject().toString();
		String packagePath = selectedNode.getParent().toString();
		Quest q = Quest.ReadQuest(GUIquestMain.PACKAGE_DIRECTORY, packagePath, questName);
		System.out.println("Quest: " + questName + " : " + packagePath + " -> " + q);
		if( q.isDescription() && q.isStyle() ){
			this.gui.setDescriptionText(
					GUIquestMain.PACKAGE_DIRECTORY + Quest.sep + packagePath + Quest.sep + questName + Quest.sep + Quest.FILE_DESCRIPTION,
					GUIquestMain.PACKAGE_DIRECTORY + Quest.sep + packagePath + Quest.sep + questName + Quest.sep + Quest.FILE_STYLE);
		}
	}
}
