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
 
package at.jku.ssw.cmm.quest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import at.jku.ssw.cmm.compiler.Tab;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.debug.CompileManager;
import at.jku.ssw.cmm.gui.file.FileManagerCode;
import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;

public class QuestTester extends Thread {

	public static final String VALIDTYPES = "";

	public static final int STEPS = 5;

	public QuestTester(TestReply testReply, GUImain main, File generator, File verifier, Object usercode, Object ignore) {
		
		this.testReply = testReply;
		this.main = main;
		
		this.generator = generator;
		this.verifier = verifier;
		this.usercode = usercode;
		this.ignore = ignore;

		this.questRun = new QuestRun();
	}
	
	private final TestReply testReply;
	private final GUImain main;

	private final File generator;
	private final File verifier;
	private final Object usercode;
	private final Object ignore;	//TODO user regex

	private final QuestRun questRun;

	public void run() {

		// ----- READ INPUT DATA -----
		String inputData = null;
		testReply.output("[info] generating input data");
		testReply.output("[path] " + this.generator);
		
		try {
			// Read or generate input data
			inputData = getInputData();
		} catch (RunTimeException e) {
			testReply.finished(new QuestMatchError( "Runtime error when generating input data", e));
			return;
		} catch (FileNotFoundException e) {
			testReply.finished(new QuestMatchError( "Input data file not found", e));
			return;
		}

		// Double-check if everything worked
		if (inputData == null){
			testReply.finished(new QuestMatchError("Unknown error after reading input data", null));
			return;
		}
		
		testReply.output("[info] input: " + inputData + "\n");

		// ----- COMPILE REFERENCE PROGRAM (which is 100% right) -----
		testReply.output("[info] generating reference output data");
		testReply.output("[path] " + this.verifier);
		String referenceOutput = null;
		Tab symbTab;
		try {
			symbTab = compile(this.verifier, false);
			if( symbTab != null )
				referenceOutput = this.questRun.run(symbTab, inputData);
		} catch (RunTimeException e) {
			testReply.finished(new QuestMatchError( "Runtime error when generating reference output data", e));
			return;
		} catch (FileNotFoundException e) {
			testReply.finished(new QuestMatchError( "Reference output generator file could not be found", e));
			return;
		}
		
		testReply.output("[info] reference: " + referenceOutput + "\n");

		// ----- COMPILE USER'S PROGRAM  -----
		testReply.output("[info] generating user output data");
		if( this.usercode instanceof File )
			testReply.output("[path] " + ((File) (this.usercode)).getPath());
		else if( this.usercode instanceof String )
			testReply.output("[source] user's code");
		String userOutput = null;
		try {
			symbTab = compile(this.usercode, true);
			if( symbTab != null )
				userOutput = this.questRun.run(symbTab, inputData);
		} catch (final RunTimeException e) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					if ( e.getNode() != null && e.getNode().line > 0)
						main.getRightPanel().getDebugPanel().setErrorMode(e.getMessage(), null, e.getNode().line, true);
					else if( e.getLine() > 0 )
						main.getRightPanel().getDebugPanel().setErrorMode(e.getMessage(), null, e.getLine(), true);
					else
						main.getRightPanel().getDebugPanel().setErrorMode(e.getMessage(), null, -1, true);
				}
			});
			return;
		} catch (FileNotFoundException e) {
			testReply.finished(new QuestMatchError("User's source code could not be found", e));
			return;
		} catch (Exception e) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					main.getRightPanel().getDebugPanel().setErrorMode("unknown error", null, -1, true);
				}
			});
		}
		
		testReply.output("[info] user: " + userOutput + "\n");
		
		System.out.println("Out1: " + referenceOutput);
		System.out.println("Out2: " + userOutput);
		
		// Double-check if output data is available
		if( referenceOutput == null || userOutput == null ){
			testReply.finished(new QuestMatchError("Missing output data after generating", null));
			return;
		}
		
		if( equalOutput(referenceOutput, userOutput) )
			// Returning null means "no error"
			testReply.finished(null);
		else
			testReply.finished(new QuestMatchError("Output does not match", null));
		
		return;
	}

	private String getInputData() throws FileNotFoundException, RunTimeException  {

		// Input generator is .cmm file
		if (this.generator.getPath().endsWith(".cmm")) {
			Tab symbTab = compile(this.generator, false);
			return this.questRun.run(symbTab, null);
		}
		// Input data is in .txt file
		else if (this.generator.getPath().endsWith(".txt")) {

			// Check if file exists
			if (!this.generator.exists())
				throw new FileNotFoundException(
						"Input text file does not exist");

			// Read input data
			try {
				return FileManagerCode.readInputDataBlank(this.generator);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		} else
			throw new FileNotFoundException("Invalid input file ending");
	}

	private Tab compile(Object data, boolean userCode) throws FileNotFoundException {

		String sourceCode = null;
		
		if( data instanceof File ) {
			File inputFile = (File)data;
			
			if (!inputFile.exists())
				throw new FileNotFoundException(
						"Input file for compilation not found: " + inputFile.getPath());
			
			try {
				sourceCode = FileManagerCode.readSourceCode(inputFile);
			} catch (IOException e1) {
				throw new FileNotFoundException("Source file not found");
			}
		}
		else if( data instanceof String ) {
			sourceCode = (String)data;
		}
		
		return CompileManager.compile(sourceCode, main, userCode);
	}
	
	private boolean equalOutput(String s1, String s2){
		
		if( this.ignore == null )
			return s1.equals(s2);
		
		if( this.ignore instanceof String[] ) {
			String[] ignore = (String[]) this.ignore;
			// Remove signs which shall be ignored
			if( ignore != null ){
				for( String c : ignore ){
					s1 = s1.replace(c, "");
					s2 = s2.replace(c, "");
				}
			}
			return s1.equals(s2);
		}
		if( this.ignore instanceof String ) {
			s1 = s1.replaceAll((String) this.ignore, "");
			s2 = s2.replaceAll((String) this.ignore, "");
			return s1.equals(s2);
		}
		throw new IllegalArgumentException();
	}
}
