package at.jku.ssw.cmm.gui;

import at.jku.ssw.cmm.gui.properties.GUImainSettings;

public interface GUIExecutable {
	public void start(boolean test);
	public GUImainSettings getSettings();
	public void saveAndDispose();
}
