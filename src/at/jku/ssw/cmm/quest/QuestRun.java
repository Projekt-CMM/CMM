package at.jku.ssw.cmm.quest;

import java.util.List;

import at.jku.ssw.cmm.compiler.Node;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.debugger.Debugger;
import at.jku.ssw.cmm.debugger.StdInOut;
import at.jku.ssw.cmm.interpreter.Interpreter;
import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;
import at.jku.ssw.cmm.interpreter.exceptions.StackOverflowException;
import at.jku.ssw.cmm.interpreter.exceptions.StackUnderflowException;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.interpreter.memory.MethodContainer;

public class QuestRun implements StdInOut, Debugger {
	
	private String inputStream;
	private String outputStream;

	public String run(Obj main, String inputStream) throws RunTimeException {
		
		this.inputStream = inputStream;
		this.outputStream = "";

		// Allocating memory for interpreter
		Memory.initialize();

		// Try to open main function
		try {
			Memory.openStackFrame(main.ast.line,
					MethodContainer.getMethodId("main"), main.size);
		} catch (StackOverflowException e1) {
			throw new IllegalStateException(e1);
		}
		
		// Initialize Interpreter
		Interpreter interpreter = new Interpreter(this, this);
		
		System.out.println("running");

		// Run main function
		interpreter.run(main.ast);
		
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
		return true;
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
			throw new RunTimeException("no input data", null);
		}
		
		return c;
	}

	@Override
	public void out(char arg0) {
		outputStream += arg0;
	}

}
