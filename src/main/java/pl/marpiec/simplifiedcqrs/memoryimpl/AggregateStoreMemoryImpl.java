package pl.marpiec.simplifiedcqrs.memoryimpl;

import pl.marpiec.simplifiedcqrs.Aggregate;
import pl.marpiec.simplifiedcqrs.AggregateStore;
import pl.marpiec.simplifiedcqrs.AggregateStoreListener;
import pl.marpiec.simplifiedcqrs.EventRow;
import pl.marpiec.simplifiedcqrs.EventStore;
import pl.marpiec.simplifiedcqrs.EventStoreListener;
import pl.marpiec.simplifiedcqrs.UID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AggregateStoreMemoryImpl extends EventStoreListener implements AggregateStore {

    private final EventStore eventStore;

    private final Map<Class<? extends Aggregate>, Set<AggregateStoreListener<? extends Aggregate>>> listeners =
            new HashMap<Class<? extends Aggregate>, Set<AggregateStoreListener<? extends Aggregate>>>();

    public AggregateStoreMemoryImpl(EventStore eventStore) {
        this.eventStore = eventStore;
        startListeningToEventStore(eventStore);
    }


    @Override
    public void addListener(Class<? extends Aggregate> aggregateClass, AggregateStoreListener<? extends Aggregate> listener) {
        Set<AggregateStoreListener<? extends Aggregate>> listenersForType = listeners.get(aggregateClass);
        if(listenersForType == null) {
            listenersForType = new HashSet<AggregateStoreListener<? extends Aggregate>>();
            listeners.put(aggregateClass, listenersForType);
        }
        listenersForType.add(listener);
    }


    @Override
    public <T extends Aggregate> T getAggregate(Class<T> aggregateClass, UID id) {

        try {
            T aggregate = aggregateClass.newInstance();
            aggregate.setId(id);
            aggregate.setVersion(0);

            List<EventRow> eventsForAggregate = eventStore.getEventsForAggregate(aggregateClass, id);
            for(EventRow eventRow: eventsForAggregate) {
                if(eventRow.getExpectedVersion() == aggregate.getVersion()) {
                    eventRow.getEvent().applyEvent(aggregate);
                    aggregate.incrementVersion();
                }
            }

            return aggregate;

        } catch (InstantiationException e) {
            throw new IllegalStateException("Error creating aggregate instance", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Error creating aggregate instance", e);
        }
    }

    @Override
    public void onAggregateChanged(Class<? extends Aggregate> aggregateClass, UID aggregateId) {
        Aggregate aggregate = getAggregate(aggregateClass, aggregateId);

        Set<AggregateStoreListener<? extends Aggregate>> listenersForType = listeners.get(aggregateClass);
        if(listenersForType != null) {
            for(AggregateStoreListener<? extends Aggregate> listener: listenersForType) {
                listener.onAggregateChanged(aggregate);
            }
        }
    }
}
