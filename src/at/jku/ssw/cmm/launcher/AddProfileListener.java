package at.jku.ssw.cmm.launcher;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.settings.GUIprofileSettings;

public class AddProfileListener implements MouseListener {

	private JFrame jFrame;
	
	public AddProfileListener( JFrame jFrame) {
		this.jFrame = jFrame;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		
		Profile profile = null;
		
		//Disposing the Launcher
		jFrame.dispose();
		
		//Setting the profile to null, for creating a new one
		GUIprofileSettings.init(profile);

		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e){}

}
