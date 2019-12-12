package com.trivix.common.core.events;

import com.trivix.common.utils.Utils;
import com.trivix.common.utils.collections.readonly.IReadOnlyCollection;
import com.trivix.common.utils.collections.readonly.ReadOnlyList;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class EventList {
    
    private AtomicInteger lastEventVersion;
    private ArrayList<IEvent> events;
    private ConcurrentHashMap<UUID, Boolean> eventKeys; 

    protected EventList() {
        lastEventVersion = new AtomicInteger(-1);
        events = new ArrayList<>();
        eventKeys = new ConcurrentHashMap<>();
    }

    public int getLastEventVersion() {
        return lastEventVersion.get();
    }
    
    public IEvent getLastEvent() {
        if (events.isEmpty())
            return null;
        return events.get(events.size() -1);
    }
    
    public boolean constainsEvent(UUID eventId) {
        return eventKeys.containsKey(eventId);
    }

    public IReadOnlyCollection<IEvent> getEvents() {
        return new ReadOnlyList<>(events);
    }
    
    public AddEventStatus add(IEvent event) {
        if (event.getVersion() < lastEventVersion.get() + 1 ||
                event.getVersion() != lastEventVersion.get() + 1) 
            return AddEventStatus.INVALID_VERSION_NUMBER;
            
        if (eventKeys.putIfAbsent(event.getEventUniqueId(), true) != null)
            return AddEventStatus.DUPLICATE_MESSAGE;
        
        if (!lastEventVersion.compareAndSet(event.getVersion() -1, event.getVersion())) {
            eventKeys.remove(event.getEventUniqueId());
            return AddEventStatus.INVALID_VERSION_NUMBER;
        }
        
        // Wait for other thread to write their events
        while(lastEventVersion.get() > events.size())
            Utils.sleep(1);
        
        events.add(event);
        return AddEventStatus.SUCCESS;
    }
}
