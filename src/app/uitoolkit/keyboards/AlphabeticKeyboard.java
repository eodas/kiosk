package app.uitoolkit.keyboards;

import app.helpers.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Implements an alphabetic keyboard.
 *
 *  [Q,     W, E, R, T, Y, U, I, O, P,     BKSP]
 *  [CAPS,  A, S, D, F, G, H, J, K, L,    ENTER]
 *  [SHIFT, Z, X, C, V, B, N, M, ., -, +, SHIFT]
 *  [CLRS,  @, SPACE,     , _, .COM, PREV, NEXT]
 */
public class AlphabeticKeyboard extends Keyboard {

    // Array of key arrangements
    private static final String[][] KEYS = {
        {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "BKSP"},
        {"CAPS", "A", "S", "D", "F", "G", "H", "J", "K", "L", "ENTER"},
        {"SHIFT", "Z", "X", "C", "V", "B", "N", "M", ".", "-", "+", "SHIFT"},
        {"CLEAR", "@", "SPACE", "_", ".COM", "PREV", "NEXT"}
    };

    private static final int KEYBOARD_WIDTH;                                    // Total width of the keyboard in pixel
    private static final String[] SYMBOLS = {"+", ".", "-", "@", "_", ".COM"};  // List of key symbols
    private static final String[] ALPHABET = new String[26];                    // List of alphabets, for shiftKey
    static {
        KEYBOARD_WIDTH = (KEY_WIDTH + KEY_SPACING) * KEYS[0].length + KEY_SPACING;
        for (int i = 0; i < 26; i += 1) {
            ALPHABET[i] = "" + (char)('A' + i);
        }
    }

    private boolean ENABLE_SYMBOL;                                              // Flag for enabling symbols

    /**
     * Create and layout the alphabetic (QWERTY) keyboard.
     *
     * @param field             text input associated with the keyboard.
     * @param enableSymbols     flag for enabling or disabling the symbols
     *                          on the keyboard. Default: true.
     * @param showCapsLock      flag for enabling or disabling the caps lock
     *                          button on the keyboard. Default: true.
     */
    public AlphabeticKeyboard(JTextField field, boolean enableSymbols, boolean showCapsLock) {
        super(field);
        JPanel inner = new JPanel();
            inner.setLayout(new GridLayout(4, 1));
            for (String[] row : KEYS) {
                JPanel rp = new JPanel();
                for (String key : row) {
                    AbstractButton ab;
                    if (key == "SHIFT" || key == "CAPS") {
                        ab = new JToggleButton();
                    } else {
                        ab = new JButton();
                    }

                    ab.addActionListener(this);
                    setKeyLookAndFeel(ab, key);
                    setEnabled(ab, field != null);

                    // Set key size
                    if (key == "SPACE") {
                        UIToolbox.setSize(ab, new Dimension(
                            KEYBOARD_WIDTH - 7 * (KEY_WIDTH + KEY_SPACING) - KEY_SPACING * 2,
                            KEY_HEIGHT));
                    } else if (key == "BKSP" || key == ".COM" || key == "CLEAR") {
                        UIToolbox.setSize(ab, new Dimension(
                            KEY_WIDTH * 2 + KEY_SPACING,
                            KEY_HEIGHT));
                    } else if (key == "ENTER" || key == "CAPS") {
                        UIToolbox.setSize(ab, new Dimension(
                            (int)(KEY_WIDTH * 1.5 + KEY_SPACING),
                            KEY_HEIGHT));
                    } else {
                        UIToolbox.setSize(ab, new Dimension(
                            KEY_WIDTH,
                            KEY_HEIGHT));
                    }

                    // Hide caps lock, disabled
                    if (!showCapsLock) {
                        if (key == "ENTER") {
                            UIToolbox.setSize(ab, new Dimension(
                                KEY_WIDTH * 2 + KEY_SPACING,
                                KEY_HEIGHT));
                        } else if (key == "CAPS") {
                            continue;
                        }
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
        setSymbolsEnabled(enableSymbols);
    }
    public AlphabeticKeyboard(JTextField field) {
        this(field, true, true);
    }

    /**
     * Converts the alphabetic keys to upper or lower case.
     *
     * @param caps flag to display as upper case if true else lower case.
     */
    public void shiftKey(boolean caps) {
        for (String alpha : ALPHABET) {
             AbstractButton ab = BLOOKUP.get(alpha);
             ab.setText(caps ? alpha.toUpperCase() : alpha.toLowerCase());
        }
    }

    /**
     * Enables or disables the symbol keys on the keyboard.
     *
     * @param enable the symbol keys if true, else disable them.
     */
    @Override
    public void setSymbolsEnabled(boolean enable) {
        ENABLE_SYMBOL = enable;
        for (String sym : SYMBOLS) {
             AbstractButton ab = BLOOKUP.get(sym);
             setEnabled(ab, field != null && enable);
        }
    }

    /**
     * Associates the text input with the keyboard.
     *
     * @param field     text input to link to the keyboard.
     */
    @Override
    public void setInputComponent(JTextField field) {
        super.setInputComponent(field);
        if (!ENABLE_SYMBOL && field != null) {
            setSymbolsEnabled(false);
        }
    }

    /**
     * Invoked when an action occurs.
     * Perform updates on the text field given a specific key pressed.
     * Synchronize the shift keys.
     *
     * @param event     the event object.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        AbstractButton ab = (AbstractButton)event.getSource();
        String name = ab.getName();
        AbstractButton s1 = BLOOKUP.get("SHIFT");
        AbstractButton s2 = BLOOKUP.get("SHIFT[1]");
        AbstractButton caps = BLOOKUP.get("CAPS");

        if (name.startsWith("SHIFT")) {
            if (!caps.isSelected()) {
                (ab == s1 ? s2 : s1).setSelected(ab.isSelected());
                shiftKey(ab.isSelected());
            } else {
                ab.setSelected(false);
            }
        } else if (name.startsWith("CAPS")) {
            shiftKey(caps.isSelected());
            caps.setText(caps.isSelected() ? "CAPS" : "caps");
        } else if (name.equals("CLEAR")) {
            field.setText("");
        } else if (name.equals("ENTER")) {
            field.transferFocus();
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
        } else if (name.equals("PREV")) {
            int caret = field.getCaretPosition();
            if (caret > 0) {
                field.setCaretPosition(caret - 1);
            }
        } else if (name.equals("NEXT")) {
            int caret = field.getCaretPosition();
            if (caret < field.getText().length()) {
                field.setCaretPosition(caret + 1);
            }
        } else {
            String add;
            if (name.equals("SPACE")) {
                add = " ";
            } else if (name.equals(".COM")) {
                add = ".com";
            } else {
                add = ab.getText();
            }
            String text = field.getText();
            if (field.getSelectedText() == null) {
                int caret = field.getCaretPosition();
                field.setText(
                    text.substring(0, caret) + add +
                    text.substring(caret, text.length()));
                field.setCaretPosition(caret + add.length());
            } else {
                field.replaceSelection(add);
            }
        }
        // release the shift keys
        if (ab != s1 && ab != s2 && s1.isSelected()) {
            s1.setSelected(false);
            s2.setSelected(false);
            if (!caps.isSelected()) {
                shiftKey(false);
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
        frame.add(new AlphabeticKeyboard(tf));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
