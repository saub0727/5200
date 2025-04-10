package car.genie.server.servlet;

import car.genie.server.dal.VehiclesDao;
import car.genie.server.model.Vehicles;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/vehicle/*")
public class VehicleServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(VehicleServlet.class.getName());
    private VehiclesDao vehiclesDao;

    // Initialize DAO
    @Override
    public void init() throws ServletException {
        vehiclesDao = VehiclesDao.getInstance();
        logger.info("✅ vehiclesDao initialized successfully!");
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    // Custom Gson instance to handle LocalDate serialization
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                @Override
                public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
                    try {
                        return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
                    } catch (DateTimeParseException e) {
                        logger.warning("Failed to parse date: " + json.getAsString());
                        return null;
                    }
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                @Override
                public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext context) {
                    return localDate != null ? new JsonPrimitive(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)) : JsonNull.INSTANCE;
                }
            })
            .serializeNulls()
            .create();

    // CREATE (POST) - Add a new vehicle
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Vehicles vehicle = parseRequestBody(req);
            if (vehicle == null || vehicle.getVehicleId() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"vehicleId is required for creating a vehicle\"}");
                return;
            }

            Vehicles createdVehicle = vehiclesDao.create(vehicle);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(createdVehicle));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL error creating vehicle", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to create vehicle: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error creating vehicle", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }

    // UPDATE (PUT) - Modify an existing vehicle
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing vehicleId in the URL\"}");
                return;
            }

            long vehicleId;
            try {
                vehicleId = Long.parseLong(pathInfo.substring(1)); // Extract vehicleId from URL
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid vehicleId format\"}");
                return;
            }

            Vehicles updatedVehicle = parseRequestBody(req);
            if (updatedVehicle == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid request body\"}");
                return;
            }

            Vehicles existingVehicle = vehiclesDao.getVehicleById(vehicleId);
            if (existingVehicle == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Vehicle not found\"}");
                return;
            }

            // Update basic vehicle properties
            if (updatedVehicle.getVin() != null) existingVehicle.setVin(updatedVehicle.getVin());
            if (updatedVehicle.getPrice() != null) existingVehicle.setPrice(updatedVehicle.getPrice());
            if (updatedVehicle.getPostingDate() != null) existingVehicle.setPostingDate(updatedVehicle.getPostingDate());
            if (updatedVehicle.getDescription() != null) existingVehicle.setDescription(updatedVehicle.getDescription());
            if (updatedVehicle.getModelId() != null) existingVehicle.setModelId(updatedVehicle.getModelId());
            
            // Update new fields - only if provided in the update request
            existingVehicle.setYear(updatedVehicle.getYear());
            existingVehicle.setMake(updatedVehicle.getMake());
            existingVehicle.setModel(updatedVehicle.getModel());
            existingVehicle.setCondition(updatedVehicle.getCondition());
            existingVehicle.setMileage(updatedVehicle.getMileage());
            existingVehicle.setFuel(updatedVehicle.getFuel());
            existingVehicle.setTransmission(updatedVehicle.getTransmission());
            existingVehicle.setDrive(updatedVehicle.getDrive());
            existingVehicle.setTitleStatus(updatedVehicle.getTitleStatus());

            Vehicles updated = vehiclesDao.updateVehicle(existingVehicle);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(updated));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL error updating vehicle", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to update vehicle: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error updating vehicle", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }

    // DELETE (DELETE) - Remove a vehicle by ID
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Missing vehicleId in the URL\"}");
                return;
            }

            long vehicleId;
            try {
                vehicleId = Long.parseLong(pathInfo.substring(1)); // Extract vehicleId from URL
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid vehicleId format\"}");
                return;
            }

            Vehicles existingVehicle = vehiclesDao.getVehicleById(vehicleId);
            if (existingVehicle == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Vehicle not found\"}");
                return;
            }

            vehiclesDao.delete(existingVehicle);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\":\"Vehicle deleted successfully\"}");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL error deleting vehicle", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to delete vehicle: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error deleting vehicle", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Unexpected error: " + e.getMessage() + "\"}");
        }
    }

    // Utility: Parse JSON request body into Vehicles object
    private Vehicles parseRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        
        String requestBody = sb.toString();
        if (requestBody.isEmpty()) {
            logger.warning("Empty request body");
            return null;
        }
        
        logger.info("Parsing request body: " + requestBody);
        
        try {
            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();
            
            Vehicles.VehiclesBuilder builder = Vehicles.builder();
            
            // Handle required fields
            if (json.has("vehicleId") && !json.get("vehicleId").isJsonNull()) {
                builder.vehicleId(json.get("vehicleId").getAsLong());
            }
            
            // Handle optional fields with careful null checking
            if (json.has("vin") && !json.get("vin").isJsonNull()) {
                builder.vin(json.get("vin").getAsString());
            }
            
            if (json.has("price") && !json.get("price").isJsonNull()) {
                try {
                    builder.price(json.get("price").getAsInt());
                } catch (NumberFormatException e) {
                    logger.warning("Invalid price format: " + json.get("price"));
                }
            }
            
            if (json.has("postingDate") && !json.get("postingDate").isJsonNull()) {
                try {
                    String dateStr = json.get("postingDate").getAsString();
                    LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                    builder.postingDate(date);
                } catch (DateTimeParseException e) {
                    logger.warning("Invalid date format: " + json.get("postingDate"));
                }
            }
            
            if (json.has("description") && !json.get("description").isJsonNull()) {
                builder.description(json.get("description").getAsString());
            }
            
            if (json.has("modelId") && !json.get("modelId").isJsonNull()) {
                try {
                    builder.modelId(json.get("modelId").getAsInt());
                } catch (NumberFormatException e) {
                    logger.warning("Invalid modelId format: " + json.get("modelId"));
                }
            }
            
            // Handle all new fields with careful null checking
            if (json.has("year") && !json.get("year").isJsonNull()) {
                try {
                    builder.year(json.get("year").getAsInt());
                } catch (NumberFormatException e) {
                    logger.warning("Invalid year format: " + json.get("year"));
                }
            }
            
            if (json.has("make") && !json.get("make").isJsonNull()) {
                builder.make(json.get("make").getAsString());
            }
            
            if (json.has("model") && !json.get("model").isJsonNull()) {
                builder.model(json.get("model").getAsString());
            }
            
            if (json.has("condition") && !json.get("condition").isJsonNull()) {
                builder.condition(json.get("condition").getAsString());
            }
            
            if (json.has("mileage") && !json.get("mileage").isJsonNull()) {
                try {
                    builder.mileage(json.get("mileage").getAsInt());
                } catch (NumberFormatException e) {
                    logger.warning("Invalid mileage format: " + json.get("mileage"));
                }
            }
            
            if (json.has("fuel") && !json.get("fuel").isJsonNull()) {
                builder.fuel(json.get("fuel").getAsString());
            }
            
            if (json.has("transmission") && !json.get("transmission").isJsonNull()) {
                builder.transmission(json.get("transmission").getAsString());
            }
            
            if (json.has("drive") && !json.get("drive").isJsonNull()) {
                builder.drive(json.get("drive").getAsString());
            }
            
            if (json.has("titleStatus") && !json.get("titleStatus").isJsonNull()) {
                builder.titleStatus(json.get("titleStatus").getAsString());
            }
            
            return builder.build();
        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Invalid JSON format in request body", e);
            return null;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing request body", e);
            return null;
        }
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*"); // Allow all origins
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}