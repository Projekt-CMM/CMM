package at.jku.ssw.cmm.gui.utils;

import java.awt.Color;
import java.awt.Dimension;
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
	
	public static final JLabel loadImage( String path, boolean createBorder ){
		BufferedImage loadBuffer = null;
		try {
			loadBuffer = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.err.println("Error reading image");
		}
		JLabel picture = new JLabel(new ImageIcon(loadBuffer));
		if( createBorder )
			picture.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		
		return picture;
	}
	
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
}
