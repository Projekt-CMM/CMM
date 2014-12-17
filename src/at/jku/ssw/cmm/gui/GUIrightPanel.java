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

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.gui.debug.ErrorTable;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.utils.LoadStatics;
import at.jku.ssw.cmm.quest.TestPanel;

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
	
	private JTabbedPane tabbedPane;
	
	/**
	 * A reference to the debug panel.
	 */
	private GUIdebugPanel debugPanel;
	
	private ErrorTable errorMap;
	
	/**
	 * A reference to the quest/profile info panel.
	 */
	private GUIProfilePanel questPanel;
	
	private TestPanel testPanel;

	private JPanel errorPanel;
	private JEditorPane errorDesc;
	
	private JTextField errorMsg;
	
	private final GUImain main;
	
	private JPanel jStatePanel;
	private JLabel jStateLabel;

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
			this.setSuccessMode();
			
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
		this.errorMap = new ErrorTable(main.getSettings().getLanguage());
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
		JPanel jQuestPanel = new JPanel();
		if (main.hasAdvancedGUI()) {
			this.testPanel = new TestPanel(main);
			JPanel jTestPanel = new JPanel();
			this.testPanel.init(jTestPanel);
			tabbedPane.add(jTestPanel, _("Quest"));
			
			
			jQuestPanel.setLayout(new BorderLayout());
			//tabbedPane.add(jQuestPanel, _("Profile"));
			questPanel = new GUIProfilePanel(jQuestPanel, main);
		}
		
		tabbedPane.setMinimumSize(new Dimension(250, 400));
		tabbedPane.setPreferredSize(new Dimension(260, 420));
		
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
	public GUIProfilePanel getProfilePanel() {
		return this.questPanel;
	}
	
	public TestPanel getTestPanel(){
		return this.testPanel;
	}

	public void showErrorPanel(String html) {
		
		System.out.println("Error: " + html);

		this.errorDesc.setDocument(LoadStatics.readStyleSheet("error"
				+ File.separator + "style.css"));
		
		try {
			this.errorDesc.setPage(LoadStatics.getHTMLUrl(this.errorMap
					.getErrorHTML(html)));
		} catch (IOException e) {
			DebugShell.out(State.ERROR, Area.ERROR, html + " not found");
			e.printStackTrace();
		}

		if (this.tabbedPane.getTabCount() == (main.hasAdvancedGUI() ? 2 : 1))
			this.tabbedPane.add(errorPanel, _("Error"), 1);
		
		this.errorMsg.setText(html);

		this.tabbedPane.setSelectedIndex(1);
	}

	public void hideErrorPanel() {
		if (this.tabbedPane.getTabCount() == (main.hasAdvancedGUI() ? 3 : 2))
			tabbedPane.remove(1);
	}
	
	public void setSuccessMode() {
		if(main.hasAdvancedGUI()) {
			this.jStatePanel.setBackground(new Color(0x92FC9B));
			this.jStateLabel.setText(_("test successful"));
		}
	}
	
	public void setFailedMode() {
		if(main.hasAdvancedGUI()) {
			this.jStatePanel.setBackground(new Color(255, 131, 131));
			this.jStateLabel.setText(_("test failed"));
		}
	}
	
	public void setTestMode() {
		if(main.hasAdvancedGUI()) {
			this.jStatePanel.setBackground(new Color(0xEFDD1E));
			this.jStateLabel.setText(_("testing current quest"));
		}
	}
	
	public void setIdleMode() {
		if(main.hasAdvancedGUI()) {
			this.jStatePanel.setBackground(Color.LIGHT_GRAY);
			this.jStateLabel.setText(_("no quest selected"));
		}
	}
	
	public void setQuestMode(String title) {
		if(main.hasAdvancedGUI()) {
			this.jStatePanel.setBackground(new Color(0x2BC6C6));
			this.jStateLabel.setText(_("current quest") + ": " + title);
		}
	}
}
