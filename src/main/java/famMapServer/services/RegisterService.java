package famMapServer.services;

import famMapServer.dataAccess.AuthTokenDAO;
import famMapServer.dataAccess.DataAccessException;
import famMapServer.dataAccess.Database;
import famMapServer.dataAccess.UserDAO;
import models.AuthToken;
import models.User;
import requests.RegisterRequest;
import results.RegisterResult;

import java.io.FileNotFoundException;

/**
 * Interacts with handler class and database for register
 */
public class RegisterService extends Service {

    /**
     * Creates new service to register new user
     */
    public RegisterService() {

    }

    /**
     * Creates a new user account, can call the LoginService and FillService
     * @param r Contains information needed to register a user
     * @return RegisterResult object with authToken, username, and password
     */
    public RegisterResult register(RegisterRequest r) throws DataAccessException {
        Database db = new Database();
        String message = "error: Register failed";
        try {
            db.openConnection();
            UserDAO uDao = new UserDAO(db.getConnection());
            User user = uDao.find(r.getUsername());
            if(user != null) {
                db.closeConnection(false);
                return new RegisterResult(false, "error: Username already taken");
            } else {
                user = new User(r.getUsername(),r.getPassword(),r.getEmail(),r.getFirstName(),r.getLastName(),
                        r.getGender(),createToken());
                uDao.insert(user);
                AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());
                String t = createToken();
                AuthToken token = new AuthToken(t,user.getUsername());
                aDao.insert(token);

                GenerateFamily generator = new GenerateFamily(db.getConnection());
                generator.fillGenerations(user,5);

                db.closeConnection(true);
                return new RegisterResult(true, t, user.getUsername(),user.getPersonID());
            }
        } catch (DataAccessException | NullPointerException | FileNotFoundException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new RegisterResult(false, message);
        }
    }
}
