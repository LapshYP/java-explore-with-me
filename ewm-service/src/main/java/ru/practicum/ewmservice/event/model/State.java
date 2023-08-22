package ru.practicum.ewmservice.event.model;


public enum State {
    PENDING, PUBLISHED, CANCELED, PUBLISH_EVENT, REJECT_EVENT, CANCEL_REVIEW, SEND_TO_REVIEW;

    public static State from(String state) {
        for (State value : State.values()) {
            if (value.name().equalsIgnoreCase(state)) {
                return value;
            }
        }
        return null;
    }
}
