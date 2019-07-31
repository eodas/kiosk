package app.uitoolkit;

import app.helpers.*;
import app.model.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * This class provides the title pane for the views.
 * Dynamically updates when user logged in.
 */
public class TitlePane extends JPanel {

    private static final int HORIZONTAL_MARGIN = 100;               // Horizontal padding on the left and right edges
    private static final int PADDING           = 20;                // Internal padding
    private static final int SUBTITLE_SIZE     = 20;                // Font size of the subtitle
    private static final int TITLE_SIZE        = 60;                // Font size of the title
    private static final String SUBTITLE_TEXT  = "Executive Order Parking";    // Text for the subtitle
    private static String HTML                 = null;              // HTML for about label

    private final JLabel TITLE;       // label of the title of the view.
    private final JLabel USER_TAG;    // label of the name of the user logged in.

    /**
     * Construct the title panel of the views.
     */
    public TitlePane() {
        JPanel inner = new JPanel();
            inner.setLayout(new BorderLayout());
            TITLE = new JLabel();
                TITLE.setFont(MyFont.LIGHT_FONT.deriveFont((float)TITLE_SIZE));
                TITLE.setHorizontalAlignment(SwingConstants.LEFT);
            inner.add(TITLE, BorderLayout.CENTER);
            JPanel subtitle = new JPanel(new GridLayout(1, 2));
                JLabel apptitle = new JLabel();
                    apptitle.setFont(MyFont.BOLD_FONT.deriveFont((float)SUBTITLE_SIZE));
                    apptitle.setHorizontalAlignment(SwingConstants.LEFT);
                    apptitle.setText(SUBTITLE_TEXT.toUpperCase());
                subtitle.add(apptitle);
                USER_TAG = new JLabel();
                    USER_TAG.setFont(MyFont.REGULAR_FONT.deriveFont((float)SUBTITLE_SIZE));
                    USER_TAG.setHorizontalAlignment(SwingConstants.RIGHT);
                subtitle.add(USER_TAG);
            inner.add(subtitle, BorderLayout.NORTH);
            inner.add(new JLabel(getHTML()), BorderLayout.EAST);
            inner.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
            UIToolbox.setSize(inner, new Dimension(
                UIToolbox.getScreenSize().width - HORIZONTAL_MARGIN * 2,
                TITLE.getFontMetrics(TITLE.getFont()).getHeight() +
                subtitle.getFontMetrics(subtitle.getFont()).getHeight() + PADDING * 2 + 10));
        UIToolbox.box(this, inner);
    }

    private String getHTML() {
        if (HTML != null) {return HTML;}
        HTML = UIToolbox.getHTML("/assets/htdocs/about.html");
        return HTML;
    }
    public void setText(String text) {
        TITLE.setText(text);
    }
    public void setUserTag(User user) {
        USER_TAG.setText((user == null) ? "" : "Logged in: " + user.getFirstName() + " " + user.getSurName());
    }

    // FOR TESTING PURPOSES ONLY

    public static void main(String[] args) throws Exception {
        UITheme.setLookAndFeel();
        JFrame frame = new JFrame();
        TitlePane tp = new TitlePane();
            tp.setText("Welcome");
        frame.add(tp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
