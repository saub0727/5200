package car.genie.server.dal;

import car.genie.server.model.VehicleClassification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleClassificationDao {
    protected ConnectionManager connectionManager;

    private static VehicleClassificationDao instance = null;

    protected VehicleClassificationDao() {
        connectionManager = new ConnectionManager();
    }

    public static VehicleClassificationDao getInstance() {
        if (instance == null) {
            instance = new VehicleClassificationDao();
        }
        return instance;
    }

    public VehicleClassification create(VehicleClassification vehicleClassification) throws SQLException {
        String insertVehicleClassification = "INSERT INTO VehicleClassification(VehicleId, Year, Size, Type, Color) VALUES(?,?,?,?,?);";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertVehicleClassification)) {
            insertStmt.setLong(1, vehicleClassification.getVehicleId());
            insertStmt.setInt(2, vehicleClassification.getYear());
            insertStmt.setString(3, vehicleClassification.getSize());
            insertStmt.setString(4, vehicleClassification.getType());
            insertStmt.setString(5, vehicleClassification.getColor());
            insertStmt.executeUpdate();
            
            return vehicleClassification;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public VehicleClassification getVehicleClassificationByVehicleId(long vehicleId) throws SQLException {
        String selectVehicleClassification = "SELECT VehicleId, Year, Size, Type, Color FROM VehicleClassification WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleClassification)) {
            selectStmt.setLong(1, vehicleId);
            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    return parseVehicleClassification(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public List<VehicleClassification> getAllVehicleClassifications() throws SQLException {
        List<VehicleClassification> vehicleClassifications = new ArrayList<>();
        String selectVehicleClassifications = "SELECT VehicleId, Year, Size, Type, Color FROM VehicleClassification;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleClassifications);
             ResultSet results = selectStmt.executeQuery()) {
            while (results.next()) {
                vehicleClassifications.add(parseVehicleClassification(results));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return vehicleClassifications;
    }

    public List<VehicleClassification> getVehicleClassificationsByYear(int year) throws SQLException {
        List<VehicleClassification> vehicleClassifications = new ArrayList<>();
        String selectVehicleClassifications = "SELECT VehicleId, Year, Size, Type, Color FROM VehicleClassification WHERE Year=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleClassifications)) {
            selectStmt.setInt(1, year);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    vehicleClassifications.add(parseVehicleClassification(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return vehicleClassifications;
    }

    public List<VehicleClassification> getVehicleClassificationsBySize(String size) throws SQLException {
        List<VehicleClassification> vehicleClassifications = new ArrayList<>();
        String selectVehicleClassifications = "SELECT VehicleId, Year, Size, Type, Color FROM VehicleClassification WHERE Size=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleClassifications)) {
            selectStmt.setString(1, size);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    vehicleClassifications.add(parseVehicleClassification(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return vehicleClassifications;
    }

    public List<VehicleClassification> getVehicleClassificationsByType(String type) throws SQLException {
        List<VehicleClassification> vehicleClassifications = new ArrayList<>();
        String selectVehicleClassifications = "SELECT VehicleId, Year, Size, Type, Color FROM VehicleClassification WHERE Type=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleClassifications)) {
            selectStmt.setString(1, type);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    vehicleClassifications.add(parseVehicleClassification(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return vehicleClassifications;
    }

    public List<VehicleClassification> getVehicleClassificationsByColor(String color) throws SQLException {
        List<VehicleClassification> vehicleClassifications = new ArrayList<>();
        String selectVehicleClassifications = "SELECT VehicleId, Year, Size, Type, Color FROM VehicleClassification WHERE Color=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleClassifications)) {
            selectStmt.setString(1, color);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    vehicleClassifications.add(parseVehicleClassification(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return vehicleClassifications;
    }

    public VehicleClassification updateVehicleClassification(VehicleClassification vehicleClassification) throws SQLException {
        String updateVehicleClassification = "UPDATE VehicleClassification SET Year=?, Size=?, Type=?, Color=? WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateVehicleClassification)) {
            updateStmt.setInt(1, vehicleClassification.getYear());
            updateStmt.setString(2, vehicleClassification.getSize());
            updateStmt.setString(3, vehicleClassification.getType());
            updateStmt.setString(4, vehicleClassification.getColor());
            updateStmt.setLong(5, vehicleClassification.getVehicleId());
            
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }
            
            return vehicleClassification;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public VehicleClassification delete(VehicleClassification vehicleClassification) throws SQLException {
        String deleteVehicleClassification = "DELETE FROM VehicleClassification WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteVehicleClassification)) {
            deleteStmt.setLong(1, vehicleClassification.getVehicleId());
            
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

    private VehicleClassification parseVehicleClassification(ResultSet results) throws SQLException {
        return VehicleClassification.builder()
                .vehicleId(results.getLong("VehicleId"))
                .year(results.getInt("Year"))
                .size(results.getString("Size"))
                .type(results.getString("Type"))
                .color(results.getString("Color"))
                .build();
    }
}
