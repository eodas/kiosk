package app.views;

import app.helpers.*;
import app.model.*;
import app.uitoolkit.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class CreateVehiclePage extends AbstractView {

    private final JList<String>    MAKE    = new JList<String>();
    private final JList<MakeModel> MODEL   = new JList<MakeModel>();
    private final JSpinner         YEAR    = new JSpinner();
    private Vehicle VEHICLE;
    private boolean fromNewPermit = true;

    public CreateVehiclePage() {
        super("CREATE_VEHICLE", "Create Vehicle: 1 of 3");
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            JPanel leftCenter = new JPanel();
                leftCenter.setLayout(new BoxLayout(leftCenter, BoxLayout.PAGE_AXIS));
                leftCenter.setAlignmentX(LEFT_ALIGNMENT);
                MAKE.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                MAKE.setLayoutOrientation(JList.VERTICAL);
                MAKE.setBorder(new LineBorder(Color.LIGHT_GRAY));
                MAKE.setOpaque(false);
                MAKE.setCellRenderer(new ListRenderer<String>(300, 100) {
                    @Override
                    public String getHTML(String make) {
                        return UIToolbox.getHTML("/assets/htdocs/vehicle.make.html")
                            .replace("{MAKE}", make);
                    }
                });
                MAKE.addListSelectionListener(new ListSelectionListener() {
                    private boolean doAction = true;
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (MAKE.isSelectionEmpty()) {return;}
                        if (doAction) {
                            List<MakeModel> models = DBManager.SELF.getModelsByMake(MAKE.getSelectedValue());
                            MODEL.setListData(models.toArray(new MakeModel[models.size()]));
                            MODEL.setSelectedIndex(0);
                        }
                        doAction = !doAction;
                    }
                });
                List<String> makers = DBManager.SELF.getAutoMakers();
                MAKE.setListData(makers.toArray(new String[makers.size()]));
                JScrollPane makeScroller = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                makeScroller.setViewportView(MAKE);
                UIToolbox.setSize(makeScroller, new Dimension(300, 300));
                leftCenter.add(makeScroller);
                UIToolbox.fillYearSpinner(YEAR, 1900, Calendar.getInstance().get(Calendar.YEAR) - 1900 + 1);
                YEAR.setValue("" + Calendar.getInstance().get(Calendar.YEAR));
                leftCenter.add(new InputField(YEAR,  "Year", 40, 16, 250, false));
            centerPanel.add(leftCenter);
            JPanel rightCenter = new JPanel();
                rightCenter.setLayout(new BoxLayout(rightCenter, BoxLayout.PAGE_AXIS));
                rightCenter.setAlignmentX(LEFT_ALIGNMENT);
                MODEL.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                MODEL.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                MODEL.setVisibleRowCount(-1);
                MODEL.setBorder(new LineBorder(Color.LIGHT_GRAY));
                MODEL.setOpaque(false);
                MODEL.setCellRenderer(new ListRenderer<MakeModel>(300, 100) {
                    @Override
                    public String getHTML(MakeModel model) {
                        return UIToolbox.getHTML("/assets/htdocs/vehicle.model.html")
                            .replace("{MAKE}", model.MAKE)
                            .replace("{MODEL}", model.MODEL);
                    }
                });
                List<MakeModel> models = DBManager.SELF.getMakeModels();
                MODEL.setListData(models.toArray(new MakeModel[models.size()]));
                JScrollPane modelScroller = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                modelScroller.setViewportView(MODEL);
                UIToolbox.setSize(modelScroller, new Dimension(950, 400));
                rightCenter.add(modelScroller);
            centerPanel.add(rightCenter);
        add(UIToolbox.box(new JPanel(new GridBagLayout()), centerPanel), BorderLayout.CENTER);
        JPanel nav = new JPanel(new BorderLayout());
        JPanel navLeft = new JPanel();
            navLeft.add(new HorizontalButton("HOME", "HOME", "Home", this));
        nav.add(navLeft, BorderLayout.WEST);
        JPanel navCenter = new JPanel(new GridLayout(1, 2));
            navCenter.add(new HorizontalButton("PREV", "PREV", "Back", this));
            navCenter.add(new HorizontalButton("NEXT", "NEXT", "Next", this, true));
            navCenter.setBorder(new EmptyBorder(5, 5, 5, 5));
        nav.add(navCenter, BorderLayout.CENTER);
        JPanel navRight = new JPanel();
            navRight.add(new HorizontalButton("CANCEL", "CANCEL", "Cancel", this, true));
        nav.add(navRight, BorderLayout.EAST);
        add(nav, BorderLayout.SOUTH);
    }

    @Override
    public boolean prepareView(Object... args) {
        if (!super.prepareView(args)) {return false;}
        if (args.length > 0) {
            VEHICLE = (Vehicle)args[0];
            String make = VEHICLE.getMake();
            String model = VEHICLE.getModel();
            MakeModel mm = null;
            if (make != null && model != null) {
                mm = new MakeModel(VEHICLE.getMake(), VEHICLE.getModel());
            }
            if (make != null) {
                MAKE.setSelectedValue((Object)VEHICLE.getMake(), true);
            } else {
                MAKE.setSelectedIndex(0);
            }
            if (mm != null) {
                MODEL.setSelectedValue((Object)mm, true);
            } else {
                MODEL.setSelectedIndex(0);
            }
        }
        if (args.length >= 2) {
            fromNewPermit = (Boolean)args[1];
        } else {
            fromNewPermit = true;
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        String name = button.getName();
        if (name == "NEXT") {
            VEHICLE.setMakeModel(MODEL.getSelectedValue(), Integer.parseInt((String)YEAR.getValue()));
            MultiPanel.SELF.show("LICENSE", VEHICLE, fromNewPermit);
        } else if (name == "PREV") {
            if (fromNewPermit) {
                MultiPanel.SELF.show("NEW_PERMIT");
            } else {
                MultiPanel.SELF.show("VEHICLES");
            }
        } else {
            super.actionPerformed(e);
        }
    }
}
