package com.trivix.common.core.events;

import java.util.UUID;

public class Event<TEventData> implements IEvent<TEventData> {
    private UUID eventUniqueId;
    private UUID aggregateId;
    private int version;
    private TEventData data;

    private Event(UUID eventUniqueId, 
                  UUID aggregateId, 
                  int version, 
                  TEventData data) {
        this.eventUniqueId = eventUniqueId;
        this.aggregateId = aggregateId;
        this.version = version;
        this.data = data;
    }
    
    public static <TEventData> Event<TEventData> create(TEventData eventData, UUID aggregateId, UUID eventId, int version)
    {
        return new Event<>(eventId, aggregateId, version, eventData);
    }

    @Override
    public UUID getAggregateId() {
        return aggregateId;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public TEventData getData() {
        return data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<TEventData> getDataType() {
        return (Class<TEventData>) data.getClass();
    }

    @Override
    public UUID getEventUniqueId() {
        return eventUniqueId;
    }
}
