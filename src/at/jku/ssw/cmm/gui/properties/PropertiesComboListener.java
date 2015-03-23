package at.jku.ssw.cmm.gui.properties;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import at.jku.ssw.cmm.gui.GUImain;

public class PropertiesComboListener implements ActionListener {

	public PropertiesComboListener(GUImain main) {
		this.main = main;
	}
	
	private final GUImain main;
	
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.main.getLeftPanel().setOrientation(((JComboBox<String>)(arg0.getSource())).getSelectedIndex());
	}

}
