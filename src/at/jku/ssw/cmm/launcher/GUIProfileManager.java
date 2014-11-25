package at.jku.ssw.cmm.launcher;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.gui.GUIquestPanel;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.XMLReadingException;
import at.jku.ssw.cmm.profile.XMLWriteException;



public class GUIProfileManager {	
	
	
	/**
	 * Creating new Profile
	 * 1. choose the name
	 * 2. choose the distribution
	 * 3. creating folder and creating profile.xml
	 * @throws ProfileCreateException 
	 */
	public static void createNewProfile() throws ProfileCreateException{
		
		JFrame chooseFrame = new JFrame("Name Chooser:");
		
		String input = (String)JOptionPane.showInputDialog(
                chooseFrame,
                "Bitte wähle einen Namen", "Name Chooser", JOptionPane.PLAIN_MESSAGE,null,null,"Name");
		
		//If a string was returned, say so.
		if ((input != null) && (input.length() > 0)) {
				
				//Saving distibution:
				JFrame jFrame = new JFrame();
				
				JFileChooser fileChooser = new JFileChooser();

				fileChooser.setDialogTitle("Wahle einen Ordner zum Speichern aus:");				

				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				 
				int userSelection = fileChooser.showSaveDialog(jFrame);
				
				if (userSelection == JFileChooser.APPROVE_OPTION) {
				   
					File fileToSave = fileChooser.getSelectedFile();
				    
				    //Create Folder and profile.xml
				    System.out.println("Saving Folder: " + fileToSave.getAbsolutePath());
				    
				    String folderPath = fileToSave.getAbsolutePath() + Quest.sep + input;
				    
				    boolean success = (new File(folderPath)).mkdirs();
				    
				    
				    if (!success) {
				    	//TODO Exception !! for looping
				    	
		        		JFrame frame = new JFrame("Warnung");
		        		JOptionPane.showMessageDialog(frame,
		        			    "Hier wurde bereits ein Profil erstellt",
		        			    "Warnung:",
		        			    JOptionPane.WARNING_MESSAGE);
		        		
		    			throw new ProfileCreateException();
				    }
				    
				    	Profile profile = new Profile();
				    	profile.setInitPath(folderPath);
				    	
				    	
				    	//TODO change to Throws
				    	try {
							Profile.writeProfile(profile);
							System.out.println("Profile created!");
						} catch (XMLWriteException e) {
							System.err.println("Profile could not be created");
							throw new ProfileCreateException();
						}
					}

				
		}else
			throw new ProfileCreateException();
	}
	
	/**
	 * TODO Profile Preview
	 * Invokes the profile selection dialog
	 */
	public static void selectProfile() throws ProfileSelectionException{
		DebugShell.out(State.LOG, Area.GUI, "Opening Profile Selection Window...");
		JFileChooser chooser = new JFileChooser("Select a profile...");
		//chooser.setFileFilter(new FileNameExtensionFilter("C Compact Profile", "xml"));
				
		//Only Directorys can be choosen
		//chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		FileFilter filter = new FileNameExtensionFilter("CMM Profile", Profile.FILE_EXTENDSION);
		//chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		
		//TODO
		//chooser.setAccessory(new ProfilePreview(chooser));
		
		//Disable Renaming etc.
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		
		//chooser.showOpenDialog(jFrame);		
	    int ret = chooser.showDialog(null, null);
	    
	    if (ret == JFileChooser.APPROVE_OPTION){ 
			if(chooser.getSelectedFile() != null && chooser.getSelectedFile().getPath() != null ){
				
				String path = chooser.getSelectedFile().getAbsolutePath();
				try {
					if(path.endsWith(Profile.FILE_PROFILE))
						Profile.setActiveProfile(Profile.ReadProfile(path.substring(0,path.indexOf(Profile.FILE_PROFILE))));
					else
						Profile.setActiveProfile(Profile.ReadProfile(path));
					//GUIquestPanel questPanel = this.rightPanelControl.getQuestPanel();
					
					//questPanel.RefreshProfile(Profile.getActiveProfile());
					
				} catch (XMLReadingException | IndexOutOfBoundsException e) {
					e.printStackTrace();
					System.err.println(path + " Wrong Profile Choosen - no Profile found");
	        		
					JFrame frame = new JFrame("Warnung");
	        		JOptionPane.showMessageDialog(frame,"Falsches Profil ausgewaehlt.","Warnung:",
	        			    JOptionPane.WARNING_MESSAGE);
	        		
					//Open the Selection Window again
	        		throw new ProfileSelectionException();
				}
				DebugShell.out(State.LOG, Area.GUI, "Profile Chooser Path:"
						+ chooser.getSelectedFile().getAbsolutePath());
			}
	    }else
				throw new ProfileSelectionException();
	}

}
