package at.jku.ssw.cmm.launcher;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.quest.GUIProfileManager;
import at.jku.ssw.cmm.profile.Profile;

public class AddProfileListener implements MouseListener {

	private JFrame jFrame;
	
	public AddProfileListener( JFrame jFrame) {
		this.jFrame = jFrame;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		jFrame.dispose();
		
		GUIProfileManager.createNewProfile();
		String[] a = { "" };
		GUImain.main(a);

		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {

		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
				
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}

}
