package at.jku.ssw.cmm.gui.event.quest;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.GUImain;

public class QuestPanelListener {

	public QuestPanelListener( GUImain main ){
		this.main = main;
	}
	
	//Interface for main GUI manipulations
	private final GUImain main;
	
	public MouseListener profileHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			//main.selectProfile();
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
	};
	
	public MouseListener questHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			main.startQuestGUI();
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
	};
}
