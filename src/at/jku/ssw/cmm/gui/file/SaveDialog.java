package at.jku.ssw.cmm.gui.file;

import static at.jku.ssw.cmm.gettext.Language._;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
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
	public SaveDialog( JFrame jFrame, RSyntaxTextArea jSourcePane, GUImainSettings settings ){
		this.jFrame = jFrame;
		this.jSourcePane = jSourcePane;
		this.settings = settings;
	}
	
	//The main GUI window frame
	private final JFrame jFrame;
	
	//The text area with the source code
	private final RSyntaxTextArea jSourcePane;
	
	//The main GUI configuration object
	private final GUImainSettings settings;
	
	/**
	 * Calls the "save as" dialog. The chosen file directory is automatically saved to the
	 * main GUI configuration object and the source code is automatically saved.
	 */
	public void doSaveAs(){
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter(
				"C-- " + _("file"), "cmm"));
		if (chooser.showSaveDialog(jFrame) == JFileChooser.APPROVE_OPTION) {
			FileManagerCode.saveSourceCode(chooser.getSelectedFile(), jSourcePane.getText());
			settings.setPath(chooser.getSelectedFile().getPath());
		}
	}
	
	public void directSave(){
		if( settings.getPath() != null )
			FileManagerCode.saveSourceCode(new File(settings.getPath()), jSourcePane.getText());
		else
			this.doSaveAs();
	}
}
