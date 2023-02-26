package famMapServer.dataAccess;

/**
 * Exception class for Dao classes
 */
public class DataAccessException extends Exception {
    /**
     * Returns exception with error cause
     * @param message String containing error cause
     */
    DataAccessException(String message)
    {
        super(message);
    }

    /**
     * Returns parent class's exception without message
     */
    DataAccessException()
    {
        super();
    }
}