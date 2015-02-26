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

public class RightPanel {
	private final QuestCreatorMain main;
	
	
	private JTextField nameInput = new JTextField("",15);
	private JTextField attributeInput = new JTextField("",15);
	
	private JLabel description = new JLabel(" ");
	private JLabel input = new JLabel(" ");
	private JLabel reference = new JLabel(" ");
	private JLabel token = new JLabel(" ");
	
	public RightPanel(QuestCreatorMain main) {
		this.main = main;
		listener  = new QuestCreatorListener(main);
	}
	
	private QuestCreatorListener listener;
	
	public JScrollPane questPanel(){
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));	
		addInputField("Name",nameInput, subPanel);
		addInputField("Attribute",attributeInput, subPanel);
		
		addButtonField("Description", "Datei",description, subPanel,listener.descriptionListener);
		addButtonField("Input.cmm", "Datei",input, subPanel,listener.inputListener);
		addButtonField("Ref.cmm", "Datei",reference, subPanel,listener.refListener);
		addButtonField("Token (optional)","Datei",token,  subPanel,listener.tokenListener);
		addButtonField("Resource", "Datei", subPanel,listener.resourcesListener);
		
		addSaveButton(subPanel);
		
		subPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder("Quest")));

		mainPanel.add(subPanel,BorderLayout.CENTER);
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		return new JScrollPane(mainPanel);
	}
	 
	//TODO Add Listener..
    private void addInputField(String text,JTextField field, JPanel container) {
        JLabel name = new JLabel(text);
        addToPanel(name, field, container);
    }
    private void addButtonField(String text,String buttonText,JLabel checked, JPanel container, ActionListener l){
    	JPanel subPanel = new JPanel(new BorderLayout());
        subPanel.add(new JLabel(text),BorderLayout.LINE_START);
        subPanel.add(checked,BorderLayout.CENTER);
        JButton button = new JButton(buttonText);
        button.addActionListener(l);
        subPanel.add(button,BorderLayout.LINE_END);
        
        subPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        subPanel.setMaximumSize(new Dimension(500,40));
    	
        container.add(subPanel);
    }
    
	    private void addButtonField(String text,String buttonText, JPanel container, ActionListener l){
	    	JLabel name = new JLabel(text);
	    	JButton button = new JButton(buttonText);
	    	button.addActionListener(l);
	    
	    	addToPanel(name, button, container);
	    }
    
    private void addSaveButton( JPanel container){
    	JPanel subPanel = new JPanel();
    	subPanel.setBackground(Color.GRAY);
    	
    	JButton button = new JButton("Save");
    	button.addActionListener(listener.saveListener);
    	subPanel.add(button);
    	subPanel.setMaximumSize(new Dimension(500,40));
    	
    	container.add(subPanel);
    }
    
    private void addToPanel(Component rightPanel, Component leftPanel, JPanel container){
    	JPanel subPanel = new JPanel(new BorderLayout());
        subPanel.add(rightPanel,BorderLayout.LINE_START);
        subPanel.add(leftPanel,BorderLayout.LINE_END);
        
        subPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
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
	
	
    
}
