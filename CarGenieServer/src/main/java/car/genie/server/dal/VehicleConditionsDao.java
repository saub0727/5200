package car.genie.server.dal;

import car.genie.server.model.VehicleConditions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleConditionsDao {
    protected ConnectionManager connectionManager;

    private static VehicleConditionsDao instance = null;

    protected VehicleConditionsDao() {
        connectionManager = new ConnectionManager();
    }

    public static VehicleConditionsDao getInstance() {
        if (instance == null) {
            instance = new VehicleConditionsDao();
        }
        return instance;
    }

    public VehicleConditions create(VehicleConditions vehicleConditions) throws SQLException {
        String insertVehicleCondition = "INSERT INTO VehicleConditions(VehicleId, Odometer, VehicleCondition, TitleStatus) VALUES(?,?,?,?);";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertVehicleCondition)) {
            insertStmt.setLong(1, vehicleConditions.getVehicleId());
            insertStmt.setInt(2, vehicleConditions.getOdometer());
            insertStmt.setString(3, vehicleConditions.getVehicleCondition());
            insertStmt.setString(4, vehicleConditions.getTitleStatus());
            insertStmt.executeUpdate();
            
            return vehicleConditions;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public VehicleConditions getVehicleConditionsByVehicleId(long vehicleId) throws SQLException {
        String selectVehicleCondition = "SELECT VehicleId, Odometer, VehicleCondition, TitleStatus FROM VehicleConditions WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleCondition)) {
            selectStmt.setLong(1, vehicleId);
            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    return parseVehicleCondition(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public List<VehicleConditions> getAllVehicleConditions() throws SQLException {
        List<VehicleConditions> vehicleConditions = new ArrayList<>();
        String selectVehicleConditions = "SELECT VehicleId, Odometer, VehicleCondition, TitleStatus FROM VehicleConditions;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleConditions);
             ResultSet results = selectStmt.executeQuery()) {
            while (results.next()) {
                vehicleConditions.add(parseVehicleCondition(results));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return vehicleConditions;
    }

    public List<VehicleConditions> getVehicleConditionsByCondition(String condition) throws SQLException {
        List<VehicleConditions> vehicleConditions = new ArrayList<>();
        String selectVehicleConditions = "SELECT VehicleId, Odometer, VehicleCondition, TitleStatus FROM VehicleConditions WHERE VehicleCondition=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleConditions)) {
            selectStmt.setString(1, condition);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    vehicleConditions.add(parseVehicleCondition(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return vehicleConditions;
    }

    public List<VehicleConditions> getVehicleConditionsByTitleStatus(String titleStatus) throws SQLException {
        List<VehicleConditions> vehicleConditions = new ArrayList<>();
        String selectVehicleConditions = "SELECT VehicleId, Odometer, VehicleCondition, TitleStatus FROM VehicleConditions WHERE TitleStatus=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleConditions)) {
            selectStmt.setString(1, titleStatus);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    vehicleConditions.add(parseVehicleCondition(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return vehicleConditions;
    }

    public VehicleConditions updateVehicleConditions(VehicleConditions vehicleConditions) throws SQLException {
        String updateVehicleCondition = "UPDATE VehicleConditions SET Odometer=?, VehicleCondition=?, TitleStatus=? WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateVehicleCondition)) {
            updateStmt.setInt(1, vehicleConditions.getOdometer());
            updateStmt.setString(2, vehicleConditions.getVehicleCondition());
            updateStmt.setString(3, vehicleConditions.getTitleStatus());
            updateStmt.setLong(4, vehicleConditions.getVehicleId());
            
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }
            
            return vehicleConditions;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public VehicleConditions delete(VehicleConditions vehicleConditions) throws SQLException {
        String deleteVehicleCondition = "DELETE FROM VehicleConditions WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteVehicleCondition)) {
            deleteStmt.setLong(1, vehicleConditions.getVehicleId());
            
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

    private VehicleConditions parseVehicleCondition(ResultSet results) throws SQLException {
        return VehicleConditions.builder()
                .vehicleId(results.getLong("VehicleId"))
                .odometer(results.getInt("Odometer"))
                .vehicleCondition(results.getString("VehicleCondition"))
                .titleStatus(results.getString("TitleStatus"))
                .build();
    }
}
