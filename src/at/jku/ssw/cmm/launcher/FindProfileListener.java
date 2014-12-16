package at.jku.ssw.cmm.launcher;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.properties.GUImainSettings;

public class FindProfileListener implements MouseListener{

	private final JFrame jFrame;
	private final GUImainSettings settings;
	

	public FindProfileListener( JFrame jFrame, GUImainSettings settings ) {
		this.jFrame = jFrame;
		this.settings = settings;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		try {
			
			settings.setProfile(GUIProfileManager.selectProfile());	
			
			jFrame.dispose();
			
			GUImain app = new GUImain(settings);
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
