package car.genie.server.dal;

import car.genie.server.model.States;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StatesDao {
    protected ConnectionManager connectionManager;

    private static StatesDao instance = null;

    protected StatesDao() {
        connectionManager = new ConnectionManager();
    }

    public static StatesDao getInstance() {
        if (instance == null) {
            instance = new StatesDao();
        }
        return instance;
    }

    public States create(States state) throws SQLException {
        String insertState = "INSERT INTO States(StateName) VALUES(?);";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertState, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, state.getStateName());
            insertStmt.executeUpdate();

            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    state.setStateId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating state failed, no ID obtained.");
                }
            }
            
            return state;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public States getStateById(int stateId) throws SQLException {
        String selectState = "SELECT StateId, StateName FROM States WHERE StateId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectState)) {
            selectStmt.setInt(1, stateId);
            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    return parseState(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public States getStateByName(String stateName) throws SQLException {
        String selectState = "SELECT StateId, StateName FROM States WHERE StateName=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectState)) {
            selectStmt.setString(1, stateName);
            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    return parseState(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public List<States> getAllStates() throws SQLException {
        List<States> states = new ArrayList<>();
        String selectStates = "SELECT StateId, StateName FROM States;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectStates)) {
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    states.add(parseState(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return states;
    }

    public States updateState(States state) throws SQLException {
        String updateState = "UPDATE States SET StateName=? WHERE StateId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateState)) {
            updateStmt.setString(1, state.getStateName());
            updateStmt.setInt(2, state.getStateId());
            
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }
            
            return state;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public States delete(States state) throws SQLException {
        String deleteState = "DELETE FROM States WHERE StateId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteState)) {
            deleteStmt.setInt(1, state.getStateId());
            
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

    private States parseState(ResultSet results) throws SQLException {
        return States.builder()
                .stateId(results.getInt("StateId"))
                .stateName(results.getString("StateName"))
                .build();
    }
}
