package at.jku.ssw.cmm.launcher;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollPane;

public class ScrollBarRepainter implements AdjustmentListener {
	
	public ScrollBarRepainter( JScrollPane scroll ) {
		this.scroll = scroll;
	}
	
	private final JScrollPane scroll;

	@Override
	public void adjustmentValueChanged(AdjustmentEvent arg0) {
		
		scroll.repaint();
	}

}
