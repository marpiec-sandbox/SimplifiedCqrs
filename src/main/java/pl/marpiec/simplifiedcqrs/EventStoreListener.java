package pl.marpiec.simplifiedcqrs;

/**
 *
 */
public abstract class EventStoreListener {

    public void startListeningToEventStore(EventStore eventStore) {
        eventStore.addListener(this);
    }

    public abstract void onAggregateChanged(Class<? extends Aggregate> aggregateClass, UID aggregateId);
}
