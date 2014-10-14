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
    private String type;
    private Object value;
 
    private List<DataNode> children;
    
    public static final char CHANGE_TAG = ' ';
 
    public DataNode(String name, String type, Object value, List<DataNode> children) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.children = children;
 
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
    }
 
    public String getName() {
        return name;
    }
 
    public String getType() {
        return type;
    }
 
    public Object getValue() {
        return value;
    }
 
    public List<DataNode> getChildren() {
        return children;
    }
    
    public int getChildCount(){
    	return this.children.size();
    }
    
    public void markChanged(){
    	this.type += CHANGE_TAG;
    }
    
    public void add( boolean init, DataNode n ){
    	
    	//System.out.println("Checking data node: " + n.print() );
    	
    	if( !init && this.children != null && this.children.size() > 0 ){
    		for( DataNode d : this.children ){
    			if( d.name.equals(n.name) ){
    				/*if( DataNode.equals(d, n) ){
    					d.type = "" + n.type + "*";
    					System.err.println("Value changed (" + d.value + ", " + d.value.getClass() + ") " + n.print());
    				}
    				else
    					d.type = n.type;*/
    				d.value = n.value;
    				d.type = n.type;
    				System.out.println("Found: " + n.name + ", new value is " + n.value + " | " + d.value + ", type is " + n.type + " | " + d.type);
    				return;
    			}
    		}
    	}
    	
    	if( this.children == null ){
    		this.children = new ArrayList<>();
    	}
    		
    	this.children.add(n);
    }
    
    public DataNode getChild(String name, String type, Object value){
    	for( DataNode d : this.children ){
    		if( d.name.equals(name) )
    			return d;
    	}
    	return new DataNode( name, type, value, new ArrayList<DataNode>() );
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
