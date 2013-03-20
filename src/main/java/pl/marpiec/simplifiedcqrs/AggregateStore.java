package pl.marpiec.simplifiedcqrs;

/**
 *
 */
public interface AggregateStore {

    <T extends Aggregate> T getAggregate(Class<T> aggregateClass, UID id);

    void addListener(Class<? extends Aggregate> aggregateClass, AggregateStoreListener<? extends Aggregate> listener);

}
