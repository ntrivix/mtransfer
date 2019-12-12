package com.trivix.common.core.events;

import com.google.inject.Singleton;
import com.trivix.common.utils.Utils;
import com.trivix.common.utils.collections.readonly.IReadOnlyCollection;
import com.trivix.common.utils.collections.readonly.ReadOnlyList;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * InMemoryEventStore implementation saves all event only in memory.
 * This class ensure concurrency and idempotence of all write operations.
 */
@Singleton
public class InMemoryEventStore implements IEventStore {
    private ConcurrentHashMap<UUID, EventList> eventsMap;
    private ConcurrentHashMap<UUID, ConcurrentLinkedQueue<Consumer<IEvent>>> eventListeners;
    private ConcurrentHashMap<Class, ConcurrentLinkedQueue<Consumer<IEvent>>> specificEventTypeListeners;

    private ExecutorService notifierThreadPool = Executors.newCachedThreadPool();
    
    private AtomicInteger parallelWritesInProgress;
    private Semaphore multiAddSemaphore;
    
    public InMemoryEventStore() {
        eventsMap = new ConcurrentHashMap<>();
        eventListeners = new ConcurrentHashMap<>();
        specificEventTypeListeners = new ConcurrentHashMap<>();
        parallelWritesInProgress = new AtomicInteger(0);
        multiAddSemaphore = new Semaphore(1, true);
    }

    @Override
    public IReadOnlyCollection<IEvent> getEvents(UUID id) {
        return eventsMap.computeIfAbsent(id, i -> 
                        new EventList()
                ).getEvents();
    }

    @Override
    public IReadOnlyCollection<IEvent> getEventsAfterVersion(UUID id, int version) {
        IReadOnlyCollection<IEvent> events = eventsMap.computeIfAbsent(id, i ->
                new EventList()
        ).getEvents();
        
        if (events.size() < version)
            return new ReadOnlyList<>(new ArrayList<>());
        
        return events.sublist(version, events.size());
    }

    @Override
    public IEvent getLastEvent(UUID id) {
        EventList eventList = eventsMap.computeIfAbsent(id, i ->
                new EventList()
        );
        
        return eventList.getLastEvent();
    }
    
    private int getLastEventVersion(UUID id)
    {
        IEvent event = getLastEvent(id);
        if (event == null)
            return -1;
        
        return event.getVersion();
    }
    
    public boolean isEventDuplicate(IEvent event) {
        EventList eventList = eventsMap.computeIfAbsent(event.getAggregateId(), i ->
                new EventList()
        );
        
        return eventList.constainsEvent(event.getEventUniqueId());
    }

    @Override
    public AddEventStatus addEvent(IEvent event) {
        waitMultiEventWriteToFinish();
        parallelWritesInProgress.incrementAndGet();
        AddEventStatus status = eventsMap.computeIfAbsent(event.getAggregateId(), i ->
                new EventList()
        ).add(event);
        
        if (status == AddEventStatus.SUCCESS)
            notifyObservers(event);
        parallelWritesInProgress.decrementAndGet();
        return status;
    }

    @Override
    public AddEventStatus addEvents(IReadOnlyCollection<IEvent> events) {
        acquireWritePermission();

        while (parallelWritesInProgress.get() > 0)
            Utils.sleep(1);
        
        HashMap<UUID, Integer> addedEvents = new HashMap<>();  
        
        // check event ids integrity
        for (IEvent event:  events) {
            addedEvents.put(event.getEventUniqueId(),
                    addedEvents.getOrDefault(event.getEventUniqueId(), 0) + 1);
            
            if (event.getVersion() != getLastEventVersion(event.getAggregateId()) + addedEvents.get(event.getAggregateId()))
                return AddEventStatus.INVALID_VERSION_NUMBER;
            if (isEventDuplicate(event))
                return AddEventStatus.DUPLICATE_MESSAGE;
        }

        for (IEvent event:  events) {
            addEvent(event);
        }
        
        multiAddSemaphore.release();
        return AddEventStatus.SUCCESS;
    }
    
    private void acquireWritePermission()
    {
        try {
            multiAddSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitMultiEventWriteToFinish() {
        while (multiAddSemaphore.availablePermits() == 0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void registerEventHandler(UUID id, Consumer<IEvent> handler) {
        eventListeners.computeIfAbsent(id, i -> new ConcurrentLinkedQueue<>())
                .add(handler);
    }

    @Override
    public <T> void registerEventHandler(final Class<T> eventType, Consumer<IEvent> handler) {
        
        specificEventTypeListeners.computeIfAbsent(eventType, i -> new ConcurrentLinkedQueue<>())
                .add(handler);
    }

    private void notifyObservers(IEvent event) {
        eventListeners.computeIfAbsent(event.getAggregateId(), i -> new ConcurrentLinkedQueue<>())
                .forEach(listener -> notifierThreadPool.execute(() -> listener.accept(event)));
        specificEventTypeListeners.computeIfAbsent(event.getDataType(), i -> new ConcurrentLinkedQueue<>())
                .forEach(listener -> notifierThreadPool.execute(() -> listener.accept(event)));
    }
}
