package app.model;

import java.sql.*;

/**
 * In-memory representation of a vehicle.
 */
public class Vehicle {

    private final User owner;
    private String plate, make, model, insurer, policy;
    private int year;
    private Date expiry;
    private final boolean inDatabase;

    /**
     * Construct a new vehicle object.
     *
     * @param owner         user who owns the vehicle
     * @param plate         the license plate
     * @param make          the manufacturer
     * @param model         the car model
     * @param year          model year
     * @param insurer       the insurance company
     * @param policy        the insurance policy
     * @param expiry        the expiry date of insurance
     * @param inDatabase    whether the vehicle is in the database
     */
    public Vehicle(User user, String plate, String make, String model, int year,
            String insurer, String policy, Date expiry, boolean inDatabase) {
        this.owner = user;
        this.plate = plate;
        this.make = make;
        this.model = model;
        this.year = year;
        this.insurer = insurer;
        this.policy = policy;
        this.expiry = expiry;
        this.inDatabase = inDatabase;
    }
    public Vehicle(User user) {
        this(user, null, null, null, -1, null, null, null, false);
    }

    // Getters

    public User getOwner() {return owner;}
    public String getPlate() {return plate;}
    public String getMake() {return make;}
    public String getModel() {return model;}
    public int    getModelYear() {return year;}
    public String getInsurer() {return insurer;}
    public String getPolicy() {return policy;}
    public Date   getExpiry() {return expiry;}
    public boolean isInDatabase() {return inDatabase;}

    // Setters

    /**
     * Set the car make and model.
     *
     * @param makeModel     the manufacturer and the car model
     * @param year          model year
     */
    public void setMakeModel(MakeModel makeModel, int year) {
        if (!isInDatabase()) {
            this.make = makeModel.MAKE;
            this.model = makeModel.MODEL;
            this.year = year;
        }
    }

    /**
     * Update the vehicle's license plate.
     *
     * @param plate         the new license plate.
     * @return              if true, successfully update the license plate;
     *                      otherwise false.
     */
    public boolean setPlate(String plate) {
        if (plate.isEmpty()) {
            return false;
        }
        boolean success = !isInDatabase() || DBManager.SELF.updateVehicle(this, "PLATE", plate);
        this.plate = plate;
        return success;
    }

    /**
     * Update the vehicle's insurance policy.
     *
     * @param insurer       the vehicle's insurer
     * @param policy        the policy number
     * @param expiry        the expiry date of the policy
     * @return              if true, successfully update the insurance;
     *                      otherwise false.
     */
    public boolean updateInsurance(String insurer, String policy, Date expiry) {
        if (policy.isEmpty()) {
            return false;
        }
        this.insurer = insurer;
        this.policy = policy;
        this.expiry = expiry;
        return !isInDatabase() || DBManager.SELF.updateVehicle(this, "INSURANCE");
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {return false;}
        if (other.getClass().getName() == this.getClass().getName()) {
            Vehicle v = (Vehicle)other;
            return getPlate().equals(v.getPlate());
        }
        return false;
    }
}
