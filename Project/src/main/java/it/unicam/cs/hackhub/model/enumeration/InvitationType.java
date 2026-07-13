package it.unicam.cs.hackhub.model.enumeration;

public enum InvitationType {

    TEAM("Team Invitation"),
    MENTOR("Mentor Invitation"),
    JUDGE("Judge Invitation");

    private final String description;

    InvitationType(String description) {
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
