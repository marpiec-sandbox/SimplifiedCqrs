package pl.marpiec.simplifiedcqrs.memoryimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.marpiec.simplifiedcqrs.Aggregate;
import pl.marpiec.simplifiedcqrs.AggregateStore;
import pl.marpiec.simplifiedcqrs.AggregateStoreCache;
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

public class AggregateStoreMemoryImpl implements EventStoreListener, AggregateStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregateStoreMemoryImpl.class);
    private final EventStore eventStore;
    private final AggregateStoreCache aggregateStoreCache;
    private final Map<Class<? extends Aggregate>, Set<AggregateStoreListener<? extends Aggregate>>> listeners =
            new HashMap<Class<? extends Aggregate>, Set<AggregateStoreListener<? extends Aggregate>>>();

    public AggregateStoreMemoryImpl(EventStore eventStore, AggregateStoreCache aggregateStoreCache) {
        this.eventStore = eventStore;
        this.aggregateStoreCache = aggregateStoreCache;

        eventStore.addListener(this);
    }

    @Override
    public void addListener(Class<? extends Aggregate> aggregateClass, AggregateStoreListener<? extends Aggregate> listener) {
        Set<AggregateStoreListener<? extends Aggregate>> listenersForType = listeners.get(aggregateClass);
        if (listenersForType == null) {
            listenersForType = new HashSet<AggregateStoreListener<? extends Aggregate>>();
            listeners.put(aggregateClass, listenersForType);
        }
        listenersForType.add(listener);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Listener was added to AggregateStore [type: " + listener.getClass() + "]");
        }
    }

    @Override
    public <T extends Aggregate> T getAggregate(Class<T> aggregateClass, UID id) {
        T aggregate = getNewOrCachedEntity(aggregateClass, id);

        List<EventRow> eventsForAggregate = eventStore.getEventsForAggregateFromVersion(aggregateClass, id, aggregate.getVersion());
        for (EventRow eventRow : eventsForAggregate) {
            if (eventRow.getExpectedVersion() == aggregate.getVersion()) {
                eventRow.getEvent().applyEvent(aggregate);
                aggregate.incrementVersion();
            } else {
                throw new IllegalStateException("Unexpected version for aggregate when applying event. " +
                        "[aggregateType:" + aggregateClass.getName() + ", aggregateId:" + id + ", aggregateVersion:" + aggregate.getVersion() +
                        "eventType:" + eventRow.getEvent().getClass().getName() + ", expectedVersion:" + eventRow.getExpectedVersion() + "]");
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(eventsForAggregate.size() + " events applied for aggregate [type:" + aggregateClass.getName() + ", id:" + id + "]");
        }

        return aggregate;
    }

    private <T extends Aggregate> T getNewOrCachedEntity(Class<T> aggregateClass, UID id) {
        try {
            T aggregate;
            if (aggregateStoreCache == null) {
                aggregate = null;
            } else {
                aggregate = (T) aggregateStoreCache.get(aggregateClass, id);
            }
            if (aggregate == null) {
                aggregate = aggregateClass.newInstance();
                aggregate.setId(id);
                aggregate.setVersion(0);
                if (aggregateStoreCache != null) {
                    aggregateStoreCache.put(aggregate);
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Aggregate instance created [type:" + aggregateClass.getName() + ", id:" + id + "]");
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
        if (listenersForType != null) {
            for (AggregateStoreListener<? extends Aggregate> listener : listenersForType) {
                listener.onAggregateChanged(aggregate);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(listenersForType.size() + " listeners called after aggregate changed [type:" +
                        aggregateClass.getName() + ", id:" + aggregateId + "]");
            }
        }
    }
}
