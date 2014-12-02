package at.jku.ssw.cmm.gui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

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
	
	public void updateRecentFiles( List<String> recentFiles ){
		
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
			this.recentMI.add(mi);
			//TODO nested class
		}
	}
}
