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
 
package at.jku.ssw.cmm.gui.treetable.var;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import at.jku.ssw.cmm.gui.treetable.DataNode;

/**
 * Contains the data for one node of the tree table.
 * <br>
 * <i>NOTE: </i> This code has been adapted from a tutorial, see {@link http://www.hameister.org/JavaSwingTreeTable.html}
 * 
 * @author fabian
 *
 */
public class VarDataNode extends DataNode {
	 
    private final String name;
    private Object type;
    private Object value;
    
    private int declaration;
    private int call;
    
    private int address;

    private List<DataNode> children;
    
    public static final char CHANGE_TAG = ' ';
    public static final char READ_TAG = '*';
    
    public VarDataNode(String name, Object type, Object value, List<DataNode> arrayList, int address, int decl, int call) {
    	this.name = name;
        this.type = type;
        this.value = value;
        this.children = arrayList;
        this.declaration = decl;
        this.call = call;
        this.address = address;
 
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
    }
 
    public VarDataNode(String name, Object type, Object value, List<DataNode> arrayList, int address, int decl) {
        this(name, type, value, arrayList, address, decl, -1);
    }
 
    public String getName() {
        return name;
    }
 
    public Object getType() {
        return type;
    }
 
    public Object getValue() {
        return value;
    }
    
    @Override
	public Object getValueByColumn(int col) {
		switch( col ){
		case 0: return this.name;
		case 1: return this.type;
		case 2: return this.value;
		}
		return null;
	}
    
    public void setValue(Object value) {
    	this.value = value;
    }
 
    public List<DataNode> getChildren() {
        return children;
    }
    
    public int getChildCount(){
    	return this.children.size();
    }
    
    public void markChanged(){
    	this.type = "" + this.type + CHANGE_TAG;
    }
    
    public void markRead(){
    	this.type = "" + this.type + READ_TAG;
    }
    
    public int getDeclarationLine(){
    	return this.declaration;
    }
    
    public int getCallLine(){
    	return this.call;
    }
    
    public int getAddress(){
    	return this.address;
    }
    
    public void prepend( boolean init, VarDataNode n ){
    	if( !init && this.children != null && this.children.size() > 0 ){
    		for( DataNode d : this.children ){
    			if( ((VarDataNode)d).name.equals(n.name) ){
    				((VarDataNode)d).value = n.value;
    				((VarDataNode)d).type = n.type;
    				((VarDataNode)d).address = n.address;
    				((VarDataNode)d).declaration = n.declaration;
    				return;
    			}
    		}
    	}
    	
    	if( this.children == null ){
    		this.children = new ArrayList<>();
    	}
    		
    	this.children.add(0, n);
    }
    
    public void add( boolean init, VarDataNode n ){
    	
    	if( !init && this.children != null && this.children.size() > 0 ){
    		for( DataNode d : this.children ){
    			if( ((VarDataNode)d).name.equals(n.name) ){
    				((VarDataNode)d).value = n.value;
    				((VarDataNode)d).type = n.type;
    				((VarDataNode)d).address = n.address;
    				((VarDataNode)d).declaration = n.declaration;
    				return;
    			}
    		}
    	}
    	
    	if( this.children == null ){
    		this.children = new ArrayList<>();
    	}
    		
    	this.children.add(n);
    }
    
    public VarDataNode getChild(String name, Object type, Object value, int address, int decl, int call){
    	for( DataNode d : this.children ){
    		if( ((VarDataNode)d).name.equals(name) )
    			return (VarDataNode)d;
    	}
    	return new VarDataNode( name, type, value, new ArrayList<DataNode>(), address, decl );
    }
    
    public VarDataNode getChild(String name, Object type, Object value, int address, int decl){
    	return this.getChild(name, type, value, address, decl, -1);
    }
    
    public static boolean equals( VarDataNode oldNode, VarDataNode newNode ){
    	
    	if( oldNode.getValue() instanceof JButton && newNode.getValue() instanceof JButton ){
    		JButton b1 = (JButton)oldNode.getValue();
    		JButton b2 = (JButton)newNode.getValue();
    		
    		return !b1.getText().equals(b2.getText());
    	}
    	else
    		return !oldNode.getValue().equals(newNode.getValue());
    }
 
    /**
     * @return The name of this node to be displayed in the tree table
     */
    @Override
    public String toString() {
        return name;
    }
    
    public String print() {
    	return "{ Name: " + this.name + ", Type: " + this.type + ", Value: " + this.value + ", Children: " + this.children + " }";
    }

	@Override
	public void addChild(DataNode n) {
		// TODO Auto-generated method stub
		
	}
}
