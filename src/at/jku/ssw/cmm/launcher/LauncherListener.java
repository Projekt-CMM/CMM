package at.jku.ssw.cmm.launcher;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.properties.GUImainSettings;
import at.jku.ssw.cmm.profile.Profile;


public class LauncherListener implements MouseListener{
	
	private final GUImainSettings settings;
	private final JFrame jFrame;
	private final Profile profile;
	
	public LauncherListener(GUImainSettings settings, JFrame jFrame, Profile p) {
		this.settings = settings;
		this.jFrame = jFrame;
		this.profile = p;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {

		jFrame.dispose();
		System.out.println("Profile set");
		settings.setProfile(profile);
			
		GUImain app = new GUImain(settings);
		app.start(false);
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
 