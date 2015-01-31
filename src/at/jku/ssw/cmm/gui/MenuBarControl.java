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

public class MenuBarControl {
	
	public MenuBarControl( MenuBarEventListener listener ){
		this.list = new LinkedList<>();
		this.listener = listener;
		this.recentMI = null;
	}
	
	private final List<JMenuItem> list;
	private final MenuBarEventListener listener;
	
	private JMenu recentMI;
	
	private JMenuItem undo;
	private JMenuItem redo;
	
	public void add(JMenuItem mi){
		this.list.add(mi);
	}
	
	public void lockAll(){
		for( JMenuItem mi : this.list ){
			mi.setEnabled(false);
		}
		this.recentMI.setEnabled(false);
	}
	
	public void unlockAll(){
		for( JMenuItem mi : this.list ){
			mi.setEnabled(true);
		}
		this.recentMI.setEnabled(true);
		System.out.println("hello");
	}
	
	public void setRecentMenu( JMenu mi ){
		this.recentMI = mi;
	}
	
	public void updateRecentFiles( List<String> recentFiles, String currentFile ){
		
		if( this.recentMI == null )
			return;
		
		System.out.println("recent: " + recentFiles.size());
		
		if( recentFiles == null || recentFiles.size() <= 0 ){
			this.recentMI.setEnabled(false);
			return;
		}
		
		this.recentMI.removeAll();
		
		for( String s : recentFiles ){
			JMenuItem mi = new JMenuItem(s);
			
			if( s.equals(currentFile) )
				mi.setEnabled(false);
			else
				mi.addActionListener(this.listener.getRecentFileHandler(s));
			
			this.recentMI.add(mi);
		}
	}
	
	public void setUndo(JMenuItem undo){
		this.undo = undo;
	}
	
	public void setRedo(JMenuItem redo){
		this.redo = redo;
	}
	
	public void updateUndoRedo( RSyntaxTextArea tArea, boolean running ){
		this.undo.setEnabled(!running & tArea.canUndo());
		this.redo.setEnabled(!running & tArea.canRedo());
	}
}
