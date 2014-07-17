package at.jku.ssw.cmm.gui.datastruct;

/**
 * Container class for variable browser information. Every object of this class stands for
 * one data structure or function which is displayed in one of the variable tables of the
 * right panel of the main GUI. These objects are saved to a Stack in the wrapper class of
 * the right panel ({@link GUIrightPanel}).
 * 
 * @author fabian
 *
 */
public class StructureContainer {
	
	/* --- data structure types --- */
	public static final int FUNC = 0;
	public static final int STRUCT = 1;
	public static final int ARRAY = 2;
	public static final int GLOBAL = 3;

	/**
	 * Container class for variable browser information.
	 * 
	 * @param name Name of the data structure this container is referring to
	 * @param type Type of this data structure
	 * @param address Address of this data structure
	 */
	public StructureContainer( String name, int type, int address ){
		this.name = name;
		this.type = type;
		this.address = address;
	}
	
	//Name of the data structure or function
	private final String name;
	
	//Type ID of the data structure or function
	private final int type;
	
	//Address (Call stack) of the data structure or function
	private final int address;
	
	/**
	 * <i>THREAD SAFE by default</i>
	 * 
	 * @return Name of the data structure or function this node is referring to
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * <i>THREAD SAFE by default</i>
	 * 
	 * @return Type ID of the data structure or function this node is referring to
	 */
	public int getType(){
		return this.type;
	}
	
	/**
	 * <i>THREAD SAFE by default</i>
	 * 
	 * @return Address (Call stack) of the data structure or function this node is referring to
	 */
	public int getAddress(){
		return this.address;
	}
}
