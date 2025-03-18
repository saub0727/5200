package car.genie.server.dal;

import car.genie.server.model.Regions;
import car.genie.server.model.States;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RegionsDao {
    protected ConnectionManager connectionManager;

    private static RegionsDao instance = null;

    protected RegionsDao() {
        connectionManager = new ConnectionManager();
    }

    public static RegionsDao getInstance() {
        if (instance == null) {
            instance = new RegionsDao();
        }
        return instance;
    }

    public Regions create(Regions region) throws SQLException {
        String insertRegion = "INSERT INTO Regions(RegionName, StateId) VALUES(?,?);";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertRegion, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, region.getRegionName());
            insertStmt.setInt(2, region.getStateId());
            insertStmt.executeUpdate();

            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    region.setRegionId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating region failed, no ID obtained.");
                }
            }

            return region;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Regions getRegionById(int regionId) throws SQLException {
        String selectRegion = "SELECT r.RegionId, r.RegionName, r.StateId, s.StateName " +
                "FROM Regions r " +
                "JOIN States s ON r.StateId = s.StateId " +
                "WHERE r.RegionId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectRegion)) {
            selectStmt.setInt(1, regionId);
            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    return parseRegion(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public List<Regions> getRegionsByStateId(int stateId) throws SQLException {
        List<Regions> regions = new ArrayList<>();
        String selectRegions = "SELECT r.RegionId, r.RegionName, r.StateId " +
                "FROM Regions r " +
                "WHERE r.StateId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectRegions)) {
            selectStmt.setInt(1, stateId);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    regions.add(parseRegion(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return regions;
    }

    public List<Regions> getAllRegions() throws SQLException {
        List<Regions> regions = new ArrayList<>();
        String selectRegions = "SELECT r.RegionId, r.RegionName, r.StateId " +
                "FROM Regions r;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectRegions)) {
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    regions.add(parseRegion(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return regions;
    }

    public Regions updateRegion(Regions region) throws SQLException {
        String updateRegion = "UPDATE Regions SET RegionName=?, StateId=? WHERE RegionId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateRegion)) {
            updateStmt.setString(1, region.getRegionName());
            updateStmt.setInt(2, region.getStateId());
            updateStmt.setInt(3, region.getRegionId());

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }

            return region;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Regions delete(Regions region) throws SQLException {
        String deleteRegion = "DELETE FROM Regions WHERE RegionId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteRegion)) {
            deleteStmt.setInt(1, region.getRegionId());

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

    private Regions parseRegion(ResultSet results) throws SQLException {
        return Regions.builder()
                .regionId(results.getInt("RegionId"))
                .regionName(results.getString("RegionName"))
                .stateId(results.getInt("StateId"))
                .build();
    }
}
