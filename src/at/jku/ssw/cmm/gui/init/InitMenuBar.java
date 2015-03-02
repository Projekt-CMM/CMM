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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.MenuBarControl;
import at.jku.ssw.cmm.gui.debug.GUIcontrolPanel;
import at.jku.ssw.cmm.gui.event.MenuBarEventListener;

/**
 * Contains a static method to initialize the menu bar and its drop-down menus for the main GUI
 * 
 * @author fabian
 *
 */
public class InitMenuBar {
	
	/**
	 * This method should only be called when the main GUI is initialized.
	 * It inits the main GUI's menu bar, including drop-down menus and adds event listeners
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param jFrame The main GUI window frame
	 * @param jSourcePane A reference to the text area containing the source code
	 * @param settings A reference to the main GUI's configuration object
	 * @param saveDialog A reference to the save dialog manager initialized with the main GUI
	 * @param profile A reference to the profile
	 */
	public static void initFileM( JFrame jFrame, GUImain main, MenuBarControl menuBarControl, MenuBarEventListener listener, GUIcontrolPanel ctrl){
		
		
		
		//Initialize MenuBar
		JMenuBar menubar = new JMenuBar();
		jFrame.setJMenuBar(menubar);
		
		/* --- MENU: "file" --- */
		JMenu fileM = new JMenu(_("File"));
		menubar.add(fileM);
			
			// --- file -> new ---
			JMenuItem newMI = new JMenuItem(_("New"));
			newMI.addActionListener(listener.newFileHandler);
			newMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
			fileM.add(newMI);
			menuBarControl.add(newMI);
		
			// --- file -> open ---
			JMenuItem openMI = new JMenuItem(_("Open"));
			fileM.add(openMI);
			openMI.addActionListener(listener.openHandler);
			openMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			menuBarControl.add(openMI);
			
			// --- file -> recent files ---
			if( !main.hasAdvancedGUI() ){
				JMenu recentMI = new JMenu(_("Recent files"));
				fileM.add(recentMI);
				menuBarControl.add(recentMI);
				menuBarControl.setRecentMenu(recentMI);
			}
			
			fileM.addSeparator();
			
			// --- file -> save as ---
			JMenuItem saveAsMI = new JMenuItem(_("Save As..."));
			fileM.add(saveAsMI);
			saveAsMI.addActionListener(listener.saveAsHandler);
			saveAsMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
			menuBarControl.add(saveAsMI);
			
			// --- file -> save ---
			JMenuItem saveMI = new JMenuItem(_("Save..."));
			fileM.add(saveMI);
			saveMI.addActionListener(listener.saveHandler);
			saveMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			menuBarControl.add(saveMI);
			
			fileM.addSeparator();
			
			// --- edit -> properties ---
			JMenuItem propertiesMI = new JMenuItem(_("Properties"));
			propertiesMI.addActionListener(listener.propertiesHandler);
			fileM.add(propertiesMI);
			
			// --- file -> credits ---
			JMenuItem creditsMI = new JMenuItem(_("About C Compact"));
			creditsMI.addActionListener(listener.creditsHandler);
			fileM.add(creditsMI);
						
			fileM.addSeparator();
		
			// --- file -> exit ---
			JMenuItem exitMI = new JMenuItem(_("Exit"));
			exitMI.addActionListener(listener.exitHandler);
			exitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
			fileM.add(exitMI);
			
		/* --- MENU: "source code" --- */
		JMenu codeM = new JMenu(_("Source code"));
		menubar.add(codeM);
		
			// --- edit -> undo ---
			JMenuItem undoMI = new JMenuItem(_("Undo"));
			undoMI.addActionListener(listener.undoHandler);
			undoMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
			codeM.add(undoMI);
			menuBarControl.setUndo(undoMI);
					
			// --- edit -> redo ---
			JMenuItem redoMI = new JMenuItem(_("Redo"));
			redoMI.addActionListener(listener.redoHandler);
			redoMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
			codeM.add(redoMI);
			menuBarControl.setRedo(redoMI);
			
			codeM.addSeparator();
			
			// --- edit -> run ---
			JMenuItem runMI = new JMenuItem(_("compile and run"));
			runMI.addActionListener(ctrl.getListener().F5_run);
			runMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
			codeM.add(runMI);
						
			// --- edit -> step ---
			JMenuItem stepMI = new JMenuItem(_("compile and step"));
			stepMI.addActionListener(ctrl.getListener().F6_step);
			stepMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
			codeM.add(stepMI);
						
			// --- edit -> stop ---
			JMenuItem stopMI = new JMenuItem(_("stop"));
			stopMI.addActionListener(ctrl.getListener().F7_stop);
			stopMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
			codeM.add(stopMI);
			
			ctrl.initMenuItems(runMI, stepMI, stopMI);
		
		/* --- MENU: "progress" --- */
		if( main.hasAdvancedGUI() ){
			JMenu questM = new JMenu(_("Progress"));
			menubar.add(questM);
		
			// --- progress -> profile ---
			JMenuItem profileMI = new JMenuItem(_("Select Profile"));
			profileMI.addActionListener(listener.profileHandler);
			profileMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
			questM.add(profileMI);
			
			// --- progress -> export profile ---
			JMenuItem profileExportMI = new JMenuItem(_("Export Profile"));
			profileExportMI.addActionListener(listener.profileExportHandler);
			questM.add(profileExportMI);
			
			questM.addSeparator();
					
			// --- progress -> quests ---
			JMenuItem questMI = new JMenuItem(_("Select Quest"));
			questMI.addActionListener(listener.questHandler);
			questMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
			questM.add(questMI);
			
			// --- progress -> quests ---
			JMenuItem questImportMI = new JMenuItem(_("Import Quest Package"));
			questImportMI.addActionListener(listener.questImportHandler);
			questM.add(questImportMI);
			
		}
	}
}
