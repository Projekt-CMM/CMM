package at.jku.ssw.cmm.gui.init;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Popup;
import javax.swing.PopupFactory;

public class StringPopup {

	public static void createPopUp( JComponent cp, String text, int x, int y ){
		
		PopupFactory factory = PopupFactory.getSharedInstance();
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		panel.setPreferredSize(new Dimension(200, 150));
		
		Popup popup = factory.getPopup(cp, panel, 500, 500 );//x + cp.getX(), y + cp.getY());
		
		System.out.println("Position: " + ( x + cp.getX()) + " | " + (y + cp.getY()) );
		panel.addMouseListener(new popupListener( popup ));
		
		JTextArea ta = new JTextArea( text );
		JScrollPane scrollPane = new JScrollPane(ta);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		panel.add(new JLabel("Click here to close popup..."), BorderLayout.PAGE_END);
		
		popup.show();
	}
	
	private static class popupListener implements MouseListener{
		
		public popupListener( Popup popup ){
			this.popup = popup;
		}
		
		private final Popup popup;

		@Override
		public void mouseClicked(MouseEvent e) {
			this.popup.hide();
		}

		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
	}
}
