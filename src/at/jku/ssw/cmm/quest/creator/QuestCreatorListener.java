package at.jku.ssw.cmm.quest.creator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.filechooser.FileNameExtensionFilter;

public class QuestCreatorListener {
	
	private NewQuestPanel main;
	
	public QuestCreatorListener(NewQuestPanel main) {
		this.main = main;
	}
	
	public ActionListener descriptionListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			File file = QuestCreatorStatics.getPath(new FileNameExtensionFilter("description.html", "html"));
			if(file != null){
				main.getData().setDescription(file);
				main.setDescription("\u2714");
				System.out.println("Ref File selected");
			}	
			
			
		}
	};
	
	public ActionListener inputListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			File file = QuestCreatorStatics.getPath(new FileNameExtensionFilter("Input.cmm", "cmm"));
			if(file != null){
				main.getData().setInput(file);
				main.setInput("\u2714");
				System.out.println("Input File selected");
			}	
			
		}
	};
	
	public ActionListener refListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			File file = QuestCreatorStatics.getPath(new FileNameExtensionFilter("Ref.cmm", "cmm"));
			if(file != null ){
				main.getData().setRef(file);
				main.setReference("\u2714");
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
			new ResourcesFrame();
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
	
	
}
