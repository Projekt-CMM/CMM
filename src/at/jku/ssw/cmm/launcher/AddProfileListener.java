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
 
package at.jku.ssw.cmm.launcher;

import static at.jku.ssw.cmm.gettext.Language._;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.properties.GUImainSettings;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.XMLWriteException;

public class AddProfileListener extends MouseAdapter {

	private final JFrame jFrame;
	private final GUImainSettings settings;
	
	public AddProfileListener( JFrame jFrame, GUImainSettings settings ) {
		this.jFrame = jFrame;
		this.settings = settings;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		//Setting the profile to null, for creating a new one
		Profile p = new Profile();
		
		//Disposing the Launcher
		jFrame.dispose();
		
		//Getting Profile Path
		File filePath = getPath();
		if(filePath == null)
			new GUILauncherMain();

		//Getting name
		String name = JOptionPane.showInputDialog(null,"Geben Sie Ihren Namen ein",
                "Eine Eingabeaufforderung",JOptionPane.PLAIN_MESSAGE);
		p.setName(name);
		
		if(name == null)
			new GUILauncherMain();
	
		//Setting new Initial Path, if no Path exists
		if(p.getInitPath() == null){
			String initPath = filePath.getAbsolutePath() + File.separator + Profile.FILE_BEFORE_PROFILE + p.getName();
			File dir = new File(initPath);
		     
		    // attempt to create the directory here
		    boolean successful = dir.mkdirs();
		    if (!successful){
		    	System.err.println("Profile could not be created at:" + initPath);

		  
				JFrame frame = new JFrame("Warnung");
        		JOptionPane.showMessageDialog(frame,"Profile was already created there","Warning:",
        			    JOptionPane.WARNING_MESSAGE);
		    }
			
		    System.out.println("Setting initial Path:" + initPath);
		    p.setInitPath(initPath);
		    settings.setProfile(p);
		    try {
				settings.getProfile().writeProfile();
			} catch (XMLWriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		    
		}
		
		//Starting CMM with the new Profile
		GUImain app = new GUImain(settings);
		app.start(false);
		
	}
	
	private static File getPath(){
		   JFileChooser chooser = new JFileChooser();
		   chooser.setDialogTitle(_("Please select a directory for your new profile"));
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    
		    int returnVal = chooser.showOpenDialog(chooser);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       return chooser.getSelectedFile();
		    }else{
		    	return null;
		    }
	}

}
