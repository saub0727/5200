package car.genie.server.dal;

import car.genie.server.model.Locations;
import car.genie.server.model.Vehicles;
import car.genie.server.model.Models;
import car.genie.server.model.Manufacturers;
import car.genie.server.model.Regions;
import car.genie.server.model.States;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LocationsDaoTest {

    private LocationsDao locationsDao;
    private VehiclesDao vehiclesDao;
    private ModelsDao modelsDao;
    private ManufacturersDao manufacturersDao;
    private RegionsDao regionsDao;
    private StatesDao statesDao;
    private Manufacturers testManufacturer;
    private Models testModel;
    private Vehicles testVehicle;
    private States testState;
    private Regions testRegion;
    private Locations testLocation;

    @BeforeEach
    public void setUp() throws SQLException {
        locationsDao = LocationsDao.getInstance();
        vehiclesDao = VehiclesDao.getInstance();
        modelsDao = ModelsDao.getInstance();
        manufacturersDao = ManufacturersDao.getInstance();
        regionsDao = RegionsDao.getInstance();
        statesDao = StatesDao.getInstance();

        testManufacturer = Manufacturers.builder()
                .manufacturerName("TestManufacturer")
                .build();
        testManufacturer = manufacturersDao.create(testManufacturer);
        assertNotNull(testManufacturer.getManufacturerId(), "Manufacturer ID should not be null");

        testModel = Models.builder()
                .modelName("TestModel")
                .manufacturerId(testManufacturer.getManufacturerId())
                .build();
        testModel = modelsDao.create(testModel);
        assertNotNull(testModel.getModelId(), "Model ID should not be null");

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

        testState = States.builder()
                .stateName("TX")
                .build();
        testState = statesDao.create(testState);
        assertNotNull(testState.getStateId(), "State ID should not be null");

        testRegion = Regions.builder()
                .regionName("TestRegion")
                .stateId(testState.getStateId())
                .build();
        testRegion = regionsDao.create(testRegion);
        assertNotNull(testRegion.getRegionId(), "Region ID should not be null");

        testLocation = Locations.builder()
                .vehicleId(testVehicle.getVehicleId())
                .latitude(30.2672f)
                .longitude(-97.7431f)
                .regionId(testRegion.getRegionId())
                .build();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try {
            Locations location = locationsDao.getLocationByVehicleId(testVehicle.getVehicleId());
            if (location != null) {
                locationsDao.delete(location);
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

        if (testRegion != null) {
            regionsDao.delete(testRegion);
        }

        if (testState != null) {
            statesDao.delete(testState);
        }
    }

    @Test
    public void testCreate() throws SQLException {
        Locations createdLocation = locationsDao.create(testLocation);
        
        assertNotNull(createdLocation);
        assertEquals(testVehicle.getVehicleId(), createdLocation.getVehicleId());
        assertEquals(30.2672f, createdLocation.getLatitude(), 0.0001);
        assertEquals(-97.7431f, createdLocation.getLongitude(), 0.0001);
        assertEquals(testRegion.getRegionId(), createdLocation.getRegionId());
    }

    @Test
    public void testGetLocationByVehicleId() throws SQLException {
        locationsDao.create(testLocation);
        
        Locations retrievedLocation = locationsDao.getLocationByVehicleId(testVehicle.getVehicleId());
        
        assertNotNull(retrievedLocation);
        assertEquals(testVehicle.getVehicleId(), retrievedLocation.getVehicleId());
        assertEquals(30.2672f, retrievedLocation.getLatitude(), 0.0001);
        assertEquals(-97.7431f, retrievedLocation.getLongitude(), 0.0001);
        assertEquals(testRegion.getRegionId(), retrievedLocation.getRegionId());
    }

    @Test
    public void testGetLocationsByRegionId() throws SQLException {
        locationsDao.create(testLocation);
        
        List<Locations> locationsList = locationsDao.getLocationsByRegionId(testRegion.getRegionId());
        
        assertNotNull(locationsList);
        assertFalse(locationsList.isEmpty());
        assertTrue(locationsList.stream().anyMatch(l -> 
                l.getVehicleId() == testVehicle.getVehicleId() && 
                l.getRegionId() == testRegion.getRegionId()));
    }

    @Test
    public void testUpdateLocation() throws SQLException {
        locationsDao.create(testLocation);
        
        testLocation.setLatitude(29.7604f);
        testLocation.setLongitude(-95.3698f);
        
        Locations updatedLocation = locationsDao.updateLocation(testLocation);
        
        assertNotNull(updatedLocation);
        assertEquals(29.7604f, updatedLocation.getLatitude(), 0.0001);
        assertEquals(-95.3698f, updatedLocation.getLongitude(), 0.0001);
        
        Locations retrievedLocation = locationsDao.getLocationByVehicleId(testVehicle.getVehicleId());
        assertEquals(29.7604f, retrievedLocation.getLatitude(), 0.0001);
        assertEquals(-95.3698f, retrievedLocation.getLongitude(), 0.0001);
    }

    @Test
    public void testDelete() throws SQLException {
        locationsDao.create(testLocation);
        
        locationsDao.delete(testLocation);
        
        Locations retrievedLocation = locationsDao.getLocationByVehicleId(testVehicle.getVehicleId());
        assertNull(retrievedLocation);
    }
}
