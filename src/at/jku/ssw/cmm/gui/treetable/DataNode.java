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
 
package at.jku.ssw.cmm.gui.treetable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

/**
 * Contains the data for one node of the tree table.
 * <br>
 * <i>NOTE: </i> This code has been adapted from a tutorial, see {@link http://www.hameister.org/JavaSwingTreeTable.html}
 * 
 * @author fabian
 *
 */
public class DataNode {
	 
    private final String name;
    private Object type;
    private Object value;
    
    private int declaration;
    
    private int address;

    private List<DataNode> children;
    
    public static final char CHANGE_TAG = ' ';
    public static final char READ_TAG = '*';
 
    public DataNode(String name, Object type, Object value, List<DataNode> children, int address, int decl) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.children = children;
        this.declaration = decl;
        this.address = address;
 
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
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
    
    public int getAddress(){
    	return this.address;
    }
    
    public void prepend( boolean init, DataNode n ){
    	if( !init && this.children != null && this.children.size() > 0 ){
    		for( DataNode d : this.children ){
    			if( d.name.equals(n.name) ){
    				d.value = n.value;
    				d.type = n.type;
    				d.address = n.address;
    				d.declaration = n.declaration;
    				return;
    			}
    		}
    	}
    	
    	if( this.children == null ){
    		this.children = new ArrayList<>();
    	}
    		
    	this.children.add(0, n);
    }
    
    public void add( boolean init, DataNode n ){
    	
    	if( !init && this.children != null && this.children.size() > 0 ){
    		for( DataNode d : this.children ){
    			if( d.name.equals(n.name) ){
    				d.value = n.value;
    				d.type = n.type;
    				d.address = n.address;
    				d.declaration = n.declaration;
    				return;
    			}
    		}
    	}
    	
    	if( this.children == null ){
    		this.children = new ArrayList<>();
    	}
    		
    	this.children.add(n);
    }
    
    public DataNode getChild(String name, Object type, Object value, int address, int decl){
    	for( DataNode d : this.children ){
    		if( d.name.equals(name) )
    			return d;
    	}
    	return new DataNode( name, type, value, new ArrayList<DataNode>(), address, decl );
    }
    
    public static boolean equals( DataNode oldNode, DataNode newNode ){
    	
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
    public String toString() {
        return name;
    }
    
    public String print() {
    	return "{ Name: " + this.name + ", Type: " + this.type + ", Value: " + this.value + ", Children: " + this.children + " }";
    }
}
