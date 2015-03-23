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
 
package at.jku.ssw.cmm.gettext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;

/**
 * This is a provisional implementation for GNU Gettext translation files.
 * 
 * @author fabian
 */
public class Language {
	
	/**
	 * A map containing all the original English languages and
	 * their translations into the current language.
	 */
	private static Map<String, String> langMap = null;
	
	/**
	 * The default language. Valid language abbreviations are (so far)
	 * <li> en - English </li>
	 * <li> de - German </li>
	 */
	public static final String DEFAULT_LANGUAGE = "en";
	
	/**
	 * Loads the language file for the given language (parameter) and saves
	 * the translations to a HashMap.
	 * 
	 * @param langCode The code for the language which has to be loaded.<br>
	 * 			<b> NOTE: </b> The language code has to be equal to the
	 * 			language file's name, located in the folder named "po".<br>
	 * 			<b> Example: </b> "po/<i>de</i>.po"
	 */
	public static void loadLanguage(String langCode){
		
		// Initialize translations map
		langMap = new HashMap<>();
		
		// Reset to default language if given language not available
		if( langCode == null || langCode.equals(DEFAULT_LANGUAGE) ){
			langMap = null;
			DebugShell.out(State.ERROR, Area.SYSTEM, "Language " + langCode + " does not exist!");
			return;
		}
		
		// Variables for reading the translations file
		BufferedReader file;
		String line;
		
		String key = "", msg = "";
		int mode = 0;
		
		// Start reading file
		try {
			//file = new BufferedReader(new FileReader( ));
			file = new BufferedReader(new InputStreamReader(new FileInputStream(new File("po/" + langCode)), "UTF8"));
			while ((line = file.readLine()) != null) {
				
				//Load a translated String
				if( !line.startsWith("#") ){
				
					if( (mode == 0 || mode == 2) && line.contains("msgid") ) {
						if( mode == 2 )
							langMap.put(key, msg);
						key = "";
						msg = "";
						
						mode = 1;
					}
					
					if( mode == 1 && line.contains("msgstr") ) {
						mode = 2;
					}
					else if( mode == 1 && line.contains("\"") ){
						key += line.substring(line.indexOf("\"")+1, line.length()-1);
					}
					
					if( mode == 2 && line.contains("\"") ) {
						msg += line.substring(line.indexOf("\"")+1, line.length()-1);
					}
				}
			}
			file.close();
		}
		// In case of error: reset to default language
		catch (IOException e) {
			langMap = null;
			DebugShell.out(State.ERROR, Area.SYSTEM, "Failed to read translations file: " + e.getMessage());
		}
	}

	/**
	 * Translated the given string to the language loaded.
	 * 
	 * @param s The string which has to be translated
	 * @return The translation
	 */
	public static String _(String s) {
		if( langMap == null ){
			return s;
		}
		else{
			try{
				return langMap.get(s).equals("") ? s : langMap.get(s);
			}catch(Exception e){
				System.out.println("[error] loading string \"" + s + "\"");
				return s;
			}
		}
	}
	
	/**
	 * @return TRUE if there has been a translation loaded, FALSE if not
	 */
	public static boolean languageLoaded(){
		return langMap != null;
	}
}