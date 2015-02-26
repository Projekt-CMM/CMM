package at.jku.ssw.cmm.quest.creator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class QuestCreatorListener {
	
	private QuestCreatorMain main;
	
	public QuestCreatorListener(QuestCreatorMain main) {
		this.main = main;
	}
	
	public ActionListener descriptionListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			File file = getPath(new FileNameExtensionFilter("description.html", "html"));
			if(file != null){
				main.getData().setDescription(file);
				main.getRightPanel().setDescription("\u2714");
				System.out.println("Ref File selected");
			}	
			
			
		}
	};
	
	public ActionListener inputListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			File file = getPath(new FileNameExtensionFilter("Input.cmm", "cmm"));
			if(file != null){
				main.getData().setInput(file);
				main.getRightPanel().setInput("\u2714");
				System.out.println("Input File selected");
			}	
			
		}
	};
	
	public ActionListener refListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			File file = getPath(new FileNameExtensionFilter("Ref.cmm", "cmm"));
			if(file != null ){
				main.getData().setRef(file);
				main.getRightPanel().setReference("\u2714");
				System.out.println("Ref File selected");
			}	
		}
	};
	
	public ActionListener tokenListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Pressed");
			// TODO Auto-generated method stub
			
		}
	};
	
	public ActionListener resourcesListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Pressed");
			// TODO Auto-generated method stub
			
		}
	};
	
	public ActionListener saveListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Pressed");
			Data data = main.getData();
			
		}
	};
	
	private static File getPath(FileNameExtensionFilter filter){
		   JFileChooser chooser = new JFileChooser();
		    /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "ZIP-Files", "zip");*/
		    chooser.setFileFilter(filter);
		    
		    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		    
		    int returnVal = chooser.showOpenDialog(chooser);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       return chooser.getSelectedFile();
		    }else{
		    	return null;
		    }
	}
	
	
}
