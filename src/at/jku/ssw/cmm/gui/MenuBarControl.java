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

import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import at.jku.ssw.cmm.gui.event.MenuBarEventListener;

/**
 * This class controlls the interaction of different parts of the GUI with the
 * menu bar of the main GUI.
 * 
 * @author fabian
 */
public class MenuBarControl {
	
	/**
	 * This class controlls the interaction of different parts of the GUI with the
	 * menu bar of the main GUI.
	 * 
	 * @param listener The event listener of the main GUI menu bar
	 */
	public MenuBarControl( MenuBarEventListener listener ){
		this.list = new LinkedList<>();
		this.listener = listener;
		this.recentMI = new JMenu();
	}
	
	/**
	 * A reference to the main GUI menu bar event listener
	 */
	private final MenuBarEventListener listener;
	
	/**
	 * The submenu containing the recently opened files
	 */
	private JMenu recentMI;
	
	/**
	 * All menu items in this list can be locked or unlocked together.
	 * This list is used to lock all operations which shall not be possible
	 * during debugging (eg. "open new file")
	 */
	private final List<JMenuItem> list;
	
	/**
	 * The menu item for "undo" perations in the source code
	 */
	private JMenuItem undo;
	
	/**
	 * The menu item for "redo" operations in the source code
	 */
	private JMenuItem redo;
	
	/**
	 * Adds the given menu item to the list of those items, which
	 * are locked during debugging
	 * 
	 * @param The menu item to be added to the list
	 */
	public void add(JMenuItem mi){
		this.list.add(mi);
	}
	
	/**
	 * Lock all listed menu items.
	 * This method should be called when the debugger is starting
	 */
	public void lockAll(){
		for( JMenuItem mi : this.list ){
			mi.setEnabled(false);
		}
		if( this.recentMI != null )
			this.recentMI.setEnabled(false);
	}
	
	/**
	 * Unlock all listed items.
	 * This method should be called when the debugger has stopped
	 */
	public void unlockAll(){
		for( JMenuItem mi : this.list ){
			mi.setEnabled(true);
		}
		if( this.recentMI != null )
			this.recentMI.setEnabled(true);
	}
	
	/**
	 * Replace the current menu for the recently opened files
	 * with the menu given as parameter
	 * 
	 * @param mi The new "recent files" menu
	 */
	public void setRecentMenu( JMenu mi ){
		this.recentMI = mi;
	}
	
	/**
	 * Update the list of current files in the menubar with the given
	 * list of recent files
	 * 
	 * @param recentFiles The list of recent files
	 * @param currentFile The latest file which is currently being edited.
	 * 			This file is not selectable in the menu, as it is already opened
	 */
	public void updateRecentFiles( List<String> recentFiles, String currentFile ){
		
		// Cancel if there is no menu for recent files
		if( this.recentMI == null )
			return;
		
		// Disable recent files menu if there are no recent files
		if( recentFiles.size() <= 0 ){
			this.recentMI.setEnabled(false);
			return;
		}
		
		// Clear the list of recent files in order to reinitialize it (see code below)
		this.recentMI.removeAll();
		
		// Add the recent files from the new list to the recent files menu
		for( String s : recentFiles ){
			JMenuItem mi = new JMenuItem(s);
			
			// Check wheather the file is already listed
			if( s.equals(currentFile) )
				mi.setEnabled(false);
			else
				mi.addActionListener(this.listener.getRecentFileHandler(s));
			
			// Add file name to the list
			this.recentMI.add(mi);
		}
	}
	
	/**
	 * Saves the reference to the "undo" menu item in order to disable
	 * it if there is nothing to undo
	 * 
	 * @param undo The "undo" menu item
	 */
	public void setUndo(JMenuItem undo){
		this.undo = undo;
	}
	
	/**
	 * Saves the reference to the "redo" menu item in order to disable
	 * it if there is nothing to redo
	 * 
	 * @param redo The "redo" menu item
	 */
	public void setRedo(JMenuItem redo){
		this.redo = redo;
	}
	
	/**
	 * Updates the und and redo menu items in the menubar of the main GUI
	 * 
	 * @param tArea The text area containing the source code
	 * @param running TRUE if debugger is running, otherwise FALSE
	 */
	public void updateUndoRedo( RSyntaxTextArea tArea, boolean running ){
		this.undo.setEnabled(!running & tArea.canUndo());
		this.redo.setEnabled(!running & tArea.canRedo());
	}
}
