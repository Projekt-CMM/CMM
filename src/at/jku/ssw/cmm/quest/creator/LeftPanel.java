package at.jku.ssw.cmm.quest.creator;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class LeftPanel {
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
		
		JButton addButton = new JButton("Add");
		JButton exportButton = new JButton("Export");
		
		bottomPanel.add(addButton,BorderLayout.WEST);
		bottomPanel.add(exportButton,BorderLayout.EAST);
		
		return bottomPanel;
	}
}
