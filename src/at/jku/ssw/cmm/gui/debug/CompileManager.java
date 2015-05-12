package at.jku.ssw.cmm.gui.debug;

import java.util.ArrayList;

import at.jku.ssw.cmm.compiler.Compiler;
import at.jku.ssw.cmm.compiler.Tab;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.preprocessor.Preprocessor;
import at.jku.ssw.cmm.preprocessor.exception.PreprocessorException;

public class CompileManager {

	public static Tab compile(String code, GUImain main, boolean userCode ) {

		// Assemble complete source code using preprocessor
		try {
			code = Preprocessor.expand(code, main.getSettings().getWorkingDirectory(),
					main.getLeftPanel().getSourceCodeRegister());
		}
		// Preprocessor exception, reason ins known
		catch (PreprocessorException e1) {

			// Reset source code file register
			if( userCode ) {
				Object[] e = { 1, 0, null };
				main.getLeftPanel().getSourceCodeRegister().clear();
				main.getLeftPanel().getSourceCodeRegister().add(e);
			}
			// Show preprocessor error
			setErrorMode(main, "#3001", userCode, e1.getMessage(), e1.getFile(), e1.getLine(), false);

			return null;
		}
		// Preprocessor error, reason is unknown
		catch (Exception e1) {

			// Reset source code file register
			if( userCode ) {
				Object[] e = { 1, 0, null };
				main.getLeftPanel().getSourceCodeRegister().clear();
				main.getLeftPanel().getSourceCodeRegister().add(e);
			}
			// Display default message for preprocessor error
			setErrorMode(main, "#3003", userCode, "Preprocessor." + e1, "", -1, false);
				
			return null;
		}

		// Compile the source code
		// Object for the compiler is allocated
		Compiler compiler = new Compiler();

		// No main.getRightPanel().getDebugPanel() modes
		compiler.debug[0] = false;
		compiler.debug[1] = false;

		// Compile current file
		compiler.compile(code);

		// Error displaying and error count
		at.jku.ssw.cmm.compiler.Error e = null;
		
		try {
			// Compile current file
			compiler.compile(code);
		}
		// In case of compiler crash
		catch (Exception e1) {
			setErrorMode(main, "#3004", userCode, "Compiler." + e1, null, -1, false);
			return null;
		}

		e = compiler.getError();
		// compiler returned errors
		if (e != null) {
			setErrorMode(main, "#3005", userCode, e.msg, null, e.line, false);
			return null;
		}

		return compiler.getSymbolTable();
	}
	
	private static void setErrorMode( final GUImain main, final String errorCode, final boolean userCode, final String para1, final String para2, final int para3, final boolean para4 ) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				if(userCode)
					main.getRightPanel().getDebugPanel().setErrorMode(para1, para2, para3, para4);
				else
					new ErrorMessage().showErrorMessage(main.getJFrame(), errorCode, main.getSettings().getLanguage());
			}
		});
	}
}
