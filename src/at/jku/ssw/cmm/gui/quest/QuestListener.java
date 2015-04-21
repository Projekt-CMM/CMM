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
        	//Checks if the quest is locked or not
			if(main != null && main.getCurrentQuest() != null && !main.getCurrentQuest().getState().equals(Quest.STATE_LOCKED)){
				main.getGUImain().getSettings().getProfile().setCurrentQuest(main.getCurrentQuest());
				main.getGUImain().getRightPanel().getTestPanel().getTestButton().setEnabled(true);
				main.getGUImain().getRightPanel().getTestPanel().reset();
				main.getFrame().dispose();
				
				Quest lastClickedQuest = main.getCurrentQuest();

	        	//Opening new Quest File
	        	if(lastClickedQuest != null){
	        		File file;
	        		if(lastClickedQuest != null && lastClickedQuest.getCmmFilePath() != null){
	        			file = new File(lastClickedQuest.getCmmFilePath());
	        			//String path = lastClickedQuest.getInitPath() + Quest.sep + lastClickedQuest.getPackagePath() + Quest.sep + lastClickedQuest.getQuestPath();

		        		if(file.exists()){
		        			//Saving the Current File
			        		main.getGUImain().getSaveManager().safeCheck(_("Opening new file"));
			        		
			        		//Opening the Last File
			        		new MenuBarEventListener(null,main.getGUImain()).openFile(file);
		        		}
	        		
	        		}else if( lastClickedQuest.isDefaultCmm()){
	        			String path =  lastClickedQuest.getInitPath() + File.separator + lastClickedQuest.getPackagePath() + File.separator +
	        							lastClickedQuest.getQuestPath() + File.separator + Quest.FILE_DEFAULT;
	        			file = new File(path);
	        					
	        			//Saving the Current File
	        			new MenuBarEventListener(null, main.getGUImain()).newFile(file);
	        		}
	        	
	        		//changing the opened file (in the profile)
	        		Profile.UpdateOpen(main.getGUImain().getSettings().getProfile(), lastClickedQuest);
	        	}
	        	
	        	//Updating the QuestPanelDescription
	        	main.getGUImain().getSettings().updateDescPane(questPanel);
        	
			
				System.out.println("Quest Selected");
			}
		}
		
	};
}
