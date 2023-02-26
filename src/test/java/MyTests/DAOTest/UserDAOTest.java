package MyTests.DAOTest;

import famMapServer.dataAccess.Database;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.dataAccess.UserDAO;
import models.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private Database db;
    private User bestUser;
    private User bestUser2;
    private User bestUser3;
    private UserDAO uDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        bestUser = new User("amaks007","ladyKillers11","austinmckamey007@gmail.com",
                "Austin","McKamey","m","12345");
        bestUser2 = new User("jcDancer","MasterChef21","jen.k.mckamey@gmail.com",
                "Jennifer","McKamey","f","12346");
        bestUser3 = new User("feltSt","aptiveIsBest","steven.felt@gmail.com",
                "Stevie","Felt","m","10045");
        Connection conn = db.getConnection();
        db.clearTables();
        uDao = new UserDAO(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        uDao.insert(bestUser);

        User compareUser = uDao.find(bestUser.getUsername());

        assertNotNull(compareUser);
        assertEquals(bestUser,compareUser);
    }

    @Test
    public void insertFail() throws DataAccessException {
        uDao.insert(bestUser);

        assertThrows(DataAccessException.class, ()-> uDao.insert(bestUser));
    }

    @Test
    public void findPass() throws DataAccessException {
        uDao.insert(bestUser);
        uDao.insert(bestUser2);
        uDao.insert(bestUser3);

        User compareUser = uDao.find(bestUser2.getUsername());

        assertNotNull(compareUser);
        assertEquals(bestUser2,compareUser);
    }

    @Test
    public void findFail() throws DataAccessException {
        uDao.insert(bestUser);
        uDao.insert(bestUser2);
        uDao.insert(bestUser3);

        assertNull(uDao.find("Master67"));
    }

    @Test
    public void clearPass() throws DataAccessException {
        uDao.insert(bestUser);
        uDao.insert(bestUser2);
        uDao.insert(bestUser3);

        uDao.clear();

        assertNull(uDao.find(bestUser.getUsername()));
        assertNull(uDao.find(bestUser2.getUsername()));
        assertNull(uDao.find(bestUser3.getUsername()));
    }

    @Test
    public void clearPass2() throws DataAccessException {
        uDao.insert(bestUser);
        uDao.insert(bestUser2);

        uDao.clear();

        assertNull(uDao.find(bestUser.getUsername()));
        assertNull(uDao.find(bestUser2.getUsername()));

        uDao.insert(bestUser3);

        uDao.clear();

        assertNull(uDao.find(bestUser3.getUsername()));
    }
}
