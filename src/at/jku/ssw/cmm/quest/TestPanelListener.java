package at.jku.ssw.cmm.quest;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JOptionPane;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.XMLWriteException;

public class TestPanelListener implements TestReply {
	
	public TestPanelListener( GUImain main, GUITestPanel testPanel ){
		this.main = main;
		this.testPanel = testPanel;
		this.startListener = new StartListener((TestReply)this);
	}
	
	private final GUImain main;
	private final GUITestPanel testPanel;
	public final StartListener startListener;
	
	private QuestTester qt = null;

	public class StartListener implements MouseListener {
		
		public StartListener(TestReply reply) {
			this.reply = reply;
		}
		
		private final TestReply reply;
		
		@Override
		public void mouseClicked(MouseEvent e) {
			
			//Get current quest
			Quest quest = main.getSettings().getProfile().getCurrentQuest();
			
			//Check if current quest is available
			if( quest == null || qt != null )
				return;
			
			// Save current *.cmm file
			if (main.getSettings().getCMMFilePath() != null)
				// Save to working directory
				main.getSaveManager().directSave();
			
			//Start test -> set right panel to test mode
			main.getRightPanel().setTestMode();
			
			//TODO is there a static variable for "packages" folder name???
			System.out.println("Quest: " + "packages" + File.separator + quest.getPackagePath() + File.separator + quest.getQuestPath());
			System.out.println("Ref: " + Quest.FILE_REF + ", Input: " +  Quest.FILE_INPUT_CMM);
			
			//No reference -> can not test
			if( !quest.isRef()){
				main.getRightPanel().setFailedMode();
				return;
			}
			
			testPanel.reset();
			main.getRightPanel().getDebugPanel().setReadyMode();
			
			try{
				qt = new QuestTester(reply, main,
						new File("packages" + File.separator + quest.getPackagePath() + File.separator + quest.getQuestPath() + File.separator + Quest.FILE_INPUT_CMM),
						new File("packages" + File.separator + quest.getPackagePath() + File.separator + quest.getQuestPath() + File.separator + Quest.FILE_REF),
						main.getSettings().hasCMMFilePath() ? new File(main.getSettings().getCMMFilePath()) : main.getLeftPanel().getSourceCode(),
						quest.getMatcher());
				qt.start();
			} catch(Exception e1) {
				System.err.println("[Critical] Error when initializing quest tester thread");
				e1.printStackTrace();
			}
		}
	
		@Override
		public void mouseEntered(MouseEvent e) {	}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
	};
	
	public MouseListener cancelListener = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			
			if( qt != null )
				qt.cancel();
		}

		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
	};

	@Override
	public void finished(final QuestMatchError e) {
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				//Test successful
				if( e == null ){
					main.getRightPanel().setSuccessMode();
					JOptionPane.showMessageDialog(null, _("Quest successfully completed"));
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
				}
			}
		});
		this.qt = null;
	}

	@Override
	public void setInputData(final String data) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
					main.getRightPanel().getTestPanel().setParamText(data, 0);
			}
		});
	}

	@Override
	public void setCorrectOutput(final String data) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				main.getRightPanel().getTestPanel().setParamText(data, 1);
			}
		});
	}

	@Override
	public void setUserOutput(final String data) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				main.getRightPanel().getTestPanel().setParamText(data, 2);
			}
		});
	}
}
