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
 
package at.jku.ssw.cmm.gui.file;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public final class LoadStatics {

	public static final ImageIcon loadIcon(String path, int width, int height){
		
		ImageIcon imageIcon = new ImageIcon(path); // load the image to a imageIcon

		return new ImageIcon(scaleImage(width,height,  new ImageIcon(path),Image.SCALE_AREA_AVERAGING)); //return the new image
	}
	
	
	public static Image scaleImage(int maxwidth,int maxheight, ImageIcon imageIcon, int SCALE) {

		int ACCSIZE = (int) Math.sqrt(maxheight * maxheight + maxwidth * maxwidth); 

		
		int width = imageIcon.getIconWidth();
		int height = imageIcon.getIconHeight();
		double ratio = 1.0;

		Image image = imageIcon.getImage();
		
		//Calculate the height and new width
		if (width > height) {
			ratio = (double) (ACCSIZE - 5) / width;
			width = ACCSIZE - 5;
			height = (int) (height * ratio);
			
		}else 
			//Scale the image normal if the sizes are equal
			if (width == height){
			return image = image.getScaledInstance(maxwidth, maxheight,SCALE );

		}else
		{
			//If the Width is greater than it should be, the difference is reduced on both sides
			if(width < maxwidth){
				int dwidth = maxwidth - width;
				
				width = width - dwidth;
				height = height - dwidth;
			}
			
			//Calculating new Sizes
				ratio = (double) maxheight / height;
				height = maxheight;
				width = (int) (width * ratio);
			
		}

		//Scaling the Image with best quality.
		return image = image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
	}
	
	public static void scalePreviewImage(Image image, JPanel panel, int width, int height, int diagonal ) {
		width = image.getWidth(panel);
		height = image.getHeight(panel);
		double ratio = 1.0;

		/*
		 * Determine how to scale the image. Since the accessory can expand
		 * vertically make sure we don't go larger than 150 when scaling
		 * vertically.
		 */
		if (width >= height) {
			ratio = (double) (diagonal - 5) / width;
			width = diagonal - 5;
			height = (int) (height * ratio);
		} else {
			if (panel.getHeight() > 150) {
				ratio = (double) (diagonal - 5) / height;
				height = diagonal - 5;
				width = (int) (width * ratio);
			} else {
				ratio = (double) panel.getHeight() / height;
				height = panel.getHeight();
				width = (int) (width * ratio);
			}
		}

		image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
	}

	/**
	 * Copy Files from one Destination to another
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public static void copyFileUsingStream(File source, File dest) throws IOException {
		
		//Closes the operation if source or destination is null
		if(source == null || dest == null)
			return;
		
		//is the file existing
		if(!source.exists())
			throw new IOException();
		
		//To cancel useless operations
		if(source.equals(dest))
			return;
		
		//Creating direktories,:
		String absolutePath = dest.getAbsolutePath();
		new File(absolutePath.substring(0, absolutePath.indexOf(dest.getName()))).mkdirs();
		
		System.out.println("Copying files from:" + source + " to " + dest); 
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	    	if(is != null)
	    		is.close();
	        
	    	if(is != null)
	    		os.close();
	    }
	}
	
	/**
	 * Copying the whole Folder into another one!
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
    public static void copyFolder(File source, File destination) throws IOException{
        	if(source.isDirectory()){
        		//if directory not exists, create it
        		if(!destination.exists()){
        		   destination.mkdir();
        		   System.out.println("Directory copied from " 
                                  + source + "  to " + destination);
        		}
     
        		//list all the directory contents
        		String files[] = source.list();
     
        		for (String file : files) {
        		   //construct the src and dest file structure
        		   File srcFile = new File(source, file);
        		   File destFile = new File(destination, file);
        		   //recursive copy
        		   copyFolder(srcFile,destFile);
        		}
     
        	}else{
        		LoadStatics.copyFileUsingStream(source, destination);
        	    System.out.println("File copied from " + source + " to " + destination);
        	}
        }

	public static javax.swing.text.Document readStyleSheet(String path, int size) throws MalformedURLException {
		java.net.URL cssURL = null;
		cssURL = new File(path).toURI().toURL();

		// Import stylesheet
		if (cssURL != null) {
			System.out.println("Reading stylesheet: " + cssURL);
			StyleSheet s = new StyleSheet();
			s.importStyleSheet(cssURL);
	        setFontSize(s, size);
			
			HTMLEditorKit kit = new HTMLEditorKit();
			kit.setStyleSheet(s);
			javax.swing.text.Document doc = kit.createDefaultDocument();
			
			return doc;
		}
		return null;
	}
	
	private static void setFontSize(StyleSheet s, int size) {
		switch(size) {
		case -1:
			s.addRule("h1{font-size:1.2em;}h2{font-size:1.1em;}h3{font-size:1.0em;}p{font-size:0.9em;}");
			break;
		default:
			s.addRule("h1{font-size:1.3em;}h2{font-size:1.2em;}h3{font-size:1.1em;}p{font-size:1.0em;}");
			break;
		case 1:
			s.addRule("h1{font-size:1.4em;}h2{font-size:1.3em;}h3{font-size:1.2em;}p{font-size:1.1em;}");
			break;
		case 2:
			s.addRule("h1{font-size:1.5em;}h2{font-size:1.4em;}h3{font-size:1.3em;}p{font-size:1.2em;}");
			break;
		case 3:
			s.addRule("h1{font-size:1.6em;}h2{font-size:1.5em;}h3{font-size:1.4em;}p{font-size:1.3em;}");
			break;
		}
	}

	public static java.net.URL getHTMLUrl(String path) {

		java.net.URL htmlURL = null;

		try {
			htmlURL = new File(path).toURI().toURL();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		return htmlURL;
	}
}
