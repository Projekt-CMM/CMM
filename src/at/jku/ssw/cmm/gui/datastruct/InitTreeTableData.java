package at.jku.ssw.cmm.gui.datastruct;

import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.compiler.Strings;
import at.jku.ssw.cmm.compiler.Struct;
import at.jku.ssw.cmm.gui.event.debug.StringPopupListener;
import at.jku.ssw.cmm.gui.popup.PopupInterface;
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
	
	public static TreeTableDataModel readSymbolTable( CMMwrapper compiler, PopupInterface popup, String fileName ){
		
		//Create root node
		DataNode node = new DataNode(fileName, "", "", new ArrayList<DataNode>());
		
		//Add global variables
		node = readVariables( true, compiler.getSymbolTable().curScope.locals, node, Memory.getGlobalPointer(), popup );
		
		//Read local variables recursively
		getNextAddress( true, compiler, Memory.getFramePointer(), node, popup );
		
		//Create tree table model
		TreeTableDataModel model = new TreeTableDataModel( node );
		
		return model;
	}
	
	public static void updateTreeTable( TreeTableDataModel model, DataNode node, CMMwrapper compiler, PopupInterface popup, String fileName ){
		
		//Read global variables
		readVariables( false, compiler.getSymbolTable().curScope.locals, node, Memory.getGlobalPointer(), popup );
				
		//Read local variables recursively
		getNextAddress( false, compiler, Memory.getFramePointer(), node, popup );
	}
	
	private static void getNextAddress( boolean init, CMMwrapper compiler, int address, DataNode node, PopupInterface popup ){
		
		String name;

		int methodID = Memory.loadInt(address-8);
		name = MethodContainer.getMethodName(methodID);

		DataNode funcNode;
		
		if( init )
			funcNode = new DataNode( name + "()", "", "", new ArrayList<DataNode>() );
		else
			funcNode = node.getChild(name + "()", "", "");
		
		readVariables( init, findNodeByName(compiler.getSymbolTable().curScope.locals, name).locals, funcNode, address, popup );
		node.add(init, funcNode);
		
		if( name == "main" )
			return;
		else
			getNextAddress( init, compiler, Memory.loadInt(address-4), node, popup );
	}
	
	private static DataNode readVariables( boolean init, Obj obj, DataNode node, int address, PopupInterface popup ){
		
		while( obj != null ){
				if( obj.type.kind == Struct.INT && obj.kind != Obj.PROC ){
					if( obj.kind == Obj.VAR && !obj.isRef ){
						node.add(init, new DataNode(obj.name, "int", Memory.loadInt(address + obj.adr), null));
					}
					else if( obj.kind == Obj.VAR && obj.isRef && obj.kind != Obj.PROC ){
						node.add(init, new DataNode(obj.name, "int", Memory.loadInt(Memory.loadInt(address + obj.adr)), null));
					}
					else{
						node.add(init, new DataNode(obj.name, "int", obj.val, null));
					}
				}
				if( obj.type.kind == Struct.CHAR && obj.kind != Obj.PROC ){
					if( obj.kind == Obj.VAR && !obj.isRef ){
						node.add(init, new DataNode(obj.name, "char", Memory.loadChar(address + obj.adr), null));
					}
					else if( obj.kind == Obj.VAR && obj.isRef ){
						node.add(init, new DataNode(obj.name, "char", Memory.loadChar(Memory.loadInt(address + obj.adr)), null));
					}
					else{
						node.add(init, new DataNode(obj.name, "char", obj.val, null));
					}
				}
				if( obj.type.kind == Struct.FLOAT && obj.kind != Obj.PROC ){
					if( obj.kind == Obj.VAR && !obj.isRef ){
						node.add(init, new DataNode(obj.name, "float", Memory.loadFloat(address + obj.adr), null));
					}
					else if( obj.kind == Obj.VAR && obj.isRef ){
						node.add(init, new DataNode(obj.name, "float", Memory.loadFloat(Memory.loadInt(address + obj.adr)), null));
					}
					else{
						node.add(init, new DataNode(obj.name, "float", obj.fVal, null));
					}
				}
				if( obj.type.kind == Struct.ARR && obj.kind != Obj.PROC ){
					node.add(init, readArray(init, obj, node.getChild(obj.name, "array", ""), address + obj.adr, popup));
				}
				if( obj.type.kind == Struct.STRUCT && obj.kind != Obj.PROC ){
					DataNode n = readVariables( init, obj.type.fields, node.getChild(obj.name, "struct", ""), address + obj.adr, popup );
					node.add(init, n);
						
				}
				if( obj.type.kind == Struct.STRING && obj.kind != Obj.PROC ){
					JButton b = new JButton(Strings.get(Memory.loadStringAddress(address + obj.adr)));
					MouseListener l = new StringPopupListener(popup, Strings.get(Memory.loadStringAddress(address + obj.adr)));
					b.addMouseListener(l);
					
					node.add(init, new DataNode(obj.name, "string", b, null));
				}
			
			//Next symbol table node
			obj = obj.next;
		}
		return node;
	}
	
	private static DataNode readArray( boolean init, Obj count, DataNode node, int address, PopupInterface popup ){
		
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
				node.add(init, readArray(init, count.type.fields, node.getChild(count.name, "array", ""), address + address + size * i, popup));
			}
			else if( count.kind == Struct.STRUCT ){
				DataNode n = readVariables( init, count.type.fields, new DataNode(count.name, "struct", "", new ArrayList<DataNode>()), address + count.adr, popup );
				node.add(init, n);
			}
			
			node.add(init, new DataNode("" + i, typeName, "" + value, null));
		}
		
		return node;
		
	}
	
	public static DataNode createDataStructure( String fileName ) {
        DataNode root = new DataNode(fileName, "", "", null);
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
	private static Obj findNodeByName( Obj start, String name ){
		
		Obj count = start;
		while( count != null ){
			
			if( count.name.equals(name) ){
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
