package famMapServer.dataAccess;

import models.AuthToken;

import java.sql.*;

/**
 * Dao class to access AuthToken database information
 */
public class AuthTokenDAO {
    /** Connection to database */
    private final Connection conn;

    /**
     * Creates new AuthTokenDAO to interact with database
     * @param conn Connection to database
     */
    public AuthTokenDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Adds AuthToken to database
     * @param token AuthToken to insert in database
     * @throws DataAccessException
     */
    public void insert(AuthToken token) throws DataAccessException {
        String sql = "INSERT INTO AuthToken (authToken, username) VALUES(?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,token.getAuthToken());
            stmt.setString(2,token.getUsername());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting AuthToken into the database");
        }
    }

    /**
     * Find AuthToken in database
     * @param authToken String form of AuthToken to find
     * @return AuthToken of interest
     * @throws DataAccessException
     */
    public AuthToken find(String authToken) throws DataAccessException {
        AuthToken token;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthToken WHERE authToken = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1,authToken);
            rs = stmt.executeQuery();
            if(rs.next()) {
                token = new AuthToken(rs.getString("authToken"), rs.getString("username"));
                return token;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding AuthToken");
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
     * Clears all AuthToken from database
     * @throws DataAccessException
     */
    public void clear() throws DataAccessException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "DELETE FROM AuthToken";
            stmt.executeUpdate(sql);
        } catch(SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing AuthToken table");
        }
    }

}
