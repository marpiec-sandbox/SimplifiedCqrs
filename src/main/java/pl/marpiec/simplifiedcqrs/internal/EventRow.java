package pl.marpiec.simplifiedcqrs.internal;

import pl.marpiec.simplifiedcqrs.Aggregate;
import pl.marpiec.simplifiedcqrs.Event;
import pl.marpiec.simplifiedcqrs.UID;

public class EventRow {

    private final UID userId;
    private final UID aggregateId;
    private int expectedVersion;
    private final Event<? extends Aggregate> event;

    public EventRow(UID userId, UID aggregateId, int expectedVersion, Event<? extends Aggregate> event) {
        this.userId = userId;
        this.aggregateId = aggregateId;
        this.expectedVersion = expectedVersion;
        this.event = event;
    }

    public UID getUserId() {
        return userId;
    }

    public UID getAggregateId() {
        return aggregateId;
    }

    public int getExpectedVersion() {
        return expectedVersion;
    }

    public void setExpectedVersion(int expectedVersion) {
        this.expectedVersion = expectedVersion;
    }

    public Event<? extends Aggregate> getEvent() {
        return event;
    }
}
