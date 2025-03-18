package car.genie.server.dal;

import car.genie.server.model.VehicleSpecs;
import car.genie.server.model.Vehicles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleSpecsDao {
    protected ConnectionManager connectionManager;

    private static VehicleSpecsDao instance = null;

    protected VehicleSpecsDao() {
        connectionManager = new ConnectionManager();
    }

    public static VehicleSpecsDao getInstance() {
        if (instance == null) {
            instance = new VehicleSpecsDao();
        }
        return instance;
    }

    public VehicleSpecs create(VehicleSpecs vehicleSpecs) throws SQLException {
        String insertVehicleSpecs = "INSERT INTO VehicleSpecs(VehicleId, Cylinders, Fuel, Transmission, Drive) " +
                "VALUES(?,?,?,?,?);";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertVehicleSpecs)) {
            insertStmt.setLong(1, vehicleSpecs.getVehicleId());
            insertStmt.setString(2, vehicleSpecs.getCylinders());
            insertStmt.setString(3, vehicleSpecs.getFuel());
            insertStmt.setString(4, vehicleSpecs.getTransmission());
            insertStmt.setString(5, vehicleSpecs.getDrive());
            insertStmt.executeUpdate();
            
            return vehicleSpecs;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public VehicleSpecs getVehicleSpecsByVehicleId(long vehicleId) throws SQLException {
        String selectVehicleSpecs = "SELECT vs.VehicleId, vs.Cylinders, vs.Fuel, vs.Transmission, vs.Drive " +
                "FROM VehicleSpecs vs " +
                "WHERE vs.VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleSpecs)) {
            selectStmt.setLong(1, vehicleId);
            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    return parseVehicleSpecs(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public List<VehicleSpecs> getVehicleSpecsByFuel(String fuel) throws SQLException {
        List<VehicleSpecs> vehicleSpecsList = new ArrayList<>();
        String selectVehicleSpecs = "SELECT vs.VehicleId, vs.Cylinders, vs.Fuel, vs.Transmission, vs.Drive " +
                "FROM VehicleSpecs vs " +
                "WHERE vs.Fuel=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleSpecs)) {
            selectStmt.setString(1, fuel);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    vehicleSpecsList.add(parseVehicleSpecs(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return vehicleSpecsList;
    }

    public List<VehicleSpecs> getVehicleSpecsByTransmission(String transmission) throws SQLException {
        List<VehicleSpecs> vehicleSpecsList = new ArrayList<>();
        String selectVehicleSpecs = "SELECT vs.VehicleId, vs.Cylinders, vs.Fuel, vs.Transmission, vs.Drive " +
                "FROM VehicleSpecs vs " +
                "WHERE vs.Transmission=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleSpecs)) {
            selectStmt.setString(1, transmission);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    vehicleSpecsList.add(parseVehicleSpecs(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return vehicleSpecsList;
    }

    public List<VehicleSpecs> getVehicleSpecsByDrive(String drive) throws SQLException {
        List<VehicleSpecs> vehicleSpecsList = new ArrayList<>();
        String selectVehicleSpecs = "SELECT vs.VehicleId, vs.Cylinders, vs.Fuel, vs.Transmission, vs.Drive " +
                "FROM VehicleSpecs vs " +
                "WHERE vs.Drive=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectVehicleSpecs)) {
            selectStmt.setString(1, drive);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    vehicleSpecsList.add(parseVehicleSpecs(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return vehicleSpecsList;
    }

    public VehicleSpecs updateVehicleSpecs(VehicleSpecs vehicleSpecs) throws SQLException {
        String updateVehicleSpecs = "UPDATE VehicleSpecs SET Cylinders=?, Fuel=?, Transmission=?, Drive=? " +
                "WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateVehicleSpecs)) {
            updateStmt.setString(1, vehicleSpecs.getCylinders());
            updateStmt.setString(2, vehicleSpecs.getFuel());
            updateStmt.setString(3, vehicleSpecs.getTransmission());
            updateStmt.setString(4, vehicleSpecs.getDrive());
            updateStmt.setLong(5, vehicleSpecs.getVehicleId());
            
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }
            
            return vehicleSpecs;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public VehicleSpecs delete(VehicleSpecs vehicleSpecs) throws SQLException {
        String deleteVehicleSpecs = "DELETE FROM VehicleSpecs WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteVehicleSpecs)) {
            deleteStmt.setLong(1, vehicleSpecs.getVehicleId());
            
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

    private VehicleSpecs parseVehicleSpecs(ResultSet results) throws SQLException {
        return VehicleSpecs.builder()
                .vehicleId(results.getLong("VehicleId"))
                .cylinders(results.getString("Cylinders"))
                .fuel(results.getString("Fuel"))
                .transmission(results.getString("Transmission"))
                .drive(results.getString("Drive"))
                .build();
    }
}
