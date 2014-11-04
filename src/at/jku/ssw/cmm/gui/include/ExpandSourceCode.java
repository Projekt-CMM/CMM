package at.jku.ssw.cmm.gui.include;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.exception.IncludeNotFoundException;
import at.jku.ssw.cmm.gui.file.FileManagerCode;

/**
 * This class contains the "preprocessor" for the CMM compiler.
 * It adds include file code to the original user's source code before
 * it is given to the compiler.
 * 
 * @author fabian
 *
 */
public class ExpandSourceCode {

	/**
	 * Loads include files and automatically adds them to the source code which is compiled
	 * (not the source code which is displayed on the GUI!). Counts the lines of code and saves
	 * them to the code register object, sorted file by file.
	 * 
	 * <hr><i> THREAD SAFE by default</i><hr>
	 * 
	 * @param sourceCode
	 * @param workingDirectory
	 * @param codeRegister
	 * @return
	 * @throws IncludeNotFoundException 
	 */
	public static String expand( String sourceCode, String workingDirectory, List<Object[]> codeRegister, List<Integer> breakpoints ) throws IncludeNotFoundException{
		
		//Debug message
		DebugShell.out(State.LOG, Area.COMPILER, "Starting include file scan");
		
		//Code offset to the user's source code
		int offset = 0;
		
		//Is the current library from the standard lib directory (FALSE)
		//or from the project/file directory (TRUE) ?
		boolean localDirectory = true;
		
		//TRUE if all libraries are included
		boolean finishedIncludes = false;
		
		//Reset code register list
		codeRegister.clear();
		
		//Save copy of source code
		String l = sourceCode;
		
		//String for library code
		String preFunctionSource = "";

		//Source code will be re-initialized
		sourceCode = "";
		
		int line = 1;
		for( String s : l.split("\n") ){
			
			Pattern p = null;
			
			//Local include directory
			if( s.startsWith("#include") && s.contains("<") && !finishedIncludes ){
				p = Pattern.compile("#include <([\\w./]+)>");
				DebugShell.out(State.LOG, Area.COMPILER, "Local include file: " + s);
				localDirectory = true;
			}
			
			//Project directory (working path)#include <stdilb.h>
			else if( s.startsWith("#include") && s.contains("\"") && !finishedIncludes ){
				p = Pattern.compile("#include \"([\\w./]+)\"");
				DebugShell.out(State.LOG, Area.COMPILER, "Working directory include file: " + s);
				localDirectory = false;
			}
			
			else{
				//Line contains a breakpoint
				if( s.startsWith("" + GUImain.BREAKPOINT) ){
					sourceCode = sourceCode + s.substring(1) + "\n";
					breakpoints.add(line);
				}
				else
					sourceCode = sourceCode + s + "\n";
				if( !s.isEmpty() )
					finishedIncludes = true;
			}
			
			//Found a "#include"
			if( p != null ){
			
				Matcher m = p.matcher(s);
				
				//Found a file name
				if (m.find()) {
					DebugShell.out(State.LOG, Area.COMPILER, "Reading include file: " + m.group(1));
				    
				    String include;
				    
				    if( localDirectory )
				    	include = FileManagerCode.readSourceCode(new File("clib/" + m.group(1)));
				    else
				    	include = FileManagerCode.readSourceCode(new File(workingDirectory + "/" + m.group(1)));
				    
				    if( include != null ){

				    	int length = count( include, '\n' );
				    	offset += length + 1;
				    	preFunctionSource = preFunctionSource + include + "\n";
				    	
				    	Object[] e = {offset - length, offset, m.group(1)};
				    	codeRegister.add(e);
				    }
				    else{
				    	DebugShell.out(State.WARNING, Area.COMPILER, "File not found: " + m.group(1));
				    	throw new IncludeNotFoundException(m.group(1), line);
				    }
				}
			}
			
			line ++;
		}
		
		sourceCode = preFunctionSource + sourceCode;
		
		int length = count( sourceCode, '\n' );
    	offset += length;
    	
    	//Correct breakpoint offset
    	for( int i = 0; i < breakpoints.size(); i++ ){
    		breakpoints.set(i, breakpoints.get(i) + offset - length - codeRegister.size() );
    	}
    	
    	//Add original user code to code register
    	Object[] e = {offset - length + 1, length, "original file"};
    	codeRegister.add(0, e);
		
		return sourceCode;
	}
	
	/**
	 * Counts the number of times a character occurs in a given String.
	 * 
	 * @param text The String that is searched for a given character
	 * @param sign The character that is counted
	 * @return The number of times the character "sign" occurs in the String "text"
	 */
	private static int count( String text, char sign ){
		
		int count = 0;
		
		//Search every character in text
		for( char c : text.toCharArray() ){
			if( c == sign )
				count++;
		}
		
		return count;
	}
	
	/**
	 * Calculates the line number in the user's source code from the complete code line,
	 * which comes from the compiler and interpreter.
	 * 
	 * @param line The line from the compiler/interpreter (source code including
	 * 		library code)
	 * @param codeStart The line in the complete code, where the user's code starts
	 * @param files The number of referenced libraries
	 * @return The line in the user's code
	 */
	public static int correctLine( int line, int codeStart, int files ){
		return line - codeStart + files;
	}
}
