package at.jku.ssw.cmm.gui.treetable.example;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import at.jku.ssw.cmm.gui.treetable.var.VarDataNode;

public class TreePackageImageRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private final Icon scroll;
	
	public TreePackageImageRenderer(){
	    this.scroll = new ImageIcon("images/scroll.png");
	}
	
	@Override
	public JComponent getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
		
		DataNodeExample node = (DataNodeExample) value;

		setLeafIcon(scroll);

		super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row,
				hasFocus);
		return this;
	}
	
	@Override
    public Color getBackgroundNonSelectionColor() {
        return null;
    }

    @Override
    public Color getBackgroundSelectionColor() {
        return null;
    }

    @Override
    public Color getBackground() {
        return null;
    }
}
