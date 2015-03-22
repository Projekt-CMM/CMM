/*
 *  This file is part of C-Compact.
 *
 *  C-Compact is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  C-Compact is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with C-Compact. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (c) 2014-2015 Fabian Hummer
 *  Copyright (c) 2014-2015 Thomas Pointhuber
 *  Copyright (c) 2014-2015 Peter Wassermair
 */
 
package at.jku.ssw.cmm.gui.event;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import at.jku.ssw.cmm.gui.GUImain;
import at.jku.ssw.cmm.gui.credits.Credits;
import at.jku.ssw.cmm.gui.debug.ErrorMessage;
import at.jku.ssw.cmm.gui.file.FileManagerCode;
import at.jku.ssw.cmm.gui.properties.GUILanguage;
import at.jku.ssw.cmm.gui.properties.GUIProperties;
import at.jku.ssw.cmm.launcher.GUILauncherMain;
import at.jku.ssw.cmm.quest.importexport.ExportProfile;
import at.jku.ssw.cmm.quest.importexport.ImportQuests;

/**
 * Contains event listeners for the menu bar in the main GUI.
 * 
 * @author fabian
 *
 */
public class MenuBarEventListener {

	/**
	 * Contains event listeners for the menu bar in the main GUI.
	 * 
	 * @param jFrame
	 *            The main window frame
	 * @param main
	 *            Reference to the main GUI
	 */
	public MenuBarEventListener(JFrame jFrame, GUImain main) {
		this.jFrame = jFrame;
		this.main = main;
	}

	/**
	 * The main window frame
	 */
	private final JFrame jFrame;

	/**
	 * Reference to the main GUI
	 */
	private final GUImain main;

	/**
	 * Event listener for the "new file" entry in the "file" drop-down menu
	 */
	public ActionListener newFileHandler = new ActionListener() {
		

		@Override
		public void actionPerformed(ActionEvent arg0) {
			newFile(null);
		}

	};
	
	/**
	 * Creating a new File, and opens a predefined text if the file is not null
	 * @param file
	 */
	public void newFile(File file){
		if (main.getSettings().getCMMFilePath() != null && main.isFileChanged()) {
			// Custom button text
			Object[] options = { _("Save now"), _("Proceed without saving") };

			// Init warning dialog with two buttons
			int n = JOptionPane.showOptionDialog(jFrame,
					_("The current file has not yet been saved!"),
					_("Opening new file"), JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null, // do not use a
														// custom Icon
					options, // the titles of buttons
					options[0]); // default button title

			if (n == JOptionPane.YES_OPTION)
				// Save the last changes to current file path
				main.getSaveManager().directSave();
		}

		if(file == null)
			main.getLeftPanel().getSourcePane().setText("#include <stdio.h>\n#include <stdlib.h>\n\nvoid main() {\n\t//Your code here...\n}");
		else{
			try {
				this.main.getLeftPanel().getSourcePane().setText(FileManagerCode.readSourceCode(file));
			} catch (IOException e) {
				new ErrorMessage().showErrorMessage(jFrame, "#2014", main.getSettings().getLanguage());
				main.getLeftPanel().getSourcePane().setText("");
			}

			main.getLeftPanel().getInputPane().setText("");
			main.getSettings().setCMMFilePath(null);
			main.updateWinFileName();
			main.getRightPanel().getDebugPanel().updateFileName();
			main.getRightPanel().getDebugPanel().setReadyMode();
		}
			
		main.getSettings().setCMMFilePath(null);
		main.updateWinFileName();
		main.getRightPanel().getDebugPanel().updateFileName();
		main.getRightPanel().getDebugPanel().setReadyMode();
	}

	/**
	 * Event listener for the "open" entry in the "file" drop-down menu
	 */
	public ActionListener openHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			main.getSaveManager().safeCheck(_("Opening new file"));

			// Create file chooser (opens a window to select a file)
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("CMM "
					+ _("file"), "cmm"));

			// User selected a file
			if (chooser.showOpenDialog(jFrame) == JFileChooser.APPROVE_OPTION) {

				openFile(chooser.getSelectedFile());
			}
		}

	};
	
	public void openFile( File file ){
		// Open file and load text t source code panel
		//this.leftPanelControl.initSourcePane(FileManagerCode.readSourceCode(file));
		try {
			this.main.getLeftPanel().getSourcePane().setText(FileManagerCode.readSourceCode(file));
		} catch (IOException e) {
			new ErrorMessage().showErrorMessage(jFrame, "#2012", main.getSettings().getLanguage());
		}

		// Set input data
		this.main.getLeftPanel().getInputPane().setText(FileManagerCode.readInputData(file));

		// Set the new C-- file directory
		main.getSettings().setCMMFilePath(file.getPath());

		main.updateWinFileName();
		main.getRightPanel().getDebugPanel().updateFileName();
		main.getRightPanel().getDebugPanel().setReadyMode();
	}
	
	public RecentFileHandler getRecentFileHandler( String path ){
		return new RecentFileHandler(path);
	}
	
	public class RecentFileHandler implements ActionListener{
		
		public RecentFileHandler( String path ){
			this.path = path;
		}
		
		private final String path;

		@Override
		public void actionPerformed(ActionEvent e) {
			File file = new File(path);
			
			main.getSaveManager().safeCheck(_("Opening new file"));
			
			if (!file.exists()){
				//Show error message with information about the error
				JOptionPane.showMessageDialog(new JFrame(),
						_("The following file could not be found: ") + "\n" + path,
						_("File does not exist"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			openFile(new File(path));
		}
	}

	/**
	 * Event listener for the "save as" entry in the "file" drop-down menu
	 */
	public ActionListener saveAsHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			// Call the main GUI's save dialog (contains file chooser and save
			// routines
			main.getSaveManager().doSaveAs();
			main.setFileSaved();
			main.updateWinFileName();
			main.getRightPanel().getDebugPanel().updateFileName();
		}

	};

	/**
	 * Event listener for the "save" entry in the "file" drop-down menu
	 */
	public ActionListener saveHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if (main.getSettings().getCMMFilePath() == null || main.getSettings().getCMMFilePath().equals(_("Unnamed")))
				// Open "save as" dialog if there is no working directory
				main.getSaveManager().doSaveAs();
			else
				// Save to working directory
				main.getSaveManager().directSave();

			main.setFileSaved();
			main.updateWinFileName();
			main.getRightPanel().getDebugPanel().updateFileName();
		}
	};
	
	/**
	 * Event listener for the "save" entry in the "file" drop-down menu
	 */
	public ActionListener printHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			try {
				main.getLeftPanel().getSourcePane().print();
			} catch (PrinterException e) {
				new ErrorMessage().showErrorMessage(jFrame, "#9001", main.getSettings().getLanguage());
			}
		}
	};
	
	public ActionListener creditsHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			new Credits().start();
		}
	};

	/**
	 * Event listener for the "exit" entry in the "file" drop-down menu - closes
	 * the program
	 */
	public ActionListener exitHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			if(main.getSaveManager().safeCheck(_("Closing C Compact")))
				WindowEventListener.updateAndExit(jFrame, main.getSettings());
		}
	};
	
	public ActionListener cutHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			main.getLeftPanel().getSourcePane().cut();
		}
	};
	
	public ActionListener copyHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			main.getLeftPanel().getSourcePane().copy();
		}
	};
	
	public ActionListener pasteHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			main.getLeftPanel().getSourcePane().paste();
		}
	};
	
	public ActionListener undoHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			main.getLeftPanel().getSourcePane().undoLastAction();
		}
	};
	
	public ActionListener redoHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			main.getLeftPanel().getSourcePane().redoLastAction();
		}
	};
	
	public ActionListener languageHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			GUILanguage l = new GUILanguage(main);
			l.start();
		}
	};
	
	public ActionListener propertiesHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			GUIProperties p = new GUIProperties(main);
			p.start();
		}
	};

	/**
	 * Event listener for the "select profile" option in the "progress"
	 * drop-down menu of menu bar
	 */
	public ActionListener profileHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			// ...and saved
			main.getSaveManager().directSave();
			main.getSettings().writeXMLsettings();
			main.dispose();
			
			//Starting Launcher
			new GUILauncherMain();
		}
	};
	
	/**
	 * Event listener for the "export profile" option in the "progress"
	 * drop-down menu of menu bar
	 */
	public ActionListener profileExportHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			ExportProfile.Export(main.getJFrame(), main.getSettings().getProfile());
		}
	};

	/**
	 * Event listener for the "select quest" option in the "progress" drop-down
	 * menu of the menu bar
	 */
	public ActionListener questHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			main.startQuestGUI();
		}
	};
	
	/**
	 * Event listener for the "import quest package" option in the "progress" drop-down
	 * menu of the menu bar
	 */
	public ActionListener questImportHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			ImportQuests.copyPackage();
		}
	};
	
	/**
	 * Event listener for the "edit profile" option in the "progress"
	 * drop-down menu of menu bar
	 */
	/*public ActionListener editProfileHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			main.dispose();
			GUIprofileSettings.init(main.getSettings(), false);
		}
	};*/

}
