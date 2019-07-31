package app.uitoolkit;

import app.helpers.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class PageCounter extends JPanel {

    private static final int HEIGHT    = 35;    // panel height
    private static final int FONT_SIZE = 20;    // label font size

    private final JLabel TOTAL;
    private final JLabel CURRENT;

    public PageCounter(int current, int total, int width) {
        JPanel inner = new JPanel();
            inner.setLayout(new FlowLayout());
            CURRENT = new JLabel();
                CURRENT.setFont(MyFont.SEMIBOLD_FONT.deriveFont((float)FONT_SIZE));
                setCurrent(current);
            inner.add(CURRENT);
            JLabel outOf = new JLabel();
                outOf.setFont(MyFont.REGULAR_FONT.deriveFont((float)FONT_SIZE));
                outOf.setText(" out of ");
            inner.add(outOf);
            TOTAL = new JLabel();
                TOTAL.setFont(MyFont.SEMIBOLD_FONT.deriveFont((float)FONT_SIZE));
                TOTAL.setText("" + total);
            inner.add(TOTAL);
            inner.setAlignmentY(CENTER_ALIGNMENT);
            UIToolbox.setSize(inner, new Dimension(width, HEIGHT));
        UIToolbox.box(this, inner);
        setBorder(new LineBorder(Color.BLACK));
    }
    public PageCounter(int current, int total) {
        this(current, total, 200);
    }

    public void setCurrent(int current) {
        CURRENT.setText("" + current);
    }

    // FOR TESTING PURPOSES ONLY

    public static void main(String[] args) throws Exception {
        UITheme.setLookAndFeel();
        JFrame frame = new JFrame();
        frame.add(new PageCounter(5, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
