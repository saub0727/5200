package car.genie.server.dal;

import car.genie.server.model.Locations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationsDao {
    protected ConnectionManager connectionManager;

    private static LocationsDao instance = null;

    protected LocationsDao() {
        connectionManager = new ConnectionManager();
    }

    public static LocationsDao getInstance() {
        if (instance == null) {
            instance = new LocationsDao();
        }
        return instance;
    }

    public Locations create(Locations location) throws SQLException {
        String insertLocation = "INSERT INTO Locations(VehicleId, Latitude, Longitude, RegionId) VALUES(?,?,?,?);";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertLocation)) {
            insertStmt.setLong(1, location.getVehicleId());
            insertStmt.setFloat(2, location.getLatitude());
            insertStmt.setFloat(3, location.getLongitude());
            insertStmt.setInt(4, location.getRegionId());
            insertStmt.executeUpdate();
            
            return location;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Locations getLocationByVehicleId(long vehicleId) throws SQLException {
        String selectLocation = "SELECT VehicleId, Latitude, Longitude, RegionId FROM Locations WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectLocation)) {
            selectStmt.setLong(1, vehicleId);
            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    return parseLocation(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public List<Locations> getLocationsByRegionId(int regionId) throws SQLException {
        List<Locations> locations = new ArrayList<>();
        String selectLocations = "SELECT VehicleId, Latitude, Longitude, RegionId FROM Locations WHERE RegionId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectLocations)) {
            selectStmt.setInt(1, regionId);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    locations.add(parseLocation(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return locations;
    }

    public List<Locations> getAllLocations() throws SQLException {
        List<Locations> locations = new ArrayList<>();
        String selectLocations = "SELECT VehicleId, Latitude, Longitude, RegionId FROM Locations;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectLocations);
             ResultSet results = selectStmt.executeQuery()) {
            while (results.next()) {
                locations.add(parseLocation(results));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return locations;
    }

    public Locations updateLocation(Locations location) throws SQLException {
        String updateLocation = "UPDATE Locations SET Latitude=?, Longitude=?, RegionId=? WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateLocation)) {
            updateStmt.setFloat(1, location.getLatitude());
            updateStmt.setFloat(2, location.getLongitude());
            updateStmt.setInt(3, location.getRegionId());
            updateStmt.setLong(4, location.getVehicleId());
            
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }
            
            return location;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Locations delete(Locations location) throws SQLException {
        String deleteLocation = "DELETE FROM Locations WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteLocation)) {
            deleteStmt.setLong(1, location.getVehicleId());
            
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

    private Locations parseLocation(ResultSet results) throws SQLException {
        return Locations.builder()
                .vehicleId(results.getLong("VehicleId"))
                .latitude(results.getFloat("Latitude"))
                .longitude(results.getFloat("Longitude"))
                .regionId(results.getInt("RegionId"))
                .build();
    }
}
