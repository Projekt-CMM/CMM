package at.jku.ssw.cmm.quest.importexport;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import at.jku.ssw.cmm.gui.file.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;

public class ExportProfile {
	public static void Export(JFrame frame, Profile p){
		File destfolder = getPath();
		File destpath = new File(destfolder.getAbsolutePath()+File.separator+p.getName());
		
		for( File f : destfolder.listFiles() ) {
			if( f.getName().equals(p.getName()) ) {
				//Custom button text
				Object[] options = {"Overwrite existing profile",
				                    "Cancel"};
				int n = JOptionPane.showOptionDialog(frame,
				    "There is already a folder named \"" + p.getName() + "\" in this directory",
				    "Warning: Folder already exists",
				    JOptionPane.YES_NO_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    options,
				    options[1]);
				
				if( n == 1 ) {
					System.out.println("Cancelling export");
					return;
				}
				System.out.println("Overwriting...");
			}
		}

		
		//Zip Datei implementieren
		if(destpath != null)
			try {
				LoadStatics.copyFolder(new File(p.getInitPath()), destpath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	private static File getPath(){
		   JFileChooser chooser = new JFileChooser();    
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    
		    int returnVal = chooser.showOpenDialog(chooser);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       return chooser.getSelectedFile();
		    }else{
		    	return null;
		    }
	}
}
