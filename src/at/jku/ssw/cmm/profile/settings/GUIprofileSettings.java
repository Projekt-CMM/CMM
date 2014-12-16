/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
package at.jku.ssw.cmm.profile.settings;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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
	public static void init(GUImainSettings settings, boolean returnToLauncher) {
		
		GUIprofileSettings app = new GUIprofileSettings();
		
		/*try {
			profile = Profile.ReadProfile("profileTest");
		} catch (XMLReadingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		if(settings.getProfile() == null)
			app.start(_("Create new profile"), settings, returnToLauncher );
		else
			app.start(_("Edit Profile"), settings, returnToLauncher );
	}
	
	//The main window frame
	private JFrame jFrame;
	
	private ProfileSettingsListener listener;
	
	private UpperPanel upperPanel;
	private CentralPanel centralPanel;
	private LowerPanel lowerPanel;
	
	private GUImainSettings settings;
	
	private boolean returnToLauncher;

	public void start( String title, GUImainSettings settings, boolean returnToLauncher ) {
		
		this.settings = settings;
		this.returnToLauncher = returnToLauncher;
		
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
		this.jFrame.addWindowListener(new ProfileWindowEventListener(this, jFrame));
		
		//Change Sizes of the window specific
		if(settings.getProfile() != null){
			this.jFrame.setMinimumSize(new Dimension(500, 400));
		}else{
			this.jFrame.setMinimumSize(new Dimension(500, 200));
		}
		
		//Initialize central listener
		this.listener = new ProfileSettingsListener(jFrame, this);
		
		//Load upper panel
		this.upperPanel = new UpperPanel(settings.getProfile(), listener);
		this.jFrame.add(this.upperPanel, BorderLayout.PAGE_START);
		
		//Load only if it is an existing Profile
		if(settings.getProfile() != null){
			//Load central panel
			this.centralPanel = new CentralPanel(settings.getProfile(), listener);
			this.jFrame.add(this.centralPanel, BorderLayout.CENTER);
		}	
			//Load lower panel
			this.lowerPanel = new LowerPanel(settings.getProfile(), listener);
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
		return this.settings.getProfile();
	}
	
	public void setProfile(Profile profile){
		this.settings.setProfile(profile);
	}
	
	public GUImainSettings getSettings(){
		return this.settings;
	}
	
	public void dispose(JFrame jFrame){
		
		if(this.settings.getProfile() == null || this.settings.getProfile().getInitPath() == null || returnToLauncher)
			new GUILauncherMain(settings);
		else{
			GUImain app = new GUImain(settings);
			app.start(false);
		}
			
		//Close window
		jFrame.dispose();
	}
}
