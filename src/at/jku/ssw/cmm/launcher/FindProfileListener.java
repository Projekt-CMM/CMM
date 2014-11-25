package at.jku.ssw.cmm.launcher;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import at.jku.ssw.cmm.gui.GUImain;

public class FindProfileListener implements MouseListener{

private JFrame jFrame;
	

	public FindProfileListener( JFrame jFrame) {
		this.jFrame = jFrame;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		try {
			GUIProfileManager.selectProfile();
			jFrame.dispose();
			String[] a = { "" };
			GUImain.main(a);
			
		} catch (ProfileSelectionException e) {
			
		}

		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
