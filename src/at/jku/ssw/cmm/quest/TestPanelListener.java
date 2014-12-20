package at.jku.ssw.cmm.quest;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.profile.Quest;

public class TestPanelListener implements MouseListener, TestReply {
	
	public TestPanelListener( GUImain main, GUITestPanel testPanel ){
		this.main = main;
		this.testPanel = testPanel;
	}
	
	private final GUImain main;
	private final GUITestPanel testPanel;

	@Override
	public void mouseClicked(MouseEvent e) {
		
		//Get current quest
		Quest quest = main.getSettings().getProfile().getCurrentQuest();
		
		//Check if current quest is available
		if( quest == null )
			return;
		
		//Start test -> set right panel to test mode
		main.getRightPanel().setTestMode();
		
		//TODO is there a static variable for "packages" folder name???
		System.out.println("Quest: " + "packages" + File.separator + quest.getPackagePath() + File.separator + quest.getQuestPath());
		System.out.println("Ref: " + quest.getRef() + ", Input: " + quest.getInput());
		
		//No reference -> can not test
		if( quest.getRef() == null ){
			this.testPanel.output("[ERROR] Quest incomplete: no reference file");
			this.main.getRightPanel().setFailedMode();
			return;
		}
		
		String[] ignore = {"\n", ",", ";"};
		QuestTester qt = new QuestTester((TestReply)this,
				"packages" + File.separator + quest.getPackagePath() + File.separator + quest.getQuestPath() + File.separator + quest.getInput(),
				"packages" + File.separator + quest.getPackagePath() + File.separator + quest.getQuestPath() + File.separator + quest.getRef(),
				main.getSettings().getCMMFilePath(), ignore);
		qt.start();
	}

	@Override
	public void mouseEntered(MouseEvent e) {	}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void output(final String msg) {
		
		try {
			java.awt.EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					testPanel.output(msg);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void finished(final QuestMatchError e) {
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				//Test successful
				if( e == null )
					main.getRightPanel().setSuccessMode();
				//Test failed
				else{
					main.getRightPanel().setFailedMode();
					testPanel.output(e.getMessage());
				}
			}
		});
	}
}
