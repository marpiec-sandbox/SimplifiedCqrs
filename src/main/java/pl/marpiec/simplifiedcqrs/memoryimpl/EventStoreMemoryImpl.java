package pl.marpiec.simplifiedcqrs.memoryimpl;

import pl.marpiec.simplifiedcqrs.Aggregate;
import pl.marpiec.simplifiedcqrs.Event;
import pl.marpiec.simplifiedcqrs.EventRow;
import pl.marpiec.simplifiedcqrs.EventStore;
import pl.marpiec.simplifiedcqrs.EventStoreListener;
import pl.marpiec.simplifiedcqrs.UID;
import pl.marpiec.simplifiedcqrs.exception.AggregateAlreadyExistsException;
import pl.marpiec.simplifiedcqrs.exception.ConcurrentAggregateModificationException;
import pl.marpiec.simplifiedcqrs.exception.NoEventsForAggregateException;
import pl.marpiec.simplifiedcqrs.exception.NoEventsForTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventStoreMemoryImpl implements EventStore {

    private final Map<Class<? extends Aggregate>, Map<UID, List<EventRow>>> eventsByType =
            new HashMap<Class<? extends Aggregate>, Map<UID, List<EventRow>>>();

    private final Set<EventStoreListener> listeners = new HashSet<EventStoreListener>();

    @Override
    public void addEventForNewAggregate(UID userId, UID newAggregadeId, Event<? extends Aggregate> event) {

        Class<? extends Aggregate> aggregateClass = event.getAggregateClass();
        EventRow eventRow = new EventRow(userId, newAggregadeId, 0, event);

        Map<UID, List<EventRow>> eventsForType = eventsByType.get(aggregateClass);

        if (eventsForType == null) {
            eventsForType = new HashMap<UID, List<EventRow>>();
            eventsByType.put(aggregateClass, eventsForType);
        }

        List<EventRow> eventsForAggregate = eventsForType.get(newAggregadeId);

        if (eventsForAggregate == null) {
            eventsForAggregate = new ArrayList<EventRow>();
            eventsForType.put(newAggregadeId, eventsForAggregate);
        } else {
            throw new AggregateAlreadyExistsException("Trying to create new Aggregate when one with the same type and id is already defined. " +
                    "There already are created events for aggregate of type "
                    + aggregateClass.getSimpleName() + " and id " + newAggregadeId);
        }

        eventsForAggregate.add(eventRow);

        callAllListenersAboutNewEvent(aggregateClass, newAggregadeId);
    }

    @Override
    public void addEventForExistingAggregate(UID userId, UID aggregateId, int expectedVersion, Event<? extends Aggregate> event) {
        addEventForExistingWithVersionCheck(userId, aggregateId, expectedVersion, event, false);
    }

    @Override
    public void addEventIgnoreVersion(UID userId, UID aggregateId, int expectedVersion, Event<? extends Aggregate> event) {
        addEventForExistingWithVersionCheck(userId, aggregateId, expectedVersion, event, true);
    }

    private void addEventForExistingWithVersionCheck(UID userId, UID aggregateId, int expectedVersion, Event<? extends Aggregate> event, boolean ignoreVersion) {
        Class<? extends Aggregate> aggregateClass = event.getAggregateClass();
        EventRow eventRow = new EventRow(userId, aggregateId, expectedVersion, event);
        Map<UID, List<EventRow>> eventsForType = eventsByType.get(aggregateClass);

        if(eventsForType == null) {
            throw new NoEventsForTypeException("Trying to add event for non existing aggregate. No events for type "+ aggregateClass +
                    " are defined, so aggregate with id "+aggregateId +" does not extst");
        }

        List<EventRow> eventsForAggregate = eventsForType.get(aggregateId);

        if(eventsForAggregate == null) {
            throw new NoEventsForAggregateException("Trying to add event for non existing aggregate. No aggregate of type "+
                    aggregateClass + " and id " +aggregateId+" are defined so aggregate does not exist");
        }

        if (ignoreVersion) {
            eventRow.setExpectedVersion(eventsForAggregate.size());
        }
        if (eventsForAggregate.size() > eventRow.getExpectedVersion()) {
            throw new ConcurrentAggregateModificationException("Expected version " + eventRow.getExpectedVersion() + " but is " + eventsForAggregate.size());
        }

        eventsForAggregate.add(eventRow);
        callAllListenersAboutNewEvent(aggregateClass, aggregateId);
    }



    @Override
    public List<EventRow> getEventsForAggregate(Class<? extends Aggregate> aggregateClass, UID aggregateId) {
        Map<UID, List<EventRow>> eventsForType = eventsByType.get(aggregateClass);

        if(eventsForType == null) {
            throw new NoEventsForTypeException("Trying to get events for non existing aggregate. No events for type "+ aggregateClass +
                    " are defined, so aggregate with id "+aggregateId +" does not extst");
        }

        List<EventRow> eventsForAggregate = eventsForType.get(aggregateId);

        if(eventsForAggregate == null) {
            throw new NoEventsForAggregateException("Trying to get events for non existing aggregate. No aggregate of type "+
                    aggregateClass + " and id " +aggregateId+" are defined so aggregate does not exist");
        }
        return eventsForAggregate;
    }

    @Override
    public List<EventRow> getAllEventsByType(Class<? extends Aggregate> aggregateClass) {
        Map<UID, List<EventRow>> eventsForType = eventsByType.get(aggregateClass);
        List<EventRow> allEventsForType = new ArrayList<EventRow>();
        if(eventsForType == null) {
            return allEventsForType;
        } else {
            for(List<EventRow> eventsForAggregate: eventsForType.values()) {
                allEventsForType.addAll(eventsForAggregate);
            }
            return allEventsForType;
        }
    }


    @Override
    @Deprecated
    public void updateEvent(EventRow eventRow) {
        // not required for memory implementation
    }

    @Override
    public void addListener(EventStoreListener listener) {
        listeners.add(listener);
    }

    @Override
    public void callListenersForAllAggregates() {
        for(Map.Entry<Class<? extends Aggregate>, Map<UID, List<EventRow>>> eventsForType: eventsByType.entrySet()) {
            for(UID id: eventsForType.getValue().keySet()) {
                callAllListenersAboutNewEvent(eventsForType.getKey(), id);
            }

        }
    }

    private void callAllListenersAboutNewEvent(Class<? extends Aggregate> aggregateClass, UID aggregateId) {
        for(EventStoreListener listener: listeners) {
            listener.onAggregateChanged(aggregateClass, aggregateId);
        }
    }
}
