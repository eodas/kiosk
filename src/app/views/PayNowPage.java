package app.views;

import app.Main;
import app.helpers.*;
import app.model.Permit;
import app.uitoolkit.*;
import app.uitoolkit.keyboards.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * This class provides a view that allowing users to
 * pay their outstanding fines.
 */
public class PayNowPage extends AbstractView {

    private final JTextField AMOUNT = new JTextField();
    private final InputField AMOUNT_FIELD;
    private final JLabel CHANGE = new JLabel();
    private final JLabel OUTSTANDING = new JLabel();
    private Permit PERMIT = null;
    private double $AMOUNT = 0, $CHANGE, $OUTSTANDING;

    public PayNowPage() {
        super("PAY_NOW", "Pay Now");
        AMOUNT.setColumns(5);
        NumericKeyboard kb = new NumericKeyboard(null, true, true) {
            @Override public void actionPerformed(ActionEvent event) {
                AbstractButton ab = (AbstractButton)event.getSource();
                String name = ab.getName();
                String add = ab.getText();
                String text = field.getText();
                if (("$0.00".equals(text) || text.matches("\\$0+"))  
                        && !name.equals("CE") 
                        && !name.equals("BKSP") 
                        && field.getSelectedText() == null) {
                    if (".".equals(add)) {add = "0.";}
                    field.setText("$" + add);
                    field.setCaretPosition(1 + add.length());
                } else if ("$0.".equals(text) && ".".equals(add)) {
                    // do nothing
                } else if (("$".equals(text) || text.isEmpty()) && ".".equals(add)) {
                    field.setText("$0.");
                    field.setCaretPosition(3);
                } else {
                    super.actionPerformed(event);
                }
            }
        };
        JPanel main = new JPanel();
            main.setLayout(new GridBagLayout());
            JPanel inner = new JPanel();
                inner.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 0));
                JPanel form = new JPanel(new GridLayout(3, 1));
                    form.add(AMOUNT_FIELD = new InputField(AMOUNT,  "Amount", 40, 16, 500, false));
                    form.add(new InputField(CHANGE,  "Change", 40, 16, 500, false));
                    form.add(new InputField(OUTSTANDING, "Outstanding", 40, 16, 500, false));
                    AMOUNT.addCaretListener(new CaretListener() {
                        @Override
                        public void caretUpdate(CaretEvent e) {
                            String amount = AMOUNT.getText();
                            try {
                                if (amount.isEmpty() || amount.charAt(0) != '$') {
                                    AMOUNT.setText("$" + amount);
                                } else {
                                    double amt = Double.parseDouble(AMOUNT.getText().substring(1));
                                    if (amt >= 0 && amt < 1000) {
                                        $AMOUNT = amt;
                                        AMOUNT_FIELD.showError(false);
                                    } else {
                                        AMOUNT_FIELD.showError(true);
                                    }
                                }
                            } catch (Exception exe) {
                                AMOUNT_FIELD.showError(true);
                            }
                            if ($OUTSTANDING >= $AMOUNT) {
                                $CHANGE = 0;
                                CHANGE.setText(String.format("$%.2f", $CHANGE));
                                OUTSTANDING.setText(String.format("$%.2f", $OUTSTANDING - $AMOUNT));
                            } else {
                                $CHANGE = $AMOUNT - $OUTSTANDING;
                                CHANGE.setText(String.format("$%.2f", $CHANGE));
                                OUTSTANDING.setText(String.format("$%.2f", 0f));
                            }
                        }
                    });
                inner.add(form);
                inner.add(kb);
        add(UIToolbox.box(main, inner), BorderLayout.CENTER);
        JPanel nav = new JPanel(new BorderLayout());
            JPanel navLeft = new JPanel();
                navLeft.add(new HorizontalButton("HOME", "HOME", "Home", this));
            nav.add(navLeft, BorderLayout.WEST);
            JPanel navCenter = new JPanel(new GridLayout(1, 2));
                navCenter.add(new HorizontalButton("DEFER", null, "Defer", this));
                navCenter.add(new HorizontalButton("SUBMIT", null, "Submit", this));
                navCenter.setBorder(new EmptyBorder(5, 5, 5, 5));
            nav.add(navCenter, BorderLayout.CENTER);
            JPanel navRight = new JPanel();
                navRight.add(new HorizontalButton("EXIT", "EXIT", "Logout", this, true));
            nav.add(navRight, BorderLayout.EAST);
        add(nav, BorderLayout.SOUTH);
        // attach event listeners
        AMOUNT.addFocusListener(kb);
    }

    @Override
    public boolean prepareView(Object... args) {
        if (!super.prepareView(args)) {return false;}
        if (args.length >= 1) {
            PERMIT = (Permit)args[0];
        } else {
            PERMIT = null;
        }
        AMOUNT_FIELD.showError(false);
        AMOUNT.setText(String.format("$%.2f", 0f));
        CHANGE.setText(String.format("$%.2f", 0f));
        $OUTSTANDING = Main.USER.getFines();
        OUTSTANDING.setText(String.format("$%.2f", $OUTSTANDING));
        AMOUNT.requestFocusInWindow();
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        String name = button.getName();
        if (name == "DEFER" || name == "SUBMIT") {
            if (name == "SUBMIT") {
                Main.USER.payFines($AMOUNT - $CHANGE);
            }
            if (PERMIT != null) {
                MultiPanel.SELF.show("RECEIPT", PERMIT);
            } else {
                MultiPanel.SELF.show("HOME");
            }
        } else {
            super.actionPerformed(e);
        }
    }
}
