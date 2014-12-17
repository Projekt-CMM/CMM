package at.jku.ssw.cmm.quest;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.utils.LoadStatics;

public class TestPanel {
	
	public TestPanel( GUImain main ){
		this.main = main;
	}
	
	private JPanel cp;
	
	private final GUImain main;
	
	private JPanel questPanel;
	
	private JEditorPane jDescPane;
	
	private JPanel resultPanel;
	
	private JTextArea jResultInfo;
	private JButton jButtonTest;
	
	public void init( JPanel panel ){
		this.cp = panel;
		
		cp.setBorder(new EmptyBorder(5, 5, 5, 5));
		cp.setLayout(new BorderLayout());
		
		this.initQuestPanel();
		this.initResultPanel();
	}
	
	private void initQuestPanel(){
		this.questPanel = new JPanel();
		this.questPanel.setBorder(new TitledBorder(_("Quest Information")));
		this.questPanel.setLayout(new BorderLayout());
		
		this.jDescPane = new JEditorPane();
		this.jDescPane.setEditable(false);
		this.jDescPane.setContentType("text/html");
		JScrollPane scrollPane = new JScrollPane(jDescPane);
		
		this.questPanel.add(scrollPane, BorderLayout.CENTER);
		this.cp.add(questPanel, BorderLayout.CENTER);
	}
	
	private void initResultPanel(){
		this.resultPanel = new JPanel();
		this.resultPanel.setBorder(new TitledBorder(_("Test")));
		this.resultPanel.setLayout(new BoxLayout(this.resultPanel, BoxLayout.PAGE_AXIS));
		
		this.jResultInfo = new JTextArea();
		this.jResultInfo.setEditable(false);
		this.jResultInfo.setText("hello world\n1234");
		JScrollPane scrollPane = new JScrollPane(jResultInfo);
		scrollPane.setMinimumSize(new Dimension(10, 100));
		scrollPane.setPreferredSize(new Dimension(100, 105));
		scrollPane.setMaximumSize(new Dimension(1000, 110));
		this.resultPanel.add(scrollPane);//, BorderLayout.CENTER);
		
		this.jButtonTest = new JButton(_("Run test"));
		
		this.resultPanel.add(this.jButtonTest);//, BorderLayout.PAGE_END);
		this.cp.add(resultPanel, BorderLayout.PAGE_END);
	}
	
	public void updateQuestInfo(){
		if( main.getSettings().getProfile().getCurrentQuest() != null ){
			String html = main.getSettings().getProfile().getCurrentQuest().getInitPath() +  File.separator +
					main.getSettings().getProfile().getCurrentQuest().getPackagePath() + File.separator +
					main.getSettings().getProfile().getCurrentQuest().getQuestPath() +
					File.separator + "description.html";
			System.out.println("-> " + html);
			
			this.jDescPane.setDocument(LoadStatics.readStyleSheet("packages/default/style.css"));
			
			try {
				this.jDescPane.setPage(LoadStatics.getHTMLUrl(html));
			} catch (IOException e) {
				DebugShell.out(State.ERROR, Area.ERROR, html + " not found");
				e.printStackTrace();
			}
			
			this.jDescPane.repaint();
		}
	}

}
