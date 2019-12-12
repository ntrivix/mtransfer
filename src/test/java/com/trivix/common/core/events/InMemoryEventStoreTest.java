package com.trivix.common.core.events;

import com.trivix.common.utils.collections.readonly.IReadOnlyCollection;
import org.junit.jupiter.api.Test;
import testutils.Tuple;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryEventStoreTest {
    
    @Test
    public void addEventTest() {
        // Arrange.
        IEventStore eventStore = new InMemoryEventStore();
        IEvent event = Event.create("testData", UUID.randomUUID(), UUID.randomUUID(), 0);
        
        // Act.
        AddEventStatus status = eventStore.addEvent(event);
        IReadOnlyCollection<IEvent> events = eventStore.getEvents(event.getAggregateId());
        
        // Assert.
        assertEquals(AddEventStatus.SUCCESS, status);
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }

    @Test
    public void addDuplicateEventTest() {
        // Arrange.
        IEventStore eventStore = new InMemoryEventStore();
        IEvent event = Event.create("testData", UUID.randomUUID(), UUID.randomUUID(), 0);
        IEvent eventDuplicate = Event.create("testData", event.getAggregateId(), event.getEventUniqueId(), 1);

        // Act.
        eventStore.addEvent(event);
        AddEventStatus status = eventStore.addEvent(eventDuplicate);
        IReadOnlyCollection<IEvent> events = eventStore.getEvents(event.getAggregateId());

        // Assert.
        assertEquals(AddEventStatus.DUPLICATE_MESSAGE, status);
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }

    @Test
    public void addInvalidVersionEventTest() {
        // Arrange.
        IEventStore eventStore = new InMemoryEventStore();
        IEvent event = Event.create("testData", UUID.randomUUID(), UUID.randomUUID(), 0);
        IEvent event1 = Event.create("testData", event.getAggregateId(), UUID.randomUUID(), 0);

        // Act.
        eventStore.addEvent(event);
        AddEventStatus status = eventStore.addEvent(event1);
        IReadOnlyCollection<IEvent> events = eventStore.getEvents(event.getAggregateId());

        // Assert.
        assertEquals(AddEventStatus.INVALID_VERSION_NUMBER, status);
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }
    
    @Test
    public void addTwoEventConcurrently() throws ExecutionException, InterruptedException {
        // Arrange.
        IEventStore eventStore = new InMemoryEventStore();
        
        UUID aggregateId = UUID.randomUUID();
        IEvent event1 = Event.create("testData", aggregateId, UUID.randomUUID(), 0);
        IEvent event2 = Event.create("testData", aggregateId, UUID.randomUUID(), 1);

        Tuple<AddEventStatus, AddEventStatus> results = submitTwoEventConcurrently(eventStore, event1, event2);
        AddEventStatus status1 = results.first;
        AddEventStatus status2 = results.second;
       
        IReadOnlyCollection<IEvent> events = eventStore.getEvents(aggregateId);

        // Assert.
        assertEquals(AddEventStatus.SUCCESS, status1);
        assertEquals(AddEventStatus.SUCCESS, status2);
        assertEquals(2, events.size());
        assertTrue(events.get(0).equals(event1) ^ events.get(1).equals(event1));
        assertTrue(events.get(0).equals(event2) ^ events.get(1).equals(event2));
    }

    @Test
    public void addDuplicateEventConcurrently() throws ExecutionException, InterruptedException {
        IEventStore eventStore = new InMemoryEventStore();

        UUID aggregateId = UUID.randomUUID();
        IEvent event1 = Event.create("testData", aggregateId, UUID.randomUUID(), 0);
        IEvent event2 = Event.create("testData", aggregateId, event1.getEventUniqueId(), 1);

        Tuple<AddEventStatus, AddEventStatus> results = submitTwoEventConcurrently(eventStore, event1, event2);
        AddEventStatus status1 = results.first;
        AddEventStatus status2 = results.second;

        IReadOnlyCollection<IEvent> events = eventStore.getEvents(aggregateId);

        // Assert.
        assertTrue(status1 == AddEventStatus.SUCCESS ^ status2 == AddEventStatus.SUCCESS);
        assertTrue(status1 == AddEventStatus.DUPLICATE_MESSAGE ^ status2 == AddEventStatus.DUPLICATE_MESSAGE);
        assertEquals(1, events.size());
        assertTrue(events.get(0).equals(event1) || events.get(0).equals(event1));
    }

    @Test
    public void addTwoConcurrentlyInvalidVersionNumber() throws ExecutionException, InterruptedException {
        IEventStore eventStore = new InMemoryEventStore();

        UUID aggregateId = UUID.randomUUID();
        IEvent event1 = Event.create("testData", aggregateId, UUID.randomUUID(), 0);
        IEvent event2 = Event.create("testData", aggregateId, UUID.randomUUID(), 0);

        Tuple<AddEventStatus, AddEventStatus> results = submitTwoEventConcurrently(eventStore, event1, event2);
        AddEventStatus status1 = results.first;
        AddEventStatus status2 = results.second;

        IReadOnlyCollection<IEvent> events = eventStore.getEvents(aggregateId);

        // Assert.
        assertTrue(status1 == AddEventStatus.SUCCESS ^ status2 == AddEventStatus.SUCCESS);
        assertTrue(status1 == AddEventStatus.INVALID_VERSION_NUMBER ^ status2 == AddEventStatus.INVALID_VERSION_NUMBER);
        assertEquals(1, events.size());
        assertTrue(events.get(0).equals(event1) || events.get(0).equals(event1));
    }
    
    private Tuple<AddEventStatus, AddEventStatus> submitTwoEventConcurrently(IEventStore eventStore, IEvent event1, IEvent event2) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        Future<AddEventStatus> f1 = executor.submit(() -> eventStore.addEvent(event1));
        Future<AddEventStatus> f2 = executor.submit(() -> eventStore.addEvent(event2));
        
        return new Tuple<>(f1.get(), f2.get());
    }

    
}