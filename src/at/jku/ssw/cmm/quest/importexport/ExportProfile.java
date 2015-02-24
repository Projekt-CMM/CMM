package at.jku.ssw.cmm.quest.importexport;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import at.jku.ssw.cmm.gui.file.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;

public class ExportProfile {
	public static void Export(Profile p){
		File destpath = getPath();
		
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
