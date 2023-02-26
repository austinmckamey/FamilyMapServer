package MyTests.DAOTest;

import famMapServer.dataAccess.Database;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.dataAccess.PersonDAO;
import models.Person;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PersonDAOTest {
    private Database db;
    private Person bestPerson;
    private Person bestPerson2;
    private Person bestPerson3;
    private Person familyMember1;
    private Person familyMember2;
    private PersonDAO pDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();
        bestPerson = new Person("12345","amaks007","Austin","McKamey",
                "m","12344","12343","12346");
        bestPerson2 = new Person("12346","jcDancer","Jennifer","McKamey",
                "f","23456","23455","12345");
        bestPerson3 = new Person("10045","feltSt","Stevie","Felt",
                "m","10043","10042",null);
        familyMember1 = new Person("25345","amaks007","Rachelle","McKamey",
                "f","12344","12343",null);
        familyMember2 = new Person("24567","amaks007","Kyle","McKamey",
                "m","12344","12343",null);
        Connection conn = db.getConnection();
        db.clearTables();
        pDao = new PersonDAO(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        pDao.insert(bestPerson);

        Person comparePerson = pDao.find(bestPerson.getPersonID());

        assertNotNull(comparePerson);
        assertEquals(bestPerson,comparePerson);
    }

    @Test
    public void insertFail() throws DataAccessException {
        pDao.insert(bestPerson);

        assertThrows(DataAccessException.class, ()-> pDao.insert(bestPerson));
    }

    @Test
    public void findPass() throws DataAccessException {
        pDao.insert(bestPerson);
        pDao.insert(bestPerson2);
        pDao.insert(bestPerson3);

        Person comparePerson = pDao.find(bestPerson2.getPersonID());

        assertNotNull(comparePerson);
        assertEquals(bestPerson2,comparePerson);
    }

    @Test
    public void findFail() throws DataAccessException {
        pDao.insert(bestPerson);
        pDao.insert(bestPerson2);
        pDao.insert(bestPerson3);

        assertNull(pDao.find("00000"));
    }

    @Test
    public void clearPass() throws DataAccessException {
        pDao.insert(bestPerson);
        pDao.insert(bestPerson2);
        pDao.insert(bestPerson3);

        pDao.clear();

        assertNull(pDao.find(bestPerson.getPersonID()));
        assertNull(pDao.find(bestPerson2.getPersonID()));
        assertNull(pDao.find(bestPerson3.getPersonID()));
    }

    @Test
    public void clearPass2() throws DataAccessException {
        pDao.insert(bestPerson);
        pDao.insert(bestPerson2);

        pDao.clear();

        assertNull(pDao.find(bestPerson.getPersonID()));
        assertNull(pDao.find(bestPerson2.getPersonID()));

        pDao.insert(bestPerson3);

        pDao.clear();

        assertNull(pDao.find(bestPerson3.getPersonID()));
    }

    @Test
    public void getFamilyMembersPass() throws DataAccessException {
        pDao.insert(bestPerson);
        pDao.insert(bestPerson2);
        pDao.insert(bestPerson3);
        pDao.insert(familyMember1);
        pDao.insert(familyMember2);

        ArrayList<Person> family = pDao.getFamilyMembers(bestPerson.getAssociatedUser());
        for(Person person : family) {
            assertEquals(bestPerson.getAssociatedUser(), person.getAssociatedUser());
        }
    }

    @Test
    public void getFamilyMembersFail() throws DataAccessException {
        pDao.insert(bestPerson);
        pDao.insert(bestPerson2);
        pDao.insert(familyMember1);
        pDao.insert(familyMember2);

        assertEquals(0, pDao.getFamilyMembers(bestPerson3.getAssociatedUser()).size());
    }

    @Test
    public void deletePass() throws DataAccessException {
        pDao.insert(bestPerson);
        pDao.insert(bestPerson2);
        pDao.insert(bestPerson3);
        pDao.insert(familyMember1);
        pDao.insert(familyMember2);

        pDao.delete(bestPerson.getPersonID());

        assertNull(pDao.find(bestPerson.getPersonID()));
    }

    @Test
    public void deleteFail() throws DataAccessException {
        pDao.insert(bestPerson);
        pDao.insert(bestPerson2);
        pDao.insert(bestPerson3);
        pDao.insert(familyMember1);
        pDao.insert(familyMember2);

        pDao.delete("00000");

        assertNotNull(bestPerson);
        assertNotNull(bestPerson2);
        assertNotNull(bestPerson3);
        assertNotNull(familyMember1);
        assertNotNull(familyMember2);
    }
}
