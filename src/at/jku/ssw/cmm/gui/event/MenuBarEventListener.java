package at.jku.ssw.cmm.gui.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import at.jku.ssw.cmm.gui.GUImainSettings;
import at.jku.ssw.cmm.gui.file.FileManagerCode;
import at.jku.ssw.cmm.gui.file.SaveDialog;
import at.jku.ssw.cmm.gui.mod.GUIrPanelMod;

/**
 * Contains event listeners for the main in the main GUI.
 * 
 * @author fabian
 *
 */
public class MenuBarEventListener {
	
	public MenuBarEventListener( JFrame jFrame, RSyntaxTextArea jSourcePane, GUImainSettings settings, GUIrPanelMod modifier, SaveDialog saveDialog ){
		this.jFrame = jFrame;
		this.jSourcePane = jSourcePane;
		this.settings = settings;
		this.modifier = modifier;
		this.saveDialog = saveDialog;
	}
	
	//The main window frame
	private final JFrame jFrame;
	
	//The text area for the source code
	private final RSyntaxTextArea jSourcePane;
	
	//A reference to the configuration object of the main GUI.
	private final GUImainSettings settings;
	
	//A reference to the save dialog manager class.-
	private final SaveDialog saveDialog;
	
	private final GUIrPanelMod modifier;
	
	/**
	 * Event listener for the "new file" entry in the "file" drop-down menu
	 */
	public ActionListener newFileHandler = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			if( modifier.getPanelMode() == 0 ){
				jSourcePane.setText("");
				settings.setPath(null);
			}
		}
	};
	
	/**
	 * Event listener for the "open" entry in the "file" drop-down menu
	 */
	public ActionListener openHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			if( modifier.getPanelMode() == 0 ){
				//Create file chooser (opens a window to select a file)
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("C-- file", "cmm"));
				
				//User selected a file
				if (chooser.showOpenDialog(jFrame) == JFileChooser.APPROVE_OPTION) {
					//Open file and load text t source code panel
					jSourcePane.setText(FileManagerCode.readSourceCode(chooser.getSelectedFile()));
					//Set the new C-- file directory
					settings.setPath(chooser.getSelectedFile().getPath());
				}
			}
		}
	};
	
	/**
	 * Event listener for the "save as" entry in the "file" drop-down menu
	 */
	public ActionListener saveAsHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			if( modifier.getPanelMode() == 0 ){
				//Call the main GUI's save dialog (contains file chooser and save routines
				saveDialog.doSaveAs();
			}
		}
	};
	
	/**
	 * Event listener for the "save" entry in the "file" drop-down menu
	 */
	public ActionListener saveHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			if( modifier.getPanelMode() == 0 ){
				if( settings.getPath() != null )
					//Save to working directory
					FileManagerCode.saveSourceCode(new File(settings.getPath()), jSourcePane.getText());
				else
					//Open "save as" dialog if there is no working directory
					saveDialog.doSaveAs();
			}
		}
	};
	
	/**
	 * Event listener for the "exit" entry in the "file" drop-down menu - closes the program
	 */
	public ActionListener exitHandler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			WindowEventListener.doSaveCloseProgram(jFrame, settings, saveDialog);
		}
	};
}
