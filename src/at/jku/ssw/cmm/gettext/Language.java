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
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Language {
	
	private static Map<String, String> langMap = null;
	
	//TODO default language should be english
	public static final String DEFAULT_LANGUAGE = "de";
	
	public static void loadLanguage(String langCode){
		
		System.out.println("Loading language -> " + langCode);
		
		langMap = new HashMap<>();
		
		//Reset to default language
		if( langCode == null || langCode.equals(DEFAULT_LANGUAGE) ){
			langMap = null;
			return;
		}
		
		BufferedReader file;
		String line;
		String line2;
		
		try {
			file = new BufferedReader(new FileReader("po/" + langCode ));
			while ((line = file.readLine()) != null) {
				//Load a translated String
				if( line.startsWith("msgid") && !line.contains("\"\"") && (line2 = file.readLine()) != null && line2.startsWith("msgstr") && !line.contains("\"\"") ){
					
					//Save translated String
					langMap.put(line.substring(line.indexOf("\"")+1, line.length()-1), line2.substring(line2.indexOf("\"")+1, line2.length()-1));
					//System.out.println("Reading String: " + line.substring(line.indexOf("\"")+1, line.length()-1) + " -> " + line2.substring(line2.indexOf("\"")+1, line2.length()-1));
				}
			}
			file.close();
			
		} catch (IOException e) {
			langMap = null;
		}
	}

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
	
	public static boolean languageLoaded(){
		return langMap != null;
	}
}