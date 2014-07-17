package at.jku.ssw.cmm.gui.event.panel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;

import at.jku.ssw.cmm.gui.GUIrightPanel;

public class PanelRunStackListener {

	public PanelRunStackListener( GUIrightPanel master, JList<Object> jCallStack2 ){
		this.master = master;
		this.jCallStack = jCallStack2;
	}
	
	private final GUIrightPanel master;
	private final JList<Object> jCallStack;
	
	//Call stack listener
	public MouseListener jCallStackListener = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			
			System.out.println("Selected: " + jCallStack.getSelectedIndex() + " - " + jCallStack.getSelectedValue() );
			master.selectFunction((String)jCallStack.getSelectedValue(), jCallStack.getSelectedIndex());
		}

		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	};
}
