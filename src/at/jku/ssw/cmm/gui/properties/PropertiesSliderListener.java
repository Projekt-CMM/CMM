package at.jku.ssw.cmm.gui.properties;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import at.jku.ssw.cmm.gui.GUImain;

public class PropertiesSliderListener {
	
	public PropertiesSliderListener( GUImain main, GUIProperties master ){
		this.main = main;
		this.master = master;
	}
	
	private final GUImain main;
	private final GUIProperties master;

	public ChangeListener sliderCodeListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider slider = (JSlider)e.getSource();
			main.getSettings().setCodeSize(GUIProperties.sliderPosToFont(slider.getValue()));
			master.updateTextSize();
		}
	};
	
	public ChangeListener sliderTextListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider slider = (JSlider)e.getSource();
			main.getSettings().setTextSize(GUIProperties.sliderPosToFont(slider.getValue()));
			master.updateTextSize();
		}
	};
	
	public ChangeListener sliderVarListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider slider = (JSlider)e.getSource();
			main.getSettings().setVarSize(GUIProperties.sliderPosToFont(slider.getValue()));
			master.updateTextSize();
		}
	};
}
