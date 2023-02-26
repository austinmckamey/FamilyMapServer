package famMapServer.services;

import famMapServer.dataAccess.*;
import models.Event;
import models.Person;
import models.User;
import requests.LoadRequest;
import results.LoadResult;

/**
 * Interacts with handler class and database for load
 */
public class LoadService {

    /**
     * Creates new service object to clear database and populate with data from request body
     */
    public LoadService() {

    }

    /**
     * Clears database and fills with specified data
     *
     * @param l Arrays of users, persons, and events to add to database
     * @return LoadResult object with number of persons and events added
     */
    public LoadResult load(LoadRequest l) throws DataAccessException {
        Database db = new Database();
        try {
            db.openConnection();
            db.clearTables();
            EventDAO eDao = new EventDAO(db.getConnection());
            PersonDAO pDao = new PersonDAO(db.getConnection());
            UserDAO uDao = new UserDAO(db.getConnection());

            for (Event event : l.getEvents()) {
                if (event.getEventID() == null |
                        event.getAssociatedUsername() == null |
                        event.getPersonID() == null |
                        event.getCountry() == null |
                        event.getCity() == null |
                        event.getEventType() == null) {
                    db.closeConnection(false);
                    return new LoadResult(false, "Invalid values in Event objects");
                } else {
                    eDao.insert(event);
                }
            }
            for (Person person : l.getPersons()) {
                if (person.getPersonID() == null |
                        person.getAssociatedUser() == null |
                        person.getFirstName() == null |
                        person.getLastName() == null |
                        (!person.getGender().equals("f") &&
                                !person.getGender().equals("m"))) {
                    db.closeConnection(false);
                    return new LoadResult(false, "Invalid values in Person objects");
                } else {
                    pDao.insert(person);
                }
            }
            for (User user : l.getUsers()) {
                if (user.getUsername() == null |
                        user.getPassword() == null |
                        user.getEmail() == null |
                        user.getFirstName() == null |
                        user.getLastName() == null |
                        user.getPersonID() == null |
                        (!user.getGender().equals("f") &&
                                !user.getGender().equals("m"))) {
                    db.closeConnection(false);
                    return new LoadResult(false, "Invalid values in User objects");
                } else {
                    uDao.insert(user);
                }
            }

            db.closeConnection(true);

            int x = l.getUsers().size();
            int y = l.getPersons().size();
            int z = l.getEvents().size();
            return new LoadResult(true, "Successfully added " + x +
                    " users, " + y + " persons, and " + z + " events to the database.");
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new LoadResult(false, "Internal server error");
        }
    }
}
