package app.views;

import app.Main;
import app.helpers.*;
import app.model.*;
import app.uitoolkit.*;

import java.util.List;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class HistoryPage extends AbstractView {

    private final JList<Permit> PERMITS;
    private final JLabel RECEIPT = new JLabel();

    public HistoryPage() {
        super("HISTORY", "History");
        JPanel main = new JPanel(new BorderLayout());
            JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
                PERMITS = new JList<Permit>();
                    PERMITS.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                    PERMITS.setLayoutOrientation(JList.VERTICAL);
                    PERMITS.setVisibleRowCount(-1);
                    PERMITS.setBorder(new LineBorder(Color.LIGHT_GRAY));
                    PERMITS.setOpaque(false);
                    PERMITS.setCellRenderer(new ListRenderer<Permit>(400, 125) {
                        @Override
                        public String getHTML(Permit permit) {
                            return UIToolbox.getHTML("/assets/htdocs/permit.html")
                                .replace("{PLATE}", permit.getVehicle().getPlate())
                                .replace("{ISSUED_DATE}", permit.getIssueDate().toString())
                                .replace("{EXPIRY_DATE}", permit.getEndDate().toString());
                        }
                    });
                    PERMITS.addListSelectionListener(new ListSelectionListener() {
                        @Override
                        public void valueChanged(ListSelectionEvent e) {
                            if (PERMITS.isSelectionEmpty()) {return;}
                            Permit permit = PERMITS.getSelectedValue();
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
                    });
                    JScrollPane permitScroller = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    permitScroller.setViewportView(PERMITS);
                    UIToolbox.setSize(permitScroller, new Dimension(400, 535));
                inner.add(permitScroller);
                JPanel detailsPane = new JPanel(new BorderLayout());
                    detailsPane.setBorder(new LineBorder(Color.LIGHT_GRAY));
                    JPanel details = new JPanel(new GridBagLayout());
                        details.setBorder(new LineBorder(Color.LIGHT_GRAY));
                        details.setBackground(Color.WHITE);
                        UIToolbox.setSize(RECEIPT, new Dimension(500, 535));
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
        List<Permit> permits = DBManager.SELF.getPermitsByUser(Main.USER);
        PERMITS.clearSelection();
        PERMITS.setListData(permits.toArray(new Permit[permits.size()]));
        PERMITS.requestFocusInWindow();
        PERMITS.setSelectedIndex(0);
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
