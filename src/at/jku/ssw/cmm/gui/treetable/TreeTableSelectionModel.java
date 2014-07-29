package at.jku.ssw.cmm.gui.treetable;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;

/**
 * Takes care that the whole column is activated if the user clicks a part of the tree.
 * Therefore, this class is a part of the connection between the JTable and the JTree.
 * <br>
 * <i>NOTE: </i> This code has been adapted from a tutorial, see {@link http://www.hameister.org/JavaSwingTreeTable.html}
 * 
 * @author fabian
 *
 */
public class TreeTableSelectionModel extends DefaultTreeSelectionModel {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 3017637487875828408L;

	public TreeTableSelectionModel() {
        super();
 
        getListSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// Auto-generated method stub
			}
        });
    }
     
    ListSelectionModel getListSelectionModel() {
        return listSelectionModel;
    }
}
