package at.jku.ssw.cmm.launcher;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.gui.properties.GUImainSettings;
import at.jku.ssw.cmm.profile.settings.GUIprofileSettings;

public class AddProfileListener implements MouseListener {

	private final JFrame jFrame;
	private final GUImainSettings settings;
	
	public AddProfileListener( JFrame jFrame, GUImainSettings settings ) {
		this.jFrame = jFrame;
		this.settings = settings;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		//Setting the profile to null, for creating a new one
		settings.setProfile(null);
		
		//Disposing the Launcher
		jFrame.dispose();
		
		
		GUIprofileSettings.init(settings, false);

		
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
