package MyTests.ServicesTest;

import famMapServer.dataAccess.*;
import models.AuthToken;
import models.Event;
import models.Person;
import models.User;
import famMapServer.services.ClearService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private ClearService service;

    PersonDAO pDao;
    AuthTokenDAO aDao;
    EventDAO eDao;
    UserDAO uDao;

    private Person person1;
    private Person person2;

    private Event event1;
    private Event event2;

    private User user1;
    private User user2;

    private AuthToken token1;
    private AuthToken token2;

    @BeforeEach
    public void setUp() throws DataAccessException {

        person1 = new Person("12345","amaks007","Austin","McKamey",
                "m",null,null,null);
        person2 = new Person("23452","amaks007","Kyle","McKamey",
                "m",null,null,null);

        event1 = new Event("68793","amaks007","12345",(float)34.56,(float)65.78,"USA",
                "Bozeman","Birth",2000);
        event2 = new Event("34523","amaks007","12345",(float)34.56,(float)65.78,"USA",
                "Helena","Front flip",2003);

        user1 = new User("amaks007","masterchef","austinmckamey@gmail.com",
                "Austin","McKamey","m","12345");
        user2 = new User("feltSt","woohoohh","seteenfetl@gmail.com",
                "Steven","Felt","m","23645");

        token1 = new AuthToken("goodtoke","amaks007");
        token2 = new AuthToken("greattok", "feltSt");

        Database db = new Database();
        db.openConnection();
        db.clearTables();

        pDao = new PersonDAO(db.getConnection());
        aDao = new AuthTokenDAO(db.getConnection());
        eDao = new EventDAO(db.getConnection());
        uDao = new UserDAO(db.getConnection());

        pDao.insert(person1);
        pDao.insert(person2);

        eDao.insert(event1);
        eDao.insert(event2);

        uDao.insert(user1);
        uDao.insert(user2);

        aDao.insert(token1);
        aDao.insert(token2);

        db.closeConnection(true);

        service = new ClearService();
    }

    @Test
    public void clearPass() throws DataAccessException {
        service.clear();

        Database db = new Database();
        db.openConnection();
        pDao = new PersonDAO(db.getConnection());
        aDao = new AuthTokenDAO(db.getConnection());
        eDao = new EventDAO(db.getConnection());
        uDao = new UserDAO(db.getConnection());

        assertNull(pDao.find(person1.getPersonID()));
        assertNull(aDao.find(token1.getAuthToken()));
        assertNull(eDao.find(event1.getEventID()));
        assertNull(uDao.find(user1.getUsername()));

        db.closeConnection(false);
    }

    @Test
    public void clearPass2() throws DataAccessException {
        service.clear();

        Database db = new Database();
        db.openConnection();
        pDao = new PersonDAO(db.getConnection());
        aDao = new AuthTokenDAO(db.getConnection());
        eDao = new EventDAO(db.getConnection());
        uDao = new UserDAO(db.getConnection());

        assertNull(pDao.find(person2.getPersonID()));
        assertNull(aDao.find(token2.getAuthToken()));
        assertNull(eDao.find(event2.getEventID()));
        assertNull(uDao.find(user2.getUsername()));

        db.closeConnection(false);
    }
}
