package car.genie.server.dal;

import car.genie.server.model.Manufacturers;
import car.genie.server.model.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class ManufacturersDao {
    protected ConnectionManager connectionManager;
    private static ManufacturersDao instance = null;

    protected ManufacturersDao() {
        connectionManager = new ConnectionManager();
    }

    public static ManufacturersDao getInstance() {
        if (instance == null) {
            instance = new ManufacturersDao();
        }
        return instance;
    }

    /**
     * Create a new manufacturer in the Manufacturers table.
     */
    public Manufacturers create(Manufacturers manufacturer) throws SQLException {
        String insertManufacturer = "INSERT INTO Manufacturers(ManufacturerName) VALUES(?);";

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement insertStmt = connection.prepareStatement(insertManufacturer, RETURN_GENERATED_KEYS)) {

            insertStmt.setString(1, manufacturer.getManufacturerName());
            insertStmt.executeUpdate();

            try (ResultSet resultKey = insertStmt.getGeneratedKeys()) {
                if (resultKey.next()) {
                    int manufacturerId = resultKey.getInt(1);
                    manufacturer.setManufacturerId(manufacturerId);
                    return manufacturer;
                } else {
                    throw new SQLException("Unable to retrieve auto-generated key.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get a manufacturer by its ID.
     */
    public Manufacturers getManufacturerByManufacturerId(int manufacturerId) throws SQLException {
        String selectManufacturer = "SELECT ManufacturerId, ManufacturerName FROM Manufacturers WHERE ManufacturerId=?;";

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement selectStmt = connection.prepareStatement(selectManufacturer)) {
            selectStmt.setInt(1, manufacturerId);

            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    return parseManufacturer(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    /**
     * Get a manufacturer by its name.
     */
    public Manufacturers getManufacturerByName(String manufacturerName) throws SQLException {
        String selectManufacturer = "SELECT ManufacturerId, ManufacturerName FROM Manufacturers WHERE ManufacturerName=?;";

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement selectStmt = connection.prepareStatement(selectManufacturer)) {
            selectStmt.setString(1, manufacturerName);

            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    return parseManufacturer(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    /**
     * Get manufacturers with a specific name pattern.
     */
    public List<Manufacturers> getManufacturersByNameLike(String namePattern) throws SQLException {
        List<Manufacturers> manufacturers = new ArrayList<>();
        String selectManufacturers = "SELECT ManufacturerId, ManufacturerName FROM Manufacturers WHERE ManufacturerName LIKE ? ORDER BY ManufacturerName;";

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement selectStmt = connection.prepareStatement(selectManufacturers)) {
            selectStmt.setString(1, "%" + namePattern + "%");

            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    manufacturers.add(parseManufacturer(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return manufacturers;
    }

    /**
     * Update a manufacturer's name.
     */
    public Manufacturers updateManufacturer(Manufacturers manufacturer) throws SQLException {
        String updateManufacturer = "UPDATE Manufacturers SET ManufacturerName=? WHERE ManufacturerId=?;";

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement updateStmt = connection.prepareStatement(updateManufacturer)) {

            updateStmt.setString(1, manufacturer.getManufacturerName());
            updateStmt.setInt(2, manufacturer.getManufacturerId());

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }
            return manufacturer;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Delete a manufacturer by its ID.
     */
    public Manufacturers delete(Manufacturers manufacturer) throws SQLException {
        String deleteManufacturer = "DELETE FROM Manufacturers WHERE ManufacturerId=?;";

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement deleteStmt = connection.prepareStatement(deleteManufacturer)) {
            deleteStmt.setInt(1, manufacturer.getManufacturerId());
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Delete failed, no rows affected.");
            }

            // Return null to indicate the manufacturer no longer exists
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get all manufacturers.
     */
    public List<Manufacturers> getAllManufacturers() throws SQLException {
        List<Manufacturers> manufacturers = new ArrayList<>();
        String selectManufacturers = "SELECT ManufacturerId, ManufacturerName FROM Manufacturers ORDER BY ManufacturerName;";

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement selectStmt = connection.prepareStatement(selectManufacturers);
                ResultSet results = selectStmt.executeQuery()) {

            while (results.next()) {
                manufacturers.add(parseManufacturer(results));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return manufacturers;
    }

    /**
     * Helper method to parse a manufacturer from a ResultSet
     */
    private Manufacturers parseManufacturer(ResultSet results) throws SQLException {
        return Manufacturers.builder()
                .manufacturerId(results.getInt("ManufacturerId"))
                .manufacturerName(results.getString("ManufacturerName"))
                .build();
    }
}