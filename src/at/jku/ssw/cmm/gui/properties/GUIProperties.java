package at.jku.ssw.cmm.gui.properties;

import static at.jku.ssw.cmm.gettext.Language._;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

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
	private JLabel JLabelVarOffset;
	
	private RSyntaxTextArea exampleCode;
	private JTextArea exampleText;
	
	public void start(){
		
		// Thread analysis
		if( SwingUtilities.isEventDispatchThread() )
			System.out.println("[EDT Analyse] Quest GUI runnung on EDT.");
		
		// Initialize window
		this.jFrame = new JFrame("C Compact - " + _("User Interface Settings"));
		this.jFrame.setLocationRelativeTo(null);
		this.jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.jFrame.add(this.initTextSizePanel());
		this.updateTextSize();
		
		// Causes this Window to be sized to fit the preferred size and layouts
		// of its subcomponents.
		this.jFrame.pack();
		this.jFrame.setVisible(true);
	}
	
	private JPanel initTextSizePanel(){
		
		//Initialize panel
		JPanel panel = new JPanel();
		
		// Panel properties
		panel.setBorder(new TitledBorder(_("Text Field Options")));
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0;   //request any extra vertical space
		c.weighty = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(5,5,5,5);
		
		panel.add(new JLabel(_("Source code font size")), setLayoutPosition(c, 0, 0, 1, 1));
		
		JSlider jSliderCode = new JSlider(JSlider.HORIZONTAL, 0, MAX_FONTSIZE_STEPS, 1);
		jSliderCode.setMajorTickSpacing(1);
		jSliderCode.setMinorTickSpacing(1);
		jSliderCode.setPaintTicks(true);
		jSliderCode.setValue(fontToSliderPos(this.main.getSettings().getCodeSize()));
		jSliderCode.addChangeListener(this.listener.sliderCodeListener);
		panel.add(jSliderCode, setLayoutPosition(c, 0, 1, 1, 1));
		
		this.jLabelCode = new JLabel("" + this.main.getSettings().getCodeSize() + " " + _("pixels"));
		panel.add(this.jLabelCode, setLayoutPosition(c, 0, 2, 1, 1));
		
		this.exampleCode = new RSyntaxTextArea(12, 3);
		
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping("text/ccompact", "at.jku.ssw.cmm.gui.init.CCompactTokenMaker");
		this.exampleCode.setSyntaxEditingStyle("text/ccompact");
		
		this.exampleCode.setCodeFoldingEnabled(true);
		this.exampleCode.setAntiAliasingEnabled(true);
		
		this.exampleCode.setText("void main(){\n    print('x');\n}");
		this.exampleCode.setEditable(false);
		
		this.exampleCode.setMinimumSize(new Dimension(200, 50));
		this.exampleCode.setPreferredSize(new Dimension(200, 50));
		this.exampleCode.setMaximumSize(new Dimension(200, 50));
		
		this.exampleCode.setBorder(BorderFactory.createLoweredBevelBorder());
		
		panel.add(this.exampleCode, setLayoutPosition(c, 1, 0, 1, 3));
		
		panel.add(new JLabel(_("Input and output text area font size")), setLayoutPosition(c, 0, 3, 1, 1));
		
		JSlider jSliderText = new JSlider(JSlider.HORIZONTAL, 0, MAX_FONTSIZE_STEPS, 1);
		jSliderText.setMajorTickSpacing(1);
		jSliderText.setMinorTickSpacing(1);
		jSliderText.setPaintTicks(true);
		jSliderText.setValue(fontToSliderPos(this.main.getSettings().getTextSize()));
		jSliderText.addChangeListener(this.listener.sliderTextListener);
		panel.add(jSliderText, setLayoutPosition(c, 0, 4, 1, 1));
		
		this.jLabelText = new JLabel("" + this.main.getSettings().getTextSize() + " " + _("pixels"));
		panel.add(this.jLabelText, setLayoutPosition(c, 0, 5, 1, 1));
		
		this.exampleText = new JTextArea(14, 3);
		this.exampleText.setText("Hello World!\1 2 3 4 5");
		this.exampleText.setEditable(false);
		this.exampleText.setMinimumSize(new Dimension(200, 50));
		this.exampleText.setPreferredSize(new Dimension(200, 50));
		this.exampleText.setMaximumSize(new Dimension(200, 50));
		
		this.exampleText.setBorder(BorderFactory.createLoweredBevelBorder());
		
		panel.add(this.exampleText, setLayoutPosition(c, 1, 3, 1, 3));
		
		// Return ready panel
		return panel;
	}
	
	public void updateTextSize(){
		this.exampleCode.setFont(this.exampleCode.getFont().deriveFont((float)this.main.getSettings().getCodeSize()));
		this.exampleCode.repaint();
		
		this.exampleText.setFont(this.exampleText.getFont().deriveFont((float)this.main.getSettings().getTextSize()));
		this.exampleText.repaint();
		
		this.jLabelCode.setText("" + this.main.getSettings().getCodeSize() + " " + _("pixels"));
		this.jLabelText.setText("" + this.main.getSettings().getTextSize() + " " + _("pixels"));
	}
	
	/**
	 * Writes all the below given data to the layout constrains. Used to define the position
	 * and size of a component in the GridBag Layout.
	 * 
	 * @param c Layout Constrains.
	 * @param x Position of the grid (x)
	 * @param y Position of the grid (y)
	 * @param width How many columns the component is wide
	 * @param height How many lines the component is high
	 * @return The modified Layout Constrains. Actually not necessary as the parameter c is
	 * called by reference, however you can use the return value directly inside another function call.
	 */
	private GridBagConstraints setLayoutPosition( GridBagConstraints c, int x, int y, int width, int height ){
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.gridheight = height;
		
		return c;
	}
	
	public static int sliderPosToFont( int slider ){
		return 2*slider + 6;
	}
	
	public static int fontToSliderPos( int fontSize ){
		return (fontSize-6)/2;
	}
}
