package MyTests.ServicesTest;

import famMapServer.dataAccess.*;
import models.Event;
import models.Person;
import models.User;

import famMapServer.services.FillService;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FillServiceTest {

    private FillService service;

    PersonDAO pDao;
    EventDAO eDao;
    UserDAO uDao;

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() throws DataAccessException {

        user1 = new User("amaks007","masterchef","austinmckamey@gmail.com",
                "Austin","McKamey","m","12345");
        user2 = new User("feltSt","woohoohh","seteenfetl@gmail.com",
                "Steven","Felt","m","23645");

        Database db = new Database();
        db.openConnection();
        db.clearTables();

        uDao = new UserDAO(db.getConnection());

        uDao.insert(user1);
        uDao.insert(user2);

        db.closeConnection(true);

        service = new FillService();
    }

    @Test
    public void fillPass() throws DataAccessException {
        service.fill(user1.getUsername(), 4);

        Database db = new Database();
        db.openConnection();

        pDao = new PersonDAO(db.getConnection());
        eDao = new EventDAO(db.getConnection());

        ArrayList<Event> events = eDao.getAllEvents(user1.getUsername());
        for(Event event : events) {
            assertNotNull(event.getEventID());
        }
        ArrayList<Person> family = pDao.getFamilyMembers(user1.getUsername());
        for(Person person : family) {
            assertNotNull(person.getPersonID());
        }

        db.closeConnection(false);
    }

    @Test
    public void fillFail() throws DataAccessException {
        assertFalse(service.fill(user1.getUsername(), -5).success);

        service.fill(user2.getUsername(), 4);

        Database db = new Database();
        db.openConnection();

        pDao = new PersonDAO(db.getConnection());
        eDao = new EventDAO(db.getConnection());

        ArrayList<Event> events = eDao.getAllEvents(user1.getUsername());
        for(Event event : events) {
            assertNull(event.getEventID());
        }
        ArrayList<Person> family = pDao.getFamilyMembers(user1.getUsername());
        for(Person person : family) {
            assertNull(person.getPersonID());
        }

        db.closeConnection(false);
    }
}
