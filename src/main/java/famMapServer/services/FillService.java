package famMapServer.services;

import famMapServer.dataAccess.DataAccessException;
import famMapServer.dataAccess.Database;
import famMapServer.dataAccess.UserDAO;
import models.User;
import results.FillResult;

import java.io.FileNotFoundException;

/**
 * Interacts with handler class and database for fill
 */
public class FillService {


    /**
     * Creates new service object to fill database for current user
     */
    public FillService() {

    }

    /**
     * Fills database with new information for specified user with specified generations
     * @param username Username of current user
     * @param generations Amount of generations to generate
     * @return FillResult object with number of persons and events added
     */
    public FillResult fill(String username, int generations) throws DataAccessException {
        Database db = new Database();
        try {
            db.openConnection();
            UserDAO uDao = new UserDAO(db.getConnection());
            User user = uDao.find(username);
            if(user == null) {
                db.closeConnection(false);
                return new FillResult(false, "User not registered in database");
            }
            if(generations < 0) {
                db.closeConnection(false);
                return new FillResult(false, "Invalid generations parameter");
            }
            db.clearUserFromTables(username);

            GenerateFamily generator = new GenerateFamily(db.getConnection());
            generator.fillGenerations(user,generations + 1);

            db.closeConnection(true);
            int x = 2;
            for(int i = 0; i < generations; ++i) {
                x = x * 2;
            }
            --x;
            int y = ((x-1) * 3) + 2;

            return new FillResult(true, "Successfully added " + x + " persons " +
                    "and " + y + " events to database.");
        } catch (DataAccessException | FileNotFoundException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new FillResult(false, "Internal server error");
        }
    }
}
