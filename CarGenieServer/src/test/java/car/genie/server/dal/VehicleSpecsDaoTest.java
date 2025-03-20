//package car.genie.server.dal;
//
//import car.genie.server.model.Manufacturers;
//import car.genie.server.model.Models;
//import car.genie.server.model.VehicleSpecs;
//import car.genie.server.model.Vehicles;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class VehicleSpecsDaoTest {
//    private VehicleSpecsDao vehicleSpecsDao;
//    private VehiclesDao vehiclesDao;
//    private ModelsDao modelsDao;
//    private ManufacturersDao manufacturersDao;
//
//    private Manufacturers testManufacturer;
//    private Models testModel;
//    private Vehicles testVehicle;
//    private VehicleSpecs testVehicleSpecs;
//
//    private static final String TEST_VIN = "TESTVEHICLESPEC123";
//
//    @BeforeEach
//    public void setUp() throws SQLException {
//        vehicleSpecsDao = VehicleSpecsDao.getInstance();
//        vehiclesDao = VehiclesDao.getInstance();
//        modelsDao = ModelsDao.getInstance();
//        manufacturersDao = ManufacturersDao.getInstance();
//
//        try {
//            Vehicles existingVehicle = vehiclesDao.getVehicleByVin(TEST_VIN);
//            if (existingVehicle != null) {
//                try {
//                    VehicleSpecs existingSpecs = vehicleSpecsDao.getVehicleSpecsByVehicleId(existingVehicle.getVehicleId());
//                    if (existingSpecs != null) {
//                        vehicleSpecsDao.delete(existingSpecs);
//                    }
//                } catch (SQLException e) {
//
//                }
//                vehiclesDao.delete(existingVehicle);
//            }
//        } catch (SQLException e) {
//
//        }
//
//        testManufacturer = Manufacturers.builder()
//                .manufacturerName("TestManufacturer")
//                .build();
//        testManufacturer = manufacturersDao.create(testManufacturer);
//
//        testModel = Models.builder()
//                .modelName("TestModel")
//                .manufacturerId(testManufacturer.getManufacturerId())
//                .build();
//        testModel = modelsDao.create(testModel);
//
//        testVehicle = Vehicles.builder()
//                .vehicleId(System.currentTimeMillis())
//                .vin(TEST_VIN)
//                .price(10000)
//                .postingDate(LocalDate.now())
//                .description("Test Vehicle for VehicleSpecs")
//                .modelId(testModel.getModelId())
//                .build();
//        testVehicle = vehiclesDao.create(testVehicle);
//
//        testVehicleSpecs = VehicleSpecs.builder()
//                .vehicleId(testVehicle.getVehicleId())
//                .cylinders("4")
//                .fuel("GAS")
//                .transmission("AUTOMATIC")
//                .drive("FWD")
//                .build();
//    }
//
//    @Test
//    public void testCreate() throws SQLException {
//        VehicleSpecs createdVehicleSpecs = vehicleSpecsDao.create(testVehicleSpecs);
//
//        assertNotNull(createdVehicleSpecs);
//        assertEquals(testVehicle.getVehicleId(), createdVehicleSpecs.getVehicleId());
//        assertEquals("4", createdVehicleSpecs.getCylinders());
//        assertEquals("GAS", createdVehicleSpecs.getFuel());
//        assertEquals("AUTOMATIC", createdVehicleSpecs.getTransmission());
//        assertEquals("FWD", createdVehicleSpecs.getDrive());
//
//        vehicleSpecsDao.delete(createdVehicleSpecs);
//        vehiclesDao.delete(testVehicle);
//        modelsDao.delete(testModel);
//        manufacturersDao.delete(testManufacturer);
//    }
//
//    @Test
//    public void testGetVehicleSpecsByVehicleId() throws SQLException {
//        vehicleSpecsDao.create(testVehicleSpecs);
//
//        VehicleSpecs retrievedVehicleSpecs = vehicleSpecsDao.getVehicleSpecsByVehicleId(testVehicle.getVehicleId());
//
//        assertNotNull(retrievedVehicleSpecs);
//        assertEquals(testVehicle.getVehicleId(), retrievedVehicleSpecs.getVehicleId());
//        assertEquals("4", retrievedVehicleSpecs.getCylinders());
//        assertEquals("GAS", retrievedVehicleSpecs.getFuel());
//        assertEquals("AUTOMATIC", retrievedVehicleSpecs.getTransmission());
//        assertEquals("FWD", retrievedVehicleSpecs.getDrive());
//
//        vehicleSpecsDao.delete(retrievedVehicleSpecs);
//        vehiclesDao.delete(testVehicle);
//        modelsDao.delete(testModel);
//        manufacturersDao.delete(testManufacturer);
//    }
//
//    @Test
//    public void testGetVehicleSpecsByFuel() throws SQLException {
//        vehicleSpecsDao.create(testVehicleSpecs);
//
//        List<VehicleSpecs> vehicleSpecsList = vehicleSpecsDao.getVehicleSpecsByFuel("GAS");
//
//        assertNotNull(vehicleSpecsList);
//        assertFalse(vehicleSpecsList.isEmpty());
//        assertTrue(vehicleSpecsList.stream().anyMatch(vs ->
//                vs.getVehicleId() == testVehicle.getVehicleId() &&
//                "GAS".equals(vs.getFuel())));
//
//        vehicleSpecsDao.delete(testVehicleSpecs);
//        vehiclesDao.delete(testVehicle);
//        modelsDao.delete(testModel);
//        manufacturersDao.delete(testManufacturer);
//    }
//
//    @Test
//    public void testUpdateVehicleSpecs() throws SQLException {
//        vehicleSpecsDao.create(testVehicleSpecs);
//
//        testVehicleSpecs.setCylinders("6");
//        testVehicleSpecs.setFuel("DIESEL");
//        testVehicleSpecs.setTransmission("MANUAL");
//        testVehicleSpecs.setDrive("4WD");
//
//        VehicleSpecs updatedVehicleSpecs = vehicleSpecsDao.updateVehicleSpecs(testVehicleSpecs);
//
//        assertNotNull(updatedVehicleSpecs);
//        assertEquals("6", updatedVehicleSpecs.getCylinders());
//        assertEquals("DIESEL", updatedVehicleSpecs.getFuel());
//        assertEquals("MANUAL", updatedVehicleSpecs.getTransmission());
//        assertEquals("4WD", updatedVehicleSpecs.getDrive());
//
//        VehicleSpecs retrievedVehicleSpecs = vehicleSpecsDao.getVehicleSpecsByVehicleId(testVehicle.getVehicleId());
//        assertEquals("6", retrievedVehicleSpecs.getCylinders());
//        assertEquals("DIESEL", retrievedVehicleSpecs.getFuel());
//        assertEquals("MANUAL", retrievedVehicleSpecs.getTransmission());
//        assertEquals("4WD", retrievedVehicleSpecs.getDrive());
//
//        vehicleSpecsDao.delete(updatedVehicleSpecs);
//        vehiclesDao.delete(testVehicle);
//        modelsDao.delete(testModel);
//        manufacturersDao.delete(testManufacturer);
//    }
//
//    @Test
//    public void testDelete() throws SQLException {
//        vehicleSpecsDao.create(testVehicleSpecs);
//
//        vehicleSpecsDao.delete(testVehicleSpecs);
//
//        VehicleSpecs retrievedVehicleSpecs = vehicleSpecsDao.getVehicleSpecsByVehicleId(testVehicle.getVehicleId());
//        assertNull(retrievedVehicleSpecs);
//
//        vehiclesDao.delete(testVehicle);
//        modelsDao.delete(testModel);
//        manufacturersDao.delete(testManufacturer);
//    }
//}
