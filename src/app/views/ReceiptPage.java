package app.views;

import app.Main;
import app.helpers.*;
import app.model.*;
import app.uitoolkit.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public class ReceiptPage extends AbstractView {

    private final JLabel RECEIPT = new JLabel();

    public ReceiptPage() {
        super("RECEIPT", "Receipt");
        JPanel main = new JPanel(new BorderLayout());
            JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
                JPanel detailsPane = new JPanel(new BorderLayout());
                    detailsPane.setBorder(new LineBorder(Color.LIGHT_GRAY));
                    JPanel details = new JPanel(new GridLayout(1, 1));
                        RECEIPT.setBorder(new LineBorder(Color.LIGHT_GRAY));
                        UIToolbox.setSize(RECEIPT, new Dimension(500, 535));
                        details.setBackground(Color.WHITE);
                        details.add(RECEIPT);
                    detailsPane.add(details, BorderLayout.CENTER);
                inner.add(detailsPane);
            main.add(UIToolbox.box(new JPanel(new GridBagLayout()), inner), BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
        JPanel nav = new JPanel(new BorderLayout());
            JPanel navLeft = new JPanel();
                navLeft.add(new HorizontalButton("HOME", "HOME", "Home", this));
            nav.add(navLeft, BorderLayout.WEST);
            JPanel navCenter = new JPanel(new GridLayout(1, 1));
            navCenter.add(new HorizontalButton("OK", null, "OK", this));
            navCenter.setBorder(new EmptyBorder(5,5,5,5));
            nav.add(navCenter, BorderLayout.CENTER);
            JPanel navRight = new JPanel();
                navRight.add(new HorizontalButton("EXIT", "EXIT", "Logout", this, true));
            nav.add(navRight, BorderLayout.EAST);
        add(nav, BorderLayout.SOUTH);
    }

    @Override
    public boolean prepareView(Object... args) {
        if (!super.prepareView(args)) {return false;}
        RECEIPT.setText(null);
        if (args.length >= 1) {
            Permit permit = (Permit)args[0];
            RECEIPT.setText(UIToolbox.getHTML("/assets/htdocs/permit.receipt.html")
                    .replace("{USER_ID}", "" + Main.USER.getID())
                    .replace("{FIRST_NAME}", Main.USER.getFirstName())
                    .replace("{SURNAME}", Main.USER.getSurName())
                    .replace("{LICENSE}", permit.getVehicle().getPlate())
                    .replace("{MAKE}", permit.getVehicle().getMake())
                    .replace("{MODEL}", permit.getVehicle().getModel())
                    .replace("{YEAR}", "" + permit.getVehicle().getModelYear())
                    .replace("{ISSUED_DATE}", permit.getIssueDate().toString())
                    .replace("{EXPIRY}", permit.getEndDate().toString())
                );
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        String name = button.getName();
        if (name == "OK") {MultiPanel.SELF.show("HOME");}
        else {super.actionPerformed(e);}
    }
}
