package at.jku.ssw.cmm.launcher;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.gui.properties.GUImainSettings;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.settings.GUIprofileSettings;

public class EditProfileListener implements MouseListener {

	private final JFrame jFrame;
	private final GUImainSettings settings;
	private final Profile profile;
	
	public EditProfileListener( JFrame jFrame, GUImainSettings settings, Profile profile ) {
		this.jFrame = jFrame;
		this.settings = settings;
		this.profile = profile;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		//Setting the profile to null, for creating a new one
		settings.setProfile(profile);
		
		//Disposing the Launcher
		jFrame.dispose();
		
		
		GUIprofileSettings.init(settings, true);

		
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
