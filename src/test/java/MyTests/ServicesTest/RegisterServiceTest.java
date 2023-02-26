package MyTests.ServicesTest;

import famMapServer.dataAccess.*;
import famMapServer.services.RegisterService;
import requests.RegisterRequest;
import results.RegisterResult;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {

    private RegisterService service;
    private RegisterRequest request;
    private Database db;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
        request = new RegisterRequest("amaks007","masterchef","austin@mckamey.com",
                "Austin","McKamey","m");
        service = new RegisterService();
    }

    @Test
    public void registerPass() throws DataAccessException {
        RegisterResult result = service.register(request);

        db.openConnection();
        UserDAO uDao = new UserDAO(db.getConnection());
        AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());

        assertEquals("amaks007",result.getUsername());
        assertNotNull(uDao.find("amaks007"));
        assertNotNull(aDao.find(result.getAuthToken()));
        assertNotNull(result.getPersonID());

        db.closeConnection(false);
    }

    @Test
    public void registerFail() throws DataAccessException {
        service.register(request);
        RegisterResult result = service.register(request);

        assertFalse(result.success);
        assertEquals("error: Username already taken", result.message);
    }
}
