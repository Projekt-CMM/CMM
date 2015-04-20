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
 
package at.jku.ssw.cmm.gui;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.gui.debug.ErrorMessage;
import at.jku.ssw.cmm.gui.debug.ErrorTable;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.file.FileManagerCode;
import at.jku.ssw.cmm.gui.file.LoadStatics;
import at.jku.ssw.cmm.quest.GUITestPanel;

/**
 * This class is responsible for the right panel of the main GUI. The right
 * panel contains a tabbed pane with two tabs:
 * <ul>
 * <li>Debugger panel: Contains control buttons for the debugger and the
 * variable tree table.</li>
 * <li>Quest/Profile info panel: Contains information about the user and his
 * current quest.</li>
 * </ul>
 * Also, the right panel contains basic control elements for the main GUI, eg.
 * the breakpoint button.
 * 
 * @author fabian
 *
 */
public class GUIrightPanel {

	/**
	 * This class is responsible for the right panel of the main GUI. The right
	 * panel contains a tabbed pane with two tabs:
	 * <ul>
	 * <li>Debugger panel: Contains control buttons for the debugger and the
	 * variable tree table.</li>
	 * <li>Quest/Profile info panel: Contains information about the user and his
	 * current quest.</li>
	 * </ul>
	 * Also, the right panel contains basic control elements for the main GUI,
	 * eg. the breakpoint button.
	 */
	public GUIrightPanel(GUImain main) {
		this.main = main;
	}
	
	/**
	 * A reference to the main GUI
	 */
	private final GUImain main;
	
	/**
	 * The tabbed panel which contains all sub-panels of the right panel
	 */
	private JTabbedPane tabbedPane;
	
	/**
	 * A reference to the debug panel.
	 */
	private GUIdebugPanel debugPanel;
	
	/**
	 * This object contains a map which links an error message to an error
	 * description file.
	 */
	private ErrorTable errorMap;
	
	/**
	 * A reference to the quest/profile info panel.
	 */
	private ProfilePanel2 questPanel;
	
	/**
	 * A reference to the panel controller for quest success tests
	 */
	private GUITestPanel testPanel;

	/**
	 * A reference to the error description panel
	 */
	private JPanel errorPanel;
	
	/**
	 * The text area showing error descriptions
	 */
	private JEditorPane errorDesc;
	
	/**
	 * An additional text field below the error description which
	 * shwos the original error message
	 */
	private JTextField errorMsg;
	
	/**
	 * This panel visualizes the current quest state.
	 * It is only visible when C Compact is launched with a user profile.
	 */
	private JPanel jStatePanel;
	
	/**
	 * This label prints the name of the current quest state onto the
	 * quest state panel
	 */
	private JLabel jStateLabel;

	/**
	 * Initializes the right panel of the main GUI
	 * 
	 * @return The right panel
	 */
	public JPanel init() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		// Profile state panel
		if( main.hasAdvancedGUI() ){
			this.jStatePanel = new JPanel();
			this.jStatePanel.setBorder(BorderFactory.createLoweredBevelBorder());
			
			this.jStatePanel.setMinimumSize(new Dimension(20, 30));
			this.jStatePanel.setPreferredSize(new Dimension(30, 30));
			this.jStatePanel.setMaximumSize(new Dimension(8000, 30));
			this.jStateLabel = new JLabel();
			this.jStatePanel.add(this.jStateLabel);
			this.setIdleMode();
			
			JPanel wrapper = new JPanel();
			wrapper.setBorder(new EmptyBorder(5, 5, 0, 5));
			wrapper.setLayout(new BorderLayout());
			wrapper.add(this.jStatePanel, BorderLayout.CENTER);
			
			panel.add(wrapper, BorderLayout.PAGE_START);
		}
		
		// Tabbed Pane
		tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Initialize Debug Panel
		JPanel jDebugPanel = new JPanel();
		jDebugPanel.setLayout(new BorderLayout());
		debugPanel = new GUIdebugPanel(jDebugPanel, main);
		tabbedPane.add(jDebugPanel, _("Debug"));

		// Initialize error panel
		this.errorMap = new ErrorTable(main.getJFrame(), main.getSettings().getLanguage());
		this.errorPanel = new JPanel();
		this.errorPanel.setLayout(new BorderLayout());

		this.errorDesc = new JEditorPane();
		this.errorDesc.setEditable(false);
		this.errorDesc.setContentType("text/html");

		JScrollPane editorScrollPane = new JScrollPane(this.errorDesc);
		editorScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		editorScrollPane.setPreferredSize(new Dimension(100, 300));
		editorScrollPane.setMinimumSize(new Dimension(10, 10));
		
		this.errorPanel.add(editorScrollPane, BorderLayout.CENTER);
		
		this.errorMsg = new JTextField();
		this.errorMsg.setEditable(false);
		
		this.errorPanel.add(this.errorMsg, BorderLayout.PAGE_END);

		// Initialize Quest Panel
		if (main.hasAdvancedGUI()) {
			this.testPanel = new GUITestPanel(main);
			JPanel jTestPanel = new JPanel();
			this.testPanel.init(jTestPanel);
			tabbedPane.add(jTestPanel, _("Quest"));
			
			
			questPanel = new ProfilePanel2(main);
			tabbedPane.add(questPanel.getProfilePanel(), _("Profile"), 2);
			
			//Opening last Used Quest
			main.getSettings().updateDescPane(testPanel);
		}
		
		tabbedPane.setMinimumSize(new Dimension(250, 400));
		
		panel.add(tabbedPane, BorderLayout.CENTER);

		return panel;
	}
	
	/**
	 * @return A reference to the debug panel manager
	 */
	public GUIdebugPanel getDebugPanel() {
		return this.debugPanel;
	}

	/**
	 * @return A reference to the quest/profile info panel manager
	 */
	public ProfilePanel2 getProfilePanel() {
		return this.questPanel;
	}
	
	/**
	 * @return A reference to the quest test panel manager
	 */
	public GUITestPanel getTestPanel(){
		return this.testPanel;
	}

	/**
	 * Enables the error panel and shows the error message for the given error
	 * code.
	 * 
	 * @param errorCode The error code from the compiler, preprocessor or
	 * 			interpreter. If there is no matching error description for the
	 * 			given code, a default message is shown.
	 * @return The title of the error. If there is no title specified in the head
	 * 			of the error document, the headline is taken
	 */
	public String[] showErrorPanel(String errorCode) {

		javax.swing.text.Document style = null;
		try {
			style = LoadStatics.readStyleSheet("error"
					+ File.separator + "style.css", main.getSettings().getDescSize());
		} catch (MalformedURLException e) {
			new ErrorMessage().showErrorMessage(this.main.getJFrame(), "#1011", this.main.getSettings().getLanguage());
		}
		
		if( style == null )
			new ErrorMessage().showErrorMessage(this.main.getJFrame(), "#1012", this.main.getSettings().getLanguage());
		this.errorDesc.setDocument(style);
		
		String desc = null;
		try {
			desc = FileManagerCode.readInputDataBlank(new File(
					this.errorMap == null ? null : this.errorMap
					.getErrorHTML(errorCode)));
			this.errorDesc.setContentType("text/html");
			desc = desc.replace("<errormessage/>", errorCode);
			desc = desc.replace("<errormessage>", errorCode);
			this.errorDesc.setText(desc);
		} catch (IOException e) {
			new ErrorMessage().showErrorMessage(this.main.getJFrame(), "#1021", this.main.getSettings().getLanguage());
			this.errorDesc.setText("#1021: Error message not found");
		}

		if (this.tabbedPane.indexOfTab(_("Error")) == -1) {
			this.tabbedPane.add(errorPanel, _("Error"), 1);
		}
		
		this.errorMsg.setText(errorCode);

		this.tabbedPane.setSelectedIndex(1);
		
		String[] result = new String[2];
		
		if(desc == null)
			return result;
		
		if(desc.contains("<postfix>"))
			result[1] = desc.substring(desc.indexOf("<postfix>")+9, desc.indexOf("</postfix>"));
		
		if(desc.contains("<prefix>"))
			result[0] = desc.substring(desc.indexOf("<prefix>")+8, desc.indexOf("</prefix>"));
		
		else for( int i = 1; i <= 8; i++ ) {
			if(desc.contains("<h" + i + ">")) {
				result[0] = desc.substring(desc.indexOf("<h" + i + ">")+4, desc.indexOf("</h" + i + ">"));
				break;
			}
		}
		
		return result;
	}

	/**
	 * Hides the error panel
	 */
	public void hideErrorPanel() {
		if( tabbedPane.indexOfTab(_("Error")) != -1 )
		tabbedPane.remove(tabbedPane.indexOfTab(_("Error")));
	}
	
	/**
	 * Sets the quest state panel to success mode
	 */
	public void setSuccessMode() {
		if(main.hasAdvancedGUI()) {
			this.jStatePanel.setBackground(new Color(0x92FC9B));
			this.jStateLabel.setText(_("test successful"));
		}
	}
	
	/**
	 * Sets the quest state panel to fail mode
	 */
	public void setFailedMode() {
		if(main.hasAdvancedGUI()) {
			this.jStatePanel.setBackground(new Color(255, 131, 131));
			this.jStateLabel.setText(_("test failed"));
		}
	}
	
	/**
	 * Sets the quest state panel to test mode
	 */
	public void setTestMode() {
		if(main.hasAdvancedGUI()) {
			this.jStatePanel.setBackground(new Color(0xEFDD1E));
			this.jStateLabel.setText(_("testing current quest"));
		}
	}
	
	/**
	 * Sets the quest state panel to idle mode
	 */
	public void setIdleMode() {
		if(main.hasAdvancedGUI()) {
			this.jStatePanel.setBackground(Color.LIGHT_GRAY);
			this.jStateLabel.setText(_("no quest selected"));
		}
	}
	
	/**
	 * Sets the quest state panel to quest mode
	 * 
	 * @param title The title of the current quest
	 */
	public void setQuestMode(String title) {
		if(main.hasAdvancedGUI()) {
			this.jStatePanel.setBackground(new Color(0x2BC6C6));
			this.jStateLabel.setText(_("current quest") + ": " + title);
		}
	}
}
