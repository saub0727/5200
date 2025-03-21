//package car.genie.server.dal;
//
//import car.genie.server.model.Models;
//import car.genie.server.model.Manufacturers;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.AfterEach;
//
//import java.sql.SQLException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ModelsDaoTest {
//
//    private ModelsDao modelsDao;
//    private ManufacturersDao manufacturersDao;
//    private Manufacturers testManufacturer;
//    private Models testModel;
//
//    @BeforeEach
//    public void setUp() throws SQLException {
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
//    }
//
//    @AfterEach
//    public void tearDown() throws SQLException {
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
//        Models newModel = Models.builder()
//                .modelName("NewTestModel")
//                .manufacturerId(testManufacturer.getManufacturerId())
//                .build();
//        Models created = modelsDao.create(newModel);
//
//        try {
//            assertNotNull(created, "Created model should not be null");
//            assertNotNull(created.getModelId(), "Created model ID should not be null");
//            assertEquals("NewTestModel", created.getModelName());
//            assertEquals(testManufacturer.getManufacturerId(), created.getManufacturerId());
//        } finally {
//            if (created != null && created.getModelId() != null) {
//                modelsDao.delete(created);
//            }
//        }
//    }
//
//    @Test
//    public void testGetModelByModelId() throws SQLException {
//        Models found = modelsDao.getModelByModelId(testModel.getModelId());
//        assertNotNull(found, "Should find model by ID");
//        assertEquals(testModel.getModelId(), found.getModelId());
//        assertEquals(testModel.getModelName(), found.getModelName());
//        assertEquals(testManufacturer.getManufacturerId(), found.getManufacturerId());
//    }
//
//    @Test
//    public void testGetModelsByManufacturerId() throws SQLException {
//        List<Models> models = modelsDao.getModelsByManufacturerId(testManufacturer.getManufacturerId());
//        assertNotNull(models, "Models list should not be null");
//        assertFalse(models.isEmpty(), "Models list should not be empty");
//
//        boolean found = false;
//        for (Models model : models) {
//            if (model.getModelId().equals(testModel.getModelId())) {
//                found = true;
//                break;
//            }
//        }
//        assertTrue(found, "Test model should exist in manufacturer's models list");
//    }
//
//    @Test
//    public void testUpdateModel() throws SQLException {
//        testModel.setModelName("UpdatedTestModel");
//        Models updated = modelsDao.updateModel(testModel);
//
//        assertNotNull(updated, "Updated model should not be null");
//        assertEquals(testModel.getModelId(), updated.getModelId());
//        assertEquals("UpdatedTestModel", updated.getModelName());
//        assertEquals(testManufacturer.getManufacturerId(), updated.getManufacturerId());
//
//        // Verify in database
//        Models fromDb = modelsDao.getModelByModelId(testModel.getModelId());
//        assertNotNull(fromDb, "Should find updated model in database");
//        assertEquals("UpdatedTestModel", fromDb.getModelName());
//    }
//
//    @Test
//    public void testDelete() throws SQLException {
//        Models toDelete = Models.builder()
//                .modelName("ToDeleteModel")
//                .manufacturerId(testManufacturer.getManufacturerId())
//                .build();
//        toDelete = modelsDao.create(toDelete);
//        assertNotNull(toDelete.getModelId(), "Created model should have an ID");
//
//        Models deleted = modelsDao.delete(toDelete);
//        assertNull(deleted, "Delete should return null");
//
//        // Verify deletion
//        Models fromDb = modelsDao.getModelByModelId(toDelete.getModelId());
//        assertNull(fromDb, "Deleted model should not exist in database");
//    }
//
//    @Test
//    public void testGetNonExistentModel() throws SQLException {
//        Models nonExistent = modelsDao.getModelByModelId(-1);
//        assertNull(nonExistent, "Non-existent model should return null");
//    }
//}