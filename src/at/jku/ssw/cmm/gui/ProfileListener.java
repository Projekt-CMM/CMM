package at.jku.ssw.cmm.gui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import at.jku.ssw.cmm.launcher.ProfileCreateException;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.XMLWriteException;
import at.jku.ssw.cmm.profile.settings.ImagePreviewPanel;

public class ProfileListener {
	private ProfilePanel2 main;
	
	public ProfileListener(ProfilePanel2 main) {
		this.main = main;
	}
	
	public FocusAdapter nameFocusListener = new FocusAdapter(){
		
		@Override
		public void focusLost(FocusEvent e) {
			if(main.getJProfileName().getText() != null){
				main.getGUImain().getSettings().getProfile().setName(main.getJProfileName().getText());
				
				try {
					main.getGUImain().getSettings().getProfile().writeProfile();
				} catch (XMLWriteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		
	};
	
	/**
	 * Listener for the profile image. If the User clicks his profile image,
	 * an image selection GUI shall open.
	 */
	public MouseAdapter profileImageListener = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent arg0) {
		
			try {
				File file = chooseProfileImage();
				
				System.out.println(file.getAbsolutePath());
				
				//Changing Profile Pics in the Profile
				try {
					Profile p = main.getGUImain().getSettings().getProfile();
					
					p.changeProfileImage( file.getAbsolutePath());
					main.loadProfilePic();
					//main.refreshProfilePic(p.getInitPath() + File.separator + p.getProfileimage());
					
				} catch (IOException | XMLWriteException e) {
					e.printStackTrace();
				}
				
				
			} catch (ProfileCreateException e) {
				//Nothing happens
			}
		}
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
