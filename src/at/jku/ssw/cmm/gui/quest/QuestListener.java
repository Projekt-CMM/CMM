package at.jku.ssw.cmm.gui.quest;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.quest.GUITestPanel;

public class QuestListener {
	
	private final GUIquestSelection main;
	private final GUITestPanel questPanel;
	
	public QuestListener(GUIquestSelection main, GUITestPanel questPanel){
	this.main = main;
	this.questPanel = questPanel;
	}
	
	public MouseAdapter backToPackagesButton = new MouseAdapter() {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(main != null){
				main.changetoPackagesTable();
				System.out.println("Table updated");
			}
		}
		
	};
	
	public MouseAdapter openQuest = new MouseAdapter() {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(main != null && main.getCurrentQuest() != null){
				main.getGUImain().getSettings().getProfile().setCurrentQuest(main.getCurrentQuest());
				main.getFrame().dispose();
				
				Quest lastClickedQuest = main.getCurrentQuest();
	        	String path = lastClickedQuest.getInitPath() + Quest.sep + lastClickedQuest.getPackagePath() + Quest.sep + lastClickedQuest.getQuestPath();

	        	if(lastClickedQuest.isDescription() && lastClickedQuest.isStyle()){
        			questPanel.setDescDoc(path + Quest.sep + Quest.FILE_DESCRIPTION, path + Quest.sep + Quest.FILE_STYLE);
		        	questPanel.setjQuestTitle(main.getGUImain().getSettings().getProfile().getCurrentQuest().getTitle());
		      
		        	
        		//When the Quest only has a description
		        }else if(lastClickedQuest.isDescription()){
		        	questPanel.setDescDoc(path + Quest.sep + Quest.FILE_DESCRIPTION,"packages/default/style.css");
		        	questPanel.setjQuestTitle(main.getGUImain().getSettings().getProfile().getCurrentQuest().getTitle());		        
        		//TODO
	        	//this.questPanel.setjQuestInfo(LoadStatics.loadHTMLdoc(file, style));
        		}
        	
			
				System.out.println("Quest Selected");
			}
		}
		
	};
}
