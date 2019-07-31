package app.views;

import java.awt.event.*;

import javax.swing.*;
import app.helpers.*;

public class WelcomePage extends AbstractView {

	public WelcomePage() {
		super("WELCOME", null);
		JButton start = new JButton();
		String image = this.getClass().getResource("/assets/images/logo.png").toString();
		start.setIcon(new ImageIcon(WelcomePage.class.getResource("/assets/images/logo.png")));
		start.setText(UIToolbox.getHTML("/assets/htdocs/welcome.html")
				.replace("{IMAGE}", image));
		start.setOpaque(false);
		start.setBorderPainted(false);
		start.setContentAreaFilled(false);
		start.addActionListener(this);
		add(start);
	}

	@Override
	public boolean prepareView(Object... args) {
		return false; // Do nothing
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MultiPanel.SELF.show("LOGIN");
	}
}
