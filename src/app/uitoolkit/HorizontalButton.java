package app.uitoolkit;

import app.helpers.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class implements a small horizontally oriented button.
 * consisting of an icon and a label text.
 */
public class HorizontalButton extends JButton {

    // Default values

    private static final int HEIGHT     = 50;    // Button size (width and height)
    private static final int ICON_SIZE  = 20;    // Icon font size
    private static final int LABEL_SIZE = 18;    // Label font size
    private static final String HTML =           // HTML for button formatting
        "<html><body>"
        + "{PART1}{SPACE}{PART2}"
        + "</body></html>";
    private static final String PART =           // HTML for each part (icon and label)
        "<span style=\"font-family: {FONT}; font-size: {SIZE}pt; line-height: {HEIGHT};\">{TEXT}</span>";

    /**
     * Construct a new button.
     *
     * @param name        of the button. Used to reference in it.
     * @param icon        to show in the center of the button. (large)
     * @param label       to show in the bottom-left of the button
     * @param listener    to perform an action when clicked.
     * @param size        the width and height of the button
     */
    public HorizontalButton(String name, String icon, String label, ActionListener listener, int height, int width, boolean rightIcon) {
        setName(name);
        if (height <= 0) {height = HEIGHT;}
        setText(getHTML(icon, label, height, rightIcon));
        if (listener != null) {addActionListener(listener);}
        if (width <= 0) {
            width = this.getFontMetrics(
                MyFont.ICON_FONT.deriveFont(((float) height/HEIGHT) * ICON_SIZE)
            ).stringWidth(icon + label) + 20;
        }
        UIToolbox.setSize(this, new Dimension(width, height));
    }
    public HorizontalButton(String name, String icon, String label, ActionListener listener, int width, boolean rightIcon) {
        this(name, icon, label, listener, HEIGHT, width, rightIcon);}
    public HorizontalButton(String name, String icon, String label, ActionListener listener, int width) {
        this(name, icon, label, listener, HEIGHT, width, false);}
    public HorizontalButton(String name, String icon, String label, ActionListener listener, boolean rightIcon) {
        this(name, icon, label, listener, HEIGHT, -1, rightIcon);}
    public HorizontalButton(String name, String icon, String label, ActionListener listener) {
        this(name, icon, label, listener, HEIGHT, -1, false);}
    public HorizontalButton(String name, String icon, String label) {
        this(name, icon, label, null);}

    private String getHTML(String icon, String label, int height, boolean rightIcon) {
        String p1, p2;
        p1 = (icon == null) ? "" :
             PART.replace("{FONT}", "MyIcons")
                 .replace("{SIZE}", ""+((int) (((double) height/HEIGHT) * ICON_SIZE)))
                 .replace("{HEIGHT}", ""+height)
                 .replace("{TEXT}", (MyFont.ICONS.containsKey(icon)) ? MyFont.ICONS.get(icon) : " ");
        p2 = PART.replace("{FONT}", "OpenSansRegular")
                 .replace("{SIZE}", ""+((int) (((double) height/HEIGHT) * LABEL_SIZE)))
                 .replace("{HEIGHT}", ""+height)
                 .replace("{TEXT}", label);
        return HTML.replace("{PART1}", rightIcon ? p2 : p1)
                   .replace("{SPACE}", (icon == null) ? "" : "&nbsp;&nbsp;&nbsp;&nbsp;")
                   .replace("{PART2}", rightIcon ? p1 : p2);
    }

    // FOR TESTING PURPOSES ONLY

    public static void main(String[] args) {
        UITheme.setLookAndFeel();
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.out.println(((JButton)event.getSource()).getName());
            }
        };
        frame.add(new HorizontalButton("HOME", "HOME", "Home", al));
        frame.add(new HorizontalButton("BACK", "BACK", "Back", al));
        frame.add(new HorizontalButton("PREV", "PREV", "Prev", al, 200));
        //frame.add(new PageCounter(5, 10, 400));
        frame.add(new HorizontalButton("NEXT", "NEXT", "Next", al, 200, true));
        frame.add(new HorizontalButton("CONTINUE", "FORW", "Continue", al, true));
        frame.add(new HorizontalButton("EXIT", "EXIT", "Exit", al, true));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
