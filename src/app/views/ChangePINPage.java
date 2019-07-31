package app.views;

import app.Main;
import app.helpers.*;
import app.uitoolkit.*;
import app.uitoolkit.keyboards.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * This class provides a view that allowing users to
 * change their access PIN.
 */
public class ChangePINPage extends AbstractView {

    private final JPasswordField  OLD_PIN      = new JPasswordField(4); // Field for old PIN
    private final JPasswordField  NEW_PIN      = new JPasswordField(4); // Field for new PIN
    private final JPasswordField  NEW_PIN2     = new JPasswordField(4); // Field for confirming new PIN

    private final InputField OLD_PIN_FIELD;     // Input Field for old PIN
    private final InputField NEW_PIN_FIELD;     // Input Field for new PIN
    private final InputField NEW_PIN2_FIELD;    // Input Field for confirming new PIN

    public ChangePINPage() {
        super("CHANGE_PIN", "Change PIN");
        NumericKeyboard kb = new NumericKeyboard(null);
        JPanel main = new JPanel();
            main.setLayout(new GridBagLayout());
            JPanel inner = new JPanel();
                inner.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 0));
                JPanel form = new JPanel(new GridLayout(3, 1));
                    form.add(OLD_PIN_FIELD = new InputField(OLD_PIN,  "Old Pin", 40, 16, 500, false));
                    form.add(NEW_PIN_FIELD = new InputField(NEW_PIN,  "New Pin", 40, 16, 500, false));
                    form.add(NEW_PIN2_FIELD = new InputField(NEW_PIN2, "Retype New Pin", 40, 16, 500, false));
                    CaretListener cl = new CaretListener() {
                        @Override
                        public void caretUpdate(CaretEvent e) {
                            JPasswordField pf = (JPasswordField)e.getSource();
                            String text = new String(pf.getPassword());
                            try {
                                if (text.length() > 4) {
                                    pf.setText(text.substring(0, 4));
                                }
                            } catch (Exception exe) {}
                        }
                    };
                    OLD_PIN.addCaretListener(cl);
                    NEW_PIN.addCaretListener(cl);
                    NEW_PIN2.addCaretListener(cl);
                inner.add(form);
                inner.add(kb);
        add(UIToolbox.box(main, inner), BorderLayout.CENTER);
        JPanel nav = new JPanel(new BorderLayout());
            JPanel navLeft = new JPanel();
                navLeft.add(new HorizontalButton("HOME", "HOME", "Home", this));
                navLeft.add(new HorizontalButton("BACK", "BACK", "Back", this));
            nav.add(navLeft, BorderLayout.WEST);
            JPanel navCenter = new JPanel(new GridLayout(1, 1));
                navCenter.add(new HorizontalButton("SUBMIT", null, "Submit", this));
                navCenter.setBorder(new EmptyBorder(5, 5, 5, 5));
            nav.add(navCenter, BorderLayout.CENTER);
            JPanel navRight = new JPanel();
                navRight.add(new HorizontalButton("EXIT", "EXIT", "Logout", this, true));
            nav.add(navRight, BorderLayout.EAST);
        add(nav, BorderLayout.SOUTH);
        // attach event listeners
        OLD_PIN.addFocusListener(kb);
        NEW_PIN.addFocusListener(kb);
        NEW_PIN2.addFocusListener(kb);
    }

    @Override
    public boolean prepareView(Object... args) {
        if (!super.prepareView(args)) {return false;}
        OLD_PIN.setText(""); OLD_PIN_FIELD.showError(false);
        NEW_PIN.setText(""); NEW_PIN_FIELD.showError(false);
        NEW_PIN2.setText(""); NEW_PIN2_FIELD.showError(false);
        OLD_PIN.requestFocusInWindow();
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        String name = button.getName();
        if (name.equals("SUBMIT")) {
            int oldPIN = 0, newPIN = 0, newPIN2 = 0;
            try {
                try {oldPIN = Integer.parseInt(new String(OLD_PIN.getPassword()));}
                    catch (Exception ex) {OLD_PIN_FIELD.showError(true); throw ex;}
                try {newPIN = Integer.parseInt(new String(NEW_PIN.getPassword()));}
                    catch (Exception ex) {NEW_PIN_FIELD.showError(true); throw ex;}
                try {newPIN2 = Integer.parseInt(new String(NEW_PIN2.getPassword()));}
                    catch (Exception ex) {NEW_PIN2_FIELD.showError(true); throw ex;}
                boolean ok = Main.USER.changePIN(oldPIN, newPIN, newPIN2);
                if (ok) {
                    MultiPanel.SELF.show("USER");
                } else {
                    OLD_PIN.setText(""); OLD_PIN_FIELD.showError(true);
                    NEW_PIN.setText(""); NEW_PIN_FIELD.showError(true);
                    NEW_PIN2.setText(""); NEW_PIN2_FIELD.showError(true);
                    OLD_PIN.requestFocusInWindow();
                }
            } catch (Exception ex) {}
        } else if (name.equals("BACK")) {
            MultiPanel.SELF.show("USER");
        } else {
            super.actionPerformed(e);
        }
    }
}
