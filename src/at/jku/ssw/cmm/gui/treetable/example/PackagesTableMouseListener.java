package at.jku.ssw.cmm.gui.treetable.example;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.SwingUtilities;

import at.jku.ssw.cmm.gui.quest.GUIquestSelection;
import at.jku.ssw.cmm.gui.treetable.TableButtonMouseListener;
import at.jku.ssw.cmm.gui.treetable.TreeTable;
import at.jku.ssw.cmm.profile.Quest;

public class PackagesTableMouseListener extends TableButtonMouseListener {

	private GUIquestSelection main;
	private final TreeTable<?> treeTable;	
	
	public PackagesTableMouseListener(GUIquestSelection main, TreeTable<?> t) {
		super(main.getGUImain(), t);
		
		this.main = main;
		this.treeTable = t;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int row = this.treeTable.getTable().rowAtPoint(e.getPoint());
		int col = this.treeTable.getTable().columnAtPoint(e.getPoint());

		//Left Mouse Clicked
		if(SwingUtilities.isLeftMouseButton(e) && this.treeTable.getTable().getValueAt(row, 0) instanceof TreeTableListener){
			TreeTableListener name = (TreeTableListener)this.treeTable.getTable().getValueAt(row, 0);
	
			List<String> fileNames = Quest.ReadFileNames(name.getPath());
			
			 if(fileNames.contains(Quest.FILE_DESCRIPTION) && fileNames.contains(Quest.FILE_STYLE)){
				main.displayURL(name.getPath() + Quest.sep + Quest.FILE_DESCRIPTION, name.getPath() + Quest.sep + Quest.FILE_STYLE);
			} else if(fileNames.contains(Quest.FILE_DESCRIPTION)){
				main.displayURL(name.getPath() + Quest.sep + Quest.FILE_DESCRIPTION);
			}
			
		}else{
			main.displayURL("packages/default/de.html"); 
		}
		
		forwardEventToButton(e,	row, col);
		//super.mouseClicked(e);
	}
	
	
}
