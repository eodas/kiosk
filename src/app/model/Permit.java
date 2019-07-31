package app.model;

import java.sql.*;
import java.util.Calendar;

import app.helpers.UIToolbox;

/**
 * In-memory representation of a permit. Read-only.
 */
public class Permit {

    private Vehicle vehicle = null;
    private Date startDate = null, endDate = null, issueDate = null;
    private int days = 1;
    private final boolean inDatabase;

    /**
     * Construct a new Permit Object
     *
     * @param vehicle           the vehicle assigned to this permit
     * @param startDate         the start date of the permit
     * @param endDate           the end date of the permit
     * @param issueDate         the date and time the permit was issued
     * @param inDatabase        whether the vehicle is in the Database
     */
    public Permit(Vehicle vehicle, Date startDate, Date endDate, Date issueDate, boolean inDatabase) {
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.issueDate = issueDate;
        this.inDatabase = inDatabase;
    }
    public Permit(Vehicle vehicle, int days) {
        if (vehicle != null) {
            this.setVehicle(vehicle);
            this.setDaysLeft(days);
        }
        this.inDatabase = false;
    }
    public Permit(Vehicle vehicle) {
        this(vehicle, 1);
    }

    public Vehicle getVehicle() {return vehicle;}
    public Date getStartDate() {return startDate;}
    public Date getEndDate() {
        if (endDate == null) {
            endDate = DBManager.SELF.getPermitEndDate(this);
        }
        return endDate;
    }
    public Date getIssueDate() {
        if (issueDate == null) {
            issueDate = DBManager.SELF.getPermitIssuedDate(this);
        }
        return issueDate;
    }
    public int getDaysLeft() {return days;}
    public boolean isInDatabase() {return inDatabase;}

    /**
     * Sets the vehicle of associated with the permit
     *
     * @param vehicle     associated with the permit
     */
    public void setVehicle(Vehicle vehicle) {
        if (!isInDatabase()) {
            this.vehicle = vehicle;
            this.startDate = DBManager.SELF.getPermitExpiryByVehicle(vehicle);
            Calendar date = Calendar.getInstance();
            if (this.startDate != null) {
                date.setTime(startDate);
            }
            date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);
            date.set(Calendar.ZONE_OFFSET, 0);
            date.set(Calendar.HOUR, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            this.startDate = UIToolbox.convertToSQLDate(date);
        }
    }

    /**
     * Sets the vehicle of associated with the permit
     *
     * @param vehicle     associated with the permit
     */
    public void setDaysLeft(int days) {
        if (!isInDatabase() && this.startDate != null) {
            Calendar date = Calendar.getInstance();
            date.setTime(this.startDate);
            date.set(Calendar.DATE, date.get(Calendar.DATE) + days);
            this.endDate = UIToolbox.convertToSQLDate(date);
            this.days = days;
        }
    }
}
