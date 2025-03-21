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
        StringBuilder query = new StringBuilder("SELECT v.* FROM Vehicles v");
        boolean joinConditions = false;
        boolean joinSpecs = false;

        // Determine which joins are needed
        if (condition != null && !condition.isEmpty() || titleStatus != null && !titleStatus.isEmpty()) {
            query.append(" JOIN VehicleConditions vc ON v.VehicleId = vc.VehicleId");
            joinConditions = true;
        }
        if (fuel != null && !fuel.isEmpty() || transmission != null && !transmission.isEmpty() || drive != null && !drive.isEmpty()) {
            query.append(" JOIN VehicleSpecs vs ON v.VehicleId = vs.VehicleId");
            joinSpecs = true;
        }

        // Ensure WHERE clause
        query.append(" WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // Add conditions dynamically
        if (condition != null && !condition.isEmpty() && joinConditions) {
            query.append(" AND vc.VehicleCondition = ?");
            params.add(condition);
        }
        if (titleStatus != null && !titleStatus.isEmpty() && joinConditions) {
            query.append(" AND vc.TitleStatus = ?");
            params.add(titleStatus);
        }
        if (fuel != null && !fuel.isEmpty() && joinSpecs) {
            query.append(" AND vs.Fuel = ?");
            params.add(fuel);
        }
        if (transmission != null && !transmission.isEmpty() && joinSpecs) {
            query.append(" AND vs.Transmission = ?");
            params.add(transmission);
        }
        if (drive != null && !drive.isEmpty() && joinSpecs) {
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

        // Add LIMIT at the end
        query.append(" LIMIT 100");

        logger.log(Level.INFO, "Executing query: " + query.toString());

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
                    Vehicles vehicle = new Vehicles(
                            results.getLong("VehicleId"),
                            results.getString("Vin"),
                            results.getInt("Price"),
                            results.getDate("PostingDate").toLocalDate(),
                            results.getString("Description"),
                            results.getInt("ModelId")
                    );
                    vehicles.add(vehicle);
                    logger.log(Level.INFO, "Found vehicle: " + vehicle.getVehicleId() + ", VIN: " + vehicle.getVin());
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL Exception occurred: " + e.getMessage(), e);
            throw e;
        }
        return vehicles;
    }
}
