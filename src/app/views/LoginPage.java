package app.views;

import app.Main;
import app.helpers.*;
import app.model.*;
import app.uitoolkit.*;
import app.uitoolkit.keyboards.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * This class provides a view that allows users
 * to log into the application.
 */
public class LoginPage extends AbstractView {

    private final JTextField USER;          // User employee ID field
    private final JPasswordField PIN;       // Access PIN field
    private final InputField USER_FIELD;    // input field for user employee ID
    private final InputField PIN_FIELD;     // input filed for user PIN

    private Timeout timeout = null;

    public LoginPage() {
        super("LOGIN", "Login");
        NumericKeyboard kb = new NumericKeyboard(null);
        JPanel main = new JPanel();
            main.setLayout(new GridBagLayout());
            JPanel inner = new JPanel();
                inner.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 0));
                JPanel form = new JPanel(new GridLayout(2, 1));
                USER = new JTextField();
                    USER.addCaretListener(new CaretListener() {
                        @Override
                        public void caretUpdate(CaretEvent e) {
                            String text = USER.getText();
                            try {
                                if (text.length() > 4) {
                                    USER.setText(text.substring(0, 4));
                                }
                            } catch (Exception exe) {}
                        }
                    });
                PIN = new JPasswordField();
                    PIN.addCaretListener(new CaretListener() {
                        @Override
                        public void caretUpdate(CaretEvent e) {
                            String text = new String(PIN.getPassword());
                            try {
                                if (text.length() > 4) {
                                    PIN.setText(text.substring(0, 4));
                                }
                            } catch (Exception exe) {}
                        }
                    });
                    form.add(USER_FIELD = new InputField(USER, "Employee ID", 60, 16, 500, false));
                    form.add(PIN_FIELD = new InputField(PIN, "Pin", 60, 16, 500, false));
                inner.add(form);
                inner.add(kb);
        add(UIToolbox.box(main, inner), BorderLayout.CENTER);
        JPanel nav = new JPanel();
            nav.add(new HorizontalButton("ENTER", "CHECK", "Enter", this, UIToolbox.getScreenSize().width - 10));
        add(nav, BorderLayout.SOUTH);
        // attach event listeners
        USER.addFocusListener(kb);
        PIN.addFocusListener(kb);
        this.addMouseMotionListener(new MouseAdapter() {
            void reset() {
                if (timeout != null) {
                    timeout.reset();
                }
            }
            @Override public void mousePressed(MouseEvent e) {reset();}
            @Override public void mouseClicked(MouseEvent e) {reset();}
            @Override public void mouseWheelMoved(MouseWheelEvent e) {reset();}
            @Override public void mouseMoved(MouseEvent e) {reset();}
            @Override public void mouseDragged(MouseEvent e) {reset();}
        });
    }

    @Override
    public boolean prepareView(Object... args) {
        if (timeout != null) {
            timeout.stop();
        }
        USER.setText(""); USER_FIELD.showError(false);
        PIN.setText(""); PIN_FIELD.showError(false);
        USER.requestFocusInWindow();
        timeout = new Timeout(new Runnable() {
            @Override public void run() {
                MultiPanel.SELF.show("WELCOME");
            }
        }, 60 * 1000).start();
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (timeout != null) {
            timeout.reset();
        }
        JButton button = (JButton)e.getSource();
        String name = button.getName();
        long id = 0;
        int pin = 0;
        if (name == "ENTER") {
            try {
                String username = USER.getText();
                if (!username.isEmpty() && username.length() == 4) {
                    try {id = Integer.parseInt(USER.getText());}
                    catch (Exception ex) {USER_FIELD.showError(true); throw ex;}
                }
                String password = new String(PIN.getPassword());
                if (!password.isEmpty() && password.length() == 4) {
                    try {pin = Integer.parseInt(new String(PIN.getPassword()));}
                    catch (Exception ex) {USER_FIELD.showError(true); throw ex;}
                }
                if ((Main.USER = DBManager.SELF.getUser(id, pin)) != null) {
                    System.out.println("LOGGED IN AS: " + Main.USER.getID());
                    if (timeout != null) {
                        timeout.stop();
                    }
                    MultiPanel.SELF.show("HOME");
                } else {
                    System.out.println("LOGIN ATTEMPT FAILED");
                    USER_FIELD.showError(true);
                    PIN_FIELD.showError(true);
                    PIN.setText("");
                    if (!DBManager.SELF.userExists(id)) {
                        USER.setText("");
                        USER.requestFocusInWindow();
                    } else {
                        PIN.requestFocusInWindow();
                    }
                }
            } catch (Exception ex) {}
        }
    }
}
