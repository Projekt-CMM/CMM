package at.jku.ssw.cmm.profile.settings;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.midi.SysexMessage;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.GUImainSettings;
import at.jku.ssw.cmm.gui.utils.LoadStatics;
import at.jku.ssw.cmm.launcher.GUILauncherMain;
import at.jku.ssw.cmm.launcher.ProfileCreateException;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
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
			
			GUIprofileSettings.dispose(gui.getProfile(), jFrame);
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
			try {		
				if(gui.getProfile() == null)
					gui.setProfile(new Profile());					
				
				if(gui.getUpperPanel().getProfileName() == null || gui.getUpperPanel().getProfileName().equals("")){
	        		JOptionPane.showMessageDialog(new JFrame(),_("Please choose a Profile Name!"),_("Warnung") + ":",
	        			    JOptionPane.WARNING_MESSAGE);
					throw new ProfileCreateException();
				}	
				else
					//New Profile Name
					gui.getProfile().setName(gui.getUpperPanel().getProfileName());
				

				//Setting new Initial Path, if no Path exists
				if(gui.getProfile().getInitPath() == null){
					String initPath = selectPath().getAbsolutePath() + Quest.sep + Profile.FILE_BEFORE_PROFILE + gui.getProfile().getName();
					File dir = new File(initPath);
				     
				    // attempt to create the directory here
				    boolean successful = dir.mkdirs();
				    if (!successful){
				    	System.err.println("Profile could not be created at:" + initPath);

				  
						JFrame frame = new JFrame("Warnung");
		        		JOptionPane.showMessageDialog(frame,"Profile was already created tgere","Warning:",
		        			    JOptionPane.WARNING_MESSAGE);
		        		
				    	throw new ProfileCreateException();
				    }
					
				    System.out.println("Setting initial Path:" + initPath);
				    gui.getProfile().setInitPath(initPath);
				  				    
				}
				
				try {
					String profileImagePath = gui.getUpperPanel().getProfilePic();
					
					System.out.println(gui.getUpperPanel().getProfilePic());
					//Only copying files if the File is not the Profile image
					if(profileImagePath != null && !profileImagePath.equals(Profile.FILE_PROFILEIMAGE)){
					
					//Delete old avatar.<png>
					if(gui.getProfile().getProfileimage() != null){
						Profile p = gui.getProfile();
						
						File file = new File(p.getInitPath() + p.sep + p.getProfileimage());
						file.delete();
					}
						
					//Copying the Profile image into the Profile Folder
					gui.setProfile(Profile.changeProfileImage(gui.getProfile(), profileImagePath));
					
					}
				} catch (IOException e) {
					System.out.println("");
				}
				
				//Saving Profile
				Profile.writeProfile(gui.getProfile());
				
				//Setting as active Profile
				Profile.setActiveProfile(gui.getProfile());
		        
				jFrame.dispose();
				
				GUImain app = new GUImain(new GUImainSettings(gui.getProfile()));
				app.start(false);
		        
				
			} catch (XMLWriteException | ProfileCreateException e) {
				System.out.println("Profile creation aborted");
			}
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
	

	
	private File selectPath() throws ProfileCreateException{
        // JFileChooser-Objekt erstellen
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int rueckgabeWert = chooser.showOpenDialog(null);
        
        if(rueckgabeWert == JFileChooser.APPROVE_OPTION)
        {
             
        	return chooser.getSelectedFile();
                  
        }
        else
        	throw new ProfileCreateException();
	}
	
	/**
	 * Listener for the profile image. If the User clicks his profile image,
	 * an image selection GUI shall open.
	 */
	public MouseListener profileImageListener = new MouseListener() {

		@Override
		public void mousePressed(MouseEvent arg0) {
		
			try {
				File file = chooseProfileImage();
				
				System.out.println(file.getAbsolutePath());
				
				if(gui.getProfile() == null)
					gui.setProfile(new Profile());
										
				gui.getUpperPanel().refreshProfilePic(file.getAbsolutePath());
				
			} catch (ProfileCreateException e) {
				//Nothing happens
			}
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
	
	private File chooseProfileImage() throws ProfileCreateException{
		
		JFileChooser chooser = new JFileChooser();
		ImagePreviewPanel preview = new ImagePreviewPanel();
		chooser.setAccessory(preview);
		chooser.addPropertyChangeListener(preview);
		
		FileFilter imageFilter = new FileNameExtensionFilter(
			    "Image files", ImageIO.getReaderFileSuffixes()); 
		chooser.setFileFilter(imageFilter);
		
		int rueckgabeWert = chooser.showOpenDialog(null);
        
        if(rueckgabeWert == JFileChooser.APPROVE_OPTION)
        {
             
        	return chooser.getSelectedFile();
                  
        }
        else
        	throw new ProfileCreateException();
		
	}
}
