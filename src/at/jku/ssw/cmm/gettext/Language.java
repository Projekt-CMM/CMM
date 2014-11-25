package at.jku.ssw.cmm.gettext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Language {
	
	private static Map<String, String> langMap = null;
	
	public static void loadLanguage(String langCode){
		
		System.out.println("Loading language -> " + langCode);
		
		langMap = new HashMap<>();
		
		//Reset to default language
		if( langCode == null ){
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
}