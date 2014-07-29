package at.jku.ssw.cmm.gui.datastruct;

import java.util.ArrayList;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.compiler.Struct;
import at.jku.ssw.cmm.gui.treetable.DataNode;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.interpreter.memory.MethodContainer;

/**
 * Reads the call stack for all global and local variables to be displayed in the tree table
 * 
 * @author fabian
 *
 */
public class ReadCallStackHierarchy {
	
	public static TreeTableDataModel readSymbolTable( CMMwrapper compiler ){
		
		DataNode node = new DataNode("root", "void", "", new ArrayList<DataNode>());
		
		getNextAddress( compiler, Memory.getFramePointer(), node );
		
		TreeTableDataModel model = new TreeTableDataModel( node );
		
		return model;
	}
	
	private static void getNextAddress( CMMwrapper compiler, int address, DataNode node ){
		
		String name;

		int methodID = Memory.loadInt(address-8);
		name = MethodContainer.getMethodName(methodID);

		DataNode funcNode = new DataNode( name + "()", "", "line " + Memory.loadInt(address - 16), new ArrayList<DataNode>() );
		readVariables( ReadSymbolTable.findNodeByName(compiler.getSymbolTable().curScope.locals, name).locals, funcNode, address );
		node.add(funcNode);
		
		if( name == "main" )
			return;
		else
			getNextAddress( compiler, Memory.loadInt(address-4), node );
	}
	
	private static DataNode readVariables( Obj obj, DataNode node, int address ){
		
		while( obj != null ){
				if( obj.type.kind == Struct.INT ){
					if( obj.kind == Obj.VAR && !obj.isRef ){
						node.add(new DataNode(obj.name, "int", Memory.loadInt(address + obj.adr), null));
					}
					else if( obj.kind == Obj.VAR && obj.isRef ){
						node.add(new DataNode(obj.name, "int", Memory.loadInt(Memory.loadInt(address + obj.adr)), null));
					}
					else{
						node.add(new DataNode(obj.name, "int", obj.val, null));
					}
				}
				if( obj.type.kind == Struct.CHAR ){
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
				if( obj.type.kind == Struct.FLOAT ){
					if( obj.kind == Obj.VAR && !obj.isRef ){
						node.add(new DataNode(obj.name, "float", Memory.loadFloat(address + obj.adr), null));
					}
					else if( obj.kind == Obj.VAR && obj.isRef ){
						node.add(new DataNode(obj.name, "float", Memory.loadFloat(Memory.loadInt(address + obj.adr)), null));
					}
					else{
						node.add(new DataNode(obj.name, "float", obj.val, null));
					}
				}
				/*if( obj.type.kind == Struct.ARR ){
					type = "array";
					JButton b = new JButton("View");
					System.out.println("Registering array: " + obj.name );
					MouseListener l = new PanelRunLinkListener( listenerModifier, obj.name, StructureContainer.ARRAY, address + obj.adr, global, b );
					b.addMouseListener(l);
					value = b;
				}*/
				if( obj.type.kind == Struct.STRUCT ){
					DataNode n = readVariables( obj.type.fields, new DataNode(obj.name, "struct", "", new ArrayList<DataNode>()), address );
					node.add(n);
				}
				if( obj.type.kind == Struct.STRING ){
					node.add(new DataNode(obj.name, "string", "", null));
				}
			
			//Next symbol table node
			obj = obj.next;
		}
		return node;
	}
	
	public static DataNode createDataStructure() {
        DataNode root = new DataNode("R1", "R1", Integer.valueOf(10), null);
        return root;
    }
}
