package at.jku.ssw.cmm.gui.event.quest;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.mod.GUImainMod;

public class QuestPanelListener {

	public QuestPanelListener( GUImainMod mod ){
		this.mod = mod;
	}
	
	//Interface for main GUI manipulations
	private final GUImainMod mod;
	
	public MouseListener profileHandler = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			mod.selectProfile();
			
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
			mod.startQuestGUI();
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
