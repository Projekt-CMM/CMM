package at.jku.ssw.cmm.launcher;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import at.jku.ssw.cmm.profile.Profile;
import at.jku.ssw.cmm.profile.ProfileNotFoundException;
import at.jku.ssw.cmm.profile.XMLReadingException;

public class ProfilePreviewPanel extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private int width, height;
	private ImageIcon icon;
	private Image image;
	private static final int ACCSIZE = 155;
	private Color bg;

	public ProfilePreviewPanel() {
		setPreferredSize(new Dimension(ACCSIZE, -1));
		bg = getBackground();
	}

	private Profile profile;
	
	public void propertyChange(PropertyChangeEvent e) {
		String propertyName = e.getPropertyName();

		// Make sure we are responding to the right event.
		if (propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
			File selection = (File) e.getNewValue();
			String name;

			if (selection == null)
				return;
			else
				name = selection.getAbsolutePath();

			/*
			 * Make reasonably sure we have an image format that AWT can handle
			 * so we don't try to draw something silly.
			 */
			if ((name != null) && (name.toLowerCase().endsWith(".cp"))){
				System.out.println(name);
				try {
					String path = selection.getAbsoluteFile().getParent();
					profile = Profile.ReadProfile(path);
					if(profile.getProfileimage() == null)
						return;
					
					name = path + File.separator + profile.getProfileimage();
					
					icon = new ImageIcon(name);
					image = icon.getImage();
					scaleImage();
					repaint();
				} catch (XMLReadingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ProfileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	private void scaleImage() {
		width = image.getWidth(this);
		height = image.getHeight(this);
		double ratio = 1.0;

		/*
		 * Determine how to scale the image. Since the accessory can expand
		 * vertically make sure we don't go larger than 150 when scaling
		 * vertically.
		 */
		if (width >= height) {
			ratio = (double) (ACCSIZE - 5) / width;
			width = ACCSIZE - 5;
			height = (int) (height * ratio);
		} else {
			if (getHeight() > 150) {
				ratio = (double) (ACCSIZE - 5) / height;
				height = ACCSIZE - 5;
				width = (int) (width * ratio);
			} else {
				ratio = (double) getHeight() / height;
				height = getHeight();
				width = (int) (width * ratio);
			}
		}

		image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
	}

	public void paintComponent(Graphics g) {
		g.setColor(bg);
		g.fillRect(0, 0, ACCSIZE, getHeight());
		g.drawImage(image, getWidth() / 2 - width / 2 + 5, getHeight() / 2
				- height / 2, this);
		
	}

}
