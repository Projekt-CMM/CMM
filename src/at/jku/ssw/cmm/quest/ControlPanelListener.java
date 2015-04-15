package at.jku.ssw.cmm.quest;

import java.awt.event.MouseAdapter;

import at.jku.ssw.cmm.gui.GUImain;

public class ControlPanelListener {
	
	private final GUImain main;
	
	public ControlPanelListener(GUImain main){
		this.main = main;
	}
	
	public MouseAdapter openAllPackageListener = new MouseAdapter(){
		@Override
		public void mouseClicked(java.awt.event.MouseEvent e) {
			
			main.startQuestGUI();
			main.getGUIquestSelection().changetoPackagesTable();
		};
	};
	

	public MouseAdapter openPackageListener = new MouseAdapter(){
		@Override
		public void mouseClicked(java.awt.event.MouseEvent e) {

			if((main.getGUIquestSelection() != null && main.getGUIquestSelection().getPath() != null)){
								
				System.out.println(main.getGUIquestSelection().getPath());
				main.startQuestGUI();
				main.getGUIquestSelection().changetoQuestTable();
			}
		};
	};
}
