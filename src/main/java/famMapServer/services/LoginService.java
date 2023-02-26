package famMapServer.services;

import famMapServer.dataAccess.AuthTokenDAO;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.dataAccess.Database;
import famMapServer.dataAccess.UserDAO;
import models.AuthToken;
import models.User;
import requests.LoginRequest;
import results.LoginResult;

/**
 * Interacts with handler class and database for login
 */
public class LoginService extends Service {

    /**
     * Creates new service to log the user in
     */
    public LoginService() {

    }

    /**
     * Logs user in and starts the session with the server
     * @param l Contains username and password
     * @return LoginResult object with authToken and personID
     */
    public LoginResult login(LoginRequest l) throws DataAccessException {
        Database db = new Database();
        try {
            db.openConnection();

            UserDAO uDao = new UserDAO(db.getConnection());
            User user = uDao.find(l.getUsername());
            if(user == null) {
                db.closeConnection(false);
                return new LoginResult(false, "error: User not found in database");
            }
            if(!user.getPassword().equals(l.getPassword())) {
                db.closeConnection(false);
                return new LoginResult(false, "error: Password incorrect");
            }
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());
            String t = createToken();
            AuthToken token = new AuthToken(t,user.getUsername());
            aDao.insert(token);

            db.closeConnection(true);

            return new LoginResult(true, t, user.getUsername(), user.getPersonID());
        } catch (DataAccessException | NullPointerException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new LoginResult(false, "error: User not found in database");
        }

    }
}
