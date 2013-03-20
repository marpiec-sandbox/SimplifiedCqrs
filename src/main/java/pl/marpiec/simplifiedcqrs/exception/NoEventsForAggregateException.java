package pl.marpiec.simplifiedcqrs.exception;

/**
 *
 */
public class NoEventsForAggregateException extends RuntimeException {

    public NoEventsForAggregateException(String message) {
        super(message);
    }
}
