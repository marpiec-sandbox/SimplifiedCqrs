package pl.marpiec.simplifiedcqrs.exception;

/**
 *
 */
public class NoEventsForTypeException extends RuntimeException {

    public NoEventsForTypeException(String message) {
        super(message);
    }
}
