package car.genie.server.servlet;

import car.genie.server.dal.FilterVehiclesDao;
import car.genie.server.model.Vehicles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/filtervehicles")
public class FilterVehicles extends HttpServlet {
    private static final Logger logger = Logger.getLogger(FilterVehicles.class.getName());
    private FilterVehiclesDao filterVehiclesDao;

    @Override
    public void init() throws ServletException {
        filterVehiclesDao = FilterVehiclesDao.getInstance();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Allow CORS
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PrintWriter out = resp.getWriter();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>)
                (localDate, type, jsonSerializationContext) -> jsonSerializationContext.serialize(
                        localDate.format(DateTimeFormatter.ISO_LOCAL_DATE))).create();

        // Retrieve filters from request parameters
        String condition = req.getParameter("condition");
        String titleStatus = req.getParameter("titleStatus");
        String fuel = req.getParameter("fuel");
        String transmission = req.getParameter("transmission");
        String drive = req.getParameter("drive");

        Integer minPrice = null;
        Integer maxPrice = null;
        try {
            String minPriceStr = req.getParameter("minPrice");
            String maxPriceStr = req.getParameter("maxPrice");

            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                minPrice = Integer.parseInt(minPriceStr);
            }

            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                maxPrice = Integer.parseInt(maxPriceStr);
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid price parameter", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(Map.of("error", "Invalid price parameter. Must be a number.")));
            out.flush();
            return;
        }

        logger.log(Level.INFO, "Received request with parameters - Condition: {0}, TitleStatus: {1}, Fuel: {2}, Transmission: {3}, Drive: {4}, MinPrice: {5}, MaxPrice: {6}",
                new Object[]{condition, titleStatus, fuel, transmission, drive, minPrice, maxPrice});

        try {
            List<Vehicles> vehicles = filterVehiclesDao.filterVehicles(condition, titleStatus, fuel, transmission, drive, minPrice, maxPrice);
            if (vehicles.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("error", "No vehicles found matching the criteria.")));
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(vehicles));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL Exception while fetching vehicles", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "Internal server error.")));
        }
        out.flush();
    }
}
