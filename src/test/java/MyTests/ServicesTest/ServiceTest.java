package MyTests.ServicesTest;

import famMapServer.services.Service;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {

    private Service service;

    @BeforeEach
    public void setUp() {
        service = new Service();
    }

    @Test
    public void createTokenPass() {
        String token1 = service.createToken();
        String token2 = service.createToken();

        assertNotEquals(token1,token2);
    }

    @Test
    public void createTokenPass2() {
        assertNotNull(service.createToken());
    }
}
