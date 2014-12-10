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
	 */
	public static boolean saveSourceCode(File fileName, String code,
			String input) {

		// Add file type extension of necessary
		if (!fileName.getName().endsWith(".cmm")) {
			String path = fileName.getPath();
			fileName = new File(path + ".cmm");
		}

		// Write source code file
		BufferedWriter file;
		try {
			file = new BufferedWriter(new FileWriter(fileName));
			file.write(code);
			file.close();
		} catch (IOException e) {
			return false;
		}

		// Get input data file path
		File inputFile = new File(fileName.getPath().substring(0,
				fileName.getPath().indexOf(".cmm"))
				+ ".input.txt");
		try {
			file = new BufferedWriter(new FileWriter(inputFile));
			file.write(input);
			file.close();
		} catch (IOException e) {
			return false;
		}

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
	 */
	public static String readSourceCode(File fileName) {

		BufferedReader file;
		String line;
		String code = null;

		try {

			file = new BufferedReader(new FileReader(fileName));
			code = file.readLine();

			while ((line = file.readLine()) != null) {
				code = code + "\n" + line;
			}
			file.close();

		} catch (IOException e) {
			return null;
		}

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
	public static String readInputData(File fileName) {

		// Get input data file path
		return readInputDataBlank(new File(fileName.getPath().substring(0, fileName.getPath().indexOf(".cmm")) + ".input.txt"));
	}

	public static String readInputDataBlank(File inputFile) {

		BufferedReader file;
		String line;
		String input = null;

		try {

			file = new BufferedReader(new FileReader(inputFile));
			input = file.readLine();

			while ((line = file.readLine()) != null) {
				input = input + "\n" + line;
			}
			file.close();

		} catch (IOException e) {
			return null;
		}
		
		System.out.println("Read: " + input);

		return input;
	}
}
