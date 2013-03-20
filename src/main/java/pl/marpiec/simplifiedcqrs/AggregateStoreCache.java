package pl.marpiec.simplifiedcqrs;

public interface AggregateStoreCache {

    Aggregate get(Class<? extends Aggregate> aggregateClass, UID id);

    void put(Aggregate aggregate);

}
