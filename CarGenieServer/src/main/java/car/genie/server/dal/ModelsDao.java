package car.genie.server.dal;

import car.genie.server.model.Models;
import car.genie.server.model.Manufacturers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class ModelsDao {
    protected ConnectionManager connectionManager;
    private static ModelsDao instance = null;

    protected ModelsDao() {
        connectionManager = new ConnectionManager();
    }

    public static ModelsDao getInstance() {
        if (instance == null) {
            instance = new ModelsDao();
        }
        return instance;
    }

    /**
     * Create a new model in the Models table.
     */
    public Models create(Models model) throws SQLException {
        String insertModel = "INSERT INTO Models(ModelName, ManufacturerId) VALUES(?,?);";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertModel, RETURN_GENERATED_KEYS)) {

            insertStmt.setString(1, model.getModelName());
            if (model.getManufacturer() != null) {
                insertStmt.setInt(2, model.getManufacturer().getManufacturerId());
            } else {
                insertStmt.setNull(2, java.sql.Types.INTEGER);
            }

            insertStmt.executeUpdate();

            try (ResultSet resultKey = insertStmt.getGeneratedKeys()) {
                if (resultKey.next()) {
                    int modelId = resultKey.getInt(1);
                    model.setModelId(modelId);
                    return model;
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
     * Get a model by its ModelId, including its manufacturer information.
     */
    public Models getModelByModelId(int modelId) throws SQLException {
        String selectModel = "SELECT m.ModelId, m.ModelName, m.ManufacturerId, " +
                "mf.ManufacturerName " +
                "FROM Models m " +
                "LEFT JOIN Manufacturers mf ON m.ManufacturerId = mf.ManufacturerId " +
                "WHERE m.ModelId=?;";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectModel)) {

            selectStmt.setInt(1, modelId);
            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    String modelName = results.getString("ModelName");
                    int manufacturerId = results.getInt("ManufacturerId");
                    String manufacturerName = results.getString("ManufacturerName");
                    Manufacturers manufacturer = new Manufacturers(manufacturerId, manufacturerName);
                    return new Models(modelId, modelName, manufacturer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    /**
     * Get all models for a specific manufacturer.
     */
    public List<Models> getModelsByManufacturer(Manufacturers manufacturer) throws SQLException {
        List<Models> models = new ArrayList<>();
        String selectModels = "SELECT ModelId, ModelName " +
                "FROM Models " +
                "WHERE ManufacturerId=?;";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectModels)) {

            selectStmt.setInt(1, manufacturer.getManufacturerId());
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    int modelId = results.getInt("ModelId");
                    String modelName = results.getString("ModelName");

                    Models model = new Models(modelId, modelName, manufacturer);
                    models.add(model);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return models;
    }

    /**
     * Update a model's name and manufacturer.
     */
    public Models updateModel(Models model) throws SQLException {
        String updateModel = "UPDATE Models SET ModelName=?, ManufacturerId=? WHERE ModelId=?;";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateModel)) {

            updateStmt.setString(1, model.getModelName());
            if (model.getManufacturer() != null) {
                updateStmt.setInt(2, model.getManufacturer().getManufacturerId());
            } else {
                updateStmt.setNull(2, java.sql.Types.INTEGER);
            }
            updateStmt.setInt(3, model.getModelId());

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }

            return model;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Delete a model by its ID.
     */
    public Models delete(Models model) throws SQLException {
        String deleteModel = "DELETE FROM Models WHERE ModelId=?;";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteModel)) {

            deleteStmt.setInt(1, model.getModelId());
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Delete failed, no rows affected.");
            }

            // Return null to indicate the model no longer exists
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}