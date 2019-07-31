package app.helpers;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

/**
 * This static class contains a collection of commonly
 * used methods for constructing, displaying, manipulating
 * user interface components.
 */
public class UIToolbox {

    private static UIToolbox self = new UIToolbox();

    private UIToolbox() { } // static class, no constructor

    /**
     * Return the screen size.
     *
     * @return the screen size.
     */
    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Add the panel to the outer panel and box.
     * This fixes the size of the panel, so it does not
     * expand when resized.
     *
     * @param outer     panel to be added to.
     * @param inner     panel to add to the outer.
     * @return          the outer panel
     */
    public static JPanel box(JPanel outer, JPanel inner) {
        Box box = Box.createVerticalBox();
        box.add(inner);
        outer.add(box);
        return outer;
    }

    /**
     * Set the size of the given of the component.
     *
     * @param comp      component to set the size of
     * @param size      width and height of the panel
     * @return          the component that was given.
     */
    public static Component setSize(Component comp, Dimension size) {
        comp.setPreferredSize(size);
        comp.setMinimumSize(size);
        comp.setMaximumSize(size);
        comp.setSize(size);
        return comp;
    }

    /**
     * Makes the given JFrame display as fullscreen.
     *
     * @param frame     the JFrame to make fullscreen.
     */
    public static void fullscreen(JFrame frame) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
    }

    /**
     * Reads HTML document at the given address and
     * returns its content as a string.
     *
     * @param address   of the HTML document to read
     * @return          the content of the HTML document
     */
    public static String getHTML(String address) {
        InputStream fis = null;
        BufferedReader reader = null;
        String html = "";
        try {
            fis = self.getClass().getResourceAsStream(address);
            reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            for (;;) {
                line = reader.readLine();
                if (line == null) {break;}
                html += line;
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                reader.close();
                fis.close();
            } catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
        return html;
    }

    /**
     * Fills the months into the combobox given the year.
     * Months and year are greater than or equal to current date.
     *
     * @param monthComboBox     the combobox to fill
     * @param currentYear       constraint for current year
     */
    public static void fillMonthComboBox(JComboBox<String> monthComboBox, boolean currentYear) {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        for (int i = 0; i < months.length - 1; i++) {
            if (!currentYear || i >= Calendar.getInstance().get(Calendar.MONTH)) {
                monthComboBox.addItem(months[i]);
            }
        }
    }

    /**
     * Fills the next N years into the spinner, given a start year.
     *
     * @param yearSpinner       the spinner to fill
     * @param year              the start year
     * @param nyears            the N years beginning with the start year.
     */
    public static void fillYearSpinner(JSpinner yearSpinner, int year, int nyears) {
        String[] years = new String[nyears];
        for (int i = 0; i < years.length; i++) years[i] = "" + (year + i);
        SpinnerListModel yearsModel = new SpinnerListModel(years);
        yearSpinner.setModel(yearsModel);
    }

    /**
     * Converts calendar to java.sql.Date
     *
     * @param date      calendar object to convert
     * @return          date converted to java.sql.Date
     */
    public static java.sql.Date convertToSQLDate(Calendar date) {
        return new java.sql.Date(date.getTime().getTime());
    }

}
