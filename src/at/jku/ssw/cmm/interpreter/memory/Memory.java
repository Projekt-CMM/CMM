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
 
package at.jku.ssw.cmm.interpreter.memory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.jku.ssw.cmm.compiler.Node;
import at.jku.ssw.cmm.interpreter.Interpreter;
import at.jku.ssw.cmm.interpreter.exceptions.RunTimeException;
import at.jku.ssw.cmm.interpreter.exceptions.StackOverflowException;
import at.jku.ssw.cmm.interpreter.exceptions.StackUnderflowException;
import at.jku.ssw.cmm.interpreter.memory.MemoryInformation;

public final class Memory {
	private static final int MEMORY_SIZE = 8388608; // 8MB

	// 0: Return value
	// 4: start global space
	// ...
	// MEMORY_SIZE: top of stack

	private static final ByteBuffer memory = ByteBuffer.allocate(MEMORY_SIZE);
	{
		for (int i = 0; i < MEMORY_SIZE; i++) {
			memory.put(i, (byte) 0);
		}
	}
	
	private static Map<Integer,MemoryInformation> memoryInformations = new HashMap<>();
	
	private static int framePointer;
	private static int stackPointer;
	private static int globalPointer;

	private static int returnValue;
	private static float floatreturnValue;

	public static List<Integer> readVariables, changedVariables;
	
	public static void initialize() {
		framePointer = MEMORY_SIZE / 2;
		stackPointer = MEMORY_SIZE / 2;
		globalPointer = 0;

		readVariables = new ArrayList<Integer>();
		changedVariables = new ArrayList<Integer>();
		
		// initialize arrays
		memoryInformations.clear();
		for (int i = 0; i < MEMORY_SIZE; i++) {
			memory.put(i, (byte) 0);
		}
	}

	/*
	 * Call Stack Using, Line Number, Frame Size + Frame Pointer + ProcID
	 */
	public static void openStackFrame(int lineNumber, int procId, int frameSize)
			throws StackOverflowException {
		memory.putInt(stackPointer, lineNumber); // Line Nummer
		stackPointer += 4;
		memory.putInt(stackPointer, procId); // Methoden ID: name?
		stackPointer += 4;
		memory.putInt(stackPointer, framePointer); // Dynamic Link
		stackPointer += 4;

		framePointer = stackPointer;
		stackPointer += frameSize; // Variablen Size

	}

	@SuppressWarnings("unused")
	private static void assertStackOverflow() throws StackOverflowException {
		if (stackPointer >= MEMORY_SIZE)
			throw new StackOverflowException();

	}

	public static void closeStackFrame() throws StackUnderflowException {
		stackPointer = framePointer;
		stackPointer -= 4;
		framePointer = memory.getInt(stackPointer);
		stackPointer -= 8;
	}

	@SuppressWarnings("unused")
	private static void assertStackUnderflow() throws StackUnderflowException {
		if (framePointer <= 0)
			throw new StackUnderflowException();
	}

	public static boolean loadBool(int address) {
		readVariables.add(address);
		if(memory.get(address) == 0)
			return false;
		else
			return true;
	}

	public static boolean loadBoolSave(int address, Node p) throws RunTimeException {
		checkIfMemoryIsInitialized(address, p);
		return loadBool(address);
	}
	
	public static void storeBool(int address, boolean value) {
		changedVariables.add(address);
		getMemoryInformation(address).isInitialized = true;
		if(value)
			memory.put(address, (byte)0x01);
		else
			memory.put(address, (byte)0x00);
	}
	
	public static int loadInt(int address) {
		readVariables.add(address);
		return memory.getInt(address);
	}
	
	public static int loadIntSave(int address, Node p) throws RunTimeException {
		checkIfMemoryIsInitialized(address, p);
		return loadInt(address);
	}
	
	public static void storeInt(int address, int value) {
		changedVariables.add(address);
		getMemoryInformation(address).isInitialized = true;
		memory.putInt(address, value);
	}

	public static char loadChar(int address) {
		readVariables.add(address);
		return memory.getChar(address);
	}

	public static char loadCharSave(int address, Node p) throws RunTimeException {
		checkIfMemoryIsInitialized(address, p);
		return loadChar(address);
	}
	
	public static void storeChar(int address, char value) {
		changedVariables.add(address);
		getMemoryInformation(address).isInitialized = true;
		memory.putChar(address, value);
	}

	public static float loadFloat(int address) {
		readVariables.add(address);
		return memory.getFloat(address);
	}

	public static float loadFloatSave(int address, Node p) throws RunTimeException {
		checkIfMemoryIsInitialized(address, p);
		return loadFloat(address);
	}
	
	public static void storeFloat(int address, float value) {
		changedVariables.add(address);
		getMemoryInformation(address).isInitialized = true;
		memory.putFloat(address, value);
	}

	public static void storeStringAdress(int address, int value) {
		changedVariables.add(address);
		getMemoryInformation(address).isInitialized = true;
		memory.putInt(address, value);
	}

	public static int loadStringAddress(int address) {
		readVariables.add(address);
		return memory.getInt(address);
	}

	public static int loadStringAddressSave(int address, Node p) throws RunTimeException {
		checkIfMemoryIsInitialized(address, p);
		return loadStringAddress(address);
	}
	
	public static void setBoolReturnValue(boolean value) {
		if(value)
			returnValue = 1;
		else
			returnValue = 0;
	}
	
	public static void setIntReturnValue(int value) {
		returnValue = value;
	}

	public static void setCharReturnValue(char value) {
		returnValue = (int) value;
	}

	public static void setFloatReturnValue(float value) {
		floatreturnValue = value;
	}

	public static boolean getBoolReturnValue() {
		if(returnValue == 0)
			return false;
		else
			return true;
	}
	
	public static int getIntReturnValue() {
		return returnValue;
	}

	public static char getCharReturnValue() {
		return (char) returnValue;
	}

	public static float getFloatReturnValue() {
		return floatreturnValue;
	}

	// TODO
	/*
	 * private static void assertAddress(int offset) {
	 * 
	 * return false; }
	 */

	public static int getFramePointer() {
		return framePointer;
	}

	public static int getStackPointer() {
		return stackPointer;
	}

	public static int getGlobalPointer() {
		return globalPointer;
	}
	
	public static MemoryInformation getMemoryInformation(int address) {
		if(!memoryInformations.containsKey(address))
			memoryInformations.put(address, new MemoryInformation());
		return memoryInformations.get(address);
	}
	
	public static void checkIfMemoryIsInitialized(int address, Node p) throws RunTimeException {
		if(!getMemoryInformation(address).isInitialized) {
			if(getMemoryInformation(address).varName != null)
				throw new RunTimeException(getMemoryInformation(address).varName + " is not initialized", p, Interpreter.currentLine);
			else
				throw new RunTimeException("variable is not initialized", p, Interpreter.currentLine);
		}
	}

	public static void copyMemoryRegion(int source, int destination, int size) throws RunTimeException {
		// check if size-property is correct
		if(size < 0)
			throw new RunTimeException("negative structure size", null, Interpreter.currentLine);

		for(int i = 0; i < size; i++) {
			if(memoryInformations.containsKey(source+i)) {
				// clone memoryInformations if possible
				try {
					memoryInformations.put(destination+i, memoryInformations.get(source+i).clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
					throw new RunTimeException("this error should never happen", null, Interpreter.currentLine);
				}
			} else {
				// remove old memory Informations if required
				memoryInformations.remove(destination+i);
			}
			
			// detect changes
			changedVariables.add(destination+i);
			readVariables.add(source+i);
			
			// clone data
			memory.put(destination+i, memory.get(source+i));
		}
	}
}
