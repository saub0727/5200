package car.genie.server.dal;

import car.genie.server.model.States;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StatesDaoTest {
    private StatesDao statesDao;
    private States testState;

    @BeforeEach
    public void setUp() throws SQLException {
        statesDao = StatesDao.getInstance();

        testState = States.builder()
                .stateName("ZZ")
                .build();

        try {
            States existingState = statesDao.getStateByName("ZZ");
            if (existingState != null) {
                statesDao.delete(existingState);
            }
        } catch (SQLException e) {

        }
    }

    @Test
    public void testCreate() throws SQLException {
        States createdState = statesDao.create(testState);
        assertNotNull(createdState);
        assertTrue(createdState.getStateId() > 0);
        assertEquals("ZZ", createdState.getStateName());
        statesDao.delete(createdState);
    }

    @Test
    public void testGetStateById() throws SQLException {
        States createdState = statesDao.create(testState);

        States retrievedState = statesDao.getStateById(createdState.getStateId());

        assertNotNull(retrievedState);
        assertEquals(createdState.getStateId(), retrievedState.getStateId());
        assertEquals(createdState.getStateName(), retrievedState.getStateName());

        statesDao.delete(createdState);
    }

    @Test
    public void testGetStateByName() throws SQLException {
        States createdState = statesDao.create(testState);
        
        States retrievedState = statesDao.getStateByName("ZZ");
        
        assertNotNull(retrievedState);
        assertEquals(createdState.getStateId(), retrievedState.getStateId());
        assertEquals(createdState.getStateName(), retrievedState.getStateName());
        
        statesDao.delete(createdState);
    }

    @Test
    public void testGetAllStates() throws SQLException {
        States createdState = statesDao.create(testState);
        
        List<States> states = statesDao.getAllStates();
        
        assertNotNull(states);
        assertFalse(states.isEmpty());
        assertTrue(states.stream().anyMatch(s -> s.getStateName().equals("ZZ")));
        
        statesDao.delete(createdState);
    }

    @Test
    public void testUpdateState() throws SQLException {
        States createdState = statesDao.create(testState);
        
        // Update the state
        createdState.setStateName("YY");
        States updatedState = statesDao.updateState(createdState);
        
        // Verify the update
        assertNotNull(updatedState);
        assertEquals("YY", updatedState.getStateName());
        
        // Verify by retrieving again
        States retrievedState = statesDao.getStateById(createdState.getStateId());
        assertEquals("YY", retrievedState.getStateName());
        
        // Clean up
        statesDao.delete(createdState);
    }

    @Test
    public void testDelete() throws SQLException {
        // Create a new state
        States createdState = statesDao.create(testState);
        
        // Delete the state
        statesDao.delete(createdState);
        
        // Verify the state was deleted
        States retrievedState = statesDao.getStateById(createdState.getStateId());
        assertNull(retrievedState);
    }
}
