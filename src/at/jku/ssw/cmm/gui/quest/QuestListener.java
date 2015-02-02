package at.jku.ssw.cmm.gui.quest;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class QuestListener {
	
	private final GUIquestSelection main;
	
	public QuestListener(GUIquestSelection main){
	this.main = main;
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
				System.out.println("Quest Selected");
			}
		}
		
	};
}
