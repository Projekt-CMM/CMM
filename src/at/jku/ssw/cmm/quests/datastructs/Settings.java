package at.jku.ssw.cmm.quests.datastructs;

public class Settings {
	private int background;
	private int sounds;
	private int color_picker;
	private int auto_complete;
	private int spell_checking;
	public static boolean debug = false;
	
	
	/**
	 * @return the background
	 */
	public int getBackground() {
		return background;
	}
	/**
	 * @param background the background to set
	 */
	public void setBackground(int background) {
		this.background = background;
	}
	/**
	 * @return the sounds
	 */
	public int getSounds() {
		return sounds;
	}
	/**
	 * @param sounds the sounds to set
	 */
	public void setSounds(int sounds) {
		this.sounds = sounds;
	}
	/**
	 * @return the color_picker
	 */
	public int getColor_picker() {
		return color_picker;
	}
	/**
	 * @param color_picker the color_picker to set
	 */
	public void setColor_picker(int color_picker) {
		this.color_picker = color_picker;
	}
	/**
	 * @return the auto_complete
	 */
	public int getAuto_complete() {
		return auto_complete;
	}
	/**
	 * @param auto_complete the auto_complete to set
	 */
	public void setAuto_complete(int auto_complete) {
		this.auto_complete = auto_complete;
	}
	/**
	 * @return the spell_checking
	 */
	public int getSpell_checking() {
		return spell_checking;
	}
	/**
	 * @param spell_checking the spell_checking to set
	 */
	public void setSpell_checking(int spell_checking) {
		this.spell_checking = spell_checking;
	}
	/**
	 * @return the debug
	 */
	public static boolean isDebug() {
		return debug;
	}
	/**
	 * @param debug the debug to set
	 */
	public static void setDebug(boolean debug) {
		Settings.debug = debug;
	}
	
	
	
}
