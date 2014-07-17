package at.jku.ssw.cmm.gui.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This file manager is considered to save a list of strings to a given (text) file.
 * It can be used for input stream saving and loading.
 * 
 * @author fabian
 *
 */
public class FileManagerList {
	
	/**
	 * Saves the given list to a file. The elements of the list are separated by "\n",
	 * as a consequence the list can be viewed with an external text editor easily.
	 * 
	 * @param file2 The target file (directory)
	 * @param l The list of strings, which will be saved to the given file
	 * @return TRUE if saving successful, FALSE if saving failed
	 */
	public static boolean saveList( File file2, List<String> l ){
		
		BufferedWriter file;
		try {
			file = new BufferedWriter(new FileWriter(file2));
			for( String s : l ){
				file.write(s);
			}
			file.close();
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Reads a list of Strings from a (text) file.
	 * 
	 * @param file2 The target file which has to be read
	 * @return The list with the contents of the file (as ArrayList). Returns null if reading failed
	 */
	public static List<String> readList( File file2 ){
		
		BufferedReader file;
		String line;
		
		List <String> l = new ArrayList<>();
		
		try {
			file = new BufferedReader(new FileReader(file2));
			while ((line = file.readLine()) != null) {
				l.add(line);
			}
			file.close();
			
		} catch (IOException e) {
			return null;
		}
		
		return l;
	}
}
