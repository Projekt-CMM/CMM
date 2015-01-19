package at.jku.ssw.cmm.gui.treetable;

import java.util.List;

public abstract class DataNode {
	public abstract List<DataNode> getChildren();
	public abstract int getChildCount();
	public abstract Object getValueByColumn(int col);
	public abstract void addChild(DataNode n);
	
	@Override
	public abstract String toString();
}
