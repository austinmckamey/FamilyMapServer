package MyTests.ServicesTest;

import famMapServer.dataAccess.*;
import models.Event;
import models.Person;
import models.User;
import famMapServer.services.LoadService;
import requests.LoadRequest;
import results.LoadResult;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class LoadServiceTest {

    private LoadService service;
    private LoadRequest request;
    private Database db;

    ArrayList<Person> people;
    ArrayList<Event> events;
    ArrayList<User> users;

    PersonDAO pDao;
    EventDAO eDao;
    UserDAO uDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        people = new ArrayList<>();
        events = new ArrayList<>();
        users = new ArrayList<>();

        Person person1 = new Person("12345", "amaks007", "Austin", "McKamey",
                "m", null, null, null);
        Person person2 = new Person("23452", "amaks007", "Kyle", "McKamey",
                "m", null, null, null);

        Event event1 = new Event("68793", "amaks007", "12345", (float) 34.56, (float) 65.78, "USA",
                "Bozeman", "Birth", 2000);
        Event event2 = new Event("34523", "amaks007", "12345", (float) 34.56, (float) 65.78, "USA",
                "Helena", "Front flip", 2003);

        User user1 = new User("amaks007", "masterchef", "austinmckamey@gmail.com",
                "Austin", "McKamey", "m", "12345");
        User user2 = new User("feltSt", "woohoohh", "seteenfetl@gmail.com",
                "Steven", "Felt", "m", "23645");

        people.add(person1);
        people.add(person2);

        events.add(event1);
        events.add(event2);

        users.add(user1);
        users.add(user2);

        request = new LoadRequest(users,people,events);

        db = new Database();
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);

        service = new LoadService();
    }

    @Test
    public void loadPass() throws DataAccessException {
        LoadResult result = service.load(request);

        assertTrue(result.success);

        db = new Database();
        db.openConnection();

        pDao = new PersonDAO(db.getConnection());
        eDao = new EventDAO(db.getConnection());
        uDao = new UserDAO(db.getConnection());

        assertNotNull(pDao.find("12345").getPersonID());
        assertNotNull(eDao.find("68793").getEventID());
        assertNotNull(uDao.find("amaks007").getUsername());

        db.closeConnection(false);
    }

    @Test
    public void loadFail() throws DataAccessException {
        people = new ArrayList<>();
        events = new ArrayList<>();
        users = new ArrayList<>();

        Person person1 = new Person(null, "amaks007", "Austin", "McKamey",
                "m", null, null, null);
        Person person2 = new Person("23452", "amaks007", "Kyle", "McKamey",
                "m", null, null, null);

        people.add(person1);
        people.add(person2);

        request = new LoadRequest(users,people,events);

        assertFalse(service.load(request).success);
    }
}

