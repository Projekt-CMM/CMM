package at.jku.ssw.cmm.quest;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.file.LoadStatics;

public class GUITestPanel {
	
	public GUITestPanel( GUImain main ){
		this.main = main;
	}
	
	private JPanel cp;
	
	private final GUImain main;
	
	private JPanel questPanel;
	
	private JEditorPane jDescPane;
	
	private JPanel resultPanel;
	
	private JTextField[] jParamFields;
	
	private JProgressBar jProgressTest;
	private JTextArea jResultInfo = new JTextArea();

	private JButton jButtonTest;
	
	public void init( JPanel panel ){
		this.cp = panel;
		
		cp.setBorder(new EmptyBorder(5, 5, 5, 5));
		cp.setLayout(new BorderLayout());
		this.initControlPanel();
		this.initQuestPanel();
		//this.initResultPanel();
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
	
	private JButton openPackageButton;
	
	public JButton getOpenPackageButton(){
		return openPackageButton;
	}
	
	/**
	 * Quest Control Panel
	 */
	private void initControlPanel(){
		JButton testButton = new JButton(_("Run Test"));
		openPackageButton = new JButton(_("Package"));
		openPackageButton.setEnabled(false);
		
		JButton openAllPackages = new JButton(_("All Packages"));
		
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		controlPanel.setBorder(new TitledBorder(_("Test")));
		
		ControlPanelListener l = new ControlPanelListener(main);
		openPackageButton.addMouseListener(l.openPackageListener);
		openAllPackages.addMouseListener(l.openAllPackageListener);
		
		testButton.addMouseListener(new TestPanelListener(this.main, this));
		
		controlPanel.add(testButton);
		controlPanel.add(openPackageButton);
		controlPanel.add(openAllPackages);
		
		controlPanel.setPreferredSize(new Dimension(0,90));
		controlPanel.setMinimumSize(new Dimension(0,50));
		
		this.cp.add(controlPanel, BorderLayout.NORTH);
	}
	
	@Deprecated
	private void initResultPanel(){
		this.resultPanel = new JPanel();
		this.resultPanel.setBorder(new TitledBorder(_("Test Parameters")));
		this.resultPanel.setLayout(new BoxLayout(this.resultPanel, BoxLayout.PAGE_AXIS));
		
		this.jParamFields = new JTextField[3];
		
		this.resultPanel.add(new JLabel(_("Input Data")));
		this.jParamFields[0] = new JTextField();
		this.resultPanel.add(this.jParamFields[0]);
		
		this.resultPanel.add(new JLabel(_("Correct Output")));
		this.jParamFields[1] = new JTextField();
		this.resultPanel.add(this.jParamFields[1]);
		
		this.resultPanel.add(new JLabel(_("Your Program's Output")));
		this.jParamFields[2] = new JTextField();
		this.resultPanel.add(this.jParamFields[2]);
		
		this.resultPanel.add(new JLabel(_("Test Progress")));
		this.jProgressTest = new JProgressBar();
		this.resultPanel.add(this.jProgressTest);
		
		this.jButtonTest = new JButton(_("Run test"));
		this.jButtonTest.addMouseListener(new TestPanelListener(this.main, this));
		this.resultPanel.add(this.jButtonTest);
		
		this.cp.add(resultPanel, BorderLayout.PAGE_END);
	}
	
	public void reset() {
		for( int i = 0; i < 3; i++ )
			this.jParamFields[i].setText("");
		this.jProgressTest.setValue(0);
	}
	
	public void setParamText(String text, int index) {
		if( index > 0 && index < 3 )
			this.jParamFields[index].setText(text);
		this.jProgressTest.setValue((index+1)*34);
	}

	public void setDescDoc(String html, String css) {
		
		try {
			this.jDescPane.setDocument(LoadStatics.readStyleSheet(css, 0));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			this.jDescPane.setPage(LoadStatics.getHTMLUrl(html));
		} catch (IOException e) {
			DebugShell.out(State.ERROR, Area.ERROR, html + " not found");
			e.printStackTrace();
		}
		
		this.jDescPane.repaint();
	}

	public void setjQuestTitle(String title) {
		
		this.main.getRightPanel().setQuestMode(title);
	}

}
