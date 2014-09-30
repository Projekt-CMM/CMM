package at.jku.ssw.cmm.gui.datastruct;

import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.compiler.Strings;
import at.jku.ssw.cmm.compiler.Struct;
import at.jku.ssw.cmm.gui.GUIdebugPanel;
import at.jku.ssw.cmm.gui.event.debug.PanelRunLinkListener;
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
public class ReadCallStackHierarchy {
	
	public static TreeTableDataModel readSymbolTable( CMMwrapper compiler, GUIdebugPanel listenerModifier ){
		
		//Create root node
		DataNode node = new DataNode("root", "void", "", new ArrayList<DataNode>());
		
		//Add global variables
		node = readVariables(compiler.getSymbolTable().curScope.locals, node, Memory.getGlobalPointer(), listenerModifier);
		
		//Read local variables recursively
		getNextAddress( compiler, Memory.getFramePointer(), node, listenerModifier );
		
		//Create tree table model
		TreeTableDataModel model = new TreeTableDataModel( node );
		
		return model;
	}
	
	private static void getNextAddress( CMMwrapper compiler, int address, DataNode node,GUIdebugPanel listenerModifier ){
		
		String name;

		int methodID = Memory.loadInt(address-8);
		name = MethodContainer.getMethodName(methodID);

		DataNode funcNode = new DataNode( name + "()", "", "", new ArrayList<DataNode>() );
		readVariables( ReadSymbolTable.findNodeByName(compiler.getSymbolTable().curScope.locals, name).locals, funcNode, address, listenerModifier );
		node.add(funcNode);
		
		if( name == "main" )
			return;
		else
			getNextAddress( compiler, Memory.loadInt(address-4), node, listenerModifier );
	}
	
	private static DataNode readVariables( Obj obj, DataNode node, int address, GUIdebugPanel listenerModifier ){
		
		while( obj != null ){
				if( obj.type.kind == Struct.INT && obj.kind != Obj.PROC ){
					if( obj.kind == Obj.VAR && !obj.isRef ){
						node.add(new DataNode(obj.name, "int", Memory.loadInt(address + obj.adr), null));
					}
					else if( obj.kind == Obj.VAR && obj.isRef && obj.kind != Obj.PROC ){
						node.add(new DataNode(obj.name, "int", Memory.loadInt(Memory.loadInt(address + obj.adr)), null));
					}
					else{
						node.add(new DataNode(obj.name, "int", obj.val, null));
					}
				}
				if( obj.type.kind == Struct.CHAR && obj.kind != Obj.PROC ){
					if( obj.kind == Obj.VAR && !obj.isRef ){
						node.add(new DataNode(obj.name, "char", Memory.loadChar(address + obj.adr), null));
					}
					else if( obj.kind == Obj.VAR && obj.isRef ){
						node.add(new DataNode(obj.name, "char", Memory.loadChar(Memory.loadInt(address + obj.adr)), null));
					}
					else{
						node.add(new DataNode(obj.name, "char", obj.val, null));
					}
				}
				if( obj.type.kind == Struct.FLOAT && obj.kind != Obj.PROC ){
					if( obj.kind == Obj.VAR && !obj.isRef ){
						node.add(new DataNode(obj.name, "float", Memory.loadFloat(address + obj.adr), null));
					}
					else if( obj.kind == Obj.VAR && obj.isRef ){
						node.add(new DataNode(obj.name, "float", Memory.loadFloat(Memory.loadInt(address + obj.adr)), null));
					}
					else{
						node.add(new DataNode(obj.name, "float", obj.fVal, null));
					}
				}
				if( obj.type.kind == Struct.ARR && obj.kind != Obj.PROC ){
					node.add(readArray(obj, address + obj.adr, listenerModifier));
				}
				if( obj.type.kind == Struct.STRUCT && obj.kind != Obj.PROC ){
					DataNode n = readVariables( obj.type.fields, new DataNode(obj.name, "struct", "", new ArrayList<DataNode>()), address + obj.adr, listenerModifier );
					node.add(n);
				}
				if( obj.type.kind == Struct.STRING && obj.kind != Obj.PROC ){
					JButton b = new JButton(Strings.get(Memory.loadStringAddress(address + obj.adr)));
					MouseListener l = new PanelRunLinkListener( listenerModifier, obj.name,
							StructureContainer.STRING, address + obj.adr, false, b );
					b.addMouseListener(l);
					
					node.add(new DataNode(obj.name, "string", b, null));
				}
			
			//Next symbol table node
			obj = obj.next;
		}
		return node;
	}
	
	private static DataNode readArray( Obj count, int address, GUIdebugPanel listenerModifier ){
		
		int length = count.type.elements;
		int size = count.type.size / count.type.elements;
		
		System.out.println("Reading array...");
		
		DataNode node = new DataNode( count.name, "array", "", new ArrayList<DataNode>() );
		
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
				node.add(readArray(count.type.fields, address + address + size * i, listenerModifier));
			}
			else if( count.kind == Struct.STRUCT ){
				DataNode n = readVariables( count.type.fields, new DataNode(count.name, "struct", "", new ArrayList<DataNode>()), address + count.adr, listenerModifier );
				node.add(n);
			}
			
			node.add(new DataNode("" + i, typeName, "" + value, null));
		}
		
		return node;
		
	}
	
	public static DataNode createDataStructure() {
        DataNode root = new DataNode("R1", "R1", Integer.valueOf(10), null);
        return root;
    }
}
