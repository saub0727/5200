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
import java.util.List;

@WebServlet("/vehicle/*")
public class VehicleServlet extends HttpServlet {
    private VehiclesDao vehiclesDao;

    // Initialize DAO
    @Override
    public void init() throws ServletException {
        vehiclesDao = VehiclesDao.getInstance();
        System.out.println("✅ vehiclesDao initialized successfully!");
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
                    return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                @Override
                public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext context) {
                    return new JsonPrimitive(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
                }
            })
            .create();

    // CREATE (POST) - Add a new vehicle
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Vehicles vehicle = parseRequestBody(req);
        if (vehicle == null || vehicle.getVehicleId() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"vehicleId is required for creating a vehicle\"}");
            return;
        }

        try {
            Vehicles createdVehicle = vehiclesDao.create(vehicle);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(createdVehicle));
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to create vehicle\"}");
        }
    }

    // UPDATE (PUT) - Modify an existing vehicle
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

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

        try {
            Vehicles existingVehicle = vehiclesDao.getVehicleById(vehicleId);
            if (existingVehicle == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Vehicle not found\"}");
                return;
            }

            // Update vehicle properties manually, including postingDate
            existingVehicle.setVin(updatedVehicle.getVin());
            existingVehicle.setPrice(updatedVehicle.getPrice());
            existingVehicle.setPostingDate(updatedVehicle.getPostingDate()); // Ensure it is set manually
            existingVehicle.setDescription(updatedVehicle.getDescription());
            existingVehicle.setModelId(updatedVehicle.getModelId());

            Vehicles updated = vehiclesDao.updateVehicle(existingVehicle);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(updated));

        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to update vehicle\"}");
        }
    }


    // DELETE (DELETE) - Remove a vehicle by ID
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

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

        try {
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
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to delete vehicle\"}");
        }
    }

    // Utility: Parse JSON request body into Vehicles object
    private Vehicles parseRequestBody(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        JsonObject json = gson.fromJson(reader, JsonObject.class);

        if (json == null) return null;

        return new Vehicles(
                json.has("vehicleId") ? json.get("vehicleId").getAsLong() : null,
                json.has("vin") ? json.get("vin").getAsString() : null,
                json.has("price") ? json.get("price").getAsInt() : null,
                json.has("postingDate") && !json.get("postingDate").isJsonNull()
                        ? LocalDate.parse(json.get("postingDate").getAsString(), DateTimeFormatter.ISO_LOCAL_DATE)
                        : null, // Ensure postingDate is handled correctly
                json.has("description") ? json.get("description").getAsString() : null,
                json.has("modelId") ? json.get("modelId").getAsInt() : null
        );
    }
    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*"); // Allow all origins
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

}
