package app.views;

import app.Main;
import app.helpers.*;
import app.uitoolkit.*;
import java.awt.*;
import javax.swing.*;

/**
 * This class provides a view that displays
 * the main menu (home screen) for navigation
 * to other views.
 */
public class HomePage extends AbstractView {

    private final SquareButton PAY_NOW; // Reference to Pay Now button

    public HomePage() {
        super("HOME", "Home");
        JPanel main = new JPanel();
            main.setLayout(new GridBagLayout());
            JPanel inner = new JPanel(new GridLayout(2, 3));
                inner.add(new SquareButton("USER", "USER", "My Profile", this));
                inner.add(new SquareButton("VEHICLES", "CAR", "My Vehicles", this));
                inner.add(new SquareButton("SUBSCRIPTION", "MAIL", "Subscription", this));
                inner.add(new SquareButton("NEW_PERMIT", "NEW", "Get Permit", this));
                inner.add(new SquareButton("HISTORY", "FILES", "History", this));
                inner.add(PAY_NOW = new SquareButton("PAY_NOW", "DOLLAR", "Pay Now", this));
        add(UIToolbox.box(main, inner), BorderLayout.CENTER);
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            nav.add(new HorizontalButton("EXIT", "EXIT", "Logout", this, true));
        add(nav, BorderLayout.SOUTH);
    }

    @Override
    public boolean prepareView(Object... args) {
        if (!super.prepareView(args)) {return false;}
        PAY_NOW.setVisible(Main.USER.hasFines());
        return true;
    }
}
