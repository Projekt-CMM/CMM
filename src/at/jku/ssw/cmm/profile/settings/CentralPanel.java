package at.jku.ssw.cmm.profile.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.gui.utils.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;

public class CentralPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public CentralPanel( Profile profile, ProfileSettingsListener listener ){
		
		super();
		
		this.profile = profile;

		this.init();
	}

	@SuppressWarnings("unused")
	private final Profile profile;
	
	private JList<JPanel> achievements;
	
	private void init(){
		
		this.setLayout(new BorderLayout());
		this.setBorder(new TitledBorder("Achievements"));
		
		this.achievements = new JList<>(initList());
		this.achievements.setCellRenderer(new TokenListRenderer((DefaultListModel<JPanel>)this.achievements.getModel()));
		this.achievements.setBackground(this.getBackground());

		JScrollPane scroll = new JScrollPane(this.achievements);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setMinimumSize(new Dimension(100, 100));
		scroll.setPreferredSize(new Dimension(200, 200));
		scroll.setMaximumSize(new Dimension(500, 500));
		scroll.setBackground(this.getBackground());
		
		this.add(scroll, BorderLayout.CENTER);
	}
	
	private static DefaultListModel<JPanel> initList(){
		DefaultListModel<JPanel> model = new DefaultListModel<>();
		
		JPanel panel;
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(LoadStatics.loadImage("profileTest/tokens/Icon_Craft.png", false));
		panel.add(new JLabel("Token of code crafting"));
		model.addElement(panel);
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(LoadStatics.loadImage("profileTest/tokens/Icon_Deko.png", false));
		panel.add(new JLabel("Golden star of efficient coding"));
		model.addElement(panel);
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(LoadStatics.loadImage("profileTest/tokens/Icon_Fisch.png", false));
		panel.add(new JLabel("Achievement of fishy programming"));
		model.addElement(panel);
		
		/*panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(LoadStatics.loadImage("profileTest/tokens/Icon_Haus.png", false));
		panel.add(new JLabel("Token of clean comments, Level 2"));
		model.addElement(panel);
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(LoadStatics.loadImage("profileTest/tokens/Icon_Komp.png", false));
		panel.add(new JLabel("Artefact of C Compact code logics"));
		model.addElement(panel);
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(LoadStatics.loadImage("profileTest/tokens/Icon_Rescue.png", false));
		panel.add(new JLabel("Token of error handling, Level 4"));
		model.addElement(panel);
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(LoadStatics.loadImage("profileTest/tokens/Icon_Segel.png", false));
		panel.add(new JLabel("Achievement of super fast algorithms, Level 3"));
		model.addElement(panel);*/
		
		return model;
	}
	
}
