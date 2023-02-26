package MyTests.ServicesTest;

import famMapServer.dataAccess.AuthTokenDAO;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.dataAccess.Database;
import famMapServer.dataAccess.UserDAO;
import models.User;
import famMapServer.services.LoginService;
import requests.LoginRequest;
import results.LoginResult;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {

    private LoginService service;
    private LoginRequest request;
    private Database db;

    UserDAO uDao;
    AuthTokenDAO aDao;

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        db.openConnection();
        db.clearTables();

        user1 = new User("amaks007","masterchef","austinmckamey@gmail.com",
                "Austin","McKamey","m","12345");
        user2 = new User("feltSt","woohoohh","seteenfetl@gmail.com",
                "Steven","Felt","m","23645");

        uDao = new UserDAO(db.getConnection());
        uDao.insert(user1);
        uDao.insert(user2);

        request = new LoginRequest("amaks007","masterchef");

        db.closeConnection(true);

        service = new LoginService();
    }

    @Test
    public void loginPass() throws DataAccessException {
        LoginResult result = service.login(request);

        assertTrue(result.success);

        db = new Database();
        db.openConnection();

        aDao = new AuthTokenDAO(db.getConnection());

        assertNotNull(aDao.find(result.getAuthToken()).getAuthToken());
        assertEquals("amaks007",aDao.find(result.getAuthToken()).getUsername());

        db.closeConnection(false);
    }

    @Test
    public void loginFail() throws DataAccessException {
        request = new LoginRequest("amaks007","whoknows");
        LoginResult result = service.login(request);

        assertFalse(result.success);
        assertEquals("error: Password incorrect", result.message);

        request = new LoginRequest("notreal","whoknows");
        result = service.login(request);

        assertFalse(result.success);
        assertEquals("error: User not found in database", result.message);
    }
}
