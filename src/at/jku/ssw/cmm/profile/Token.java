package at.jku.ssw.cmm.profile;

public class Token {
	
	/**
	 * The Name of the Token
	 */
	private String name;
	
	/**
	 * The Description of the Token
	 */
	private String description;
	
	/**
	 * A relative Path to the token image
	 */
	private String imagePath;
	
	
	/**
	 * Static Strings, for reading the XML
	 */
	public static final String
		XML_TITLE = "title",
		XML_DESCRIPTION = "description",
		XML_IMAGEPATH = "imagepath";
	
	/**
	 * FolderName of the Tokens
	 */
	public static final String
		FOLDER_TOKENS = "tokens";

	public String getName() {
		return name;
	}
	
	/**
	 * Get all available Tokens
	 */
	public void getTokens(){
		
	}
	
	/**
	 * Reads the "stats.xml" file
	 */
	private void readXML(){
		
	}
	

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	

	
	
}
