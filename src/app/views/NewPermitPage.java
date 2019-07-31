package app.views;

import app.Main;
import app.helpers.*;
import app.model.*;
import app.uitoolkit.*;

import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class NewPermitPage extends AbstractView {

    private final JSpinner DAYS;
    private final JLabel START_DATE, EXPIRY_DATE;
    private final JList<Vehicle> CAR_LIST;
    private final HorizontalButton SUBMIT;
    private final InputField EXPIRY_DATE_FIELD;
    private Permit PERMIT = null;

    public NewPermitPage() {
        super("NEW_PERMIT", "Get Permit");
        JPanel main = new JPanel(new BorderLayout());
            JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
                CAR_LIST = new JList<Vehicle>();
                    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    CAR_LIST.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                    CAR_LIST.setLayoutOrientation(JList.VERTICAL);
                    CAR_LIST.setBorder(new LineBorder(Color.LIGHT_GRAY));
                    CAR_LIST.setOpaque(false);
                    CAR_LIST.setCellRenderer(new ListRenderer<Vehicle>(300, 100) {
                        @Override
                        public String getHTML(Vehicle vehicle) {
                            return (vehicle == null) ?
                                UIToolbox.getHTML("/assets/htdocs/vehicle.new.html") :
                                UIToolbox.getHTML("/assets/htdocs/vehicle.html")
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
                        private boolean doAction = true;
                        @Override
                        public void valueChanged(ListSelectionEvent e) {
                            if (CAR_LIST.isSelectionEmpty()) {return;}
                            if (doAction) {
                                Vehicle vehicle = CAR_LIST.getSelectedValue();
                                if (vehicle == null) {
                                    MultiPanel.SELF.show("CREATE_VEHICLE", new Vehicle(Main.USER));
                                } else if (PERMIT != null) {
                                    int days = Integer.parseInt((String)DAYS.getValue());
                                    PERMIT.setVehicle(vehicle);
                                    PERMIT.setDaysLeft(days);
                                    START_DATE.setText(PERMIT.getStartDate().toString());
                                    EXPIRY_DATE.setText(PERMIT.getEndDate().toString());
                                }
                            }
                            doAction = !doAction;
                        }
                    });
                    CAR_LIST.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e){
                            if (e.getClickCount() == 2) {
                                int index = CAR_LIST.locationToIndex(e.getPoint());
                                ListModel<Vehicle> dlm = CAR_LIST.getModel();
                                Vehicle vehicle = dlm.getElementAt(index);
                                CAR_LIST.ensureIndexIsVisible(index);
                                if (vehicle != null) {
                                    MultiPanel.SELF.show("EDIT_VEHICLE", vehicle, true);
                                }
                            }
                        }
                    });
                    JScrollPane carlistScroller = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    carlistScroller.setViewportView(CAR_LIST);
                    UIToolbox.setSize(carlistScroller, new Dimension(300, 440));
                inner.add(carlistScroller);
                JPanel detailsPane = new JPanel(new BorderLayout());
                    detailsPane.setBorder(new LineBorder(Color.LIGHT_GRAY));
                    final int WIDTH = 375;
                    JPanel details = new JPanel(new GridLayout(4, 2));
                        details.add(new InputField(DAYS = new JSpinner(), "Days", 40, 16, WIDTH, false));
                        UIToolbox.fillYearSpinner(DAYS, 1, 2000);
                        details.add(new InputField(START_DATE = new JLabel(), "Start Date", 40, 16, WIDTH, false));
                        details.add(EXPIRY_DATE_FIELD =new InputField(EXPIRY_DATE = new JLabel(), "End Date", 40, 16, WIDTH, false));
                        DAYS.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                int days = Integer.parseInt((String)DAYS.getValue());
                                PERMIT.setDaysLeft(days);
                                EXPIRY_DATE.setText(PERMIT.getEndDate().toString());
                                Vehicle vehicle = CAR_LIST.getSelectedValue();
                                if (vehicle != null) {
                                    if (PERMIT.getEndDate().before(vehicle.getExpiry())) {
                                        EXPIRY_DATE_FIELD.showError(false);
                                    } else {
                                        EXPIRY_DATE_FIELD.showError(true);
                                    }
                                }
                            }
                        });
                    detailsPane.add(details, BorderLayout.CENTER);
                inner.add(detailsPane);
            main.add(UIToolbox.box(new JPanel(new GridBagLayout()), inner), BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
        JPanel nav = new JPanel(new BorderLayout());
            JPanel navLeft = new JPanel();
                navLeft.add(new HorizontalButton("HOME", "HOME", "Home", this));
            nav.add(navLeft, BorderLayout.WEST);
            JPanel navCenter = new JPanel(new GridLayout(1, 1));
            navCenter.add(SUBMIT = new HorizontalButton("SUBMIT", "SUBMIT", "Submit", this));
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
        if (Main.USER.hasFines()) {
            MultiPanel.SELF.show("PERMIT_DENIED");
            return false;
        }
        // clear
        START_DATE.setText(null);
        EXPIRY_DATE.setText(null);
        SUBMIT.setForeground(null);
        // Get vehicle
        List<Vehicle> vehicles = DBManager.SELF.getVehiclesByUser(Main.USER);
        vehicles.add(null);
        CAR_LIST.clearSelection();
        CAR_LIST.setListData(vehicles.toArray(new Vehicle[vehicles.size()]));
        CAR_LIST.setSelectedIndex(0);
        CAR_LIST.requestFocusInWindow();
        if (CAR_LIST.getSelectedValue() == null) {
            MultiPanel.SELF.show("CREATE_VEHICLE", new Vehicle(Main.USER));
            return false;
        }
        // Get permit
        if (args.length < 1) {
            PERMIT = new Permit(CAR_LIST.getSelectedValue());
        } else {
            Vehicle vehicle = (Vehicle)args[0];
            PERMIT = new Permit(vehicle);
            for (Vehicle v : vehicles) {
                if (v != null && v.equals(vehicle)) {
                    CAR_LIST.setSelectedValue(v, true);
                }
            }
            if (CAR_LIST.isSelectionEmpty()) {
                CAR_LIST.setSelectedIndex(0);
            }
        }
        DAYS.setValue("" + PERMIT.getDaysLeft());
        if (PERMIT.getStartDate() != null) {
            START_DATE.setText(PERMIT.getStartDate().toString());
            EXPIRY_DATE.setText(PERMIT.getEndDate().toString());
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        String name = button.getName();
        if (name == "SUBMIT") {
            if (DBManager.SELF.addPermit(PERMIT) && Main.USER.setFines(PERMIT.getDaysLeft() * 3.50)) {
                if (Main.USER.isFirstTime()) {
                    MultiPanel.SELF.show("SUBSCRIPTION", PERMIT);
                } else {
                    MultiPanel.SELF.show("PAY_NOW", PERMIT);
                }
            } else {
                SUBMIT.setForeground(Color.RED);
            }
        } else {
            super.actionPerformed(e);
        }
    }
}
