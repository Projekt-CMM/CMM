package at.jku.ssw.cmm.launcher;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import at.jku.ssw.cmm.gui.properties.GUImainSettings;
import at.jku.ssw.cmm.gui.utils.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.XMLReadingException;


public class GUILauncherMain extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private final JPanel jGlobalPanel;
	
	private final GUImainSettings settings;
	
	
	public static void main(String[] args) {
		new GUILauncherMain();
	}
	
	public GUILauncherMain(GUImainSettings settings){
		super("C Compact Launcher");
		super.setMinimumSize(new Dimension(700,550));
		
		this.settings = settings;
		
		jGlobalPanel = new JPanel();
		jGlobalPanel.setLayout(new BorderLayout());
		jGlobalPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		
		//Block contains Welcome Block and CMM Logo
		this.addWelcomeBlock();
		
		//Block contains 
		this.addProfilePanel();
		this.addBottomPanel();	
		
		super.add(jGlobalPanel);
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setVisible(true);
	}
	
	public GUILauncherMain(){
		this(new GUImainSettings());
	}

	/**
	 * Adding a Panel whitch contains:
	 * Welcome Message and logo
	 */
	private void addWelcomeBlock(){
		JPanel jWelcomePanel = new JPanel(new BorderLayout());
		
		//setting the welcome label
		JLabel welcomeMessage = new JLabel(_("Welcome"));
		jWelcomePanel.add(welcomeMessage,BorderLayout.LINE_START);
		
		//setting the size of the welcome message
		welcomeMessage.setFont (welcomeMessage.getFont().deriveFont (64.0f));
		
		
		JPanel logo = new JPanel();
		//Path for the logo image
		logo.add(LoadStatics.loadImage("images/logo.png", false, 75, 75));
		
		jWelcomePanel.add(logo,BorderLayout.LINE_END);
	
		
		jGlobalPanel.add(jWelcomePanel,BorderLayout.PAGE_START);
	}
	
	/**
	 * Adding selection Bar + Last Profiles Preview
	 */
	private void addProfilePanel(){
		JPanel jProfilePanel = new JPanel(new BorderLayout());
		JPanel jSelectProfilePanel = new JPanel(new BorderLayout());
		
		//Profile selection Bar
			JLabel jSelectProfileLabel = new JLabel(_("Select Profile:"));
			jSelectProfilePanel.add(jSelectProfileLabel, BorderLayout.LINE_START);
			
			JPanel jRightButtons = new JPanel(new FlowLayout());
			jRightButtons.setBackground(Color.WHITE);
			
				//creating the find button
				JButton jFindProfile = new JButton(_("Find"));
				jFindProfile.addMouseListener(new FindProfileListener((JFrame)this, settings));
				jRightButtons.add(jFindProfile);
				
				
				//creating new button
				JButton jCreateProfile = new JButton("New");
				jCreateProfile.addMouseListener(new AddProfileListener((JFrame)this, settings));
				jRightButtons.add(jCreateProfile);
			
			//adding the buttons to the selection Panel
			jSelectProfilePanel.add(jRightButtons,BorderLayout.LINE_END);
			
			jSelectProfilePanel.setBackground(Color.white);
			jSelectProfilePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		jProfilePanel.add(jSelectProfilePanel, BorderLayout.PAGE_START);
		
		
		//creating profile Preview Panel
		JPanel jPreviewPanel = new JPanel();
			
			
			jPreviewPanel.setBackground(Color.white);
			jPreviewPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			
				JPanel jPreviewProfile = new JPanel();
				
				//Show recent profiles
				for(String path : this.settings.getRecentProfiles())
					try {
						jPreviewProfile.add(addProfilePreview(Profile.ReadProfile(path)));
					} catch (XMLReadingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			JScrollPane scrollPane = new JScrollPane(jPreviewProfile);
			
			//Scrollbar only Horizontal activated
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			//scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
			
		jProfilePanel.add(scrollPane);
		
		//Adding panel to Global panel	
		jGlobalPanel.add(jProfilePanel,BorderLayout.CENTER);
	}
	
	private JPanel addProfilePreview(Profile profile){
		JPanel jMarginPanel = new JPanel(new BorderLayout());

		
		//jMarginPanel.setMinimumSize(new Dimension(200,300));
		//jMarginPanel.setPreferredSize(new Dimension(200,300));
		//jMarginPanel.setMinimumSize(new Dimension(200,300));
		
		jMarginPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 10, 10, 10), new EtchedBorder()));
		
		JPanel jProfile = new JPanel(new BorderLayout());
		jProfile.setBackground(Color.WHITE);
		jProfile.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JPanel jProfileTop = new JPanel(new BorderLayout());
			jProfileTop.setBackground(Color.WHITE);
			
			if(!profile.isMaster()){	
				
				JLabel name = new JLabel(_("Name") + ": " + profile.getName());
				JLabel level = new JLabel(_("Level") + ": " + profile.getLevel());
			
			
				jProfileTop.add(name,BorderLayout.PAGE_START);
				jProfileTop.add(level,BorderLayout.CENTER);
			}
			
		jProfile.add(jProfileTop, BorderLayout.PAGE_START);
		
		JPanel profilePicPanel = new JPanel();
		
		/*try {
			profilePicPanel.setBackground(ImageIO.read(new File("profileTest/icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		
		profilePicPanel.setPreferredSize(new Dimension(200,200));
		profilePicPanel.setMinimumSize(new Dimension(200,200));
		
		//TODO save images in labels
		if( profile.getProfileimage() == null )
			profilePicPanel.add(LoadStatics.loadImage(Profile.FILE_DEFAULTIMAGE, false, 200, 200));
		else
			profilePicPanel.add(LoadStatics.loadImage(profile.getInitPath() + File.separator + profile.getProfileimage(), false, 200, 200));
		
		profilePicPanel.addMouseListener(new EditProfileListener((JFrame)this,settings, profile));
			
		jProfile.add(profilePicPanel, BorderLayout.CENTER);
		
		if(!profile.isMaster()){
			JButton openProfile = new JButton("OPEN");
			openProfile.addMouseListener(new LauncherListener(settings,(JFrame)this, profile));
		jProfile.add(openProfile,BorderLayout.PAGE_END);
		}
			
		jMarginPanel.add(jProfile,BorderLayout.CENTER);
		
		
		return jMarginPanel	;
		
	}
	
	private void addBottomPanel(){
		JPanel jFinishPanel = new JPanel(new BorderLayout());
		
		String[] languages = { "Englisch", "Deutsch"};
		JComboBox<String> jLanguageChooser = new JComboBox<>(languages);
		jLanguageChooser.setSelectedIndex(1);
		jLanguageChooser.setMinimumSize(new Dimension(100,30));
		jLanguageChooser.setPreferredSize(new Dimension(100,30));
		//jLanguateChooser.addActionListener(this);
		
		jFinishPanel.add(jLanguageChooser,BorderLayout.LINE_START);
	
	
	//JButton jStartButton = new JButton("Start");
		//jFinishPanel.add(jStartButton,BorderLayout.LINE_END);
	
		
		jFinishPanel.setBackground(Color.white);
		jFinishPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

	jGlobalPanel.add(jFinishPanel,BorderLayout.PAGE_END);
	//Page end Finished
	}

}
