package at.jku.ssw.cmm.profile.settings;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.profile.Profile;

public class LowerPanel extends JPanel {
	
private static final long serialVersionUID = 1L;
	
	public LowerPanel( Profile profile, ProfileSettingsListener listener ){
		
		super();
		
		this.profile = profile;
		this.listener = listener;

		this.init();
	}

	@SuppressWarnings("unused")
	private final Profile profile;
	
	private JButton jButtonCancel;
	private JButton jButtonSave;
	
	private JProgressBar jQuestProgress;
	
	private final ProfileSettingsListener listener;
	
	private void init(){
		
		this.setLayout(new BorderLayout());
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		this.jQuestProgress = new JProgressBar(0, 10);
		this.jQuestProgress.setValue(5);
		this.jQuestProgress.setName("Hello world");
		this.add(this.jQuestProgress, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		
		this.jButtonCancel = new JButton("Cancel");
		buttonPanel.add(this.jButtonCancel);
		this.jButtonCancel.addMouseListener(this.listener.cancelButtonListener);
		
		this.jButtonSave = new JButton("Save");
		buttonPanel.add(this.jButtonSave);
		this.jButtonSave.addMouseListener(this.listener.saveButtonListener);
		
		this.add(buttonPanel, BorderLayout.LINE_END);
	}

}
