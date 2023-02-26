package famMapServer.services;

import famMapServer.dataAccess.*;
import models.AuthToken;
import models.Event;
import results.AllEventsResult;
import results.EventResult;

import java.util.ArrayList;

/**
 * Interacts with handler class and database for event
 */
public class EventService {

    /**
     * Creates new service object to get event or all events of family members
     */
    public EventService() {

    }

    /**
     * Finds event specified by ID
     * @param eventID Identifier of current event
     * @param token Authorized token for current user session
     * @return Event specified by ID
     */
    public EventResult getEvent(String eventID, String token) throws DataAccessException {
        Database db = new Database();
        try {
            db.openConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());
            EventDAO eDao = new EventDAO(db.getConnection());

            AuthToken authToken = aDao.find(token);
            if(authToken == null) {
                db.closeConnection(false);
                return new EventResult(false, "error: Authorization token is invalid");
            }

            Event event = eDao.find(eventID);
            if(!event.getAssociatedUsername().equals(authToken.getUsername())) {
                db.closeConnection(false);
                return new EventResult(false, "error: Event does not belong to current user");
            }
            db.closeConnection(true);
            return new EventResult(true,event.getAssociatedUsername(),eventID,event.getPersonID(),event.getLatitude(),
                    event.getLongitude(),event.getCountry(),event.getCity(),event.getEventType(),event.getYear());
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new EventResult(false, "error: Internal server error");
        }
    }

    /**
     * Finds all events of all family members of current user
     * @param token Authorized token for current user session
     * @return Array of all events of all family members of current user
     */
    public AllEventsResult getUserFamilyEvents(String token) throws DataAccessException {
        Database db = new Database();
        try {
            db.openConnection();
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());
            EventDAO eDao = new EventDAO(db.getConnection());

            AuthToken authToken = aDao.find(token);
            if(authToken == null) {
                db.closeConnection(false);
                return new AllEventsResult(false, "error: Authorization token is invalid");
            }

            ArrayList<Event> events = eDao.getAllEvents(authToken.getUsername());
            db.closeConnection(true);
            return new AllEventsResult(true,events);
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new AllEventsResult(false, "error: Internal server error");
        }
    }
}
