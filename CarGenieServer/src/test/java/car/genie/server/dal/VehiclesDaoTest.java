//package car.genie.server.dal;
//
//import car.genie.server.model.Vehicles;
//import car.genie.server.model.Models;
//import car.genie.server.model.Manufacturers;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.AfterEach;
//
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class VehiclesDaoTest {
//
//    private VehiclesDao vehiclesDao;
//    private ModelsDao modelsDao;
//    private ManufacturersDao manufacturersDao;
//    private Manufacturers testManufacturer;
//    private Models testModel;
//    private Vehicles testVehicle;
//
//    @BeforeEach
//    public void setUp() throws SQLException {
//        vehiclesDao = VehiclesDao.getInstance();
//        modelsDao = ModelsDao.getInstance();
//        manufacturersDao = ManufacturersDao.getInstance();
//
//        // Create test manufacturer
//        testManufacturer = Manufacturers.builder()
//                .manufacturerName("TestManufacturer")
//                .build();
//        testManufacturer = manufacturersDao.create(testManufacturer);
//        assertNotNull(testManufacturer.getManufacturerId(), "Manufacturer ID should not be null");
//
//        // Create test model
//        testModel = Models.builder()
//                .modelName("TestModel")
//                .manufacturerId(testManufacturer.getManufacturerId())
//                .build();
//        testModel = modelsDao.create(testModel);
//        assertNotNull(testModel.getModelId(), "Model ID should not be null");
//
//        // Create test vehicle
//        testVehicle = Vehicles.builder()
//                .vehicleId(1L)
//                .vin("TEST123456789")
//                .price(30000)
//                .postingDate(LocalDate.now())
//                .description("Test Vehicle")
//                .modelId(testModel.getModelId())
//                .build();
//        testVehicle = vehiclesDao.create(testVehicle);
//        assertNotNull(testVehicle.getVehicleId(), "Vehicle ID should not be null");
//    }
//
//    @AfterEach
//    public void tearDown() throws SQLException {
//        if (testVehicle != null && testVehicle.getVehicleId() != null) {
//            vehiclesDao.delete(testVehicle);
//            Vehicles deleted = vehiclesDao.getVehicleById(testVehicle.getVehicleId());
//            assertNull(deleted, "Test vehicle should be deleted");
//        }
//
//        if (testModel != null && testModel.getModelId() != null) {
//            modelsDao.delete(testModel);
//            Models deleted = modelsDao.getModelByModelId(testModel.getModelId());
//            assertNull(deleted, "Test model should be deleted");
//        }
//
//        if (testManufacturer != null && testManufacturer.getManufacturerId() != null) {
//            manufacturersDao.delete(testManufacturer);
//            Manufacturers deleted = manufacturersDao
//                    .getManufacturerByManufacturerId(testManufacturer.getManufacturerId());
//            assertNull(deleted, "Test manufacturer should be deleted");
//        }
//    }
//
//    @Test
//    public void testCreate() throws SQLException {
//        Vehicles newVehicle = Vehicles.builder()
//                .vehicleId(2L)
//                .vin("TEST987654321")
//                .price(25000)
//                .postingDate(LocalDate.now())
//                .description("New Test Vehicle")
//                .modelId(testModel.getModelId())
//                .build();
//        Vehicles created = vehiclesDao.create(newVehicle);
//
//        try {
//            assertNotNull(created, "Created vehicle should not be null");
//            assertEquals(2L, created.getVehicleId());
//            assertEquals("TEST987654321", created.getVin());
//            assertEquals(25000, created.getPrice());
//            assertEquals(testModel.getModelId(), created.getModelId());
//        } finally {
//            if (created != null && created.getVehicleId() != null) {
//                vehiclesDao.delete(created);
//            }
//        }
//    }
//
//    @Test
//    public void testGetVehicleById() throws SQLException {
//        Vehicles found = vehiclesDao.getVehicleById(testVehicle.getVehicleId());
//        assertNotNull(found, "Should find vehicle by ID");
//        assertEquals(testVehicle.getVehicleId(), found.getVehicleId());
//        assertEquals(testVehicle.getVin(), found.getVin());
//        assertEquals(testVehicle.getPrice(), found.getPrice());
//        assertEquals(testModel.getModelId(), found.getModelId());
//    }
//
//    @Test
//    public void testGetVehicleByVin() throws SQLException {
//        Vehicles found = vehiclesDao.getVehicleByVin(testVehicle.getVin());
//        assertNotNull(found, "Should find vehicle by VIN");
//        assertEquals(testVehicle.getVehicleId(), found.getVehicleId());
//        assertEquals(testVehicle.getVin(), found.getVin());
//        assertEquals(testVehicle.getPrice(), found.getPrice());
//        assertEquals(testModel.getModelId(), found.getModelId());
//    }
//
//    @Test
//    public void testGetVehiclesByModelId() throws SQLException {
//        List<Vehicles> vehicles = vehiclesDao.getVehiclesByModelId(testModel.getModelId());
//        assertNotNull(vehicles, "Vehicles list should not be null");
//        assertFalse(vehicles.isEmpty(), "Vehicles list should not be empty");
//
//        boolean found = false;
//        for (Vehicles vehicle : vehicles) {
//            if (vehicle.getVehicleId().equals(testVehicle.getVehicleId())) {
//                found = true;
//                break;
//            }
//        }
//        assertTrue(found, "Test vehicle should exist in model's vehicles list");
//    }
//
//    @Test
//    public void testUpdateVehicle() throws SQLException {
//        testVehicle.setPrice(35000);
//        testVehicle.setDescription("Updated Test Vehicle");
//        Vehicles updated = vehiclesDao.updateVehicle(testVehicle);
//
//        assertNotNull(updated, "Updated vehicle should not be null");
//        assertEquals(testVehicle.getVehicleId(), updated.getVehicleId());
//        assertEquals(35000, updated.getPrice());
//        assertEquals("Updated Test Vehicle", updated.getDescription());
//        assertEquals(testModel.getModelId(), updated.getModelId());
//
//        // Verify in database
//        Vehicles fromDb = vehiclesDao.getVehicleById(testVehicle.getVehicleId());
//        assertNotNull(fromDb, "Should find updated vehicle in database");
//        assertEquals(35000, fromDb.getPrice());
//        assertEquals("Updated Test Vehicle", fromDb.getDescription());
//    }
//
//    @Test
//    public void testDelete() throws SQLException {
//        Vehicles toDelete = Vehicles.builder()
//                .vehicleId(3L)
//                .vin("TEST456789123")
//                .price(20000)
//                .postingDate(LocalDate.now())
//                .description("To Delete Vehicle")
//                .modelId(testModel.getModelId())
//                .build();
//        toDelete = vehiclesDao.create(toDelete);
//        assertNotNull(toDelete.getVehicleId(), "Created vehicle should have an ID");
//
//        Vehicles deleted = vehiclesDao.delete(toDelete);
//        assertNull(deleted, "Delete should return null");
//
//        // Verify deletion
//        Vehicles fromDb = vehiclesDao.getVehicleById(toDelete.getVehicleId());
//        assertNull(fromDb, "Deleted vehicle should not exist in database");
//    }
//
//    @Test
//    public void testGetNonExistentVehicle() throws SQLException {
//        Vehicles nonExistent = vehiclesDao.getVehicleById(-1L);
//        assertNull(nonExistent, "Non-existent vehicle should return null");
//    }
//}