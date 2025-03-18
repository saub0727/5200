package car.genie.server.dal;

import car.genie.server.model.VehicleConditions;
import car.genie.server.model.Vehicles;
import car.genie.server.model.Models;
import car.genie.server.model.Manufacturers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleConditionsDaoTest {

    private VehicleConditionsDao vehicleConditionsDao;
    private VehiclesDao vehiclesDao;
    private ModelsDao modelsDao;
    private ManufacturersDao manufacturersDao;
    private Manufacturers testManufacturer;
    private Models testModel;
    private Vehicles testVehicle;
    private VehicleConditions testVehicleConditions;

    @BeforeEach
    public void setUp() throws SQLException {
        vehicleConditionsDao = VehicleConditionsDao.getInstance();
        vehiclesDao = VehiclesDao.getInstance();
        modelsDao = ModelsDao.getInstance();
        manufacturersDao = ManufacturersDao.getInstance();

        // Create test manufacturer
        testManufacturer = Manufacturers.builder()
                .manufacturerName("TestManufacturer")
                .build();
        testManufacturer = manufacturersDao.create(testManufacturer);
        assertNotNull(testManufacturer.getManufacturerId(), "Manufacturer ID should not be null");

        // Create test model
        testModel = Models.builder()
                .modelName("TestModel")
                .manufacturerId(testManufacturer.getManufacturerId())
                .build();
        testModel = modelsDao.create(testModel);
        assertNotNull(testModel.getModelId(), "Model ID should not be null");

        // Create test vehicle
        testVehicle = Vehicles.builder()
                .vehicleId(1L)
                .vin("TEST123456789")
                .price(30000)
                .postingDate(LocalDate.now())
                .description("Test Vehicle")
                .modelId(testModel.getModelId())
                .build();
        testVehicle = vehiclesDao.create(testVehicle);
        assertNotNull(testVehicle.getVehicleId(), "Vehicle ID should not be null");

        // Create test vehicle conditions
        testVehicleConditions = VehicleConditions.builder()
                .vehicleId(testVehicle.getVehicleId())
                .odometer(50000)
                .vehicleCondition("GOOD")
                .titleStatus("CLEAN")
                .build();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try {
            VehicleConditions conditions = vehicleConditionsDao.getVehicleConditionsByVehicleId(testVehicle.getVehicleId());
            if (conditions != null) {
                vehicleConditionsDao.delete(conditions);
            }
        } catch (SQLException e) {
            // Ignore
        }

        if (testVehicle != null) {
            vehiclesDao.delete(testVehicle);
        }

        if (testModel != null && testModel.getModelId() != null) {
            modelsDao.delete(testModel);
        }

        if (testManufacturer != null && testManufacturer.getManufacturerId() != null) {
            manufacturersDao.delete(testManufacturer);
        }
    }

    @Test
    public void testCreate() throws SQLException {
        VehicleConditions createdConditions = vehicleConditionsDao.create(testVehicleConditions);
        
        assertNotNull(createdConditions);
        assertEquals(testVehicle.getVehicleId(), createdConditions.getVehicleId());
        assertEquals(50000, createdConditions.getOdometer());
        assertEquals("GOOD", createdConditions.getVehicleCondition());
        assertEquals("CLEAN", createdConditions.getTitleStatus());
    }

    @Test
    public void testGetVehicleConditionsByVehicleId() throws SQLException {
        vehicleConditionsDao.create(testVehicleConditions);
        
        VehicleConditions retrievedConditions = vehicleConditionsDao.getVehicleConditionsByVehicleId(testVehicle.getVehicleId());
        
        assertNotNull(retrievedConditions);
        assertEquals(testVehicle.getVehicleId(), retrievedConditions.getVehicleId());
        assertEquals(50000, retrievedConditions.getOdometer());
        assertEquals("GOOD", retrievedConditions.getVehicleCondition());
        assertEquals("CLEAN", retrievedConditions.getTitleStatus());
    }

    @Test
    public void testGetVehicleConditionsByCondition() throws SQLException {
        vehicleConditionsDao.create(testVehicleConditions);
        
        List<VehicleConditions> conditionsList = vehicleConditionsDao.getVehicleConditionsByCondition("GOOD");
        
        assertNotNull(conditionsList);
        assertFalse(conditionsList.isEmpty());
        assertTrue(conditionsList.stream().anyMatch(c -> 
                c.getVehicleId() == testVehicle.getVehicleId() && 
                c.getVehicleCondition().equals("GOOD")));
    }

    @Test
    public void testGetVehicleConditionsByTitleStatus() throws SQLException {
        vehicleConditionsDao.create(testVehicleConditions);
        
        List<VehicleConditions> conditionsList = vehicleConditionsDao.getVehicleConditionsByTitleStatus("CLEAN");
        
        assertNotNull(conditionsList);
        assertFalse(conditionsList.isEmpty());
        assertTrue(conditionsList.stream().anyMatch(c -> 
                c.getVehicleId() == testVehicle.getVehicleId() && 
                c.getTitleStatus().equals("CLEAN")));
    }

    @Test
    public void testUpdateVehicleConditions() throws SQLException {
        vehicleConditionsDao.create(testVehicleConditions);
        
        testVehicleConditions.setOdometer(60000);
        testVehicleConditions.setVehicleCondition("EXCELLENT");
        testVehicleConditions.setTitleStatus("REBUILT");
        
        VehicleConditions updatedConditions = vehicleConditionsDao.updateVehicleConditions(testVehicleConditions);
        
        assertNotNull(updatedConditions);
        assertEquals(60000, updatedConditions.getOdometer());
        assertEquals("EXCELLENT", updatedConditions.getVehicleCondition());
        assertEquals("REBUILT", updatedConditions.getTitleStatus());
        
        VehicleConditions retrievedConditions = vehicleConditionsDao.getVehicleConditionsByVehicleId(testVehicle.getVehicleId());
        assertEquals(60000, retrievedConditions.getOdometer());
        assertEquals("EXCELLENT", retrievedConditions.getVehicleCondition());
        assertEquals("REBUILT", retrievedConditions.getTitleStatus());
    }

    @Test
    public void testDelete() throws SQLException {
        vehicleConditionsDao.create(testVehicleConditions);
        
        vehicleConditionsDao.delete(testVehicleConditions);
        
        VehicleConditions retrievedConditions = vehicleConditionsDao.getVehicleConditionsByVehicleId(testVehicle.getVehicleId());
        assertNull(retrievedConditions);
    }
}
