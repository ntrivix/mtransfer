package com.trivix.common.core.events;

public class EventSubmissionException extends RuntimeException {
    private IEvent event;
    private AddEventStatus status;

    public EventSubmissionException(IEvent event, AddEventStatus status) {
        super("Can't submit event {" + event + "}. Status =" + status);
        this.event = event;
        this.status = status;
    }

    public IEvent getEvent() {
        return event;
    }

    public AddEventStatus getStatus() {
        return status;
    }
}
