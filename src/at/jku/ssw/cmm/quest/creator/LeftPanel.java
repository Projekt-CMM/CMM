package at.jku.ssw.cmm.quest.creator;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class LeftPanel {
	@SuppressWarnings("unused")
	private QuestCreatorMain main;
	
	public LeftPanel(QuestCreatorMain main) {
		this.main = main;
	}
	
	public JPanel mainPanel(){
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JPanel subPanel = new JPanel(new BorderLayout());
		subPanel.add(topPanel(),BorderLayout.NORTH);
		subPanel.add(bottomPanel(),BorderLayout.SOUTH);
		subPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder("Packages")));
		
		mainPanel.add(subPanel,BorderLayout.CENTER);
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		
		
		return mainPanel;
	}
	
	public JPanel topPanel(){
		JPanel empty = new JPanel(new BorderLayout());
		return empty;
	}
	
	public JPanel bottomPanel(){
		JPanel bottomPanel = new JPanel(new BorderLayout());
	
		
		JPanel addPanel = new JPanel(new BorderLayout());
		String[] optionString = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };
		JComboBox<Object> options = new JComboBox<Object>(optionString);
		addPanel.add(options,BorderLayout.WEST);
		
		JButton addButton = new JButton("Add");
		addPanel.add(addButton,BorderLayout.EAST);
		
		bottomPanel.add(addPanel,BorderLayout.WEST);
		JButton exportButton = new JButton("Export");
		bottomPanel.add(exportButton,BorderLayout.EAST);
		
		return bottomPanel;
	}
}
