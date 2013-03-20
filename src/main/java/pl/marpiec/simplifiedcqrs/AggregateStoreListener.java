package pl.marpiec.simplifiedcqrs;

/**
 *
 */
public interface AggregateStoreListener<T extends Aggregate> {

    void onAggregateChanged(Aggregate aggregate);
}
