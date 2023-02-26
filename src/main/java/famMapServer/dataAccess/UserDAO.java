package famMapServer.dataAccess;

import models.User;

import java.sql.*;

/**
 * Dao class to access User database information
 */
public class UserDAO {
    /** Connection to database */
    private final Connection conn;

    /**
     * Creates new UserDAO to interact with database
     * @param conn Connection to database
     */
    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Adds User to database
     * @param user User to insert in database
     * @throws DataAccessException
     */
    public void insert(User user) throws DataAccessException {
        String sql = "INSERT INTO User (username, password, email, firstName, lastName, " +
                "gender, personID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,user.getUsername());
            stmt.setString(2,user.getPassword());
            stmt.setString(3,user.getEmail());
            stmt.setString(4,user.getFirstName());
            stmt.setString(5,user.getLastName());
            stmt.setString(6,user.getGender());
            stmt.setString(7,user.getPersonID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting User into database");
        }
    }

    /**
     * Finds User in database
     * @param username Unique identifier of User to find
     * @return User of interest
     * @throws DataAccessException
     */
    public User find(String username) throws DataAccessException {
        User user;
        ResultSet rs = null;
        String sql = "SELECT * FROM User WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,username);
            rs = stmt.executeQuery();
            if(rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"),
                        rs.getString("email"),rs.getString("firstName"),rs.getString("lastName"),
                        rs.getString("gender"),rs.getString("personID"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding User");
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
     * Clears all User from database
     * @throws DataAccessException
     */
    public void clear() throws DataAccessException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM User";
            stmt.executeUpdate(sql);
        } catch(SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing User table");
        }
    }
}
