package app.model;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * This class manages and provides the set of allowed actions
 * that can be performed on the database. The actions are
 * specific to the database, its tables and schema. Such actions
 * include, but not limited to, creating, updating or querying
 * the users, vehicles, or permits.
 */
public class DBManager {

    private static final String DB_ADDRESS = "jdbc:sqlite:KioskParking.db";
    public static final DBManager SELF = new DBManager(DB_ADDRESS);
    private Connection connection;

    /**
     * Print an error message on exception thrown.
     *
     * @param e     the exception
     */
    private void error(Exception e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
        e.printStackTrace(System.err);
    }

    /**
     * Construct a new DB manager.
     * Loads the database driver and connects to the database.
     *
     * @param dbURI     the path to the database
     */
    private DBManager(String dbURI) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            error(e);
        }
        while (connection == null) {
            connect(dbURI);
        }
        System.out.println("DBManager initialized.");
    }

    /**
     * Connects to the database.
     *
     * @param dbURI     the path to the database
     */
    private void connect(String dbURI) {
        try {
            connection = DriverManager.getConnection(dbURI);
            connection.setAutoCommit(true);
        } catch (Exception e) {
            error(e);
        }
    }

    /**
     * Disconnects from the database.
     * Closes the connection to the database (frees system resources).
     */
    public void destroy() {
        try {
            connection.close();
            System.out.println("DBManager destroyed.");
        } catch (Exception e) {
            error(e);
        }
    }

    // QUERIES

    /**
     * Query if the user exists in the database.
     *
     * @param id        the employee ID to check
     * @return          true if the user exists, otherwise false.
     */
    public boolean userExists(long id) {
        try {
            PreparedStatement pstmt = SQL.USER_EXISTS.prepareStatement(connection);
                pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            error(e);
        }
        return false;
    }

    /**
     * Retrieves the user data from the database given a valid
     * pairing of user ID and PIN. Returns null, if invalid.
     *
     * @param id        the employee ID
     * @param pin       the PIN given by the user (to be validate)
     * @return          the user data or null if ID and PIN pair invalid.
     */
    public User getUser(long id, int pin) {
        try {
            PreparedStatement pstmt = SQL.GET_USER.prepareStatement(connection);
                pstmt.setLong(1, id);
                pstmt.setInt(2, pin);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(id,
                    rs.getString("fname"),
                    rs.getString("sname"),
                    rs.getString("email"),
                    rs.getDouble("fines"),
                    rs.getDate("lactive") == null);
            }
        } catch (Exception e) {
            error(e);
        }
        return null;
    }

    /**
     * Retrieves all vehicles owned by the given user
     * from the database. Returns a list of vehicles.
     *
     * @param user      the owner of the vehicles
     * @return          the list of vehicles.
     */
    public List<Vehicle> getVehiclesByUser(User user) {
        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        try {
            PreparedStatement pstmt = SQL.GET_VEHICLES_BY_USER.prepareStatement(connection);
                pstmt.setLong(1, user.getID());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                vehicles.add(new Vehicle(user,
                    rs.getString("plate"),
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("insurer"),
                    rs.getString("policy"),
                    rs.getDate("expiry"),
                    true
                ));
            }
        } catch (Exception e) {
            error(e);
        }
        return vehicles;
    }

    /**
     * Retrieves all permits issued to the given user
     * from the database. Returns a list of permits.
     *
     * @param user      for which permits are issued to
     * @return          the list of permits.
     */
    public List<Permit> getPermitsByUser(User user) {
        List<Permit> permits = new ArrayList<Permit>();
        try {
            PreparedStatement pstmt = SQL.GET_PERMITS_BY_USER.prepareStatement(connection);
                pstmt.setLong(1, user.getID());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                permits.add(new Permit(
                    new Vehicle(user,
                        rs.getString("plate"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getString("insurer"),
                        rs.getString("policy"),
                        rs.getDate("expiry"),
                        true
                    ),
                    rs.getDate("start"),
                    rs.getDate("end"),
                    rs.getDate("issued"),
                    true
                ));
            }
        } catch (Exception e) {
            error(e);
        }
        return permits;
    }

    /**
     * Return the expiry date of the latest permit issued
     * for the given vehicle.
     *
     * @param vehicle   the vehicle
     * @return          the expiry date of the latest permit
     */
    public Date getPermitExpiryByVehicle(Vehicle vehicle) {
        try {
            PreparedStatement pstmt = SQL.GET_PERMIT_EXPIRY_BY_VEHICLE.prepareStatement(connection);
                pstmt.setString(1, vehicle.getPlate());
            ResultSet rs = pstmt.executeQuery();
            return rs.getDate(1);
        } catch (Exception e) {
            error(e);
        }
        return null;
    }

    /**
     * Return the issued date of the given permit.
     *
     * @param vehicle   the permit
     * @return          the issued date
     */
    public Date getPermitIssuedDate(Permit permit) {
        try {
            PreparedStatement pstmt = SQL.GET_PERMITS_ISSUED_DATE.prepareStatement(connection);
                pstmt.setString(1, permit.getVehicle().getPlate());
                pstmt.setDate(2, permit.getStartDate());
            ResultSet rs = pstmt.executeQuery();
            return rs.getDate(1);
        } catch (Exception e) {
            error(e);
        }
        return null;
    }

    /**
     * Return the end date of the given permit.
     *
     * @param vehicle   the permit
     * @return          the end date
     */
    public Date getPermitEndDate(Permit permit) {
        try {
            PreparedStatement pstmt = SQL.GET_PERMITS_END_DATE.prepareStatement(connection);
                pstmt.setString(1, permit.getVehicle().getPlate());
                pstmt.setDate(2, permit.getStartDate());
            ResultSet rs = pstmt.executeQuery();
            return rs.getDate(1);
        } catch (Exception e) {
            error(e);
        }
        return null;
    }

    /**
     * Retrieves a list of all the insurers.
     *
     * @return a list of all the insurers.
     */
    public List<String> getInsurers() {
        List<String> insurers = new ArrayList<String>();
        try {
            PreparedStatement pstmt = SQL.GET_INSURERS.prepareStatement(connection);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                insurers.add(rs.getString(1));
            }
        } catch (Exception e) {
            error(e);
        }
        return insurers;
    }

    /**
     * Retrieves a list of all the automakers.
     *
     * @return a list of all the automakers.
     */
    public List<String> getAutoMakers() {
        List<String> automakers = new ArrayList<String>();
        try {
            PreparedStatement pstmt = SQL.GET_AUTOMAKERS.prepareStatement(connection);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                automakers.add(rs.getString(1));
            }
        } catch (Exception e) {
            error(e);
        }
        return automakers;
    }

    /**
     * Retrieves a list of the car models
     * for the given automaker.
     *
     * @param make      the car manufacturer
     * @return          a list of the car models for given automaker.
     */
    public List<MakeModel> getModelsByMake(String make) {
        List<MakeModel> models = new ArrayList<MakeModel>();
        try {
            PreparedStatement pstmt = SQL.GET_MODELS_BY_MAKE.prepareStatement(connection);
                pstmt.setString(1, make);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                models.add(new MakeModel(make, rs.getString(1)));
            }
        } catch (Exception e) {
            error(e);
        }
        return models;
    }

    /**
     * Retrieves a list of all the makes and models.
     *
     * @return a list of all the makes and models.
     */
    public List<MakeModel> getMakeModels() {
        List<MakeModel> automakers = new ArrayList<MakeModel>();
        try {
            PreparedStatement pstmt = SQL.GET_MAKE_MODELS.prepareStatement(connection);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                automakers.add(new MakeModel(rs.getString(1), rs.getString(2)));
            }
        } catch (Exception e) {
            error(e);
        }
        return automakers;
    }

    /**
     * Get the current Date.
     *
     * @return the current Date.
     */
    public Date currentDate() {
        try {
            PreparedStatement pstmt = SQL.GET_CURRENT_DATE.prepareStatement(connection);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDate(1);
            }
        } catch (Exception e) {
            error(e);
        }
        return null;
    }

    /**
     * Compute the expiry date of a permit given
     * a start date and the duration in number of days.
     *
     * @param startDate     the start date of the permit
     * @param days          the duration
     * @return              the expiry date of the permit
     */
    public Date computeExpiryDate(Date startDate, int days) {
        try {
            PreparedStatement pstmt = SQL.COMPUTE_EXPIRY_DATE.prepareStatement(connection);
                pstmt.setDate(1, startDate);
                pstmt.setInt(2, days);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDate(1);
            }
        } catch (Exception e) {
            error(e);
        }
        return null;
    }

    // INSERTS

    /**
     * Adds a newly created vehicle to the database.
     *
     * @param vehicle   the newly created vehicle
     * @return          true if the transaction was successful, otherwise false.
     */
    public boolean addVehicle(Vehicle vehicle) {
        try {
            PreparedStatement pstmt = SQL.ADD_VEHICLE.prepareStatement(connection);
                pstmt.setString(1, vehicle.getPlate());
                pstmt.setLong(2, vehicle.getOwner().getID());
                pstmt.setString(3, vehicle.getMake());
                pstmt.setString(4, vehicle.getModel());
                pstmt.setInt(5, vehicle.getModelYear());
                pstmt.setString(6, vehicle.getInsurer());
                pstmt.setString(7, vehicle.getPolicy());
                pstmt.setDate(8, vehicle.getExpiry());
                pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            error(e);
        }
        return false;
    }

    /**
     * Adds a newly created permit to the database.
     *
     * @param permit    the newly created permit
     * @return          true if the transaction was successful, otherwise false.
     */
    public boolean addPermit(Permit permit) {
        try {
            PreparedStatement pstmt = SQL.ADD_PERMIT.prepareStatement(connection);
                pstmt.setLong(1, permit.getVehicle().getOwner().getID());
                pstmt.setString(2, permit.getVehicle().getPlate());
                pstmt.setDate(3, permit.getStartDate());
                pstmt.setInt(4, permit.getDaysLeft());
                pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            error(e);
        }
        return false;
    }

    // UPATES

    /**
     * Update the given user's information.
     *
     * @param user      the user to update
     * @param op        code for what information to update.
     *
     *      NAME    first and last name
     *      FINES   outstanding fines
     *      EMAIL   email address
     *      LOGOUT  set the last active field to now
     *      PIN     set the user's PIN (args: oldPIN, newPIN)
     *
     * @args            extra arguments
     * @return          true if the transaction was successful, otherwise false.
     */
    public boolean updateUser(User user, String op, Object... args) {
        try {
            PreparedStatement pstmt = null;
            if (op.equals("NAME")) {
                pstmt = SQL.SET_USER_NAME.prepareStatement(connection);
                pstmt.setLong(3, user.getID());
                pstmt.setString(1, user.getFirstName());
                pstmt.setString(2, user.getSurName());
            } else if (op.equals("FINES")) {
                pstmt = SQL.SET_USER_FINES.prepareStatement(connection);
                pstmt.setLong(2, user.getID());
                pstmt.setDouble(1, user.getFines());
            } else if (op.equals("EMAIL")) {
                pstmt = SQL.SET_USER_EMAIL.prepareStatement(connection);
                pstmt.setLong(2, user.getID());
                pstmt.setString(1, user.getEmail());
            } else if (op.equals("LOGOUT")) {
                pstmt = SQL.SET_USER_LASTACTIVE.prepareStatement(connection);
                pstmt.setLong(1, user.getID());
            } else if (op.equals("PIN")) {
                if (args.length < 2) {
                    throw new IllegalArgumentException("Missing arguments: oldPIN, newPIN");
                }
                pstmt = SQL.SET_USER_PIN.prepareStatement(connection);
                pstmt.setLong(2, user.getID());
                pstmt.setInt(3, ((Integer)args[0]).intValue());
                pstmt.setInt(1, ((Integer)args[1]).intValue());
            }
            return pstmt.executeUpdate() == 1;
        } catch (Exception e) {
            error(e);
        }
        return false;
    }

    /**
     * Update the given vehicle's information.
     *
     * @param vehicle   the vehicle to update
     * @param op        code for what information to update.
     *
     *      PLATE       license plate (args: newPlate)
     *      INSURANCE   insurance information: insurer, policy, expiry date
     *
     * @args            extra arguments
     * @return          true if the transaction was successful, otherwise false.
     */
    public boolean updateVehicle(Vehicle vehicle, String op, Object... args) {
        try {
            PreparedStatement pstmt = null;
            if (op.equals("PLATE")) {
                if (args.length < 1) {
                    throw new IllegalArgumentException("Missing arguments: new");
                }
                pstmt = SQL.SET_VEHICLE_PLATE.prepareStatement(connection);
                pstmt.setString(2, vehicle.getPlate());
                pstmt.setString(1, args[0].toString());
            } else if (op.equals("INSURANCE")) {
                pstmt = SQL.SET_VEHICLE_INSURANCE.prepareStatement(connection);
                pstmt.setString(1, vehicle.getInsurer());
                pstmt.setString(2, vehicle.getPolicy());
                pstmt.setDate(3, vehicle.getExpiry());
                pstmt.setString(4, vehicle.getPlate());
            }
            return pstmt.executeUpdate() == 1;
        } catch (Exception e) {
            error(e);
        }
        return false;
    }

    // DELETE

    /**
     * Deletes the vehicle from the database, along with
     * all the permits associated with it (cascade).
     *
     * @param vehicle   the vehicle to delete
     * @return          true if the vehicle was deleted successfully,
     *                  otherwise false.
     */
    public boolean deleteVehicle(Vehicle vehicle) {
        try {
            PreparedStatement pstmt = SQL.DEL_VEHICLE.prepareStatement(connection);
            pstmt.setString(1, vehicle.getPlate());
            return pstmt.executeUpdate() == 1;
        } catch (Exception e) {
            error(e);
        }
        return false;
    }
}
