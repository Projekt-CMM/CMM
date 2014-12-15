package at.jku.ssw.cmm.gui.properties;

import static at.jku.ssw.cmm.gettext.Language._;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import at.jku.ssw.cmm.gui.GUImain;

public class GUIProperties{
	
	private static final int MAX_FONTSIZE_STEPS = 13;

	public GUIProperties(GUImain main){
		this.main = main;
		
		this.listener = new PropertiesSliderListener(main, this);
	}
	
	private final GUImain main;
	private final PropertiesSliderListener listener;
	
	private JFrame jFrame;
	
	private JLabel jLabelCode;
	private JLabel jLabelText;
	private JLabel jLabelVar;
	
	public void start(){
		
		// Thread analysis
		if( SwingUtilities.isEventDispatchThread() )
			System.out.println("[EDT Analyse] Properties GUI runnung on EDT.");
		
		// Initialize window
		this.jFrame = new JFrame(_("Properties"));
		this.jFrame.setLocationRelativeTo(null);
		this.jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel mainPanel = new JPanel();
		this.jFrame.add(mainPanel);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		mainPanel.add(this.initCodeSizePanel());
		mainPanel.add(this.initTextSizePanel());
		mainPanel.add(this.initVarSizePanel());
		this.updateTextSize();
		
		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.setResizable(false);
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}
	
	private JPanel initCodeSizePanel(){
		
		//Initialize panel
		JPanel panel = new JPanel();
		
		// Panel properties
		panel.setBorder(new TitledBorder(_("Source code font size")));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		JSlider jSliderCode = new JSlider(JSlider.HORIZONTAL, 0, MAX_FONTSIZE_STEPS, 1);
		jSliderCode.setMajorTickSpacing(1);
		jSliderCode.setMinorTickSpacing(1);
		jSliderCode.setPaintTicks(true);
		jSliderCode.setValue(fontToSliderPos(this.main.getSettings().getCodeSize()));
		jSliderCode.addChangeListener(this.listener.sliderCodeListener);
		panel.add(jSliderCode);
		
		this.jLabelCode = new JLabel("" + this.main.getSettings().getCodeSize() + " " + _("pixels"));
		panel.add(this.jLabelCode);
		
		// Return ready panel
		return panel;
	}
	
	private JPanel initTextSizePanel(){
		
		//Initialize panel
		JPanel panel = new JPanel();
		
		// Panel properties
		panel.setBorder(new TitledBorder(_("Text field font size")));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		JSlider jSliderText = new JSlider(JSlider.HORIZONTAL, 0, MAX_FONTSIZE_STEPS, 1);
		jSliderText.setMajorTickSpacing(1);
		jSliderText.setMinorTickSpacing(1);
		jSliderText.setPaintTicks(true);
		jSliderText.setValue(fontToSliderPos(this.main.getSettings().getTextSize()));
		jSliderText.addChangeListener(this.listener.sliderTextListener);
		panel.add(jSliderText);
		
		this.jLabelText = new JLabel("" + this.main.getSettings().getTextSize() + " " + _("pixels"));
		panel.add(this.jLabelText);
		
		// Return ready panel
		return panel;
	}
	
	private JPanel initVarSizePanel(){
		
		//Initialize panel
		JPanel panel = new JPanel();
				
		// Panel properties
		panel.setBorder(new TitledBorder(_("Variable table font size")));
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
				
		JSlider jSliderVar = new JSlider(JSlider.HORIZONTAL, 0, MAX_FONTSIZE_STEPS, 1);
		jSliderVar.setMajorTickSpacing(1);
		jSliderVar.setMinorTickSpacing(1);
		jSliderVar.setPaintTicks(true);
		jSliderVar.setValue(fontToSliderPos(this.main.getSettings().getVarSize()));
		jSliderVar.addChangeListener(this.listener.sliderVarListener);
		panel.add(jSliderVar);
				
		this.jLabelVar = new JLabel("" + this.main.getSettings().getTextSize() + " " + _("pixels"));
		panel.add(this.jLabelVar);
				
		// Return ready panel
		return panel;
	}
	
	public void updateTextSize(){
		
		this.main.getLeftPanel().updateFontSize();
		this.main.getRightPanel().getDebugPanel().updateFontSize();
		
		this.jLabelCode.setText("" + this.main.getSettings().getCodeSize() + " " + _("pixels"));
		this.jLabelText.setText("" + this.main.getSettings().getTextSize() + " " + _("pixels"));
		this.jLabelVar.setText("" + this.main.getSettings().getVarSize() + " " + _("pixels"));
	}
	
	public static int sliderPosToFont( int slider ){
		return 2*slider + 6;
	}
	
	public static int fontToSliderPos( int fontSize ){
		return (fontSize-6)/2;
	}
}
