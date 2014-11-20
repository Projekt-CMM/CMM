package at.jku.ssw.cmm.profile.settings;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.XMLWriteException;

/**
 * This class contains all listeners required for the profile settings GUI
 * as nested classes. Basically, this listeners are made for the following
 * purposes:
 * <ul>
 * <li> The user clicks the profile image to change his/her profile image </li>
 * <li> The user clicks the "cancel" button, the window closes without any changes</li>
 * <li> The user clicks the "save" button, the profile is saved, the GUI is closed</li>
 * </ul>
 * 
 * @author fabian
 *
 */
public class ProfileSettingsListener {
	
	/**
	 * This class contains all listeners required for the profile settings GUI
	 * as nested classes. Basically, this listeners are made for the following
	 * purposes:
	 * <ul>
	 * <li> The user clicks the profile image to change his/her profile image </li>
	 * <li> The user clicks the "cancel" button, the window closes without any changes</li>
	 * <li> The user clicks the "save" button, the profile is saved, the GUI is closed</li>
	 * </ul>
	 * 
	 * @param jFrame The main frame of the current window
	 * @param gui A reference to the profile settings GUI manager
	 */
	public ProfileSettingsListener( JFrame jFrame, GUIprofileSettings gui ){
		this.jFrame = jFrame;
		this.gui = gui;
	}
	
	/**
	 * The main frame of the current window
	 */
	private final JFrame jFrame;
	
	/**
	 * A reference to the profile settings GUI manager
	 */
	private final GUIprofileSettings gui;

	/**
	 * Listener for the "cancel" button, located in the <b>lower panel</b>.
	 */
	public MouseListener cancelButtonListener = new MouseListener() {

		@Override
		public void mousePressed(MouseEvent arg0) {
			
			//Close window
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

	/**
	 * Listener for the "save" button, located in the <b>upper panel</b>.
	 */
	public MouseListener saveButtonListener = new MouseListener() {

		@Override
		public void mousePressed(MouseEvent arg0) {
			
			//Actualize profile name (in case user has changed it
			gui.getProfile().setName(gui.getUpperPanel().getProfileName());
			
			//TODO Peda, bitte Profil richtig speichern
			//Es sollte auch das Bild mitgespeichert werden
			try {
				Profile.writeProfile(gui.getProfile());
			} catch (XMLWriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Close window
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
	
	/**
	 * Listener for the profile image. If the iser clicks his profile image,
	 * an image selection GUI shall open.
	 */
	public MouseListener profileImageListener = new MouseListener() {

		@Override
		public void mousePressed(MouseEvent arg0) {
			
			//TODO Peda
			//Open file handler, select image, copy image to profile folder
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
