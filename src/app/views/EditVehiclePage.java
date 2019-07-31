package app.views;

import app.helpers.*;
import app.model.*;
import app.uitoolkit.*;
import app.uitoolkit.keyboards.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class EditVehiclePage extends AbstractView {

    private final JLabel VEHICLE_INFO = new JLabel();           // Vehicle (make, model, model year) information
    private final JTextField LICENSE_PLATE= new JTextField();   // Vehicle's license plate
    private final SquareButton INSURANCE;                       // Link to the update vehicle's insurance policy
    private final InputField LP_FIELD;                          // Vehicle's license plate input field
    private Vehicle VEHICLE;
    private boolean fromNewPermit = false;

    public EditVehiclePage() {
        super("EDIT_VEHICLE", "Edit Vehicle");
        AlphaNumericKeyboard kb = new AlphaNumericKeyboard(null, false);
            kb.setSymbolsEnabled(false);
        JPanel main = new JPanel(new BorderLayout());
            JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 0));
                JPanel form = new JPanel(new GridLayout(2, 1));
                    form.add(new InputField(VEHICLE_INFO, "Vehicle", 40, 16, 300, false));
                    form.add(LP_FIELD = new InputField(LICENSE_PLATE, "License Plate", 40, 16, 500, false));
                inner.add(form);
                JPanel options = new JPanel();
                    options.add(INSURANCE = new SquareButton("INSURANCE", "SHIELD", "Update Insurance", this));
                    INSURANCE.setVisible(true);
                inner.add(options);
            main.add(UIToolbox.box(new JPanel(new GridBagLayout()), inner), BorderLayout.CENTER);
            main.add(kb, BorderLayout.SOUTH);
        add(main, BorderLayout.CENTER);
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
        LICENSE_PLATE.addFocusListener(kb);
    }

    @Override
    public boolean prepareView(Object... args) {
        if (!super.prepareView(args)) {return false;}
        if (args.length < 1) {
            VEHICLE_INFO.setText(null);
            LICENSE_PLATE.setText(null);
        } else {
            VEHICLE = (Vehicle)args[0];
            VEHICLE_INFO.setText(VEHICLE.getModelYear() + " " + VEHICLE.getMake() + " " + VEHICLE.getModel());
            LICENSE_PLATE.setText(VEHICLE.getPlate());
        }
        if (args.length >= 2) {
            fromNewPermit = (Boolean)args[1];
        } else {
            fromNewPermit = false;
        }
        LP_FIELD.showError(false);
        LICENSE_PLATE.requestFocusInWindow();
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        String name = button.getName();
        if (name == "SUBMIT") {
            if (VEHICLE.setPlate(LICENSE_PLATE.getText())) {
                if (fromNewPermit) {
                    MultiPanel.SELF.show("NEW_PERMIT", VEHICLE);
                } else {
                    MultiPanel.SELF.show("VEHICLES", VEHICLE);
                }
            } else {
                LP_FIELD.showError(true);
                LICENSE_PLATE.requestFocusInWindow();
            }
        } else if (name == "BACK") {
            if (fromNewPermit) {
                MultiPanel.SELF.show("NEW_PERMIT", VEHICLE);
            } else {
                MultiPanel.SELF.show("VEHICLES", VEHICLE);
            }
        } else if (name == "INSURANCE") {
            MultiPanel.SELF.show("INSURANCE", VEHICLE, fromNewPermit);
        } else {
            super.actionPerformed(e);
        }
    }
}
