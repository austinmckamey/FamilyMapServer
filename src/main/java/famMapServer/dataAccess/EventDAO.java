package famMapServer.dataAccess;

import models.Event;

import java.sql.*;
import java.util.ArrayList;

/**
 * Dao class to access Event database information
 */
public class EventDAO {
    /** Connection to database */
    private final Connection conn;

    /**
     * Creates new EventDAO to interact with database
     * @param conn Connection to database
     */
    public EventDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Adds Event to database
     * @param event Event to insert in database
     * @throws DataAccessException
     */
    public void insert(Event event) throws DataAccessException {
        String sql = "INSERT INTO Event (eventID, associatedUsername, personID, latitude, longitude, " +
                "country, city, eventType, year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getAssociatedUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setFloat(4, event.getLatitude());
            stmt.setFloat(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting Event into the database");
        }
    }

    /**
     * Find Event in database
     * @param eventID Unique identifier for Event to find
     * @return Event of interest
     * @throws DataAccessException
     */
    public Event find(String eventID) throws DataAccessException {
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM Event WHERE EventID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding Event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * Clears all Event from database
     * @throws DataAccessException
     */
    public void clear() throws DataAccessException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM Event";
            stmt.executeUpdate(sql);
        } catch(SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing Event table");
        }
    }

    /**
     * Returns all events associated with the username
     * @param associatedUsername Username associated with all events being returned
     * @return Array of events associated with username
     * @throws DataAccessException
     */
    public ArrayList<Event> getAllEvents(String associatedUsername) throws DataAccessException {
        Event event;
        ArrayList<Event> allEvents = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT * FROM Event WHERE associatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,associatedUsername);
            rs = stmt.executeQuery();
            while(rs.next()) {
                event = new Event(rs.getString("eventID"),rs.getString("associatedUsername"),
                        rs.getString("personID"),rs.getFloat("latitude"),rs.getFloat("longitude"),
                        rs.getString("country"),rs.getString("city"),rs.getString("eventType"),
                        rs.getInt("year"));
                allEvents.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding all events associated with user");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return allEvents;
    }

}
