package pl.marpiec.simplifiedcqrs.exception;

/**
 *
 */
public class AggregateAlreadyExistsException extends RuntimeException {
    public AggregateAlreadyExistsException(String message) {
        super(message);
    }
}
