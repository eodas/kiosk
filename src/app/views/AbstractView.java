package app.views;

import app.Main;
import app.helpers.*;
import app.uitoolkit.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Abstract implementation of a view (page) in the application.
 * Facilitates action listeners for button actions.
 */
public abstract class AbstractView extends JPanel implements ActionListener {

    protected final TitlePane TITLE = new TitlePane(); // Reference to title panel

    /**
     * Constructs a new view.
     * Sets the title and add itself to the multi-panel controller.
     *
     * @param tp        reference to the shared title pane
     * @param name      the lookup name associated with the view
     * @param text      the title text
     */
    public AbstractView(String name, String text) {
        setLayout(new BorderLayout());
        setName(name);
        if (text != null) {
            TITLE.setText(text);
            add(TITLE, BorderLayout.NORTH);
        }
    }

    /**
     * Prepare the view for displaying.
     * Invoked by MultiPanel.show before view is displayed.
     *
     * @param args      arguments needed to prepare the view
     * @return          true if logged in, otherwise false.
     */
    public boolean prepareView(Object... args) {
        if (Main.USER == null) {
            MultiPanel.SELF.show("WELCOME");
            //MultiPanel.SELF.show("LOGIN");
            return false;
        }
        TITLE.setUserTag(Main.USER);
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        String name = button.getName();
        if (name == "EXIT") {
            if (Main.USER.logout()) {
                System.out.println("LOGOUT: " + Main.USER.getID());
                Main.USER = null;
                MultiPanel.SELF.show("WELCOME");
                //MultiPanel.SELF.show("LOGIN");
            }
        } else {
            MultiPanel.SELF.show(name);
        }
    }
}
