/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
package at.jku.ssw.cmm.gui.quest;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.XMLWriteException;



public class GUIProfileManager {	
	
	
	/**
	 * Creating new Profile
	 * 1. choose the name
	 * 2. choose the distribution
	 * 3. creating folder and creating profile.xml
	 */
	public static void createNewProfile(){
		
		JFrame chooseFrame = new JFrame("Name Chooser:");
		
		String input = (String)JOptionPane.showInputDialog(
                chooseFrame,
                "Bitte wahle einen Namen:", "Name Chooser", JOptionPane.PLAIN_MESSAGE,null,null,"Name");
		
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
		        		
				        return;
				    }
				    
				    	Profile profile = new Profile();
				    	profile.setInitPath(folderPath);
				    	
				    	
				    	//TODO change to Throws
				    	try {
							Profile.writeProfile(profile);
							System.out.println("Profile created!");
						} catch (XMLWriteException e) {
							e.printStackTrace();
						}
					}

				
		}else
			//TODO Exceptions
			return;
	}

}
