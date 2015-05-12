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
 
package at.jku.ssw.cmm.preprocessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.preprocessor.exception.PreprocessorException;
import at.jku.ssw.cmm.gui.file.FileManagerCode;

public class Preprocessor {

	public static String expand( String sourceCode, String workingDirectory, List<Object[]> codeRegister) throws PreprocessorException { // Debug message
		DebugShell.out(State.LOG, Area.COMPILER, "Starting preprocessor");
	
		// Init new SourceCode storage
		String newSourceCode = "";
		
		// Reset code register list
		codeRegister.clear();

		Map<String, Integer> defines = new HashMap<>();
		
		newSourceCode = parseFile(sourceCode, workingDirectory, codeRegister, defines, 0, "main");
		
		return newSourceCode;
	}
	
	public static String parseFile( String sourceCode, String workingDirectory, List<Object[]> codeRegister, Map<String, Integer> defines, int offset, String file) throws PreprocessorException {
		// Debug message
		DebugShell.out(State.LOG, Area.PREPROCESSOR, "parse File");
		
		// Init new SourceCode storage
		String newSourceCode = "";
		
		// is parser inside comment?
		boolean insideComment = false;
		boolean insideString = false;
    	
		// TODO check if code inside string
		//boolean insideString = false;
		
		List<Boolean> ifConditions = new ArrayList<>();
		
		int lastCodeRegisterInsert = offset;
		
		// parse code
		int line = offset;		
		int fileLine = 0;
		
		if(sourceCode != null)
		for( String s : sourceCode.split("\n") ) {
			line ++;
			fileLine ++;

			String commentString = "";
			
			boolean parseAllCode = true;
			
			commentString.replace('\r', ' ');
			
			// if one of the preprocessor if is not true, parsing of all code is not possible
			for(int i=0; i< ifConditions.size(); i++) {
				if(ifConditions.get(i) == false)
					parseAllCode = false;
			}

			// Parse Comments
			for(int i = 0;i < s.length();) {
				if(insideComment) {
					if(s.regionMatches(i, "//", 0, 2)) {
						commentString += s.substring(i);
						if(i != 0)
							s = s.substring(0, i-1);
						else
							s = "";
						break;
					} else if(s.regionMatches(i, "*/", 0, 2)) {
						if(i != 0) {
							s = s.subSequence(0, i) + s.substring(i+2);
						} else {
							s = s.substring(i+2);
						}
						insideComment = false;
					} else {
						if(i != 0) {
							commentString += s.charAt(i);
							s = s.subSequence(0, i) + s.substring(i+1);
						} else {
							commentString += s.charAt(0);
							s = s.substring(1);
						}
					}
				} else if(insideString) {
					if(s.regionMatches(i, "\"", 0, 1)) {
						if(i != 0) {
							if(!s.regionMatches(i-1, "\\\"", 0, 1))
								insideString = false;
						} else {
							insideString = false;
						}
					}
					i++;
				} else {
					if(s.regionMatches(i, "/*", 0, 2)) {
						if(i != 0) {
							s = s.subSequence(0, i) + s.substring(i+2);
						} else {
							s = s.substring(i+2);
						}
						insideComment = true;
					} else if(s.regionMatches(i, "//", 0, 2)) {
						commentString += s.substring(i);
						if(i != 0)
							s = s.substring(0, i-1);
						else
							s = "";
						break;
					} else if(s.regionMatches(i, "\"", 0, 1)) {
						insideString = true;
						i++;
					} else {
						i++;
					}
				}
			}
			
			if(s.matches("^\\s*#.*$")) {
				String preString = s.substring(s.indexOf("#")+1);
				s = "//## pre-command: " + s;
				
				if(preString.isEmpty()) {
					throw new PreprocessorException("unknow preprocessor command", file, fileLine);
				} else if(preString.matches("^\\s*(ifdef|ifndef)(\\s.*|)$")) {
					Matcher m = Pattern.compile("^\\s*ifdef\\s*(\\w+)\\s*$").matcher(preString);
					if(m.matches()) {
						if(defines.containsKey(m.group(1)) && defines.get(m.group(1)) != 0)
							ifConditions.add(true);
						else
							ifConditions.add(false);
					} else {	
						m = Pattern.compile("^\\s*ifndef\\s*(\\w+)\\s*$").matcher(preString);
						if(m.matches()) {
							if(defines.containsKey(m.group(1)) && defines.get(m.group(1)) != 0 )
								ifConditions.add(false);
							else
								ifConditions.add(true);
						} else {
							throw new PreprocessorException("identifier required", file, fileLine);
						}
					}
				} else if(preString.matches("^\\s*else\\s*$")) {
					if(ifConditions.size() != 0) {
						// toggle state of condition
						// TODO detect multible else statements
						ifConditions.set(ifConditions.size()-1, !ifConditions.get(ifConditions.size()-1).booleanValue());
					} else {
						throw new PreprocessorException("there is no condition, where this #else can be used", file, fileLine);
					}
				} else if(preString.matches("^\\s*endif\\s*$")) {
					// remove last element
					if(ifConditions.size() != 0) {
						ifConditions.remove(ifConditions.size()-1);
					} else {
						throw new PreprocessorException("there is no condition, where this #endif can be used", file, fileLine);
					}
				} else if(parseAllCode == false) {
					// ignore other possible preprocessor commands
				} else if(preString.matches("^\\s*define(\\s.*|)$")) {
					Matcher m = Pattern.compile("^\\s*define\\s*(\\w+)\\s*(\\w*)\\s*$").matcher(preString);
					if(m.matches()) {
						int value = 1;
						
						try {
							// parse value
							if(!m.group(2).toString().isEmpty())
								value = Integer.parseInt(m.group(2));
						} catch ( java.lang.NumberFormatException e) {
							throw new PreprocessorException("preprocessor value is not an integer", file, fileLine);
						}
						
						defines.put(m.group(1), value);
					} else {
						throw new PreprocessorException("identifier required", file, fileLine);
					}
				} else if(preString.matches("^\\s*undef(\\s.*|)$")) {
					Matcher m = Pattern.compile("^\\s*undef\\s*(\\w+)\\s*$").matcher(preString);
					if(m.matches()) {
						defines.remove(m.group(1));
					} else {
						throw new PreprocessorException("identifier required", file, fileLine);
					}
				} else if(preString.matches("^\\s*include.*$")) {
					String path = null;
					boolean localDirectory = true;
					if(preString.matches("^\\s*include\\s*<.*>\\s*$")) {
						// clib include
						Matcher m = Pattern.compile("^\\s*include\\s*<(.*)>\\s*$").matcher(preString);
						if(m.matches()) {
							path = m.group(1);
						}
					} else if(preString.matches("^\\s*include\\s*\".*\"\\s*$")) {
						localDirectory = false;
						// normal include
						Matcher m = Pattern.compile("^\\s*include\\s*\"(.*)\"\\s*$").matcher(preString);
						if(m.matches()) {
							path = m.group(1);
						}
					}
					
					if(path != null) {
						String includeCode;
						if( localDirectory )
							path = "clib/" + path;
					    else
					    	path = workingDirectory + "/" + path;
						
						try {
							includeCode = FileManagerCode.readSourceCode(new File(path));
						} catch(StackOverflowError e) {
							throw new PreprocessorException("cyclic includes" , file, fileLine);
						} catch(IOException e) {
							DebugShell.out(State.WARNING, Area.PREPROCESSOR, "File not found: " + path);
					    	throw new PreprocessorException("include not found: " + path , file, fileLine);
						}
						
						if( includeCode != null ){
							// add include tag
							newSourceCode += addString(s,commentString);
							
							// parse file
							String newSourceCodeHelp = "";
							try {
								newSourceCodeHelp = parseFile(includeCode, workingDirectory, codeRegister, defines, line, path);
							} catch(StackOverflowError e) {
								throw new PreprocessorException("cyclic includes" , file, fileLine);
							}
							
							// copy source-code
							newSourceCode += newSourceCodeHelp;
							
							Object[] newCodeInsert = {lastCodeRegisterInsert+1, line, file};
					    	codeRegister.add(newCodeInsert);
					    	
							// calculate new line
							line += countLines(newSourceCodeHelp);
					    	
					    	lastCodeRegisterInsert = line;
						}
					    else{
					    	DebugShell.out(State.WARNING, Area.PREPROCESSOR, "File not found: " + path);
					    	throw new PreprocessorException("include not found: " + path , file, fileLine);
					    }
						continue;
					} else {
						throw new PreprocessorException("no include path specified", file, fileLine);
					}
				} else {
					throw new PreprocessorException("unknow preprocessor command", file, fileLine);
				}
			}
			
			if(parseAllCode == false) {
				newSourceCode += addString("// ign " + s,commentString);
				continue;
			}
			
			newSourceCode += addString(s,commentString);
		}

		if(!ifConditions.isEmpty()) {
			throw new PreprocessorException("not all preprocessor conditions have ended", file, fileLine);
		}
		
		Object[] newCodeInsert = {lastCodeRegisterInsert+1, line, file};
    	codeRegister.add(newCodeInsert);
    	
		return newSourceCode;
	}
	
	public static String addString(String s, String commentString) {
		if(commentString != null && !commentString.isEmpty()) 
			return s + "// comment " + commentString + "\n";
		else
			return s + "\n";
	}

	// http://stackoverflow.com/questions/2850203/count-the-number-of-lines-in-a-java-string
	public static int countLines(String str){
		if(str == null)
			return 0;
		
		String[] lines = str.split("\r\n|\r|\n");
		return  lines.length;
	}

	public static Object[] returnFileAndNumber(int codeLine, List<Object[]> codeRegister) {
		// init vars
		Object[] returnObj = {"unknow", 0};
		
		String objName = null;
		int objLine = 0;
		
		// get name of codepart
		for(Object[] curObj : codeRegister) {
			if(codeLine >= (int)curObj[0] && codeLine <= (int)curObj[1]) {
				objName = curObj[2].toString();
			}
		}
		
		// calculate linenumber
		if(objName != null) {
			for(Object[] curObj : codeRegister) {
				if(curObj[2].toString().equals(objName)) {
					if(codeLine > (int)curObj[1]) {
						objLine += (int)curObj[1] - (int)curObj[0] + 1;
					} else if(codeLine >= (int)curObj[0] && codeLine <= (int)curObj[1]) {
						objLine += codeLine - (int)curObj[0] + 1;
					}
				}
			}

			returnObj = new Object[] {objName, objLine};
		}
		
		return returnObj;
	}
}
