package at.jku.ssw.cmm.quest;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.XMLWriteException;

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
		
		// Save current *.cmm file
		if (main.getSettings().getCMMFilePath() != null)
			// Save to working directory
			main.getSaveManager().directSave();
		/*else
			// Open "save as" dialog if there is no working directory
			main.getSaveManager().doSaveAs();

		main.setFileSaved();
		main.updateWinFileName();
		main.getRightPanel().getDebugPanel().updateFileName();*/
		
		//Start test -> set right panel to test mode
		main.getRightPanel().setTestMode();
		
		//TODO is there a static variable for "packages" folder name???
		System.out.println("Quest: " + "packages" + File.separator + quest.getPackagePath() + File.separator + quest.getQuestPath());
		System.out.println("Ref: " + Quest.FILE_REF + ", Input: " +  Quest.FILE_INPUT_CMM);
		
		//No reference -> can not test
		if( !quest.isRef()){
			this.testPanel.output("[ERROR] Quest incomplete: no reference file");
			this.main.getRightPanel().setFailedMode();
			return;
		}
		
		this.testPanel.reset();
		
		String[] ignore = {"\n", ",", ";"};
		QuestTester qt = new QuestTester((TestReply)this, this.main,
				new File("packages" + File.separator + quest.getPackagePath() + File.separator + quest.getQuestPath() + File.separator + Quest.FILE_INPUT_CMM),
				new File("packages" + File.separator + quest.getPackagePath() + File.separator + quest.getQuestPath() + File.separator + Quest.FILE_REF),
				main.getSettings().hasCMMFilePath() ? new File(main.getSettings().getCMMFilePath()) : main.getLeftPanel().getSourceCode(),
				ignore);
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
				if( e == null ){
					main.getRightPanel().setSuccessMode();
					try {
						//Change the Quest to finished
						main.getSettings().getProfile().changeQuestStateToFinished( main.getSettings().getProfile().getCurrentQuest());
						
						//Repaint the Tokens
						main.getRightPanel().getProfilePanel().refreshCentralPanel();
					} catch (XMLWriteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				//Test failed
				else{
					main.getRightPanel().setFailedMode();
					testPanel.output(e.getMessage());
				}
			}
		});
	}
}
