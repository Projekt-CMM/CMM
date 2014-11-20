package at.jku.ssw.cmm.profile.settings;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.XMLWriteException;

public class ProfileSettingsListener {
	
	public ProfileSettingsListener( JFrame jFrame, GUIprofileSettings gui ){
		this.jFrame = jFrame;
		this.gui = gui;
	}
	
	private final JFrame jFrame;
	private final GUIprofileSettings gui;

	public MouseListener cancelButtonListener = new MouseListener() {

		@Override
		public void mousePressed(MouseEvent arg0) {
			jFrame.dispose();
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {}
		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent arg0) {}
	};

	public MouseListener saveButtonListener = new MouseListener() {

		@Override
		public void mousePressed(MouseEvent arg0) {
			
			gui.getProfile().setName(gui.getUpperPanel().getProfileName());
			
			try {
				Profile.writeProfile(gui.getProfile());
			} catch (XMLWriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jFrame.dispose();
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {}
		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent arg0) {}
	};
	
	public MouseListener profileImageListener = new MouseListener() {

		@Override
		public void mousePressed(MouseEvent arg0) {
			System.out.println("Profile image clicked");
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {}
		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent arg0) {}
	};
}
