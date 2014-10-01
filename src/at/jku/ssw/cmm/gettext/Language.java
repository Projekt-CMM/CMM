package at.jku.ssw.cmm.gettext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Language {
	
	private static Map<String, String> langMap = null;
	
	public static void loadLanguage(String langCode){
		langMap = new HashMap<>();
		
		BufferedReader file;
		String line;
		String line2;
		
		try {
			file = new BufferedReader(new FileReader("po/" + langCode + ".po"));
			while ((line = file.readLine()) != null) {
				//Load a translated String
				if( line.startsWith("msgid") && (line2 = file.readLine()) != null && line2.startsWith("msgstr") ){
					
					//Save translated String
					langMap.put(line.substring(6), line2.substring(7));
					System.out.println("Reading String: " + line.substring(6) + " -> " + line2.substring(7));
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
				return langMap.get(s);
			}catch(Exception e){
				System.out.println("[error] loading string \"" + s + "\"");
				return s;
			}
		}
	}
}