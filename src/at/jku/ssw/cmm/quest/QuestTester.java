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
import java.util.ArrayList;

import at.jku.ssw.cmm.compiler.Compiler;
import at.jku.ssw.cmm.compiler.Tab;
import at.jku.ssw.cmm.preprocessor.exception.PreprocessorException;
import at.jku.ssw.cmm.gui.file.FileManagerCode;
import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;
import at.jku.ssw.cmm.preprocessor.Preprocessor;
import at.jku.ssw.cmm.quest.exception.CompilerErrorException;

public class QuestTester extends Thread {
	
	/*public static void main(String[] args) {
		
		QuestTester tester = new QuestTester(new TestReplyMock(),
				"packages/01 Einstieg/01 Simples Hello World/input.cmm",
				"packages/01 Einstieg/01 Simples Hello World/ref.cmm",
				"packages/01 Einstieg/01 Simples Hello World/user.cmm",
				null);
		tester.run();
	}*/
	
	public static final String VALIDTYPES = "";

	public static final int STEPS = 5;

	public QuestTester(TestReply testReply, String generator, String verifier, String usercode, Object ignore) {
		
		this.testReply = testReply;
		
		this.generator = generator;
		this.verifier = verifier;
		this.usercode = usercode;
		this.ignore = ignore;

		this.questRun = new QuestRun();
	}
	
	private final TestReply testReply;

	private final String generator;
	private final String verifier;
	private final String usercode;
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
			testReply.finished(new QuestMatchError("Input data file not found", e));
			return;
		} catch (CompilerErrorException e) {
			testReply.finished(new QuestMatchError( "Error when compiling input data generator", e));
			return;
		} catch (PreprocessorException e) {
			testReply.finished(new QuestMatchError( "Preprocessor error in generator source code", e));
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
			symbTab = compile(this.verifier);
			
			referenceOutput = this.questRun.run(symbTab, inputData);
		} catch (RunTimeException e) {
			testReply.finished(new QuestMatchError( "Runtime error when generating reference output data", e));
			return;
		} catch (CompilerErrorException e) {
			testReply.finished(new QuestMatchError( "Error when compiling reference output data generator", e));
			return;
		} catch (FileNotFoundException e) {
			testReply.finished(new QuestMatchError( "Reference output generator file could not be found", e));
			return;
		} catch (PreprocessorException e) {
			testReply.finished(new QuestMatchError( "Preprocessor error in reference source code", e));
			return;
		}
		
		testReply.output("[info] reference: " + referenceOutput + "\n");

		// ----- COMPILE REFERENCE PROGRAM (which is 100% right) -----
		testReply.output("[info] generating user output data");
		testReply.output("[path] " + this.usercode);
		String userOutput = null;
		try {
			symbTab = compile(this.usercode);
			userOutput = this.questRun.run(symbTab, inputData);
		} catch (RunTimeException e) {
			testReply.finished(new QuestMatchError("Runtime error when generating reference output data", e));
			return;
		} catch (CompilerErrorException e) {
			testReply.finished(new QuestMatchError("Error when compiling reference output data generator", e));
			return;
		} catch (FileNotFoundException e) {
			testReply.finished(new QuestMatchError("User's source code could not be found", e));
			return;
		} catch (PreprocessorException e) {
			testReply.finished(new QuestMatchError("Preprocessor error in user's source code", e));
			return;
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

	private String getInputData() throws FileNotFoundException,
			CompilerErrorException, RunTimeException, PreprocessorException {

		// Input generator is .cmm file
		if (this.generator.endsWith(".cmm")) {
			Tab symbTab = compile(this.generator);
			return this.questRun.run(symbTab, null);
		}
		// Input data is in .txt file
		else if (this.generator.endsWith(".txt")) {

			// Create input data file
			File inputFile = new File(this.generator);

			// Check if file exists
			if (!inputFile.exists())
				throw new FileNotFoundException(
						"Input text file does not exist");

			// Read input data
			try {
				return FileManagerCode.readInputDataBlank(new File(this.generator));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		} else
			throw new FileNotFoundException("Invalid input file ending");
	}

	private static Tab compile(String path) throws FileNotFoundException, CompilerErrorException, PreprocessorException {
		File inputFile = new File(path);

		if (!inputFile.exists())
			throw new FileNotFoundException(
					"Input file for compilation not found: " + path);
		
		String sourceCode = null;
		try {
			sourceCode = Preprocessor.expand(FileManagerCode.readSourceCode(inputFile), "", new ArrayList<Object[]>(), new ArrayList<Integer>());
		} catch (IOException e1) {
			// TODO Add error handling
			e1.printStackTrace();
		}
		
		if( sourceCode == null )
			throw new CompilerErrorException("Source code is null", null);

		// Object for the compiler is allocated
		Compiler compiler = new Compiler();

		// No debug modes
		compiler.debug[0] = false;
		compiler.debug[1] = false;

		// Compile current file
		compiler.compile(sourceCode);

		// Error displaying and error count
		at.jku.ssw.cmm.compiler.Error e = compiler.getError();

		if (e != null){
			throw new CompilerErrorException(path, e);
		}

		return compiler.getSymbolTable();
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
