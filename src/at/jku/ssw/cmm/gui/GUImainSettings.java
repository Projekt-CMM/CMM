package at.jku.ssw.cmm.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;

/**
 * Contains configuration data for the main GUI. The main GUI has a reference to an object of this class.
 * The configuration is saved in the "config.cfg" file in the program's working directory.
 * For loading the config data, see the method readConfigFile below.
 * For saving config data, see {@link WindowEventListener}
 * <br>
 * The config data includes variables like:
 * <ul>
 * <li> screen resolution </li>
 * <li> directory of the current c-- file </li>
 * </ul>
 * 
 * @author fabian
 *
 */
public class GUImainSettings {
	
	/**
	 * Contains configuration data for the main GUI.
	 */
	public GUImainSettings(){
		this.readConfigFile();
	}
	
	//Size of the main GUI window
	private int sizeX;
	private int sizeY;
	
	//Position of the main GUI window
	private int posX;
	private int posY;
	
	//Path of the current c-- file (null if no current file available)
	private String path;
	
	//Constant variable for the minimum width of the right panel
	private static final int RIGHT_OFFSET = 150;
	
	/**
	 * NOTE: The position is not actualized by events, as this is not necessary at the moment.
	 * 
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @return The current x position of the main GUI window, according to the configuration object.
	 */
	public int getSourceSizeX(){
		return (int)( (this.sizeX-RIGHT_OFFSET) * 0.0857 );
	}
	
	/**
	 * NOTE: The position is not actualized by events, as this is not necessary at the moment.
	 * 
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @return The current x position of the main GUI window, according to the configuration object.
	 */
	public int getSourceSizeY(){
		return (int)(this.sizeY*0.0357);
	}
	
	/**
	 * NOTE: This method does <b>not</b> change the actual size of any graphical object.
	 * It is considered for actualizing configuration data basing on the current window parameters.
	 * See {@link WindowComponentListener}
	 * 
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @param x The current width of the main GUI window.
	 */
	public void setSizeX( int x ){
		this.sizeX = x;
	}
	
	/**
	 * NOTE: This method does <b>not</b> change the actual size of any graphical object.
	 * It is considered for actualizing configuration data basing on the current window parameters.
	 * See {@link WindowComponentListener}
	 * 
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @param y The current height of the main GUI window.
	 */
	public void setSizeY( int y ){
		this.sizeY = y;
	}
	
	/**
	 * NOTE: This method does <b>not</b> change the actual size of any graphical object.
	 * It is considered for actualizing configuration data basing on the current window parameters.
	 * 
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @param x The current x position of the main GUI window.
	 */
	public void setPosX( int x ){
		this.posX = x;
	}
	
	/**
	 * NOTE: This method does <b>not</b> change the actual size of any graphical object.
	 * It is considered for actualizing configuration data basing on the current window parameters.
	 * 
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @param y The current y position of the main GUI window.
	 */
	public void setPosY( int y ){
		this.posY = y;
	}
	
	/**
	 * Set the path of the current c-- file.
	 * 
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @param p The new path of the c-- file. Loading null or "*" means
	 * that no working directory is registered.
	 */
	//TODO profile
	public void setPath( String p ){
		if( p == "#" || p == null )
			this.path = null;
		else{
			this.path = p.endsWith(".cmm") ? p : p + ".cmm";
		}
	}
	
	/**
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @return The path of the current c-- file.
	 */
	//TODO profile
	public String getPath(){
		return this.path;
	}
	
	/**
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @return TRUE if the config contains a working path for the current c-- file
	 * 		<br> FALSE if the working path is null or "*"
	 */
	//TODO profile
	public boolean hasPath(){
		if( this.path == null )
			return false;
		return true;
	}
	
	/**
	 * <ul>
	 * <li> Opens the file "config.cfg" in the program's working directory. </li>
	 * <li> Reads the config file and locally saves configurations. </li>
	 * <li> Creates a new config file with standard config if "config.cfg" not found. </li>
	 * </ul>
	 * <hr><i>THREAD SAFE by default</i><hr>
	 */
	private void readConfigFile(){
		
		BufferedReader file;
		System.out.println("Searching for config file...");
		
		try {
			
			file = new BufferedReader(new FileReader("config.cfg"));
			
			this.sizeX = Integer.parseInt(file.readLine());
			this.sizeY = Integer.parseInt(file.readLine());
			this.posX  = Integer.parseInt(file.readLine());
			this.posY  = Integer.parseInt(file.readLine());
			
			String path = file.readLine();
			if( path.endsWith("#") )
				this.setPath("#");
			else
				this.setPath(path);
			
			file.close();
			
			System.out.println("Config is: " + this.sizeX + " | " + this.sizeY + " | " + this.posX + " | " + this.posY);
			System.out.println("Current file: " + this.path);
			
		} catch (IOException e) {
			System.out.println("Config file not found. Replacing...");
			this.createConfigFile();
			this.path = null;
			this.sizeX = 600;
			this.sizeY = 400;
			this.posX  = 0;
			this.posY  = 0;
		}
	}
	
	/**
	 * Creates a config file with standard configurations (see method readConfigFile())
	 * 
	 * <hr><i>THREAD SAFE by default</i><hr>
	 */
	private void createConfigFile(){
		BufferedWriter file;
		try {
			file = new BufferedWriter(new FileWriter("config.cfg"));
			file.write("600\n");
			file.write("400\n");
			file.write("0\n");
			file.write("0\n");
			file.write("#");
			file.close();
		} catch (IOException e) {}
	}
	
	/**
	 * Saves the current configuration in "config.cfg" in the program's working directory.
	 * See {@link WindowEventListener}
	 * 
	 * <hr><i>THREAD SAFE by default</i><hr>
	 */
	public void saveConfigFile(){
		
		DebugShell.out(State.LOG, Area.SYSTEM, "Updating config file...");
		
		BufferedWriter file;
		try {
			file = new BufferedWriter(new FileWriter("config.cfg"));
			file.write(this.sizeX + "\n");
			file.write(this.sizeY + "\n");
			file.write(this.posX + "\n");
			file.write(this.posY + "\n");
			
			if( this.path == null )
				file.write("#");
			else
				file.write(this.path);
			
			file.close();
			
			DebugShell.out(State.LOG, Area.SYSTEM, "config up to date");
		} catch (IOException e) {
			DebugShell.out(State.WARNING, Area.SYSTEM, "failed to save config file");
		}
	}
}
