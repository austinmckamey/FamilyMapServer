package MyTests.ServicesTest;

import famMapServer.dataAccess.*;
import models.AuthToken;
import models.Event;
import famMapServer.services.EventService;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EventServiceTest {

    private EventService service;

    private Event event1;
    private Event event2;
    private Event event3;
    private Event event4;
    private Event event5;

    private AuthToken token1;
    private AuthToken token2;

    @BeforeEach
    public void setUp() throws DataAccessException {

        event1 = new Event("68793","amaks007","12345",(float)34.56,(float)65.78,"USA",
                "Bozeman","Birth",2000);
        event2 = new Event("34523","amaks007","12345",(float)34.56,(float)65.78,"USA",
                "Helena","Front flip",2003);
        event3 = new Event("45747","amaks007","12345",(float)34.56,(float)65.78,"USA",
                "Spanish Fork","Camping",2011);
        event4 = new Event("68568","feltSt","56744",(float)34.56,(float)65.78,"USA",
                "Orem","Birth",2001);
        event5 = new Event("12346","feltSt","56744",(float)34.56,(float)65.78,"USA",
                "Provo","Death",2110);

        token1 = new AuthToken("goodtoke","amaks007");
        token2 = new AuthToken("greattok", "feltSt");

        Database db = new Database();
        db.openConnection();
        db.clearTables();

        EventDAO eDao = new EventDAO(db.getConnection());
        AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());

        eDao.insert(event1);
        eDao.insert(event2);
        eDao.insert(event3);
        eDao.insert(event4);
        eDao.insert(event5);

        aDao.insert(token1);
        aDao.insert(token2);

        db.closeConnection(true);

        service = new EventService();
    }

    @Test
    public void getEventPass() throws DataAccessException {
        assertEquals(event1.getEventID(), service.getEvent(event1.getEventID(),token1.getAuthToken()).getEventID());
        assertEquals(event2.getEventID(), service.getEvent(event2.getEventID(),token1.getAuthToken()).getEventID());
        assertEquals(event4.getEventID(), service.getEvent(event4.getEventID(),token2.getAuthToken()).getEventID());
    }

    @Test
    public void getEventFail() throws DataAccessException {
        assertNull(service.getEvent(event3.getEventID(),token2.getAuthToken()).getEventID());
        assertNull(service.getEvent(event5.getEventID(),token1.getAuthToken()).getEventID());
    }

    @Test
    public void getUserFamilyEventsPass() throws DataAccessException {
        ArrayList<Event> events = service.getUserFamilyEvents(token1.getAuthToken()).getData();
        assertNotEquals(0, events.size());
        for(Event event : events) {
            assertEquals(token1.getUsername(), event.getAssociatedUsername());
        }
    }

    @Test
    public void getUserFamilyEventsFail() throws DataAccessException {
        assertNull(service.getUserFamilyEvents("badtoken").getData());
    }
}
