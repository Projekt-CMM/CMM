package at.jku.ssw.cmm.quest.creator;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

public class QuestCreatorMain {
	
	private JFrame mainFrame;
	private Data data = new Data();
	
	private RightPanel rightPanel;
	private LeftPanel leftPanel;
	
	public static void main(String[] args){
		new QuestCreatorMain().initMainWindow();
	}
	
	public void initMainWindow(){
		mainFrame = new JFrame("Quest Creator");
		mainFrame.setLayout(new BorderLayout());
		
		rightPanel = new RightPanel(this);
		leftPanel = new LeftPanel(this);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPanel.mainPanel(), rightPanel.questPanel());
		splitPane.setOneTouchExpandable(true);
		
		mainFrame.add(splitPane,BorderLayout.CENTER);
		mainFrame.setVisible(true);
	}

	
	public JFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(JFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public RightPanel getRightPanel() {
		return rightPanel;
	}

	public void setRightPanel(RightPanel rightPanel) {
		this.rightPanel = rightPanel;
	}

	public LeftPanel getLeftPanel() {
		return leftPanel;
	}

	public void setLeftPanel(LeftPanel leftPanel) {
		this.leftPanel = leftPanel;
	}
	
	
}
