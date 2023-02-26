package MyTests.DAOTest;

import famMapServer.dataAccess.Database;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.dataAccess.EventDAO;
import models.Event;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EventDAOTest {
    private Database db;
    private Event bestEvent;
    private Event bestEvent2;
    private Event bestEvent3;
    private Event groupEvent1;
    private Event groupEvent2;
    private EventDAO eDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        bestEvent = new Event("68793","amaks007","12345",(float)34.56,(float)65.78,"USA",
                "Bozeman","Birth",2000);
        bestEvent2 = new Event("69584","amaks007","12345",(float)74.56,(float)23.78,"USA",
                "Portland","Marriage",2021);
        bestEvent3 = new Event("97053","jcDancer","12346",(float)53.56,(float)98.78,"USA",
                "Sherwood","Birth",2001);
        groupEvent1 = new Event("23513","amaks007","12345",(float)34.56,(float)65.78,"USA",
                "Orem","Exam",2089);
        groupEvent2 = new Event("35474","amaks007","12345",(float)34.56,(float)65.78,"USA",
                "Provo","Death",2045);
        Connection conn = db.getConnection();
        db.clearTables();
        eDao = new EventDAO(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        eDao.insert(bestEvent);

        Event comparePerson = eDao.find(bestEvent.getEventID());

        assertNotNull(comparePerson);
        assertEquals(bestEvent,comparePerson);
    }

    @Test
    public void insertFail() throws DataAccessException {
        eDao.insert(bestEvent);

        assertThrows(DataAccessException.class, ()-> eDao.insert(bestEvent));
    }

    @Test
    public void findPass() throws DataAccessException {
        eDao.insert(bestEvent);
        eDao.insert(bestEvent2);
        eDao.insert(bestEvent3);

        Event comparePerson = eDao.find(bestEvent2.getEventID());

        assertNotNull(comparePerson);
        assertEquals(bestEvent2,comparePerson);
    }

    @Test
    public void findFail() throws DataAccessException {
        eDao.insert(bestEvent);
        eDao.insert(bestEvent2);
        eDao.insert(bestEvent3);

        assertNull(eDao.find("87690"));
    }

    @Test
    public void clearPass() throws DataAccessException {
        eDao.insert(bestEvent);
        eDao.insert(bestEvent2);
        eDao.insert(bestEvent3);

        eDao.clear();

        assertNull(eDao.find(bestEvent.getEventID()));
        assertNull(eDao.find(bestEvent2.getEventID()));
        assertNull(eDao.find(bestEvent3.getEventID()));
    }

    @Test
    public void clearPass2() throws DataAccessException {
        eDao.insert(bestEvent);
        eDao.insert(bestEvent2);

        eDao.clear();

        assertNull(eDao.find(bestEvent.getEventID()));
        assertNull(eDao.find(bestEvent2.getEventID()));

        eDao.insert(bestEvent3);

        eDao.clear();

        assertNull(eDao.find(bestEvent3.getEventID()));
    }

    @Test
    public void getAllEventsPass() throws DataAccessException {
        eDao.insert(bestEvent);
        eDao.insert(bestEvent2);
        eDao.insert(bestEvent3);
        eDao.insert(groupEvent1);
        eDao.insert(groupEvent2);

        ArrayList<Event> events = eDao.getAllEvents(bestEvent.getAssociatedUsername());
        for(Event event : events) {
            assertEquals(bestEvent.getAssociatedUsername(), event.getAssociatedUsername());
        }
    }

    @Test
    public void getAllEventsFail() throws DataAccessException {
        eDao.insert(bestEvent);
        eDao.insert(bestEvent2);
        eDao.insert(groupEvent1);
        eDao.insert(groupEvent2);

        assertEquals(0, eDao.getAllEvents(bestEvent3.getAssociatedUsername()).size());
    }
}