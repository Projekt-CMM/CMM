package at.jku.ssw.cmm.quest.creator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class RightPanel {
	private final QuestCreatorMain main;
	
	public RightPanel(QuestCreatorMain main) {
		this.main = main;
		newQuestPanel = new NewQuestPanel(main).questPanel();
		ressourcePanel = new NewResourcePanel(main).mainPanel();
	}
	
	private JSplitPane rightVertiacal;
	private final JComponent newQuestPanel;
	private final JComponent ressourcePanel;
	
	
	
	private JPanel selectPanel(){
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setMaximumSize(new Dimension(100,400));
		
		JPanel addPanel = new JPanel(new BorderLayout());
		String[] optionString = { "New Quest", "Add Resource" };
		JComboBox options = new JComboBox(optionString);
		
		options.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				 if (e.getStateChange() == ItemEvent.SELECTED) {
			          Object item = e.getItem();
			          if(item instanceof String)
			        	  if(((String)item).equals("Add Resource"))
			        		  changeToNewRessource();
			        	  else if(((String)item).equals("New Quest"))
			        		  changeToNewQuest();
			       }
				
			}
			//Resource
		});
		
		addPanel.add(options,BorderLayout.WEST);
		
		JButton addButton = new JButton("Exit");
		addPanel.add(addButton,BorderLayout.EAST);
		
		bottomPanel.add(addPanel,BorderLayout.WEST);
		JButton exportButton = new JButton("Export");
		bottomPanel.add(exportButton,BorderLayout.EAST);
		
		return bottomPanel;
	}
	
	public JComponent changeToNewRessource(){
		if(rightVertiacal != null)
			rightVertiacal.remove(newQuestPanel);
		else
			initRightVertical();
		
		rightVertiacal.add(ressourcePanel, JSplitPane.BOTTOM);
		rightVertiacal.repaint();
		
		return rightVertiacal;
	}
	
	public JComponent changeToNewQuest(){
		if(rightVertiacal != null)
			rightVertiacal.remove(ressourcePanel);
		else
			initRightVertical();
		
		rightVertiacal.add(newQuestPanel, JSplitPane.BOTTOM);
		rightVertiacal.repaint();
		
		return rightVertiacal;
	}
	
	private void initRightVertical(){
		rightVertiacal = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightVertiacal.add(selectPanel(), JSplitPane.TOP);
	}
}
