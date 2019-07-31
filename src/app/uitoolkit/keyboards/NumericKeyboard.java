package app.uitoolkit.keyboards;

import app.helpers.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Implements a numeric keyboard.
 *
 *  [CE, BKSP]
 *  [7,  8, 9]
 *  [4,  5, 6]
 *  [1,  2, 3]
 *  [0,     .]
 */
public class NumericKeyboard extends Keyboard {

    // Array of key arrangements
    private static final String[][] KEYS = {
        {"CE", "BKSP"},
        {"7", "8", "9"},
        {"4", "5", "6"},
        {"1", "2", "3"},
        {"0"     , "."}
    };

    /**
     * Create and layout the numeric keyboard.
     *
     * @param field                 text input associated with the keyboard.
     * @param showControls          flag for enabling or disabling the controls
     *                              (clear, backspace) on the keyboard. Default: true.
     * @param showDecimalPoint      flag for enabling or disabling the decimal point
     *                              button on the keyboard. Default: false.
     */
    public NumericKeyboard(JTextField field, boolean showControls, boolean showDecimalPoint) {
        super(field);
        JPanel inner = new JPanel();
            inner.setLayout(new GridLayout(showControls ? 5 : 4, 1));
            for (int i = 0; i < KEYS.length; i += 1) {
                if (i == 0 && !showControls) {continue;}
                String[] row = KEYS[i];
                JPanel rp = new JPanel();
                for (String key : row) {
                    if (!showDecimalPoint && key == ".") {continue;}
                    AbstractButton ab = new JButton();

                    ab.addActionListener(this);
                    setKeyLookAndFeel(ab, key, true);
                    setEnabled(ab, field != null);

                    // Set key size
                    if (key == "BKSP") {
                        UIToolbox.setSize(ab, new Dimension(
                            KEY_WIDTH * 2 + KEY_SPACING,
                            KEY_HEIGHT));
                    } else if (key == "0") {
                        UIToolbox.setSize(ab, new Dimension(
                            showDecimalPoint ? KEY_WIDTH * 2 + KEY_SPACING : KEY_WIDTH * 3 + KEY_SPACING * 2,
                            KEY_HEIGHT));
                    } else {
                        UIToolbox.setSize(ab, new Dimension(
                            KEY_WIDTH,
                            KEY_HEIGHT));
                    }

                    // Add key to lookup
                    key += BLOOKUP.containsKey(key) ? "[1]" : "";
                    BLOOKUP.put(key, ab);
                    ab.setName(key);

                    rp.setOpaque(false);
                    rp.add(ab);
                }
                inner.add(rp);
            }
            inner.setOpaque(false);
        UIToolbox.box(this, inner);
    }
    public NumericKeyboard(JTextField field) {
        this(field, true, false);
    }

    /**
     * Invoked when an action occurs.
     * Perform updates on the text field given a specific key pressed.
     *
     * @param event        the event object.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        AbstractButton ab = (AbstractButton)event.getSource();
        String name = ab.getName();

        if (name.equals("CE")) {
            field.setText("");
        } else if (name.equals("BKSP")) {
            if (field.getSelectedText() == null) {
                String text = field.getText();
                int caret = field.getCaretPosition();
                if (caret > 0) {
                    field.setText(
                        text.substring(0, caret - 1) +
                        text.substring(caret, text.length()));
                    field.setCaretPosition(caret - 1);
                }
            } else {
                field.replaceSelection("");
            }
        } else {
            String add = ab.getText();
            if (field.getSelectedText() == null) {
                String text = field.getText();
                int caret = field.getCaretPosition();
                field.setText(
                    text.substring(0, caret) + add +
                    text.substring(caret, text.length()));
                field.setCaretPosition(caret + add.length());
             } else {
                 field.replaceSelection(add);
             }
        }
    } // actionPerformed

    // FOR TESTING PURPOSES ONLY

    public static void main(String[] args) throws Exception {
        UITheme.setLookAndFeel();
        JFrame frame = new JFrame();
        frame.setLayout(new java.awt.GridLayout(2, 1));
        JTextField tf = new JTextField();
        tf.setFont(MyFont.LIGHT_FONT.deriveFont((float)36));
        frame.add(tf);
        frame.add(new NumericKeyboard(tf));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
