package app.model;

import java.util.regex.*;

/**
 * This class provides validation for email addresses.
 */
public class EmailFormatValidator {

    // String of the regular expression
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    // Compiled regular expression
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * Validates the email address given.
     * Returns true if valid, else false.
     *
     * @param email         the email address to validate.
     * @return              true if email is valid, else false.
     */
    public static boolean validate(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // FOR TESTING PURPOSES ONLY

    public static void main(String[] args) {
        Object[][] tests = new Object[][] {
            { "address@mail.com.2j",false },        // it's not allowed to have a digit in the second level tld
            { "address@mail@example.com", false },  // you cannot have @ twice in the address
            { "address!!!@mail.com", false },       // you cannot the have special character '!' in the address
            { "address@.com", false },              // tld cannot start with a dot
            { "addressmail.com", false },           // must contain a @ character and a tld
            { ".address.mail@example.com", false }, // the address cannot start with a dot
            { "address..mail@example.com", false }, // you cannot have double dots in your address
            { "address@mail.com",true },
            { "address+mail@example.com", true },
            { "abc.efg-900@mail-box.com", true },
            { "abc123@example.com.gr", true }
        };
        for (Object[] test : tests) {
            String teststr = (String)test[0];
            boolean valid = (Boolean)test[1];
            boolean passed = EmailFormatValidator.validate(teststr) == valid;
            System.out.printf("%-30s%10s%10s\n",
                teststr,
                valid ? "VALID" : "INVALID",
                passed ? "PASSED" : "FAILED"
            );
        }
    }
}
