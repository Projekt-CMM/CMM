package at.jku.ssw.cmm.gui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;

public class MenuBarControl {
	
	public MenuBarControl(){
		this.list = new LinkedList<>();
	}
	
	private final List<JMenuItem> list;
	
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
}
