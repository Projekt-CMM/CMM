package at.jku.ssw.cmm.gui.popup;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePopup extends JPanel {

	private static final long serialVersionUID = -892555477960117047L;

	public ImagePopup( String path )
    {
        setVisible(true);
        this.image = new ImageIcon(path).getImage();
    }

    private final Image image;

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), this);
    }
}
