package at.jku.ssw.cmm.quest.creator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ResourcesFrame extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4161401409585675156L;


	public ResourcesFrame() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addComp(new JLabel("Subfolder"),new JLabel("hallo"));
		addComp(new JLabel("Datei"),new JButton("Open"));
		
		addComp(new JLabel(""),new JButton("Add"));
		
		setMinimumSize(new Dimension(500,100));


		
	}
	
	
	public void addComp(Component right, Component left){
		JPanel subPanel = new JPanel(new BorderLayout());
		subPanel.add(right,BorderLayout.LINE_START);
		subPanel.add(left,BorderLayout.LINE_END);
        
        subPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        subPanel.setMaximumSize(new Dimension(500,40));
    	
        add(subPanel);
	}
}
