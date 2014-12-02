package at.jku.ssw.cmm.launcher;

import static at.jku.ssw.cmm.gettext.Language._;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import at.jku.ssw.cmm.gui.utils.LoadStatics;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.XMLReadingException;


public class GUILauncherMain {

	private static JFrame jFrame;
	private static JPanel jGlobalPanel;
	
	
	public static void main(String[] args) {
		init();

	}
	
	public static void init(){
		
		GUILauncherMain launcher = new GUILauncherMain();
		
		jFrame = new JFrame("C Compact Launcher");
		
		jFrame.setMinimumSize(new Dimension(700,550));
		
		
		jGlobalPanel = new JPanel();
		jGlobalPanel.setLayout(new BorderLayout());
		jGlobalPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		
		//Block contains Welcome Block and CMM Logo
		launcher.addWelcomeBlock();
		
		//Block contains 
		launcher.addProfilePanel();
		launcher.addBottomPanel();	
		
		jFrame.add(jGlobalPanel);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setVisible(true);
		
		System.out.println("finished");
	}
	
	/**
	 * 
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
				jFindProfile.addMouseListener(new FindProfileListener(jFrame));
				jRightButtons.add(jFindProfile);
				
				
				//creating new button
				JButton jCreateProfile = new JButton("New");
				jCreateProfile.addMouseListener(new AddProfileListener(jFrame));
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
				
				//TODO implement real Profile settings.xml
				for(int i = 0; i< 5; i++)
					jPreviewProfile.add(addProfilePreview(new Profile()));

				jPreviewProfile.add(addProfilePreview(new Profile()));
				
			JScrollPane scrollPane = new JScrollPane(jPreviewProfile);
			
			//Scrollbar only Horizontal activated
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			//scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
			
		jProfilePanel.add(scrollPane);
		
		//Adding panel to Global panel	
		jGlobalPanel.add(jProfilePanel,BorderLayout.CENTER);
	}
	
	public JPanel addProfilePreview(Profile profile){
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
				
				JLabel name = new JLabel("Name: Nick");
				JLabel level = new JLabel("Level: 14");
			
			
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
		
		if(!profile.isMaster()){
			profilePicPanel.add(LoadStatics.loadImage("profileTest/icon.png", false, 200, 200));
		}
		else{
			profilePicPanel.add(LoadStatics.loadImage("addProfile.png", false, 200, 200));
			profilePicPanel.addMouseListener(new AddProfileListener(jFrame));
		}
			
		jProfile.add(profilePicPanel, BorderLayout.CENTER);
		
		if(!profile.isMaster()){
			JButton openProfile = new JButton("OPEN");
			openProfile.addMouseListener(new LauncherListener(profile,jFrame));
		jProfile.add(openProfile,BorderLayout.PAGE_END);
		}
			
		jMarginPanel.add(jProfile,BorderLayout.CENTER);
		
		
		return jMarginPanel	;
		
	}
	
	private void addBottomPanel(){
		JPanel jFinishPanel = new JPanel(new BorderLayout());
		
		String[] languages = { "Englisch", "Deutsch"};
		JComboBox jLanguateChooser = new JComboBox(languages);
		jLanguateChooser.setSelectedIndex(1);
		jLanguateChooser.setMinimumSize(new Dimension(100,30));
		jLanguateChooser.setPreferredSize(new Dimension(100,30));
		//jLanguateChooser.addActionListener(this);
		
		jFinishPanel.add(jLanguateChooser,BorderLayout.LINE_START);
	
	
	//JButton jStartButton = new JButton("Start");
		//jFinishPanel.add(jStartButton,BorderLayout.LINE_END);
	
		
		jFinishPanel.setBackground(Color.white);
		jFinishPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

	jGlobalPanel.add(jFinishPanel,BorderLayout.PAGE_END);
	//Page end Finished
	}

}
