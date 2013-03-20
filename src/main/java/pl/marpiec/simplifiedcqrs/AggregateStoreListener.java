package pl.marpiec.simplifiedcqrs;

/**
 *
 */
public abstract class AggregateStoreListener<T extends Aggregate> {

    public final void startListeningToAggregateStore(AggregateStore aggregateStore, Class<T> aggregateClass) {
        aggregateStore.addListener(aggregateClass, this);
    }

    public abstract void onAggregateChanged(Aggregate aggregate);
}
