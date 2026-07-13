package it.unicam.cs.hackhub.model.enumeration;

public enum SubmissionState {

    DRAFT("Draft"),
    SUBMITTED("Submitted"),
    EVALUATED("Evaluated");

    private final String description;

    SubmissionState(String description) {
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
