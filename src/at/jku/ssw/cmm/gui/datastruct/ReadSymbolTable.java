package at.jku.ssw.cmm.gui.datastruct;

import java.awt.event.MouseListener;

import javax.swing.JButton;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.compiler.Struct;
import at.jku.ssw.cmm.gui.GUIrightPanel;
import at.jku.ssw.cmm.gui.event.panel.PanelRunLinkListener;
import at.jku.ssw.cmm.interpreter.memory.Memory;

public class ReadSymbolTable {
	
	/**
	 * Initializes the search and update process for the <b>global</b> variables table of the main GUI.
	 * Do not forget to call "table.repaint" afterwards.
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param compiler A reference to the compiler object containing the symbol table
	 * @param table The table model containing table data
	 * @param listenerModifier A reference to the main GUI right panel wrapper class {@link GUIrightPanel}
	 * @param methodName The name of the method or struct which contains the variables we are searching for
	 * @param type Type of the variable container ( function | struct | array | global ), see {@link StructureContainer}
	 * @param address Start address for the search and read process
	 */
	public static void readGlobals( CMMwrapper compiler, VarTableModel table, GUIrightPanel listenerModifier, String methodName, int type, int address ){
		table.reset();
		
		//Reading a function
		if( type == StructureContainer.GLOBAL && methodName != null ){
			readVariables( compiler.getSymbolTable().curScope.locals,
					Memory.getGlobalPointer(), table, listenerModifier, true );
		}
		//Reading a struct
		else if ( type == StructureContainer.STRUCT && methodName != null ){
			readVariables( findNodeByName(compiler.getSymbolTable().curScope.locals, methodName).type.fields,
					address, table, listenerModifier, true );
		}
		else if ( type == StructureContainer.ARRAY && methodName != null ){
			System.out.println("Opening array: " + methodName);
			readArray( findNodeByName(compiler.getSymbolTable().curScope.locals, methodName), address, table, listenerModifier, true );
		}
	}
	
	/**
	 * Initializes the search and update process for the <b>local</b> variables table of the main GUI.
	 * Do not forget to call "table.repaint" afterwards.
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param compiler A reference to the compiler object containing the symbol table
	 * @param table The table model containing table data
	 * @param listenerModifier A reference to the main GUI right panel wrapper class {@link GUIrightPanel}
	 * @param methodName The name of the method or struct which contains the variables we are searching for
	 * @param type Type of the variable container ( function | struct | array | global ), see {@link StructureContainer}
	 * @param address Start address for the search and read process
	 */
	public static void readLocals( CMMwrapper compiler, VarTableModel table, GUIrightPanel listenerModifier, String methodName, int type, int address ){
		table.reset();
		
		//Reading a function
		if( type == StructureContainer.FUNC && methodName != null ){
			readVariables( findNodeByName(compiler.getSymbolTable().curScope.locals, methodName).locals,
					ReadCallStack.getAddressByIndex(address), table, listenerModifier, false );
		}
		//Reading a struct
		else if ( type == StructureContainer.STRUCT && methodName != null ){
			readVariables( findNodeByName(compiler.getSymbolTable().curScope.locals, methodName).type.fields,
					address, table, listenerModifier, false );
		}
		else if( type == StructureContainer.ARRAY && methodName != null ){
			System.out.println("Opening array: " + methodName);
			readArray( findNodeByName(compiler.getSymbolTable().curScope.locals, methodName), address, table, listenerModifier, false );
		}
		
		
	}
	
	/**
	 * Reads variables of <b>functions and structures</b> and saves them to the given table model.
	 * Creates buttons and listeners for more complex data structures (variable browser).
	 * <br> NOTE: This function is used by the {@link readGlobals} and {@link readLocals} methods above.
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param count The start node in the symbol table
	 * @param address The address of the function or structure which is being read
	 * @param table The table model containing table data
	 * @param listenerModifier A reference to the main GUI right panel wrapper class {@link GUIrightPanel}
	 * @param global TRUE if reading global data, FALSE if reading local data (this is necessary for the
	 * 				"view" button listeners of complex data structures such as arrays or structs)
	 */
	public static void readVariables( Obj count, int address, VarTableModel table, GUIrightPanel listenerModifier, boolean global ){
		
		//Search symbol tree - linked list
		while( count != null ){
			
			//Only use variables and constants
			if(count.kind == Obj.CON || count.kind == Obj.VAR){
				String type = "undef";
				@SuppressWarnings("unused")
				String prefix = "undef";
				Object value = null;
				
				if( count.type.kind == Struct.INT ){
					type = "int";
					if( count.kind == Obj.VAR && !count.isRef ){
						value = Memory.loadInt(address + count.adr);
						prefix = "";
					}
					else if( count.kind == Obj.VAR && count.isRef ){
						value = Memory.loadInt(Memory.loadInt(address + count.adr));
						prefix = "reference";
					}
					else{
						value = count.val;
						prefix = "";
					}
				}
				if( count.type.kind == Struct.CHAR ){
					type = "char";
					if( count.kind == Obj.VAR ){
						value = Memory.loadChar(address + count.adr);
						prefix = "";
					}
					else if( count.kind == Obj.VAR && count.isRef ){
						value = Memory.loadChar(Memory.loadInt(address + count.adr));
						prefix = "reference";
					}
					else{
						value = count.val;
						prefix = "";
					}
				}
				if( count.type.kind == Struct.ARR ){
					type = "array";
					JButton b = new JButton("View");
					System.out.println("Registering array: " + count.name );
					MouseListener l = new PanelRunLinkListener( listenerModifier, count.name, StructureContainer.ARRAY, address + count.adr, global, b );
					b.addMouseListener(l);
					value = b;
				}
				if( count.type.kind == Struct.BOOL )
					type = "bool";
				if( count.type.kind == Struct.FLOAT ){
					type = "float";
					if( count.kind == Obj.VAR ){
						value = Memory.loadFloat(address + count.adr);
						prefix = "";
					}
					else if( count.kind == Obj.VAR && count.isRef ){
						value = Memory.loadFloat(Memory.loadInt(address + count.adr));
						prefix = "reference";
					}
					else{
						value = count.val;
						prefix = "";
					}
				}
				if( count.type.kind == Struct.STRUCT ){
					type = "struct";
					prefix = "";
					JButton b = new JButton("View");
					MouseListener l = new PanelRunLinkListener( listenerModifier, count.name, StructureContainer.STRUCT, address + count.adr, global, b );
					b.addMouseListener(l);
					value = b;
				}
				if( count.type.kind == Struct.STRING ){
					type = "string";
					prefix = "";
					JButton b = new JButton("View");
					MouseListener l = new PanelRunLinkListener( listenerModifier, count.name,
							StructureContainer.STRING, address + count.adr, global, b );
					b.addMouseListener(l);
					value = b;
					
				}
				
				Object[] buffer = {count.name, type, value};
				table.addRow(buffer);
			}
			
			//Next node
			count = count.next;
		}
	}
	
	/**
	 * Reads variables of <b>arrays</b> and saves them to the given table model.
	 * Creates buttons and listeners for more complex data structures (variable browser).
	 * <br> NOTE: This function is used by the {@link readGlobals} and {@link readLocals} methods above.
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param count The start node in the symbol table
	 * @param address The address of the function or structure which is being read
	 * @param table The table model containing table data
	 * @param listenerModifier A reference to the main GUI right panel wrapper class {@link GUIrightPanel}
	 * @param global TRUE if reading global data, FALSE if reading local data (this is necessary for the
	 * 				"view" button listeners of complex data structures such as arrays or structs)
	 */
	private static void readArray( Obj count, int address, VarTableModel table, GUIrightPanel listenerModifier, boolean global ){
		
		int length = count.type.elements;
		int size = count.type.size / count.type.elements;
		
		System.out.println("Reading array...");
		
		for( int i = 0; i < length; i++ ){
			
			Object value = "";
			String typeName = "";
			
			if( count.kind == Struct.INT ){
				typeName = "int";
				value = Memory.loadInt(address + size * i);
			}
			else if( count.kind == Struct.CHAR ){
				typeName = "char";
				value = Memory.loadChar(address + size * i);
			}
			else if( count.kind == Struct.FLOAT ){
				typeName = "float";
				value = Memory.loadFloat(address + size * i);
			}
			else if( count.kind == Struct.BOOL ){
				typeName = "bool";
			}
			else if( count.kind == Struct.ARR ){
				typeName = "array";
				JButton b = new JButton("View");
				System.out.println("Registering array: " + count.name );
				MouseListener l = new PanelRunLinkListener( listenerModifier, count.name, StructureContainer.ARRAY, address + count.adr, global, b );
				b.addMouseListener(l);
				value = b;
			}
			else if( count.kind == Struct.STRUCT ){
				typeName = "struct";
				JButton b = new JButton("View");
				System.out.println("Registering array: " + count.name );
				MouseListener l = new PanelRunLinkListener( listenerModifier, count.name, StructureContainer.STRUCT, address + count.adr, global, b );
				b.addMouseListener(l);
				value = b;
			}
			
			Object[] buffer = {count.name + "[" + i + "]", typeName, value};
			table.addRow(buffer);
		}
		
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
		
		//System.out.println("Looking for: " + name);
		
		Obj count = start;
		while( count != null ){
			
			//System.out.println("checking: " + count.name);
			
			if( count.name.equals(name) ){
				//System.out.println("found: " + count.name);
				return count;
			}
			
			if( count.locals != null ){
				Obj re = findNodeByName( count.locals, name );
				if( re != null )
					return re;
			}
			count = count.next;
		}
		
		return null;
	}
}
