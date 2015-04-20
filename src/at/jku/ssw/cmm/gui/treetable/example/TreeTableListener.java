package at.jku.ssw.cmm.gui.treetable.example;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import at.jku.ssw.cmm.gui.quest.GUIquestSelection;

public class TreeTableListener {
	
	private String path;
	private GUIquestSelection mainSelection;
	private String name;
	
	public TreeTableListener(String path,GUIquestSelection mainSelection, String name) {
		this.path = path;
		this.mainSelection = mainSelection;
		this.name = name;
	}
	
	public MouseAdapter mouseListener = new MouseAdapter(){

		@Override
		public void mouseClicked(MouseEvent arg0) {
			
			System.out.println("Mouse klicked: " + path);
			if(mainSelection != null){
				mainSelection.setPath(path);
				mainSelection.getGUImain().getRightPanel().getTestPanel().getOpenPackageButton().setEnabled(true);
				mainSelection.getGUImain().getRightPanel().getTestPanel().setTestButtonVisible(true);
				mainSelection.changetoQuestTable();
				System.out.println("Selection repainted!");
				
			}
			
		}

		
		
	};
	
	public String getPath(){
		return path;
	}
	
	public String toString(){
		return name;
	}
}
