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
	}
	
	public void unlockAll(){
		for( JMenuItem mi : this.list ){
			mi.setEnabled(true);
		}
	}
	
	public void setRecentMenu( JMenu mi ){
		this.recentMI = mi;
	}
	
	public void updateRecentFiles( List<String> recentFiles, String currentFile ){
		
		if( this.recentMI == null )
			return;
		
		if( recentFiles == null || recentFiles.size() <= 0 ){
			this.recentMI.setEnabled(false);
			return;
		}
		
		this.recentMI.setEnabled(true);
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
	
	public void updateUndoRedo( RSyntaxTextArea tArea ){
		this.undo.setEnabled(tArea.canUndo());
		this.redo.setEnabled(tArea.canRedo());
	}
}
