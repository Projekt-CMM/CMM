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
 
package at.jku.ssw.cmm.gui.init;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;

import at.jku.ssw.cmm.gui.properties.GUImainSettings;

/**
 * Class containing static methods to initialize the left part of the main GUI. This includes functions to
 * initialize the syntax text field, the input text field and the output text field as well as labels
 * and additional swing elements. 
 * 
 * @author fabian
 * 
 */
public class InitLeftPanel {
	
	/**
	 * Initializes the text area for the source code in the main GUI.
	 * 
	 * @param pane The main panel for the GUI (JPanel)
	 * @param settings The settings class which contains the config data of the window.
	 * @return A reference to the text area generated (RSyntaxTextArea)
	 */
	public static RSyntaxTextArea initCodePane( JPanel pane, GUImainSettings settings ){
		
		RSyntaxTextArea textArea = new RSyntaxTextArea();
		
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/ccompact", "at.jku.ssw.cmm.gui.init.CCompactTokenMaker");
		textArea.setSyntaxEditingStyle("text/ccompact");
		
	    textArea.setCodeFoldingEnabled(true);
	    textArea.setFont(textArea.getFont().deriveFont((float)20.0));
	    textArea.setAntiAliasingEnabled(true);
	    
	    //textArea.setFont(textArea.getFont().deriveFont((float)20.0));
	    
	    //textArea.setToolTipText("<html><b>Title</b><br>description<br>description2<br><i>italic text</i></html>");
	    RTextScrollPane sp = new RTextScrollPane(textArea);
	    sp.setFoldIndicatorEnabled(true);
	    pane.add(sp);
        
        return textArea;
	}
	
	/**
	 * Initializes the text area for the program <b>input</b> stream in the main GUI.
	 * 
	 * @param pane The main panel for the GUI (JPanel)
	 * @return A reference to the JTextArea generated
	 */
	public static JInputDataPane initInputPane( JPanel pane ){
		
		//Label saying "input" above the text area for the input stream
		JPanel valuePanel = new JPanel();
		valuePanel.setBorder(BorderFactory.createTitledBorder(_("Input")));
		valuePanel.setLayout(new BorderLayout());
        
		//The text area for the input stream
        JInputDataPane tArea = new JInputDataPane();
        tArea.setLineWrap(false);
        tArea.setLayout(new BorderLayout());
        tArea.setMargin(new Insets(5, 5, 5, 5));
        tArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        //tArea.setFont(tArea.getFont().deriveFont((float)20.0));
        
        JScrollPane scroll = new JScrollPane(tArea);
        
        valuePanel.add(scroll, BorderLayout.CENTER);
        pane.add(valuePanel);
        
        return tArea;
	}
	
	/**
	 * Initializes the text area for the program <b>output</b> stream in the main GUI.
	 * 
	 * @param pane The main panel for the GUI (JPanel)
	 * @return A reference to the JTextArea generated
	 */
	public static JTextArea initOutputPane( JPanel pane ){
		
		//Label saying "output" above the text area for the output stream
		JPanel valuePanel = new JPanel();
		valuePanel.setBorder(BorderFactory.createTitledBorder(_("Output")));
		valuePanel.setLayout(new BorderLayout());
        
		//The text area for the output stream
        JTextArea tArea = new JTextArea();
        tArea.setLayout(new BorderLayout());
        tArea.setMargin(new Insets(5, 5, 5, 5));
        tArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        //tArea.setFont(tArea.getFont().deriveFont((float)20.0));
        
        JScrollPane scroll = new JScrollPane(tArea);
        
        valuePanel.add(scroll, BorderLayout.CENTER);
        pane.add(valuePanel);
        
        return tArea;
	}
}
