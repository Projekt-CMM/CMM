package at.jku.ssw.cmm.profile.settings;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.properties.GUImainSettings;
import at.jku.ssw.cmm.launcher.GUILauncherMain;
import at.jku.ssw.cmm.profile.Profile;

public class GUIprofileSettings {
	
	/**
	 * Launches the program and initiates the main window.
	 * @param profile 
	 * 
	 * @param args
	 *            The shell arguments.
	 */
	public static Profile init(Profile profile) {
		
		GUIprofileSettings app = new GUIprofileSettings();
		
		/*try {
			profile = Profile.ReadProfile("profileTest");
		} catch (XMLReadingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		if(profile == null)
			app.start(_("Create new profile"), profile );
		else
			app.start(_("Edit Profile"), profile);
		
		return profile;
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
				
		//Initialize quest settings window
		this.jFrame = new JFrame("C Compact - " + title );
		
		this.jFrame.setLocationRelativeTo(null);
		this.jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.jFrame.setLayout(new BorderLayout());
		this.jFrame.setResizable(false);
		
		//Exit ProfileSettings and starting the right jFrame on Close
		this.jFrame.addWindowListener(new ProfileWindowEventListener(profile, jFrame));
		
		//Change Sizes of the window specific
		if(profile != null){
			this.jFrame.setMinimumSize(new Dimension(500, 400));
		}else{
			this.jFrame.setMinimumSize(new Dimension(500, 200));
		}
		
		//Initialize central listener
		this.listener = new ProfileSettingsListener(jFrame, this);
		
		//Load upper panel
		this.upperPanel = new UpperPanel(profile, listener);
		this.jFrame.add(this.upperPanel, BorderLayout.PAGE_START);
		
		//Load only if it is an existing Profile
		if(profile != null){
			//Load central panel
			this.centralPanel = new CentralPanel(profile, listener);
			this.jFrame.add(this.centralPanel, BorderLayout.CENTER);
		}	
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
	
	public void setProfile(Profile profile){
		this.profile = profile;
	}
	
	public static void dispose(Profile profile, JFrame jFrame){
		
		if(profile == null || profile.getInitPath() == null)
			GUILauncherMain.init();
		else{
			GUImain app = new GUImain(new GUImainSettings(profile));
			app.start(false);
		}
			
		//Close window
		jFrame.dispose();
	}
}
