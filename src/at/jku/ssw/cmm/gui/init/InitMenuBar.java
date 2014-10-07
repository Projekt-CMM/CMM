package at.jku.ssw.cmm.gui.init;

import static at.jku.ssw.cmm.gettext.Language._;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.GUImainSettings;
import at.jku.ssw.cmm.gui.debug.GUIdebugPanel;
import at.jku.ssw.cmm.gui.event.LanguageListener;
import at.jku.ssw.cmm.gui.event.MenuBarEventListener;
import at.jku.ssw.cmm.gui.file.SaveDialog;

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
	public static void initFileM( JFrame jFrame, RSyntaxTextArea jSourcePane, GUImain main, GUImainSettings settings, GUIdebugPanel modifier, SaveDialog saveDialog ){
		
		//Initialize listener for the menu bar
		MenuBarEventListener listener = new MenuBarEventListener( jFrame, jSourcePane, main, settings, modifier, saveDialog );
		
		//Initialize MenuBar
		JMenuBar menubar = new JMenuBar();
		jFrame.setJMenuBar(menubar);
		
		/* --- MENU: "file" --- */
		JMenu fileM = new JMenu(_("File"));
		menubar.add(fileM);
			
			// --- file -> new ---
			JMenuItem newMI = new JMenuItem(_("New"));
			newMI.addActionListener(listener.newFileHandler);
			fileM.add(newMI);
		
			// --- file -> open ---
			JMenuItem openMI = new JMenuItem(_("Open"));
			fileM.add(openMI);
			openMI.addActionListener(listener.openHandler);
			
			fileM.addSeparator();
			
			// --- file -> save as ---
			JMenuItem saveAsMI = new JMenuItem(_("Save As..."));
			fileM.add(saveAsMI);
			saveAsMI.addActionListener(listener.saveAsHandler);
			
			// --- file -> save ---
			JMenuItem saveMI = new JMenuItem(_("Save..."));
			fileM.add(saveMI);
			saveMI.addActionListener(listener.saveHandler);
			
			fileM.addSeparator();
		
			// --- file -> exit ---
			JMenuItem exitMI = new JMenuItem(_("Exit"));
			exitMI.addActionListener(listener.exitHandler);
			fileM.add(exitMI);
		
		/* --- MENU: "progress" --- */
		if( GUImain.ADVANCED_GUI ){
		JMenu questM = new JMenu(_("Progress"));
		menubar.add(questM);
		
			// --- progress -> profile ---
			JMenuItem profileMI = new JMenuItem(_("Select Profile"));
			profileMI.addActionListener(listener.profileHandler);
			questM.add(profileMI);
					
			// --- progress -> quests ---
			JMenuItem questMI = new JMenuItem(_("Select Quest"));
			questMI.addActionListener(listener.questHandler);
			questM.add(questMI);
		
		/* --- MENU: "language" --- */
		JMenu langM = new JMenu(_("Language"));
		menubar.add(langM);
		
			File folder = new File("po");
			File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		    	if (listOfFiles[i].isFile()) {
		    		System.out.println("File " + listOfFiles[i].getName());
		    		if( listOfFiles[i].getName().endsWith(".po") ){
			    		JMenuItem mi = new JMenuItem(listOfFiles[i].getName());
			    		mi.addActionListener(new LanguageListener(main, listOfFiles[i].getName()));
			    		langM.add(mi);
		    		}
		    	}
		    }
		}
	}
}
