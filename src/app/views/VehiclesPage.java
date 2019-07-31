package app.views;

import app.Main;
import app.helpers.*;
import app.model.*;
import app.uitoolkit.*;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class VehiclesPage extends AbstractView {

    private final JLabel MAKE, MODEL, MODEL_YEAR, LICENSE_PLATE;
    private final JLabel INSURER, POLICY, EXPIRY_DATE;
    private final JList<Vehicle> CAR_LIST;
    private final JPanel MAIN;
    private final JPanel DETAILS, NO_VEHICLES;
    private final CardLayout DECK;

    public VehiclesPage() {
        super("VEHICLES", "My Vehicles");
        MAIN = new JPanel();
            DECK = new CardLayout();
            MAIN.setLayout(DECK);
            JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
                CAR_LIST = new JList<Vehicle>();
                    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    CAR_LIST.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                    CAR_LIST.setLayoutOrientation(JList.VERTICAL);
                    CAR_LIST.setVisibleRowCount(-1);
                    CAR_LIST.setBorder(new LineBorder(Color.LIGHT_GRAY));
                    CAR_LIST.setOpaque(false);
                    CAR_LIST.setCellRenderer(new ListRenderer<Vehicle>(300, 100) {
                        @Override
                        public String getHTML(Vehicle vehicle) {
                            return UIToolbox.getHTML("/assets/htdocs/vehicle.html")
                                .replace("{MAKE}",        vehicle.getMake())
                                .replace("{MODEL}",       vehicle.getModel())
                                .replace("{YEAR}",        "" + vehicle.getModelYear())
                                .replace("{PLATE}",       vehicle.getPlate())
                                .replace("{INSURER}",     vehicle.getInsurer())
                                .replace("{POLICY}",      vehicle.getPolicy())
                                .replace("{EXPIRY_DATE}", dateFormat.format(vehicle.getExpiry()));
                        }
                    });
                    CAR_LIST.addListSelectionListener(new ListSelectionListener() {
                        @Override
                        public void valueChanged(ListSelectionEvent e) {
                            if (CAR_LIST.isSelectionEmpty()) {return;}
                            Vehicle vehicle = CAR_LIST.getSelectedValue();
                            MAKE.setText(vehicle.getMake());
                            MODEL.setText(vehicle.getModel());
                            MODEL_YEAR.setText("" + vehicle.getModelYear());
                            LICENSE_PLATE.setText(vehicle.getPlate());
                            POLICY.setText(vehicle.getPolicy());
                            INSURER.setText(vehicle.getInsurer());
                            EXPIRY_DATE.setText(dateFormat.format(vehicle.getExpiry()));
                        }
                    });
                    JScrollPane carScroller = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    carScroller.setViewportView(CAR_LIST);
                    UIToolbox.setSize(carScroller, new Dimension(300, 535));
                inner.add(carScroller);
                JPanel detailsPane = new JPanel(new BorderLayout());
                    detailsPane.setBorder(new LineBorder(Color.LIGHT_GRAY));
                    final int PADDING = 10;
                    final int WIDTH = 375;
                    JPanel details = new JPanel(new GridLayout(4, 2, 10, 0));
                        details.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
                        details.add(new InputField(MAKE = new JLabel(), "Make", 40, 16, WIDTH, false));
                        details.add(new InputField(MODEL = new JLabel(), "Model", 40, 16, WIDTH, false));
                        details.add(new InputField(LICENSE_PLATE = new JLabel(), "License Plate", 40, 16, WIDTH, false));
                        details.add(new InputField(MODEL_YEAR = new JLabel(), "Model Year", 40, 16, WIDTH, false));
                        details.add(new InputField(INSURER = new JLabel(), "Insurer", 40, 16, WIDTH, false));
                        details.add(new InputField(POLICY = new JLabel(), "Policy", 40, 16, WIDTH, false));
                        details.add(new InputField(EXPIRY_DATE = new JLabel(), "Expiry Date", 40, 16, WIDTH, false));
                    detailsPane.add(details, BorderLayout.CENTER);
                    JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                        controls.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
                        controls.add(new HorizontalButton("DELETE", "TRASH", "Delete", this, true));
                        controls.add(new HorizontalButton("EDIT", "EDIT", "Edit", this, true));
                        controls.add(new HorizontalButton("CREATE", "CAR", "New", this, 150, true));
                    detailsPane.add(controls, BorderLayout.SOUTH);
                inner.add(detailsPane);
            MAIN.add(DETAILS = UIToolbox.box(new JPanel(new GridBagLayout()), inner));
            DECK.addLayoutComponent(DETAILS, "DETAILS");
            NO_VEHICLES = new JPanel(new GridLayout(1, 1));
                JButton noVehicleButton = new JButton(UIToolbox.getHTML("/assets/htdocs/iconLabel.vertical.html")
                    .replace("{ICON_SIZE}", "200")
                    .replace("{LABEL_SIZE}", "30")
                    .replace("{ICON}", MyFont.ICONS.get("CAR"))
                    .replace("{LABEL}", "You have no vehicles to view at this time.<br/>Tap to create one."));
                noVehicleButton.setName("NO_VEHICLES");
                noVehicleButton.addActionListener(this);
                noVehicleButton.setOpaque(false);
                noVehicleButton.setContentAreaFilled(false);
                NO_VEHICLES.add(noVehicleButton);
            MAIN.add(NO_VEHICLES);
            DECK.addLayoutComponent(NO_VEHICLES, "NO_VEHICLES");
        add(MAIN, BorderLayout.CENTER);
        JPanel nav = new JPanel(new BorderLayout());
            JPanel navLeft = new JPanel();
                navLeft.add(new HorizontalButton("HOME", "HOME", "Home", this));
            nav.add(navLeft, BorderLayout.WEST);
            JPanel navCenter = new JPanel(new GridLayout(1, 1));
            nav.add(navCenter, BorderLayout.CENTER);
            JPanel navRight = new JPanel();
                navRight.add(new HorizontalButton("EXIT", "EXIT", "Logout", this, true));
            nav.add(navRight, BorderLayout.EAST);
        add(nav, BorderLayout.SOUTH);
    }

    @Override
    public boolean prepareView(Object... args) {
        if (!super.prepareView(args)) {return false;}
        // clear
        CAR_LIST.removeAll();
        MAKE.setText(null);
        MODEL.setText(null);
        MODEL_YEAR.setText(null);
        LICENSE_PLATE.setText(null);
        POLICY.setText(null);
        EXPIRY_DATE.setText(null);
        INSURER.setText(null);
        // Get vehicles
        List<Vehicle> vehicles = DBManager.SELF.getVehiclesByUser(Main.USER);
        if (vehicles.isEmpty()) {
            DECK.show(MAIN, "NO_VEHICLES");
            return true;
        } else {
            DECK.show(MAIN, "DETAILS");
            CAR_LIST.clearSelection();
            CAR_LIST.setListData(vehicles.toArray(new Vehicle[vehicles.size()]));
            CAR_LIST.requestFocusInWindow();
            if (args.length >= 1) {
                Vehicle vehicle = (Vehicle)args[0];
                for (Vehicle v : vehicles) {
                    if (v.equals(vehicle)) {
                        CAR_LIST.setSelectedValue(v, true);
                    }
                }
            }
            if (CAR_LIST.isSelectionEmpty()){
                CAR_LIST.setSelectedIndex(0);
            }
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        String name = button.getName();
        if (name == "EDIT") {
            MultiPanel.SELF.show("EDIT_VEHICLE", CAR_LIST.getSelectedValue());
        } else if (name == "CREATE" || name == "NO_VEHICLES") {
                MultiPanel.SELF.show("CREATE_VEHICLE", new Vehicle(Main.USER), false);
        } else if (name == "DELETE") {
            DBManager.SELF.deleteVehicle(CAR_LIST.getSelectedValue());
            MultiPanel.SELF.show("VEHICLES");
        } else {
            super.actionPerformed(e);
        }
    }
}
