package app.uitoolkit;

import app.helpers.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Abstract definition of a list item in a JList.
 * Each item is render by HTML.
 */
public abstract class ListRenderer<T> extends JLabel implements ListCellRenderer<T> {

    private T value;

    public ListRenderer(int width, int height) {
        UIToolbox.setSize(this, new Dimension(width, height));
    }

    /**
     * This method finds the text corresponding
     * to the selected value and returns the label, set up
     * to display the text.
     */
    @Override
    public Component getListCellRendererComponent(JList<? extends T> list,
            T value, int index, boolean isSelected, boolean cellHasFocus) {
        this.value = value;
        setText(getHTML(value));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }

    /**
     * Generates and returns the HTML text in the label
     *
     * @param value       the object value corresponding to the list item
     * @return            the HTML text
     */
    public abstract String getHTML(T value);

    /**
     * Returns the value corresponding to the list item.
     *
     * @return the value corresponding to the list item.
     */
    public T getValue() {
        return value;
    }
}
