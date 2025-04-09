package car.genie.server.dal;

import car.genie.server.model.Models;
import car.genie.server.model.VehicleClassification;
import car.genie.server.model.VehicleConditions;
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

    public List<Vehicles> getRecommendedVehicles(String vin) throws SQLException {
        List<Vehicles> finalRecommendations = new ArrayList<>();

        Vehicles baseVehicle = getVehicleByVin(vin);
        if (baseVehicle == null) {
            throw new SQLException("No vehicle found for given VIN: " + vin);
        }

        VehicleConditionsDao conditionsDao = VehicleConditionsDao.getInstance();
        VehicleConditions baseCondition = conditionsDao.getVehicleConditionsByVehicleId(baseVehicle.getVehicleId());
        if (baseCondition == null) {
            throw new SQLException("No vehicle conditions found for VIN: " + vin);
        }
        VehicleClassificationDao classificationDao = VehicleClassificationDao.getInstance();
        VehicleClassification baseClassification = classificationDao.getVehicleClassificationByVehicleId(baseVehicle.getVehicleId());
        if (baseClassification == null) {
            throw new SQLException("No vehicle classification found for VIN: " + vin);
        }

        int basePrice = baseVehicle.getPrice();
        int baseYear = baseClassification.getYear();
        int baseOdometer = baseCondition.getOdometer();
        int baseModelId = baseVehicle.getModelId();
        Models baseModel = null;
        try {
            baseModel = ModelsDao.getInstance().getModelByModelId(baseModelId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int baseManufacturerId = (baseModel != null) ? baseModel.getManufacturerId() : -1;

        String sql = "SELECT v.VehicleId, v.Vin, v.Price, v.PostingDate, v.Description, v.ModelId, " +
                "vc.Odometer, vc.VehicleCondition, vc.TitleStatus, " +
                "vc2.Year, m.ManufacturerId " +
                "FROM Vehicles v " +
                "JOIN VehicleConditions vc ON v.VehicleId = vc.VehicleId " +
                "JOIN VehicleClassification vc2 ON v.VehicleId = vc2.VehicleId " +
                "JOIN Models m ON v.ModelId = m.ModelId " +
                "WHERE v.Price BETWEEN ? AND ? " +
                "AND vc.VehicleCondition = ? " +
                "AND vc.TitleStatus = ? " +
                "AND vc2.Year BETWEEN ? AND ? " +
                "AND v.Vin <> ? " +
                "LIMIT 20";

        int lowerPrice = basePrice - 1000;
        int upperPrice = basePrice + 1000;
        int lowerYear = baseYear - 2;
        int upperYear = baseYear + 2;

        class CandidateVehicle {
            Vehicles vehicle;
            int odometer;
            int year;
            int manufacturerId;
            double score;
        }
        List<CandidateVehicle> candidates = new ArrayList<>();

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, lowerPrice);
            stmt.setInt(2, upperPrice);
            stmt.setString(3, baseCondition.getVehicleCondition());
            stmt.setString(4, baseCondition.getTitleStatus());
            stmt.setInt(5, lowerYear);
            stmt.setInt(6, upperYear);
            stmt.setString(7, vin);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CandidateVehicle cand = new CandidateVehicle();

                    cand.vehicle = Vehicles.builder()
                            .vehicleId(rs.getLong("VehicleId"))
                            .vin(rs.getString("Vin"))
                            .price(rs.getInt("Price"))
                            .postingDate(rs.getDate("PostingDate").toLocalDate())
                            .description(rs.getString("Description"))
                            .modelId(rs.getInt("ModelId"))
                            .build();
                    cand.odometer = rs.getInt("Odometer");
                    cand.year = rs.getInt("Year");
                    cand.manufacturerId = rs.getInt("ManufacturerId");


                    double priceScore = Math.max(0, 1000 - Math.abs(cand.vehicle.getPrice() - basePrice));
                    double yearDiff = Math.abs(cand.year - baseYear);
                    double yearScore = Math.max(0, (2 - yearDiff) * 250);
                    double modelBonus = (cand.vehicle.getModelId() == baseModelId) ? 1000 : 0;
                    if (modelBonus == 0 && cand.manufacturerId == baseManufacturerId) {
                        modelBonus = 500;
                    }
                    double odometerScore = Math.max(0, 500 - Math.abs(cand.odometer - baseOdometer));

                    LocalDate basePosting = baseVehicle.getPostingDate();
                    LocalDate candPosting = cand.vehicle.getPostingDate();
                    long dayDiff = Math.abs(java.time.temporal.ChronoUnit.DAYS.between(basePosting, candPosting));
                    double postingDateScore = Math.max(0, 30 - dayDiff);

                    cand.score = priceScore + yearScore + modelBonus + odometerScore + postingDateScore;
                    candidates.add(cand);
                }
            }
        }

        candidates.sort((c1, c2) -> Double.compare(c2.score, c1.score));

        int resultCount = Math.min(5, candidates.size());
        for (int i = 0; i < resultCount; i++) {
            finalRecommendations.add(candidates.get(i).vehicle);
        }

        return finalRecommendations;
    }
}
