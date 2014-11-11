package at.jku.ssw.cmm.gui.datastruct;

import java.util.Stack;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.interpreter.memory.MethodContainer;

public class ReadCallStack {
	
	/**
	* Initializes the call stack reading process and returns a Stack of Strings
	* with function names.
	*
	* <hr><i>THREAD SAFE by default</i><hr>
	*
	* @return A Stack of Strings containing the Call Stack function names -
	* the currently running function at the bottom.
	*/
	//TODO unused???
	public static Stack<String> readCallStack(){
		
		Stack<String> stack = new Stack<>();
		
		getNextAddress( Memory.getFramePointer(), stack );
		
		return stack;
	}
	
	/**
	* Helper method for {@link readCallStack}. This method reads the call stack recursively.
	*
	* <hr><i>THREAD SAFE by default</i><hr>
	*
	* @param address Starting address of the next search level
	* @param stack The stack for saving the function names.
	*/
	private static void getNextAddress( int address, Stack<String> stack ){
		
		String name;
		
		int methodID = Memory.loadInt(address-8);
		name = MethodContainer.getMethodName(methodID);
		stack.push(name);
		
		if( name == "main" )
			return;
		else
			getNextAddress( Memory.loadInt(address-4), stack );
	}
	
	/**
	* Initializes a call stack search process which finds a method address by its call index
	* (the last function called has the index 0, the "main" function has the highest index)
	*
	* <hr><i>THREAD SAFE by default</i><hr>
	*
	* @param index The call index of the function
	* @return The address (where the variables start) of the function. The frame pointer
	* would point at this address if this function would be the highest in the
	* call stack.
	*/
	//TODO unused???
	public static int getAddressByIndex( int index ){
		return getAddressRecursive( index, 0, Memory.getFramePointer() );
	}
	
	/**
	* Search method for {@link getAddressByIndex}. This method reads the call stack recursively.
	*
	* <hr><i>THREAD SAFE by default</i><hr>
	*
	* @param index The call index of the function
	* @param level The current level of the search algorithm
	* @param address The address of the function checked by the last recursive call
	* @return The address of the function matching the call index
	*/
	private static int getAddressRecursive( int index, int level, int address ){
		
		if( level == index ){
			return address;
		}
		else{
			level++;
			return getAddressRecursive( index, level, Memory.loadInt(address-4));
		}
	}
}