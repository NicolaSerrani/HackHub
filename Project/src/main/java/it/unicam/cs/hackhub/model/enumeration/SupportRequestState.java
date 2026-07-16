package it.unicam.cs.hackhub.model.enumeration;

public enum SupportRequestState {

    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved");

    private final String description;

    SupportRequestState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
