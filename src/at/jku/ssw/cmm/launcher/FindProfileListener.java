package at.jku.ssw.cmm.launcher;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.properties.GUImainSettings;
import at.jku.ssw.cmm.profile.Profile;

public class FindProfileListener implements MouseListener{

private JFrame jFrame;
	

	public FindProfileListener( JFrame jFrame) {
		this.jFrame = jFrame;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		try {

			
			Profile profile;
			
			profile = GUIProfileManager.selectProfile();	
			
			jFrame.dispose();
			
			GUImain app = new GUImain(new GUImainSettings(profile));
			app.start(false);
			
		} catch (ProfileSelectionException e) {
			
		}

		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
