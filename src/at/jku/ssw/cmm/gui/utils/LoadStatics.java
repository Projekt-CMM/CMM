package at.jku.ssw.cmm.gui.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public final class LoadStatics {

	public static final JLabel loadImage( String path ){
		return loadImage( path, true );
	}
	
	//TODO
	public static final JLabel loadImage( String path, boolean createBorder ){
		return loadImage(path, true, 20 ,20);
	}
	
	public static final JLabel loadImage(String path, boolean createBorder, int width, int height){
		
		
		BufferedImage loadBuffer = null;
		try {
			loadBuffer = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.err.println("Error reading image");
		}
		
		//Resize Window
		if(width != -1 && height != -1)
			loadBuffer =  scaleImage(loadBuffer, width, height);
		
		JLabel picture = new JLabel(new ImageIcon(loadBuffer));
		if( createBorder )
			
			picture.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		
		
		return picture;
	}
	
	private static final BufferedImage scaleImage(BufferedImage image, int width, int height){
		
		BufferedImage otherImage = image;
				BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

				Graphics g = newImage.createGraphics();
				g.drawImage(otherImage, 0, 0, width, height, null);
				g.dispose();
				
		return newImage;
				
	}
	
	@Deprecated
	public static final JScrollPane loadHTMLdoc( String path, String pathCSS ){
		JEditorPane editorPane = new JEditorPane();
      	editorPane.setEditable(false);
      	java.net.URL htmlURL = null;
      	java.net.URL cssURL = null;
		try {
			htmlURL = new File(path).toURI().toURL();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		try {
			cssURL = new File(pathCSS).toURI().toURL();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		//Set content type of editor pane to HTML
		editorPane.setContentType("text/html");
      	
      	//Import stylesheet
      	if( cssURL != null ){
      		System.out.println("Reading stylesheet: " + cssURL);
      		StyleSheet s = new StyleSheet();
      		s.importStyleSheet(cssURL);
      		HTMLEditorKit kit = (HTMLEditorKit)editorPane.getEditorKit();
      		kit.setStyleSheet(s);
      		javax.swing.text.Document doc = kit.createDefaultDocument();
      		editorPane.setDocument(doc);
      	}
      	
      	//Import Text
      	if (htmlURL != null) {
      		System.out.println("Loading HTML document: " + htmlURL);
      	    try {
      	        editorPane.setPage(htmlURL);
      	    } catch (IOException e) {
      	        System.err.println("Attempted to read a bad URL: " + htmlURL);
      	    }
      	} else {
      	    System.err.println("Couldn't find file");
      	}
      	
      	//Put the editor pane in a scroll pane.
      	editorPane.setMinimumSize(new Dimension(10, 10));
      	JScrollPane editorScrollPane = new JScrollPane(editorPane);
      	editorScrollPane.setVerticalScrollBarPolicy(
      	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      	editorScrollPane.setPreferredSize(new Dimension(100, 300));
      	editorScrollPane.setMinimumSize(new Dimension(10, 10));
      	
      	return editorScrollPane;
	}
	
	public static javax.swing.text.Document readStyleSheet( String path ){
		java.net.URL cssURL = null;
		try {
			cssURL = new File(path).toURI().toURL();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		//Import stylesheet
      	if( cssURL != null ){
      		System.out.println("Reading stylesheet: " + cssURL);
      		StyleSheet s = new StyleSheet();
      		s.importStyleSheet(cssURL);
      		HTMLEditorKit kit = new HTMLEditorKit();
      		kit.setStyleSheet(s);
      		javax.swing.text.Document doc = kit.createDefaultDocument();
      		return doc;
      	}
      	return null;
	}
	
	public static java.net.URL getHTMLUrl( String path ){
		
		java.net.URL htmlURL = null;
		
		try {
			htmlURL = new File(path).toURI().toURL();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		return htmlURL;
	}
}
