/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
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
	
	public ChangeListener sliderDescListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider slider = (JSlider)e.getSource();
			main.getSettings().setDescSize(slider.getValue()-1);
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
