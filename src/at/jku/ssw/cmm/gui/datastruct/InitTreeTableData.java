package at.jku.ssw.cmm.gui.datastruct;

import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.DebugShell;
import at.jku.ssw.cmm.DebugShell.Area;
import at.jku.ssw.cmm.DebugShell.State;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.compiler.Strings;
import at.jku.ssw.cmm.compiler.Struct;
import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.event.debug.StringPopupListener;
import at.jku.ssw.cmm.gui.treetable.DataNode;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.interpreter.memory.MethodContainer;

/**
 * Reads the call stack for all global and local variables to be displayed in the <b>tree table</b>.
 * 
 * @author fabian
 *
 */
public class InitTreeTableData {
	
	/**
	 * Reads the call stack for global and local variables and returns a data model for the tree table.<br>
	 * <b>Note:</b> This method re-creates the whole data model. This will collapse the tree table nodes!
	 * 
	 * @param compiler A reference to the compiler wrapper object
	 * @param main A reference to the main interface which is necessary to invoke mains
	 * @param fileName The name of the current *.cmm file
	 * @return The data model for the tree table
	 */
	public static TreeTableDataModel readSymbolTable( CMMwrapper compiler, GUImain main, String fileName ){
		
		//Create root node
		DataNode node = new DataNode(fileName, "", "", new ArrayList<DataNode>(), -1, -1);
		
		//Add global variables
		node = readVariables( true, compiler.getSymbolTable().curScope.locals, node, Memory.getGlobalPointer(), main );
		
		//Read local variables recursively
		getNextAddress( true, compiler, Memory.getFramePointer(), node, main );
		
		//Create tree table model
		TreeTableDataModel model = new TreeTableDataModel( node );
		
		return model;
	}
	
	/**
	 * Updates a given tree table data model with the new variable values. Should be used to update
	 * the tree table without collapsing the nodes. Should not be used when a new function is called
	 * or at the very first initialization.
	 * 
	 * @param model The tree table data model which is already in use
	 * @param node The root node of the tree
	 * @param compiler A reference to the compiler wrapper object
	 * @param main A reference to the main interface which is necessary to invoke mains
	 * @param fileName The name of the current *.cmm file
	 */
	public static void updateTreeTable( TreeTableDataModel model, DataNode node, CMMwrapper compiler, GUImain main, String fileName ){
		
		//Read global variables
		readVariables( false, compiler.getSymbolTable().curScope.locals, node, Memory.getGlobalPointer(), main );
				
		//Read local variables recursively
		getNextAddress( false, compiler, Memory.getFramePointer(), node, main );
	}
	
	/**
	 * Iterates through the call stack recursively. Reads local variables of functions by calling
	 * "readVariables()"
	 * 
	 * @param init TRUE if the tree table data model is re-created (called from readSymbolTable())<br>
	 * 			FALSE if the data model is updated (called from updateTreeTable())
	 * @param compiler A reference to the compiler wrapper object
	 * @param address The start address of the current function
	 * @param node The data node which has to be updated (the data node of this function)
	 * @param main A reference to the main interface which is necessary to invoke mains
	 */
	private static void getNextAddress( boolean init, CMMwrapper compiler, int address, DataNode node, GUImain main ){
		
		//Name of the function
		String name;
		name = MethodContainer.getMethodName(Memory.loadInt(address-8));

		//Tree table data node of this function
		DataNode funcNode;
		
		Obj obj = findNodeByName(compiler.getSymbolTable().curScope.locals, name);
		
		if( init )
			//Initialize data node if re-creating the data model TODO address
			funcNode = new DataNode( name + "()", "", "", new ArrayList<DataNode>(), -1, obj.line );
		else
			//Update the data node
			funcNode = node.getChild( name + "()", "", "", -1, obj.line );
		
		//Read local variables of the current function
		readVariables( init, obj.locals, funcNode, address, main );
		node.prepend(init, funcNode);
		
		if( name == "main" )
			//Return if reached the bottom of the call stack (main)
			return;
		else
			//Read next function in call stack
			getNextAddress( init, compiler, Memory.loadInt(address-4), node, main );
	}
	
	/**
	 * Reads gloabal or local variables from a given address in the call stack and a given
	 * node in the symbol table.
	 * 
	 * @param init TRUE if the tree table data model is re-created (called from readSymbolTable())<br>
	 * 			FALSE if the data model is updated (called from updateTreeTable())
	 * @param obj The start node in the symbol table
	 * @param node The data node which has to be updated
	 * @param address The start address of the current function
	 * @param main A reference to the main interface which is necessary to invoke mains
	 * @return The updated data node (see papam "node")
	 */
	private static DataNode readVariables( boolean init, Obj obj, DataNode node, int address, GUImain main ){
		
		//Iterate through symbol table
		while( obj != null ){
			//Reading an INTEGER
			if( obj.type.kind == Struct.INT && obj.kind != Obj.PROC && !obj.library ){
				if( obj.kind == Obj.VAR && !obj.isRef ){
					if(!Memory.getMemoryInformation(address + obj.adr).isInitialized)
						node.add(init, new DataNode(obj.name, "int", "undef", null, address + obj.adr, obj.line));
					else
						node.add(init, new DataNode(obj.name, "int", Memory.loadInt(address + obj.adr), null, address + obj.adr, obj.line));
				}
				else if( obj.kind == Obj.VAR && obj.isRef && obj.kind != Obj.PROC ){
					if(!Memory.getMemoryInformation(Memory.loadInt(address + obj.adr)).isInitialized)
						node.add(init, new DataNode(obj.name, "int", "undef", null, address + obj.adr, obj.line));
					else
						node.add(init, new DataNode(obj.name, "int", Memory.loadInt(Memory.loadInt(address + obj.adr)), null, address + obj.adr, obj.line));
				}
				else{
					node.add(init, new DataNode(obj.name, "int", obj.val, null, -1, obj.line));
				}
			}
			//Reading a CHARACTER
			else if( obj.type.kind == Struct.CHAR && obj.kind != Obj.PROC && !obj.library ){
				if( obj.kind == Obj.VAR && !obj.isRef ){
					if(!Memory.getMemoryInformation(address + obj.adr).isInitialized)
						node.add(init, new DataNode(obj.name, "char", "undef", null, address + obj.adr, obj.line));
					else
						node.add(init, new DataNode(obj.name, "char", Memory.loadChar(address + obj.adr), null, address + obj.adr, obj.line));
				}
				else if( obj.kind == Obj.VAR && obj.isRef ){
					if(!Memory.getMemoryInformation(Memory.loadInt(address + obj.adr)).isInitialized)
						node.add(init, new DataNode(obj.name, "char", "undef", null, address + obj.adr, obj.line));
					else
						node.add(init, new DataNode(obj.name, "char", Memory.loadChar(Memory.loadInt(address + obj.adr)), null, address + obj.adr, obj.line));
				}
				else{
					node.add(init, new DataNode(obj.name, "char", obj.val, null, -1, obj.line));
				}
			}
			//Reading a FLOAT
			else if( obj.type.kind == Struct.FLOAT && obj.kind != Obj.PROC && !obj.library ){
				if( obj.kind == Obj.VAR && !obj.isRef ){
					if(!Memory.getMemoryInformation(address + obj.adr).isInitialized)
						node.add(init, new DataNode(obj.name, "float", "undef", null, address + obj.adr, obj.line));
					else
						node.add(init, new DataNode(obj.name, "float", Memory.loadFloat(address + obj.adr), null, address + obj.adr, obj.line));
				}
				else if( obj.kind == Obj.VAR && obj.isRef ){
					if(!Memory.getMemoryInformation(Memory.loadInt(address + obj.adr)).isInitialized)
						node.add(init, new DataNode(obj.name, "float", "undef", null, address + obj.adr, obj.line));
					else
						node.add(init, new DataNode(obj.name, "float", Memory.loadFloat(Memory.loadInt(address + obj.adr)), null, address + obj.adr, obj.line));
				}
				else{
					node.add(init, new DataNode(obj.name, "float", obj.fVal, null, -1, obj.line));
				}
			}
			//Reading a BOOLEAN
			else if( obj.type.kind == Struct.BOOL && obj.kind != Obj.PROC && !obj.library ){
				if( obj.kind == Obj.VAR && !obj.isRef ){
					if(!Memory.getMemoryInformation(address + obj.adr).isInitialized)
						node.add(init, new DataNode(obj.name, "bool", "undef", null, address + obj.adr, obj.line));
					else
						node.add(init, new DataNode(obj.name, "bool", Memory.loadBool(address + obj.adr), null, address + obj.adr, obj.line));
				}
				else if( obj.kind == Obj.VAR && obj.isRef ){
					if(!Memory.getMemoryInformation(Memory.loadInt(address + obj.adr)).isInitialized)
						node.add(init, new DataNode(obj.name, "bool", "undef", null, address + obj.adr, obj.line));
					else
						node.add(init, new DataNode(obj.name, "bool", Memory.loadBool(Memory.loadInt(address + obj.adr)), null, address + obj.adr, obj.line));
				}
				else{
					node.add(init, new DataNode(obj.name, "bool", obj.val==0 ? "false" : "true", null, -1, obj.line));
				}
			}
			//Reading an ARRAY (any type)
			else if( obj.type.kind == Struct.ARR && obj.kind != Obj.PROC && !obj.library ){
				node.add(init, readArray(init, obj, node.getChild(obj.name, "array", "", -1, obj.line), address + obj.adr, main));
			}
			//Reading a STRUCTURE
			else if( obj.type.kind == Struct.STRUCT && obj.kind != Obj.PROC && obj.kind != Obj.TYPE && !obj.library ){
				DataNode n = readVariables( init, obj.type.fields, node.getChild(obj.name, "struct", "", -1, obj.line), address + obj.adr, main );
				node.add(init, n);
			}
			//READING A STRING
			else if( obj.type.kind == Struct.STRING && obj.kind != Obj.PROC && !obj.library ){
				// TODO isInitialized
				// TODO reference
				JButton b = new JButton(Strings.get(Memory.loadStringAddress(address + obj.adr)));
				MouseListener l = new StringPopupListener(main, Strings.get(Memory.loadStringAddress(address + obj.adr)));
				b.addMouseListener(l);
					
				node.add(init, new DataNode(obj.name, "string", b, null, address + obj.adr, obj.line));
			}
			
			//Next symbol table node
			obj = obj.next;
		}
		
		//Return the updated tree table data node
		return node;
	}
	
	/**
	 * Initiates the array reading process. Supports multi-dimensional arrays
	 * 
	 * @param init TRUE if the tree table data model is re-created (called from readSymbolTable())<br>
	 * 			FALSE if the data model is updated (called from updateTreeTable())
	 * @param obj The start node in the symbol table
	 * @param node The data node which has to be updated
	 * @param address The start address of the current function
	 * @param main A reference to the main interface which is necessary to invoke mains
	 * @return The updated data node (see papam "node")
	 */
	private static DataNode readArray( boolean init, Obj obj, DataNode node, int address, GUImain main ){
		
		DebugShell.out(State.LOG, Area.READVAR, "[initTreeTable] Reading array: " + obj.kind);
		
		return readArrayElements(init, obj.type, obj.name, node, address, 0, main);
		
	}
	
	/**
	 * Reads an array. Do not call this method. The array reading process is started with
	 * the method "readArray()"
	 * 
	 * @param init TRUE if the tree table data model is re-created (called from readSymbolTable())<br>
	 * 			FALSE if the data model is updated (called from updateTreeTable())
	 * @param obj The start node in the symbol table
	 * @param name
	 * @param node The data node which has to be updated
	 * @param address The start address of the current function
	 * @param offset The address offset (for multi-dimensional arrays
	 * @param main A reference to the main interface which is necessary to invoke mains
	 * @return The updated data node (see papam "node")
	 */
	public static DataNode readArrayElements( boolean init, Struct obj, String name, DataNode node, int address, int offset, GUImain main ){
		
		int length = obj.elements;
		int size = obj.size / obj.elements;
		
		DebugShell.out(State.LOG, Area.READVAR, "reading array level. size: " + size + ", elements: " + length + ", offset: " + offset );
		
		for( int i = 0; i < length; i++ ){
			
			Object value = "";
			String typeName = "";
			
			if( obj.elemType.elements > 0 ){
				node.add(init,readArrayElements(init, obj.elemType, name, node.getChild("["+i+"]", "array", "", -1, -1), address, offset + size * i, main));
			}
			else{
				if( obj.elemType.kind == Struct.CHAR ){
					typeName = "char";
					value = Memory.loadChar(address + offset + size * i);
					node.add(init, new DataNode("[" + i + "]", typeName, "" + value, null, address + offset + size * i, -1));
				}
				else if( obj.elemType.kind == Struct.FLOAT ){
					typeName = "float";
					value = Memory.loadFloat(address + offset + size * i);
					node.add(init, new DataNode("[" + i + "]", typeName, "" + value, null, address + offset + size * i, -1));
				}
				else if( obj.elemType.kind == Struct.BOOL ){
					typeName = "bool";
					value = Memory.loadBool(address + offset + size * i);
					node.add(init, new DataNode("[" + i + "]", typeName, "" + value, null, address + offset + size * i, -1));
				}
				else if( obj.elemType.kind == Struct.INT ){
					typeName = "int";
					value = Memory.loadInt(address + offset + size * i);
					node.add(init, new DataNode("[" + i + "]", typeName, "" + value, null, address + offset + size * i, -1));
				}
				else if( obj.elemType.kind == Struct.STRUCT ){
					DataNode n = readVariables( init, obj.fields, new DataNode(name, "struct", "", new ArrayList<DataNode>(), -1, -1), address + offset + size * i, main );
					node.add(init, n);
				}
				else if( obj.elemType.kind == Struct.STRING ){
					JButton b = new JButton(Strings.get(Memory.loadStringAddress(address + offset + size * i)));
					MouseListener l = new StringPopupListener(main, Strings.get(Memory.loadStringAddress(address + offset + size * i)));
					b.addMouseListener(l);
					
					node.add(init, new DataNode("[" + i + "]", "string", b, null, address + offset + size * i, -1));
				}
			}
		}
		
		return node;
	}
	
	/**
	 * Creates a default data structure for the tree table which contains nothing else than a root node
	 * with the name of the currently edited *.cmm file. Used during READY GUI mode (eg. when the user
	 * types code, creates a new file, ...)
	 * 
	 * @param fileName The name of the current file
	 * @return The data structure containing a root node with the file name
	 */
	public static DataNode createDataStructure( String fileName ) {
        DataNode root = new DataNode(fileName, "", "", null, -1, -1);
        return root;
    }
	
	/**
	 * Finds a node in the symbol table by its name. Also works with equal names in different
	 * scopes of the source code, because the "start" node limits the search to one function
	 * or data structure.
	 * 
	 * <hr><i>THREAD SAFE by default</i><hr>
	 * 
	 * @param start The start node for the search
	 * @param name The name of the node that is searched
	 * @return The node (if found), null if not found
	 */
	public static Obj findNodeByName( Obj start, String name ){
		
		Obj obj = start;
		while( obj != null ){
			
			if( obj.name.equals(name) ){
				return obj;
			}

			obj = obj.next;
		}
		
		return null;
	}
}
