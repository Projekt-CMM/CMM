package at.jku.ssw.cmm.quest.importexport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.sun.crypto.provider.DESParameters;

import at.jku.ssw.cmm.gui.file.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.XMLWriteException;

public class ExportProfile {
	public static void Export(JFrame frame, Profile p){
		File destfolder = getPath();
		if(destfolder == null)
			return;
		
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
				copyProfileQuests(p,destpath);
			} catch (IOException | ClassNotFoundException e) {
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
	
	
	
	private static void copyProfileQuests(Profile profile, File destpath) throws IOException, ClassNotFoundException{		
		Profile p = new Profile();
		p.setName(profile.getName());
		p.setProfileimage(profile.getProfileimage());
		p.setInitPath(destpath.getPath());
		p.setPackagesPath(profile.getPackagesPath());
		
		List<Quest> questList = new ArrayList<>();
		for(Quest q : profile.getProfileQuests()){
			Quest nq = new Quest();
			nq.setCmmFilePath(q.getCmmFilePath());
			nq.setDate(q.getDate());
			nq.setToken(q.getToken());
			nq.setPackagePath(q.getPackagePath());
			nq.setQuestPath(q.getQuestPath());
			nq.setState(q.getState());
			questList.add(nq);
		}
		
		p.setProfileQuests(questList);
		
		for(Quest q: p.getProfileQuests()){
			if(q.getCmmFilePath() != null){
				String dest = destpath.getAbsolutePath() + File.separator + p.getPackagesPath() + File.separator + q.getPackagePath()
						+ /*File.separator + q.getQuestPath() +*/ File.separator;
				//asdf.input.txt
				try {
					if(!new File(q.getCmmFilePath()).exists())
						q.setCmmFilePath(null);
					else{
					
					LoadStatics.copyFileUsingStream(new File(q.getCmmFilePath()), new File(dest + q.getQuestPath() +".cmm"));
					LoadStatics.copyFileUsingStream(new File(q.getCmmFilePath().substring(0, q.getCmmFilePath().lastIndexOf(".cmm")) + ".input.txt"), new File(dest + q.getQuestPath() +".input.txt"));
					q.setCmmFilePath(dest + q.getQuestPath() +".cmm");}
				} catch (IOException e) {
				}
			}
		}
		try {
			p.writeProfile();
		} catch (XMLWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
