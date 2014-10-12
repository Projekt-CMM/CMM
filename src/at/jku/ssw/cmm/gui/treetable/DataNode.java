package at.jku.ssw.cmm.gui.treetable;

import java.util.ArrayList;
import java.util.List;

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
    private final String type;
    private Object value;
 
    private List<DataNode> children;
 
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
    
    public void add( boolean init, DataNode n ){
    	
    	System.out.println("Checking data node: " + n.print() );
    	
    	if( !init && this.children != null && this.children.size() > 0 ){
    		for( DataNode d : this.children ){
    			if( d.name.equals(n.name) ){
    				d.value = n.value;
    				System.out.println("Found: " + n.name + ", new value is " + n.value + " | " + d.value);
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
 
    /**
     * @return The name of this node to be displayed in the tree table
     */
    public String toString() {
        return name;
    }
    
    private String print() {
    	return "{ Name: " + this.name + ", Type: " + this.type + ", Value: " + this.value + ", Children: " + this.children + " }";
    }
}
