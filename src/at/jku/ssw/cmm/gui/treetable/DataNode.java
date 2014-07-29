package at.jku.ssw.cmm.gui.treetable;

import java.util.Collections;
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
    private final Object value;
 
    private List<DataNode> children;
 
    public DataNode(String name, String type, Object value, List<DataNode> children) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.children = children;
 
        if (this.children == null) {
            this.children = Collections.emptyList();
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
    
    public void add( DataNode n ){
    	this.children.add(n);
    }
 
    /**
     * @return The name of this node to be displayed in the tree table
     */
    public String toString() {
        return name;
    }
}
