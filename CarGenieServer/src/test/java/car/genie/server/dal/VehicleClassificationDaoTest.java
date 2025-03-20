//package car.genie.server.dal;
//
//import car.genie.server.model.VehicleClassification;
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
//public class VehicleClassificationDaoTest {
//
//    private VehicleClassificationDao vehicleClassificationDao;
//    private VehiclesDao vehiclesDao;
//    private ModelsDao modelsDao;
//    private ManufacturersDao manufacturersDao;
//    private Manufacturers testManufacturer;
//    private Models testModel;
//    private Vehicles testVehicle;
//    private VehicleClassification testVehicleClassification;
//
//    @BeforeEach
//    public void setUp() throws SQLException {
//        vehicleClassificationDao = VehicleClassificationDao.getInstance();
//        vehiclesDao = VehiclesDao.getInstance();
//        modelsDao = ModelsDao.getInstance();
//        manufacturersDao = ManufacturersDao.getInstance();
//
//        testManufacturer = Manufacturers.builder()
//                .manufacturerName("TestManufacturer")
//                .build();
//        testManufacturer = manufacturersDao.create(testManufacturer);
//        assertNotNull(testManufacturer.getManufacturerId(), "Manufacturer ID should not be null");
//
//        testModel = Models.builder()
//                .modelName("TestModel")
//                .manufacturerId(testManufacturer.getManufacturerId())
//                .build();
//        testModel = modelsDao.create(testModel);
//        assertNotNull(testModel.getModelId(), "Model ID should not be null");
//
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
//
//        testVehicleClassification = VehicleClassification.builder()
//                .vehicleId(testVehicle.getVehicleId())
//                .year(2022)
//                .size("COMPACT")
//                .type("SEDAN")
//                .color("RED")
//                .build();
//    }
//
//    @AfterEach
//    public void tearDown() throws SQLException {
//        try {
//            VehicleClassification classification = vehicleClassificationDao.getVehicleClassificationByVehicleId(testVehicle.getVehicleId());
//            if (classification != null) {
//                vehicleClassificationDao.delete(classification);
//            }
//        } catch (SQLException e) {
//            // Ignore
//        }
//
//        if (testVehicle != null) {
//            vehiclesDao.delete(testVehicle);
//        }
//
//        if (testModel != null && testModel.getModelId() != null) {
//            modelsDao.delete(testModel);
//        }
//
//        if (testManufacturer != null && testManufacturer.getManufacturerId() != null) {
//            manufacturersDao.delete(testManufacturer);
//        }
//    }
//
//    @Test
//    public void testCreate() throws SQLException {
//        VehicleClassification createdClassification = vehicleClassificationDao.create(testVehicleClassification);
//
//        assertNotNull(createdClassification);
//        assertEquals(testVehicle.getVehicleId(), createdClassification.getVehicleId());
//        assertEquals(2022, createdClassification.getYear());
//        assertEquals("COMPACT", createdClassification.getSize());
//        assertEquals("SEDAN", createdClassification.getType());
//        assertEquals("RED", createdClassification.getColor());
//    }
//
//    @Test
//    public void testGetVehicleClassificationByVehicleId() throws SQLException {
//        vehicleClassificationDao.create(testVehicleClassification);
//
//        VehicleClassification retrievedClassification = vehicleClassificationDao.getVehicleClassificationByVehicleId(testVehicle.getVehicleId());
//
//        assertNotNull(retrievedClassification);
//        assertEquals(testVehicle.getVehicleId(), retrievedClassification.getVehicleId());
//        assertEquals(2022, retrievedClassification.getYear());
//        assertEquals("COMPACT", retrievedClassification.getSize());
//        assertEquals("SEDAN", retrievedClassification.getType());
//        assertEquals("RED", retrievedClassification.getColor());
//    }
//
//    @Test
//    public void testGetVehicleClassificationsByYear() throws SQLException {
//        vehicleClassificationDao.create(testVehicleClassification);
//
//        List<VehicleClassification> classificationList = vehicleClassificationDao.getVehicleClassificationsByYear(2022);
//
//        assertNotNull(classificationList);
//        assertFalse(classificationList.isEmpty());
//        assertTrue(classificationList.stream().anyMatch(c ->
//                c.getVehicleId() == testVehicle.getVehicleId() &&
//                c.getYear() == 2022));
//    }
//
//    @Test
//    public void testGetVehicleClassificationsBySize() throws SQLException {
//        vehicleClassificationDao.create(testVehicleClassification);
//
//        List<VehicleClassification> classificationList = vehicleClassificationDao.getVehicleClassificationsBySize("COMPACT");
//
//        assertNotNull(classificationList);
//        assertFalse(classificationList.isEmpty());
//        assertTrue(classificationList.stream().anyMatch(c ->
//                c.getVehicleId() == testVehicle.getVehicleId() &&
//                c.getSize().equals("COMPACT")));
//    }
//
//    @Test
//    public void testGetVehicleClassificationsByType() throws SQLException {
//        vehicleClassificationDao.create(testVehicleClassification);
//
//        List<VehicleClassification> classificationList = vehicleClassificationDao.getVehicleClassificationsByType("SEDAN");
//
//        assertNotNull(classificationList);
//        assertFalse(classificationList.isEmpty());
//        assertTrue(classificationList.stream().anyMatch(c ->
//                c.getVehicleId() == testVehicle.getVehicleId() &&
//                c.getType().equals("SEDAN")));
//    }
//
//    @Test
//    public void testGetVehicleClassificationsByColor() throws SQLException {
//        vehicleClassificationDao.create(testVehicleClassification);
//
//        List<VehicleClassification> classificationList = vehicleClassificationDao.getVehicleClassificationsByColor("RED");
//
//        assertNotNull(classificationList);
//        assertFalse(classificationList.isEmpty());
//        assertTrue(classificationList.stream().anyMatch(c ->
//                c.getVehicleId() == testVehicle.getVehicleId() &&
//                c.getColor().equals("RED")));
//    }
//
//    @Test
//    public void testUpdateVehicleClassification() throws SQLException {
//        vehicleClassificationDao.create(testVehicleClassification);
//
//        testVehicleClassification.setYear(2023);
//        testVehicleClassification.setSize("MID-SIZE");
//        testVehicleClassification.setType("SUV");
//        testVehicleClassification.setColor("BLUE");
//
//        VehicleClassification updatedClassification = vehicleClassificationDao.updateVehicleClassification(testVehicleClassification);
//
//        assertNotNull(updatedClassification);
//        assertEquals(2023, updatedClassification.getYear());
//        assertEquals("MID-SIZE", updatedClassification.getSize());
//        assertEquals("SUV", updatedClassification.getType());
//        assertEquals("BLUE", updatedClassification.getColor());
//
//        VehicleClassification retrievedClassification = vehicleClassificationDao.getVehicleClassificationByVehicleId(testVehicle.getVehicleId());
//        assertEquals(2023, retrievedClassification.getYear());
//        assertEquals("MID-SIZE", retrievedClassification.getSize());
//        assertEquals("SUV", retrievedClassification.getType());
//        assertEquals("BLUE", retrievedClassification.getColor());
//    }
//
//    @Test
//    public void testDelete() throws SQLException {
//        vehicleClassificationDao.create(testVehicleClassification);
//
//        vehicleClassificationDao.delete(testVehicleClassification);
//
//        VehicleClassification retrievedClassification = vehicleClassificationDao.getVehicleClassificationByVehicleId(testVehicle.getVehicleId());
//        assertNull(retrievedClassification);
//    }
//}
