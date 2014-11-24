package at.jku.ssw.cmm.profile.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import at.jku.ssw.cmm.profile.Profile;

public class GUIprofileSettings {
	
	/**
	 * Launches the program and initiates the main window.
	 * 
	 * @param args
	 *            The shell arguments.
	 */
	public static void init() {
		
		GUIprofileSettings app = new GUIprofileSettings();
		
		Profile profile = null;
		/*try {
			profile = Profile.ReadProfile("profileTest");
		} catch (XMLReadingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		app.start("Create new profile", profile );
	}
	
	//The main window frame
	private JFrame jFrame;
	
	private ProfileSettingsListener listener;
	
	private UpperPanel upperPanel;
	private CentralPanel centralPanel;
	private LowerPanel lowerPanel;
	
	private Profile profile;

	public void start( String title, Profile profile ) {
		
		this.profile = profile;
		
		//Thread analysis
		if (SwingUtilities.isEventDispatchThread())
			System.out.println("[EDT Analyse] Quest GUI runnung on EDT.");
		
		//Standard look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		    e.printStackTrace();
		}
				
		//Initialize quest settings window
		this.jFrame = new JFrame("C Compact - " + title );
		this.jFrame.setMinimumSize(new Dimension(500, 400));
		this.jFrame.setLocationRelativeTo(null);
		this.jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.jFrame.setLayout(new BorderLayout());
		this.jFrame.setResizable(false);
		
		//Initialize central listener
		this.listener = new ProfileSettingsListener(jFrame, this);
		
		//Load upper panel
		this.upperPanel = new UpperPanel(profile, listener);
		this.jFrame.add(this.upperPanel, BorderLayout.PAGE_START);
		
		//Load central panel
		this.centralPanel = new CentralPanel(profile, listener);
		this.jFrame.add(this.centralPanel, BorderLayout.CENTER);
		
		//Load lower panel
		this.lowerPanel = new LowerPanel(profile, listener);
		this.jFrame.add(this.lowerPanel, BorderLayout.PAGE_END);

		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}
	
	public UpperPanel getUpperPanel(){
		return this.upperPanel;
	}
	
	public CentralPanel getCentralPanel(){
		return this.centralPanel;
	}
	
	public LowerPanel getLowerPanel(){
		return this.lowerPanel;
	}
	
	public Profile getProfile(){
		return this.profile;
	}
}
