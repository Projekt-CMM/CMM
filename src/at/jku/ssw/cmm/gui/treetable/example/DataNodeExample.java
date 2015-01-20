package at.jku.ssw.cmm.gui.treetable.example;

import java.util.ArrayList;
import java.util.List;

import at.jku.ssw.cmm.gui.treetable.DataNode;

public class DataNodeExample extends DataNode {
	
	private final String name;
	private final Object value1;
	private final Object value2;
	
	private boolean questflag;
	
	private final List<DataNode> children;
	
	public DataNodeExample( String name, Object value1, Object value2 ) {
		this.name = name;
		this.value1 = value1;
		this.value2 = value2;
		
		this.children = new ArrayList<>();
		
		this.questflag = false;
	}

	@Override
	public List<DataNode> getChildren() {
		return this.children;
	}

	@Override
	public int getChildCount() {
		return this.children.size();
	}

	@Override
	public Object getValueByColumn(int col) {
		switch(col){
		case 0: return this.name;
		case 1: return this.value1;
		case 2: return this.value2;
		}
		return null;
	}

	@Override
	public void addChild(DataNode n) {
		this.children.add(n);
		System.out.println("added child: " + n.getValueByColumn(0));
	}

	@Override
	public String toString() {
		return this.name;
	}
	
	public void setQuestFlag( boolean flag ) {
		this.questflag = flag;
	}
	
	public boolean getQuestflag() {
		return this.questflag;
	}
}
