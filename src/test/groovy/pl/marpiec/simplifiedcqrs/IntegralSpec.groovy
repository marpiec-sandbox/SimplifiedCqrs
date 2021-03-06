package pl.marpiec.simplifiedcqrs

import org.slf4j.LoggerFactory
import pl.marpiec.simplifiedcqrs.event.ChangePasswordEvent
import pl.marpiec.simplifiedcqrs.event.CreateUserEvent
import pl.marpiec.simplifiedcqrs.memoryimpl.AggregateStoreCacheMemoryImpl
import pl.marpiec.simplifiedcqrs.memoryimpl.AggregateStoreMemoryImpl
import pl.marpiec.simplifiedcqrs.memoryimpl.EventStoreMemoryImpl
import pl.marpiec.simplifiedcqrs.memoryimpl.UIDGeneratorMemoryImpl
import spock.lang.Specification

class IntegralSpec extends Specification {

    def setup() {
        LoggerFactory.getLogger(this.getClass()).debug("Test started")
    }

    def "Aggregate can be aquired from AggregateStore"() {

        given:
        EventStore eventStore = new EventStoreMemoryImpl()
        AggregateStoreCache aggregateStoreCache = new AggregateStoreCacheMemoryImpl()
        AggregateStore aggregateStore = new AggregateStoreMemoryImpl(eventStore, aggregateStoreCache)
        def uidGenerator = new UIDGeneratorMemoryImpl()

        def currentUserId = uidGenerator.nextUID()

        when:
        def userAggregateId = uidGenerator.nextUID()
        def createUserEvent = new CreateUserEvent("Marcin", "mySecret", "marcin@marpiec.pl")

        eventStore.addEventForNewAggregate(currentUserId, userAggregateId, createUserEvent)
        def user = aggregateStore.getAggregate(User.class, userAggregateId)

        then:
        user.getId() == userAggregateId
        user.getVersion() == 1
        user.getName() == "Marcin"
        user.getPassword() == "mySecret"
        user.getEmail() == "marcin@marpiec.pl"

        when:
        def changePasswordEvent = new ChangePasswordEvent("evenBetterSecret")
        eventStore.addEventForExistingAggregate(currentUserId, user.getId(), user.getVersion(), changePasswordEvent)
        user = aggregateStore.getAggregate(User.class, userAggregateId)

        then:
        user.getId() == userAggregateId
        user.getVersion() == 2
        user.getName() == "Marcin"
        user.getPassword() == "evenBetterSecret"
        user.getEmail() == "marcin@marpiec.pl"

    }

}

