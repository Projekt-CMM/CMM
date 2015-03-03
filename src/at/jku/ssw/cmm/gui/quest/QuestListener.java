package at.jku.ssw.cmm.gui.quest;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import at.jku.ssw.cmm.gui.event.MenuBarEventListener;
import at.jku.ssw.cmm.profile.Profile;
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

	        	//Opening new Quest File
	        	if(lastClickedQuest != null){
	        		if(lastClickedQuest.getCmmFilePath() != null){
	        		File file = new File(lastClickedQuest.getCmmFilePath());
	        		if(file.exists()){
	        			//Saving the Current File
		        		main.getGUImain().getSaveManager().safeCheck(_("Opening new file"));
		        		
		        		//Opening the Last File
		        		new MenuBarEventListener(null,main.getGUImain()).openFile(file);
	        		}
	        	}
	        		//changing the file to be opened
	        		Profile.UpdateOpen(main.getGUImain().getSettings().getProfile(), lastClickedQuest);
	        	}
	        	
	        	//Updating the QuestPanelDescription
	        	main.getGUImain().getSettings().updateDescPane(questPanel);
        	
			
				System.out.println("Quest Selected");
			}
		}
		
	};
}
