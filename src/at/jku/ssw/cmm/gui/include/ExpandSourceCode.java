package at.jku.ssw.cmm.gui.include;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.exception.IncludeNotFoundException;
import at.jku.ssw.cmm.gui.file.FileManagerCode;

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
		
		System.out.println("Starting include file scan");
		
		int offset = 0;
		boolean localDirectory = true;
		boolean finishedIncludes = false;
		
		codeRegister.clear();
		
		String l = sourceCode;
		String preFunctionSource = "";
		sourceCode = "";
		
		int line = 1;
		for( String s : l.split("\n") ){
			
			Pattern p = null;
			
			//Local include directory
			if( s.startsWith("#include") && s.contains("<") && !finishedIncludes ){
				p = Pattern.compile("#include <([\\w./]+)>");
				System.out.println("Local include file: " + s);
				localDirectory = true;
			}
			
			//Project directory (working path)
			else if( s.startsWith("#include") && s.contains("\"") && !finishedIncludes ){
				p = Pattern.compile("#include \"([\\w./]+)\"");
				System.out.println("Working directory include file: " + s);
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
				    System.out.println("Reading include file: " + m.group(1));
				    
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
				    	System.out.println("File not found: " + m.group(1));
				    	throw new IncludeNotFoundException(m.group(1), line);
				    }
				}
			}
			
			line ++;
		}
		
		sourceCode = preFunctionSource + sourceCode;
		
		int length = count( sourceCode, '\n' );
    	offset += length;
    	
    	Object[] e = {offset - length + 1, length, "original file"};
    	codeRegister.add(0, e);
    	
    	//Correct breakpoint offset through includes
    	for( int i = 0; i < breakpoints.size(); i++ ){
    		breakpoints.set(i, breakpoints.get(i) + offset - length + 1 );
    	}
		
		return sourceCode;
	}
	
	private static int count( String text, char sign ){
		
		int count = 0;
		
		for( char c : text.toCharArray() ){
			if( c == sign )
				count++;
		}
		
		System.out.println("Total " + count + " lines");
		
		return count;
	}
	
	public static int correctLine( int line, int codeStart, int files ){
		return line - codeStart + files;
	}
}
