package at.jku.ssw.cmm.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class VarTableVisToggle {
	
	@SuppressWarnings("unchecked")
	public VarTableVisToggle(){
		this.componentList = new List[2];
		this.componentList[0] = new ArrayList<>();
		this.componentList[1] = new ArrayList<>();
	}
	
	private final List<JComponent>[] componentList;
	
	public void registerComponent( int mode, JComponent c ) {
		
		try{
			this.componentList[mode].add(c);
		}catch( IndexOutOfBoundsException e ){
			System.out.println( "[error] Could not register component \"" + c + "\", error: " + e );
			return;
		}catch( NullPointerException e ){
			System.out.println( "[error] Null Pointer Exception, error: " + e );
			return;
		}
	}
	
	public void setVisible( int mode ){
		
		try{
			for( List<JComponent> list : this.componentList ){
				for( JComponent c : list ){
					c.setVisible(false);
				}
			}
			for( JComponent c : this.componentList[mode] ){
				c.setVisible(true);
			}
		}catch( IndexOutOfBoundsException e ){
			System.out.println( "[error] Invalid mode, error: " + e );
			return;
		}
	}
}
