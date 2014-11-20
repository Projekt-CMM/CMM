package at.jku.ssw.cmm.profile.settings;

import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class TokenListRenderer implements ListCellRenderer<JPanel> {
	
	public TokenListRenderer( DefaultListModel<JPanel> model ){
		this.model = model;
	}

	private final DefaultListModel<JPanel> model;

	@Override
	public Component getListCellRendererComponent(JList<? extends JPanel> arg0,
			JPanel arg1, int arg2, boolean arg3, boolean arg4) {

		return (JPanel) model.get(arg2);
	}

}
