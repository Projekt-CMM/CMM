package at.jku.ssw.cmm.gui.treetable.example;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.jku.ssw.cmm.gui.quest.GUIquestSelection;

public class TreeTableListener {
	
	private String path;
	private GUIquestSelection mainSelection;
	
	public TreeTableListener(String path,GUIquestSelection mainSelection) {
		this.path = path;
		this.mainSelection = mainSelection;
	}
	
	public MouseListener mouseListener = new MouseListener(){

		@Override
		public void mouseClicked(MouseEvent arg0) {
			
			System.out.println("Mouse klicked: " + path);
			if(mainSelection != null)
				mainSelection.setPath(path);
			//TODO close Old frame and open another one
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent arg0) {}

		@Override
		public void mouseReleased(MouseEvent arg0) {}

		
	};
}
