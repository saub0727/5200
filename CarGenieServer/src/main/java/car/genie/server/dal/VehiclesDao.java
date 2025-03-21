package car.genie.server.dal;

import car.genie.server.model.Models;
import car.genie.server.model.Vehicles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

public class VehiclesDao {
    protected ConnectionManager connectionManager;

    private static VehiclesDao instance = null;

    protected VehiclesDao() {
        connectionManager = new ConnectionManager();
    }

    public static VehiclesDao getInstance() {
        if (instance == null) {
            instance = new VehiclesDao();
        }
        return instance;
    }

    /**
     * Create a new vehicle in the Vehicles table.
     */
    public Vehicles create(Vehicles vehicle) throws SQLException {
        String insertVehicle = "INSERT INTO Vehicles(VehicleId, Vin, Price, PostingDate, Description, ModelId) " +
                "VALUES(?,?,?,?,?,?);";

        System.out.println("Executing query: " + insertVehicle);
        System.out.println("VehicleId: " + vehicle.getVehicleId());
        System.out.println("VIN: " + vehicle.getVin());
        System.out.println("Price: " + vehicle.getPrice());
        System.out.println("PostingDate: " + vehicle.getPostingDate());
        System.out.println("Description: " + vehicle.getDescription());
        System.out.println("ModelId: " + vehicle.getModelId());

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertVehicle)) {

            insertStmt.setLong(1, vehicle.getVehicleId());
            insertStmt.setString(2, vehicle.getVin());
            insertStmt.setInt(3, vehicle.getPrice());
            insertStmt.setDate(4, Date.valueOf(vehicle.getPostingDate()));
            insertStmt.setString(5, vehicle.getDescription());
            if (vehicle.getModelId() != null) {
                insertStmt.setInt(6, vehicle.getModelId());
            } else {
                insertStmt.setNull(6, java.sql.Types.INTEGER);
            }

            int rowsAffected = insertStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }

            System.out.println("Vehicle created successfully!");
            return vehicle;

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }



    /**
     * Get a vehicle by its ID, including its model information.
     */
    public Vehicles getVehicleById(long vehicleId) throws SQLException {
        String selectVehicle = "SELECT VehicleId, Vin, Price, PostingDate, Description, ModelId " +
                "FROM Vehicles WHERE VehicleId=?;";

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement selectStmt = connection.prepareStatement(selectVehicle)) {

            selectStmt.setLong(1, vehicleId);
            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    return parseVehicle(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    /**
     * Get a vehicle by its VIN, including its model information.
     */
    public Vehicles getVehicleByVin(String vin) throws SQLException {
        String selectVehicle = "SELECT VehicleId, Vin, Price, PostingDate, Description, ModelId " +
                "FROM Vehicles WHERE Vin=?;";

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement selectStmt = connection.prepareStatement(selectVehicle)) {

            selectStmt.setString(1, vin);
            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    return parseVehicle(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    /**
     * Get all vehicles for a specific model ID.
     */
    public List<Vehicles> getVehiclesByModelId(int modelId) throws SQLException {
        List<Vehicles> vehicles = new ArrayList<>();
        String selectVehicles = "SELECT v.VehicleId, v.Vin, v.Price, v.PostingDate, v.Description, v.ModelId " +
                "FROM Vehicles v " +
                "WHERE v.ModelId=?;";

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement selectStmt = connection.prepareStatement(selectVehicles)) {

            selectStmt.setInt(1, modelId);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    Vehicles vehicle = parseVehicle(results);
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return vehicles;
    }

    /**
     * Update a vehicle's information.
     */
    public Vehicles updateVehicle(Vehicles vehicle) throws SQLException {
        String updateVehicle = "UPDATE Vehicles SET Vin=?, Price=?, PostingDate=?, Description=?, ModelId=? " +
                "WHERE VehicleId=?;";

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement updateStmt = connection.prepareStatement(updateVehicle)) {

            updateStmt.setString(1, vehicle.getVin());
            updateStmt.setInt(2, vehicle.getPrice());
            updateStmt.setDate(3, Date.valueOf(vehicle.getPostingDate()));
            updateStmt.setString(4, vehicle.getDescription());
            if (vehicle.getModelId() != null) {
                updateStmt.setInt(5, vehicle.getModelId());
            } else {
                updateStmt.setNull(5, java.sql.Types.INTEGER);
            }
            updateStmt.setLong(6, vehicle.getVehicleId());

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }
            return vehicle;

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Delete a vehicle by its ID.
     */
    public Vehicles delete(Vehicles vehicle) throws SQLException {
        String deleteVehicle = "DELETE FROM Vehicles WHERE VehicleId=?;";

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement deleteStmt = connection.prepareStatement(deleteVehicle)) {

            deleteStmt.setLong(1, vehicle.getVehicleId());
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Delete failed, no rows affected.");
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Helper method to parse a vehicle from a ResultSet with JOIN results
     */
    private Vehicles parseVehicle(ResultSet results) throws SQLException {
        return Vehicles.builder()
                .vehicleId(results.getLong("VehicleId"))
                .vin(results.getString("Vin"))
                .price(results.getInt("Price"))
                .postingDate(results.getDate("PostingDate").toLocalDate())
                .description(results.getString("Description"))
                .modelId(results.getInt("ModelId"))
                .build();
    }

    /**
     * Helper method to parse a vehicle when we already have the model ID
     */
    private Vehicles parseVehicleWithKnownModelId(ResultSet results, int modelId) throws SQLException {
        return Vehicles.builder()
                .vehicleId(results.getLong("VehicleId"))
                .vin(results.getString("Vin"))
                .price(results.getInt("Price"))
                .postingDate(results.getDate("PostingDate").toLocalDate())
                .description(results.getString("Description"))
                .modelId(modelId)
                .build();
    }

}