package at.jku.ssw.cmm.gui.properties;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import at.jku.ssw.cmm.gui.GUImain;

public class PropertiesActionListener implements ActionListener {

	public PropertiesActionListener( GUImain main ) {
		this.main = main;
	}
	
	private final GUImain main;
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		JCheckBox check = (JCheckBox)arg0.getSource();
		this.main.getSettings().setShowReturn(check.isSelected());
	}
}
