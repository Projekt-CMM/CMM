package at.jku.ssw.cmm.profile;

import java.io.File;
import java.util.ArrayList;

public class Profile {
	
	//File Names
	private String profilePath;
	private String packagesPath;
	//private String tokensPath;
	
	//File Seperator
	private static String sep = System.getProperty("file.separator");
		
	
	//General Profile Fields
	private String name;
	private int xp;
	
	private String profileFolder;
	
	private ArrayList<Quest> profileQuests;
	
	public Profile(String profilePath, String packagePath){
		this.profilePath = profilePath;
		this.packagesPath = packagePath;
	}
	
	public static Profile ReadProfile(String profilePath, String packagesPath) {
		ArrayList<String> fileNames = Quest.ReadFileNames(profilePath);

			
		return null;
	}
	
	private static Profile ReadProfileXML(){
		
		return null;
	}
	
	
	public static final String
		XML_NAME = "name",
		XML_XP = "xp";
	
	
	
	
	/**
	 * @return the initPath
	 */
	public String getInitPath() {
		return profilePath;
	}
	
	/**
	 * @param initPath the initPath to set
	 */
	public void setInitPath(String initPath) {
		this.profilePath = initPath;
	}
	
	/**
	 * @return the packagesPath
	 */
	public String getPackagesPath() {
		return packagesPath;
	}
	
	/**
	 * @param packagesPath the packagesPath to set
	 */
	public void setPackagesPath(String packagesPath) {
		this.packagesPath = packagesPath;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the xp
	 */
	public int getXp() {
		return xp;
	}
	
	/**
	 * @param xp the xp to set
	 */
	public void setXp(int xp) {
		this.xp = xp;
	}
	
	/**
	 * @return the profileFolder
	 */
	public String getProfileFolder() {
		return profileFolder;
	}
	
	/**
	 * @param profileFolder the profileFolder to set
	 */
	public void setProfileFolder(String profileFolder) {
		this.profileFolder = profileFolder;
	}
	
	/**
	 * @return the profileQuests
	 */
	public ArrayList<Quest> getProfileQuests() {
		return profileQuests;
	}
	
	/**
	 * @param profileQuests the profileQuests to set
	 */
	public void setProfileQuests(ArrayList<Quest> profileQuests) {
		this.profileQuests = profileQuests;
	}
	
	/**
	 * <b>Calculating and returns the Level</b>
	 * @return the Level
	 */
	public int getLevel(){
		return (int) Math.sqrt(xp); //square of 2	
	}



	
}
