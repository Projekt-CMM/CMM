package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import javax.swing.*;
import javax.swing.border.Border;

import at.jku.ssw.cmm.profile.Package;
import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.Quest;
import at.jku.ssw.cmm.profile.XMLReadingException;

import java.beans.*;
import java.awt.*;
import java.io.File;


//TODO get working maybe unused later..
public class ProfilePreview extends JPanel implements PropertyChangeListener {
	
	//Profile Picture
	private JLabel jProfilePicture;
	
	//Profile Name
	private JLabel jProfileName;
	
	//Profile level
	private JLabel jProfileLevel;
	
	//private JPanel jStartPanel;
	private JPanel jStartPanel;
	
	private Color bg;
	
	
	//Constructor
	public ProfilePreview(JFileChooser chooser) {
		
		this.setMinimumSize(new Dimension (100,100));
		this.setPreferredSize(new Dimension (100,100));
		
		this.bg = getBackground();
		
		//Adding property Change Listener.
        chooser.addPropertyChangeListener(this);
    }
	

	public void propertyChange(PropertyChangeEvent arg0) {

		JLabel test = new JLabel("test");
		test.setMinimumSize(new Dimension (100,100));
		test.setPreferredSize(new Dimension (100,100));
		this.add(test);
		
		this.setVisible(true);
		
		Object value = arg0.getNewValue();
		
		//Filtering file paths
		if ( value != null && value.toString().contains(Profile.sep)){
			System.err.println("Profile Preview: " + value);
			//TODO FABIAN xD
			
			try {
				Profile profile = Profile.ReadProfile(value.toString());
				
				if(profile != null){
					System.err.println("Name: " + profile.getName() + " Level:" + profile.getLevel());	
					
					
					drawProfile(profile);
					if(jStartPanel != null)
						this.add(jStartPanel/*,BorderLayout.EAST*/);
					this.repaint();
					
					
				}
				
				//JPanel panel = new JPanel();
				
				this.setMinimumSize(new Dimension (100,100));
				this.setPreferredSize(new Dimension (100,100));
				this.repaint();
				
			} catch (XMLReadingException e) {
				
				//No or false Profile was opened
				this.setMinimumSize(new Dimension (10,10));
				this.setPreferredSize(new Dimension (10,10));
				this.repaint();
			}
		}
		
	}
	
	public void drawProfile(Profile profile){
		jStartPanel = new JPanel(new BorderLayout());
		
		JPanel jInfoPanel = new JPanel();
		jInfoPanel.setLayout(new BoxLayout(jInfoPanel, BoxLayout.LINE_AXIS));
		
		jInfoPanel.add(new JLabel(profile.getName()));
		jInfoPanel.add(new JLabel(_("Level:") + " " + profile.getLevel()));
		
		jStartPanel.add(jInfoPanel,BorderLayout.PAGE_START);
		
		//this.setMinimumSize(new Dimension (100,100));
		//this.setPreferredSize(new Dimension (100,100));	

		
	}
	
	@Override
    public void paintComponent(Graphics g) {
		

		
		g.setColor(bg);
		
		if(jStartPanel != null)
			this.add(jStartPanel/*,BorderLayout.EAST*/);
		this.repaint();

		
		//drawProfile();
	
		
		//this.add(jStartPanel,BorderLayout.EAST);
		
		
    	/*JLabel test = new JLabel("test");
		test.setMinimumSize(new Dimension (100,100));
		test.setPreferredSize(new Dimension (100,100));
		this.add(test);
		
		this.setVisible(true);
		
		/*JLabel test = new JLabel("test");
		test.setMinimumSize(new Dimension (100,100));
		test.setPreferredSize(new Dimension (100,100));
		this.add(test);*/
		
		//this.setVisible(true);
        
        /*
         * If we don't do this, we will end up with garbage from previous
         * images if they have larger sizes than the one we are currently
         * drawing. Also, it seems that the file list can paint outside
         * of its rectangle, and will cause odd behavior if we don't clear
         * or fill the rectangle for the accessory before drawing. This might
         * be a bug in JFileChooser.
         */

    }

  
	
}