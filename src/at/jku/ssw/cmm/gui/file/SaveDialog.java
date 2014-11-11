package at.jku.ssw.cmm.gui.file;

import static at.jku.ssw.cmm.gettext.Language._;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import at.jku.ssw.cmm.gui.GUImainSettings;

/**
 * This is a manager class for opening a dialog window for choosing a file in order to save the source
 * code of the RSyntaxTextArea in the main GUI. The save dialog is called
 *  - when the user clicks "file -> save as" or presses a hot key
 *  - when the user tries to save and there is no working directory given in the configuration data
 *  - When the user closes the window and has never saved the current C-- file
 *  
 * @author fabian
 *
 */
public class SaveDialog {
	
	/**
	 * This constructor should be called when the main GUI is initialized.
	 * 
	 * @param jFrame The main GUI window frame
	 * @param jSourcePane The text area with the source code
	 * @param settings The main GUI configuration object
	 */
	public SaveDialog( JFrame jFrame, RSyntaxTextArea jSourcePane, JTextPane jInputPane, GUImainSettings settings ){
		this.jFrame = jFrame;
		this.jSourcePane = jSourcePane;
		this.jInputPane = jInputPane;
		this.settings = settings;
	}
	
	/**
	 * The main GUI window frame
	 */
	private final JFrame jFrame;
	
	/**
	 * The text area with the source code
	 */
	private final RSyntaxTextArea jSourcePane;
	
	/**
	 * The text panel with the input data
	 */
	private final JTextPane jInputPane;
	
	/**
	 * The main GUI configuration object
	 */
	private final GUImainSettings settings;
	
	/**
	 * Calls the "save as" dialog. The chosen file directory is automatically saved to the
	 * main GUI configuration object and the source code is automatically saved.
	 */
	public boolean doSaveAs(){
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter(
				"CMM " + _("file"), "cmm"));
		
		int option = chooser.showSaveDialog(jFrame);
		
		if (option == JFileChooser.APPROVE_OPTION) {
			FileManagerCode.saveSourceCode(chooser.getSelectedFile(), jSourcePane.getText(), jInputPane.getText());
			settings.setPath(chooser.getSelectedFile().getPath());
		}
		else if(option == JFileChooser.CANCEL_OPTION)
			return true;
		
		return false;
	}
	
	/**
	 * Saves the current file directly without calling a save dialog.
	 * Save dialog is called though, if there is no working directory for the file defined.
	 */
	public boolean directSave(){
		if( settings.getPath() != null ){
			FileManagerCode.saveSourceCode(new File(settings.getPath()), jSourcePane.getText(), jInputPane.getText());
			return false;
		}
		else
			return this.doSaveAs();
	}
}
