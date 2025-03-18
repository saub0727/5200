package car.genie.server.dal;

import car.genie.server.model.Images;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ImagesDao {
    protected ConnectionManager connectionManager;

    private static ImagesDao instance = null;

    protected ImagesDao() {
        connectionManager = new ConnectionManager();
    }

    public static ImagesDao getInstance() {
        if (instance == null) {
            instance = new ImagesDao();
        }
        return instance;
    }

    public Images create(Images image) throws SQLException {
        String insertImage = "INSERT INTO Images(VehicleId, ImageURL) VALUES(?,?);";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertImage, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setLong(1, image.getVehicleId());
            insertStmt.setString(2, image.getImageURL());
            insertStmt.executeUpdate();
            
            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    image.setImageId(generatedKeys.getInt(1));
                    return image;
                } else {
                    throw new SQLException("Creating image failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Images getImageById(int imageId) throws SQLException {
        String selectImage = "SELECT ImageId, VehicleId, ImageURL FROM Images WHERE ImageId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectImage)) {
            selectStmt.setInt(1, imageId);
            try (ResultSet results = selectStmt.executeQuery()) {
                if (results.next()) {
                    return parseImage(results);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public List<Images> getImagesByVehicleId(long vehicleId) throws SQLException {
        List<Images> images = new ArrayList<>();
        String selectImages = "SELECT ImageId, VehicleId, ImageURL FROM Images WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectImages)) {
            selectStmt.setLong(1, vehicleId);
            try (ResultSet results = selectStmt.executeQuery()) {
                while (results.next()) {
                    images.add(parseImage(results));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return images;
    }

    public List<Images> getAllImages() throws SQLException {
        List<Images> images = new ArrayList<>();
        String selectImages = "SELECT ImageId, VehicleId, ImageURL FROM Images;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectImages);
             ResultSet results = selectStmt.executeQuery()) {
            while (results.next()) {
                images.add(parseImage(results));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return images;
    }

    public Images updateImage(Images image) throws SQLException {
        String updateImage = "UPDATE Images SET VehicleId=?, ImageURL=? WHERE ImageId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateImage)) {
            updateStmt.setLong(1, image.getVehicleId());
            updateStmt.setString(2, image.getImageURL());
            updateStmt.setInt(3, image.getImageId());
            
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected.");
            }
            
            return image;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Images delete(Images image) throws SQLException {
        String deleteImage = "DELETE FROM Images WHERE ImageId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteImage)) {
            deleteStmt.setInt(1, image.getImageId());
            
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

    public List<Images> deleteImagesByVehicleId(long vehicleId) throws SQLException {
        String deleteImages = "DELETE FROM Images WHERE VehicleId=?;";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteImages)) {
            deleteStmt.setLong(1, vehicleId);
            
            deleteStmt.executeUpdate();
            
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Images parseImage(ResultSet results) throws SQLException {
        return Images.builder()
                .imageId(results.getInt("ImageId"))
                .vehicleId(results.getLong("VehicleId"))
                .imageURL(results.getString("ImageURL"))
                .build();
    }
}
