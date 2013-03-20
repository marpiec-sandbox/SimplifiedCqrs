package pl.marpiec.simplifiedcqrs;

public interface Event<T extends Aggregate> {

    void applyEvent(Aggregate aggregate);

    Class<T> getAggregateClass();
}
