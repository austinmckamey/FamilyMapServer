package MyTests.ServicesTest;

import famMapServer.dataAccess.AuthTokenDAO;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.dataAccess.Database;
import famMapServer.dataAccess.PersonDAO;
import models.AuthToken;
import models.Person;
import famMapServer.services.PersonService;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PersonServiceTest {

    private PersonService service;

    private Person person1;
    private Person person2;
    private Person person3;
    private Person person4;
    private Person person5;

    private AuthToken token;
    private AuthToken token2;

    @BeforeEach
    public void setUp() throws DataAccessException {

        person1 = new Person("12345","amaks007","Austin","McKamey",
                "m",null,null,null);
        person2 = new Person("23452","amaks007","Kyle","McKamey",
                "m",null,null,null);
        person3 = new Person("45675","amaks007","Jennifer","McKamey",
                "f",null,null,null);
        person4 = new Person("57885","feltSt","Steven","Felt",
                "m",null,null,null);
        person5 = new Person("36345","feltSt","Sheila","Parker",
                "f",null,null,null);

        token = new AuthToken("goodtoke","amaks007");
        token2 = new AuthToken("greattok", "feltSt");

        Database db = new Database();
        db.openConnection();
        db.clearTables();

        PersonDAO pDao = new PersonDAO(db.getConnection());
        AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());

        pDao.insert(person1);
        pDao.insert(person2);
        pDao.insert(person3);
        pDao.insert(person4);
        pDao.insert(person5);

        aDao.insert(token);
        aDao.insert(token2);

        db.closeConnection(true);

        service = new PersonService();
    }

    @Test
    public void getPersonPass() throws DataAccessException {
        assertEquals(person1.getPersonID(), service.getPerson(person1.getPersonID(),token.getAuthToken()).getPersonID());
        assertEquals(person2.getPersonID(), service.getPerson(person2.getPersonID(),token.getAuthToken()).getPersonID());
        assertEquals(person4.getPersonID(), service.getPerson(person4.getPersonID(),token2.getAuthToken()).getPersonID());
    }

    @Test
    public void getPersonFail() throws DataAccessException {
        assertNull(service.getPerson(person3.getPersonID(),token2.getAuthToken()).getPersonID());
        assertNull(service.getPerson(person5.getPersonID(),token.getAuthToken()).getPersonID());
    }

    @Test
    public void getFamilyMembersPass() throws DataAccessException {
        ArrayList<Person> family = service.getFamilyMembers(token.getAuthToken()).getData();
        assertNotEquals(0, family.size());
        for(Person person : family) {
            assertEquals(token.getUsername(), person.getAssociatedUser());
        }
    }

    @Test
    public void getFamilyMembersFail() throws DataAccessException {
        assertNull(service.getFamilyMembers("badtoken").getData());
    }
}
