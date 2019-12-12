package com.trivix.common.core.events;

import java.util.UUID;

public interface IEvent<TDataType> {
    UUID getAggregateId();
    int getVersion();
    TDataType getData();
    Class<TDataType> getDataType();

    UUID getEventUniqueId();
}
