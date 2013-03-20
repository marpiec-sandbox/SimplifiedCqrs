package pl.marpiec.simplifiedcqrs.exception;

/**
 *
 */
public class ConcurrentAggregateModificationException extends RuntimeException {

    public ConcurrentAggregateModificationException(String message) {
        super(message);
    }
}
