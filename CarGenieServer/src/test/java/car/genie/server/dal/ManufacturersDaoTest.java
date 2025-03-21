//package car.genie.server.dal;
//
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
//public class ManufacturersDaoTest {
//
//    private ManufacturersDao manufacturersDao;
//    private Manufacturers testManufacturer;
//
//    @BeforeEach
//    public void setUp() throws SQLException {
//        manufacturersDao = ManufacturersDao.getInstance();
//        testManufacturer = Manufacturers.builder()
//                .manufacturerName("TestManufacturer")
//                .build();
//        testManufacturer = manufacturersDao.create(testManufacturer);
//        assertNotNull(testManufacturer.getManufacturerId(), "Manufacturer ID should not be null");
//    }
//
//    @AfterEach
//    public void tearDown() throws SQLException {
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
//        Manufacturers newManufacturer = Manufacturers.builder()
//                .manufacturerName("NewTestManufacturer")
//                .build();
//
//        Manufacturers created = manufacturersDao.create(newManufacturer);
//
//        try {
//            assertNotNull(created, "Created manufacturer should not be null");
//            assertNotNull(created.getManufacturerId(), "Created manufacturer ID should not be null");
//            assertEquals("NewTestManufacturer", created.getManufacturerName());
//        } finally {
//            // Cleanup even if test fails
//            if (created != null && created.getManufacturerId() != null) {
//                manufacturersDao.delete(created);
//            }
//        }
//    }
//
//    @Test
//    public void testGetManufacturerByManufacturerId() throws SQLException {
//        Manufacturers found = manufacturersDao.getManufacturerByManufacturerId(testManufacturer.getManufacturerId());
//        assertNotNull(found, "Should find manufacturer by ID");
//        assertEquals(testManufacturer.getManufacturerId(), found.getManufacturerId());
//        assertEquals(testManufacturer.getManufacturerName(), found.getManufacturerName());
//    }
//
//    @Test
//    public void testGetManufacturerByName() throws SQLException {
//        Manufacturers found = manufacturersDao.getManufacturerByName(testManufacturer.getManufacturerName());
//        assertNotNull(found, "Should find manufacturer by name");
//        assertEquals(testManufacturer.getManufacturerId(), found.getManufacturerId());
//        assertEquals(testManufacturer.getManufacturerName(), found.getManufacturerName());
//    }
//
//    @Test
//    public void testUpdateManufacturer() throws SQLException {
//        testManufacturer.setManufacturerName("UpdatedTestManufacturer");
//        Manufacturers updated = manufacturersDao.updateManufacturer(testManufacturer);
//
//        assertNotNull(updated, "Updated manufacturer should not be null");
//        assertEquals(testManufacturer.getManufacturerId(), updated.getManufacturerId());
//        assertEquals("UpdatedTestManufacturer", updated.getManufacturerName());
//
//        // Verify in database
//        Manufacturers fromDb = manufacturersDao.getManufacturerByManufacturerId(testManufacturer.getManufacturerId());
//        assertNotNull(fromDb, "Should find updated manufacturer in database");
//        assertEquals("UpdatedTestManufacturer", fromDb.getManufacturerName());
//    }
//
//    @Test
//    public void testDelete() throws SQLException {
//        Manufacturers toDelete = Manufacturers.builder()
//                .manufacturerName("ToDeleteManufacturer")
//                .build();
//        toDelete = manufacturersDao.create(toDelete);
//        assertNotNull(toDelete.getManufacturerId(), "Created manufacturer should have an ID");
//
//        Manufacturers deleted = manufacturersDao.delete(toDelete);
//        assertNull(deleted, "Delete should return null");
//
//        // Verify deletion
//        Manufacturers fromDb = manufacturersDao.getManufacturerByManufacturerId(toDelete.getManufacturerId());
//        assertNull(fromDb, "Deleted manufacturer should not exist in database");
//    }
//
//    @Test
//    public void testGetAllManufacturers() throws SQLException {
//        List<Manufacturers> allManufacturers = manufacturersDao.getAllManufacturers();
//        assertNotNull(allManufacturers, "Manufacturers list should not be null");
//        assertFalse(allManufacturers.isEmpty(), "Manufacturers list should not be empty");
//
//        // Verify test manufacturer exists in list
//        boolean found = false;
//        for (Manufacturers manufacturer : allManufacturers) {
//            if (manufacturer.getManufacturerId().equals(testManufacturer.getManufacturerId())) {
//                found = true;
//                break;
//            }
//        }
//        assertTrue(found, "Test manufacturer should exist in the list");
//    }
//
//    @Test
//    public void testGetManufacturersByNameLike() throws SQLException {
//        List<Manufacturers> manufacturers = manufacturersDao.getManufacturersByNameLike("Test");
//
//        assertNotNull(manufacturers, "Search results should not be null");
//        assertFalse(manufacturers.isEmpty(), "Search results should not be empty");
//
//        // Verify test manufacturer exists in results
//        boolean found = false;
//        for (Manufacturers manufacturer : manufacturers) {
//            if (manufacturer.getManufacturerId().equals(testManufacturer.getManufacturerId())) {
//                found = true;
//                break;
//            }
//        }
//        assertTrue(found, "Test manufacturer should exist in search results");
//    }
//
//    @Test
//    public void testGetNonExistentManufacturer() throws SQLException {
//        Manufacturers nonExistent = manufacturersDao.getManufacturerByManufacturerId(-1);
//        assertNull(nonExistent, "Non-existent manufacturer should return null");
//    }
//}