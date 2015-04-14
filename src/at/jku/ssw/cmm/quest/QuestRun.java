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

import java.util.List;

import at.jku.ssw.cmm.compiler.Node;
import at.jku.ssw.cmm.compiler.Tab;
import at.jku.ssw.cmm.debugger.Debugger;
import at.jku.ssw.cmm.debugger.StdInOut;
import at.jku.ssw.cmm.interpreter.Interpreter;
import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;
import at.jku.ssw.cmm.interpreter.exceptions.StackUnderflowException;
import at.jku.ssw.cmm.interpreter.memory.Memory;

public class QuestRun implements StdInOut, Debugger {
	
	private String inputStream;
	private String outputStream;
	
	private boolean run = true;
	
	private Interpreter interpreter;

	public String run(Tab symbolTab, String inputStream) throws RunTimeException  {
		
		this.inputStream = inputStream;
		this.outputStream = "";
		
		this.run = true;

		// Allocating memory for interpreter
		Memory.initialize();
		
		// Initialize Interpreter
		interpreter = new Interpreter(this, this);
		
		System.out.println("running");

		// Run main function
		interpreter.run(symbolTab);
		
		try {
			Memory.closeStackFrame();
		} catch (StackUnderflowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outputStream;
	}

	@Override
	public boolean step(Node arg0, List<Integer> readVariables,
			List<Integer> changedVariables) {
		System.out.println("Step: " + arg0);
		return run;
	}

	@Override
	public char in() throws RunTimeException {
		
		char c;
		
		try {
			// Read and remove next character
			c = this.inputStream.charAt(0);
			this.inputStream = this.inputStream.substring(1, this.inputStream.length());
		} catch (Exception e) {
			
			// Throw interpreter runtime error if no more input data available
			throw new RunTimeException("no input data", null, -1);
		}
		
		return c;
	}

	@Override
	public void out(char arg0) {
		outputStream += arg0;
	}
	
	public void stop() {
		this.run = false;
	}

}
