package at.jku.ssw.cmm.gui.init;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;

public class MenuBarVisToggle {

	public MenuBarVisToggle(){
		this.componentList = new ArrayList<>();
	}
	
	private final List<JMenuItem> componentList;
	
	public void registerComponent( JMenuItem c ){
		this.componentList.add(c);
	}
	
	public void disable( int index ){
		
		for( JMenuItem mi : this.componentList )
			mi.setEnabled(true);
		
		this.componentList.get(index).setEnabled(false);
	}
}
