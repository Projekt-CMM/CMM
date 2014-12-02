package at.jku.ssw.cmm.gui.init;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.MenuBarControl;
import at.jku.ssw.cmm.gui.event.MenuBarEventListener;

/**
 * Contains a static method to initialize the menu bar and its drop-down menus for the main GUI
 * 
 * @author fabian
 *
 */
public class InitMenuBar {
	
	/**
	 * This method should only be called when the main GUI is initialized.
	 * It inits the main GUI's menu bar, including drop-down menus and adds event listeners
	 * 
	 * <hr><i>NOT THREAD SAFE, do not call from any other thread than EDT</i><hr>
	 * 
	 * @param jFrame The main GUI window frame
	 * @param jSourcePane A reference to the text area containing the source code
	 * @param settings A reference to the main GUI's configuration object
	 * @param saveDialog A reference to the save dialog manager initialized with the main GUI
	 */
	public static void initFileM( JFrame jFrame, GUImain main, MenuBarControl menuBarControl, MenuBarEventListener listener ){
		
		
		
		//Initialize MenuBar
		JMenuBar menubar = new JMenuBar();
		jFrame.setJMenuBar(menubar);
		
		/* --- MENU: "file" --- */
		JMenu fileM = new JMenu(_("File"));
		menubar.add(fileM);
			
			// --- file -> new ---
			JMenuItem newMI = new JMenuItem(_("New"));
			newMI.addActionListener(listener.newFileHandler);
			newMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
			fileM.add(newMI);
			menuBarControl.add(newMI);
		
			// --- file -> open ---
			JMenuItem openMI = new JMenuItem(_("Open"));
			fileM.add(openMI);
			openMI.addActionListener(listener.openHandler);
			openMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			menuBarControl.add(openMI);
			
			// --- file -> recent files ---
			if( !main.hasAdvancedGUI() ){
				JMenu recentMI = new JMenu(_("Recent files"));
				fileM.add(recentMI);
				menuBarControl.add(recentMI);
				menuBarControl.setRecentMenu(recentMI);
			}
			
			fileM.addSeparator();
			
			// --- file -> save as ---
			JMenuItem saveAsMI = new JMenuItem(_("Save As..."));
			fileM.add(saveAsMI);
			saveAsMI.addActionListener(listener.saveAsHandler);
			saveAsMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
			menuBarControl.add(saveAsMI);
			
			// --- file -> save ---
			JMenuItem saveMI = new JMenuItem(_("Save..."));
			fileM.add(saveMI);
			saveMI.addActionListener(listener.saveHandler);
			saveMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			menuBarControl.add(saveMI);
			
			fileM.addSeparator();
		
			// --- file -> exit ---
			JMenuItem exitMI = new JMenuItem(_("Exit"));
			exitMI.addActionListener(listener.exitHandler);
			exitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
			fileM.add(exitMI);
		
		/* --- MENU: "progress" --- */
		if( main.hasAdvancedGUI() ){
			JMenu questM = new JMenu(_("Progress"));
			menubar.add(questM);
		
			// --- progress -> profile ---
			JMenuItem profileMI = new JMenuItem(_("Select Profile"));
			profileMI.addActionListener(listener.profileHandler);
			profileMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
			questM.add(profileMI);
					
			// --- progress -> quests ---
			JMenuItem questMI = new JMenuItem(_("Select Quest"));
			questMI.addActionListener(listener.questHandler);
			questMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
			questM.add(questMI);
		}
	}
}
