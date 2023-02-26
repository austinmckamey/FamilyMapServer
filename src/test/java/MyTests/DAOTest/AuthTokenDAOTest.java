package MyTests.DAOTest;

import famMapServer.dataAccess.AuthTokenDAO;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.dataAccess.Database;
import models.AuthToken;
import org.junit.jupiter.api.*;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTokenDAOTest {
    private Database db;
    private AuthToken bestToken;
    private AuthToken bestToken2;
    private AuthToken bestToken3;
    private AuthTokenDAO aDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        bestToken = new AuthToken("gwe3453n23412","amaks007");
        bestToken2 = new AuthToken("frt2150h23400","jcDancer");
        bestToken3 = new AuthToken("ase1241g26786","feltSt");
        Connection conn = db.getConnection();
        db.clearTables();
        aDao = new AuthTokenDAO(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        aDao.insert(bestToken);

        AuthToken comparePerson = aDao.find(bestToken.getAuthToken());

        assertNotNull(comparePerson);
        assertEquals(bestToken,comparePerson);
    }

    @Test
    public void insertFail() throws DataAccessException {
        aDao.insert(bestToken);

        assertThrows(DataAccessException.class, ()-> aDao.insert(bestToken));
    }

    @Test
    public void findPass() throws DataAccessException {
        aDao.insert(bestToken);
        aDao.insert(bestToken2);
        aDao.insert(bestToken3);

        AuthToken comparePerson = aDao.find(bestToken2.getAuthToken());

        assertNotNull(comparePerson);
        assertEquals(bestToken2,comparePerson);
    }

    @Test
    public void findFail() throws DataAccessException {
        aDao.insert(bestToken);
        aDao.insert(bestToken2);
        aDao.insert(bestToken3);

        assertNull(aDao.find("hkjl1243j42354"));
    }

    @Test
    public void clearPass() throws DataAccessException {
        aDao.insert(bestToken);
        aDao.insert(bestToken2);
        aDao.insert(bestToken3);

        aDao.clear();

        assertNull(aDao.find(bestToken.getAuthToken()));
        assertNull(aDao.find(bestToken2.getAuthToken()));
        assertNull(aDao.find(bestToken3.getAuthToken()));
    }

    @Test
    public void clearPass2() throws DataAccessException {
        aDao.insert(bestToken);
        aDao.insert(bestToken2);

        aDao.clear();

        assertNull(aDao.find(bestToken.getAuthToken()));
        assertNull(aDao.find(bestToken2.getAuthToken()));

        aDao.insert(bestToken3);

        aDao.clear();

        assertNull(aDao.find(bestToken3.getAuthToken()));
    }
}
