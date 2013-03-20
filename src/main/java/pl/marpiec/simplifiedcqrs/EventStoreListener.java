package pl.marpiec.simplifiedcqrs;

public interface EventStoreListener {

    void onAggregateChanged(Class<? extends Aggregate> aggregateClass, UID aggregateId);

}
