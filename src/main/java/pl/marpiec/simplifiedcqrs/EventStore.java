package pl.marpiec.simplifiedcqrs;

import java.util.List;

public interface EventStore {

    void addEventForNewAggregate(UID userId, UID newAggregadeId, Event<? extends Aggregate> event);

    void addEventForExistingAggregate(UID userId, UID aggregadeId, int expectedVersion, Event<? extends Aggregate> event);

    void addEventIgnoreVersion(UID userId, UID aggregateId, int expectedVersion, Event<? extends Aggregate> event);

    List<EventRow> getEventsForAggregate(Class<? extends Aggregate> aggregateClass, UID id);

    List<EventRow> getAllEventsByType(Class<? extends Aggregate> aggregateClass);

    /**Use only for migrating events, not for normal business!!! */
    @Deprecated
    void updateEvent(EventRow eventRow);

    void addListener(EventStoreListener listener);

    void callListenersForAllAggregates();
}
