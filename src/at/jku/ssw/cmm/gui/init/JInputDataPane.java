package at.jku.ssw.cmm.gui.init;

import java.awt.Dimension;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

public class JInputDataPane extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7416447987376490746L;
	
	boolean wrap = true;

	public JInputDataPane() {
		super();
	}

	public JInputDataPane(boolean wrap) {
		super();
		this.wrap = wrap;
	}

	public JInputDataPane(StyledDocument doc) {
		super(doc);
	}

	public boolean getScrollableTracksViewportWidth() {
		if (wrap)
			return super.getScrollableTracksViewportWidth();
		else
			return false;
	}

	public void setSize(Dimension d) {
		if (!wrap) {
			if (d.width < getParent().getSize().width)
				d.width = getParent().getSize().width;
		}
		super.setSize(d);
	}

	/**
	 * Sets the line-wrapping policy of the JInputDataPane
	 * By default this property is true
	 * @param wrap
	 */
	void setLineWrap(boolean wrap) {
		setVisible(false);
		this.wrap = wrap;
		setVisible(true);
	}

}
