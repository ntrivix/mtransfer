package com.trivix.common.core.events;

public class InvalidEventVersionException extends Exception {
    private int expectedId;
    private int providedId;
    private IEvent event;

    public InvalidEventVersionException(int expectedId, int providedId, IEvent event) {
        super("Expected event with id=" + expectedId + ", provided id=" + providedId);
        this.expectedId = expectedId;
        this.providedId = providedId;
        this.event = event;
    }

    public int getExpectedId() {
        return expectedId;
    }

    public int getProvidedId() {
        return providedId;
    }

    public IEvent getEvent() {
        return event;
    }
}
