package app.uitoolkit;

import app.helpers.*;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 * Base definition of the an input field.
 * Untyped; does not specify the input field type.
 */
public class InputField extends JPanel {

    private static final int LABEL_SIZE = 14;    // label font size
    private static final int INPUT_SIZE = 24;    // input field font size
    private static final int WIDTH      = 300;   // total width of this component

    public final JPanel INNER;        // reference to the inner panel
    public final JLabel LABEL;        // reference to the label object
    public final JComponent INPUT;    // reference to the input object

    public InputField(JComponent input, String text, int isize, int lsize, int width, boolean labelOnTop) {
        int ih, lh;
        // default values
        if (lsize <= 0) {lsize = LABEL_SIZE;}
        if (isize <= 0) {isize = INPUT_SIZE;}
        if (width <= 0) {width = WIDTH;}
        // store references and construct components
        setLayout(new GridLayout(1, 1));
        INPUT = input;
        INNER = new JPanel();
            INNER.setLayout(new BorderLayout());
            LABEL = new JLabel(text.toUpperCase());
                LABEL.setHorizontalAlignment(SwingConstants.LEFT);
                LABEL.setFont(MyFont.SEMIBOLD_FONT.deriveFont((float)lsize));
                lh = LABEL.getFontMetrics(LABEL.getFont()).getHeight();
            INNER.add(LABEL, labelOnTop ? BorderLayout.NORTH : BorderLayout.SOUTH);
                INPUT.setFont(MyFont.REGULAR_FONT.deriveFont((float)isize));
                ih = INPUT.getFontMetrics(INPUT.getFont()).getHeight() + 20;
            INNER.add(INPUT, BorderLayout.CENTER);
        UIToolbox.setSize(this, new Dimension(width + 20, ih + lh + 10));
        UIToolbox.box(this, INNER);
    }
    public InputField(JComponent input, String text, boolean labelOnTop) {
        this(input, text, -1, -1, -1, labelOnTop);}
    public InputField(JComponent input, String text, int width) {
        this(input, text, -1, -1, width, true);}
    public InputField(JComponent input, String text) {
        this(input, text, WIDTH);}

    /**
     * Displays or hides error.
     *
     * @param show    if true, display error, otherwise hide error.
     */
    public void showError(boolean show) {
        if (INPUT instanceof JTextField) {
            INPUT.setBorder(show ? new LineBorder(Color.RED) : (new JTextField()).getBorder());
        }
        INPUT.setForeground(show ? Color.RED : null);
        LABEL.setForeground(show ? Color.RED : null);
    }

    // FOR TESTING PURPOSES ONLY

    public static void main(String[] args) throws Exception {
        UITheme.setLookAndFeel();
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        JPanel inner = new JPanel();
                inner.setLayout(new BoxLayout(inner, BoxLayout.PAGE_AXIS));
                inner.add(new InputField(new JTextField(), "Employee ID:"));
                inner.add(new InputField(new JPasswordField(), "PIN:"));
                inner.setLayout(new BoxLayout(inner, BoxLayout.PAGE_AXIS));
                inner.add(new InputField(new JComboBox<String>((new java.text.DateFormatSymbols()).getMonths()), "Months:"));
                inner.add(new InputField(new JSpinner(), "Year:"));
                inner.add(new InputField(new JFormattedTextField(), "Amount:"));
        frame.add(UIToolbox.box(new JPanel(), inner));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
