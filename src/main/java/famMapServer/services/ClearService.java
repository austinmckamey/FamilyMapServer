package famMapServer.services;

import famMapServer.dataAccess.DataAccessException;
import famMapServer.dataAccess.Database;
import results.ClearResult;

/**
 * Interacts with handler class and database for clear
 */
public class ClearService {

    /** Variable to determine whether clear was successful */
    private boolean success;

    /**
     * Creates new service object to clear database
     */
    public ClearService() {

    }

    /**
     * Clears the database
     * @return a ClearResult object, indicating success or failure, with error details
     */
    public ClearResult clear() throws DataAccessException {
        Database db = new Database();
        try {
            db.openConnection();
            db.clearTables();
            db.closeConnection(true);

            return new ClearResult(true, "Clear succeeded!");
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new ClearResult(false, "error: Internal server error");
        }
    }

}
