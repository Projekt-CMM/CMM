package at.jku.ssw.cmm.quest.creator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class NewResourcePanel {

	@SuppressWarnings("unused")
	private final QuestCreatorMain main;
	
	
	private JPanel folderPanel;
	private JPanel filePanel;
	
	public NewResourcePanel(QuestCreatorMain main) {
		this.main = main;
		//listener  = new QuestCreatorListener(main);
		folderPanel();
		filePanel();
	}
	
	public JComponent mainPanel(){
		JPanel mainPanel =  new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		mainPanel.add(folderPanel);
		mainPanel.add(filePanel);
		
		
		
		return new JScrollPane(mainPanel);
	}
	
	private JComponent folderPanel(){
		folderPanel = new JPanel(new BorderLayout());
		
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));	
		subPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder("New Folder")));

		//TODO Add Panels to subPanel
		QuestCreatorStatics.addInputField("Folder Name", new JTextField("",15), subPanel);
		
		JButton addFolderButton = new JButton("Add Folder");
		addFolderButton.addActionListener(addFolderListener);
		QuestCreatorStatics.addToPanel(new JPanel(), addFolderButton, subPanel);
		
		//Panel above
		folderPanel.add(subPanel,BorderLayout.CENTER);
		folderPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		
		return folderPanel;
	}
	
	private JComponent filePanel(){
		filePanel = new JPanel(new BorderLayout());
		
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));	
		subPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder("Select File")));

		//TODO Add Panels to subPanel
		QuestCreatorStatics.addButtonField("Add File", "New File", subPanel, selectFileListener);
		
		//Panel above
		filePanel.add(subPanel,BorderLayout.CENTER);
		filePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		return filePanel;
	}
	
	public ActionListener addFolderListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
				System.out.println("addFolder");
		}
	};
	
	public ActionListener selectFileListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
				File file = QuestCreatorStatics.getPath(new FileNameExtensionFilter("description.html", "html"));
				if(file != null){
					System.out.println("File selected");
				}	
		}
	};

}
