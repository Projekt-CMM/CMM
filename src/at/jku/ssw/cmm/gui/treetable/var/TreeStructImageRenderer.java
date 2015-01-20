package at.jku.ssw.cmm.gui.treetable.var;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeStructImageRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private final Icon function;
	private final Icon struct;
	private final Icon array;
	private final Icon cmm;
	private final Icon _int;
	private final Icon _char;
	private final Icon _bool;
	private final Icon _string;
	
	public TreeStructImageRenderer(){
	    this.function = new ImageIcon("images/func.png");
	    this.struct = new ImageIcon("images/struct.png");
	    this.array = new ImageIcon("images/array.png");
	    this.cmm = new ImageIcon("images/cmm.png");
	    this._int = new ImageIcon("images/int.png");
	    this._char = new ImageIcon("images/char.png");
	    this._bool = new ImageIcon("images/bool.png");
	    this._string = new ImageIcon("images/string.png");
	}
	
	@Override
	public JComponent getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
		
		VarDataNode node = (VarDataNode) value;

		if (node.getName().endsWith("()")) {
			setOpenIcon(function);
			setClosedIcon(function);
		} else if (node.getName().endsWith(".cmm")) {
			setOpenIcon(cmm);
			setClosedIcon(cmm);
			setLeafIcon(cmm);
		} else if (((String) node.getType()).startsWith("struct")) {
			setOpenIcon(struct);
			setClosedIcon(struct);
		} else if (((String) node.getType()).startsWith("array")) {
			setOpenIcon(array);
			setClosedIcon(array);
		} else if (((String) node.getType()).startsWith("int")) {
			setLeafIcon(_int);
		} else if (((String) node.getType()).startsWith("char")) {
			setLeafIcon(_char);
		} else if (((String) node.getType()).startsWith("bool")) {
			setLeafIcon(_bool);
		} else if (((String) node.getType()).startsWith("string")) {
			setLeafIcon(_string);
		} else {
			setOpenIcon(getDefaultOpenIcon());
			setClosedIcon(getDefaultClosedIcon());
			setLeafIcon(getDefaultLeafIcon());
		}

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
