package famMapServer.services;

import famMapServer.dataAccess.AuthTokenDAO;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.dataAccess.Database;
import famMapServer.dataAccess.PersonDAO;
import models.AuthToken;
import models.Person;
import results.FamilyResult;
import results.PersonResult;

import java.util.ArrayList;

/**
 * Interacts with handler class and database for person
 */
public class PersonService {

    /**
     * Creates new service object to get person or all family members
     */
    public PersonService() {

    }

    /**
     * Finds person specified by ID
     * @param personID Identifier of person
     * @param token Authorized token for current user session
     * @return Person specified by ID
     */
    public PersonResult getPerson(String personID, String token) throws DataAccessException {
        Database db = new Database();
        try {
            db.openConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());
            PersonDAO pDao = new PersonDAO(db.getConnection());

            AuthToken authToken = aDao.find(token);
            if(authToken == null) {
                db.closeConnection(false);
                return new PersonResult(false, "error: Authorization token is invalid");
            }

            Person person = pDao.find(personID);
            if(!person.getAssociatedUser().equals(authToken.getUsername())) {
                db.closeConnection(false);
                return new PersonResult(false, "error: Person does not belong to current User");
            }
            db.closeConnection(true);
            return new PersonResult(true,person.getAssociatedUser(),personID,person.getFirstName(),
                    person.getLastName(),person.getGender(),person.getFatherID(),person.getMotherID(),person.getSpouseID());
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new PersonResult(false, "error: Internal server error");
        }
    }

    /**
     * Finds all family members of current user
     * @param token Authorized token for current user session
     * @return Array of all family members of current user
     */
    public FamilyResult getFamilyMembers(String token) throws DataAccessException {
        Database db = new Database();
        try {
            db.openConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());
            PersonDAO pDao = new PersonDAO(db.getConnection());

            AuthToken authToken = aDao.find(token);
            if(authToken == null) {
                db.closeConnection(false);
                return new FamilyResult(false, "error: Authorization token is invalid");
            }

            ArrayList<Person> persons = pDao.getFamilyMembers(authToken.getUsername());
            db.closeConnection(true);
            return new FamilyResult(true,persons);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new FamilyResult(false, "error: Internal server error");
        }
    }
}
