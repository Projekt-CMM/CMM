package at.jku.ssw.cmm.gui.treetable.example;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.gui.treetable.DataNode;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;
import at.jku.ssw.cmm.gui.treetable.TreeTableModel;
import at.jku.ssw.cmm.gui.treetable.var.VarDataNode;

public class TreeTableExample {

	public static void main(String[] args) {
		new TreeTableExample().init();
	}

	private JFrame jFrame;
	
	private TreeTable<DataNode> treeTable;
	private TreeTableDataModel<DataNode> treeTableModel;
	
	private static final String[] columnNames = { "Name", "Progress", "x" };
	private static final Class<?>[] columnTypes = { TreeTableModel.class, String.class, String.class };

	public void init() {
		//Initializes the window for the treeTable example
		this.jFrame = new JFrame("TreeTable Example");
		this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.jFrame.getContentPane().setPreferredSize(new Dimension(800, 500));
		this.jFrame.setMinimumSize(new Dimension(600, 400));
		this.jFrame.setLocation(10, 10);
		
		System.out.println("Starting treeTable example");
		
		//Create the tree
		DataNode root = new DataNodeExample("root", "hello", "world");
		
		root.addChild(new DataNodeExample("l1", "a", "b"));
		root.addChild(new DataNodeExample("l2", "c", "d"));
		root.addChild(new DataNodeExample("l3", "e", "f"));
		root.addChild(new DataNodeExample("l4", "g", "h"));
		
		treeTableModel = new TreeTableDataModel<>(root, columnNames, columnTypes);
		
		treeTable = new TreeTable<>(treeTableModel);
		treeTable.getTableHeader().setReorderingAllowed(false);
		//treeTable.getTableHeader().setVisible(true);
		
		//treeTable.updateTreeModel();
		
		JScrollPane p = new JScrollPane(treeTable);
		//p.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel master = new JPanel();
		master.setLayout(new BorderLayout());
		master.setBorder(new TitledBorder("Tree table"));
		
		master.add(p, BorderLayout.CENTER);
		this.jFrame.add(master);

		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}
}
