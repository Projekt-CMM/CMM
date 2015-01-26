package at.jku.ssw.cmm.gui.treetable.example;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TreeTableListener {
	
	public MouseListener mouseListener = new MouseListener(){

		@Override
		public void mouseClicked(MouseEvent arg0) {
			//int row = table.rowAtPoint(arg0.getPoint());
			//int column = table.columnAtPoint(arg0.getPoint());
			System.out.println("Mouse klicked: ");
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			System.out.println("mouseEntered");
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			System.out.println("mouseExited");
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			System.out.println("mousePressed");
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			System.out.println("mouseReleased");
			
		}

		
	};
}
