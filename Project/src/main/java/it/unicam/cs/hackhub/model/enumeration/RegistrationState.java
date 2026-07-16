package it.unicam.cs.hackhub.model.enumeration;

public enum RegistrationState {

    ACTIVE("Active"),
    RETIRED("Retired");

    private final String description;

    RegistrationState(String description) {
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
