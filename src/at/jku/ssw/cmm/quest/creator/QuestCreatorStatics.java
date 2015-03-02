package at.jku.ssw.cmm.quest.creator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class QuestCreatorStatics {

    public static void addInputField(String text,JTextField field, JPanel container) {
        JLabel name = new JLabel(text);
        QuestCreatorStatics.addToPanel(name, field, container);
    }
	
	public static void addButtonField(String text,String buttonText,JLabel checked, JPanel container, ActionListener l){
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
	
	public static void addButtonField(String text,String buttonText, JPanel container, ActionListener l){
    	JLabel name = new JLabel(text);
    	JButton button = new JButton(buttonText);
    	button.addActionListener(l);
    
    	addToPanel(name, button, container);
    }
	
    public static void addToPanel(Component rightPanel, Component leftPanel, JPanel container){
    	JPanel subPanel = new JPanel(new BorderLayout());
        subPanel.add(rightPanel,BorderLayout.LINE_START);
        subPanel.add(leftPanel,BorderLayout.LINE_END);
        
        subPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        subPanel.setMaximumSize(new Dimension(500,40));
    	
        container.add(subPanel);
    }
    
	public static File getPath(FileNameExtensionFilter filter){
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
