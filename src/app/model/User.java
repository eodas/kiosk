package app.model;

/**
 * In-memory representation of an user.
 */
public class User {

    private long id;                    // user employee ID
    private String fname, sname, email; // first name, surname, email address
    private double fines;               // outstanding fines
    private boolean ftime;              // first time using the application

    /**
     * Construct a new user object.
     *
     * @param id            user employee ID
     * @param fname         first name
     * @param sname         surname (last name)
     * @param email         email address, maybe null
     * @param fines         outstanding fines
     * @param ftime         first time using the application
     */
    public User(long id, String fname, String sname, String email, double fines, boolean ftime) {
        this.id = id;
        this.fname = fname;
        this.sname = sname;
        this.email = email;
        this.fines = fines;
        this.ftime = ftime;
    }

    // Getters

    public long    getID() {return id;}                // Returns user employee ID
    public String  getFirstName() {return fname;}    // Returns first name
    public String  getSurName() {return sname;}        // Returns surname
    public String  getEmail() {return email;}        // Returns email address
    public double  getFines() {return fines;}        // Returns outstanding fines

    public boolean hasEmail() {return getEmail() != null;}    // Returns true if email address is assigned
    public boolean hasFines() {return getFines() > 0;}        // Returns true if fines are non-zero

    public boolean isFirstTime() {return ftime;}            // First time using the application

    // Setters

    /**
     * Update the user's first and last names
     *
     * @param fname         first name
     * @param sname         surname
     * @return              true if the transaction was successful, otherwise false.
     */
    public boolean setName(String fname, String sname) {
        this.fname = fname;
        this.sname = sname;
        return DBManager.SELF.updateUser(this, "NAME");
    }

    /**
     * Check if the given PIN matches that stored in the
     * database for the given user. Then changes the PIN
     * if the two given new pins match.
     *
     * @param PIN           the given PIN to check
     * @param newPIN        the new PIN
     * @param newPIN2       the new PIN confirmed
     * @return              true, if valid, otherwise false.
     */
    public boolean changePIN(int PIN, int newPIN, int newPIN2) {
        return newPIN == newPIN2 && DBManager.SELF.updateUser(this, "PIN", PIN, newPIN);
    }

    /**
     * Update the user's email address
     *
     * @param email         subscription email address
     * @return              true if the transaction was successful, otherwise false.
     */
    public boolean setEmail(String email) {
        this.email = email;
        return DBManager.SELF.updateUser(this, "EMAIL");
    }

    /**
     * Update the user's outstanding fines.
     *
     * @param fines         the outstanding fines
     * @return              true if the transaction was successful, otherwise false.
     */
    public boolean setFines(double fines) {
        if (fines < 0) {
            throw new RuntimeException("Fines must be non-negative.");
        }
        this.fines = fines;
        return DBManager.SELF.updateUser(this, "FINES");
    }

    /**
     * Pay down the outstanding fines.
     *
     * @param payment       the amount paid.
     * @return              the amount of change.
     */
    public double payFines(double payment) {
        if (payment < 0) {
            throw new RuntimeException("Payment must be non-negative.");
        }
        double change;
        if (payment >= fines) {
            change = payment - fines;
            setFines(0);
        } else {
            change = 0;
            setFines(fines - payment);
        }
        return change;
    }

    /**
     * Logout this user.
     *
     * @return true if logged out successfully, otherwise false.
     */
    public boolean logout() {
        return DBManager.SELF.updateUser(this, "LOGOUT");
    }
}
