package car.genie.server.servlet;

import car.genie.server.dal.VehiclesDao;
import car.genie.server.model.Vehicles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/findvehicles")
public class FindVehicles extends HttpServlet {
    protected VehiclesDao vehiclesDao;

    @Override
    public void init() throws ServletException {
        vehiclesDao = VehiclesDao.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set response type
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Allow CORS
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = resp.getWriter();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>)
                (localDate, type, jsonSerializationContext) -> jsonSerializationContext.serialize(
                        localDate.format(DateTimeFormatter.ISO_LOCAL_DATE))).create(); // Serialize to yyyy-MM-dd

        // Retrieve and validate vin from the URL query string
        String vin = req.getParameter("vin");
        if (vin == null || vin.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("error", "Please enter a valid VIN.")));
            out.flush();
        } else {
            // Try to retrieve Vehicles, return json
            try {
                Vehicles vehicle = vehiclesDao.getVehicleByVin(vin);
                if (vehicle == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson(Map.of("error", "No vehicle found for VIN: " + vin)));
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    out.println(gson.toJson(vehicle));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gson.toJson(Map.of("error", "Internal server error.")));
            }
            out.flush();
        }
    }
}
