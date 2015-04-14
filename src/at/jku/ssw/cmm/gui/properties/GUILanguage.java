package at.jku.ssw.cmm.gui.properties;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.gettext.Language;
import at.jku.ssw.cmm.gui.GUIExecutable;
import at.jku.ssw.cmm.gui.file.LoadStatics;

public class GUILanguage implements MouseListener {

	public GUILanguage(GUIExecutable main) {
		this.main = main;
	}

	private final GUIExecutable main;
	
	private JFrame jFrame;
	
	private JComboBox<String> jLanguageChooser;
	private JButton jButtonOK;

	public void start() {
		// Thread analysis
		if (SwingUtilities.isEventDispatchThread())
			System.out.println("[EDT Analyse] Language chooser GUI runnung on EDT.");

		// Initialize window
		this.jFrame = new JFrame(this.main.getSettings().getLanguage() == null ? _("Welcome") : _("Language"));
		this.jFrame.setLocationRelativeTo(null);
		this.jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel();
		this.jFrame.add(mainPanel);
		mainPanel.setLayout(new BorderLayout());

		this.init(mainPanel);

		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.setResizable(false);
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}
	
	private void init(JPanel mainPanel) {
		
		JPanel logo = new JPanel();
		logo.add(LoadStatics.loadImage("images/logo.png", false, 120, 120));
		mainPanel.add(logo, BorderLayout.PAGE_START);
		
		JPanel center = new JPanel();
		center.setBorder(new TitledBorder(_("Choose a language")));
		
		String[] languages = {"English", "Deutsch"};
		jLanguageChooser = new JComboBox<>(languages);
		jLanguageChooser.setSelectedIndex(0);
		jLanguageChooser.setMinimumSize(new Dimension(100,30));
		jLanguageChooser.setPreferredSize(new Dimension(100,30));
		center.add(jLanguageChooser);
		
		this.jButtonOK = new JButton(_("OK"));
		center.add(this.jButtonOK);
		this.jButtonOK.addMouseListener(this);
		
		mainPanel.add(center, BorderLayout.CENTER);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		boolean init = (main.getSettings().getLanguage()==null);
		
		switch(this.jLanguageChooser.getSelectedIndex()) {
		case 0: this.main.getSettings().setLanguage("en"); break;
		case 1: this.main.getSettings().setLanguage("de"); break;
		default: this.main.getSettings().setLanguage(Language.DEFAULT_LANGUAGE); break;
		}
		
		this.jFrame.dispose();

		// Load translations
		Language.loadLanguage(this.main.getSettings().getLanguage() + ".po");
		
		if( init )
			this.main.start(false);
		
		else {
			main.saveAndDispose();
			main.start(false);
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
