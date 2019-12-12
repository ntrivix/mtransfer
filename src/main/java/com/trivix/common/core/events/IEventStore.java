package com.trivix.common.core.events;

import com.trivix.common.utils.collections.readonly.IReadOnlyCollection;

import java.util.UUID;
import java.util.function.Consumer;

public interface IEventStore {
    IReadOnlyCollection<IEvent> getEvents(UUID id);
    IReadOnlyCollection<IEvent> getEventsAfterVersion(UUID id, int version);
    IEvent getLastEvent(UUID id);
    
    AddEventStatus addEvent(IEvent event);

    AddEventStatus addEvents(IReadOnlyCollection<IEvent> events);

    void registerEventHandler(UUID id, Consumer<IEvent> handler);

    <T> void registerEventHandler(Class<T> eventType, Consumer<IEvent> handler);
}
