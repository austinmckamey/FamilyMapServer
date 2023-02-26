package famMapServer.dataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * public class to control connection to Database
 */
public class Database {
    /** connection to database */
    private Connection conn;

    /**
     * opens connection to Database
     * @return connection to Database
     * @throws DataAccessException
     */
    public Connection openConnection() throws DataAccessException {
        try {
            final String CONNECTION_URL = "jdbc:sqlite:/Users/austinmckamey/Documents/CS240/FamilyMapServer/fmsTables.db";

            conn = DriverManager.getConnection(CONNECTION_URL);

            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Unable to open connection to database");
        }

        return conn;
    }

    /**
     * Returns connection to Database
     * @return connection to Database
     * @throws DataAccessException
     */
    public Connection getConnection() throws DataAccessException {
        if(conn == null) {
            return openConnection();
        } else {
            return conn;
        }
    }

    /**
     * Closes connection to Database
     * @param commit determines if information is committed to Database
     * @throws DataAccessException
     */
    public void closeConnection(boolean commit) throws DataAccessException {
        try {
            if (commit) {
                conn.commit();
            } else {
                conn.rollback();
            }

            conn.close();
            conn = null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Unable to close database connection");
        }
    }

    /**
     * Clears all tables in Database
     * @throws DataAccessException
     */
    public void clearTables() throws DataAccessException {
        try (Statement stmt = conn.createStatement()){
            String sql = "DELETE FROM Event";
            stmt.executeUpdate(sql);
            sql = "DELETE FROM AuthToken";
            stmt.executeUpdate(sql);
            sql = "DELETE FROM Person";
            stmt.executeUpdate(sql);
            sql = "DELETE FROM User";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing tables");
        }
    }

    /**
     * Clears all tables of anything with associated username
     * @param username associated username no longer wanted in Database
     * @throws DataAccessException
     */
    public void clearUserFromTables(String username) throws DataAccessException {
        try (Statement stmt = conn.createStatement()){
            String sql = "DELETE FROM Event WHERE associatedUsername='" + username + "'";
            stmt.executeUpdate(sql);
            sql = "DELETE FROM Person WHERE associatedUsername='" + username + "'";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DataAccessException("SQL Error encountered while clearing tables from user");
        }
    }
}
