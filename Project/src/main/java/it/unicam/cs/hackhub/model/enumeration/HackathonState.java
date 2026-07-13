package it.unicam.cs.hackhub.model.enumeration;

public enum HackathonState {

    REGISTRATION_OPEN("Registration Open"),
    IN_PROGRESS("In Progress"),
    UNDER_EVALUATION("Under Evaluation"),
    COMPLETED("Completed");

    private final String description;

    HackathonState(String description) {
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