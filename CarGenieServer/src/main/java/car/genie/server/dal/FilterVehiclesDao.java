package car.genie.server.dal;

import car.genie.server.model.Vehicles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;

public class FilterVehiclesDao {
    protected ConnectionManager connectionManager;
    private static FilterVehiclesDao instance = null;
    private static final Logger logger = Logger.getLogger(FilterVehiclesDao.class.getName());

    protected FilterVehiclesDao() {
        connectionManager = new ConnectionManager();
    }

    public static FilterVehiclesDao getInstance() {
        if (instance == null) {
            instance = new FilterVehiclesDao();
        }
        return instance;
    }

    public List<Vehicles> filterVehicles(String condition, String titleStatus, String fuel,
            String transmission, String drive, Integer minPrice,
            Integer maxPrice) throws SQLException {
        List<Vehicles> vehicles = new ArrayList<>();

        // Updated SELECT to match your exact schema
        StringBuilder query = new StringBuilder(
                "SELECT v.VehicleId, v.Vin, v.Price, v.PostingDate, v.Description, v.ModelId, " +
                        "vc.VehicleCondition, vc.TitleStatus, vc.Odometer, " +
                        "vs.Fuel, vs.Transmission, vs.Drive, " +
                        "vcl.Year, " +
                        "m.ModelName, " +
                        "man.ManufacturerName " +
                        "FROM Vehicles v");

        // Using LEFT JOIN to ensure we get vehicles even if some related data is
        // missing
        query.append(" LEFT JOIN VehicleConditions vc ON v.VehicleId = vc.VehicleId");
        query.append(" LEFT JOIN VehicleSpecs vs ON v.VehicleId = vs.VehicleId");
        query.append(" LEFT JOIN VehicleClassification vcl ON v.VehicleId = vcl.VehicleId");
        query.append(" LEFT JOIN Models m ON v.ModelId = m.ModelId");
        query.append(" LEFT JOIN Manufacturers man ON m.ManufacturerId = man.ManufacturerId");

        // WHERE clause
        query.append(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // Condition filters
        if (condition != null && !condition.isEmpty()) {
            query.append(" AND vc.VehicleCondition = ?");
            params.add(condition);
        }

        if (titleStatus != null && !titleStatus.isEmpty()) {
            query.append(" AND vc.TitleStatus = ?");
            params.add(titleStatus);
        }

        if (fuel != null && !fuel.isEmpty()) {
            query.append(" AND vs.Fuel = ?");
            params.add(fuel);
        }

        if (transmission != null && !transmission.isEmpty()) {
            query.append(" AND vs.Transmission = ?");
            params.add(transmission);
        }

        if (drive != null && !drive.isEmpty()) {
            query.append(" AND vs.Drive = ?");
            params.add(drive);
        }

        if (minPrice != null) {
            query.append(" AND v.Price >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            query.append(" AND v.Price <= ?");
            params.add(maxPrice);
        }

        // Add LIMIT
        // query.append(" LIMIT 100");

        logger.log(Level.INFO, "Executing query: " + query.toString());
        logger.log(Level.INFO, "With parameters: " + params.toString());

        try (Connection connection = connectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    statement.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    statement.setString(i + 1, (String) param);
                }
            }

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    // Initialize with basic properties
                    Vehicles.VehiclesBuilder vehicleBuilder = Vehicles.builder()
                            .vehicleId(results.getLong("VehicleId"))
                            .modelId(results.getInt("ModelId"));

                    // Handle potentially null values
                    try {
                        vehicleBuilder.vin(results.getString("Vin"));
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "Vin not found or null", e);
                    }

                    try {
                        vehicleBuilder.price(results.getInt("Price"));
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "Price not found or null", e);
                    }

                    try {
                        if (results.getDate("PostingDate") != null) {
                            vehicleBuilder.postingDate(results.getDate("PostingDate").toLocalDate());
                        }
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "PostingDate not found or null", e);
                    }

                    try {
                        vehicleBuilder.description(results.getString("Description"));
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "Description not found or null", e);
                    }

                    // Handle additional fields with careful null checking
                    try {
                        vehicleBuilder.condition(results.getString("VehicleCondition"));
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "VehicleCondition not found or null", e);
                    }

                    try {
                        vehicleBuilder.titleStatus(results.getString("TitleStatus"));
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "TitleStatus not found or null", e);
                    }

                    try {
                        int odometer = results.getInt("Odometer");
                        if (!results.wasNull()) {
                            vehicleBuilder.mileage(odometer);
                        }
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "Odometer not found or null", e);
                    }

                    try {
                        vehicleBuilder.fuel(results.getString("Fuel"));
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "Fuel not found or null", e);
                    }

                    try {
                        vehicleBuilder.transmission(results.getString("Transmission"));
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "Transmission not found or null", e);
                    }

                    try {
                        vehicleBuilder.drive(results.getString("Drive"));
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "Drive not found or null", e);
                    }

                    try {
                        int year = results.getInt("Year");
                        if (!results.wasNull()) {
                            vehicleBuilder.year(year);
                        }
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "Year not found or null", e);
                    }

                    try {
                        vehicleBuilder.model(results.getString("ModelName"));
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "ModelName not found or null", e);
                    }

                    try {
                        vehicleBuilder.make(results.getString("ManufacturerName"));
                    } catch (SQLException e) {
                        logger.log(Level.FINE, "ManufacturerName not found or null", e);
                    }

                    Vehicles vehicle = vehicleBuilder.build();
                    vehicles.add(vehicle);
                    logger.log(Level.INFO, "Found vehicle: " + vehicle.getVehicleId());
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL Exception occurred: " + e.getMessage(), e);
            throw e;
        }

        return vehicles;
    }
}