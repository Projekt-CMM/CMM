package at.jku.ssw.cmm.preprocessor;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.gui.exception.IncludeNotFoundException;
import at.jku.ssw.cmm.gui.file.FileManagerCode;

public class Preprocessor {

	public static String expand( String sourceCode, String workingDirectory, List<Object[]> codeRegister, List<Integer> breakpoints ) throws IncludeNotFoundException{
		// Debug message
		DebugShell.out(State.LOG, Area.COMPILER, "Starting preprocessor");
	
		// Init new SourceCode storage
		String newSourceCode = "";
		
		// Reset code register list
		codeRegister.clear();
		
		// Reset breakpoints list
		breakpoints.clear();
		
		newSourceCode = parseFile(sourceCode, workingDirectory, codeRegister, breakpoints, 0, "main");

		for(Object[] codePart : codeRegister) {
			System.out.println(codePart[0] + "|" + codePart[1] + "|" + codePart[2]);
			
		}
		
		return newSourceCode;
	}
	
	public static String parseFile( String sourceCode, String workingDirectory, List<Object[]> codeRegister, List<Integer> breakpoints, int offset, String file) throws IncludeNotFoundException{
		// Debug message
		DebugShell.out(State.LOG, Area.COMPILER, "parse File");
		
		// Init new SourceCode storage
		String newSourceCode = "";
		
		// is parser inside comment?
		boolean insideComment = false;
    	
		// TODO check if code inside string
		//boolean insideString = false;

		int lastCodeRegisterInsert = offset;
		
		// parse code
		int line = offset;		
		for( String s : sourceCode.split("\n") ){
			line ++;
			String commentString = null;
			
			if(s.contains("//")) {
				commentString = s.substring(s.indexOf("//"));
				s = s.substring(0, s.indexOf("//"));
			}
			
			// check if code is inside comment
			if(insideComment) {
				// check if comment end in this line
				if(s.contains("*/")) {
					insideComment = false;
					s += s.substring(0, s.indexOf("*/"));
					s = s.substring(0, s.indexOf("*/")+2);
				} else {
					newSourceCode += addString(s,commentString);
					continue;
				}
			}

			// parse multiline comments
			while(s.contains("/*")) {
				if(s.contains("*/")) {
					String newS = s.substring(0, s.indexOf("/*"));
					newS += s.substring(s.indexOf("*/")+2);
					s = newS;
				} else {
					insideComment = true;
					if(commentString != null)
						commentString = s.substring(s.indexOf("/*")) + commentString;
					else
						commentString = s.substring(s.indexOf("/*")) ;
					s = s.substring(0, s.indexOf("/*"));
				}
			}
			
			if(s.matches("^\\s*#.*$")) {
				String preString = s.substring(s.indexOf("#")+1);
				s = "//## pre-command: " + s;

				if(preString.isEmpty()) {
					// not known preprocessor command
				} else if(preString.matches("^\\s*include.*$")) {
					String path = null;
					boolean localDirectory = true;
					if(preString.matches("^\\s*include\\s*<.*>\\s*$")) {
						// clib include
						Matcher m = Pattern.compile("^\\s*include\\s*<(.*)>\\s*$").matcher(preString);
						if(m.matches()) {
							path = m.group(1);
							System.out.println("clib include: " + path);
						}
					} else if(preString.matches("^\\s*include\\s*\".*\"\\s*$")) {
						localDirectory = false;
						// normal include
						Matcher m = Pattern.compile("^\\s*include\\s*\"(.*)\"\\s*$").matcher(preString);
						if(m.matches()) {
							path = m.group(1);
							System.out.println("normal include: " + path);
						}
					}
					
					if(path != null) {
						String includeCode;
						if( localDirectory )
							path = "clib/" + path;
					    else
					    	path = workingDirectory + "/" + path;
						
						includeCode = FileManagerCode.readSourceCode(new File(path));
						
						if( includeCode != null ){
							// add include tag
							newSourceCode += addString(s,commentString);
							
							// parse file
							String newSourceCodeHelp = parseFile(includeCode, workingDirectory, codeRegister, breakpoints, line, path);
							
							// copy source-code
							newSourceCode += newSourceCodeHelp;
							
							Object[] newCodeInsert = {lastCodeRegisterInsert+1, line, file};
					    	codeRegister.add(newCodeInsert);
					    	
							// calculate new line
							line += countLines(newSourceCodeHelp);
					    	
					    	lastCodeRegisterInsert = line;
						}
					    else{
					    	// TODO, better exception
					    	DebugShell.out(State.WARNING, Area.COMPILER, "File not found: " + path);
					    	throw new IncludeNotFoundException(path, line);
					    }
						continue;
					} else {
						// incorrect include
					}
				} else if(preString.matches("^\\s*(pause|wait)\\s*$")) {
					// add breakpoint
					breakpoints.add(line);
				} else {
					// not known preprocessor command
				}
			}
			
			newSourceCode += addString(s,commentString);
		}

		Object[] newCodeInsert = {lastCodeRegisterInsert+1, line, file};
    	codeRegister.add(newCodeInsert);
    	
		return newSourceCode;
	}
	
	public static String addString(String s, String commentString) {
		if(commentString != null) 
			return s + commentString + "\n";
		else
			return s + "\n";
	}

	// http://stackoverflow.com/questions/2850203/count-the-number-of-lines-in-a-java-string
	public static int countLines(String str){
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
			if(codeLine >= Integer.parseInt(curObj[0].toString()) && codeLine <= Integer.parseInt(curObj[1].toString())) {
				objName = curObj[2].toString();
			}
		}
		
		// calculate linenumber
		if(objName != null) {
			for(Object[] curObj : codeRegister) {
				if(curObj[2].toString() == objName) {
					if(codeLine > Integer.parseInt(curObj[1].toString())) {
						objLine += Integer.parseInt(curObj[1].toString()) - Integer.parseInt(curObj[0].toString()) + 1;
					} else if(codeLine >= Integer.parseInt(curObj[0].toString()) && codeLine <= Integer.parseInt(curObj[1].toString())) {
						objLine += codeLine - Integer.parseInt(curObj[0].toString()) + 1;
					}
				}
			}

			returnObj = new Object[] {objName, objLine};
		}
		
		return returnObj;
	}
}
