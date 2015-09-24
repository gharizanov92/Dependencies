package graph;

/**
 * Created by gharizanov on 23.9.2015 �..
 */
public class CyclicGraphException extends Exception {

    public CyclicGraphException() {
    }

    public CyclicGraphException(String message) {
        super(message);
    }

    public CyclicGraphException(String message, Throwable cause) {
        super(message, cause);
    }

    public CyclicGraphException(Throwable cause) {
        super(cause);
    }

    public CyclicGraphException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
