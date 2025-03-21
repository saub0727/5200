//package car.genie.server.dal;
//
//import car.genie.server.model.Regions;
//import car.genie.server.model.States;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.sql.SQLException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class RegionsDaoTest {
//    private RegionsDao regionsDao;
//    private StatesDao statesDao;
//    private States testState;
//    private Regions testRegion;
//
//    @BeforeEach
//    public void setUp() throws SQLException {
//        regionsDao = RegionsDao.getInstance();
//        statesDao = StatesDao.getInstance();
//
//        // Create a test state with a unique name to avoid conflicts
//        testState = States.builder()
//                .stateName("ZZ")
//                .build();
//
//        // Clean up any existing test data
//        try {
//            States existingState = statesDao.getStateByName("ZZ");
//            if (existingState != null) {
//                statesDao.delete(existingState);
//            }
//        } catch (SQLException e) {
//            // Ignore if no test data exists
//        }
//
//        // Create the test state in the database
//        testState = statesDao.create(testState);
//
//        // Create a test region
//        testRegion = Regions.builder()
//                .regionName("TestRegion")
//                .stateId(testState.getStateId())
//                .build();
//    }
//
//    @Test
//    public void testCreate() throws SQLException {
//        Regions createdRegion = regionsDao.create(testRegion);
//
//        assertNotNull(createdRegion);
//        assertTrue(createdRegion.getRegionId() > 0);
//        assertEquals("TestRegion", createdRegion.getRegionName());
//        assertEquals(testState.getStateId(), createdRegion.getStateId());
//
//        regionsDao.delete(createdRegion);
//        statesDao.delete(testState);
//    }
//
//    @Test
//    public void testGetRegionById() throws SQLException {
//        Regions createdRegion = regionsDao.create(testRegion);
//
//        Regions retrievedRegion = regionsDao.getRegionById(createdRegion.getRegionId());
//
//        assertNotNull(retrievedRegion);
//        assertEquals(createdRegion.getRegionId(), retrievedRegion.getRegionId());
//        assertEquals(createdRegion.getRegionName(), retrievedRegion.getRegionName());
//        assertEquals(createdRegion.getStateId(), retrievedRegion.getStateId());
//
//        regionsDao.delete(createdRegion);
//        statesDao.delete(testState);
//    }
//
//    @Test
//    public void testGetRegionsByStateId() throws SQLException {
//        Regions createdRegion = regionsDao.create(testRegion);
//
//        List<Regions> regions = regionsDao.getRegionsByStateId(testState.getStateId());
//
//        assertNotNull(regions);
//        assertFalse(regions.isEmpty());
//        assertTrue(regions.stream().anyMatch(r ->
//                r.getRegionId() == createdRegion.getRegionId() &&
//                r.getRegionName().equals(createdRegion.getRegionName())));
//
//        regionsDao.delete(createdRegion);
//        statesDao.delete(testState);
//    }
//
//    @Test
//    public void testGetAllRegions() throws SQLException {
//        Regions createdRegion = regionsDao.create(testRegion);
//
//        List<Regions> regions = regionsDao.getAllRegions();
//
//        assertNotNull(regions);
//        assertFalse(regions.isEmpty());
//        assertTrue(regions.stream().anyMatch(r ->
//                r.getRegionId() == createdRegion.getRegionId() &&
//                r.getRegionName().equals(createdRegion.getRegionName())));
//
//        regionsDao.delete(createdRegion);
//        statesDao.delete(testState);
//    }
//
//    @Test
//    public void testUpdateRegion() throws SQLException {
//        Regions createdRegion = regionsDao.create(testRegion);
//
//        createdRegion.setRegionName("UpdatedRegion");
//        Regions updatedRegion = regionsDao.updateRegion(createdRegion);
//
//        assertNotNull(updatedRegion);
//        assertEquals("UpdatedRegion", updatedRegion.getRegionName());
//
//        Regions retrievedRegion = regionsDao.getRegionById(createdRegion.getRegionId());
//        assertEquals("UpdatedRegion", retrievedRegion.getRegionName());
//
//        regionsDao.delete(createdRegion);
//        statesDao.delete(testState);
//    }
//
//    @Test
//    public void testDelete() throws SQLException {
//        Regions createdRegion = regionsDao.create(testRegion);
//
//        regionsDao.delete(createdRegion);
//
//        Regions retrievedRegion = regionsDao.getRegionById(createdRegion.getRegionId());
//        assertNull(retrievedRegion);
//
//        statesDao.delete(testState);
//    }
//}
