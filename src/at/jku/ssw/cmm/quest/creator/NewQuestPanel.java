package at.jku.ssw.cmm.quest.creator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class NewQuestPanel {
	private final QuestCreatorMain main;
	
	
	private JTextField nameInput = new JTextField("",15);
	private JTextField attributeInput = new JTextField("",15);
	
	private JLabel description = new JLabel(" ");
	private JLabel input = new JLabel(" ");
	private JLabel reference = new JLabel(" ");
	private JLabel token = new JLabel(" ");
	
	private Data data = new Data();
	
	public NewQuestPanel(QuestCreatorMain main) {
		this.main = main;
		listener  = new QuestCreatorListener(this);
	}
	
	private QuestCreatorListener listener;
	
	public JScrollPane questPanel(){
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));	
		QuestCreatorStatics.addInputField("Name",nameInput, subPanel);
		QuestCreatorStatics.addInputField("Attribute",attributeInput, subPanel);
		
		QuestCreatorStatics.addButtonField("Description", "Datei",description, subPanel,listener.descriptionListener);
		QuestCreatorStatics.addButtonField("Input.cmm", "Datei",input, subPanel,listener.inputListener);
		QuestCreatorStatics.addButtonField("Ref.cmm", "Datei",reference, subPanel,listener.refListener);
		QuestCreatorStatics.addButtonField("Token (optional)","Datei",token,  subPanel,listener.tokenListener);
		QuestCreatorStatics.addButtonField("Resource", "Datei", subPanel,listener.resourcesListener);
		
		//Empty Panel
		subPanel.add(new JPanel());
		
		//Save Button
		addSaveButton(subPanel);
		
		subPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder("Quest")));

		mainPanel.add(subPanel,BorderLayout.CENTER);
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		return new JScrollPane(mainPanel);
	}
	 
	//TODO Add Listener..    
    

    
    private void addSaveButton( JPanel container){
    	JPanel subPanel = new JPanel();
    	subPanel.setBackground(Color.GRAY);
    	
    	JButton button = new JButton("Add");
    	button.addActionListener(listener.saveListener);
    	subPanel.add(button);
    	subPanel.setMaximumSize(new Dimension(500,40));
    	
    	container.add(subPanel);
    }
    
	public void setDescription(String description) {
		this.description.setText(description);
	}

	public void setInput(String input) {
		this.input.setText(input);
	}

	public void setReference(String reference) {
		this.reference.setText(reference);
	}

	public void setToken(String token) {
		this.token.setText(token);
	}
	
	public void setData(Data data){
		this.data= data;
	}
	
	public Data getData(){
		
		return data;
	}
	
	
    
}
