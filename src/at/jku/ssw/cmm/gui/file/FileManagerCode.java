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
 
package at.jku.ssw.cmm.gui.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class contains static methods to save and load a String (text, source
 * code, ...) to a given file.
 */
public class FileManagerCode {

	/**
	 * Saves the given text to a *.cmm file. If the ".cmm" postfix is missing at
	 * the end of the given path, it is added automatically.
	 * 
	 * @param fileName
	 *            The file where the text has to be saved to
	 * @param code
	 *            The text which has to be saved
	 * @return TRUE if saving successful, FALSE if saving failed
	 * @throws IOException 
	 */
	public static boolean saveSourceCode(File fileName, String code,
			String input) throws IOException {

		// Add file type extension of necessary
		if (!fileName.getName().endsWith(".cmm")) {
			String path = fileName.getPath();
			fileName = new File(path + ".cmm");
		}

		// Write source code file
		BufferedWriter file;
		file = new BufferedWriter(new FileWriter(fileName));
		file.write(code);
		file.close();

		// Get input data file path
		File inputFile = new File(fileName.getPath().substring(0,
				fileName.getPath().indexOf(".cmm"))
				+ ".input.txt");
		file = new BufferedWriter(new FileWriter(inputFile));
		file.write(input);
		file.close();

		return true;
	}

	/**
	 * Opens the given file, reads the text contained and gives it back as
	 * return parameter
	 * 
	 * @param fileName
	 *            The file which has to be opened
	 * @return The content of the file (as String). Returns null if file reading
	 *         failed
	 * @throws IOException 
	 */
	public static String readSourceCode(File fileName) throws IOException {

		BufferedReader file;
		String line;
		String code = null;
		
		file = new BufferedReader(new FileReader(fileName));
		code = file.readLine();

		while ((line = file.readLine()) != null) {
			code = code + "\n" + line;
		}
		file.close();

		return code;
	}

	/**
	 * Opens the given file, generates input file name, reads the text contained
	 * and gives it back as return parameter
	 * 
	 * @param fileName
	 *            The file which has to be opened
	 * @return The content of the file (as String). Returns null if file reading
	 *         failed
	 */
	public static String readInputData(File fileName) { //TODO add error management?

		// Get input data file path
		try {
			return readInputDataBlank(new File(fileName.getPath().substring(0, fileName.getPath().indexOf(".cmm")) + ".input.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public static String readInputDataBlank(File inputFile) throws IOException {

		BufferedReader file;
		String line;
		String input = null;
		
		file = new BufferedReader(new FileReader(inputFile));
		input = file.readLine();

		while ((line = file.readLine()) != null) {
			input = input + "\n" + line;
		}
		file.close();

		return input;
	}
}
