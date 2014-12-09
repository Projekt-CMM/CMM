package at.jku.ssw.cmm.gui.treetable.context;

import static at.jku.ssw.cmm.gettext.Language._;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import at.jku.ssw.cmm.gui.GUImain;

public class InitContextMenu {

	public static JPopupMenu initContextMenu( GUImain main, String name, int decl, int call ) {
		
		JPopupMenu menu = new JPopupMenu("hello");
		//menu
		
		JMenuItem id = new JMenuItem(_("Jump to Declaration"));
		menu.add(id);
		id.addActionListener(new ContextMenuListener(main, name, decl));
		
		/*JMenuItem ic = new JMenuItem(_("Jump to Call"));
		menu.add(ic);
		ic.addActionListener(new ContextMenuListener(main, name, call));*/
		
		menu.repaint();
		
		return menu;
	}
}
