package famMapServer.dataAccess;

import models.Person;

import java.sql.*;
import java.util.ArrayList;

/**
 * Dao class to access Person database information
 */
public class PersonDAO {
    /** Connection to database */
    private final Connection conn;

    /**
     * Creates new PersonDAO to interact with database
     * @param conn Connection to database
     */
    public PersonDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Adds Person in database
     * @param person Person to insert in database
     * @throws DataAccessException
     */
    public void insert(Person person) throws DataAccessException {
        String sql = "INSERT INTO Person (personID, associatedUsername, firstName, lastName, gender, " +
                "motherID, fatherID, spouseID) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, person.getPersonID());
            stmt.setString(2,person.getAssociatedUser());
            stmt.setString(3,person.getFirstName());
            stmt.setString(4,person.getLastName());
            stmt.setString(5,person.getGender());
            stmt.setString(6,person.getMotherID());
            stmt.setString(7,person.getFatherID());
            stmt.setString(8,person.getSpouseID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting Person into database");
        }
    }

    /**
     * Find Person in database
     * @param personID Unique identifier for Person to find
     * @return Person of interest
     * @throws DataAccessException
     */
    public Person find(String personID) throws DataAccessException {
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM Person WHERE personID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,personID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                person = new Person(rs.getString("personID"),rs.getString("associatedUsername"),
                        rs.getString("firstName"),rs.getString("lastName"),rs.getString("gender"),
                        rs.getString("motherID"),rs.getString("fatherID"),rs.getString("spouseID"));
                return person;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding Person");
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
     * Returns all family members of associated user
     * @param associatedUsername Username associated with all family members
     * @return Array of all family members associated with user
     * @throws DataAccessException
     */
    public ArrayList<Person> getFamilyMembers(String associatedUsername) throws DataAccessException {
        Person person;
        ArrayList<Person> familyMembers = new ArrayList<>();
        ResultSet rs = null;
        String sql = "SELECT * FROM Person WHERE associatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,associatedUsername);
            rs = stmt.executeQuery();
            while(rs.next()) {
                person = new Person(rs.getString("personID"),rs.getString("associatedUsername"),
                        rs.getString("firstName"),rs.getString("lastName"),rs.getString("gender"),
                        rs.getString("motherID"),rs.getString("fatherID"),rs.getString("spouseID"));
                familyMembers.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding family members");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return familyMembers;
    }

    /**
     * Clears all Person from database
     * @throws DataAccessException
     */
    public void clear() throws DataAccessException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM Person";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing Person table");
        }
    }

    public void delete(String personID) throws DataAccessException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM Person WHERE personID = '" + personID + "'";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while deleting from Person table");
        }
    }

}
