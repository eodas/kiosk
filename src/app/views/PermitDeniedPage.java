package app.views;

import app.helpers.*;
import app.uitoolkit.*;
import java.awt.*;
import javax.swing.*;

/**
 * This class provides a view that displays
 * the options when new permit attempted, but
 * user has outstanding fines.
 */
public class PermitDeniedPage extends AbstractView {
    public PermitDeniedPage() {
        super("PERMIT_DENIED", "Outstanding Fines");
        JPanel main = new JPanel();
            main.setLayout(new GridBagLayout());
            JPanel inner = new JPanel();
                inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
                JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    buttons.add(new SquareButton("HOME", "HOME", "Home", this));
                    buttons.add(new SquareButton("PAY_NOW", "DOLLAR", "Pay Now", this));
                    UIToolbox.setSize(inner, new Dimension(UIToolbox.getScreenSize().width, 300));
                inner.add(buttons);
                JLabel msg = new JLabel("You have outstanding fines. Choose one of the above options to continue.");
                    msg.setHorizontalAlignment(SwingConstants.CENTER);
                    msg.setFont(MyFont.REGULAR_FONT.deriveFont(20f));
                inner.add(msg);
        add(UIToolbox.box(main, inner), BorderLayout.CENTER);
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            nav.add(new HorizontalButton("EXIT", "EXIT", "Logout", this, true));
        add(nav, BorderLayout.SOUTH);
    }
}
