package app.views;

import app.Main;
import app.helpers.*;
import app.uitoolkit.*;
import app.uitoolkit.keyboards.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * This class provides the view to display and
 * allow editing of user information.
 */
public class UserPage extends AbstractView {

    private final JLabel USER_ID          = new JLabel();        // User ID
    private final JTextField FIRST_NAME   = new JTextField();    // the user's first name field
    private final JTextField SURNAME      = new JTextField();    // the user's surname field
    private final InputField FNAME_FIELD;
    private final InputField SNAME_FIELD;

    public UserPage() {
        super("USER", "My Profile");
        AlphabeticKeyboard kb = new AlphabeticKeyboard(null, false, true);
        JPanel main = new JPanel(new BorderLayout());
            JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 0));
                JPanel form = new JPanel(new GridLayout(2, 2, 20, 0));
                    form.add(new InputField(USER_ID, "Employee ID", 40, 16, 300, false));
                    form.add(new JPanel()); // dummy
                    form.add(FNAME_FIELD = new InputField(FIRST_NAME, "First Name", 40, 16, 300, false));
                    form.add(SNAME_FIELD = new InputField(SURNAME, "Last Name", 40, 16, 300, false));
                inner.add(form);
                JPanel options = new JPanel();
                    options.add(new SquareButton("CHANGE_PIN", "KEY", "Change PIN", this));
                inner.add(options);
            main.add(UIToolbox.box(new JPanel(new GridBagLayout()), inner), BorderLayout.CENTER);
            main.add(kb, BorderLayout.SOUTH);
        add(main, BorderLayout.CENTER);
        JPanel nav = new JPanel(new BorderLayout());
            JPanel navLeft = new JPanel();
                navLeft.add(new HorizontalButton("HOME", "HOME", "Home", this));
            nav.add(navLeft, BorderLayout.WEST);
            JPanel navCenter = new JPanel(new GridLayout(1, 1));
                navCenter.add(new HorizontalButton("SUBMIT", null, "Submit", this));
                navCenter.setBorder(new EmptyBorder(5, 5, 5, 5));
            nav.add(navCenter, BorderLayout.CENTER);
            JPanel navRight = new JPanel();
                navRight.add(new HorizontalButton("EXIT", "EXIT", "Logout", this, true));
            nav.add(navRight, BorderLayout.EAST);
        add(nav, BorderLayout.SOUTH);
        UIToolbox.setSize(USER_ID, new Dimension(USER_ID.getWidth(), USER_ID.getHeight() - 20));
        // attach event listeners
        FIRST_NAME.addFocusListener(kb);
        SURNAME.addFocusListener(kb);
    }

    @Override
    public boolean prepareView(Object... args)  {
        if (!super.prepareView(args)) {return false;}
        USER_ID.setText("" + Main.USER.getID());
        FIRST_NAME.setText(Main.USER.getFirstName());
        SURNAME.setText(Main.USER.getSurName());
        FNAME_FIELD.showError(false);
        SNAME_FIELD.showError(false);
        FIRST_NAME.requestFocusInWindow();
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        String name = button.getName();
        if (name == "SUBMIT") {
            if (Main.USER.setName(FIRST_NAME.getText(), SURNAME.getText())) {
                MultiPanel.SELF.show("HOME");
            } else {
                FNAME_FIELD.showError(true);
                SNAME_FIELD.showError(true);
            }
        } else if (name == "CHANGE_PIN") {
            MultiPanel.SELF.show("CHANGE_PIN");
        } else {
            super.actionPerformed(e);
        }
    }
}
