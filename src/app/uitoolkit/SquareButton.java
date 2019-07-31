package app.uitoolkit;

import app.helpers.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * This class implements a large square button (tile)
 * consisting of a large icon and a label text.
 */
public class SquareButton extends JButton {

    // Default values

    private static final int SIZE       = 200;    // Button size (width and height)
    private static final int ICON_SIZE  = 100;    // Icon font size
    private static final int LABEL_SIZE = 16;     // Label font size

    private static final String HTML = UIToolbox.getHTML("/assets/htdocs/iconLabel.vertical.html");

    /**
     * Construct a new button.
     *
     * @param name        of the button. Used to reference in it.
     * @param icon        to show in the center of the button. (large)
     * @param label       to show in the bottom-left of the button
     * @param listener    to perform an action when clicked.
     * @param size        the width and height of the button
     */
    public SquareButton(String name, String icon, String label, ActionListener listener, int size) {
        setName(name);
        setText(getHTML(icon, label, size));
        if (listener != null) {addActionListener(listener);}
        size = (size <= 0) ? SIZE : size;
        UIToolbox.setSize(this, new Dimension(size, size));
    }
    public SquareButton(String name, String icon, String label, ActionListener listener) {
        this(name, icon, label, listener, SIZE);}
    public SquareButton(String name, String icon, String label) {
        this(name, icon, label, null);}

    private String getHTML(String icon, String label, int size) {
        return HTML.replace("{ICON_SIZE}",  ""+((int) (((double) size/SIZE) * ICON_SIZE)))
                   .replace("{LABEL_SIZE}", ""+((int) (((double) size/SIZE) * LABEL_SIZE)))
                   .replace("{ICON}",       (MyFont.ICONS.containsKey(icon)) ? MyFont.ICONS.get(icon) : " ")
                   .replace("{LABEL}",      label);
    }

    // FOR TESTING PURPOSES ONLY

    public static void main(String[] args) throws Exception {
        UITheme.setLookAndFeel();
        JFrame frame = new JFrame();
        JPanel inner = new JPanel();
            inner.setLayout(new GridLayout(2, 3));
            ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    System.out.println(((JButton)event.getSource()).getName());
                }
            };
            inner.add(new SquareButton("USER", "USER", "My Profile", al));
            inner.add(new SquareButton("VEHICLES", "CAR", "My Vehicles", al));
            inner.add(new SquareButton("SUBSCIPTION", "MAIL", "Subscription", al));
            inner.add(new SquareButton("NEW_PERMIT", "NEW", "Get Permit", al));
            inner.add(new SquareButton("HISTORY", "FILES", "History", al));
            inner.add(new SquareButton("PAY_NOW", "DOLLAR", "Pay Now", al));
        frame.add(UIToolbox.box(new JPanel(), inner));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
